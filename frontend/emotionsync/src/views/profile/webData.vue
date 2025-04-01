<template>
  <div class="app">
    <!-- 左侧部分：功能导航 -->
    <div class="sidebar">
      <h2>基本数据信息</h2>
      <p class="user-count">当前网站总注册用户量：{{ users.length }}</p>
      <div class="navigation-buttons">
        <button v-for="btn in navigationButtons" :key="btn.value" @click="changeChart(btn.value)"
                class="nav-btn">
          {{ btn.label }}
        </button>
      </div>
    </div>

    <!-- 右侧渲染部分 -->
    <div class="content">
      <!-- 默认视图部分 -->
      <div v-if="currentView === 'default'" class="default-view">
        <h3>项目说明</h3>
        <p>
          ZenZone
          是一个旨在提供数字化放松体验的项目，结合互动功能、解压游戏和情绪支持，帮助用户减轻压力、放松身心。我们的目标是通过创新的方式，提供一种可以帮助用户调整情绪的数字环境，满足现代人在快节奏生活中对心理健康和情绪管理的需求。
        </p>
        <div>
          本项目包含多个模块，用户可以通过不同的功能部分来进行数据查看和互动。功能模块包括：
          <ul>
            <li><strong>AI对话</strong>：通过与智能AI的互动，用户可以获得情感支持，帮助缓解压力和焦虑。AI能够根据用户的情绪变化提供个性化的回应和陪伴，创造一个温馨的对话环境，帮助用户在轻松愉快的氛围中释放压力。
            </li>

            <li><strong>心理小测试</strong>：用户可以通过参与简单的心理测试，了解自己的情绪状态。测试结果会为用户提供有针对性的反馈，帮助用户识别情绪波动，并提供科学的建议，帮助其更好地管理自己的心理健康。
            </li>

            <li><strong>游戏时长数据</strong>：展示用户在各种在线小游戏中的使用时长，帮助了解用户偏好与参与度。</li>
            <li><strong>白噪声时长数据</strong>：记录用户播放不同类型白噪音的时长，为用户提供科学的放松辅助。</li>
          </ul>
          用户可以通过选择不同的功能模块，深入了解他们的活动数据，或者参与互动，获得更多个性化的体验。
        </div>
      </div>

      <!-- 游戏数据部分 -->
      <div v-if="currentView === 'game'" class="chart-section">
        <h3>网站游戏时长数据</h3>
        <canvas id="gameDurationChart"></canvas>
        <div>
          <p>
            本部分展示了用户在不同游戏中的总时长数据。每个游戏的时长都是基于用户参与游戏的时间进行统计，帮助分析游戏的使用趋势、用户的偏好以及游戏的参与度。此数据对于优化游戏体验和提升用户满意度有重要意义。
          </p>
          <p>
            通过这些数据，项目团队可以深入了解用户在不同游戏模块上的投入时间，从而调整游戏内容和推送策略，以提升用户体验。
          </p>
        </div>
      </div>

      <!-- 白噪音数据部分 -->
      <div v-if="currentView === 'noise'" class="chart-section">
        <h3>网站白噪声时长数据</h3>
        <canvas id="whiteNoiseChart"></canvas>
        <div>
          <p>
            本部分展示了用户使用不同类型白噪音的总时长数据。白噪音是帮助用户放松和集中注意力的有效工具，广泛应用于冥想、学习、睡眠等场景。通过此数据，用户可以了解自己在各类白噪音类型中的使用习惯。
          </p>
          <p>
            我们提供多种白噪音类型，用户可以选择适合自己的音频类型来缓解压力和提升放松效果。数据展示有助于用户对比不同类型白噪音的效果，从而优化其放松策略。
          </p>
        </div>
      </div>

      <!-- 情绪数据部分 -->
      <div v-if="currentView === 'emotion'" class="chart-section">
        <h3>网站用户情绪频次数据</h3>
        <canvas id="emotionChart"></canvas>
        <div>
            本部分展示了用户情绪的频次数据。通过此数据，可以了解用户在使用本网站时的情绪趋势。
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted, nextTick} from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';

