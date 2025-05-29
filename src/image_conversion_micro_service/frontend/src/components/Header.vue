<template>
  <!--
    @file Header.vue
    @description 應用程序的全局頭部組件。
    該組件負責展示網站 Logo、主導航菜單，並處理導航邏輯。
    它使用了粘性定位 (sticky positioning) 使頭部在頁面滾動時保持在頂部可見。
    通過引入並使用 HeaderNav 子組件來渲染導航項。
  -->
  <header class="app-header">
    <!-- Logo 區域 -->
    <div class="logo">
      <!-- 網站 Logo 圖片 -->
      <img src="../assets/image/logo.png" alt="網站 Logo" class="logo-img" />
      <!-- 網站標題文本 -->
      <h1 class="logo-text">mosaic</h1>
    </div>

    <!-- 
      主導航菜單 (HeaderNav 子組件)
      - :styleList: 綁定 navigationItems 數據，傳遞給子組件以渲染導航鏈接。
      - :activeName: 綁定 currentActiveNav 狀態，用於高亮顯示當前活動的導航項。
      - @click: 監聽子組件發出的 'click' 事件，當用戶點擊導航項時觸發 handleNavigation 方法。
    -->
    <HeaderNav
      :styleList="navigationItems"
      :activeName="currentActiveNav"
      @click="handleNavigation"
    />

    <!-- 可選的用戶操作區域 (當前註釋掉) -->
    <!-- <div class="user-actions">
      <span>用戶操作區</span>
    </div> -->

    <!-- (僅為演示目的，顯示當前活動項目名稱，可移除) -->
    <!-- <div style="clear: both; padding-top: 10px; color: #666;">
      當前活動項目: {{ currentActiveNav }}
    </div> -->
  </header>
</template>

<script setup lang="ts">
/**
 * @file Header.vue <script setup lang="ts">
 * @description Header 組件的 TypeScript 邏輯部分。
 * 職責：
 * 1. 引入並註冊 HeaderNav 子組件。
 * 2. 引入 Vue Router 的 useRouter 實例用於編程式導航。
 * 3. 定義導航項目列表 (`navigationItems`)，包含名稱和路徑。
 * 4. 管理當前活動的導航項目狀態 (`currentActiveNav`)。
 * 5. 提供處理導航點擊事件的方法 (`handleNavigation`)，該方法會更新活動狀態並觸發路由跳轉。
 */

import { ref } from 'vue';
import HeaderNav from './HeaderNav.vue'; // 引入 HeaderNav 子組件
import { useRouter } from 'vue-router'; // 引入 Vue Router 的 useRouter

// --- 響應式狀態 (State) ---

/**
 * @typedef {object} NavigationItem
 * @property {string} name - 導航項目的顯示名稱。
 * @property {string} path - 導航項目對應的路由路徑。
 */

/**
 * @type {import('vue').Ref<NavigationItem[]>}
 * @description 導航項目的數據列表。每個對象包含 `name` (顯示文本) 和 `path` (路由路徑)。
 * 此列表將作為 prop 傳遞給 `HeaderNav` 子組件。
 */
const navigationItems = ref([
  { name: 'Guide', path: '/' },
  { name: 'Use now', path: '/usenow' },
  { name: 'NFT', path: '/hot-songs' }, // 假設 '/hot-songs' 是 NFT 相關的路徑
]);

/**
 * @type {import('vue').Ref<string>}
 * @description 當前被激活的導航項目的名稱。
 * 用於控制 `HeaderNav` 子組件中哪個項目應高亮顯示。
 * 初始值通常是列表中的第一個項目名稱或一個預設的名稱。
 */
const currentActiveNav = ref(navigationItems.value[0]?.name || '首頁');

/**
 * @const {import('vue-router').Router} router
 * @description Vue Router 的實例，用於執行編程式導航。
 */
const router = useRouter();

// --- 方法 (Methods) ---

/**
 * @function handleNavigation
 * @description 處理從 `HeaderNav` 子組件發出的 `click` 事件的回調函數。
 * 當用戶點擊導航欄中的某個項目時被調用。
 * @param {string} name - 被點擊的導航項目的 `name` 屬性值。
 * @param {string} path - 被點擊的導航項目的 `path` 屬性值。
 * @returns {void}
 * 行為：
 * 1. 更新 `currentActiveNav` 的值為被點擊項目的 `name`，這會觸發 `HeaderNav` 組件更新高亮狀態。
 * 2. 使用 Vue Router 的 `router.push(path)` 方法執行頁面導航到指定的 `path`。
 */
function handleNavigation(name: string, path: string) {
  console.log(`Header 元件收到點擊事件: Name = ${name}, Path = ${path}`);

  // 1. 更新當前活動項目的狀態
  currentActiveNav.value = name;

  // 2. 執行頁面導航
  router.push(path);
}

</script>

