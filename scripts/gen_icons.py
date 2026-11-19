"""Generate launcher icon PNGs for Ubah Ikon.

Draws a solid-color rounded square with a white 4-point sparkle, in all
densities (mdpi..xxxhdpi). Run from the repo root:
    pip install pillow
    python3 scripts/gen_icons.py

The icons are generated at build time by GitHub Actions, so the PNGs are
not committed to the repository.
"""
import math
import os

from PIL import Image, ImageDraw, ImageFont

REPO_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
BASE = os.path.join(REPO_ROOT, "app", "src", "main", "res")

COLORS = [
    ("ic_launcher", "#003435"),  # default
    ("ic_red", "#E53935"),
    ("ic_orange", "#F57C00"),
    ("ic_green", "#43A047"),
    ("ic_teal", "#00897B"),
    ("ic_blue", "#1E88E5"),
    ("ic_purple", "#8E24AA"),
    ("ic_pink", "#D81B60"),
    ("ic_brown", "#6D4C41"),
]
DENSITIES = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}


def hex2rgb(h):
    h = h.lstrip("#")
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))


def star_points(cx, cy, r_out, r_in, n=4):
    pts = []
    for i in range(n * 2):
        r = r_out if i % 2 == 0 else r_in
        ang = math.pi * i / n - math.pi / 2
        pts.append((cx + r * math.cos(ang), cy + r * math.sin(ang)))
    return pts


for name, hexcol in COLORS:
    rgb = hex2rgb(hexcol)
    for dens, size in DENSITIES.items():
        os.makedirs(os.path.join(BASE, f"mipmap-{dens}"), exist_ok=True)
        img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        dr = ImageDraw.Draw(img)
        dr.rounded_rectangle(
            [0, 0, size - 1, size - 1],
            radius=int(size * 0.22),
            fill=rgb + (255,),
        )
        cx = cy = size / 2
        pts = star_points(cx, cy, size * 0.30, size * 0.075)
        dr.polygon(pts, fill=(255, 255, 255, 255))
        img.save(os.path.join(BASE, f"mipmap-{dens}", name + ".png"))

print("icons generated")

# --- Widget swatches: satu drawable warna solid per warna.
# (android:backgroundTint tidak bisa diandalkan di layout RemoteViews,
# jadi layout widget memakai drawable statis ini.)
DRAWABLE_DIR = os.path.join(REPO_ROOT, "app", "src", "main", "res", "drawable")
os.makedirs(DRAWABLE_DIR, exist_ok=True)
for name, hexcol in COLORS:
    short = {"ic_launcher": "default"}.get(name, name.replace("ic_", ""))
    with open(os.path.join(DRAWABLE_DIR, f"swatch_{short}.xml"), "w") as f:
        f.write(
            '<?xml version="1.0" encoding="utf-8"?>\n'
            '<shape xmlns:android="http://schemas.android.com/apk/res/android"\n'
            '    android:shape="rectangle">\n'
            f'    <solid android:color="{hexcol}" />\n'
            '    <corners android:radius="12dp" />\n'
            '</shape>\n'
        )

# --- Widget preview image untuk daftar widget (previewImage).
def _load_font(bold, size):
    for path in (
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
        "/usr/share/fonts/TTF/DejaVuSans-Bold.ttf",
        "DejaVuSans-Bold.ttf",
    ):
        try:
            return ImageFont.truetype(path, size)
        except Exception:
            continue
    return ImageFont.load_default()

PREVIEW_DIR = os.path.join(
    REPO_ROOT, "app", "src", "main", "res", "drawable-xxxhdpi"
)
os.makedirs(PREVIEW_DIR, exist_ok=True)
pw, ph = 720, 800
preview = Image.new("RGBA", (pw, ph), (255, 255, 255, 255))
pdr = ImageDraw.Draw(preview)
font_title = _load_font(True, 44)
pdr.text((40, 36), "Ubah Ikon", fill=(34, 34, 34, 255), font=font_title)
margin, gap = 40, 24
cell = (pw - 2 * margin - 2 * gap) // 3
top = 130
for i, (name, hexcol) in enumerate(COLORS):
    row, col = divmod(i, 3)
    x0 = margin + col * (cell + gap)
    y0 = top + row * (cell + gap)
    pdr.rounded_rectangle(
        [x0, y0, x0 + cell, y0 + cell],
        radius=28,
        fill=hex2rgb(hexcol) + (255,),
    )
    if i == 0:
        pts = star_points(x0 + cell / 2, y0 + cell / 2, cell * 0.30, cell * 0.075)
        pdr.polygon(pts, fill=(255, 255, 255, 255))
preview.save(os.path.join(PREVIEW_DIR, "widget_preview.png"))
print("widget swatches + preview generated")
