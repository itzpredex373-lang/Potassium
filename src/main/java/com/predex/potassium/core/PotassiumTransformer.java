package com.predex.potassium.core;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PotassiumTransformer implements net.minecraft.launchwrapper.IClassTransformer {
    private static final Logger LOGGER = LogManager.getLogger("Potassium-ASM");
    private static final String HOOK = "com/predex/potassium/core/PotassiumCoreHooks";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || transformedName == null) return basicClass;

        try {
            if ("net.minecraft.client.particle.EffectRenderer".equals(transformedName)) {
                return transformEffectRenderer(basicClass);
            }
            if ("net.minecraft.client.renderer.Tessellator".equals(transformedName)) {
                return transformTessellator(basicClass);
            }
            if ("net.minecraft.client.renderer.RenderGlobal".equals(transformedName)) {
                return transformRenderGlobal(basicClass);
            }
            if ("net.minecraft.client.renderer.entity.RenderManager".equals(transformedName)) {
                return transformRenderManager(basicClass);
            }
            if ("net.minecraft.client.renderer.BlockModelRenderer".equals(transformedName)) {
                return transformBlockModelRenderer(basicClass);
            }
        } catch (Throwable error) {
            LOGGER.warn("[Potassium ASM] Transformer failed for " + transformedName, error);
            // Never make the client unloadable because an optional hook failed.
        }

        return basicClass;
    }

    private byte[] transformEffectRenderer(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"(Lnet/minecraft/client/particle/EntityFX;)V".equals(mn.desc)) continue;

            InsnList hook = new InsnList();
            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "allowParticleSpawn", "()Z", false));
            LabelNode allowed = new LabelNode();
            hook.add(new JumpInsnNode(Opcodes.IFNE, allowed));
            hook.add(new InsnNode(Opcodes.RETURN));
            hook.add(allowed);
            mn.instructions.insert(hook);
            changed = true;
        }

        if (changed) { LOGGER.info("[Potassium ASM] EffectRenderer transformed"); }
        return changed ? write(cn) : bytes;
    }

    private byte[] transformTessellator(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"()V".equals(mn.desc)) continue;
            if (!"draw".equals(mn.name) && !"func_78381_a".equals(mn.name)) continue;

            InsnList hook = new InsnList();
            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "allowTessellatorDraw", "()Z", false));
            LabelNode allowed = new LabelNode();
            hook.add(new JumpInsnNode(Opcodes.IFNE, allowed));
            hook.add(new InsnNode(Opcodes.RETURN));
            hook.add(allowed);
            mn.instructions.insert(hook);
            changed = true;
            break;
        }

        if (changed) { LOGGER.info("[Potassium ASM] Tessellator transformed"); }
        return changed ? write(cn) : bytes;
    }

    private byte[] transformRenderGlobal(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"(J)V".equals(mn.desc)) continue;
            if (!"updateChunks".equals(mn.name) && !"func_72716_a".equals(mn.name)) continue;

            InsnList hook = new InsnList();
            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "allowChunkRendererUpdate", "()Z", false));
            LabelNode allowed = new LabelNode();
            hook.add(new JumpInsnNode(Opcodes.IFNE, allowed));
            hook.add(new InsnNode(Opcodes.RETURN));
            hook.add(allowed);
            mn.instructions.insert(hook);
            changed = true;
            break;
        }

        if (changed) { LOGGER.info("[Potassium ASM] RenderGlobal transformed"); }
        return changed ? write(cn) : bytes;
    }

    private byte[] transformRenderManager(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"(Lnet/minecraft/entity/Entity;DDDFF)V".equals(mn.desc)) continue;
            if (!"renderEntity".equals(mn.name) && !"func_147939_a".equals(mn.name)) continue;

            InsnList hook = new InsnList();
            hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "allowEntityRender",
                    "(Lnet/minecraft/entity/Entity;)Z",
                    false));
            LabelNode allowed = new LabelNode();
            hook.add(new JumpInsnNode(Opcodes.IFNE, allowed));
            hook.add(new InsnNode(Opcodes.RETURN));
            hook.add(allowed);
            mn.instructions.insert(hook);
            changed = true;
            break;
        }

        if (changed) { LOGGER.info("[Potassium ASM] RenderManager transformed"); }
        return changed ? write(cn) : bytes;
    }

    private byte[] transformBlockModelRenderer(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"()Z".equals(mn.desc)) continue;
            if (mn.instructions == null || mn.instructions.size() == 0) continue;

            // 1.8.9's main block-model method is:
            // (IBlockAccess, IBakedModel, IBlockState, BlockPos, WorldRenderer, boolean)boolean
            // We identify it by its six parameters rather than relying on a
            // particular MCP method name.
            if (!"(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockPos;Lnet/minecraft/client/renderer/WorldRenderer;Z)Z".equals(mn.desc)) {
                continue;
            }

            InsnList hook = new InsnList();
            hook.add(new VarInsnNode(Opcodes.ALOAD, 0));
            hook.add(new VarInsnNode(Opcodes.ALOAD, 2));
            hook.add(new VarInsnNode(Opcodes.ALOAD, 3));
            hook.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "skipFullyOccludedBlock",
                    "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockPos;)Z",
                    false));
            LabelNode allowed = new LabelNode();
            hook.add(new JumpInsnNode(Opcodes.IFEQ, allowed));
            hook.add(new InsnNode(Opcodes.ICONST_0));
            hook.add(new InsnNode(Opcodes.IRETURN));
            hook.add(allowed);
            mn.instructions.insert(hook);
            changed = true;
            break;
        }

        if (changed) { LOGGER.info("[Potassium ASM] BlockModelRenderer transformed"); }
        return changed ? write(cn) : bytes;
    }

    private static ClassNode read(byte[] bytes) {
        ClassNode cn = new ClassNode();
        new ClassReader(bytes).accept(cn, 0);
        return cn;
    }

    private static byte[] write(ClassNode cn) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
