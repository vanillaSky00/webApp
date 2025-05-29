// src/utils/intersectionObserver.js

/**
 * @fileoverview
 * 這是一個小工具，用來偵測網頁上的某些東西（比如圖片或區塊）是不是滾動進入了用戶的螢幕畫面。
 * 一旦元素出現，它會給元素加上一個叫做 'is-visible' 的 CSS class (樣式標記)，
 * 然後就不再重複偵測這個元素了，這樣可以確保一些效果（例如動畫）只觸發一次。
 *
 * @module intersectionObserver
 * @see 想知道更多技術細節，可以看這裡：https://developer.mozilla.org/zh-CN/docs/Web/API/Intersection_Observer_API
 *
 * @section 運行順序總覽 (當元素被觸發時)
 * 1.  **初始設置階段 (通常在頁面/組件加載時)**：
 *     a. 你的代碼調用 `observeElements('.some-selector')`。
 *     b. `observeElements` 內部：
 *        i.  如果「觀察員」(`observer`) 還沒啟動，會先調用 `initScrollObserver()`。
 *        ii. `initScrollObserver` 創建一個 `IntersectionObserver` 實例 (我們的「觀察員」)，並把 `observerCallback` 函數「註冊」給它，告訴它：「嘿，觀察員，如果你看到什麼東西了，就去執行 `observerCallback` 裡面的動作。」
 *        iii.回到 `observeElements`，它會找到所有符合 `.some-selector` 的網頁元素。
 *        iv. 對於找到的每一個元素，調用 `observer.observe(element)`，這等於是告訴「觀察員」：「開始盯著這個特定的元素！」
 *
 * 2.  **元素進入視口觸發階段 (用戶滾動頁面時)**：
 *     a. 當用戶滾動頁面，某個被「觀察員」盯著的元素符合了 `observerOptions` 裡設定的條件（例如，元素露出了 10%）。
 *     b. **瀏覽器內部機制** 會自動觸發，並調用之前註冊的 `observerCallback` 函數。瀏覽器會把哪些元素被看到了（`entries`）以及「觀察員」自己（`observerInstance`）傳遞給 `observerCallback`。
 *     c. `observerCallback` 函數開始執行：
 *        i.  遍歷 `entries` (通常一次觸發只會有一個 entry，除非多個元素同時滿足條件)。
 *        ii. 對於每個 `entry`，如果 `entry.isIntersecting` 是 `true` (表示真的看到了)：
 *            1. `entry.target.classList.add('is-visible')`：給被看到的元素加上 'is-visible' class。
 *            2. `observerInstance.unobserve(entry.target)`：告訴「觀察員」：「這個元素你已經看過了，不用再盯著它了。」
 *
 * 所以，簡單來說：
 * `observeElements` (包含 `initScrollObserver`) 是 "準備和瞄準" 階段。
 * 瀏覽器偵測到目標後，自動執行 `observerCallback`，這是 "開火並記錄戰果" 階段。
 */

/**
 * 這是我們主要的「觀察員」物件。整個程式裡只會有這一個。
 * 它是由 `initScrollObserver` 這個函數叫出來（初始化）的。
 * @private
 * @type {IntersectionObserver | null}
 */
let observer = null;

/**
 * 這是「觀察員」發現有東西進到畫面時，會自動做的事情 (回調函數)。
 * **這個函數是由瀏覽器的 IntersectionObserver API 在偵測到交叉時自動調用的。**
 *
 * 運行流程：
 * 1. 瀏覽器偵測到某個被 `observer.observe(element)` 監聽的元素進入/離開視口（根據 `observerOptions`）。
 * 2. 瀏覽器自動執行此 `observerCallback` 函數。
 * 3. 函數內部：
 *    a. 遍歷 `entries` 數組（每個 `entry` 代表一個狀態改變的被觀察元素）。
 *    b. 如果 `entry.isIntersecting` 為 `true`（元素進入視口）：
 *       i.  `entry.target.classList.add('is-visible')`：給元素添加 'is-visible' class。
 *       ii. `observerInstance.unobserve(entry.target)`：指示「觀察員」停止觀察這個元素。
 *
 * @private
 * @param {IntersectionObserverEntry[]} entries - 一個列表，裡面裝著被「觀察員」看到的那些東西的詳細資料。
 * @param {IntersectionObserver} observerInstance - 就是那個正在工作的「觀察員」本人。
 */
const observerCallback = (entries, observerInstance) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) { // isIntersecting 的意思就是「正在和畫面交叉」，也就是看到了
      // 東西進入畫面了
      entry.target.classList.add('is-visible');

      // **重點：東西一出現，就別再看了**
      observerInstance.unobserve(entry.target); // unobserve 就是「停止觀察」
      console.log(`有個東西 (${entry.target.className}) 出現了，動畫跑一次就好，不再觀察它。`); // 看看日誌
    }
  });
};

/**
 * 「觀察員」工作時的一些設定。
 * - `root`: `null` 代表看整個瀏覽器視窗。
 * - `rootMargin`: '0px' 代表視窗邊緣不擴大也不縮小。
 * - `threshold`: 0.1 代表東西只要露出來 10%，就算是被看到了。
 *
 * @private
 * @type {IntersectionObserverInit}
 */
