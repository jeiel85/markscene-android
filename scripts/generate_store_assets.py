"""
MarkScene Play Store Asset Generator
Generates: 512x512 app icon, 1024x500 feature graphic
"""

import math
from PIL import Image, ImageDraw, ImageFont
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
OUTPUT_DIR = REPO_ROOT / "store-assets"

# Colors (from app theme)
TEAL_DARK = (15, 118, 110)       # #0F766E
TEAL_LIGHT = (20, 184, 166)      # #14B8A6
NEAR_WHITE = (230, 255, 250)     # #E6FFFA
DARK_BG = (31, 41, 55)           # #1F2937
WHITE = (255, 255, 255)
BG_LIGHT = (250, 248, 243)       # #FAF8F3
TEXT_SECONDARY = (122, 111, 90)  # #7A6F5A


def draw_icon(draw, cx, cy, size):
    """Draw the MarkScene icon (camera/scene card motif) centered at cx,cy"""
    s = size / 108.0  # scale factor (viewport = 108)

    # Background circle
    r = size * 0.46
    draw.ellipse([cx - r, cy - r, cx + r, cy + r], fill=TEAL_DARK)

    # Inner card (rounded rect approximation)
    card_left = cx - 38 * s
    card_top = cy - 30 * s
    card_right = cx + 38 * s
    card_bottom = cy + 30 * s
    card_r = 8 * s
    draw.rounded_rectangle([card_left, card_top, card_right, card_bottom], radius=card_r, fill=TEAL_LIGHT)

    # Photo area
    photo_left = cx - 24 * s
    photo_top = cy - 10 * s
    photo_right = cx + 24 * s
    photo_bottom = cy + 10 * s
    photo_r = 5 * s
    draw.rounded_rectangle([photo_left, photo_top, photo_right, photo_bottom], radius=photo_r, fill=NEAR_WHITE)

    # Mountain path inside photo
    path_points = [
        (cx - 24 * s, cy - 8 * s),
        (cx - 16 * s, cy - 16 * s),
        (cx - 8 * s, cy - 8 * s),
        (cx + 8 * s, cy - 16 * s),
        (cx + 16 * s, cy - 8 * s),
    ]
    # Fill bottom area under mountain line
    poly_points = path_points + [(cx + 24 * s, cy + 10 * s), (cx - 24 * s, cy + 10 * s)]
    draw.polygon(poly_points, fill=TEAL_DARK)

    # Camera lens circle
    lens_r = 4 * s
    draw.ellipse([cx + 20 * s - lens_r, cy - 16 * s - lens_r,
                  cx + 20 * s + lens_r, cy - 16 * s + lens_r], fill=TEAL_DARK)


def create_app_icon():
    """Generate 512x512 app icon PNG"""
    size = 512
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)

    # Gradient background (diagonal)
    for i in range(size):
        t = i / size
        r = int(DARK_BG[0] + (TEAL_DARK[0] - DARK_BG[0]) * t)
        g = int(DARK_BG[1] + (TEAL_DARK[1] - DARK_BG[1]) * t)
        b = int(DARK_BG[2] + (TEAL_DARK[2] - DARK_BG[2]) * t)
        draw.line([(0, i), (size, i)], fill=(r, g, b))

    # Rounded rect for icon safe zone
    margin = size * 0.08
    draw.rounded_rectangle([margin, margin, size - margin, size - margin],
                           radius=size * 0.18, fill=(0, 0, 0, 0))

    # Draw icon
    draw_icon(draw, size / 2, size / 2, size * 0.72)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    filepath = OUTPUT_DIR / "MarkScene_icon_512.png"
    img.save(filepath, "PNG")
    return filepath


