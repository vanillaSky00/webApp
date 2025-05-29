<template>
  <div class="use-now-page">
    <h1 class="page-title">製作你的照片馬賽克</h1>
    <p class="page-subtitle">將你的照片集變成獨一無二的藝術品！</p>

    <section class="upload-section">
      <StyleImageUploader v-model="styleImageFile" @update:model-value="onFileSelectionChange" />
    </section>

    <section class="upload-section">
      <h2 class="section-title">步驟 2：上傳你的素材圖片資料夾</h2>
      <p class="section-description">
        選擇一個包含多張小圖片的資料夾，這些圖片將被用來填充你的馬賽克。系統會自動過濾非圖片檔案。
      </p>
      <DirectoryUploadBlock_Simplified @files-selected="handleFolderFilesSelected" />
    </section>

    <!-- 使用 MosaicGenerateButton 組件 -->
    <MosaicGenerateButton
      :style-image="styleImageFile"
      :source-images="folderImageFiles"
      :api-url="backendConfig.baseUrl"
      :operation="backendConfig.mosaicOperation"
      :disabled="!canGenerate || isGenerating"
      @success="handleGenerationSuccess"
      @error="handleGenerationError"
      @progress="updateGenerationProgress"
      @busy-change="handleBusyChange"
    >
      <!-- 按鈕文本根據 isGenerating 動態變化 (通過 slot 傳入) -->
      {{ isGenerating ? '生成中...' : '開始生成馬賽克' }}
    </MosaicGenerateButton>

    <!-- 進度條區域 -->
    <div v-if="isGenerating" class="progress-area">
      <p>圖片正在努力生成中，請稍候...</p>
      <div class="progress-bar-container">
        <div class="progress-bar" :style="{ width: generationProgress + '%' }">
          {{ generationProgress }}%
        </div>
      </div>
    </div>

    <!-- 最終圖片展示區域 -->
    <MosaicResultDisplay
      v-if="finalImageUrl && !isGenerating"
      :image-url="finalImageUrl"
      :download-file-name="downloadFileName"
    />

    <!-- 錯誤信息顯示區域 -->
    <div v-if="errorMessage" class="error-message">
      錯誤：{{ errorMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import DirectoryUploadBlock_Simplified from '../components/DirectoryUploadBlock_Simplified.vue';
import StyleImageUploader from '../components/StyleImageUploader.vue';
import MosaicResultDisplay from '../components/MosaicResultDisplay.vue';
import MosaicGenerateButton from '../components/MosaicGenerateButton.vue';

const styleImageFile = ref<File | null>(null);
const folderImageFiles = ref<File[]>([]);

const isGenerating = ref(false); // 控制整體生成狀態，影響UI如進度條、按鈕文本
const generationProgress = ref(0);
const finalImageUrl = ref<string | null>(null);
const errorMessage = ref<string | null>(null);

const backendConfig = {
  baseUrl: 'https://localhost:8080/api/images/process',
  mosaicOperation: 'mosaic'
};

// 計算屬性，用於判斷是否滿足生成條件
const canGenerate = computed(() => {
  return styleImageFile.value !== null && folderImageFiles.value.length > 0;
});

const downloadFileName = computed(() => {
  const prefix = styleImageFile.value ? styleImageFile.value.name.split('.')[0] : 'mosaic';
  return `${prefix}_photomosaic_${Date.now()}.png`;
});

// 當文件選擇變化時，清除舊的結果和錯誤
const onFileSelectionChange = () => {
  finalImageUrl.value = null;
  errorMessage.value = null;
  generationProgress.value = 0; // 重置進度
  if (isGenerating.value) { // 如果正在生成時用戶更改了文件，理論上應該取消之前的操作，這裡簡化處理
      isGenerating.value = false;
  }
};

const handleFolderFilesSelected = (files: File[]) => {
  folderImageFiles.value = files;
  if (files.length > 0) {
    onFileSelectionChange(); // 調用通用清理邏輯
  }
};

// 由 MosaicGenerateButton 的 @busy-change 事件觸發
function handleBusyChange(isChildBusy: boolean) {
  // isGenerating 也應該反映子組件的忙碌狀態
  // 但要注意，子組件完成後 isChildBusy 會變 false，此時 isGenerating 應該等 progress 到 100 或出錯
  // 這裡的 isGenerating 主要用於控制進度條顯示和按鈕文本
  if (isChildBusy) {
      isGenerating.value = true; // 子組件開始忙碌，父組件也進入生成狀態
      finalImageUrl.value = null; // 清除上一次的結果
      errorMessage.value = null; // 清除上一次的錯誤
  }
  // isGenerating 變為 false 的邏輯主要在 success 和 error 處理器中
}

function handleGenerationSuccess(payload: { imageUrl: string; message?: string }) {
  console.log('Generation successful:', payload.imageUrl);
  finalImageUrl.value = payload.imageUrl;
  errorMessage.value = null;
  // isGenerating.value = false; // 將在 progress = 100 時或由 busy-change 的 false 觸發
  // generationProgress.value = 100; // 已由 progress 事件處理
  if (payload.message) console.log('Backend message:', payload.message);
}

function handleGenerationError(message: string) {
  console.error('Generation error:', message);
  errorMessage.value = message;
  finalImageUrl.value = null;
  isGenerating.value = false; // 出錯時，明確結束生成狀態
  generationProgress.value = 0;
}

function updateGenerationProgress(progress: number) {
  generationProgress.value = progress;
  if (progress > 0 && progress < 100) {
    if (!isGenerating.value) isGenerating.value = true; // 確保進度條可見
  } else if (progress === 100 && !errorMessage.value) { // 成功完成
    // 延遲一點設置 isGenerating = false，讓進度條動畫播完
    setTimeout(() => {
        if (generationProgress.value === 100 && !errorMessage.value) { // 再次檢查，防止狀態競爭
             isGenerating.value = false;
        }
    }, 400); // 略長於進度條的 transition duration
  } else if (progress === 0 && !isGenerating.value && !errorMessage.value) {
    // 初始狀態或重置
  }
  // isGenerating 在出錯時由 handleGenerationError 設置為 false
}

</script>

<style scoped lang="scss">
/* 樣式與之前版本保持一致，不需要修改 */
.use-now-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  padding-top: 90px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background-color: #fdfdfd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.page-title {
  text-align: center;
  font-size: 2.4rem;
  color: #2c3e50;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.page-subtitle {
  text-align: center;
  font-size: 1.15rem;
  color: #555;
  margin-bottom: 3rem;
}

.upload-section {
  margin-bottom: 3rem;
  padding: 25px;
  background-color: #ffffff;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}

.upload-section > .section-title {
  font-size: 1.6rem;
  color: #34495e;
  margin-top: 0;
  margin-bottom: 1rem;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 0.75rem;
  font-weight: 500;
}

.upload-section > .section-description {
  font-size: 1rem;
  color: #606060;
  line-height: 1.7;
  margin-bottom: 1.8rem;
}

.progress-area {
  margin: 2.5rem 0;
  text-align: center;
  p {
    margin-bottom: 1rem;
    color: #555;
    font-size: 1.05rem;
  }
}

.progress-bar-container {
  width: 100%;
  background-color: #ecf0f1;
  border-radius: 6px;
  overflow: hidden;
  height: 2.2rem;
  display: flex;
  align-items: center;
  box-shadow: inset 0 1px 3px rgba(0,0,0,0.1);
}

.progress-bar {
  height: 100%;
  background-image: linear-gradient(45deg, #3498db, #2980b9);
  width: 0%;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.95rem;
  font-weight: 600;
  transition: width 0.4s cubic-bezier(0.25, 0.1, 0.25, 1);
  text-shadow: 0 1px 1px rgba(0,0,0,0.2);
}

.error-message {
  margin-top: 2rem;
  color: #c0392b;
  background-color: #fdedec;
  border: 1px solid #e74c3c;
  padding: 12px 18px;
  border-radius: 6px;
  text-align: center;
  font-weight: 500;
}
</style>
