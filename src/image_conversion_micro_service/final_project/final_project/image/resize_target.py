from PIL import Image
import os

input_path = "input/test.jpg"
output_path = "input/test_resized.jpg"

img = Image.open(input_path)
resized = img.resize((600, 480))
resized.save(output_path)