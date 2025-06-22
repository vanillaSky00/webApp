<template>
  <div>
    <h2>OpenAPI Spec (Raw JSON)</h2>
    <pre>{{ json }}</pre>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const json = ref('Loading...')

onMounted(() => {
  console.log('[OpenApiViewer] mounted – about to fetch spec')

  fetch('http://localhost:8080/v3/api-docs')
    .then(res => res.json())
    .then(data => {
      json.value = JSON.stringify(data, null, 2)
    })
    .catch(err => {
      json.value = `Error: ${err.message}`
    })
})
</script>
