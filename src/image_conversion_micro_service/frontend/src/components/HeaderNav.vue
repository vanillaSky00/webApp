<template>
  <!--
    @file HeaderNav.vue
    @description 一個可重用的導航菜單組件。
    該組件負責根據傳入的 `styleList` prop 動態渲染一個導航項目列表 (<ul><li>)。
    它能夠高亮顯示當前活動的導航項 (基於 `activeName` prop)，
    並在用戶點擊某個導航項時，通過 `click` 事件通知父組件。

    模板註釋（保留原始口語化解釋）：
    👉 為什麼用 <ul>？
    用 <ul> 是因為我們要畫出一個「選單列」，每個選項都是列表（像：熱門、新歌、排行榜），用 <li> 搭配 <ul> 是最常見的 HTML 寫法，也方便控制樣式。
    👉 為什麼用 :class 加條件？
    這是 Vue 的動態 class 語法。意思是：如果這個項目的名字是"目前選中的"，就套用 active 樣式。這樣就可以在畫面上「高亮選中項目」。
    👉 為什麼用 v-for？
    你不可能一行行手寫每個選單吧？用 v-for 可以根據傳進來的 styleList 陣列"動態"產生幾個 <li>，這樣元件才通用、彈性。
    👉 為什麼要加 :key？
    Vue 在更新畫面時會比較每個元素。key 是「身分證」，可以讓 Vue 更快、更正確地比對資料，避免渲染錯誤或效率低。
    👉 為什麼不用直接寫 $emit？ (此處指 handleChangeView 函數)
    把功能抽出來寫成 handleChangeView() 是好習慣，這樣可讀性好、好維護，且之後功能改變（例如記錄點擊次數）也好加東西。
    👉 為什麼只顯示 name？
    styleList 的每個元素應該長這樣 { name: '熱門', path: '/hot' }，你只要顯示名字即可，路徑是點擊後內部使用的資料。
  -->
  <ul class="header-nav">
    <!--
      使用 v-for 指令遍歷 styleList prop 中的每個導航項目。
      - :key: 為每個列表項提供唯一的鍵 (item.path)，有助於 Vue 高效更新 DOM。
      - :class: 動態綁定 class。如果當前項目的 name 與 activeName prop 匹配，則添加 'active' class。
      - @click: 監聽點擊事件，觸發 handleChangeView 方法，並傳遞當前點擊的項目。
    -->
    <li
      :class="{ active: item.name === activeName }"
      v-for="item in styleList"
      :key="item.path"
      @click="handleChangeView(item)"
    >
      {{ item.name }} <!-- 僅顯示導航項目的名稱 -->
    </li>
  </ul>
</template>

<script setup lang="ts">
/**
 * @file HeaderNav.vue <script setup lang="ts">
 * @description HeaderNav 組件的 TypeScript 邏輯部分。
 * 職責：
 * 1. 定義組件名稱為 'HeaderNav' (通過 `defineOptions`)。
 * 2. 定義組件接收的 props (`styleList` 和 `activeName`) 及其類型 (通過 `defineProps`)。
 *    - `styleList`: 一個包含導航項目對象的數組，每個對象應有 `name` (顯示文本) 和 `path` (路由路徑)。
 *    - `activeName`: 一個字符串，表示當前應高亮顯示的導航項目的名稱。
 * 3. 定義組件可以發出的事件 (`click`) 及其 payload 類型 (通過 `defineEmits`)。
 *    - `click` 事件：當用戶點擊導航項時觸發，攜帶被點擊項目的 `name` 和 `path`。
 * 4. 提供處理導航項點擊的方法 (`handleChangeView`)，該方法會觸發 `click` 事件。
 *
 * 原始口語化註釋：
 * 你寫的每一個變數與函式都會「被 Vue 看到」，不需要顯式回傳給模板使用。這讓開發者能專注在邏輯本身，而不是框架語法。
 */

import { defineProps, defineEmits, defineOptions } from 'vue';

/**
 * @typedef {object} NavigationItem
 * @property {string} name - 導航項目的顯示名稱。
 * @property {string} path - 導航項目對應的路由路徑。
 */

/**
 * @typedef {object} Props
 * @property {NavigationItem[]} styleList - 導航項目列表。父組件通過此 prop 提供菜單數據。
 *                                          (原始口語化: styleList 表示你希望元件由外部提供選單項目，而不是寫死在內部。)
 * @property {string} activeName - 當前活動的導航項目的名稱。用於高亮顯示。
 */
// 告訴 Vue「我會從父元件接收到這些資料」。
// 父元件是什麼？簡單講就是「誰用你，你的爸爸就是誰」。
const props = defineProps<{
  styleList: Array<{ name: string, path: string }>,
  activeName: string
}>();

/**
 * @typedef {object} Emits
 * @property {(event: 'click', name: string, path: string) => void} click - 當導航項被點擊時觸發。
 *                                                                         (原始口語化: 當你希望子元件「通知」父元件一些事件（例如按了哪個選項），你就會用到 emit。)
 *                                                                         (原始口語化TS解釋: TypeScript 中對函式型別的描述，它表明「這是一個函式，接收三個參數，回傳值是 void（也就是沒有回傳值）」)
 */
