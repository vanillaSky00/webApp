<!-- ImageUploadButton.vue -->
<template>
  <button
      :disabled="busy"
      @click="selectFile"
      class="iu-btn"
  >
    <slot>
      <!-- default button label -->
      {{ busy ? 'Uploading…' : 'Compress' }}
    </slot>
    <input
        ref="hiddenInput"
        type="file"
        accept="image/*"
        style="display:none"
        @change="handleFile"
    />
  </button>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const props = defineProps({
  /** compress | decompress (default compress) */
  op: { type: String, default: 'compress' },
  /** override backend URL if needed */
  url: { type: String, default: '/api/home/images/process' }
})

/* ---- emits ---- */
const emit = defineEmits(['success', 'error'])

/* ---- refs ---- */
const hiddenInput = ref(null)
const busy        = ref(false)

/* ---- helpers ---- */
function selectFile () {
  if (!busy.value) hiddenInput.value.click()
}

async function handleFile (e) {
  const file = e.target.files[0]
  if (!file) return
  busy.value = true

  try {
    const form = new FormData()
    form.append('image', file)

    const res = await axios.post(
        props.url, form,
        {
          params: { op: props.op },
          headers: { 'Content-Type': 'multipart/form-data' },
          responseType: 'blob'
        }
    )

    // --- create a download link ---
    const blob = new Blob([res.data])
    const url  = URL.createObjectURL(blob)

    // derive filename from header or fallback
    let name = 'processed_' + file.name
    const cd = res.headers['content-disposition']
    if (cd) {
      const m = cd.match(/filename="?([^"]+)"?/)
      if (m) name = m[1]
    }

    const a = document.createElement('a')
    a.href = url
    a.download = name
    document.body.appendChild(a)
    a.click()
    a.remove()
    URL.revokeObjectURL(url)

    emit('success', name)
  } catch (err) {
    console.error(err)
    emit('error', err)
  } finally {
    busy.value = false
    e.target.value = ''        // reset input so the same file can be re-chosen
  }
}
</script>

<style scoped>
.iu-btn {
  padding: 0.5rem 1rem;
  background: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.iu-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
