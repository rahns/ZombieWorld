#!/usr/bin/env python3
"""Draws the app icon: the game's zombie, shambling under a full moon, at high resolution.

Usage: python3 make_icon.py   (needs Pillow)
Writes ZombieWorld/Assets.xcassets/AppIcon.appiconset/AppIcon.png (1024x1024).
"""
import math
import os
import random

from PIL import Image, ImageDraw, ImageFilter

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "..", "ZombieWorld", "Assets.xcassets", "AppIcon.appiconset", "AppIcon.png")

S = 2048  # drawn at 2x, then downsampled for smooth edges
OUTLINE = (0x3F, 0x26, 0x31)


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def background():
    img = Image.new("RGB", (S, S))
    top, bottom = (0x14, 0x17, 0x33), (0x4A, 0x27, 0x5C)
    d = ImageDraw.Draw(img)
    for y in range(S):
        d.line([(0, y), (S, y)], fill=lerp(top, bottom, (y / S) ** 1.2))

    # Stars
    rng = random.Random(7)
    for _ in range(70):
        x, y, r = rng.randrange(S), rng.randrange(int(S * 0.55)), rng.choice([3, 4, 5, 7])
        d.ellipse([x - r, y - r, x + r, y + r], fill=(255, 250, 220))

    # Moon with a soft glow
    glow = Image.new("L", (S, S))
    ImageDraw.Draw(glow).ellipse([1180, 140, 1900, 860], fill=255)
    glow = glow.filter(ImageFilter.GaussianBlur(110))
    img.paste((0xFF, 0xF1, 0xB8), (0, 0), glow.point(lambda v: int(v * 0.45)))
    d = ImageDraw.Draw(img)
    d.ellipse([1320, 280, 1760, 720], fill=(0xFF, 0xF4, 0xC6))
    for (cx, cy, r) in [(1450, 420, 46), (1610, 560, 64), (1520, 640, 30), (1660, 380, 26)]:
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=(0xF0, 0xDC, 0x9E))
    return img


