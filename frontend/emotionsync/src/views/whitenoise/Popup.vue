<template>
  <div class="app">
    <!-- 显示 UI 的按钮 -->
    <button v-if="!isUIVisible" class="show-ui-button" @click="toggleUI">
      <img src="@/assets/hide.svg" alt="显示 UI 图标" class="button-icon" />
      <span>显示 UI</span>
    </button>
    <br>
    <button class="back-button" @click="backToMain">
      <span>返回主页</span>
    </button>

    <!-- 深色遮罩 -->
    <div class="overlay" :class="{ 'dark-overlay': isDarkMode }"></div>

    <!-- 弹窗内容 -->
    <div v-if="isUIVisible" class="popup-content">
      <!-- 顶栏 -->
      <div class="popup-header">
        <div class="left">
          <img src="@/assets/white-noise-icon.jpg" alt="White Noise" class="icon" />
          <span>White Noise</span>
        </div>
        <div class="right">
          <div class="nav-item" @click="toggleFullscreen">
            <a class="no-underline">{{ isFullscreen ? '取消全屏' : '全屏' }}</a>
          </div>
          <div class="nav-item" @click="toggleUI">
            <a class="no-underline">隐藏 UI</a>
          </div>
          <div class="nav-item" @click="toggleDarkMode">
            <a class="no-underline">{{ isDarkMode ? '浅色模式' : '深色模式' }}</a>
          </div>
        </div>
      </div>

      <!-- 按钮区 -->
      <div class="button-grid">
        <div
            v-for="(button, index) in buttons"
            :key="index"
            class="square-button"
            @click="playOrPause(button.audio, button.text)"
        >
          <img :src="button.icon" alt="Icon" />
          <span>{{ button.text }}</span>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup lang="ts">
import { ref,onBeforeUnmount } from 'vue';
import { defineProps } from 'vue';
import { useUserStore } from '@/store/userStore';
import {useRouter} from "vue-router";
import axios from 'axios';

const userStore = useUserStore();
const isFullscreen = ref(false);
const isUIVisible = ref(true); // 控制 UI 是否可见
const isDarkMode = ref(false); // 控制深色模式
const router = useRouter();
const currentAudioName = ref(''); // 保存当前播放音频的名称

const props = defineProps({
  selectedEmotion: String,
});

// 你可以像这样直接访问 `props.selectedEmotion`，或者使用解构
const { selectedEmotion } = props; // 这样你可以直接使用 selectedEmotion 变量

const buttons = ref([
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/rain.svg', text: 'Rain', audio: 'https://ambicular.com/sounds/rain/splashing-rainfall160.mp3' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/wind.svg', text: 'Wind', audio: 'https://st2.asoftmurmur.com/assets/p/content/wind/main-wind.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/thunder.svg', text: 'Thunder', audio: 'https://st2.asoftmurmur.com/assets/p/content/thunder/main-thunder.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/brook.svg', text: 'Waves', audio: 'https://st3.asoftmurmur.com/assets/p/content/waves/glue-waves.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/forest.svg', text: 'Forest', audio: 'https://st2.asoftmurmur.com/assets/p/content/birds/main-birds.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/fire.svg', text: 'Fire', audio: 'https://st2.asoftmurmur.com/assets/p/content/fire/main-fire.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/cafe.svg', text: 'Cafe', audio: 'https://st3.asoftmurmur.com/assets/p/content/people/main-people.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/leafs.svg', text: 'Crickets', audio: 'https://st3.asoftmurmur.com/assets/p/content/crickets/glue-crickets.mp4' },
  { icon: 'https://defonic.b-cdn.net/defonic/images/icons/ocean.svg', text: 'Boating', audio: 'https://ambicular.com/sounds/defonicprem/rowing160.mp3' },

]);

// 组件卸载前停止播放
onBeforeUnmount(() => {
  if(audioPlayer){
    backToController();
    stopAudio();
    stopTimer();
  }
});



function playOrPause(audioUrl: string, audioName: string) {
  currentAudioName.value = audioName; // 更新当前音频名称
  playAudio(audioUrl); // 调用前面定义的 playAudio 方法
}

let audioPlayer: HTMLAudioElement | null = null;
const playDuration = ref(0); // 播放时长（秒）
let timer: number | null = null; // 计时器

function playAudio(audioUrl: string) {
  if (!audioPlayer) {
    audioPlayer = new Audio(audioUrl);
    audioPlayer.loop = true;
    audioPlayer.play()
        .then(() => startTimer())
        .catch((error) => console.error("播放音频失败:", error));
  } else if (audioPlayer.src.includes(audioUrl)) {
    if (audioPlayer.paused) {
      audioPlayer.play()
          .then(() => startTimer())
          .catch((error) => console.error("播放音频失败:", error));
    } else {
      backToController();
      audioPlayer.pause();
      stopTimer();
    }
  } else {
    backToController();
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


function toggleFullscreen() {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen();
    isFullscreen.value = true;
  } else {
    document.exitFullscreen();
    isFullscreen.value = false;
  }
}

function toggleUI() {
  isUIVisible.value = !isUIVisible.value;
}

function backToController(){
  if(playDuration.value != 0){
    // 发送情绪数据到后端
    // 从 userStore 获取用户名
    const username = userStore.username;
    console.log(username);
    // 发送数据到后端
    axios
        .post('http://localhost:9000/api/whitenoise', {
          username: username,          // 用户名
          playDuration: playDuration.value, // 播放时长
          emotion: selectedEmotion,   // 当前选定情绪
          audioName: currentAudioName.value // 当前播放的音频名称
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

function toggleDarkMode() {
  isDarkMode.value = !isDarkMode.value;
}
</script>



<style scoped>
/* 背景 */
/* 背景始终可见 */
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
/* 深色遮罩层 */
.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0); /* 默认透明 */
  transition: background-color 0.3s ease;
}

.overlay.dark-overlay {
  background-color: rgba(0, 0, 0, 0.5); /* 深色模式时覆盖黑色半透明 */
}
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



/* 显示 UI 按钮 */
.show-ui-button {
  position: fixed;
  bottom: 20px; /* 改为左下角 */
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

.show-ui-button:hover {
  background-color: rgba(0, 0, 0, 0.2); /* 悬停时添加半透明背景 */
}

.show-ui-button .button-icon {
  width: 24px; /* 图标宽度 */
  height: 24px; /* 图标高度 */
}

/* 返回主页按钮 */
.back-button {
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

.back-button:hover {
  background-color: rgba(0, 0, 0, 0.2); /* 悬停时添加半透明背景 */
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
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: rgba(240, 240, 240, 0.1);
  color: black;
  border-radius: 8px;
}

.left {
  display: flex;
  align-items: center;
}

.icon {
  width: 24px;
  height: 24px;
  margin-right: 8px;
}

.right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-item {
  position: relative;
  padding: 10px;
  cursor: pointer;
  color: black;
  font-size: 14px;
  transition: color 0.3s;
}

.nav-item:hover {
  color: #007bff; /* 悬停时文本颜色 */
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: transparent;
  transition: background-color 0.3s;
}

.nav-item:hover::after {
  background-color: #007bff; /* 悬停时显示下划线 */
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
  width: 40px;
  height: 40px;
  margin-bottom: 8px;
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

</style>
