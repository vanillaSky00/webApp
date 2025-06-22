<template>
  <div class="image-source-selector-container">
    <h2 class="main-title">步驟 2：選擇你的素材圖片</h2>
    <p class="main-description">
      你可以從本機資料夾上傳圖片，或選擇我們提供的預設主題圖片集。所有選擇的圖片將合併用於生成拼貼畫。
    </p>

    <div class="mode-switcher">
      <button
        @click="setSourceMode('upload')"
        :class="['mode-button', { active: sourceMode === 'upload' }]"
        :disabled="isLoadingCurrentAction"
      >
        從本機上傳資料夾
      </button>
      <button
        @click="setSourceMode('predefined')"
        :class="['mode-button', { active: sourceMode === 'predefined' }]"
        :disabled="isLoadingCurrentAction"
      >
        選擇主題圖片集
      </button>
    </div>

    <div class="upload-area">
      <button @click="handleMainActionClick" class="action-button" :disabled="isLoadingCurrentAction">
        {{ mainButtonTextComputed }}
        <span v-if="isLoadingCurrentAction && sourceMode === 'upload'" class="spinner"></span>
      </button>

      <div v-if="isLoadingCurrentAction" class="loading-info">
        <span v-if="sourceMode === 'predefined' && !showPredefinedModal">正在載入主題圖片...</span>
        <span v-else-if="sourceMode === 'upload'">檢查檔案中...</span>
        <span v-else-if="showPredefinedModal">開啟主題選擇...</span>
      </div>
      
      <div v-if="!isLoadingCurrentAction && currentActionInfoMessage" class="info-message">
        {{ currentActionInfoMessage }}
      </div>

      <div v-if="!isLoadingCurrentAction && currentActionErrorMessage" class="error-message">
        {{ currentActionErrorMessage }}
      </div>
    </div>

    <!-- 已選素材來源列表 -->
    <div v-if="selectedSources.length > 0" class="selected-sources-list">
      <h3 class="selected-sources-title">已選素材集 (共 {{ totalSelectedFilesCount }} 張圖片):</h3>
      <ul>
        <li v-for="source in selectedSources" :key="source.id" class="selected-source-item">
          <span class="source-icon">
            {{ source.type === 'upload' ? '📁' : '🖼️' }}
          </span>
          <span class="source-name">
            {{ source.name }} ({{ source.files.length }} 張)
          </span>
          <button 
            @click="removeSource(source.id)" 
            class="remove-source-button" 
            title="移除此素材集"
            :disabled="isLoadingCurrentAction"
          >
            ×
          </button>
        </li>
      </ul>
      <button 
        v-if="selectedSources.length > 0" 
        @click="clearAllSources" 
        class="clear-all-button"
        :disabled="isLoadingCurrentAction"
      >
        清除所有已選素材
      </button>
    </div>
     <div v-else-if="!isLoadingCurrentAction && !currentActionErrorMessage && !currentActionInfoMessage && predefinedThemesInitialized" class="info-message placeholder-message">
      目前尚未選擇任何素材圖片。請點擊上方按鈕新增。
    </div>


    <!-- 隱藏的資料夾上傳核心組件 -->
    <DirectoryUploadBlock_Simplified
      ref="userUploaderInstance"
      @files-selected="handleUserUploadedFilesFromComponent"
      @count-updated="handleUserUploadCountUpdate"
      @error-updated="handleUserUploadErrorUpdate"
    />

    <!-- 主題選擇 Modal -->
    <PredefinedThemeModal
      v-if="showPredefinedModal"
      :themes="predefinedThemes"
      @select-theme="handlePredefinedThemeSelected"
      @close="closePredefinedModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import DirectoryUploadBlock_Simplified from './DirectoryUploadBlock_Simplified.vue';
import PredefinedThemeModal from './PredefinedThemeModal.vue';

// --- 類型定義 ---
interface PredefinedThemeImage {
  name: string;
  urlLoader: () => Promise<string>;
}

export interface PredefinedTheme { // 導出給 Modal 使用
  name: string; // 資料夾名稱 e.g., 'city_theme_images'
  path: string; // 相對路徑，用於標識
  images: PredefinedThemeImage[]; // 此主題下的圖片列表
}

interface SelectedSource {
  id: string; // 用於唯一標識和移除
  name: string; // 資料夾名稱或主題名稱
  type: 'upload' | 'predefined';
  files: File[];
}

