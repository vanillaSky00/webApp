<template>
  <div class="final-image-area">
    <h2 class="section-title">生成結果</h2>
    <img :src="imageUrl" alt="最終生成的馬賽克圖片" class="final-image">
    <a :href="imageUrl" :download="downloadFileName" class="download-button">
      下載馬賽克圖片
    </a>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  imageUrl: string;
  downloadFileName: string;
}>();
</script>

<style scoped lang="scss">
/* 複製 UseNowView.vue 中與 final-image-area 相關的樣式 */
.section-title { /* Duplicated for standalone presentation, can be inherited if within a similar structure */
  font-size: 1.6rem;
  color: #34495e;
  margin-top: 0;
  margin-bottom: 1rem;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 0.75rem;
  font-weight: 500;
}

.final-image-area {
  margin-top: 2.5rem;
  text-align: center;
  padding: 25px;
  background-color: #ffffff;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);

  .final-image {
    display: block;
    max-width: 100%;
    max-height: 550px;
    border: 1px solid #dcdcdc;
    border-radius: 6px;
    margin: 0 auto 2rem auto;
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  }
}

.download-button {
  display: inline-block;
  padding: 12px 28px;
  font-size: 1.05rem;
  font-weight: 500;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  text-decoration: none;
  user-select: none;
  background-color: #1abc9c; /* 青綠色 */
  min-width: 200px;

   &:hover {
    background-color: #16a085; /* 深一點的青綠色 */
    box-shadow: 0 3px 8px rgba(0,0,0,0.1);
    transform: translateY(-1px);
  }
  &:active {
    transform: translateY(0px);
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  }
}
</style>