const emit = defineEmits<{
  (e: 'click', name: string, path: string): void
}>();

/**
 * @function defineOptions
 * @description 設置組件的選項，此處用於給組件命名為 'HeaderNav'。
 * 這有助於在 Vue Devtools 中識別組件，以及在遞歸組件或模板引用時使用。
 */
defineOptions({ name: 'HeaderNav' }); // 👈 給元件命名

/**
 * @function handleChangeView
 * @description 處理導航項目點擊事件的內部方法。
 * 當用戶點擊列表中的某個 `<li>` 元素時被調用。
 * 它的主要作用是將點擊事件和相關數據 (項目名稱和路徑) 向上冒泡給父組件。
 * (原始口語化: 這個函式其實就是事件橋樑，幫你把點選的內容回報給上層元件使用。)
 * @param {NavigationItem} item - 被點擊的導航項目對象，包含 `name` 和 `path` 屬性。
 * @returns {void}
 */
function handleChangeView(item: { name: string, path: string }) {
  emit('click', item.name, item.path);
}
</script>

<style lang="scss" scoped>
/*
  SCSS 樣式塊，`scoped` 屬性確保樣式僅作用於此 HeaderNav 組件。
  使用了外部 SCSS 變量文件 (`@import "../assets/css/var.scss";`)。
  定義了導航列表 (ul.header-nav) 和列表項 (li) 的佈局、外觀、交互效果 (hover, active)。
*/

@import "../assets/css/var.scss"; // 導入 SCSS 變量

/*
  .header-nav (ul 元素) 的樣式。
  - display: flex: 使用 Flexbox 佈局使導航項水平排列。
  - list-style: none: 移除列表項的默認項目符號 (小圓點)。
  - justify-content: space-around: Flexbox 屬性，使導航項在容器內均勻分佈，並在兩端留有空間。
  - padding 和 margin: 調整內外邊距。
  - font-size: 設置導航項的基礎字體大小。
  (原始口語化: 這種排版不是靠 list 控制, 要找他的容器)
*/
.header-nav {
  display: flex;
  list-style: none;
  justify-content: space-around; /* 或 flex-start, center, space-between 根據設計需求 */
  padding-top: 25px; /* 垂直定位的輔助 */
  padding-left: 20vw; /* 根據父級佈局調整，可能需要更靈活的方式 */
  padding-right: 10vw; /* 同上 */
  margin: 0;
  font-size: 30px; /* 基礎字體大小，會被 li 中的 font-size 覆蓋 */
  align-items: center; /* 新增：使 li 在交叉軸上居中，如果 li 高度不一致時有用 */
  height: 100%; /* 新增：使其填滿父級高度，以便 align-items 生效 */
}

/*
  li (列表項) 元素的樣式。
  - margin 和 padding: 調整間距。
  - line-height: 影響文本的行高，也間接影響 li 的高度。
  - color: 文本顏色。
  - font-size: 列表項文本的具體字體大小。
  - border-bottom: 活動狀態的下劃線基礎 (初始為 none)。
  - cursor: pointer: 鼠標懸停時顯示指針手勢。
  - transition: 為顏色、邊框、transform 等屬性變化添加平滑過渡動畫。
    (原始口語化: 👉 增加滑鼠移動時的動畫過渡效果)
*/
li {
  margin: $header-nav-margin; /* 使用 SCSS 變量 */
  padding: $header-nav-padding; /* 使用 SCSS 變量 */
  line-height: 3.3rem; /* 根據設計調整，確保文本垂直居中 */
  color: $color-grey;   /* 使用 SCSS 變量 */
  font-size: 20px;
  border-bottom: 5px solid transparent; /* 預留空間給 active 和 hover 時的邊框，避免跳動 */
  cursor: pointer;
  transition: color 0.2s ease, border-color 0.2s ease, transform 0.2s ease; /* 分別定義過渡 */
  white-space: nowrap; /* 防止導航項文字換行 */
}

/*
  活動狀態 (li.active) 的樣式。
  - color: 更改文本顏色以高亮。
  - font-weight: 加粗文本。
  - border-bottom: 添加實色下劃線以標識活動項。
*/
li.active {
  color: $color-black;       /* 使用 SCSS 變量 */
  font-weight: 600;
  border-bottom-color: $color-black; /* 改變預留的透明邊框顏色 */
}

/*
  鼠標懸停狀態 (li:hover) 的樣式。
  - color: 更改文本顏色。
  - border-bottom: 添加實色下劃線。
  - font-weight: 調整字重。
  - transform: translateY(-2px): 向上輕微移動，產生浮起效果。
    (原始口語化: 👉 滑鼠移上時加底線)
    (原始口語化: 👉 可選：有種「浮起來」的感覺)
*/
li:hover {
  color: $color-black; /* 或者一個特定的 hover 顏色 */
  border-bottom-color: $color-black; /* 改變預留的透明邊框顏色 */
  font-weight: 500; /* 可以與 active 狀態的字重有所區別 */
  transform: translateY(-2px);
}
</style>