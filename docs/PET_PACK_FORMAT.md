# .potpet format

A `.potpet` file is a ZIP archive renamed with the `.potpet` extension.

Required:
- `manifest.json`
- `model.json`

Optional:
- `animation.json`
- `texture.png`

Example manifest:

    {
      "id": "my_dragon",
      "name": "My Dragon",
      "author": "YourName",
      "apiVersion": 1
    }

Example model:

    {
      "textureWidth": 32,
      "textureHeight": 32,
      "color": [0.25, 0.55, 1.0],
      "parts": [
        {"role":"body","x":-3,"y":-3,"z":-2,"width":6,"height":6,"depth":5,"textureX":0,"textureY":0},
        {"role":"head","x":-2,"y":-8,"z":-2,"width":4,"height":4,"depth":4,"textureX":0,"textureY":16},
        {"role":"leg","x":1,"y":2,"z":-1,"width":2,"height":3,"depth":2,"textureX":16,"textureY":0},
        {"role":"leg2","x":-3,"y":2,"z":-1,"width":2,"height":3,"depth":2,"textureX":16,"textureY":8},
        {"role":"tail","x":-1,"y":-1,"z":3,"width":2,"height":2,"depth":5,"textureX":24,"textureY":0}
      ]
    }

Example animation:

    {"bob":0.20,"bobSpeed":0.16,"sway":0.12,"swaySpeed":0.11}

Supported roles: `body`, `head`, `leg`, `leg2`, `tail`, `wing`.

## Safety/performance limits

- 5 MB maximum pack size
- 1 MB maximum file size
- 32 model parts
- 32-block maximum dimension per model box
- No Java, class or script files are loaded from a pack
- ZIP entries outside the allowed file names are ignored

## Import

Open the Potassium Pet Menu, paste the full `.potpet` file path into the Import field, and press Import.

Imported packs are copied to `.minecraft/config/potassium/pets` and become selectable beside the 20 built-in pets.