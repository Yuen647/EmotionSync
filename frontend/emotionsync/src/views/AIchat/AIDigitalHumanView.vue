<template>
  <div class="ai-digital-human-container">
    <iframe 
      src="http://localhost:6006/" 
      class="ai-digital-human-frame"
      frameborder="0"
      allow="microphone; camera"
    ></iframe>
  </div>
</template>

<script setup lang="ts">
// No additional setup needed
</script>

<style scoped>
.ai-digital-human-container {
  width: 100%;
  height: calc(100vh - 64px); /* Subtract header height */
  position: relative;
}

.ai-digital-human-frame {
  width: 100%;
  height: 100%;
  border: none;
}
</style> 