def graveyard(img):
    d = ImageDraw.Draw(img)
    hill = (0x1E, 0x3B, 0x2E)
    # Tombstones behind the hill
    for (x, w, h, tilt) in [(150, 230, 360, -6), (520, 170, 260, 5), (1560, 210, 330, 7), (1830, 160, 240, -4)]:
        stone = Image.new("RGBA", (w + 80, h + 80))
        sd = ImageDraw.Draw(stone)
        sd.rounded_rectangle([40, 40, 40 + w, 40 + h + 60], radius=w // 2, fill=(0x3A, 0x3F, 0x5E), outline=OUTLINE, width=16)
        sd.line([(40 + w // 2 - 40, 40 + h // 3), (40 + w // 2 + 40, 40 + h // 3)], fill=OUTLINE, width=14)
        sd.line([(40 + w // 2, 40 + h // 3 - 50), (40 + w // 2, 40 + h // 3 + 60)], fill=OUTLINE, width=14)
        stone = stone.rotate(tilt, resample=Image.BICUBIC, expand=True)
        img.paste(stone, (x - 40, 1500 - h), stone)
    # Rolling hill
    points = [(0, S)]
    for x in range(0, S + 1, 16):
        points.append((x, 1490 + 60 * math.sin(x / 260) + 30 * math.sin(x / 97)))
    points.append((S, S))
    d.polygon(points, fill=hill)
    # Grass tufts on the ridge
    rng = random.Random(3)
    for x in range(30, S, 70):
        base = 1490 + 60 * math.sin(x / 260) + 30 * math.sin(x / 97)
        for dx in (-14, 0, 14):
            h = rng.randint(30, 70)
            d.polygon([(x + dx - 10, base + 6), (x + dx + 10, base + 6), (x + dx + dx * 0.6, base - h)], fill=hill)


def outlined_polygon(d, points, fill, width=30):
    d.polygon(points, fill=OUTLINE)
    d.line(points + [points[0]], fill=OUTLINE, width=width * 2, joint="curve")
    for p in points:
        d.ellipse([p[0] - width, p[1] - width, p[0] + width, p[1] + width], fill=OUTLINE)
    d.polygon(points, fill=fill)


def arm(d, shoulder_y, reach, skin, shade, sleeve):
    """An arm reaching to the right, with a torn sleeve and clawed fingers."""
    x0 = 860
    # Forearm tapers a little towards a bony wrist
    outlined_polygon(d, [(x0, shoulder_y - 72), (x0 + 330, shoulder_y - 62), (reach - 120, shoulder_y - 48),
                         (reach - 120, shoulder_y + 48), (x0 + 330, shoulder_y + 66), (x0, shoulder_y + 80)], skin)
    d.polygon([(x0 + 40, shoulder_y + 36), (reach - 140, shoulder_y + 28), (reach - 140, shoulder_y + 46),
               (x0 + 40, shoulder_y + 62)], fill=shade)
    for (x, r) in [(x0 + 420, 22), (x0 + 520, 14)]:  # rot spots
        d.ellipse([x - r, shoulder_y - 30 - r * 0.7, x + r, shoulder_y - 30 + r * 0.7], fill=shade)
    # Torn sleeve
    outlined_polygon(d, [(x0 - 60, shoulder_y - 95), (x0 + 170, shoulder_y - 85), (x0 + 140, shoulder_y - 20),
                         (x0 + 185, shoulder_y + 20), (x0 + 130, shoulder_y + 100), (x0 - 60, shoulder_y + 105)], sleeve, 24)
    # Hand and fingers
    hx = reach - 130
    outlined_polygon(d, [(hx - 20, shoulder_y - 80), (hx + 90, shoulder_y - 85), (hx + 120, shoulder_y + 10),
                         (hx + 90, shoulder_y + 85), (hx - 20, shoulder_y + 80)], skin, 26)
    for i, fy in enumerate((-60, -10, 40)):
        length = 150 - 20 * abs(i - 1)
        outlined_polygon(d, [(hx + 80, shoulder_y + fy - 18), (hx + 80 + length, shoulder_y + fy - 8),
                             (hx + 80 + length + 30, shoulder_y + fy + 8), (hx + 80 + length, shoulder_y + fy + 22),
                             (hx + 80, shoulder_y + fy + 24)], skin, 22)
        # nails
        d.polygon([(hx + 80 + length + 2, shoulder_y + fy - 4), (hx + 80 + length + 30, shoulder_y + fy + 8),
                   (hx + 80 + length + 2, shoulder_y + fy + 18)], fill=(0xE8, 0xE8, 0xD0))


def zombie():
    """The shambling zombie from the sprite: side-on, arms out, torn shirt, hollow red eyes."""
    layer = Image.new("RGBA", (S, S))
    d = ImageDraw.Draw(layer)
    skin, skin_light, skin_dark = (0x7E, 0xBF, 0x63), (0xB4, 0xE0, 0x8C), (0x4E, 0x8A, 0x4A)
    shirt, shirt_light, pants = (0x52, 0x60, 0x7C), (0x8B, 0x9B, 0xB4), (0x76, 0x3B, 0x36)
    blood, hair, hair_light = (0xC8, 0x2E, 0x2E), (0x5A, 0x3A, 0x2E), (0x86, 0x5A, 0x42)

    # Back arm (darker, behind the body)
    arm(d, 1130, 1700, skin_dark, (0x3B, 0x6B, 0x3A), (0x3E, 0x4A, 0x63))

    # Legs
    outlined_polygon(d, [(560, 1440), (720, 1440), (700, 1690), (540, 1690)], pants)
    outlined_polygon(d, [(780, 1440), (940, 1440), (980, 1690), (820, 1690)], (0x8A, 0x46, 0x3E))
    outlined_polygon(d, [(510, 1660), (720, 1660), (720, 1740), (490, 1740)], OUTLINE, 14)
    outlined_polygon(d, [(810, 1660), (1040, 1660), (1060, 1740), (810, 1740)], OUTLINE, 14)

    # Torso with a torn hem and blood stains
    torso = [(470, 1030), (1000, 1010), (1040, 1470), (960, 1430), (900, 1500), (820, 1440),
             (740, 1510), (660, 1440), (580, 1500), (500, 1440)]
    outlined_polygon(d, torso, shirt)
    d.polygon([(520, 1060), (640, 1050), (600, 1420), (540, 1430)], fill=shirt_light)
    for (x, y, r) in [(760, 1180, 60), (830, 1260, 34), (700, 1330, 26)]:
        d.ellipse([x - r, y - r * 0.8, x + r, y + r * 0.8], fill=blood)

    # Neck
    outlined_polygon(d, [(650, 930), (840, 930), (830, 1060), (660, 1060)], skin_dark)

    # Head
    d.rounded_rectangle([370, 300, 1210, 1010], radius=230, fill=OUTLINE)
    d.rounded_rectangle([400, 330, 1180, 980], radius=200, fill=skin)
    shade = Image.new("L", (S, S))
    ImageDraw.Draw(shade).ellipse([300, 620, 760, 1150], fill=255)
    shade = shade.filter(ImageFilter.GaussianBlur(70))
    mask = Image.new("L", (S, S))
    ImageDraw.Draw(mask).rounded_rectangle([400, 330, 1180, 980], radius=200, fill=255)
    from PIL import ImageChops
    layer.paste(skin_dark, (0, 0), ImageChops.multiply(shade, mask))
    hl = Image.new("L", (S, S))
    ImageDraw.Draw(hl).ellipse([640, 380, 1080, 560], fill=200)
    layer.paste(skin_light, (0, 0), ImageChops.multiply(hl.filter(ImageFilter.GaussianBlur(40)), mask))
    d = ImageDraw.Draw(layer)

    # Messy hair
    tufts = [(380, 560), (360, 380), (470, 420), (480, 260), (600, 360), (660, 220), (760, 340),
             (860, 230), (920, 350), (1040, 270), (1060, 380), (1190, 360), (1170, 470), (1000, 430),
             (880, 470), (760, 420), (640, 470), (520, 450), (450, 560)]
    outlined_polygon(d, tufts, hair, 24)
    d.polygon([(500, 400), (600, 360), (660, 300), (700, 380), (600, 420)], fill=hair_light)

    # Stitched scar
    d.line([(470, 640), (560, 590), (650, 575)], fill=OUTLINE, width=18, joint="curve")
    for (x, y) in [(495, 625), (560, 590), (625, 578)]:
        d.line([(x - 18, y - 32), (x + 18, y + 32)], fill=OUTLINE, width=14)

    # Hollow eyes with glowing red pupils (looking right)
    glow = Image.new("L", (S, S))
    gd = ImageDraw.Draw(glow)
    for ex in (720, 1000):
        d.ellipse([ex - 110, 570, ex + 110, 780], fill=(0x26, 0x2B, 0x44))
        gd.ellipse([ex - 10, 620, ex + 90, 720], fill=255)
    layer.paste((0xFF, 0x50, 0x40), (0, 0), glow.filter(ImageFilter.GaussianBlur(30)))
    d = ImageDraw.Draw(layer)
    for ex in (720, 1000):
        d.ellipse([ex + 5, 630, ex + 75, 705], fill=(0xE8, 0x45, 0x37))
        d.ellipse([ex + 30, 645, ex + 55, 670], fill=(0xFF, 0xD8, 0xC8))

    # Nose holes
    for nx in (1095, 1135):
        d.ellipse([nx - 14, 790, nx + 14, 820], fill=skin_dark)

    # Gaping mouth with ragged teeth
    d.rounded_rectangle([700, 840, 1130, 960], radius=50, fill=OUTLINE)
    d.rounded_rectangle([740, 880, 1100, 950], radius=30, fill=(0x76, 0x3B, 0x36))
    teeth = (0xE8, 0xE8, 0xD0)
    for i, x in enumerate(range(760, 1090, 75)):
        top = i % 2 == 0
        d.polygon([(x, 862), (x + 50, 862), (x + 25, 910 if top else 895)], fill=teeth)
    for x in (800, 950):
        d.polygon([(x, 952), (x + 45, 952), (x + 22, 905)], fill=teeth)

    # Front arm, reaching out over the body
    arm(d, 1290, 1780, skin, skin_dark, shirt)
    return layer


def main():
    img = background().convert("RGBA")
    graveyard(img)
    # Shadow under the zombie
    shadow = Image.new("L", (S, S))
    ImageDraw.Draw(shadow).ellipse([490, 1680, 1570, 1820], fill=150)
    img.paste((0, 0, 0), (0, 0), shadow.filter(ImageFilter.GaussianBlur(30)))
    img.alpha_composite(zombie(), (40, 20))
    img = img.convert("RGB").resize((1024, 1024), Image.LANCZOS)
    img.save(OUT)
    print("Wrote", os.path.normpath(OUT))


if __name__ == "__main__":
    main()
