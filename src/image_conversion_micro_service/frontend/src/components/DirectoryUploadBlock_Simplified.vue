<template>
  <!--
    @file DirectoryUploadBlock_Simplified.vue
    @description 一個簡化的 Vue 組件，用於允許用戶選擇並上傳整個資料夾中的圖片。
    組件特性：
    - 提供一個按鈕觸發資料夾選擇對話框。
    - 使用隱藏的 `input[type="file"]` 元素，並設置 `webkitdirectory` 和 `directory` 屬性以啟用資料夾選擇。
    - `multiple` 屬性允許選擇資料夾內的多個文件。
    - `accept="image/*"` 嘗試在文件對話框中初步過濾非圖片文件 (但實際過濾依賴 JavaScript)。
    - 選擇後，組件會過濾出支援的圖片類型 (JPEG, PNG, GIF, WebP, BMP) 且文件大小大於0的文件。
    - 顯示已選取的有效圖片數量。
    - 如果選中的資料夾中沒有有效圖片，會顯示相應的提示信息。
    - 通過 `files-selected` 事件向父組件發出已過濾的圖片文件列表。
  -->
  <div class="directory-upload-simplified">
    <!-- 
      觸發資料夾選擇的按鈕。
      按鈕文本會根據是否已選擇文件以及已選數量動態更新。
    -->
    <button @click="triggerFolderSelect" class="select-folder-btn">
      {{ buttonText }}
    </button>
    <!-- 
      隱藏的文件輸入元素，用於實際的資料夾選擇。
      - webkitdirectory/directory: 啟用資料夾選擇模式。
      - multiple: 允許選擇資料夾內的多個文件。
      - ref: "folderInputEl" 用於在腳本中訪問此元素。
      - @change: 文件選擇變化時觸發 handleFolderSelected 方法。
      - accept: 提示瀏覽器優先顯示圖片文件。
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
      顯示已選取的有效圖片數量 (如果大於0)。
    -->
    <div v-if="selectedCount > 0" class="selected-info">
      已選取 {{ selectedCount }} 張圖片
    </div>
    <!-- 
      顯示本地錯誤/提示信息，例如資料夾中沒有有效圖片。
    -->
    <div v-if="localErrorMessage" class="local-error">{{ localErrorMessage }}</div>
  </div>
</template>

<script setup lang="ts">
/**
 * @file DirectoryUploadBlock_Simplified.vue <script setup lang="ts">
 * @description DirectoryUploadBlock_Simplified 組件的 TypeScript 邏輯部分。
 * 職責：
 * 1. 管理文件輸入元素的引用 (`folderInputEl`)。
 * 2. 追蹤已選取的有效圖片數量 (`selectedCount`) 和本地錯誤信息 (`localErrorMessage`)。
 * 3. 定義允許的圖片 MIME 類型 (`ALLOWED_IMAGE_TYPES`)。
 * 4. 提供觸發資料夾選擇的方法 (`triggerFolderSelect`)。
 * 5. 計算按鈕的動態文本 (`buttonText`)。
 * 6. 處理文件選擇事件 (`handleFolderSelected`)：
 *    - 從選擇的文件中過濾出有效的圖片文件。
 *    - 更新 `selectedCount` 和 `localErrorMessage`。
 *    - 通過 `files-selected` 事件將過濾後的圖片文件列表發送給父組件。
 *    - 重置文件輸入元素的值，以便用戶可以重新選擇相同的資料夾。
 * 7. 定義組件發出的事件 (`defineEmits`)。
 */

import { ref, computed } from 'vue';

/**
 * @typedef {object} Emits
 * @property {(event: 'files-selected', files: File[]) => void} files-selected - 當用戶選擇了資料夾並且圖片文件被過濾後觸發，傳遞過濾後的 File 對象數組。
 */
const emit = defineEmits<{
  (e: 'files-selected', files: File[]): void
}>();

/**
 * @type {import('vue').Ref<HTMLInputElement | null>}
 * @description 對資料夾文件輸入框 (input[type="file"]) 的引用。
 */
const folderInputEl = ref<HTMLInputElement | null>(null);

/**
 * @type {import('vue').Ref<number>}
 * @description 用戶選擇的資料夾中，經過濾後有效的圖片文件數量。
 */
const selectedCount = ref(0);

/**
 * @type {import('vue').Ref<string | null>}
 * @description 在組件內部產生的錯誤或提示信息，例如資料夾中無有效圖片。
 */
const localErrorMessage = ref<string | null>(null);

/**
 * @const {string[]} ALLOWED_IMAGE_TYPES
 * @description 一個包含允許上傳的圖片 MIME 類型的數組。
 * 用於在客戶端過濾文件。
 */
