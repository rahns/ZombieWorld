#!/usr/bin/env python3
"""Draws the app icon: the zombie sprite reimagined at high resolution under a full moon.

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
GREEN = (0x43, 0xE1, 0xB3)
LIGHT = (0x8D, 0xFF, 0xDE)
DARK = (0x25, 0x95, 0x6A)
DEEP = (0x1A, 0x6E, 0x55)


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


def zombie():
    """The ghoul from the sprite: a rounded body, side arms, tattered hem, hollow eyes and mouth."""
    layer = Image.new("RGBA", (S, S))
    d = ImageDraw.Draw(layer)
    cx, top, bottom, half = 1024, 430, 1640, 430

    def body_shape(inset=0):
        pts = []
        # Dome
        for i in range(0, 181, 3):
            a = math.radians(180 + i)
            pts.append((cx + (half - inset) * math.cos(a), top + half + (half - inset) * math.sin(a)))
        # Right side down to the hem
        pts.append((cx + half - inset, bottom - 90))
        # Tattered hem, right to left
        teeth = 5
        width = 2 * (half - inset)
        for t in range(teeth * 2 + 1):
            x = cx + half - inset - width * t / (teeth * 2)
            y = bottom - inset + (0 if t % 2 == 0 else -110)
            pts.append((x, y))
        pts.append((cx - half + inset, bottom - 90))
        return pts

    # Arms reaching out
    for side in (-1, 1):
        arm = [(cx + side * 380, 1040), (cx + side * 640, 980), (cx + side * 700, 1060),
               (cx + side * 660, 1150), (cx + side * 400, 1220)]
        d.polygon(arm, fill=OUTLINE)
        inner = [(cx + side * 390, 1080), (cx + side * 620, 1025), (cx + side * 655, 1070),
                 (cx + side * 625, 1120), (cx + side * 400, 1180)]
        d.polygon(inner, fill=DARK)
        for f in range(3):  # fingers
            fx = cx + side * (680 + 20 * f)
            fy = 1010 + 55 * f
            d.ellipse([fx - 45, fy - 32, fx + 45, fy + 32], fill=OUTLINE)
            d.ellipse([fx - 28, fy - 18, fx + 28, fy + 18], fill=GREEN)

    d.polygon(body_shape(-34), fill=OUTLINE)
    d.polygon(body_shape(0), fill=GREEN)

    # Shading: darker sides, light dome highlight
    shade = Image.new("L", (S, S))
    sd = ImageDraw.Draw(shade)
    sd.ellipse([cx - half - 260, 700, cx - half + 160, bottom + 300], fill=255)
    sd.ellipse([cx + half - 160, 700, cx + half + 260, bottom + 300], fill=255)
    sd.rectangle([cx - half, bottom - 260, cx + half, bottom + 40], fill=170)
    shade = shade.filter(ImageFilter.GaussianBlur(90))
    mask = Image.new("L", (S, S))
    ImageDraw.Draw(mask).polygon(body_shape(0), fill=255)
    from PIL import ImageChops
    layer.paste(DARK, (0, 0), ImageChops.multiply(shade, mask))

    hl = Image.new("L", (S, S))
    ImageDraw.Draw(hl).ellipse([cx - 240, top + 50, cx + 240, top + 300], fill=255)
    hl = hl.filter(ImageFilter.GaussianBlur(35))
    layer.paste(LIGHT, (0, 0), ImageChops.multiply(hl, mask))

    d = ImageDraw.Draw(layer)
    # Rot spots
    for (x, y, rx, ry) in [(cx + 250, 1130, 55, 40), (cx + 300, 1230, 28, 22), (cx - 290, 1350, 45, 34)]:
        d.ellipse([x - rx, y - ry, x + rx, y + ry], fill=DEEP)
    # Stitched scar across the head
    scar = [(cx - 330, 640), (cx - 230, 560), (cx - 120, 530), (cx - 20, 545)]
    d.line(scar, fill=OUTLINE, width=22, joint="curve")
    for (x, y) in [(cx - 300, 615), (cx - 225, 562), (cx - 145, 535), (cx - 65, 538)]:
        d.line([(x - 22, y - 38), (x + 22, y + 38)], fill=OUTLINE, width=16)
    # Eyes: tall and hollow with a faint red glow inside
    for side in (-1, 1):
        ex = cx + side * 165
        d.rounded_rectangle([ex - 75, 700, ex + 75, 960], radius=60, fill=OUTLINE)
        d.ellipse([ex - 22, 860, ex + 22, 904], fill=(0xFF, 0x5A, 0x4A))
        d.ellipse([ex - 10, 872, ex + 10, 892], fill=(0xFF, 0xD0, 0xC0))
    # Gaping mouth
    d.rounded_rectangle([cx - 115, 1060, cx + 115, 1290], radius=70, fill=OUTLINE)
    d.rounded_rectangle([cx - 70, 1200, cx + 70, 1290], radius=40, fill=(0x76, 0x3B, 0x36))
    # Drool
    d.rounded_rectangle([cx + 60, 1250, cx + 92, 1360], radius=16, fill=LIGHT)
    return layer


def main():
    img = background().convert("RGBA")
    graveyard(img)
    # Shadow under the zombie
    shadow = Image.new("L", (S, S))
    ImageDraw.Draw(shadow).ellipse([560, 1600, 1490, 1760], fill=150)
    img.paste((0, 0, 0), (0, 0), shadow.filter(ImageFilter.GaussianBlur(30)))
    img.alpha_composite(zombie(), (0, 40))
    img = img.convert("RGB").resize((1024, 1024), Image.LANCZOS)
    img.save(OUT)
    print("Wrote", os.path.normpath(OUT))


if __name__ == "__main__":
    main()
