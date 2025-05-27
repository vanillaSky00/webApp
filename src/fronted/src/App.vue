<template>
  <div class="header">
    <ConnectWallet />
  </div>

  <div class="main">
    <ImageUpload />
  </div>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: flex-end;
  padding: 1rem;
}
</style>

<script setup>
import ImageUpload from './components/ImageUpload.vue'
import ConnectWallet from './components/ConnectWallet.vue'
import ImageUploadButton from './components/processAndDownloadImage.vue'
</script>