def create_feature_graphic():
    """Generate 1024x500 feature graphic PNG"""
    w, h = 1024, 500
    img = Image.new('RGBA', (w, h), DARK_BG)
    draw = ImageDraw.Draw(img)

    # Gradient background
    for i in range(h):
        t = i / h
        r = int(DARK_BG[0] + (TEAL_DARK[0] - DARK_BG[0]) * t * 0.6)
        g = int(DARK_BG[1] + (TEAL_DARK[1] - DARK_BG[1]) * t * 0.6)
        b = int(DARK_BG[2] + (TEAL_DARK[2] - DARK_BG[2]) * t * 0.6)
        draw.line([(0, i), (w, i)], fill=(r, g, b))

    # Icon on the left
    icon_x = 180
    icon_y = h // 2
    draw_icon(draw, icon_x, icon_y, 220)

    # Try to use a system font
    font_big = None
    font_small = None
    font_paths = [
        "C:/Windows/Fonts/malgun.ttf",
        "C:/Windows/Fonts/NotoSansCJKkr-Regular.otf",
        "C:/Windows/Fonts/arial.ttf",
    ]
    for fp in font_paths:
        if Path(fp).exists():
            try:
                font_big = ImageFont.truetype(fp, 64)
                font_small = ImageFont.truetype(fp, 28)
                break
            except:
                pass

    if font_big is None:
        font_big = ImageFont.load_default()
        font_small = ImageFont.load_default()

    # App name
    text_x = 380
    text_y = h // 2 - 60
    draw.text((text_x, text_y), "MarkScene", fill=NEAR_WHITE, font=font_big)

    # Tagline
    draw.text((text_x, text_y + 80), "내 하루의 장면을 검색 가능한 기억으로", fill=TEAL_LIGHT, font=font_small)

    # Subtitle
    font_xs = None
    try:
        font_xs = ImageFont.truetype(font_paths[0], 20)
    except:
        font_xs = ImageFont.load_default()
    draw.text((text_x, text_y + 120), "Scene Memory · Auto-Tagging · Recall Box · Local-First", fill=WHITE + (180,), font=font_xs)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    filepath = OUTPUT_DIR / "MarkScene_feature_graphic_1024x500.png"
    img.save(filepath, "PNG")
    return filepath


def create_phone_screenshot(name, bg_color, title_text, subtitle_text):
    """Generate a placeholder phone screenshot 1080x1920"""
    w, h = 1080, 1920
    img = Image.new('RGB', (w, h), bg_color)
    draw = ImageDraw.Draw(img)

    # Status bar
    draw.rectangle([0, 0, w, 80], fill=bg_color)

    # Try fonts
    font_title = None
    font_sub = None
    font_paths = [
        "C:/Windows/Fonts/malgun.ttf",
        "C:/Windows/Fonts/arial.ttf",
    ]
    for fp in font_paths:
        if Path(fp).exists():
            try:
                font_title = ImageFont.truetype(fp, 48)
                font_sub = ImageFont.truetype(fp, 28)
                break
            except:
                pass

    if font_title is None:
        font_title = ImageFont.load_default()
        font_sub = ImageFont.load_default()

    # Title bar
    draw.rectangle([0, 80, w, 160], fill=bg_color)
    draw.text((40, 100), title_text, fill=DARK_BG, font=font_title)

    # Content area indicator
    draw.rectangle([40, 200, w - 40, h - 100], outline=TEAL_LIGHT + (80,), width=3)
    draw.text((w // 2 - 150, h // 2 - 30), subtitle_text, fill=TEAL_DARK, font=font_sub)

    # Bottom nav bar
    draw.rectangle([0, h - 100, w, h], fill=WHITE)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    filepath = OUTPUT_DIR / f"MarkScene_screenshot_{name}.png"
    img.save(filepath, "PNG")
    return filepath


if __name__ == "__main__":
    print("Generating Play Store assets...")

    r1 = create_app_icon()
    print(f"  Icon: {r1}")

    r2 = create_feature_graphic()
    print(f"  Feature: {r2}")

    screenshots = [
        ("today", BG_LIGHT, "오늘의 장면", "Scene Timeline — 날짜별 타임라인"),
        ("create", BG_LIGHT, "새 기록", "Memory Tags + Recall 토글"),
        ("recall", BG_LIGHT, "Recall Box", "다시 볼 기록 모아보기"),
        ("search", BG_LIGHT, "검색", "태그 · 메모 · 기억 유형으로 검색"),
    ]
    for name, bg, title, sub in screenshots:
        r = create_phone_screenshot(name, bg, title, sub)
        print(f"  Screenshot: {r}")

    print(f"\nDone! All assets saved to {OUTPUT_DIR}.")
