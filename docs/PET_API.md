# Potassium Pet API v1

Potassium exposes a client-side pet API for child mods and community pet packs.

## Java API

Use:

    import com.predex.potassium.api.pet.PetDefinition;
    import com.predex.potassium.api.pet.PetRenderer;
    import com.predex.potassium.api.pet.PotassiumPetAPI;

    PetRenderer renderer = new PetRenderer() {
        @Override
        public void render(float ageInTicks, float scale) {
            // Render your own ModelBase/ModelRenderer here.
        }
    };

    PotassiumPetAPI.register(new PetDefinition(
        "my_pet", "My Pet", "MyName",
        PotassiumPetAPI.VERSION, renderer
    ));

Rules:
- IDs use lowercase letters, numbers, `_` and `-`.
- API version must equal `PotassiumPetAPI.VERSION`.
- Pets are client-side cosmetics only.
- Do not create Forge entities for a companion pet.
- Keep geometry, animation and texture work lightweight.
- Avoid allocations inside the renderer's `render()` method.

## Data-only packs

For users who do not want a child mod, Potassium also supports `.potpet` packs. See `PET_PACK_FORMAT.md`.

## Compatibility

Child mods should check the API version before registering optional integrations. If Potassium is missing, disable only the Potassium integration rather than preventing Minecraft from starting.