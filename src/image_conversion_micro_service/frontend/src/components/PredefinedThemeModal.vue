<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-content">
      <h3 class="modal-title">選擇一個主題圖片集</h3>
      <ul v-if="themes.length > 0" class="theme-list">
        <li
          v-for="theme in themes"
          :key="theme.path"
          class="theme-item"
          @click="selectTheme(theme)"
          tabindex="0"
          @keydown.enter="selectTheme(theme)"
          @keydown.space.prevent="selectTheme(theme)"
        >
          <span class="theme-name">{{ theme.name }}</span>
          <span class="theme-count">({{ theme.images.length }} 張圖片)</span>
        </li>
      </ul>
      <p v-else class="no-themes-message">目前沒有可用的主題圖片集。</p>
      <button @click="emit('close')" class="close-button">取消</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { PredefinedTheme } from './ImageSourceSelector.vue'; // 從父組件導入類型

interface Props {
  themes: PredefinedTheme[];
}
const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'select-theme', theme: PredefinedTheme): void;
  (e: 'close'): void;
}>();

const selectTheme = (theme: PredefinedTheme) => {
  emit('select-theme', theme);
};
</script>

<style scoped lang="scss">
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 25px 30px;
  border-radius: 8px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  width: 90%;
  max-width: 500px;
  text-align: left;
}

.modal-title {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
  font-size: 1.4rem;
  font-weight: 600;
}

.theme-list {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
  max-height: 300px;
  overflow-y: auto;
}

.theme-item {
  padding: 12px 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &:last-child {
    border-bottom: none;
  }

  &:hover, &:focus {
    background-color: #f0f0f0;
    outline: none;
  }
}

.theme-name {
  color: #454545;
  font-weight: 500;
}

.theme-count {
  font-size: 0.9em;
  color: #777;
}

.no-themes-message {
  color: #777;
  margin-bottom: 20px;
}

.close-button {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  font-size: 1rem;

  &:hover {
    background-color: #5a6268;
  }
}
</style>