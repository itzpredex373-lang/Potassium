package com.predex.potassium.optimization.rendering;

import java.nio.ByteBuffer;

public final class VertexBufferOptimizer {
    private VertexBufferOptimizer() {}

    public static boolean isUsable(ByteBuffer buffer) {
        return buffer != null && buffer.remaining() > 0;
    }

    public static int safeVertexCount(int bytesPerVertex, int bytes) {
        if (bytesPerVertex <= 0 || bytes <= 0) return 0;
        return bytes / bytesPerVertex;
    }
}