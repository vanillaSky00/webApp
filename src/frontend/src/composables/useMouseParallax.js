// src/composables/useMouseParallax.js
import { ref, onMounted, onUnmounted, nextTick } from 'vue';

/**
 * @file useMouseParallax.js
 * @description Vue Composable 函數，用於獲取滑鼠相對於指定容器中心或窗口中心的偏移比例。
 * 它可以被用來創建基於滑鼠移動的視差效果。
 *
 * @module composables/useMouseParallax
 *
 * @example
 * // 在組件中如何使用：
 * // <template>
 * //   <div ref="myContainer" class="parallax-container">
 * //     <div class="layer" :style="layerStyle"></div>
 * //   </div>
 * // </template>
 * //
 * // <script setup>
 * // import { ref, computed } from 'vue';
 * // import { useMouseParallax } from './composables/useMouseParallax';
 * //
 * // const myContainer = ref(null);
 * // const { offsetXRatio, offsetYRatio } = useMouseParallax(myContainer);
 * //
 * // const layerStyle = computed(() => ({
 * //   transform: `translate(${offsetXRatio.value * -50}px, ${offsetYRatio.value * -30}px)`
 * // }));
 * // </script>
 */

/**
 * 提供響應式的滑鼠相對於容器中心或窗口中心的偏移比例。
 *
 * 偏移比例的範圍是 -0.5 到 0.5：
 * - 當滑鼠在容器最左邊時，offsetXRatio 約為 -0.5。
 * - 當滑鼠在容器最右邊時，offsetXRatio 約為 0.5。
 * - 當滑鼠在容器中心時，offsetXRatio 約為 0。
 * - offsetYRatio 同理。
 *
 * 如果沒有提供 `containerRef`，或者 `containerRef.value` 為 `null`，
 * 則偏移比例會相對於整個瀏覽器窗口計算。
 *
 * 該 Composable 會自動處理 `mousemove` 和 `resize` 事件的監聽與移除，
 * 以及使用 `ResizeObserver` (如果提供了容器) 來監測容器尺寸變化。
 *
 * @param {import('vue').Ref<HTMLElement | null>} [containerRef=ref(null)] - 一個 Vue Ref，指向作為視差參考的 HTML 容器元素。
 *                                                                           如果為 `null` 或未提供，則使用瀏覽器窗口作為參考。
 * @returns {object} 返回一個包含以下響應式屬性的對象：
 * @returns {import('vue').Ref<number>} returns.offsetXRatio - 滑鼠在X軸上的偏移比例，範圍 -0.5 到 0.5。
 * @returns {import('vue').Ref<number>} returns.offsetYRatio - 滑鼠在Y軸上的偏移比例，範圍 -0.5 到 0.5。
 */
