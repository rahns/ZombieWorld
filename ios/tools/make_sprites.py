#!/usr/bin/env python3
"""Builds the game's sprites into ZombieWorld/Assets.xcassets/Sprites.

Most sprites come from Kenney's "Tiny Town" and "Tiny Dungeon" packs (CC0, www.kenney.nl).
A few things those packs don't have (zombie limbs, guns, turret, ammo) are drawn below in the
same 16x16 style and palette.

Usage:
    python3 make_sprites.py <tiny-town folder> <tiny-dungeon folder>
Each folder is an unzipped pack containing a Tiles/ directory. Needs Pillow.
"""
import json
import os
import sys

from PIL import Image, ImageEnhance

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "..", "ZombieWorld", "Assets.xcassets", "Sprites")

PALETTE = {
    "O": (0x3F, 0x26, 0x31),  # outline
    "L": (0xC0, 0xCB, 0xDC),  # light metal
    "M": (0x8B, 0x9B, 0xB4),  # metal
    "D": (0x52, 0x60, 0x7C),  # dark metal
    "W": (0xBD, 0x6C, 0x4A),  # wood
    "w": (0xEA, 0xA5, 0x6C),  # light wood
    "G": (0x43, 0xE1, 0xB3),  # zombie green
    "g": (0x69, 0xFF, 0xD4),  # light zombie green
    "d": (0x25, 0x95, 0x6A),  # dark zombie green
    "R": (0xE8, 0x45, 0x37),  # red
    "r": (0x76, 0x3B, 0x36),  # dark red
    "Y": (0xF7, 0xC5, 0x4A),  # brass
}

DRAWN = {
    "item_arm": """
................
................
................
................
............OO..
.....OOOOOOOgGO.
..OOOgGGGGGGGGGO
.ORRGGGGGGGGGGO.
.ORrGGGGGGGGdGGO
..OOOddddddddGdO
.....OOOOOOOOOO.
................
................
................
................
................""",
    "item_leg": """
................
.....OOOOO......
....ORRrRRO.....
....OWwWWWO.....
....OWwWWWO.....
....OWwWWWO.....
....OWWWWWO.....
....OGgGGGO.....
....OGgGGGO.....
....OGgGGGO.....
....OGGGGGO.....
....OGGGGGOOO...
....OGgGGGGGGO..
....OGGGGGGGGO..
....OddddddddO..
.....OOOOOOOO...""",
    "item_shotgun": """
................
................
................
................
................
..OOO...........
.OwWWOOOOOOOOOOO
.OWWWWLLLLLLLLLO
.OWWWWMMMMMMMMMO
..OWWOODOOOOOOOO
...OOOOOO.......
.....OWWO.......
......OO........
................
................
................""",
    "item_sniper": """
................
................
................
................
.....OOOOOO.....
.....OLLLMO.....
..OOO.ODDO......
.OwWWOOOOOOOOOOO
.OWWWWDDDDDDDDLO
..OWWOOOOOOOOOOO
..OWWO.ODO......
...OOO.OO.......
................
................
................
................""",
    "item_ammo": """
................
................
................
................
....O.O.O.O.....
...OYOYOYOYO....
...OYOYOYOYO....
..OOOOOOOOOOOO..
..ODDDDDDDDDDO..
..ODLLLLLLLLDO..
..ODMMMMMMMMDO..
..ODDDDDDDDDDO..
..OOOOOOOOOOOO..
................
................
................""",
    "item_train": """
................
...OOO..........
...ODO..........
..OODOOOOOOOOO..
..ORRRRRORLLRO..
..ORRRRRORLLRO..
..ORRRRRORRRRO..
.OOOOOOOOOOOOOO.
.OYRRRRRRRRRRYO.
.OOOOOOOOOOOOOO.
..ODDO....ODDO..
..ODLO....ODLO..
...OO......OO...
................
................
................""",
    "item_turret": """
................
................
................
.......OOOOOOOOO
.....OOLLLLLLLLO
....OMMMMOOOOOOO
...OMLLLMO......
...OMMMMMO......
....OMMMO.......
.....ODO........
....ODODO.......
...ODO.ODO......
..ODO...ODO.....
..OO.....OO.....
................
................""",
}

