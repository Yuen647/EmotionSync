<template>
  <div class="border-0 border-b-[1px] border-b-default border-solid">
    <div class="header">
      <RouterLink :to="{ name: 'home' }">
        <img class='headimage' :src="logo" />
      </RouterLink>
      <div class="flex-1 flex justify-end h-full">
        <div class="flex gap-[48px] h-full items-center">
          <RouterLink v-for="menu in menus" :to="{ name: menu.name }" custom v-slot="{ isActive, navigate, href }">
            <div
              class="h-full flex items-center justify-center whitespace-nowrap relative after:bottom-0 after:absolute after:h-[4px] after:block after:content-[''] after:w-full"
              :class="{ 'after:bg-primary font-semibold': isActive }">
              <div v-if="menu.name==='AIchat'||menu.name==='whitenoise'">
              <a class="no-underline text-black" :href="userStore.loggedIn?href:'#/login'">{{ menu.title }}</a>
              </div>
              <div v-else>
              <a class="no-underline text-black" :href="href">{{ menu.title }}</a>
              </div>
            </div>
          </RouterLink>
          <img class="shrink-0 w-[32px] h-[32px] rounded-full cursor-pointer"
            :src="userStore.loggedIn == 1 ? avatar : anonymous" @click="handleAvatarClick" />
        </div>
      </div>
    </div>

    <!-- 用户信息弹窗 -->
    <div v-if="showUserInfoModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="flex items-center justify-center gap-4">
          <img :src="avatar" class="w-[50px] h-[50px] rounded-full" />
          <div>
            <p class="text-lg font-semibold">用户名：{{ userStore.username }}</p>
            <!-- <RouterLink :to="{ name: 'profile' }">
              <button class="btn-primary">进入个人主页</button>
            </RouterLink> -->
            <button class="btn-primary" @click="enterProfile">进入个人主页</button>
            <br><br>
            <button class="btn-changeLogin" @click="changeLogIn">切换登录</button>
          </div>
        </div>
        <button class="close-btn" @click="closeModal">×</button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import anonymous from '@/assets/avatar-anonymous.png';
import avatar from '@/assets/avatar.png';
import logo from '@/assets/logo.png';
import { useUserStore } from '@/store/userStore';
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const menus = [
  { title: '首页', name: 'about' },
  { title: 'AI数字人', name: 'AIDigitalHuman' },
  { title: '解压小游戏', name: 'game' },
  { title: '个人日记', name: 'diary' },
  { title: '解压白噪声', name: 'whitenoise' },
]
const showUserInfoModal = ref(false); // 控制弹窗的显示与隐藏

const handleAvatarClick = () => {
  if (!userStore.loggedIn) {
    router.push({ name: 'login' });
    return;
  } else {
    showUserInfoModal.value = true; // 显示弹窗
  }
};

const enterProfile = () => {
  router.push({ name: 'profile' });
  showUserInfoModal.value = false; // 关闭弹窗
};

const closeModal = () => {
  showUserInfoModal.value = false; // 关闭弹窗
};

const changeLogIn = () => {
  userStore.logOut();
  router.push({ name: 'login' });
  return;
}
</script>

<style scoped>
.headimage {
  width: 183px;
  height: 70px;
  filter: invert(1);
}

.header {
  width: 1200px;
  height: 80px;
  margin: 0 auto;
  display: flex;
  align-self: center;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-changeLogin {
  background-color: red;
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-changeLogin:hover {
  background-color: darkred;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  position: absolute;
  top: 5px;
  right: 10px;
}
</style>