export function useMouseParallax(containerRef = ref(null)) {
  /**
   * @private
   * @type {import('vue').Ref<number>}
   * @description 當前滑鼠的客戶端 X 坐標。
   */
  const mouseX = ref(0);
  /**
   * @private
   * @type {import('vue').Ref<number>}
   * @description 當前滑鼠的客戶端 Y 坐標。
   */
  const mouseY = ref(0);

  /**
   * @private
   * @type {import('vue').Ref<number>}
   * @description 參考容器的寬度。初始為窗口寬度。
   */
  const containerWidth = ref(window.innerWidth);
  /**
   * @private
   * @type {import('vue').Ref<number>}
   * @description 參考容器的高度。初始為窗口高度。
   */
  const containerHeight = ref(window.innerHeight);
  /**
   * @private
   * @type {import('vue').Ref<{left: number, top: number}>}
   * @description 參考容器相對於視口的左上角位置。
   */
  const containerRect = ref({ left: 0, top: 0 });

  /**
   * @type {import('vue').Ref<number>}
   * @description 滑鼠相對於容器中心在 X 軸上的偏移比例 (-0.5 到 0.5)。
   */
  const offsetXRatio = ref(0);
  /**
   * @type {import('vue').Ref<number>}
   * @description 滑鼠相對於容器中心在 Y 軸上的偏移比例 (-0.5 到 0.5)。
   */
  const offsetYRatio = ref(0);

  /**
   * 更新滑鼠位置 (mouseX, mouseY) 並重新計算偏移比例。
   * @private
   * @param {MouseEvent} event - 滑鼠移動事件對象。
   */
  const updateMousePosition = (event) => {
    mouseX.value = event.clientX;
    mouseY.value = event.clientY;
    calculateOffsetRatios();
  };

  /**
   * 更新容器的尺寸 (containerWidth, containerHeight) 和位置 (containerRect) 信息。
   * 如果未指定容器，則使用窗口尺寸。
   * 更新後會重新計算偏移比例。
   * @private
   */
  const updateContainerInfo = () => {
    const element = containerRef.value;
    if (element) {
      const rect = element.getBoundingClientRect();
      containerWidth.value = rect.width;
      containerHeight.value = rect.height;
      // getBoundingClientRect() 返回的 left/top 是相對於視口的
      containerRect.value = { left: rect.left, top: rect.top };
    } else {
      containerWidth.value = window.innerWidth;
      containerHeight.value = window.innerHeight;
      containerRect.value = { left: 0, top: 0 }; // 窗口的 left/top 總是 0
    }
    calculateOffsetRatios();
  };

  /**
   * 根據當前的滑鼠位置和容器信息計算 offsetXRatio 和 offsetYRatio。
   * @private
   */
  const calculateOffsetRatios = () => {
    if (containerWidth.value > 0 && containerHeight.value > 0) {
      // 計算滑鼠在容器內的相對位置 (相對於容器左上角)
      const relativeX = mouseX.value - containerRect.value.left;
      const relativeY = mouseY.value - containerRect.value.top;

      // 計算相對於容器中心的比例
      // (relativeX / containerWidth.value) 的範圍是 0 (最左) 到 1 (最右)
      // 減去 0.5 後，範圍變成 -0.5 (最左) 到 0.5 (最右)
      offsetXRatio.value = (relativeX / containerWidth.value) - 0.5;
      offsetYRatio.value = (relativeY / containerHeight.value) - 0.5;
    } else {
      // 防止除以零，或在容器尺寸無效時重置比例
      offsetXRatio.value = 0;
      offsetYRatio.value = 0;
    }
  };

  /**
   * @private
   * @type {ResizeObserver | null}
   * @description 用於觀察指定容器尺寸變化的 ResizeObserver 實例。
   */
  let resizeObserver = null;

  /**
   * 組件掛載後執行：
   * 1. 使用 `nextTick` 確保 DOM 元素已渲染。
   * 2. 調用 `updateContainerInfo` 初始化容器尺寸和位置。
   * 3. 為目標元素 (指定容器或窗口) 添加 `mousemove` 事件監聽器。
   * 4. 為 `window` 添加 `resize` 事件監聽器。
   * 5. 如果提供了 `containerRef.value`，則創建並啟動 `ResizeObserver` 來監聽容器尺寸變化。
   */
  onMounted(() => {
    nextTick(() => {
      updateContainerInfo(); 
      
      const targetElement = containerRef.value || window;
      // 為 mousemove 選擇正確的目標
      // 如果是 window，滑鼠事件座標 (clientX, clientY) 直接是相對於視口的
      // 如果是特定容器，滑鼠事件座標也是相對於視口的，但我們需要 containerRect 來計算相對容器的位置
      targetElement.addEventListener('mousemove', updateMousePosition);
      
      // resize 事件總是監聽 window，因為容器大小變化也會被 ResizeObserver 捕獲，
      // 或者容器大小變化是因窗口大小變化引起的。
      window.addEventListener('resize', updateContainerInfo);

      if (containerRef.value) {
        resizeObserver = new ResizeObserver(entries => {
          // ResizeObserver 的回調通常在尺寸變化後觸發
          // entries[0].contentRect 包含了新的尺寸信息
          // 直接調用 updateContainerInfo 通常是安全的，它會重新獲取 rect
          updateContainerInfo();
        });
        resizeObserver.observe(containerRef.value);
      }
    });
  });

  /**
   * 組件卸載前執行：
   * 移除所有添加的事件監聽器和斷開 `ResizeObserver` 的連接，以防止內存洩漏。
   */
  onUnmounted(() => {
    const targetElement = containerRef.value || window;
    targetElement.removeEventListener('mousemove', updateMousePosition);
    window.removeEventListener('resize', updateContainerInfo);
    if (resizeObserver) {
      resizeObserver.disconnect();
    }
  });

  return {
    offsetXRatio,
    offsetYRatio,
  };
}