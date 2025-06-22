<template>
  <div class="directory-upload-simplified-core">
    <!-- 
      隱藏的文件輸入元素，用於實際的資料夾選擇。
    -->
    <input
      type="file"
      webkitdirectory
      directory
      multiple
      ref="folderInputEl"
      @change="handleFolderSelected"
      style="display: none;"
      accept="image/*"
    />
    <!-- 
      顯示相關信息和錯誤將由父組件處理，
      或者父組件可以傳遞 props 來決定是否在此顯示。
      為了簡化，此處不顯示，僅發送事件。
    -->
  </div>
</template>

<script setup lang="ts">
/**
 * @file DirectoryUploadBlock_Simplified.vue <script setup lang="ts">
 * @description DirectoryUploadBlock_Simplified 組件的核心 TypeScript 邏輯。
 *              此版本移除了可見的觸發按鈕，由父組件控制觸發。
 * 職責：
 * 1. 管理文件輸入元素的引用 (`folderInputEl`)。
 * 2. 追蹤已選取的有效圖片數量 (`selectedCountInternal`) 和本地錯誤信息 (`localErrorMessageInternal`)。
 * 3. 定義允許的圖片 MIME 類型 (`ALLOWED_IMAGE_TYPES`)。
 * 4. 提供觸發資料夾選擇的方法 (`triggerFolderSelect`) 給父組件調用。
 * 5. 處理文件選擇事件 (`handleFolderSelected`)：
 *    - 從選擇的文件中過濾出有效的圖片文件。
 *    - 更新內部計數和錯誤信息。
 *    - 通過 `files-selected` 事件將過濾後的圖片文件列表發送給父組件。
 *    - 通過 `count-updated` 和 `error-updated` 事件發送內部狀態給父組件。
 *    - 重置文件輸入元素的值。
 * 6. 定義組件發出的事件 (`defineEmits`)。
 * 7. 暴露方法給父組件 (`defineExpose`)。
 */

import { ref, watch } from 'vue';

const emit = defineEmits<{
  (e: 'files-selected', files: File[]): void;
  (e: 'count-updated', count: number): void;
  (e: 'error-updated', message: string | null): void;
}>();

const folderInputEl = ref<HTMLInputElement | null>(null);
const selectedCountInternal = ref(0);
const localErrorMessageInternal = ref<string | null>(null);

const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp'];

const triggerFolderSelect = () => {
  // 在觸發前清除舊狀態，確保父組件拿到最新的
  selectedCountInternal.value = 0;
  localErrorMessageInternal.value = null;
  emit('count-updated', 0);
  emit('error-updated', null);
  emit('files-selected', []); // 也清除已選文件
  folderInputEl.value?.click();
};

watch(selectedCountInternal, (newCount) => {
  emit('count-updated', newCount);
});

watch(localErrorMessageInternal, (newMessage) => {
  emit('error-updated', newMessage);
});

const handleFolderSelected = (event: Event) => {
  const inputElement = event.target as HTMLInputElement;
  localErrorMessageInternal.value = null; // 重置內部錯誤
  selectedCountInternal.value = 0; // 重置內部計數

  if (inputElement.files && inputElement.files.length > 0) {
    const allFiles = Array.from(inputElement.files);
    const imageFiles = allFiles.filter(file =>
      ALLOWED_IMAGE_TYPES.includes(file.type) && file.size > 0
    );

    selectedCountInternal.value = imageFiles.length;
    emit('files-selected', imageFiles);

    if (imageFiles.length === 0 && allFiles.length > 0) {
      localErrorMessageInternal.value = "選取的資料夾中沒有找到支援的圖片格式或圖片為空。";
    } else if (imageFiles.length < allFiles.length && imageFiles.length > 0) {
      // 可選提示
      // localErrorMessageInternal.value = `已過濾掉 ${allFiles.length - imageFiles.length} 個非支援格式或空文件。`;
    }
  } else {
    emit('files-selected', []);
  }

  if (inputElement) {
    inputElement.value = '';
  }
};

defineExpose({
  triggerFolderSelect
});
</script>

<style scoped lang="scss">
/* 此元件核心無可見UI，不需要太多樣式 */
.directory-upload-simplified-core {
  /* 可以保留一些基本結構樣式，如果未來需要添加內容 */
}
</style>