const navigationButtons = [
  {label: '项目说明', value: 'default'},  // 默认视图按钮
  {label: '游戏数据部分', value: 'game'},
  {label: '白噪声数据部分', value: 'noise'},
  {label: '情绪数据部分', value: 'emotion'},
];
const users = ref<any[]>([]);
const gameDurations = ref<{ [gameName: string]: number }>({});  // 存储每个游戏的总时长
const whitenoiseDurations = ref<{ [type: string]: number }>({});// 存储每种白噪声类型的总时长
const emotions = ref<{ [key: string]: number }>({});
const currentView = ref('default'); // 当前视图

// 获取所有用户的数据
const fetchUsers = async () => {
  try {
    const response = await axios.get('http://localhost:9000/api/users');
    users.value = response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
  }
};

// 渲染柱状图
const renderChart = (ctxId: string, label: string, dataValues: { [key: string]: number }, isDuration: boolean) => {
  const ctx = document.getElementById(ctxId) as HTMLCanvasElement;
  const chartData = isDuration ? Object.values(dataValues).map(time => time / 60) : Object.values(dataValues);
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: Object.keys(dataValues),
      datasets: [{
        label: label,
        data: chartData,
        backgroundColor: ['#FF6347', '#36A2EB', '#FFCE56'],
        borderColor: ['#FF6347', '#36A2EB', '#FFCE56'],
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: {position: 'top'},
        tooltip: {
          callbacks: {
            label: (tooltipItem) => {
              const itemLabel = tooltipItem.label;
              const duration = tooltipItem.raw as number;  // 强制转换为数字类型
              return isDuration ? `${itemLabel}: ${duration.toFixed(2)}分钟`
                  : `${itemLabel}: ${duration}次`;
            }
          }
        }
      },
      scales: {
        x: {beginAtZero: true},
        y: {beginAtZero: true}
      }
    }
  });
};

// 获取所有数据
const getData = async () => {
  const whitenoiseResponse = await axios.post('http://localhost:9000/profile/allWhiteNoise');
  whitenoiseDurations.value = whitenoiseResponse.data;
  const gameResponse = await axios.post(`http://localhost:9000/profile/allGameTime`);
  gameDurations.value = gameResponse.data;
  const emotionResponse = await axios.post('http://localhost:9000/profile/allEmotion');
  emotions.value = emotionResponse.data;
};

//更换图表
const changeChart = async (view: string) => {
  currentView.value = view;
  // 等待 Vue 完成 DOM 更新，然后再更新图表
  await nextTick();
  if (view === 'game') {
    renderChart('gameDurationChart', '游戏时长（分钟）', gameDurations.value, true);
  } else if (view === 'noise') {
    renderChart('whiteNoiseChart', '白噪音时长（分钟）', whitenoiseDurations.value, true);
  } else if (view === 'emotion') {
    renderChart('emotionChart', '情绪频率', emotions.value, false);
  }
};

onMounted(() => {
  fetchUsers(); // 获取所有用户数据
  getData();
});

</script>

<style scoped>
.app {
  display: flex;
  height: 100vh;
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

.user-count {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.navigation-buttons {
  display: flex;
  flex-direction: column;
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

.nav-btn:hover {
  background-color: #0056b3;
}

.chart-section {
  margin-top: 20px;
}

h3 {
  color: #007bff;
  font-size: 1.25rem;
  margin-bottom: 10px;
}

canvas {
  width: 100% !important;
  height: 300px;
  background-color: #e0e0e0;
  border-radius: 8px;
}

div {
  font-size: 1rem;
  color: #555;
  margin-top: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    box-shadow: none;
    padding: 10px;
  }

  .content {
    width: 100%;
    padding: 10px;
  }
}
</style>