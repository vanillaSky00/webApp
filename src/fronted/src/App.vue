<template>
  <div class="header">
    <ConnectWallet />
  </div>

  <div class="main">
    <ImageUpload />
  </div>

  <div class="main">
    <CompressAndDownloadImage />
  </div>

  <div class="main">
    <DecompressAndDownloadImage />
  </div>

  <div class="main">
    <MosaicAndDownloadImage />
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
import CompressAndDownloadImage from './components/CompressAndDownloadImage.vue'
import DecompressAndDownloadImage from "@/components/DecompressAndDownloadImage.vue";
import MosaicAndDownloadImage from "@/components/MosaicAndDownloadImage.vue";
</script>
