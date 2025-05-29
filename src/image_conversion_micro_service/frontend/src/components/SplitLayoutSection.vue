<template>
    <!-- 將 $attrs 綁定到根元素上，這樣父級傳入的 class 和 style 會被應用 -->
    <section v-bind="$attrs" class="split-layout-section" :class="{ 'reverse-layout': reverse }">
      <!-- 左側 (預設為圖片) -->
      <div class="split-column split-left">
        <!-- 使用 slot 允許傳入自訂內容 -->
        <slot name="left">
          <!-- 預設內容：如果沒有提供 slot="left" -->
          <img v-if="imageUrl" :src="imageUrl" :alt="imageAlt" class="featured-image">
          <div v-else class="image-placeholder">請提供圖片</div>
        </slot>
      </div>
  
      <!-- 右側 (預設為文字) -->
      <div class="split-column split-right">
        <!-- 使用 slot 允許傳入自訂內容 -->
        <slot name="right">
          <!-- 預設內容：如果沒有提供 slot="right" -->
          <h2 v-if="title" class="content-title">{{ title }}</h2>
          <p v-if="description" class="content-description">{{ description }}</p>
          <!-- 可以添加更多預設內容或 slot -->
          <slot name="right-extra"></slot> <!-- 額外內容的 slot -->
        </slot>
      </div>
    </section>
  </template>
  
  <script setup lang="ts">
  import { defineProps } from 'vue';
  
  // 定義元件可以接收的 Props
  defineProps({
    // 左側圖片的 URL (如果使用預設 slot)
    imageUrl: {
      type: String,
      default: ''
    },
    // 左側圖片的 alt 文字 (如果使用預設 slot)
    imageAlt: {
      type: String,
      default: '特色圖片'
    },
    // 右側標題 (如果使用預設 slot)
    title: {
      type: String,
      default: ''
    },
    // 右側描述 (如果使用預設 slot)
    description: {
      type: String,
      default: ''
    },
    // 是否反轉佈局 (變成右圖左文)
    reverse: {
      type: Boolean,
      default: false
    }
  });
  </script>
  
  <style scoped lang="scss">
  .split-layout-section {
    display: flex;
    // 在小螢幕上垂直排列，大螢幕上水平排列
    flex-direction: column; /* 預設垂直 */
    width: 90%; /* 預設佔滿父容器寬度 */
    height: 50vh;
    margin: 2rem auto; /* 上下外邊距 2 rem, 左右auto使居中*/
    background-color: #fff; /* 背景色 */
    border-radius: 10px; /* 輕微圓角 */
    overflow: hidden; /* 隱藏內部溢出 (例如圖片陰影) */
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* 輕微陰影 */
  
    @media (min-width: 768px) { /* 中等螢幕及以上 */
      flex-direction: row; /* 改為水平排列 */
    }
  
    // 反轉佈局的樣式
    &.reverse-layout {
      @media (min-width: 768px) {
        flex-direction: row-reverse; /* 水平反轉 */
      }
    }
  }
  
  /* 左右兩列的通用樣式 */
  .split-column {
    flex: 1; /* 預設平均分配空間 */
    padding: clamp(1.5rem, 4vw, 3rem); /* 響應式內邊距 */
    box-sizing: border-box;
  
    @media (min-width: 768px) {
      flex-basis: 50%; /* 水平排列時各佔 50% */
      flex-grow: 0;
      flex-shrink: 0;
    }
  }
  
  /* 左側 (圖片預設) */
  .split-left {
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f8f9fa; /* 圖片區域淺灰色背景 */
  
    // 如果是反轉佈局，讓圖片在右邊時也有背景色
    .reverse-layout & {
       @media (min-width: 768px) {
          // 如果反轉了，這實際是右邊，可能不需要背景色
          // background-color: transparent;
       }
    }
  }
  
  /* 右側 (文字預設) */
  .split-right {
    display: flex;
    flex-direction: column;
    justify-content: center; /* 垂直居中文本內容 */
  
    // 如果是反轉佈局，讓文字在左邊時也有樣式
     .reverse-layout & {
       @media (min-width: 768px) {
          // 如果反轉了，這實際是左邊
       }
    }
  }
  
  /* 預設圖片樣式 */
  .featured-image {
    display: block;
    max-width: 100%;
    height: auto;
    max-height: 400px; /* 限制圖片最大高度 */
    object-fit: cover; /* 覆蓋區域，可能會裁剪 */
    border-radius: 6px;
  }
  .image-placeholder {
      color: #888;
      font-style: italic;
  }
  
  /* 預設文字樣式 */
  .content-title {
    font-size: clamp(1.4rem, 3vw, 2.2rem);
    margin-bottom: 0.8rem;
    color: #212529;
    line-height: 1.3;
    font-weight: 600;
  }
  
  .content-description {
    font-size: clamp(0.9rem, 1.8vw, 1.05rem);
    color: #495057;
    line-height: 1.7;
  }
  
  /* 你可以在父元件中為 slot 傳入的內容定義樣式 */
  /* 例如： */
  /*
  :deep(.custom-button) {
    margin-top: 1.5rem;
    background-color: blue;
    color: white;
  }
  */
  </style>