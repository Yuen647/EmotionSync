<template>
  <div v-if="isVisible" class="popup-content">
    <div class="popup-header">
      <div class="left">
        <span>请选择您当前情绪</span>
      </div>
    </div>
    <!-- 按钮区 -->
    <div class="button-grid">
      <div
          v-for="(button, index) in buttons"
          :key="index"
          class="square-button"
          @click="selectEmotion(button.emotion)"
      >
        <img :src="button.icon" alt="Icon" />
        <span>{{ button.text }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import {defineProps, defineEmits, ref} from "vue";
import axios from 'axios';

import happyIcon from '@/assets/happy.png';
import sadIcon from '@/assets/sad.png';
import angryIcon from '@/assets/angry.png';
import fearIcon from '@/assets/fear.png';
import anxiousIcon from '@/assets/anxious.png';
import excitedIcon from '@/assets/excited.png';
import worryIcon from '@/assets/worry.png';
import tiredIcon from '@/assets/tired.png';
import relaxedIcon from '@/assets/relaxed.png';
// 接收来自父组件的 `isVisible` 属性
defineProps({
  isVisible: Boolean,
});

// 触发选择情绪的事件
const emit = defineEmits(["emotionSelected"]);

// 可选的情绪列表
const buttons = ref([
  { icon: happyIcon, text: 'Happy', emotion: 'happy' },
  { icon: sadIcon, text: 'Sad', emotion: 'sad' },
  { icon: angryIcon, text: 'Angry', emotion: 'angry' },
  { icon: fearIcon, text: 'Fear', emotion: 'fear' },
  { icon: anxiousIcon, text: 'Anxious', emotion: 'anxious' },
  { icon: excitedIcon, text: 'Excited', emotion: 'excited' },
  { icon: worryIcon, text: 'Worry', emotion: 'worry' },
  { icon: tiredIcon, text: 'Tired', emotion: 'tired' },
  { icon: relaxedIcon, text: 'Relaxed', emotion: 'relaxed' },
]);

// 用户选择情绪
function selectEmotion(emotion) {
  //emit("emotionSelected", emotion); // 将选择的情绪传递给父组件

  // 发送情绪数据到后端
  axios.post('http://localhost:9000/api/emotions/recommend', {
    emotion,
    numRecommendations: 2, // 假设推荐 2 个音频
  })
      .then(response => {
        console.log('Emotion sent successfully:', response.data);
        // 从后端返回的数据中提取推荐音频列表
        const recommendedAudios = response.data;
        console.log(recommendedAudios);
        // 将推荐音频数据传递给父组件
        emit("emotionSelected", { emotion, recommendedAudios });
      })
      .catch(error => {
        console.error('Error sending emotion:', error);
      });
}
</script>

<style scoped>

/* 弹窗内容始终居中 */
.popup-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%); /* 使弹窗居中 */
  background-color: rgba(240, 240, 240, 0.9); /* 浅灰色背景，带透明度 */
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 2; /* 确保弹窗在背景之上 */
}


/* 按钮网格 */
.button-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 每行 3 个按钮 */
  gap: 10px;
  justify-items: center;
  margin-top: 70px;
}

.popup-header {
  display: flex;
  justify-content: center; /* 主轴居中 */
  align-items: center; /* 交叉轴居中 */
  padding: 10px 10px;
  background-color: rgba(240, 240, 240, 0.1);
  color: black;
  border-radius: 8px;
  position: relative; /* 为右侧内容留出空间 */
}

.left {
  display: flex;
  align-items: center;
  justify-content: center; /* 子元素水平居中 */
}



.button-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 每行 3 个按钮 */
  gap: 10px;
  justify-items: center;
  margin-top: 70px;
}

/* 按钮样式 */
.square-button {
  width: 150px;
  height: 150px;
  background-color: rgba(240, 240, 240, 0); /* 与弹窗背景同色 */
  border: 2px solid rgba(240, 240, 240, 0); /* 边框同色 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.square-button:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2); /* 悬停时添加阴影 */
}

.square-button img {
  width: 50px;
  height: 50px;
  margin-bottom: 8px;
}
</style>
