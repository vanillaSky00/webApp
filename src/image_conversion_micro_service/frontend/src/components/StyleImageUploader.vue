<template>
  <div class="style-image-uploader">
    <h2 class="section-title">步驟 1：選擇你的風格圖片 (單張)</h2>
    <p class="section-description">
      這張圖片將決定馬賽克的整體外觀和顏色風格。
    </p>
    <div class="single-image-upload-area">
      <button @click="triggerStyleImageSelect" class="select-button">
        {{ localFile ? `已選擇: ${localFile.name}` : '選擇風格圖片' }}
      </button>
      <input
        type="file"
        ref="styleImageInputRef"
        @change="handleStyleImageSelected"
        style="display: none;"
        accept="image/jpeg, image/png, image/webp"
      />
      <div v-if="previewUrl" class="image-preview-container">
        <img :src="previewUrl" alt="風格圖片預覽" class="image-preview single-preview">
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted, watch } from 'vue';

// Props (v-model pattern for the file object)
// For Vue 3.3+ you might use defineModel for simpler v-model.
// Here, we use props and emits for broader compatibility.
const props = defineProps<{
  modelValue: File | null; // The selected file object
}>();

const emit = defineEmits(['update:modelValue']);

const styleImageInputRef = ref<HTMLInputElement | null>(null);
const previewUrl = ref<string | null>(null);
const localFile = ref<File | null>(props.modelValue); // Internal copy for display purposes

watch(() => props.modelValue, (newFile) => {
  localFile.value = newFile;
  if (newFile) {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value);
    }
    previewUrl.value = URL.createObjectURL(newFile);
  } else {
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value);
      previewUrl.value = null;
    }
  }
});

const triggerStyleImageSelect = () => {
  styleImageInputRef.value?.click();
};

const handleStyleImageSelected = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const file = target.files[0];
    localFile.value = file;
    emit('update:modelValue', file); // Update parent's styleImageFile

    // Clean up old preview URL and create new one
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value);
    }
    previewUrl.value = URL.createObjectURL(file);

  } else {
    localFile.value = null;
    emit('update:modelValue', null);
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value);
      previewUrl.value = null;
    }
  }
  // Clear the input value so that selecting the same file again still triggers 'change'
  if (target) {
    target.value = '';
  }
};

onUnmounted(() => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value);
  }
});
</script>

<style scoped lang="scss">
/* 複製 UseNowView.vue 中與步驟 1 相關的樣式 */
.section-title {
  font-size: 1.6rem;
  color: #34495e;
  margin-top: 0;
  margin-bottom: 1rem;
  border-bottom: 2px solid #ecf0f1;
  padding-bottom: 0.75rem;
  font-weight: 500;
}

.section-description {
  font-size: 1rem;
  color: #606060;
  line-height: 1.7;
  margin-bottom: 1.8rem;
}

.select-button {
  display: inline-block;
  padding: 12px 28px;
  font-size: 1.05rem;
  font-weight: 500;
  color: white;
  background-color: #3498db;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
  user-select: none;

  &:hover {
    background-color: #2980b9;
    box-shadow: 0 3px 8px rgba(0,0,0,0.1);
    transform: translateY(-1px);
  }
  &:active {
    transform: translateY(0px);
    box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  }
}

.single-image-upload-area {
  text-align: center;
  padding-top: 10px;
}

.image-preview-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}
.image-preview {
  max-width: 250px;
  max-height: 250px;
  border: 2px dashed #bdc3c7;
  border-radius: 6px;
  object-fit: contain;
  padding: 5px;
  background-color: #f9f9f9;
}
</style>