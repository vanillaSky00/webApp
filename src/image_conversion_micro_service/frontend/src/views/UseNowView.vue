<template>
  <div class="use-now-page">
    <!-- 原本的標題和副標題 -->
    <h1 class="page-title main-header-title">製作你的照片馬賽克</h1>
    <p class="page-subtitle main-header-subtitle">將你的照片集變成獨一無二的藝術品！</p>

    <!-- 新增的步驟導航元件 -->
    <PageStepper @navigate="scrollToSection" />

    <!-- 步驟 1 區塊 (風格圖片) -->
    <section class="upload-section" id="style-image-section">
      <StyleImageUploader v-model="styleImageFile" @update:model-value="onFileSelectionChange" />
    </section>

    <!-- 步驟 2 區塊 (素材圖片) -->
    <section class="upload-section" id="source-images-section">
      <ImageSourceSelector 
        @files-selected="handleFolderFilesSelected"
        @source-error="handleSourceSelectorError" 
      />
    </section>

    <!-- 步驟 3 區塊 (生成按鈕和結果) -->
    <div id="generation-section">
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
        {{ isGenerating ? '生成中...' : '開始生成馬賽克' }}
      </MosaicGenerateButton>

      <div v-if="isGenerating" class="progress-area">
        <p>{{ generationProgress < 100 ? '圖片正在努力生成中，請稍候...' : '正在處理圖片，準備顯示...' }}</p>
        <div class="progress-bar-container">
          <div class="progress-bar" :style="{ width: generationProgress + '%' }">
            {{ generationProgress }}%
          </div>
        </div>
      </div>

      <MosaicResultDisplay
        v-if="finalImageUrl && !isGenerating"
        :image-url="finalImageUrl"
        :download-file-name="downloadFileName"
      />
    </div>

    <div v-if="errorMessage" class="error-message">
      錯誤：{{ errorMessage }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import ImageSourceSelector from '../components/ImageSourceSelector.vue';
import StyleImageUploader from '../components/StyleImageUploader.vue';
import MosaicResultDisplay from '../components/MosaicResultDisplay.vue';
import MosaicGenerateButton from '../components/MosaicGenerateButton.vue';
import PageStepper from '../components/PageStepper.vue';

const styleImageFile = ref<File | null>(null);
const folderImageFiles = ref<File[]>([]);

const isGenerating = ref(false);
const generationProgress = ref(0);
const finalImageUrl = ref<string | null>(null);
const errorMessage = ref<string | null>(null);

const backendConfig = {
  baseUrl: 'http://localhost:8080/api/images/process',
  mosaicOperation: 'mosaic'
};

const canGenerate = computed(() => {
  return styleImageFile.value !== null && folderImageFiles.value.length > 0;
});

const downloadFileName = computed(() => {
  const prefix = styleImageFile.value ? styleImageFile.value.name.split('.')[0] : 'mosaic';
  return `${prefix}_photomosaic_${Date.now()}.png`;
});

const onFileSelectionChange = () => {
  if (finalImageUrl.value) {
    // 如果之前有 Blob URL，將其釋放以避免記憶體洩漏
    URL.revokeObjectURL(finalImageUrl.value);
    finalImageUrl.value = null;
  }
  generationProgress.value = 0;
  if (errorMessage.value && !errorMessage.value.startsWith('素材選擇錯誤:')) {
     errorMessage.value = null;
  }
  if (isGenerating.value) {
    isGenerating.value = false;
  }
};

const handleFolderFilesSelected = (files: File[]) => {
  folderImageFiles.value = files;
  if (errorMessage.value && errorMessage.value.startsWith('素材選擇錯誤:')) {
      errorMessage.value = null;
  }
  onFileSelectionChange(); // 這裡會重置圖片相關狀態
};

const handleSourceSelectorError = (message: string | null) => {
    if (message) {
        errorMessage.value = `素材選擇錯誤: ${message}`;
        folderImageFiles.value = [];
    } else {
        if (errorMessage.value && errorMessage.value.startsWith('素材選擇錯誤:')) {
            errorMessage.value = null;
        }
    }
};

function handleBusyChange(isChildBusy: boolean) {
  if (isChildBusy) {
    isGenerating.value = true;
    if (finalImageUrl.value) {
        URL.revokeObjectURL(finalImageUrl.value);
        finalImageUrl.value = null;
    }
    if (errorMessage.value && !errorMessage.value.startsWith('素材選擇錯誤:')) {
        errorMessage.value = null;
    }
  }
}

// --- 主要修改點 ---
async function handleGenerationSuccess(payload: { imageUrl: string; message?: string }) {
  console.log('後端成功回傳，原始圖片 URL:', payload.imageUrl);
  if (payload.message) console.log('後端訊息:', payload.message);

  // 雖然進度條已到 100，但前端仍需下載和處理圖片，所以保持 isGenerating 狀態
  // isGenerating.value 保持為 true

  try {
    // 1. 使用 fetch 請求後端提供的圖片 URL
    const response = await fetch(payload.imageUrl);
    
    if (!response.ok) {
      throw new Error(`無法從伺服器獲取圖片資料，狀態: ${response.status} ${response.statusText}`);
    }

    // 2. 將回應轉換成一個 Blob (Binary Large Object)
    const imageBlob = await response.blob();

    // 3. 為這個 Blob 物件創建一個在瀏覽器內存中唯一的、臨時的 URL
    const blobUrl = URL.createObjectURL(imageBlob);

    // 4. 將這個絕對有效的 blobUrl 用於顯示和下載
    finalImageUrl.value = blobUrl;
    errorMessage.value = null; // 清除之前的任何錯誤

    // 當 Vue 元件卸載時，自動釋放 Blob URL 以節省記憶體
    // 這部分可以放在 onUnmounted 鉤子中，但對於單頁應用，
    // 在下次生成或選擇新檔案時釋放更為實際 (已在 onFileSelectionChange 中實現)

  } catch (error: any) {
    console.error('在前端處理和獲取圖片時發生錯誤:', error);
    handleGenerationError(`無法處理生成的圖片: ${error.message}`);
  } finally {
    // 處理完畢，無論成功或失敗，都結束 isGenerating 狀態
    isGenerating.value = false;
    // 將進度條重置，以便下次使用
    generationProgress.value = 0;
  }
}

function handleGenerationError(message: string) {
  console.error('生成過程中發生錯誤:', message);
  errorMessage.value = message;
  if (finalImageUrl.value) {
    URL.revokeObjectURL(finalImageUrl.value);
    finalImageUrl.value = null;
  }
  isGenerating.value = false;
  generationProgress.value = 0;
}

function updateGenerationProgress(progress: number) {
  generationProgress.value = progress;
  if (progress > 0 && progress < 100) {
    if (!isGenerating.value) isGenerating.value = true;
  }
  // 移除舊的基於 setTimeout 的邏輯，因為現在由 handleGenerationSuccess 來控制結束狀態
}

// 滾動到指定區塊的方法
const scrollToSection = (sectionId: string) => {
  const element = document.getElementById(sectionId);
  if (element) {
    const offset = 90; 
    const bodyRect = document.body.getBoundingClientRect().top;
    const elementRect = element.getBoundingClientRect().top;
    const elementPosition = elementRect - bodyRect;
    const offsetPosition = elementPosition - offset;

    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    });
  } else {
    console.warn(`Section with ID "${sectionId}" not found.`);
  }
};
</script>

<style scoped lang="scss">
.use-now-page {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  padding-top: 30px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background-color: #fdfdfd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.main-header-title {
  text-align: center;
  font-size: 2.4rem;
  color: #2c3e50;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.main-header-subtitle {
  text-align: center;
  font-size: 1.15rem;
  color: #555;
  margin-bottom: 1.5rem;
}

.upload-section {
  margin-bottom: 3rem;
  padding: 25px;
  background-color: #ffffff;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.04);
}

#generation-section {
  margin-bottom: 2rem;
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