// --- emits ---
const emit = defineEmits<{
  (e: 'files-selected', files: File[]): void;
  (e: 'source-error', message: string | null): void; // 單次操作的錯誤或整體狀態錯誤
}>();

// --- refs ---
const sourceMode = ref<'upload' | 'predefined'>('upload');
const userUploaderInstance = ref<InstanceType<typeof DirectoryUploadBlock_Simplified> | null>(null);

const selectedSources = ref<SelectedSource[]>([]);

const showPredefinedModal = ref(false);
const predefinedThemes = ref<PredefinedTheme[]>([]);
const predefinedThemesInitialized = ref(false); // 追蹤主題是否已初始化完成

const isLoadingCurrentAction = ref(false); 
const currentActionInfoMessage = ref<string | null>(null);
const currentActionErrorMessage = ref<string | null>(null);

// ---生命週期鉤子---
onMounted(async () => {
  await fetchPredefinedThemes();
});

// --- 方法 ---
const resetCurrentActionState = (clearMessages: boolean = true) => {
  if (clearMessages) {
    currentActionInfoMessage.value = null;
    currentActionErrorMessage.value = null;
  }
  // isLoadingCurrentAction 由各自的異步操作開始和結束時設置
};

const setSourceMode = (mode: 'upload' | 'predefined') => {
  if (isLoadingCurrentAction.value) return;
  sourceMode.value = mode;
  resetCurrentActionState();
};

