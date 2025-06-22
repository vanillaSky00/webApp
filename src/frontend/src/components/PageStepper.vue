<!-- src/components/PageStepper.vue -->
<template>
  <div class="page-stepper">
    <div
      v-for="step in steps"
      :key="step.id"
      class="step-item"
      @click="navigateToSection(step.targetId)"
      tabindex="0"
      @keydown.enter="navigateToSection(step.targetId)"
      @keydown.space.prevent="navigateToSection(step.targetId)"
    >
      <div class="step-circle">{{ step.number }}</div>
      <div class="step-title">{{ step.title }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface Step {
  id: string;
  number: string;
  title: string;
  targetId: string; // 對應到主頁面中區塊的 ID
}

const steps = ref<Step[]>([
  { id: 'step1', number: '1', title: '選擇你的風格圖片', targetId: 'style-image-section' },
  { id: 'step2', number: '2', title: '上傳你的素材圖片資料夾', targetId: 'source-images-section' },
  { id: 'step3', number: '3', title: '開始生成', targetId: 'generation-section' },
]);

const emit = defineEmits<{
  (e: 'navigate', targetId: string): void;
}>();

const navigateToSection = (targetId: string) => {
  emit('navigate', targetId);
};
</script>

<style scoped lang="scss">
.page-stepper {
  display: flex;
  justify-content: space-around;
  align-items: flex-start; /* 讓文字在圓圈下方對齊 */
  margin-bottom: 2.5rem; /* 與下方內容的間距 */
  padding: 10px 0;
}

.step-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.2s ease-in-out;

  &:hover,
  &:focus {
    background-color: rgba(0, 0, 0, 0.05);
    outline: none;
  }
}

.step-circle {
  width: 60px; /* 圓圈大小 */
  height: 60px;
  background-color: #4A5568; /* 深灰色，與 ImageSourceSelector 的按鈕顏色類似 */
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.8rem; /* 數字大小 */
  font-weight: 600;
  margin-bottom: 0.8rem; /* 圓圈與文字的間距 */
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.step-title {
  font-size: 0.95rem; /* 文字大小 */
  color: #2D3748; /* 文字顏色 */
  font-weight: 500;
  max-width: 150px; /* 限制文字寬度，使其適當換行 */
}
</style>