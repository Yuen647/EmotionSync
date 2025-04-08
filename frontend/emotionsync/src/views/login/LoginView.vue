<template>
  <div class="w-screen h-screen bg-[#D1E8E7] relative">
    <div class="bg-white w-[500px] rounded-[15px] absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 p-[80px] box-border flex flex-col gap-[16px]"
         style="box-shadow: 0px 6px 24px 0px rgba(14, 182, 152, 0.10);">
      <div class="text-[22px] font-bold">
        {{ type === 'login' ? '登录' : type === 'register' ? '注册' : '重设密码' }}
      </div>
      <!--表示切换登录/注册/修改密码状态-->
      <div class="text-right text-[rgba(0,0,0,0.65)] text-[14px]">
        <template v-if="type === 'login'">
          没有账号？<span class="text-[#0EB698] cursor-pointer" @click="toggleMode">
                        注册
                    </span>
        </template>
        <template v-if="type === 'register'">
          已有账号？<span class="text-[#0EB698] cursor-pointer" @click="toggleMode">
                        登录
                    </span>
        </template>
        <template v-if="type === 'reset-password'">
                    <span class="text-[#0EB698] cursor-pointer" @click="type = 'login'">
                        登录
                    </span>
        </template>
      </div>
      <div v-if="type === 'reset-password' && waitVerify" class="text-[rgba(0,0,0,0.85)] text-[14px]">
        设置一个新的密码
      </div>


      <el-input type="text" v-model="form.username" style="--el-color-primary: #0EB698"
                v-if="type === 'login' || (type === 'reset-password' && !waitVerify) || (type === 'register' && !waitVerify)">
        <template #prefix>
          <!-- 替换为小人图标 -->
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- 圆形头部 -->
            <circle cx="12" cy="8" r="4" fill="#0EB698" stroke="#4C4C4C"/>
            <!-- 身体 -->
            <path d="M6 20c0-4 4-6 6-6s6 2 6 6" fill="#0EB698" fill-opacity="0.2" stroke="#4C4C4C" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <div class="ml-[8px] w-[70px] text-left">用户名</div>
        </template>
      </el-input>

      <el-input type="password" show-password v-model="form.password" style="--el-color-primary: #0EB698"
                v-if="type === 'login' || (type === 'register' && !waitVerify)">
        <template #prefix>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path
                d="M14.1667 8.33162H5.83333C5.3731 8.33162 5 8.70472 5 9.16496V14.9983C5 15.4585 5.3731 15.8316 5.83333 15.8316H14.1667C14.6269 15.8316 15 15.4585 15 14.9983V9.16496C15 8.70472 14.6269 8.33162 14.1667 8.33162Z"
                fill="#0EB698" fill-opacity="0.2" stroke="#4C4C4C" stroke-linejoin="round" />
            <path
                d="M7.5 8.33162V5.83162C7.5 4.2975 8.61929 3.33162 10 3.33162C11.3807 3.33162 12.5 4.2975 12.5 5.83162V8.33162"
                fill="#0EB698" fill-opacity="0.2" />
            <path
                d="M7.5 8.33162V5.83162C7.5 4.2975 8.61929 3.33162 10 3.33162C11.3807 3.33162 12.5 4.2975 12.5 5.83162V8.33162"
                stroke="#4C4C4C" stroke-linecap="round" stroke-linejoin="round" />
            <path d="M10 11.665V12.4983V11.665Z" fill="#0EB698" fill-opacity="0.2" />
            <path d="M10 11.665V12.4983" stroke="#4C4C4C" stroke-linecap="round" stroke-linejoin="round" />
            <path d="M2.5 7.49829V12.4983V7.49829Z" fill="#0EB698" fill-opacity="0.2" />
            <path d="M2.5 7.49829V12.4983" stroke="#4C4C4C" stroke-linecap="round"
                  stroke-linejoin="round" />
            <path d="M17.5 7.49829V12.4983" stroke="#4C4C4C" stroke-linecap="round"
                  stroke-linejoin="round" />
          </svg>
          <div class="ml-[8px] w-[70px] text-left">Password</div>
        </template>
      </el-input>

      <el-input type="password" show-password v-model="form.password" style="--el-color-primary: #0EB698"
                v-if="type === 'reset-password'">
        <template #prefix>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14.1667 8.33162H5.83333C5.3731 8.33162 5 8.70472 5 9.16496V14.9983C5 15.4585 5.3731 15.8316 5.83333 15.8316H14.1667C14.6269 15.8316 15 15.4585 15 14.9983V9.16496C15 8.70472 14.6269 8.33162 14.1667 8.33162Z" fill="#0EB698" fill-opacity="0.2" stroke="#4C4C4C" stroke-linejoin="round" />
            <path d="M7.5 8.33162V5.83162C7.5 4.2975 8.61929 3.33162 10 3.33162C11.3807 3.33162 12.5 4.2975 12.5 5.83162V8.33162" fill="#0EB698" fill-opacity="0.2" />
            <path d="M7.5 8.33162V5.83162C7.5 4.2975 8.61929 3.33162 10 3.33162C11.3807 3.33162 12.5 4.2975 12.5 5.83162V8.33162" stroke="#4C4C4C" stroke-linecap="round" stroke-linejoin="round" />
            <path d="M10 11.665V12.4983" stroke="#4C4C4C" stroke-linecap="round" stroke-linejoin="round" />
          </svg>
          <div class="ml-[8px] w-[70px] text-left">新密码</div>
        </template>
      </el-input>
      <template v-if="type === 'register'">
        <!--
        <div class="flex justify-between">
            <input v-for="_, i in 6" class="w-[40px] h-[40px] outline-[#0EB698] text-center text-[16px]"
                :ref="(el: any) => inputsRef[i] = el" v-model="form.code[i]" @input="handleInput($event, i)"
                @keydown.delete="handleDelete(i)" />
        </div>
        -->
      </template>
      <el-input type="email" v-model="form.email" style="--el-color-primary: #0EB698"
                v-if="type === 'reset-password' && !waitVerify || (type === 'register' && !waitVerify)">
        <template #prefix>
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M3 14.9983H16.3333V9.99829V4.99829H9.66667H3V9.99829V14.9983Z" fill="#0EB698"
                  fill-opacity="0.2" stroke="#4C4C4C" stroke-linejoin="round" />
            <path d="M3 4.99829L9.66667 9.99829L16.3333 4.99829" fill="#0EB698" fill-opacity="0.2" />
            <path d="M3 4.99829L9.66667 9.99829L16.3333 4.99829" stroke="#4C4C4C" stroke-linecap="round"
                  stroke-linejoin="round" />
            <path d="M9.66667 4.99829H3V9.99829" fill="#0EB698" fill-opacity="0.2" />
            <path d="M9.66667 4.99829H3V9.99829" stroke="#4C4C4C" stroke-linecap="round"
                  stroke-linejoin="round" />
            <path d="M16.3334 9.99829V4.99829H9.66675" fill="#0EB698" fill-opacity="0.2" />
            <path d="M16.3334 9.99829V4.99829H9.66675" stroke="#4C4C4C" stroke-linecap="round"
                  stroke-linejoin="round" />
          </svg>
          <div class="ml-[8px] w-[70px] text-left">邮箱</div>
        </template>
      </el-input>
      <!-- 输入验证码-->
      <el-input v-model="form.code" type="text" placeholder="输入验证码" v-if="!showVerifyInput && (type === 'reset-password' && !waitVerify || (type === 'register' && !waitVerify))">
        <template #prefix>
          <div class="ml-[8px] w-[70px] text-left">验证码</div>
        </template>
      </el-input>
      <!-- 发送验证码按钮 -->
      <el-button type="primary" :disabled="!form.email" @click="sendCode" v-if="!showVerifyInput && (type === 'reset-password' && !waitVerify || (type === 'register' && !waitVerify))">发送验证码</el-button>

      <!-- 验证验证码按钮 -->
      <el-button type="primary" :disabled="!form.code" @click="verifyCode" v-if="!showVerifyInput && (type === 'reset-password' && !waitVerify || (type === 'register' && !waitVerify))">验证验证码</el-button>
      <br>
      <div v-if="(type === 'register' && !waitVerify) || (type === 'reset-password' && waitVerify)"
           class='opacity-35 text-left text-[14px]'>密码请输入至少6个字符，包括字母和数字</div>
      <div v-if="type === 'reset-password' && !waitVerify" class="text-[rgba(0,0,0,0.35)] text-[12px]">请重新设置密码，密码请输入至少6个字符，包括字母和数字</div>
      <div class="text-right">
                <span v-if="type === 'login'" class="text-[#0EB698] text-[14px] cursor-pointer"
                      @click="type = 'reset-password'">忘记密码?</span>
      </div>
      <template v-if="type === 'login'">
        <div>
          <el-button type="primary" class="w-full" style="height: 45px;" :style="buttonStyle"
                     @click="handleLogin">
            登录
          </el-button>
        </div>

      </template>
      <template v-if="type === 'register'">

        <div>
          <el-button type="primary" class="w-full" style="height: 45px;" :style="buttonStyle"
                     @click="handleSignup">
            注册
          </el-button>
        </div>
      </template>
      <div v-if="type === 'reset-password'">
        <el-button type="primary" class="w-full" style="height: 45px;" :style="buttonStyle"
                   :disabled="!form.email" @click="handleResetPasswordEmail">
          重新设置
        </el-button>
      </div>

    </div>

  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import { useUserStore } from '@/store/userStore';
