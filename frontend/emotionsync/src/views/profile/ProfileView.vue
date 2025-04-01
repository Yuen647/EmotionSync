<template>
  <div class="profile-view">
    <h1>个人资料</h1>
    <div class="profile-content">
      <!-- 左侧个人信息 -->
      <section class="profile-info">
        <h2>个人基本信息</h2>
        <div class="card">
          <ul class="profile-list">
            <li>
              <h3>用户名</h3>
              <div>{{ username }}
                <p v-if="role === 'researcher'" class="role-label">研究人员</p>
                <p v-else class="role-label other-role">普通用户</p>
              </div>
              <!-- 根据角色动态显示标签 -->
            </li>
            <li>
              <h3>邮箱</h3>
              <p>{{ email }}</p>
            </li>
            <li>
              <!-- 动态显示跳转链接 -->
              <div v-if="role === 'researcher'">
                <h3>网站数据</h3>
                <a href="/faa#/webData" class="data-link">查看网站数据</a>
              </div>
              <div v-else>
                <h3>定期报告</h3>
                <a href="/faa#/PersonalReport" class="data-link">查看个人定期报告</a>
              </div>
            </li>
          </ul>
        </div>
      </section>

      <!-- 右侧网站使用资料 -->
      <section class="site-info">
        <h2>个人网站使用资料</h2>
        <div class="card">
          <div class="mbti">
            <h3>人格类型测试结果</h3>
            <p>{{ mbti }}</p>
          </div>
          <!-- 白噪音时长柱状图 -->
          <div class="chart-section">
            <h3 class="chart-title">白噪音时长</h3>
            <div class="chart-container">
              <canvas id="whiteNoiseChart"></canvas>
            </div>
          </div>
          <!-- 游戏时长柱状图 -->
          <div class="chart-section">
            <h3 class="chart-title">游戏时长</h3>
            <div class="chart-container">
              <canvas id="gameDurationChart"></canvas>
            </div>
          </div>
          <!--情绪柱状图 -->
          <div class="chart-section">
            <h3 class="chart-title">情绪频率</h3>
            <div class="chart-container">
              <canvas id="emotionChart"></canvas>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import {useUserStore} from '@/store/userStore';
import axios from 'axios';
import {onMounted, ref} from 'vue';
import {Chart, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title, BarController} from 'chart.js';

Chart.register(BarController, BarElement, CategoryScale, LinearScale, Tooltip, Legend, Title);

// 响应式数据
const email = ref('');
const mbti = ref('');
const whitenoiseDurations = ref<{ [key: string]: number }>({});
const gameDurations = ref<{ [key: string]: number }>({});
const emotions = ref<{ [key: string]: number }>({});
const username = useUserStore().username; // 获取当前用户名
const role = ref('');

// 获取用户角色
const fetchUserRole = async (username: string) => {
  try {
    const response = await axios.get(`http://localhost:9000/api/users/${username}`);
    // 将 response.data.identity 的值赋给 role
    role.value = response.data.identity || '';
    console.log('获取用户角色成功:', response.data);

  } catch (error) {
    console.error('获取用户角色失败:', error);
  }
};

// 渲染柱状图
const renderChart = (ctxId: string, label: string, dataValues: { [key: string]: number }, isDuration : boolean) => {
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
        legend: { position: 'top' },
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
        x: { beginAtZero: true },
        y: { beginAtZero: true }
      }
    }
  });
};

// 获取所有数据
const getData = async () => {
  const emailResponse = await axios.post('http://localhost:9000/profile/email', {username});
  email.value = emailResponse.data;
  const mbtiResponse = await axios.post('http://localhost:9000/profile/mbti', {username});
  mbti.value = mbtiResponse.data;
  const whitenoiseResponse = await axios.post('http://localhost:9000/profile/whitenoise', {username});
  whitenoiseDurations.value = whitenoiseResponse.data;
  const gameResponse = await axios.post(`http://localhost:9000/profile/gametime`, {username: username});
  gameDurations.value = gameResponse.data;
  const emotionResponse = await axios.post('http://localhost:9000/profile/emotion', {username: username});
  emotions.value = emotionResponse.data;
  renderChart('gameDurationChart', '游戏时长（分钟）', gameDurations.value, true);
  renderChart('whiteNoiseChart', '白噪音时长（分钟）', whitenoiseDurations.value, true);
  renderChart('emotionChart', '情绪频率', emotions.value, false);

};

// 页面挂载时获取数据
onMounted(() => {
  getData();
  fetchUserRole(username);
});
</script>

<style scoped>
.profile-view {
  font-family: 'Arial', sans-serif;
  background-color: #f4f7fa;
  padding: 20px;
  max-width: 1200px;
  /* 限制最大宽度 */
  margin: 0 auto;
  /* 居中 */
  height: auto;
}

.profile-content {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 20px;
  /* 调整顶部间距 */
}

.profile-info,
.site-info {
  flex: 1;
  background-color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  /* 限制单个部分的最大宽度 */
  width: 100%;
  /* 保证在小屏时宽度为100% */
}

.card {
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  background-color: #ffffff;
}

.profile-info h2,
.site-info h2 {
  color: #333;
  font-size: 1.5em;
  margin-bottom: 15px;
}

.profile-info {
  width: 50%;
  text-align: left;
}

.mbti {
  margin-bottom: 12px;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 8px;
  transition: background-color 0.3s;
}

.mbti:hover {
  background-color: #e0f7fa;
}

h3 {
  color: #007BFF;
}

p {
  color: #555;
  font-size: 1.2em;
}

.chart-container {
  max-width: 100%;
  height: 235px;
  margin-top: 20px;
  /* 图表和内容之间的间距 */
}

canvas {
  width: 100% !important;
  height: 100% !important;
}

@media (max-width: 768px) {
  .profile-content {
    flex-direction: column;
    gap: 15px;
    /* 更小的间隙 */
  }

  .profile-info,
  .site-info {
    max-width: none;
    /* 在小屏时不限制宽度 */
    width: 100%;
  }
}

/* 研究人员标签样式 */
.role-label {
  display: inline-block;
  margin-top: 5px;
  padding: 5px 10px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(90deg, #4caf50, #2e7d32);
  border-radius: 12px;
  font-size: 0.9rem;
}

/* 跳转链接美化 */
.data-link {
  display: inline-block;
  margin-top: 10px;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(90deg, #2196f3, #1976d2);
  border-radius: 20px;
  text-align: center;
  text-decoration: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.data-link:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}
</style>