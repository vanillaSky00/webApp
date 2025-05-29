import os
import requests
from PIL import Image
from io import BytesIO

output_dir = "input/tile_set"
os.makedirs(output_dir, exist_ok=True)

NUM_IMAGES = 2000

for i in range(NUM_IMAGES):
    try:
        url = f"https://picsum.photos/seed/{i}/400/400"
        response = requests.get(url, timeout=10)

        if response.status_code == 200 and 'image' in response.headers.get('Content-Type', ''):
            img = Image.open(BytesIO(response.content)).convert("RGB")
            save_path = os.path.join(output_dir, f"tile_{i}.jpg")
            img.save(save_path)
            print(f"tile_{i}.jpg downloaded")
        else:
            print(f"Invalid response for tile_{i}.jpg")
    except Exception as e:
        print(f"Failed tile_{i}.jpg: {e}")
