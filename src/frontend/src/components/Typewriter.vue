<!-- src/components/Typewriter.vue -->
<script setup>
import { ref, watch, onUnmounted, defineProps, defineEmits, computed } from 'vue';

const props = defineProps({
  text: { type: String, required: true },
  speed: { type: Number, default: 80 },
  initialDelay: { type: Number, default: 0 },
  // loop: { type: Boolean, default: false }, // **移除 loop 功能，因為只播放一次**
  // loopDelay: { type: Number, default: 1500 },
  cursor: { type: Boolean, default: true },
  startSignal: { type: Boolean, default: true } // 保留，允許延遲啟動
});

const emit = defineEmits(['completed']); // 移除 loop-completed

const displayText = ref('');
const isComplete = ref(false);
const isRunning = ref(false); // **添加 isRunning 狀態**
const hasPlayedOnce = ref(false); // **新增：標記是否已播放過一次**
let timeoutId = null;
// let loopTimeoutId = null; // 移除 loop 相關

const startTyping = () => {
    // **修改：如果沒有文本，或者已經播放過一次，或者正在運行，則不啟動**
    if (!props.text || hasPlayedOnce.value || isRunning.value) return;

    clearTimeout(timeoutId); // 清除可能的舊計時器
    displayText.value = '';
    let currentIndex = 0; // 移到函數內部
    isComplete.value = false;
    isRunning.value = true; // **標記為正在運行**

    const type = () => {
        if (currentIndex < props.text.length) {
            displayText.value += props.text.charAt(currentIndex);
            currentIndex++;
            timeoutId = setTimeout(type, props.speed);
        } else {
            completeTyping();
        }
    };

    // 初始延遲
    timeoutId = setTimeout(type, props.initialDelay);
};

const completeTyping = () => {
    isComplete.value = true;
    isRunning.value = false; // **標記為運行結束**
    hasPlayedOnce.value = true; // **標記為已播放過一次**
    emit('completed');
    // **移除 loop 邏輯**
    // if (props.loop) { ... }
}

const clearTimeouts = () => {
    clearTimeout(timeoutId);
    // clearTimeout(loopTimeoutId); // 移除 loop 相關
    timeoutId = null;
    // loopTimeoutId = null;
}

// --- 監聽器修改 ---

// **移除對 props.text 的 watch，因為我們只根據 startSignal 播放一次**
// watch(() => props.text, (newText) => { ... });

// 監聽外部啟動信號 (只在首次變為 true 且未播放過時觸發)
watch(() => props.startSignal, (newVal, oldVal) => {
    // **當 startSignal 首次變為 true 且從未播放過時，才啟動**
    if (newVal === true && oldVal === false && !hasPlayedOnce.value) {
         console.log("Start signal received and hasn't played yet. Starting typing."); // 調試
        startTyping();
    }
    // 如果初始 startSignal 就是 true，也觸發一次
    else if (newVal === true && !oldVal && !hasPlayedOnce.value) { // 處理 immediate: true 的情況
         console.log("Initial start signal is true and hasn't played yet. Starting typing."); // 調試
         startTyping();
    }
    // **不再處理信號變為 false 的情況，因為只播一次**
}, { immediate: true }); // immediate: true 確保初始狀態為 true 時也能觸發

// --- 清理 ---
onUnmounted(() => {
    clearTimeouts();
});

</script>

<template>
  <span>
    <!-- 模板修改：如果已經播放過，直接顯示完整文本 -->
    {{ hasPlayedOnce ? props.text : displayText }}
    <!-- 光標只在正在運行時顯示 -->
    <span v-if="props.cursor && isRunning" class="typing-cursor-css is-typing"></span>
  </span>
</template>

<style scoped>
/* CSS 光標樣式不變 */
.typing-cursor-css {
  display: inline-block;
  width: 1px;
  height: 1.1em;
  background-color: currentColor;
  margin-left: 2px;
  vertical-align: text-bottom;
  opacity: 0; /* 預設不顯示 */
}
.typing-cursor-css.is-typing {
  opacity: 1;
  animation: blink 1s step-end infinite;
}

@keyframes blink {
  from, to { background-color: transparent }
  50% { background-color: currentColor; }
}
</style>