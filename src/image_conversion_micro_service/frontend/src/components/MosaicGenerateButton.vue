<!-- components/MosaicGenerateButton.vue -->
<template>
  <button
    @click="handleGenerate"
    :disabled="!canGenerateInternal || busy"
    class="generate-button"
  >
    <slot>
      {{ busy ? '生成中...' : '開始生成馬賽克' }}
    </slot>
  </button>
</template>

<script setup lang="ts">
import { ref, computed} from 'vue';
import type { PropType } from 'vue';
import axios from 'axios';

interface BackendSuccessResponse {
  success: boolean;
  imageUrl: string; // 假設成功時還是返回 imageUrl
  message?: string;
}

interface BackendErrorResponse {
  message: string;
}



const props = defineProps({
  styleImage: {
    type: Object as PropType<File | null>,
    required: true,
  },
  sourceImages: {
    type: Array as PropType<File[]>,
    required: true,
  },
  /**
   * The base URL for the image processing API.
   * Example: 'http://localhost:8080/api/home/images/process'
   */
  apiUrl: {
    type: String,
    required: true, // 讓父組件必須傳遞這個
  },
  /**
   * The operation to perform. This will be sent as a query parameter.
   * Example: 'generateMosaic', 'compress', 'decompress'
   */
  operation: {
    type: String,
    required: true, // 讓父組件必須傳遞操作類型
  },
  disabled: {
    type: Boolean,
    default: false,
  }
});

const emit = defineEmits<{
  (e: 'success', payload: { imageUrl: string; message?: string }): void;
  (e: 'error', message: string): void;
  (e: 'progress', percentage: number): void;
  (e: 'busy-change', isBusy: boolean): void;
}>();

const busy = ref(false);

const canGenerateInternal = computed(() => {
  return props.styleImage !== null && props.sourceImages.length > 0 && !props.disabled;
});

async function handleGenerate() {
  if (!canGenerateInternal.value || busy.value) return;

  busy.value = true;
  emit('busy-change', true);
  emit('progress', 0);

  const formData = new FormData();
  if (props.styleImage) {
    formData.append('image', props.styleImage, props.styleImage.name); // 後端可能期望的字段名
  }
  // 假設後端期望素材圖片的字段名也是 'sourceImages' 或類似
  // 如果後端像 ImageUploadButton 那樣只期望一個 'image' 字段，這裡需要調整
  props.sourceImages.forEach((file, index) => {
    // 你可能需要根據後端期望的格式來命名這些文件
    // 例如，如果後端可以處理多個名為 'sourceImages' 的文件：
    formData.append('image', file, file.name);
    // 或者如果需要不同的鍵名：
    // formData.append(`sourceImage[${index}]`, file, file.name);
  });

  let currentProgress = 0;

  try {
    console.log(`[MosaicGenerateButton] Sending to ${props.apiUrl} with op=${props.operation}`);
    const response = await axios.post<BackendSuccessResponse>(
      props.apiUrl, // 基礎 URL
      formData,
      {
        params: { // 這裡添加查詢參數
          op: props.operation
        },
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        responseType: 'json',
        onUploadProgress: progressEvent => {
          if (progressEvent.total) {
            const percentCompleted = Math.round((progressEvent.loaded * 90) / progressEvent.total);
            if (percentCompleted > currentProgress) {
              currentProgress = percentCompleted;
              emit('progress', currentProgress);
            }
          }
        }
      }
    );

    const result = response.data;
    if (result.success && result.imageUrl) {
      emit('progress', 100);
      emit('success', { imageUrl: result.imageUrl, message: result.message });
    } else {
      throw new Error(result.message || '後端操作未成功但未返回明確錯誤信息。');
    }

  } catch (err: any) {
    // ... (錯誤處理邏輯保持不變) ...
    console.error('[MosaicGenerateButton] Error:', err);
    let errorMessage = '生成馬賽克時發生未知錯誤。';
    if (axios.isAxiosError(err)) {
      if (err.response) {
        const errorData = err.response.data as BackendErrorResponse | { message?: string };
        errorMessage = errorData?.message || `伺服器錯誤: ${err.response.status} ${err.response.statusText}`;
      } else if (err.request) {
        errorMessage = '伺服器無響應，請檢查網絡或伺服器狀態。';
      } else {
        errorMessage = `請求設置錯誤: ${err.message}`;
      }
    } else if (err instanceof Error) {
        errorMessage = err.message;
    }
    emit('error', errorMessage);
    emit('progress', 0);
  } finally {
    busy.value = false;
    emit('busy-change', false);
  }
}
</script>

<style scoped lang="scss">
/* 樣式保持不變 */
.generate-button {
  display: block;
  width: auto;
  min-width: 220px;
  margin: 2.5rem auto;
  background-color: #2ecc71;
  font-size: 1.2rem;
  padding: 16px 35px;
  font-weight: 500;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  user-select: none;

  &:hover:not(:disabled) {
    background-color: #27ae60;
    box-shadow: 0 3px 8px rgba(0,0,0,0.1);
    transform: translateY(-1px);
  }
  &:active:not(:disabled) {
    transform: translateY(0px);
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  }
  &:disabled {
    background-color: #bdc3c7;
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
  }
}
</style>