const fetchPredefinedThemes = async () => {
  isLoadingCurrentAction.value = true;
  currentActionInfoMessage.value = "正在初始化主題列表...";
  try {
    const imageModules = import.meta.glob('@/assets/material/*/*.{jpg,jpeg,png,gif,webp,bmp}', {
      as: 'url',
      eager: false,
    });
    const themesMap: Record<string, PredefinedTheme> = {};
    for (const path in imageModules) {
      const pathParts = path.split('/');
      const fileNameWithExtension = pathParts.pop()!;
      const themeFolderName = pathParts.pop()!; 
      const formattedThemeName = themeFolderName.replace(/_/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
      if (!themesMap[themeFolderName]) {
        themesMap[themeFolderName] = { name: formattedThemeName, path: themeFolderName, images: [] };
      }
      themesMap[themeFolderName].images.push({ name: fileNameWithExtension, urlLoader: imageModules[path] as () => Promise<string> });
    }
    predefinedThemes.value = Object.values(themesMap).filter(theme => theme.images.length > 0);
    if (predefinedThemes.value.length === 0) {
        currentActionInfoMessage.value = "未找到可用的預設主題。";
    } else {
        currentActionInfoMessage.value = null; 
    }
  } catch (error) {
    console.error("掃描預設主題時發生錯誤:", error);
    currentActionErrorMessage.value = "無法載入預設主題列表。";
    emit('source-error', "無法載入預設主題列表。");
  } finally {
    isLoadingCurrentAction.value = false;
    predefinedThemesInitialized.value = true;
     // 如果初始化後沒有選擇任何東西，且沒有錯誤，則清空消息
    if (!currentActionErrorMessage.value && selectedSources.value.length === 0) {
        currentActionInfoMessage.value = null;
    }
  }
};

const handleMainActionClick = () => {
  if (isLoadingCurrentAction.value) return;
  resetCurrentActionState();

  if (sourceMode.value === 'upload') {
    isLoadingCurrentAction.value = true; // 開始上傳操作
    userUploaderInstance.value?.triggerFolderSelect();
    // isLoadingCurrentAction 會在 handleUserUploadedFilesFromComponent 或 handleUserUploadErrorUpdate 中設為 false
  } else {
    if (predefinedThemes.value.length === 0 && predefinedThemesInitialized.value) {
      currentActionErrorMessage.value = "目前沒有可用的主題圖片集可供選擇。";
      emit('source-error', currentActionErrorMessage.value);
      return;
    }
    if (!predefinedThemesInitialized.value) {
      currentActionInfoMessage.value = "主題列表仍在初始化，請稍候...";
      return;
    }
    showPredefinedModal.value = true;
  }
};

const addSourceToList = (newSource: SelectedSource) => {
  const existingSourceByNameAndType = selectedSources.value.find(
    s => s.name === newSource.name && s.type === newSource.type
  );

  if (existingSourceByNameAndType) {
    currentActionErrorMessage.value = `素材集 "${newSource.name}" (${newSource.type === 'upload' ? '本機資料夾' : '主題'}) 已存在。如需更新，請先移除舊版本。`;
    emit('source-error', currentActionErrorMessage.value); // 也通知父組件
    return; 
  }

  selectedSources.value.push(newSource);
  currentActionInfoMessage.value = `已成功新增素材集 "${newSource.name}" (${newSource.files.length} 張圖片)。`;
  currentActionErrorMessage.value = null; // 清除之前的錯誤
  emitAllSelectedFiles();
};

const handleUserUploadedFilesFromComponent = (files: File[]) => {
  isLoadingCurrentAction.value = false; // 上傳操作結束
  if (files.length === 0) {
    // DirectoryUploadBlock_Simplified 應該已經通過 handleUserUploadErrorUpdate 設置了錯誤信息
    // 如果沒有，可以在這裡設置一個默認的
    if (!currentActionErrorMessage.value) {
        currentActionErrorMessage.value = "選擇的資料夾中沒有有效的圖片。";
    }
    emit('source-error', currentActionErrorMessage.value);
    return; 
  }
  
  let folderName = "上傳的資料夾"; 
  if (files[0]?.webkitRelativePath) {
    const pathParts = files[0].webkitRelativePath.split('/');
    if (pathParts.length > 0 && pathParts[0]) { // 確保 pathParts[0] 不是空字串
      folderName = pathParts[0];
    }
  }
  
  const sourceId = `upload-${folderName.replace(/\s+/g, '_')}-${Date.now()}`;

  addSourceToList({
    id: sourceId,
    name: folderName,
    type: 'upload',
    files: files
  });
};

const handleUserUploadCountUpdate = (count: number) => {
  // 此回調現在主要用於 DirectoryUploadBlock_Simplified 的內部邏輯
  // 主UI的計數通過 selectedSources 更新
};

const handleUserUploadErrorUpdate = (message: string | null) => {
  isLoadingCurrentAction.value = false; // 上傳操作因錯誤結束
  currentActionErrorMessage.value = message;
  if (message) {
      emit('source-error', message);
  } else {
      // 如果錯誤被清除，且沒有其他信息，則清除 currentActionInfoMessage
      if (!currentActionInfoMessage.value) resetCurrentActionState();
  }
};

const handlePredefinedThemeSelected = async (theme: PredefinedTheme) => {
  showPredefinedModal.value = false;
  isLoadingCurrentAction.value = true;
  resetCurrentActionState();
  currentActionInfoMessage.value = `正在載入主題 "${theme.name}" 的圖片...`;

  try {
    const filesArray: File[] = [];
    for (const img of theme.images) {
      try {
        const imageUrl = await img.urlLoader();
        const response = await fetch(imageUrl);
        if (!response.ok) { 
            console.warn(`無法載入圖片 ${img.name} 從 ${theme.name}: ${response.statusText}`); 
            continue; 
        }
        const blob = await response.blob();
        if (blob.size === 0) {
            console.warn(`圖片 ${img.name} 從 ${theme.name} 為空檔案，已跳過。`);
            continue;
        }
        filesArray.push(new File([blob], img.name, { type: blob.type || 'application/octet-stream' }));
      } catch (e) { console.warn(`載入圖片 ${img.name} 時發生錯誤:`, e); }
    }

    if (filesArray.length === 0) {
      currentActionErrorMessage.value = `主題 "${theme.name}" 中沒有找到有效的圖片，或載入失敗。`;
      emit('source-error', currentActionErrorMessage.value);
    } else {
      addSourceToList({
        id: `predefined-${theme.path}-${Date.now()}`,
        name: theme.name,
        type: 'predefined',
        files: filesArray
      });
    }
  } catch (error) {
    console.error(`載入預設主題 "${theme.name}" 圖片時發生錯誤:`, error);
    currentActionErrorMessage.value = `載入主題 "${theme.name}" 圖片失敗。請檢查網路連線或主控台輸出。`;
    emit('source-error', currentActionErrorMessage.value);
  } finally {
    isLoadingCurrentAction.value = false;
  }
};

const closePredefinedModal = () => {
    showPredefinedModal.value = false;
    // 如果關閉 modal 時沒有成功添加主題，並且之前有 "開啟主題選擇" 或 "正在載入主題" 的消息，則清除它們
    const themeRelatedMessageActive = currentActionInfoMessage.value?.startsWith("開啟主題選擇") || currentActionInfoMessage.value?.startsWith("正在載入主題");
    const noThemeAddedRecently = !selectedSources.value.some(s => s.id.startsWith('predefined') && (Date.now() - parseInt(s.id.split('-').pop() || '0')) < 2000);

    if (themeRelatedMessageActive && noThemeAddedRecently) {
       resetCurrentActionState();
    }
}

const removeSource = (sourceId: string) => {
  if (isLoadingCurrentAction.value) return;
  const sourceToRemove = selectedSources.value.find(s => s.id === sourceId);
  selectedSources.value = selectedSources.value.filter(s => s.id !== sourceId);
  if (sourceToRemove) {
      currentActionInfoMessage.value = `已移除素材集 "${sourceToRemove.name}"。`;
      currentActionErrorMessage.value = null; 
  }
  emitAllSelectedFiles();
};

const clearAllSources = () => {
  if (isLoadingCurrentAction.value) return;
  selectedSources.value = [];
  currentActionInfoMessage.value = "已清除所有已選素材集。";
  currentActionErrorMessage.value = null;
  emitAllSelectedFiles();
}

const emitAllSelectedFiles = () => {
  const allFiles = selectedSources.value.reduce((acc, source) => {
    acc.push(...source.files);
    return acc;
  }, [] as File[]);
  
  emit('files-selected', allFiles);
  
  // 更新父組件的整體錯誤狀態
  if (allFiles.length > 0) {
    emit('source-error', null); // 有文件，清除整體錯誤
  } else if (selectedSources.value.length === 0 && !currentActionErrorMessage.value) {
    // 如果列表為空，且沒有進行中的單項錯誤，也清除整體錯誤
    // 但父組件可能需要根據 allFiles.length === 0 來禁用生成按鈕
    emit('source-error', null); 
  } else if (currentActionErrorMessage.value) {
    // 如果有當前操作錯誤，則優先傳播此錯誤
    emit('source-error', currentActionErrorMessage.value);
  }
  // 如果 allFiles.length 是 0 但 selectedSources 不是 0 (例如都選了空資料夾)
  // 這種情況由 addSourceToList 或 handleUserUploadedFilesFromComponent 階段的錯誤處理
};

// --- computed properties ---
const mainButtonTextComputed = computed(() => {
  if (sourceMode.value === 'upload') {
    return '新增本機資料夾';
  } else { 
    return '從主題集新增';
  }
});

const totalSelectedFilesCount = computed(() => {
  return selectedSources.value.reduce((sum, source) => sum + source.files.length, 0);
});


// --- watchers ---
watch(sourceMode, () => {
  if (!isLoadingCurrentAction.value) {
    resetCurrentActionState();
  }
});

watch(selectedSources, (newVal, oldVal) => {
    // 當列表從有內容變為空（例如用戶清除了所有）
    if (oldVal.length > 0 && newVal.length === 0 && !isLoadingCurrentAction.value) {
        if (!currentActionInfoMessage.value?.includes("已清除所有")) { // 避免重複提示
             currentActionInfoMessage.value = "所有素材已被清除。";
        }
    }
    // 自動清除成功訊息，如果用戶開始了新的操作或切換模式
    // 此邏輯已部分移至 resetCurrentActionState 和 setSourceMode
}, { deep: true });

</script>

<style scoped lang="scss">
.image-source-selector-container {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fff;
  max-width: 700px;
  margin: 20px auto;
}

.main-title {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 8px;
  font-weight: 600;
}

.main-description {
  font-size: 1rem;
  color: #555;
  margin-bottom: 25px;
  line-height: 1.6;
}

.mode-switcher {
  display: flex;
  margin-bottom: 20px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #4A5568;

  .mode-button {
    flex-grow: 1;
    padding: 12px 15px;
    font-size: 1rem;
    font-weight: 500;
    color: #4A5568;
    background-color: #F7FAFC;
    border: none;
    cursor: pointer;
    transition: background-color 0.2s ease, color 0.2s ease, opacity 0.2s ease;
    text-align: center;

    &:not(:last-child) {
      border-right: 1px solid #4A5568;
    }

    &.active {
      background-color: #4A5568;
      color: white;
    }

    &:hover:not(.active):not(:disabled) {
      background-color: #E2E8F0;
    }
    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

.upload-area {
  padding: 25px;
  border: 2px dashed #CBD5E0;
  border-radius: 8px;
  text-align: center;
  background-color: #F8F9FA;
  transition: background-color 0.3s ease, border-color 0.3s ease;
  margin-bottom: 25px;

  &:hover:not(:disabled) { // Assuming parent might disable this area
    background-color: #EDF2F7;
    border-color: #A0AEC0;
  }
}

.action-button {
  padding: 12px 25px;
  font-size: 1.1rem;
  color: white;
  background-color: #4A5568; 
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, opacity 0.2s ease;
  font-weight: 500;
  margin-bottom: 15px;
  position: relative; // For spinner

  &:hover:not(:disabled) {
    background-color: #2D3748; 
    transform: translateY(-1px);
  }
  &:active:not(:disabled) {
    transform: translateY(0px);
  }
  &:disabled {
    background-color: #A0AEC0;
    cursor: not-allowed;
     transform: translateY(0px);
  }

  .spinner {
    display: inline-block;
    width: 1em;
    height: 1em;
    margin-left: 8px;
    border: 2px solid rgba(255,255,255,0.3);
    border-radius: 50%;
    border-top-color: #fff;
    animation: spin 1s ease-in-out infinite;
    vertical-align: middle;
  }
}
@keyframes spin {
  to { transform: rotate(360deg); }
}


.loading-info,
.info-message,
.error-message {
  margin-top: 15px;
  padding: 10px 15px; // Increased padding
  border-radius: 6px;  // Slightly more rounded
  font-size: 0.95rem;
  font-weight: 500;
  text-align: left; // Better for longer messages
}

.loading-info {
  color: #1A202C; // Darker text for better contrast
  background-color: #E2E8F0; // Light gray
  border: 1px solid #CBD5E0;
}

.info-message {
  color: #2F855A; // Green for success/info
  background-color: #C6F6D5;
  border: 1px solid #9AE6B4;
}
.info-message.placeholder-message { // Specific style for the placeholder
    color: #4A5568;
    background-color: #F7FAFC;
    border: 1px solid #E2E8F0;
    text-align: center;
}


.error-message {
  color: #C53030; // Red for error
  background-color: #FED7D7;
  border: 1px solid #FEB2B2;
}


.selected-sources-list {
  margin-top: 20px;
  padding: 20px; // Increased padding
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background-color: #f9fafb;

  .selected-sources-title {
    font-size: 1.2rem; // Slightly larger
    color: #1f2937;
    margin-top: 0;
    margin-bottom: 15px; // More space
    font-weight: 600;
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0 0 15px 0; // Space before clear all button
  }
}

.selected-source-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 10px; // Adjusted padding
  background-color: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  margin-bottom: 10px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);

  &:last-child {
    margin-bottom: 0;
  }

  .source-icon {
    margin-right: 10px;
    font-size: 1.2rem;
    color: #4A5568;
  }

  .source-name {
    flex-grow: 1;
    color: #374151;
    font-size: 1rem;
    font-weight: 500;
  }

  .remove-source-button {
    background: none;
    border: none;
    color: #9ca3af;
    font-size: 1.5rem; // Larger target
    font-weight: bold;
    cursor: pointer;
    padding: 0 5px; // Easier to click
    line-height: 1;
    transition: color 0.2s ease;

    &:hover:not(:disabled) {
      color: #ef4444; // Red on hover
    }
    &:disabled {
      color: #d1d5db;
      cursor: not-allowed;
    }
  }
}

.clear-all-button {
  display: block; // Make it full width or centered
  margin: 15px auto 0; // Centered if block
  padding: 10px 20px;
  font-size: 0.95rem;
  color: #DC2626; // Red color for destructive action
  background-color: #FEE2E2; // Light red background
  border: 1px solid #FCA5A5;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s ease, color 0.2s ease, opacity 0.2s ease;

  &:hover:not(:disabled) {
    background-color: #F87171; // Darker red on hover
    color: white;
  }
   &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

:deep(.directory-upload-simplified-core) {
  display: none;
}
</style>