# (name, pack, tile number)
PICKS = [
    ("ground_grass", "town", 0),
    ("ground_grass_tufts", "town", 1),
    ("ground_grass_flowers", "town", 2),
    ("ground_soil", "town", 25),
    ("ground_wall", "town", 126),
    ("ground_door_closed", "town", 89),
    ("ground_door_open", "town", 78),
    ("ground_shop_open", "town", 86),
    ("deco_tree_sapling", "town", 17),
    ("deco_tree_young", "town", 28),
    ("deco_tree_big", "town", 16),
    ("deco_crop_sprout", "town", 17),
    ("item_food", "town", 29),
    ("item_coin", "town", 93),
    ("item_plank", "town", 81),
    ("item_hoe", "town", 129),
    ("item_corpse", "dungeon", 64),
    ("item_club", "dungeon", 107),
    ("item_mace", "dungeon", 117),
    ("actor_player", "dungeon", 96),
    ("actor_human_0", "dungeon", 85),
    ("actor_human_1", "dungeon", 87),
    ("actor_human_2", "dungeon", 88),
    ("actor_human_3", "dungeon", 99),
    ("actor_human_4", "dungeon", 100),
    ("actor_human_5", "dungeon", 112),
    ("actor_human_6", "dungeon", 98),
    ("actor_farmer", "dungeon", 86),
    ("actor_zombie_0", "dungeon", 108),
    ("actor_mambo", "dungeon", 84),
]


def tile(packs, pack, number):
    return Image.open(os.path.join(packs[pack], "Tiles", "tile_%04d.png" % number)).convert("RGBA")


def draw(grid):
    rows = [r for r in grid.strip("\n").split("\n")]
    assert len(rows) == 16 and all(len(r) == 16 for r in rows), grid
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, c in enumerate(row):
            if c != ".":
                img.putpixel((x, y), PALETTE[c] + (255,))
    return img


def recolour(img, mapping):
    """Swaps exact colours, keeping alpha."""
    out = img.copy()
    px = out.load()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = px[x, y]
            if a and (r, g, b) in mapping:
                px[x, y] = mapping[(r, g, b)] + (a,)
    return out


def zombify(img):
    """Gives a human sprite green, sickly skin."""
    out = img.copy()
    px = out.load()
    for y in range(out.height):
        for x in range(out.width):
            r, g, b, a = px[x, y]
            if a == 0:
                continue
            # skin tones are warm and light: turn them green
            if r > 180 and g > 120 and b < 150 and r > b + 50:
                px[x, y] = (0x8F, 0xD9, 0x7A, a)
            elif r > 140 and g > 80 and b < 110 and r > b + 60:
                px[x, y] = (0x5E, 0xA8, 0x5A, a)
    return ImageEnhance.Color(out).enhance(0.75)


def save(name, img):
    folder = os.path.join(OUT, name + ".imageset")
    os.makedirs(folder, exist_ok=True)
    img.save(os.path.join(folder, name + ".png"))
    contents = {
        "images": [{"filename": name + ".png", "idiom": "universal"}],
        "info": {"author": "xcode", "version": 1},
        "properties": {"preserves-vector-representation": False},
    }
    with open(os.path.join(folder, "Contents.json"), "w") as f:
        json.dump(contents, f, indent=2)


def main():
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    packs = {"town": sys.argv[1], "dungeon": sys.argv[2]}
    os.makedirs(OUT, exist_ok=True)
    with open(os.path.join(OUT, "Contents.json"), "w") as f:
        json.dump({"info": {"author": "xcode", "version": 1}}, f, indent=2)

    for name, pack, number in PICKS:
        save(name, tile(packs, pack, number))

    for name, grid in DRAWN.items():
        if name != "item_train":
            save(name, draw(grid))

    # Ripe crop: the sprout turned golden like wheat.
    golden = {(0x47, 0x9F, 0x4A): (0xC9, 0x8A, 0x2E), (0x84, 0xC6, 0x69): (0xF7, 0xC5, 0x4A)}
    save("deco_crop_ripe", recolour(tile(packs, "town", 17), golden))

    # The closed shop is boarded up with a plank.
    shop = tile(packs, "town", 87)
    shop.alpha_composite(tile(packs, "town", 81))
    save("ground_shop_closed", shop)

    # A zombified human for variety, and the packed turret (greyed out).
    save("actor_zombie_1", zombify(tile(packs, "dungeon", 85)))
    turret = draw(DRAWN["item_turret"])
    save("item_turret_packed", ImageEnhance.Brightness(ImageEnhance.Color(turret).enhance(0.2)).enhance(0.8))

    # The train: a little locomotive on rails.
    train = tile(packs, "dungeon", 79)
    train.alpha_composite(draw(DRAWN.pop("item_train")))
    save("item_train", train)

    print("Wrote sprites to", os.path.normpath(OUT))


if __name__ == "__main__":
    main()
