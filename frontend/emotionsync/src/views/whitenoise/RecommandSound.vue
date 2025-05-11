<template>
  <!--
  <button class="back-emotion" @click="backToEmotion">
    <span>返回情绪选择界面</span>
  </button>
  <button class="pop-up" @click="popup(selectedEmotion)">
    <span>前往完整白噪声界面</span>
  </button>
  <button class="back-button" @click="backToMain">
    <span>返回主页</span>
  </button>
  -->
  <!-- 悬浮按钮组 -->
  <div class="fab-container">
    <button class="fab-main" @click="toggleFab">
      ☰
    </button>

    <transition-group name="fab" tag="div">
      <button
          v-if="fabOpen"
          key="emotion"
          class="fab-sub"
          @click="backToEmotion"
      >
        🎭 情绪
      </button>
      <button
          v-if="fabOpen"
          key="popup"
          class="fab-sub"
          @click="popup(selectedEmotion)"
      >
        🌐 白噪声
      </button>
      <button
          v-if="fabOpen"
          key="home"
          class="fab-sub"
          @click="backToMain"
      >
        🏠 主页
      </button>
    </transition-group>
  </div>

  <div class="popup-content">
    <!-- 显示选定情绪 -->
    <h6 class="selected-emotion-title">
      您选择的情绪是：<span class="emotion-highlight">{{ selectedEmotion }}</span>
    </h6>
    <div class="popup-header">
      <div class="left">
        <span>根据选择的情绪，给您推荐以下白噪声:</span>
      </div>
    </div>

    <!-- 按钮区 -->
    <div class="button-grid" v-if="recommendations && recommendations.length > 0">
      <div
          v-for="audio in recommendations"
          :key="audio.audioName"
          class="square-button"
          @click="playOrPause(audio.audioSrc, audio.audioName)"
      >
        <img :src="audio.audioIcon" alt="Icon" />
        <span>{{ audio.audioName }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue';
import { defineProps, defineEmits } from 'vue';
import { useUserStore } from '@/store/userStore';
import {useRouter} from "vue-router";
import axios from 'axios';

const userStore = useUserStore();
const router = useRouter();
const currentAudioName = ref(''); // 保存当前播放音频的名称


const props = defineProps({
  selectedEmotion: String,
  recommendations: {
    type: Array,
    default: () => [], // 默认空数组
  }

});

// 你可以像这样直接访问 `props.selectedEmotion`，或者使用解构
const { selectedEmotion } = props; // 这样你可以直接使用 selectedEmotion 变量
// 定义要发送给父组件的事件
const emit = defineEmits(['openPopup', 'openEmotion']);

// 组件卸载前停止播放
onBeforeUnmount(() => {
  if(audioPlayer){
    backToController();
    stopAudio();
    stopTimer();
  }
});

const fabOpen = ref(false);
function toggleFab() {
  fabOpen.value = !fabOpen.value;
}


function playOrPause(audioUrl: string, audioName: string) {
  currentAudioName.value = audioName; // 更新当前音频名称
  playAudio(audioUrl); // 调用前面定义的 playAudio 方法
}

let audioPlayer: HTMLAudioElement | null = null;
const playDuration = ref(0); // 播放时长（秒）
let timer: number | null = null; // 计时器
let audio_name = "rain";

function playAudio(audioUrl: string) {
  if (!audioPlayer) { // 初次播放
    audioPlayer = new Audio(audioUrl);
    if(audio_name != null && audio_name != currentAudioName.value) {
      backToController()
    }
    audio_name = currentAudioName.value
    console.log(audio_name)
    audioPlayer.loop = true;
    audioPlayer.play()
        .then(() => startTimer())
        .catch((error) => console.error("播放音频失败:", error));
  } else if (audioPlayer.src.includes(audioUrl)) { // 当前播放器已存在，并且播放的是同一个音频
    if (audioPlayer.paused) {
      audioPlayer.play()
          .then(() => startTimer())
          .catch((error) => console.error("播放音频失败:", error));
    } else {
      // 暂停播放
      // backToController();
      audioPlayer.pause();
      stopTimer();
    }
  } else {
    if(audio_name != null && audio_name != currentAudioName.value) {
      backToController()
    }
    audio_name = currentAudioName.value
    console.log(audio_name)
    stopAudio();
    audioPlayer = new Audio(audioUrl);
    audioPlayer.loop = true;
    audioPlayer.play()
        .then(() => startTimer())
        .catch((error) => console.error("播放音频失败:", error));
  }
}

function stopAudio() {
  if (audioPlayer) {
    audioPlayer.pause();
    audioPlayer.currentTime = 0;
    audioPlayer = null;
    stopTimer();
    playDuration.value = 0; // 重置播放时长
  }
}

function startTimer() {
  if (!timer) {
    timer = setInterval(() => {
      playDuration.value++;
    }, 1000);
  }
}

function stopTimer() {
  if (timer) {
    clearInterval(timer);
    timer = null;
  }
}

function backToController(){
  if(playDuration.value != 0){
    // 发送情绪数据到后端
    // 从 userStore 获取用户名
    const username = userStore.username;
    console.log(username);
    console.log(selectedEmotion)
    console.log(playDuration.value)
    console.log(audio_name)
    // 发送数据到后端
    axios
        .post('http://localhost:9000/api/whitenoise', {
          username: username,          // 用户名
          playDuration: playDuration.value, // 播放时长
          emotion: selectedEmotion,   // 当前选定情绪
          audioName: audio_name // 当前播放的音频名称
        })
        .then((response) => {
          console.log('数据发送成功:', response.data);
        })
        .catch((error) => {
          console.error('发送数据到后端失败:', error);
        });
  }
}


function backToMain() {
  //返回主页
  router.push({ name: 'home' })
}

function popup(emotion) {
  //返回白噪声界面Popup.vue
  // 触发父组件的事件，显示 Popup 组件
  emit("openPopup",emotion);
}

function backToEmotion() {
  emit("openEmotion");
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

/* 返回主页按钮 */
.back-button {
  position: fixed;
  bottom: 20px; /* 改为右下角 */
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px; /* 图标与文字的间距 */
  padding: 10px 15px;
  font-size: 14px;
  color: white;
  background-color: transparent; /* 背景透明 */
  border: none;
  border-radius: 8px;
  cursor: pointer;
  z-index: 20;
}

.back-button:hover {
  background-color: rgba(0, 0, 0, 0.2); /* 悬停时添加半透明背景 */
}

/* 前往白噪声界面按钮 */
.pop-up {
  position: fixed;
  bottom: 60px; /* 改为右下角 */
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px; /* 图标与文字的间距 */
  padding: 10px 15px;
  font-size: 14px;
  color: white;
  background-color: transparent; /* 背景透明 */
  border: none;
  border-radius: 8px;
  cursor: pointer;
  z-index: 20;
}

.pop-up:hover {
  background-color: rgba(0, 0, 0, 0.2); /* 悬停时添加半透明背景 */
}
/* 前往白噪声界面按钮 */
.back-emotion {
  position: fixed;
  bottom: 100px; /* 改为右下角 */
  right: 20px;
  display: flex;
  align-items: center;
  gap: 10px; /* 图标与文字的间距 */
  padding: 10px 15px;
  font-size: 14px;
  color: white;
  background-color: transparent; /* 背景透明 */
  border: none;
  border-radius: 8px;
  cursor: pointer;
  z-index: 20;
}

.back-emotion:hover {
  background-color: rgba(0, 0, 0, 0.2); /* 悬停时添加半透明背景 */
}
/* 情绪标题的整体样式 */
.selected-emotion-title {
  text-align: center; /* 居中对齐 */
  margin-top: 20px; /* 与顶栏的间距 */
  font-size: 20px; /* 字体大小调整为更小 */
  color: #4a4a4a; /* 深灰色字体 */
  font-weight: bold; /* 加粗字体 */
  background: linear-gradient(90deg, #fceabb, #f8b500); /* 渐变背景 */
  padding: 10px 20px; /* 内边距，增加背景感 */
  border-radius: 10px; /* 圆角边框 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 添加柔和阴影 */
  display: inline-block; /* 使背景宽度只包裹内容 */
  width: 80%; /* 使背景宽度自适应内容 */
  max-width: 600px; /* 最大宽度 */
  margin-left: auto; /* 左右自动边距，实现居中 */
  margin-right: auto;
}

/* 为选定情绪文字添加特殊样式 */
.emotion-highlight {
  color: #ff5722; /* 橙红色字体 */
  text-decoration: underline; /* 下划线强调 */
  font-style: italic; /* 斜体显示 */
}

.fab-container {
  position: fixed;
  top: 100px;
  right: 20px;
  z-index: 100;
}

.fab-main {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #f8b500;
  color: white;
  border: none;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  transition: background-color 0.3s;
}

.fab-main:hover {
  background-color: #f1a100;
}

.fab-sub {
  display: block;
  margin-bottom: 10px;
  width: 140px;
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  color: #333;
  border: 1px solid #ccc;
  font-size: 14px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  text-align: left;
}

.fab-sub:hover {
  background-color: #f0f0f0;
}

/* 动画效果 */
.fab-enter-active, .fab-leave-active {
  transition: all 0.3s;
}
.fab-enter-from, .fab-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

</style>