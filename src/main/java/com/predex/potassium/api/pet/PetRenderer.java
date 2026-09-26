package com.predex.potassium.api.pet;

/**
 * Client-side renderer contract for Potassium-compatible pets.
 *
 * Implementations should keep rendering lightweight and avoid world/server state.
 */
public interface PetRenderer {
    void render(float ageInTicks, float scale);
}
