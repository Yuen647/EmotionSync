<template>
  <div class="app">
    <!-- 显示 UI 的按钮
    <button v-if="!isUIVisible" class="show-ui-button" @click="toggleUI">
      <img src="@/assets/hide.svg" alt="显示 UI 图标" class="button-icon" />
      <span>显示 UI</span>
    </button>
    <br>
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
        <button v-if="!isUIVisible && fabOpen" key="popup" class="fab-sub" @click="toggleUI">
          👁️ 显示 UI
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
        <div class="spacer"></div>
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
          <div class="nav-item" @click="toggleAudio">
            <a class="no-underline">{{ isAudio ? '单一音频模式' : '混合音频模式' }}</a>
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

      <!-- 音量控制条 -->
      <div v-if="isPlaying" class="volume-slider-container">
        <input
            type="range"
            min="0"
            max="100"
            v-model="volume"
            @input="handleVolumeChange"
            class="volume-slider"
        />
        <span class="volume-label">{{ volume }}%</span>
      </div>

      <!-- 睡眠计时器滑块 -->
      <div class="sleep-slider-container">
        <label for="sleep-timer">🕒 睡眠定时器（分钟）:</label>
        <input
            type="range"
            min="0"
            max="60"
            step="1"
            v-model="sleepMinutes"
            @input="onSleepTimerChange"
            class="sleep-slider"
        />
        <span class="sleep-label">
          {{ sleepMinutes > 0 ? `${sleepMinutes} 分钟后停止` : '不定时' }}
        </span>
        <!-- 新增暂停按钮 -->
        <button class="pause-button" @click="pauseAudio" v-if="isPlaying">
          ⏸️ 暂停
        </button>
      </div>

    </div>

  </div>
</template>


<script setup lang="ts">
import '@/css/WhiteNoise/Popup.css';
import { ref,onBeforeUnmount } from 'vue';
import { defineProps, defineEmits } from 'vue';
import { useUserStore } from '@/store/userStore';
import {useRouter} from "vue-router";
import axios from 'axios';

const userStore = useUserStore();
const router = useRouter();

const isFullscreen = ref(false);// 控制是否全屏
const isUIVisible = ref(true); // 控制 UI 是否可见
const isDarkMode = ref(false); // 控制深色模式
const isAudio = ref(false);// 控制单一音频和混合音频模式
const currentAudioName = ref(''); // 保存当前播放音频的名称
const isPlaying = ref(false);
const props = defineProps({
  selectedEmotion: String,
});
const sleepMinutes = ref(0);
let sleepTimeoutId: number | null = null;

function onSleepTimerChange() {
  console.log(`设置睡眠定时器为 ${sleepMinutes.value} 分钟`);
  // 如果正在播放，重新设定睡眠计时器
  if (isPlaying.value) {
    setSleepTimerIfNeeded();
  }
}

function setSleepTimerIfNeeded() {
  if (sleepTimeoutId) {
    clearTimeout(sleepTimeoutId);
    sleepTimeoutId = null;
  }

  if (sleepMinutes.value > 0 && isPlaying.value === true) {
    sleepTimeoutId = window.setTimeout(() => {
      backToController();
      stopAudio();
      alert('播放已按设定时间停止');
    }, sleepMinutes.value * 60 * 1000);
  }
}

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
const emit = defineEmits(['openEmotion']);
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







//let audioPlayer: HTMLAudioElement | null = null;
//const audioPlayer = ref<HTMLAudioElement | null>(null);// 变成响应式的
const playDuration = ref(0); // 播放时长（秒）
let timer: number | null = null; // 计时器
let audio_name = "rain";


const volume = ref(50); // 初始音量 50%
// 音量统一调节
function handleVolumeChange() {
  for (const name in audioPlayers.value) {
    audioPlayers.value[name].volume = volume.value / 100;
  }
}
// 管理多个音频实例
const audioPlayers = ref<{ [key: string]: HTMLAudioElement }>({});
let audioPlayer: HTMLAudioElement | null = null;