<style scoped>
/* 
  `scoped` 樣式塊，確保樣式僅作用於此 Header 組件。
  定義了頭部容器的佈局（Flexbox）、粘性定位、背景、邊框和陰影。
  同時也定義了 Logo 區域（包括圖片和文本）的樣式。
  使用 :deep() 選擇器來影響子組件 HeaderNav 的根元素樣式，使其能夠在 Flex 容器中伸展。
*/

.app-header {
  /* --- 粘性定位 (Sticky Positioning) --- */
  position: sticky;   /* 設置為粘性定位，使其在滾動時固定在頂部 */
  top: 0;             /* 當元素滾動到距離視口頂部 0px 時，觸發固定效果 */
  z-index: 1000;      /* 確保頭部在頁面其他內容之上 (數值可根據需要調整) */
  
  display: flex;        /* 使用 Flexbox 進行內部元素佈局 */
  align-items: center;  /* 垂直居中 Flex 子項 (Logo, HeaderNav) */
  padding: 0 25px;      /* 左右內邊距，為內容提供呼吸空間 (原為20px) */
  height: 70px;         /* 固定頭部高度 */

  background-color: #ffffff; /* 背景色，通常為白色或淺色 */
  border-bottom: 1px solid #e9ecef; /* 底部邊框，用於與下方內容分隔 (原為#e0e0e0) */
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.06); /* 輕微的底部陰影，增加層次感 (原為0.05) */
}

.logo {
  display: flex; /* 使 logo 圖片和文字在同一行 */
  align-items: center; /* 垂直居中 logo 內部元素 */
  margin-right: auto; /* 將 logo 推到最左邊，並讓導航菜單佔據剩餘空間 (原為40px固定間距) */
  /* font-size: 1.5rem; /* logo 文本的基礎字體大小，已被 .logo-text 覆蓋 */
  /* font-weight: bold; */
  /* color: #333; */
  flex-shrink: 0; /* 防止 logo 在空間不足時被壓縮 */
}

.logo-img {
  /* 
    Logo 圖片的樣式。
    使用絕對定位可以將圖片精確放置，甚至部分移出其直接父容器 (.logo) 的邊界。
    注意：如果使用絕對定位，其父容器 (.logo) 或更上層的 .app-header 
    應該有一個非 static 的 position (如 relative, absolute, fixed, sticky) 作為定位參考。
    當前 .logo 採用 flex 佈局，圖片作為 flex item，絕對定位可能不是最佳選擇，
    除非有特殊視覺需求。如果只是簡單展示，保持默認的流式佈局可能更簡單。
    此處保留了絕對定位的示例，但實際應用中需謹慎評估。
  */
  position: absolute; /* 相對於最近的已定位祖先元素定位 */
  width: 120px;    /* Logo 圖片寬度 */
  height: auto;    /* 高度自適應以保持圖片原始比例 */
  max-width: none; /* 移除最大寬度限制，允許圖片按設定寬度顯示 */
  top: -20px;      /* 向上偏移 20px (相對於定位父級) */
  left: 0px;       /* 向左偏移 0px (相對於定位父級) */
  z-index: 10;     /* 確保 Logo 圖片在其他 Header 內容之上 (如果發生重疊) */
}

.logo-text {
  /* 
    Logo 文本 (例如 "mosaic") 的樣式。
    padding-left 用於在圖片和文本之間創建間距 (假設圖片在左側)。
    margin-top 用於微調文本的垂直位置。
  */
  padding-left: 80px; /* 根據 logo-img 的寬度和期望間距調整 */
  margin-top: 10px;   /* 微調垂直對齊 */
  color: #5a6a7d;   /* Logo 文本顏色 (原為#848484) */
  font-size: 1.8rem;  /* Logo 文本字體大小 (新增) */
  font-weight: 600;   /* Logo 文本字重 (新增) */
  white-space: nowrap; /* 防止文本換行 (新增) */
}

/* 
  使用 :deep() 偽類來選擇 HeaderNav 子組件的根元素 (通常是 .header-nav)。
  使其在 Flex 容器中能夠伸展並填充可用的水平空間。
*/
.app-header > :deep(.header-nav) { 
  flex-grow: 1; /* 允許導航區域填滿剩餘空間 */
  margin-left: 40px; /* 在 Logo 和導航之間添加一些間距 */
  display: flex; /* 確保 HeaderNav 內部也是 flex 容器以便對齊 */
  justify-content: flex-start; /* 或 center, flex-end，根據設計調整導航項的對齊方式 */
}

/* 
  用戶操作區域的樣式 (如果啟用)。
  margin-left: auto; 將其推到 Flex 容器的最右側。
.user-actions {
  margin-left: auto; 
  color: #555;
  display: flex;
  align-items: center;
} 
*/

/* 
  (僅為演示目的) 清除浮動和演示文字的樣式。
  在實際生產中應移除。
div[style*="clear: both"] {
    background-color: #f0f0f0;
    padding: 10px;
    margin-top: 10px;
    border-radius: 4px;
}
*/
</style>