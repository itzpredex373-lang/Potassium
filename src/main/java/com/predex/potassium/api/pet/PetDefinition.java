package com.predex.potassium.api.pet;

/**
 * Immutable public description of a Potassium-compatible pet.
 */
public final class PetDefinition {
    private final String id;
    private final String name;
    private final String author;
    private final int apiVersion;
    private final PetRenderer renderer;

    public PetDefinition(String id, String name, String author, int apiVersion, PetRenderer renderer) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.apiVersion = apiVersion;
        this.renderer = renderer;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public int getApiVersion() { return apiVersion; }
    public PetRenderer getRenderer() { return renderer; }
}
