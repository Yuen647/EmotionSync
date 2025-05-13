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
  </div>
</template>

<script setup lang="ts">
import { ref, onBeforeUnmount } from 'vue';
import { defineProps, defineEmits } from 'vue';
import { useUserStore } from '@/store/userStore';
import {useRouter} from "vue-router";
import axios from 'axios';
import '@/css/WhiteNoise/RecommandSound.css';
const userStore = useUserStore();
const router = useRouter();
const currentAudioName = ref(''); // 保存当前播放音频的名称
const isPlaying = ref(false);

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

const volume = ref(50); // 初始音量 50%

function handleVolumeChange() {
  console.log('音量设置为：', volume.value);
  if (audioPlayer.value) {
    audioPlayer.value.volume = volume.value / 100;
  }
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
    audioPlayer.volume = volume.value / 100;
    isPlaying.value = !isPlaying.value;
    audioPlayer.loop = true;
    audioPlayer.play()
        .then(() => startTimer())
        .catch((error) => console.error("播放音频失败:", error));
  } else if (audioPlayer.src.includes(audioUrl)) { // 当前播放器已存在，并且播放的是同一个音频
    if (audioPlayer.paused) {
      isPlaying.value = !isPlaying.value;
      audioPlayer.play()
          .then(() => startTimer())
          .catch((error) => console.error("播放音频失败:", error));
    } else {
      // 暂停播放
      // backToController();
      isPlaying.value = !isPlaying.value;
      audioPlayer.pause();
      stopTimer();
    }
  } else {
    if(audio_name != null && audio_name != currentAudioName.value) {
      backToController()
    }
    audio_name = currentAudioName.value
    stopAudio();
    audioPlayer = new Audio(audioUrl);
    audioPlayer.volume = volume.value / 100;
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

