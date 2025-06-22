<!--版面配置-->
<template>
  <!-- 
    @file App.vue
    @description 應用的根組件，負責整體佈局和路由視圖的渲染。
    它包含一個頭部 (Header) 和一個主要內容區域，主要內容區域通過 <router-view> 動態顯示當前路由對應的組件。
    同時，此組件也負責初始化和管理全局的滾動觀察器 (IntersectionObserver) 以實現元素的「按需動畫」效果。
  -->
  <div class="app">
    <!-- 應用頭部組件 -->
    <Header />
    <!-- 主要內容區域，用於渲染路由組件 -->
    <main class="main-content">
      <!-- 
        Vue Router 的出口。
        v-slot="{ Component }" 是一種作用域插槽的語法，用於獲取當前路由匹配到的組件實例。
        <component :is="Component"></component> 用於動態渲染這個組件。
        這樣可以配合 <transition> 或 <keep-alive> 等 Vue 功能使用。
      -->
      <router-view v-slot="{Component}">
        <component :is="Component"></component>
      </router-view>
    </main>
  </div>
</template>

<script setup>
/**
 * @file App.vue <script setup>
 * @description App 組件的邏輯部分。
 * 主要職責：
 * 1. 引入並使用 Header 組件。
 * 2. 初始化和管理全局的 IntersectionObserver 實例 (`initScrollObserver`, `observeElements`, `disconnectObserver`)：
 *    - 在組件掛載 (onMounted) 時初始化觀察器，並對初始頁面上的元素進行觀察。
 *    - 監聽路由變化 (`router.afterEach`)，在每次路由切換完成後，重新初始化觀察器並觀察新頁面上的元素。
 *    - 在組件卸載 (onUnmounted) 時斷開並清理觀察器，以防止內存洩漏。
 * 3. 使用 `nextTick` 確保在 DOM 更新完成後才執行觀察操作，這對於異步組件或路由切換後的內容加載尤為重要。
 */
import Header from './components/Header.vue';

import { onMounted, onUnmounted, nextTick } from 'vue';
import router from './router'; // 引入 Vue Router 實例
import { 
  initScrollObserver, 
  observeElements, 
  disconnectObserver 
} from './utils/intersectionObserver'; // 引入滾動觀察相關工具函數

/**
 * @function onMounted
 * @description Vue 組件生命週期鉤子：當組件被掛載到 DOM 後執行。
 * 行為：
 * 1. 調用 `initScrollObserver()` 初始化全局滾動觀察器。
 * 2. 使用 `nextTick()` 確保在初始 DOM 渲染完成後：
 *    a. (可選) 斷開可能已存在的觀察器實例 (此處的 `if (observer) observer.disconnect();` 假設 `observer` 是可訪問的，但根據 `intersectionObserver.js` 的實現，它並非直接導出，這行可能需要調整或依賴 `disconnectObserver()` 的行為)。
 *    b. 重新調用 `initScrollObserver()` 確保觀察器已正確初始化 (如果 `disconnectObserver` 會將其設為 null)。
 *    c. 調用 `observeElements()` 開始觀察當前頁面中符合條件的元素。
 * 3. 註冊一個全局的路由後置守衛 (`router.afterEach`)：
 *    - 每當路由導航成功完成後執行。
 *    - 使用 `nextTick()` 確保新路由對應的組件 DOM 渲染完成後：
 *      i.  打印調試信息。
 *      ii. 調用 `disconnectObserver()` 清理上一個頁面的觀察（避免觀察已不存在的元素）。
 *      iii.調用 `initScrollObserver()` 重新初始化觀察器（因為 `disconnectObserver` 可能會將其設為 null）。
 *      iv. 調用 `observeElements()` 觀察新頁面上符合條件的元素。
 */
onMounted(() => {
  initScrollObserver(); // 確保觀察器模塊內的 observer 實例被創建

  // 初始頁面加載後觀察一次
  nextTick(() => {
    // 註：根據 intersectionObserver.js 的實現，observer 不是直接導出的
    // disconnectObserver() 和 initScrollObserver() 內部會處理 observer 實例的創建和銷毀
    // 所以這裡的 if (observer) observer.disconnect(); 比較冗餘，可以簡化
    disconnectObserver(); // 確保清理任何之前的狀態（如果有的話）
    initScrollObserver(); // 確保有一個乾淨的觀察器實例
    observeElements();    // 觀察初始頁面元素
    console.log("App.vue mounted, initial elements observed.");
  });

  // 監聽路由變化，在新頁面加載後重新觀察
  router.afterEach((to, from) => {
    // 確保在 DOM 更新後再觀察，特別是對於異步組件
    nextTick(() => {
      console.log(`路由從 ${from.path} 切換到 ${to.path}，重新觀察元素。`); // 調試信息
      disconnectObserver(); // 先斷開舊的觀察
      initScrollObserver(); // 重新初始化觀察器
      observeElements();    // 觀察新頁面上的元素
    });
  });
});

/**
 * @function onUnmounted
 * @description Vue 組件生命週期鉤子：當組件實例被卸載，並且其 DOM 元素從頁面上移除後執行。
 * 行為：
 * 1. 調用 `disconnectObserver()` 來停止所有觀察活動並清理 IntersectionObserver 實例，防止內存洩漏。
 */
onUnmounted(() => {
  disconnectObserver();
  console.log("App.vue unmounted, observer disconnected.");
});
</script>


<!--用來 讓這個 .vue 檔案內的 CSS 樣式「只作用在這個元件內」-->
<style scoped> 
/* 
  `scoped` 屬性會讓 Vue Loader (或類似工具) 處理這裡的 CSS，
  為每個 CSS 規則添加一個獨特的屬性選擇器 (例如 [data-v-xxxxxxx])，
  從而確保這些樣式只應用於當前組件的模板中的元素，不會洩漏到子組件或父組件。
*/

.app {
  display: flex;
  /* 
    使 .app 元素高度等於視口高度。注意: 這裡用 vw (viewport width) 來設置 height，
    通常 height 會用 vh (viewport height)。如果意圖是佔滿視口高度，應為 height: 100vh;
    如果意圖是正方形且基於視口寬度，則 width: 100vw; height: 100vw; 是正確的。
    但考慮到 flex-direction: column，100vh 作為高度可能更常見。
  */
  height: 100vh; /* 假設意圖是佔滿視口高度 */
  width: 100vw; /* 視口寬度的百分之百 */
  flex-direction: column; /* 主軸垂直排列子元素 (Header 和 main-content) */
  /*border: 2px solid red;*/ /* 用於調試佈局的邊框標記，生產時應移除 */
}

/* .main-content 如果需要特定樣式可以加在這裡，例如 flex-grow: 1; 讓它佔滿剩餘空間 */
/*
.main-content {
  flex-grow: 1;
  overflow-y: auto; // 如果內容可能超出，允許垂直滾動
}
*/
</style>


<style>
/*
  沒有 `scoped` 屬性的 <style> 標籤中的樣式是全局的。
  它們會影響整個應用程序中的所有元素，類似於在一個普通的 .css 文件中定義樣式。
  通常用於設置基礎樣式、重置瀏覽器默認樣式或定義全局主題。
*/

/* 基礎 HTML 和 body 樣式重置，確保它們佔滿整個可用空間並且沒有默認的邊距或內邊距。 */
html, body, #app { /* #app 通常是 Vue 應用掛載的根元素 ID */
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  /* overflow: hidden; // 如果不希望根級別出現滾動條，可以考慮添加，但需謹慎 */
}
</style>