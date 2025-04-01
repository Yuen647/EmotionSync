<template>
  <div class="app">
    <!-- 左侧部分：功能导航 -->
    <div class="sidebar">
      <h3>个人使用数据统计</h3>
      <br>
      <p>当前报告显示了</p>
      <p>{{ times[0] }} 到</p>
      <p>{{ times[1] }} 的信息</p>
      <div class="navigation-buttons">
        <button @click="goBack()" class="nav-btn">返回个人主页</button>
      </div>
    </div>
    <div class="content">
      <h3>报告日期选择</h3>
      <el-date-picker v-model="times"
                      type="daterange"
                      range-separator="到"
                      start-placeholder="开始日期"
                      end-placeholder="结束日期"
                      format="YYYY-MM-DD HH:mm:ss"
                      value-format="YYYY-MM-DD HH:mm:ss"
                      @change="refreshReport"
                      style="margin: 30px;"
      />
      <div class="info">
        <h3>您的游戏时长数据</h3>
        <p>本部分展示了您在不同游戏中的总时长数据。
          通过此数据，您可以了解自己玩哪个游戏的时间最长。 </p>
      </div>
      <div class="charts">
        <canvas id="gameDurationChart"/>
      </div>
      <div class="info">
        <h3>您的白噪声时长数据</h3>
        <p>
          本部分展示了您使用不同类型白噪音的总时长数据。
          白噪音是帮助用户放松和集中注意力的有效工具，广泛应用于冥想、学习、睡眠等场景。
          通过此数据，您可以了解自己在各类白噪音类型中的使用习惯。
        </p>
      </div>
      <div class="charts">
        <canvas id="whiteNoiseChart"/>
      </div>
      <div class="info">
        <h3>您的情绪频率数据</h3>
        <p>本部分展示了您使用本网站时产生各情绪的次数。</p>
      </div>
      <div class="charts">
        <canvas id="emotionChart"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue';
import router from '@/router';
import axios from 'axios';
import Chart from 'chart.js/auto';
import {useUserStore} from '@/store/userStore'

const username = useUserStore().username;
const gameDurations = ref<{ [gameType: string]: number }>({});  // 存储每个游戏的总时长
const whitenoiseDurations = ref<{ [type: string]: number }>({});//存储每种白噪声类型的总时长
const emotions = ref<{ [type: string]: number }>({});//存储每种情绪声类型的频率
const times = ref(['2024-01-01 00:00:00', '2024-12-30 00:00:00'])
let gameChart: Chart | undefined = undefined;
let whitenoiseChart: Chart | undefined = undefined;
let emotionChart: Chart | undefined = undefined;

const chartOption = {
  responsive: true,
  scales: {
    y: {beginAtZero: true, ticks: {stepSize: 5}},
    x: {grid: {display: false}}
  },
}

// 获取单个用户的游戏时长
const getGameDuration = async () => {
  const response = await axios.post(`http://localhost:9000/profile/timedgametime`,
      {
        username: username,
        startTime: times.value[0],
        endTime: times.value[1],
      });
  gameDurations.value = response.data;
};

// 获取单个用户的白噪声时长
const getWhitenoiseDuration = async () => {
  const response = await axios.post('http://localhost:9000/profile/timedwhitenoise',
      {
        username: username,
        startTime: times.value[0],
        endTime: times.value[1],
      });
  whitenoiseDurations.value = response.data;
};

// 获取单个用户的情绪
const getEmotion = async () => {
  const response = await axios.post('http://localhost:9000/profile/timedEmotion',
      {
        username: username,
        startTime: times.value[0],
        endTime: times.value[1],
      });
  emotions.value = response.data;
};

const chartDataGen = (data: { [type: string]: number }, labelText: string, colors: string[]) => {
  return {
    labels: Object.keys(data),
    datasets: [{
      label: Object.keys(data).length === 0 ? "暂无数据" : labelText,
      data: Object.values(data),
      backgroundColor: Object.values(data).map((_, index) => colors[index % colors.length]),
      borderColor: '#333',
      borderWidth: 1,
      barPercentage: 0.8,
    }]
  };
}

const updateGameChart = () => {
  const colors = ['#FF5733', '#33FF57', '#3357FF', '#FF33A1', '#57FF33', '#FF8C00']; // 游戏图表颜色数组
  const ctx = document.getElementById('gameDurationChart') as HTMLCanvasElement;
  if (ctx) {
    const chartData = chartDataGen(gameDurations.value, '游戏时长(秒)', colors);
    if (gameChart === undefined) {
      gameChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: chartOption
      });
    } else {
      gameChart.data = chartData;
      gameChart.update();
    }
  }
};

const updateWhiteNoiseChart = () => {
  const colors = ['#4CAF50', '#FFC107', '#03A9F4', '#9C27B0', '#FF5722']; // 白噪音图表颜色数组
  const ctx = document.getElementById('whiteNoiseChart') as HTMLCanvasElement;
  if (ctx) {
    const chartData = chartDataGen(whitenoiseDurations.value, '白噪声时长(秒)', colors);
    if (whitenoiseChart === undefined) {
      whitenoiseChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: chartOption
      });
    } else {
      whitenoiseChart.data = chartData;
      whitenoiseChart.update();
    }
  }
};

const updateEmotionChart = () => {
  const colors = ['#9C27B0', '#FF5722', '#FFC107', '#03A9F4', '#4CAF50']; // 情绪图表颜色数组
  const ctx = document.getElementById('emotionChart') as HTMLCanvasElement;
  if (ctx) {
    const chartData = chartDataGen(emotions.value, '情绪频率(次)', colors);
    if (emotionChart === undefined) {
      emotionChart = new Chart(ctx, {
        type: 'bar',
        data: chartData,
        options: chartOption
      });
    } else {
      emotionChart.data = chartData;
      emotionChart.update();
    }
  }
};

const refreshReport = async () => {
  await getGameDuration();
  await getWhitenoiseDuration();
  await getEmotion();
  updateGameChart();
  updateWhiteNoiseChart();
  updateEmotionChart();
}

const goBack = () => {
  router.go(-1);
}

onMounted(async () => {
  await refreshReport();
});
</script>

<style scoped>
.app {
  display: flex;
  height: 90vh;
  background-color: #f8f9fa;
  max-width: 1200px;
  margin: 0 auto;
}

.sidebar {
  width: 250px;
  padding: 20px;
  background-color: #f4f4f4;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #fff;
}

h2 {
  font-size: 1.5rem;
  margin-bottom: 15px;
}

.nav-btn {
  background-color: #007bff;
  color: white;
  padding: 10px;
  margin: 5px 0;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.charts {
  margin-top: 20px;
  width: 500px;
  height: 300px;
  justify-self: center;
}

h3 {
  color: #007bff;
  font-size: 1.25rem;
  margin-bottom: 10px;
}

canvas {
  width: 100% !important;
  height: 290px !important;
  background-color: #e0e0e0;
  border-radius: 8px;
}

div {
  font-size: 1rem;
  color: #555;
  margin-top: 10px;
  margin-bottom: 20px;
}
</style>