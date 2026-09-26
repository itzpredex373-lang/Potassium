package com.predex.potassium.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.predex.potassium.api.pet.PetDefinition;
import com.predex.potassium.api.pet.PotassiumPetAPI;
import net.minecraft.client.Minecraft;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Loads safe, user-created .potpet files from:
 * .minecraft/config/potassium/pets
 *
 * Packs are data-only. No Java classes are loaded from a pet pack.
 */
public final class PotassiumPetPackManager {
    private static final long MAX_PACK_BYTES = 5L * 1024L * 1024L;
    private static final int MAX_FILE_BYTES = 1024 * 1024;
    private static final Set<String> ALLOWED_FILES = new HashSet<String>();

    private static File petDirectory;
    private static boolean initialized;

    static {
        ALLOWED_FILES.add("manifest.json");
        ALLOWED_FILES.add("model.json");
        ALLOWED_FILES.add("animation.json");
        ALLOWED_FILES.add("texture.png");
    }

    private PotassiumPetPackManager() {}

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;
        petDirectory = new File(
                new File(Minecraft.getMinecraft().mcDataDir, "config/potassium"), "pets");
        if (!petDirectory.exists()) petDirectory.mkdirs();
        reload();
    }

    public static synchronized void reload() {
        if (petDirectory == null) return;
        File[] files = petDirectory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".potpet")) {
                loadPack(file);
            }
        }
    }

    public static synchronized boolean importPack(String sourcePath) {
        if (sourcePath == null || sourcePath.trim().length() == 0) return false;
        if (!initialized) init();

        File source = new File(sourcePath.trim());
        if (!source.isFile() || !source.getName().toLowerCase().endsWith(".potpet")) return false;
        if (source.length() <= 0L || source.length() > MAX_PACK_BYTES) return false;

        File destination = new File(petDirectory, safeFileName(source.getName()));
        if (!copyFile(source, destination)) return false;
        return loadPack(destination);
    }

    public static synchronized File getPetDirectory() {
        if (!initialized) init();
        return petDirectory;
    }

    public static synchronized List<String> getPetIds() {
        if (!initialized) init();
        List<String> ids = new ArrayList<String>();
        for (String type : PotassiumPetTypes.TYPES) ids.add(type);
        for (PetDefinition definition : PotassiumPetAPI.getAll()) ids.add(definition.getId());
        return ids;
    }

    public static synchronized String getDisplayName(String id) {
        if (PotassiumPetAPI.contains(id)) {
            return PotassiumPetAPI.get(id).getName();
        }
        return PotassiumPetTypes.displayName(id);
    }

    public static synchronized boolean isAvailable(String id) {
        return PotassiumPetAPI.contains(id)
                || containsBuiltIn(id);
    }

    private static boolean containsBuiltIn(String id) {
        for (String type : PotassiumPetTypes.TYPES) {
            if (type.equals(id)) return true;
        }
        return false;
    }

    private static boolean loadPack(File file) {
        try {
            byte[] manifestBytes = null;
            byte[] modelBytes = null;
            byte[] animationBytes = null;
            byte[] textureBytes = null;

            ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
            try {
                ZipEntry entry;
                long total = 0L;
                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (entry.isDirectory() || !ALLOWED_FILES.contains(name)) {
                        zip.closeEntry();
                        continue;
                    }

                    byte[] data = readLimited(zip, MAX_FILE_BYTES);
                    total += data.length;
                    if (total > MAX_PACK_BYTES) {
                        throw new IllegalArgumentException("pack too large");
                    }

                    if ("manifest.json".equals(name)) manifestBytes = data;
                    else if ("model.json".equals(name)) modelBytes = data;
                    else if ("animation.json".equals(name)) animationBytes = data;
                    else if ("texture.png".equals(name)) textureBytes = data;
                    zip.closeEntry();
                }
            } finally {
                zip.close();
            }

            if (manifestBytes == null || modelBytes == null) {
                throw new IllegalArgumentException("manifest.json/model.json missing");
            }

            JsonObject manifest = new JsonParser()
                    .parse(new String(manifestBytes, "UTF-8")).getAsJsonObject();
            String id = required(manifest, "id");
            String name = required(manifest, "name");
            String author = manifest.has("author") ? manifest.get("author").getAsString() : "Unknown";
            int apiVersion = manifest.has("apiVersion")
                    ? manifest.get("apiVersion").getAsInt() : 1;

            JsonObject model = new JsonParser()
                    .parse(new String(modelBytes, "UTF-8")).getAsJsonObject();
            JsonObject animation = animationBytes == null ? null
                    : new JsonParser().parse(new String(animationBytes, "UTF-8")).getAsJsonObject();

            validateAnimation(animation);

            PotassiumImportedPetRenderer renderer =
                    PotassiumImportedPetRenderer.fromJson(model, animation, textureBytes, id);
            return PotassiumPetAPI.replace(new PetDefinition(
                    id, name, author, apiVersion, renderer));
        } catch (Exception ignored) {
            // Invalid community packs fail closed and never crash Minecraft.
            return false;
        }
    }

    private static void validateAnimation(JsonObject animation) {
        if (animation == null) return;
        validateRange(animation, "bob", 0.0F, 2.0F);
        validateRange(animation, "bobSpeed", 0.01F, 2.0F);
        validateRange(animation, "sway", 0.0F, 2.0F);
        validateRange(animation, "swaySpeed", 0.01F, 2.0F);
    }

    private static void validateRange(JsonObject o, String key, float min, float max) {
        if (!o.has(key)) return;
        float value = o.get(key).getAsFloat();
        if (Float.isNaN(value) || Float.isInfinite(value) || value < min || value > max) {
            throw new IllegalArgumentException("invalid animation value");
        }
    }

    private static String required(JsonObject object, String key) {
        if (!object.has(key)) throw new IllegalArgumentException("missing " + key);
        String value = object.get(key).getAsString().trim();
        if (value.length() == 0 || value.length() > 64) {
            throw new IllegalArgumentException("invalid " + key);
        }
        return value;
    }

    private static byte[] readLimited(InputStream input, int limit) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int total = 0;
        int read;
        while ((read = input.read(buffer)) != -1) {
            total += read;
            if (total > limit) throw new IllegalArgumentException("file too large");
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static boolean copyFile(File source, File destination) {
        try {
            File parent = destination.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();

            InputStream input = new FileInputStream(source);
            FileOutputStream output = new FileOutputStream(destination);
            try {
                byte[] buffer = new byte[8192];
                int read;
                while ((read = input.read(buffer)) != -1) output.write(buffer, 0, read);
            } finally {
                input.close();
                output.close();
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static String safeFileName(String name) {
        String clean = name.replace('\\', '_').replace('/', '_');
        return clean.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
