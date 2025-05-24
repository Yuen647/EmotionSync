<template>
  <div>
    <div v-if="userStore.loggedIn !== 1" class="login-prompt">
      <h2 class="text-2xl font-bold mb-4">请先登录</h2>
      <p class="mb-4">你需要登录才能访问个人日记功能</p>
      <button @click="router.push({ name: 'login' })" class="bg-primary text-white px-6 py-2 rounded-md">
        前往登录
      </button>
    </div>
    <div v-else>
      <!-- 用于调试的用户信息显示 -->
      <div v-if="showDebug" class="debug-info">
        <p><strong>用户信息（调试用）:</strong></p>
        <p>登录状态: {{ userStore.loggedIn === 1 ? '已登录' : '未登录' }} (值: {{ userStore.loggedIn }})</p>
        <p>用户名: {{ userStore.username || '未设置' }}</p>
        <button @click="showDebug = false" class="text-xs text-gray-500">隐藏调试信息</button>
      </div>
      <DiaryView />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/userStore';
import DiaryView from './DiaryView.vue';

const router = useRouter();
const userStore = useUserStore();
const showDebug = ref(true); // 默认显示调试信息
</script>

<style scoped>
.login-prompt {
  max-width: 600px;
  margin: 100px auto;
  padding: 40px;
  text-align: center;
  background-color: #f9f9f9;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.debug-info {
  margin: 10px;
  padding: 10px;
  background-color: #f0f0f0;
  border: 1px dashed #ccc;
  border-radius: 4px;
  font-size: 12px;
}
</style> 