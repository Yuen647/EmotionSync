<template>
  <div class="background">
    <!-- 显示 EmotionSelect 组件 -->
    <EmotionSelect
        v-if="isEmotionSelectVisible"
        :isVisible="isEmotionSelectVisible"
        @emotionSelected="handleEmotionSelected"
    />

    <!-- 显示 RecommandSound 组件 -->
    <RecommandSound
        v-if="!isEmotionSelectVisible && !isPopupVisible"
        :selectedEmotion="selectedEmotion"
        :recommendations="recommendations"
        @openPopup="openPopup"
        @openEmotion="openEmotion"
    />

    <!-- 显示 Popup 组件 -->
    <Popup v-if="isPopupVisible && !isEmotionSelectVisible"
           :selectedEmotion="selectedEmotion"
           @openEmotion="openEmotion"
    />
  </div>
</template>

<script setup>
import { ref } from "vue";
import EmotionSelect from "./EmotionSelect.vue";
import Popup from "./Popup.vue";
import RecommandSound from "./RecommandSound.vue";

// 控制 EmotionSelect 的显示状态
const isEmotionSelectVisible = ref(true);
// 存储用户选择的情绪
const selectedEmotion = ref("");
// 存储推荐的音频列表
const recommendations = ref([]);
// 控制 Popup 的显示状态
const isPopupVisible = ref(false);
// 用户选择情绪后的处理
function handleEmotionSelected({ emotion, recommendedAudios }) {
  console.log(emotion);
  selectedEmotion.value = emotion; // 保存选择的情绪
  isEmotionSelectVisible.value = false;
  recommendations.value = recommendedAudios;
}

// 打开 Popup 的函数
function openPopup(emotion) {
  isPopupVisible.value = true;
  selectedEmotion.value = emotion; // 保存选择的情绪
}

// 返回 EmotionSelect 组件
function openEmotion() {
  isEmotionSelectVisible.value = true;
  selectedEmotion.value = ""; // 清空情绪选择
  recommendations.value = []; // 清空推荐音频
}
</script>

<style scoped>
/* 样式 */
.background {
  position: absolute;
  top: 80px;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-image: url('@/assets/background.gif'); /* 设置背景图片 */
  background-size: cover; /* 背景图覆盖整个页面 */
  background-position: center; /* 居中背景图 */
  z-index: 1; /* 确保背景在底层 */
}
</style>