function playOrPause(audioUrl: string, audioName: string) {
  // currentAudioName.value = audioName; // 更新当前音频名称
  // playAudio(audioUrl); // 调用前面定义的 playAudio 方法
  if (!isAudio.value) {
    // 单一音频模式
    // stopAllAudios(); // 先停止所有音频
    playSingleAudio(audioUrl, audioName);
  } else {
    // 混合音频模式
    toggleMixedAudio(audioUrl, audioName);
  }
}
let currentAudioUrl = "";
// 单一音频模式
function playSingleAudio(audioUrl: string, audioName: string) {
  currentAudioName.value = audioName;

  // 如果当前播放器存在，且正在播放同一个音频
  if (audioPlayer && currentAudioUrl === audioUrl) {
    if (audioPlayer.paused) {
      audioPlayer.play()
          .then(() => {
            isPlaying.value = true;
            startTimer();
          })
          .catch((error) => console.error("播放失败:", error));
    } else {
      backToController();
      audioPlayer.pause();
      isPlaying.value = false;
      stopTimer();
    }
  } else {
    // 切换或首次播放新的音频
    if (audioPlayer) {
      backToController();
      audioPlayer.pause(); // 停止旧播放器
      stopTimer();
    }

    const newPlayer = new Audio(audioUrl);
    newPlayer.loop = true;
    newPlayer.volume = volume.value / 100;

    newPlayer.play()
        .then(() => {
          audioPlayer = newPlayer;
          currentAudioUrl = audioUrl;
          audioPlayers.value[audioName] = newPlayer;
          isPlaying.value = true;
          startTimer();
          setSleepTimerIfNeeded();
          console.log("播放成功", audioPlayer.paused);
        })
        .catch((error) => console.error("播放失败:", error));
  }
}

function stopAudio() {
  if (audioPlayer.value) {
    audioPlayer.value.pause();
    audioPlayer.value.currentTime = 0;
    audioPlayer.value = null;
    stopTimer();
    playDuration.value = 0; // 重置播放时长
  }
}

// 混合播放
function toggleMixedAudio(audioUrl: string, audioName: string) {
  const existingPlayer = audioPlayers.value[audioName];

  if (existingPlayer) {
    if (existingPlayer.paused) {
      // 如果已存在但是暂停状态，则播放
      existingPlayer.play().catch((error) => console.error("播放音频失败:", error));
    } else {
      // 如果正在播放，则暂停
      existingPlayer.pause();
    }
  } else {
    // 如果不存在该音频播放器，则新建一个播放器并播放
    const newPlayer = new Audio(audioUrl);
    newPlayer.loop = true;
    newPlayer.volume = volume.value / 100;
    isPlaying.value = !isPlaying.value;
    newPlayer.play()
        .then(() => {
          audioPlayers.value[audioName] = newPlayer;
        })
        .catch((error) => console.error("播放音频失败:", error));
  }
}
// 暂停播放
function stopAllAudios() {
  for (const name in audioPlayers.value) {
    const audio = audioPlayers.value[name];
    audio.pause();
    audio.currentTime = 0;
  }
  audioPlayers.value = {};
  stopTimer();
  playDuration.value = 0;
  isPlaying.value = false;
}

function pauseAudio() {
  if (isAudio.value) {
    for (const player of Object.values(audioPlayers.value)) {
      if (!player.paused) {
        player.pause();
      }
    }
    isPlaying.value = false;
  }
  else {
    if (audioPlayer && !audioPlayer.paused) {
      audioPlayer.pause();
      isPlaying.value = false;
      stopTimer();
    }
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
function toggleAudio() {
  isAudio.value = !isAudio.value;
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
function backToEmotion() {
  emit("openEmotion");
}
function toggleDarkMode() {
  isDarkMode.value = !isDarkMode.value;
}
</script>

<style scoped></style>