const router = useRouter();
const type = ref<'login' | 'register' | 'reset-password'>('login')
const showVerifyInput = ref(false); // 控制验证码输入框是否显示
const waitVerify = ref(false);  // 控制是否在等待验证码验证
const route = useRoute()
const userStore = useUserStore();

const form = reactive({
  email: '',
  username: '',
  password: '',
  code: '',
});

const buttonStyle = {
  '--el-color-primary': '#0EB698',
  '--el-button-hover-bg-color': '#0EB69877',
  '--el-button-hover-border-color': '#0EB69877',
  '--el-button-disabled-bg-color': 'rgba(139, 139, 139, 0.25)',
  '--el-button-disabled-border-color': 'rgba(139, 139, 139, 0.25)',
  '--el-button-active-bg-color': '#0EB698',
  '--el-button-active-border-color': '#0EB698',
};

const handleLogin = async () => {
  try {
    const response = await axios.post('http://localhost:9000/myHello/login', form);
    console.log(form.username);
    if (response.data.message === '登录成功') {
      userStore.logIn();
      userStore.setUsername(form.username); // 保存 username 到全局状态
      goToHome();
    } else {
      alert(response.data.message || '登录失败，请重试');
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert(error.response.data.message || '网络错误，请稍后再试');
  }
};

const handleSignup = async () => {
  try {
    const response = await axios.post('http://localhost:9000/myHello/register', form);
    console.log(response.data);

    if (response.data.success) {
      // 注册成功逻辑
      alert(response.data.message);
      userStore.logIn();
      userStore.setUsername(form.username); // 保存 username 到全局状态
      goToHome();
    } else {
      // 注册失败逻辑
      alert(response.data.message || '注册失败，请重试');
    }
  } catch (error) {
    console.error('注册失败:', error);
    alert(error.response.data.message || '网络错误，请稍后再试');
  }
};

const handleResetPasswordEmail = async () => {
  try {
    const response = await axios.post('http://localhost:9000/myHello/reset-password', form);
    console.log(response.data);
    if (response.data.message === '密码重置成功') {
      userStore.logIn();
      userStore.setUsername(form.username); // 保存 username 到全局状态
      goToHome();
    } else {
      alert(response.data.message || '请求失败，请稍后再试');
    }
  } catch (error) {
    console.error('重置密码失败:', error);
    alert(error.response.data.message || '网络错误，请稍后再试');
  }
};

// 发送验证码
const sendCode = async () => {
  try {
    const response = await axios.post('http://localhost:9000/api/verify/send', { email: form.email });
    if (response.data.success) {
      alert('验证码发送成功，请查收');
      showVerifyInput.value = true;  // 显示验证码输入框
    } else {
      alert(response.data.message || '验证码发送失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    alert(error.response?.data.message || '网络错误，请稍后再试');
  }
};
// 验证验证码
const verifyCode = async () => {
  try {
    const response = await axios.post('http://localhost:9000/api/verify/check', {
      email: form.email,
      code: form.code,
    });
    if (response.data.success) {
      alert('验证码验证成功');
      waitVerify.value = false;  // 验证成功后可以继续注册/重置密码流程
    } else {
      alert(response.data.message || '验证码验证失败');
    }
  } catch (error) {
    console.error('验证验证码失败:', error);
    alert(error.response?.data.message || '网络错误，请稍后再试');
  }
};

const toggleMode = () => {
  if (type.value === 'login') {
    type.value = 'register';
  } else {
    type.value = 'login';
  }
};

onMounted(() => {
  if (route.query.type === 'reset-password') {
    type.value = 'reset-password';
    waitVerify.value = true;
  }
});

const goToHome = () => {
  router.push({ name: 'home' })
}

</script>