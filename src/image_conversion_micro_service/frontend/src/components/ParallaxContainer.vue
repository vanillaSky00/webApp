<template>
    <div ref="containerElement" class="parallax-container">
      <slot>
      </slot>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, watch, nextTick } from 'vue';
  import { useMouseParallax } from '@/composables/useMouseParallax';
  
  // --- Props (可選) ---
  const props = defineProps({
    // 最大偏移量 (px)
    maxOffset: {
      type: Number,
      default: 20
    },
    // 是否反轉移動方向 (預設與滑鼠反向)
    reverse: {
      type: Boolean,
      default: true
    },
    // 過渡效果 (CSS transition 字符串)
    transition: {
      type: String,
      default: 'transform 0.1s linear' // 默認平滑過渡
    }
  });
  
  // --- Ref for the container ---
  const containerElement = ref(null);
  
  // --- Use the composable ---
  // 將容器 ref 傳遞給 Hook
  const { offsetXRatio, offsetYRatio } = useMouseParallax(containerElement);
  
  // --- Apply styles to children ---
  const applyParallaxToChildren = () => {
      if (!containerElement.value) return;
  
      // 獲取容器內所有帶有 data-parallax-factor 的直接子元素或深層子元素
      // 注意：如果子元素是 Vue 元件，可能需要更複雜的選擇或事件傳遞
      const children = containerElement.value.querySelectorAll('[data-parallax-factor]');
  
      const direction = props.reverse ? -1 : 1; // 方向
  
      children.forEach(child => {
          const factor = parseFloat(child.dataset.parallaxFactor) || 0; // 讀取因子
          if (factor === 0) {
               child.style.transform = ''; // 因子為 0 不移動
               child.style.transition = ''; // 清除 transition
               return;
          }
  
          // 計算位移
          const translateX = offsetXRatio.value * props.maxOffset * factor * direction;
          const translateY = offsetYRatio.value * props.maxOffset * factor * direction;
  
          // 應用樣式
          child.style.transform = `translateX(${translateX}px) translateY(${translateY}px) translateZ(0px)`;
          child.style.transition = props.transition; // 應用過渡效果
          child.style.willChange = 'transform'; // 性能提示
      });
  };
  
  // --- Watch for ratio changes ---
  // 監聽偏移比例變化，並更新子元素樣式
  watch([offsetXRatio, offsetYRatio], () => {
      // 可以考慮使用 requestAnimationFrame 優化性能，避免過於頻繁的樣式更新
      requestAnimationFrame(applyParallaxToChildren);
  });
  
  // --- Ensure initial application ---
  // 元件掛載後，延遲應用一次，確保子元素已渲染
  onMounted(() => {
      nextTick(applyParallaxToChildren);
  });
  
  </script>
  
  <style scoped>
  .parallax-container {
    position: relative; /* 使得子元素的絕對定位相對於它 */
    width: 100%;
    height: 100vh; /*100% 可能會因為抓不到正確高度變成 0px*/
    overflow: hidden; /* (可選) 隱藏超出範圍的子元素 */
  }
  
  /*
    子元素 (slot 內容) 的樣式需要在父元件或全域 CSS 中定義。
    例如，你可能需要給子元素 `position: absolute` 來讓它們疊加。
  */
  :deep([data-parallax-factor]) {
      /* 給被視差的元素添加一些基礎樣式，確保 transition 生效 */
      transition: transform 0.1s linear; /* 默認 transition */
  }
  </style>