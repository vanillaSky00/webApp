import os
from PIL import Image
import re

# Natural sort key function (e.g. tile_2 < tile_10)
def natural_sort_key(filename):
    return [int(text) if text.isdigit() else text.lower()
            for text in re.split(r'(\d+)', filename)]

# Resize all valid image files from input_dir to output_dir
def compress_images(input_dir, output_dir, size=(10, 10), expected=2000):
    os.makedirs(output_dir, exist_ok=True)
    count = 0

    # Only process valid image extensions
    image_extensions = {".jpg", ".jpeg", ".png", ".bmp"}

    # Filter + sort filenames naturally
    filenames = [
        f for f in sorted(os.listdir(input_dir), key=natural_sort_key)
        if os.path.splitext(f)[1].lower() in image_extensions
    ]

    for filename in filenames:
        input_path = os.path.join(input_dir, filename)
        output_path = os.path.join(output_dir, f"tile_{count}.jpg")

        try:
            with Image.open(input_path) as img:
                img = img.convert("RGB")
                img = img.resize(size)
                img.save(output_path)
                print(f"{filename} → tile_{count}.jpg")
                count += 1
        except Exception as e:
            print(f"Failed to process {filename}: {e}")

    print(f"\nTotal {count} images compressed.")
    if count < expected:
        print(f"Warning: expected {expected}, but only {count} were processed. Missing {expected - count}.")

if __name__ == "__main__":
    compress_images("input/tile_set", "input/compressed_tile", size=(10, 10), expected=2000)