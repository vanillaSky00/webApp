<template>
  <div class="image-gallery">
    <h2>📸 Image Gallery</h2>

    <input type="file" @change="onFileChange" />
    <button @click="uploadImage" :disabled="!selectedFile">Upload</button>
    <p v-if="uploadMessage">{{ uploadMessage }}</p>

    <div class="gallery">
      <div v-for="img in images" :key="img" class="image-card">
        <img :src="`${baseUrl}/${img}`" :alt="img" />
        <p>{{ img }}</p>
        <a :href="`${baseUrl}/${img}`" download>⬇️ Download</a>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ImageGallery',
  data() {
    return {
      selectedFile: null,
      uploadMessage: '',
      images: [],
      baseUrl: 'http://localhost:8080/api/home/images/fileSystem', // download URL
    };
  },
  methods: {
    onFileChange(e) {
      this.selectedFile = e.target.files[0];
    },
    async uploadImage() {
      const formData = new FormData();
      formData.append('image', this.selectedFile);

      try {
        await axios.post('http://localhost:8080/api/home/images/fileSystem', formData);
        this.uploadMessage = '✅ Uploaded!';
        this.fetchImages(); // refresh list
      } catch (err) {
        this.uploadMessage = '❌ Upload failed: ' + err.message;
      }
    },
    async fetchImages() {
      try {
        const res = await axios.get('http://localhost:8080/api/home/images/fileSystem/list');
        this.images = res.data; // assume it's a list of filenames
      } catch (err) {
        console.error('Failed to load images', err);
      }
    },
  },
  mounted() {
    this.fetchImages();
  },
};
</script>

<style scoped>
.image-gallery {
  padding: 20px;
}
.gallery {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
.image-card {
  border: 1px solid #ddd;
  padding: 10px;
  width: 180px;
  text-align: center;
}
.image-card img {
  max-width: 100%;
  height: auto;
}
</style>