const observerOptions = {
  root: null,
  rootMargin: '0px',
  threshold: 0.1,
};

/**
 * 這個函數用來啟動我們的「滾動觀察員」。
 * 如果「觀察員」已經在工作了，再喊一次也沒關係，它不會重複啟動。
 *
 * 運行流程：
 * 1. 檢查 `observer` 是否已經存在。如果存在，直接返回。
 * 2. 如果不存在，則 `new IntersectionObserver(observerCallback, observerOptions)`：
 *    - 創建一個新的「觀察員」實例。
 *    - **關鍵**：將 `observerCallback` 函數註冊給這個「觀察員」。這意味著當「觀察員」偵測到元素交叉時，它會自動調用 `observerCallback`。
 *    - 將 `observerOptions` 作為配置傳遞給「觀察員」。
 * 3. 將新創建的「觀察員」賦值給全局的 `observer` 變數。
 *
 * @export
 * @returns {void} // void 的意思是這個函數做完事不回傳任何結果
 */
export function initScrollObserver() {
  if (observer) return; // 如果 observer 已經有東西了（代表啟動過了），就直接結束
  observer = new IntersectionObserver(observerCallback, observerOptions);
  console.log("滾動觀察員（只跑一次模式）準備好了！");
}

/**
 * 告訴「觀察員」要去注意網頁上哪些東西。
 * 你可以用 CSS 選擇器（像是 `.my-class` 或 `#my-id`）來指定要觀察的元素。
 * 如果「觀察員」還沒啟動，這個函數會先把它叫醒。
 * 它會稍微等一下（用 `setTimeout`）再去找元素，確保網頁都載入完畢了。
 * 如果有些東西之前已經加上 'is-visible' 了，就不會再重複觀察它們。
 *
 * 運行流程 (假設是第一次調用，或 `observer` 為 `null`)：
 * 1. 檢查 `observer` 是否存在。如果不存在 (或為 `null`)：
 *    a. 調用 `initScrollObserver()` 來創建並初始化「觀察員」，並註冊 `observerCallback`。
 * 2. `setTimeout` 被調用，其內部的回調函數會在當前 JavaScript 執行緒完成後（通常是 DOM 更新完成後）執行。
 * 3. `setTimeout` 的回調函數執行：
 *    a. `document.querySelectorAll(selector)`：查找所有匹配指定 CSS 選擇器的 DOM 元素。
 *    b. 遍歷找到的元素：
 *       i.  如果元素還沒有 'is-visible' class：
 *           1. `observer.observe(el)`：**關鍵**，指示「觀察員」開始監聽這個特定的 `el` 元素。從此刻起，如果 `el` 元素與視口發生交叉（根據 `observerOptions`），之前註冊的 `observerCallback` 就會被觸發。
 *
 * @export
 * @param {string} [selector='.animate-on-scroll'] - 你想觀察的元素的 CSS 選擇器。如果沒指定，預設就是找有 `.animate-on-scroll` 這個 class 的東西。
 * @returns {void}
 *
 * @example
 * // 比如在 Vue 組件掛載後，或者網頁內容都讀取完畢後：
 * import { observeElements } from './utils/intersectionObserver';
 *
 * onMounted(() => {
 *   observeElements('.product-card'); // 請觀察員注意所有 class 是 'product-card' 的元素
 *   observeElements();                // 也注意所有 class 是 '.animate-on-scroll' (預設) 的元素
 * });
 */
export function observeElements(selector = '.animate-on-scroll') {
  if (!observer) {
    initScrollObserver(); // 如果觀察員還沒啟動，先叫醒它
  }
  // 用 setTimeout 是為了等一下下，確保 Vue 或其他框架把畫面都更新好了
  setTimeout(() => {
    const elements = document.querySelectorAll(selector); // 找到所有符合條件的元素
    elements.forEach(el => {
      // 如果這個元素還沒有 'is-visible' (也就是還沒被看見過)
      if (!el.classList.contains('is-visible')) {
        observer.observe(el); // 就叫觀察員開始盯著它
      }
    });
  }, 0); // 0 毫秒，意思是最快速度執行，但會排在目前的任務之後
}

/**
 * 讓「觀察員」下班，不再觀察任何東西了。
 * 同時把「觀察員」清空，這樣如果之後還想用，可以重新啟動。
 * 這在網頁元件要被移除或頁面要關閉時很有用，可以清理資源。
 *
 * 運行流程：
 * 1. 檢查 `observer` 是否存在。
 * 2. 如果存在：
 *    a. `observer.disconnect()`：告訴「觀察員」停止監聽所有之前通過 `observer.observe()` 添加的目標元素。
 *    b. `observer = null`：將全局 `observer` 變數設回 `null`，以便將來可以重新初始化。
 *
 * @export
 * @returns {void}
 *
 * @example
 * // 比如在 Vue 組件要被移除時：
 * import { disconnectObserver } from './utils/intersectionObserver';
 *
 * onUnmounted(() => {
 *   disconnectObserver(); // 讓觀察員下班
 * });
 */
export function disconnectObserver() {
  if (observer) {
    observer.disconnect(); // 告訴觀察員停止所有觀察工作
    observer = null;       // 把觀察員變回 null，表示它不在了
    console.log("滾動觀察員已經下班了。");
  }
}