const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/bmp'];

/**
 * @function triggerFolderSelect
 * @description 以編程方式觸發隱藏的資料夾文件輸入框的點擊事件，打開資料夾選擇對話框。
 * @returns {void}
 */
const triggerFolderSelect = () => {
  folderInputEl.value?.click();
};

/**
 * @computed buttonText
 * @description 計算屬性，根據是否已選擇文件以及選中數量動態生成按鈕的文本。
 * @returns {string} 按鈕上應顯示的文本。
 */
const buttonText = computed(() => {
    return selectedCount.value > 0 ? `重新選擇資料夾 (已選 ${selectedCount.value} 張)` : '選擇素材資料夾';
});

/**
 * @function handleFolderSelected
 * @description 當用戶通過文件輸入框選擇了資料夾後觸發的回調函數。
 * 該函數會：
 * 1. 清除之前的本地錯誤信息和選中計數。
 * 2. 獲取所有選中的文件。
 * 3. 過濾出類型在 `ALLOWED_IMAGE_TYPES` 中且文件大小大於 0 的圖片文件。
 * 4. 更新 `selectedCount`。
 * 5. 通過 `files-selected` 事件將過濾後的 `File[]` 數組發送給父組件。
 * 6. 如果選擇了文件但沒有有效的圖片，則設置 `localErrorMessage`。
 * 7. 重置文件輸入元素的值，以便用戶可以重新選擇同一個資料夾。
 * @param {Event} event - 文件輸入框的 change 事件對象。
 * @returns {void}
 */
const handleFolderSelected = (event: Event) => {
  const inputElement = event.target as HTMLInputElement;
  localErrorMessage.value = null;
  selectedCount.value = 0;

  if (inputElement.files && inputElement.files.length > 0) {
    const allFiles = Array.from(inputElement.files);
    const imageFiles = allFiles.filter(file =>
      ALLOWED_IMAGE_TYPES.includes(file.type) && file.size > 0 // 確保文件大小大於0，排除空文件
    );

    selectedCount.value = imageFiles.length;
    emit('files-selected', imageFiles); 

    if (imageFiles.length === 0 && allFiles.length > 0) {
      localErrorMessage.value = "選取的資料夾中沒有找到支援的圖片格式或圖片為空。";
    } else if (imageFiles.length < allFiles.length && imageFiles.length > 0) {
      // 可選：如果部分文件被過濾，可以給一個提示
      // localErrorMessage.value = `已過濾掉 ${allFiles.length - imageFiles.length} 個非支援格式或空文件。`;
    }
  } else {
    // 沒有選擇任何文件，或者取消了選擇
    emit('files-selected', []); 
  }

  // 重置 input 的值，這樣用戶可以重新選擇同一個資料夾（否則 change 事件可能不會再次觸發）
  if (inputElement) {
    inputElement.value = ''; 
  }
};
</script>

<style scoped lang="scss">
/* 
  SCSS 樣式塊，`scoped` 屬性確保樣式僅作用於此組件。
  定義了資料夾上傳組件的容器、按鈕、已選信息和錯誤提示的樣式。
*/

.directory-upload-simplified {
  padding: 20px; /* 增加內邊距 */
  border: 2px dashed #ced4da; /* 更明顯的虛線邊框 */
  border-radius: 8px; /* 更大的圓角 */
  text-align: center;
  background-color: #f8f9fa; /* 非常淺的背景色 */
  transition: background-color 0.3s ease, border-color 0.3s ease;

  &:hover {
    background-color: #e9ecef; /* 滑鼠懸停時略微改變背景色 */
    border-color: #adb5bd;
  }
}

.select-folder-btn {
  padding: 12px 22px; /* 調整按鈕內邊距 */
  font-size: 1rem;   /* 調整字體大小 */
  color: white;
  background-color: #495057; /* 深灰色系按鈕 */
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
  font-weight: 500;

  &:hover {
    background-color: #343a40; /* 滑鼠懸停時更深的灰色 */
    transform: translateY(-1px);
  }
  &:active {
    transform: translateY(0px);
  }
}

.selected-info {
  margin-top: 12px; /* 調整與按鈕的間距 */
  font-size: 0.9rem; /* 調整字體大小 */
  color: #218838; /* 較深的綠色，表示成功/信息 */
  font-weight: 500;
}

.local-error {
  margin-top: 12px; /* 調整與按鈕的間距 */
  color: #c82333; /* 較深的紅色，表示錯誤 */
  font-size: 0.9rem; /* 調整字體大小 */
  font-weight: 500;
}
</style>