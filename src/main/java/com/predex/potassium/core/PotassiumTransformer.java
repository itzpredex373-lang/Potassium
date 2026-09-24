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
            if ("net.minecraft.client.renderer.chunk.ChunkRenderDispatcher".equals(transformedName)) {
                return basicClass;
            }
            if ("net.minecraft.client.renderer.entity.RenderManager".equals(transformedName)) {
                return transformRenderManager(basicClass);
            }
            if ("net.minecraft.client.renderer.BlockModelRenderer".equals(transformedName)) {
                return transformBlockModelRenderer(basicClass);
            }
        } catch (Throwable error) {
            LOGGER.warn("[Potassium ASM] Transformer failed for " + transformedName, error);
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

        if (changed) LOGGER.info("[Potassium ASM] EffectRenderer transformed");
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

        if (changed) LOGGER.info("[Potassium ASM] Tessellator transformed");
        return changed ? write(cn) : bytes;
    }

    private byte[] transformRenderGlobal(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"(J)V".equals(mn.desc)) continue;
            if (!"updateChunks".equals(mn.name) && !"func_174967_a".equals(mn.name)
                    && !"func_72716_a".equals(mn.name)) continue;

            InsnList begin = new InsnList();
            begin.add(new VarInsnNode(Opcodes.LLOAD, 1));
            begin.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                    "beginChunkRenderPipeline", "(J)V", false));
            mn.instructions.insert(begin);

            for (AbstractInsnNode node = mn.instructions.getFirst(); node != null; ) {
                AbstractInsnNode next = node.getNext();

                if (node.getOpcode() == Opcodes.RETURN) {
                    InsnList finish = new InsnList();
                    finish.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                            "finishChunkRenderPipeline", "()V", false));
                    mn.instructions.insertBefore(node, finish);
                    changed = true;
                }

                if (node instanceof MethodInsnNode) {
                    MethodInsnNode call = (MethodInsnNode) node;
                    if ("(Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z".equals(call.desc)
                            && ("updateChunkLater".equals(call.name)
                            || "func_178507_a".equals(call.name))) {

                        LabelNode callAllowed = new LabelNode();
                        LabelNode done = new LabelNode();

                        InsnList replacement = new InsnList();
                        // Stack before this sequence: dispatcher, renderChunk.
                        // DUP2 preserves the original arguments for the actual
                        // vanilla call if the pipeline admits the job.
                        replacement.add(new InsnNode(Opcodes.DUP2));
                        replacement.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK,
                                "allowChunkDispatch",
                                "(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher;Lnet/minecraft/client/renderer/chunk/RenderChunk;)Z",
                                false));
                        replacement.add(new JumpInsnNode(Opcodes.IFNE, callAllowed));
                        replacement.add(new InsnNode(Opcodes.POP2));
                        replacement.add(new InsnNode(Opcodes.ICONST_0));
                        replacement.add(new JumpInsnNode(Opcodes.GOTO, done));
                        replacement.add(callAllowed);
                        replacement.add(new MethodInsnNode(call.getOpcode(), call.owner,
                                call.name, call.desc, call.itf));
                        replacement.add(done);

                        mn.instructions.insertBefore(node, replacement);
                        mn.instructions.remove(node);
                        changed = true;
                    }
                }

                node = next;
            }

            changed = true;
            break;
        }

        if (changed) LOGGER.info("[Potassium ASM] RenderGlobal chunk pipeline transformed");
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

        if (changed) LOGGER.info("[Potassium ASM] RenderManager transformed");
        return changed ? write(cn) : bytes;
    }

    private byte[] transformBlockModelRenderer(byte[] bytes) {
        ClassNode cn = read(bytes);
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            if (!"()Z".equals(mn.desc)) continue;
            if (mn.instructions == null || mn.instructions.size() == 0) continue;

            if (!"(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/BlockPos;Lnet/minecraft/client/renderer/WorldRenderer;Z)Z".equals(mn.desc)) {
                continue;
            }

            InsnList hook = new InsnList();
            hook.add(new VarInsnNode(Opcodes.ALOAD, 1));
            hook.add(new VarInsnNode(Opcodes.ALOAD, 3));
            hook.add(new VarInsnNode(Opcodes.ALOAD, 4));
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

        if (changed) LOGGER.info("[Potassium ASM] BlockModelRenderer transformed");
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
