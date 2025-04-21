<template>
  <div class="upload-container">
    <h2>📤 Upload an Image</h2>
    <input
        type="file"
        accept="image/*"
        @change="handleFileChange"
        ref="fileInput"
    >
    <button @click="uploadImage" :disabled="!selectedFile">Upload</button>

    <div v-if="previewUrl" class="preview">
      <img :src="previewUrl" alt="Preview" style="max-width: 300px;">
    </div>

    <p :class="{'success': isSuccess, 'error': isError}">
      {{ message }}
    </p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedFile: null,
      previewUrl: '',
      message: '',
      isSuccess: false,
      isError: false
    };
  },
  methods: {
    handleFileChange(event) {
      this.selectedFile = event.target.files[0];
      this.previewUrl = URL.createObjectURL(this.selectedFile);
      this.message = '';
    },
    async uploadImage() {
      if (!this.selectedFile) return;

      const formData = new FormData();
      formData.append('image', this.selectedFile);

      try {
        const res = await fetch('http://localhost:8080/api/home/images/fileSystem', {
          method: 'POST',
          body: formData
        });

        if (!res.ok) throw new Error(await res.text());

        const data = await res.text();
        this.message = `✅ ${data}`;
        this.isSuccess = true;
        this.isError = false;

        // Reset form
        this.$refs.fileInput.value = '';
        this.selectedFile = null;
        this.previewUrl = '';

      } catch (err) {
        this.message = `❌ Error: ${err.message}`;//no idea the upload is success but get err
        this.isError = true;
        this.isSuccess = false;
        console.error(err);
      }
    }
  }
};
</script>

<style scoped>
.upload-container {
  border: 1px solid #ccc;
  padding: 1rem;
  max-width: 500px;
  margin: 2rem auto;
  text-align: center;
}
.preview {
  margin: 1rem 0;
}
.success {
  color: green;
}
.error {
  color: red;
}
button {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>