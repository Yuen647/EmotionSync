<template>
  <div class="w-screen h-screen bg-[#D1E8E7] relative">
    <div class="bg-white w-[500px] rounded-[15px] absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 p-[80px] box-border flex flex-col gap-[16px]"
         style="box-shadow: 0px 6px 24px 0px rgba(14, 182, 152, 0.10);">
      <div class="text-[22px] font-bold" data-testid="auth-title">
        {{ type === 'login' ? '登录' : type === 'register' ? '注册' : '重设密码' }}
      </div>
      <!--表示切换登录/注册/修改密码状态-->
      <div class="text-right text-[rgba(0,0,0,0.65)] text-[14px]">
        <template v-if="type === 'login'">
          没有账号？<span class="text-[#0EB698] cursor-pointer" data-testid="toggle-to-register" @click="toggleMode">
                        注册
                    </span>
        </template>
        <template v-if="type === 'register'">
          已有账号？<span class="text-[#0EB698] cursor-pointer" data-testid="toggle-to-login" @click="toggleMode">
                        登录
                    </span>
        </template>
        <template v-if="type === 'reset-password'">
                    <span class="text-[#0EB698] cursor-pointer" data-testid="back-to-login" @click="type = 'login'">
                        登录
                    </span>
        </template>
      </div>
      <div v-if="type === 'reset-password' && waitVerify" class="text-[rgba(0,0,0,0.85)] text-[14px]" data-testid="reset-password-hint">
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
      <!-- 验证码输入框 -->
      <div v-if="(type === 'reset-password' && !waitVerify) || (type === 'register' && !waitVerify)">
        <el-input v-model="form.code" type="text" placeholder="输入验证码" style="--el-color-primary: #0EB698">
          <template #prefix>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none"
                 xmlns="http://www.w3.org/2000/svg" class="mr-2">
              <path d="M12 2L4 5V11C4 16 7.6 20.3 12 22C16.4 20.3 20 16 20 11V5L12 2Z"
                    stroke="#4C4C4C" stroke-width="1.5" stroke-linejoin="round"
                    fill="#0EB698" fill-opacity="0.2" />
              <path d="M9 12L11 14L15 10" stroke="#4C4C4C" stroke-width="1.5"
                    stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <div class="ml-[8px] w-[70px] text-left">验证码</div>
          </template>
          <template #append>
            <el-button
                type="primary"
                size="small"
                style="color: blue;"
                :disabled="!form.email || countdown > 0"
                @click="sendCode"
            >
              {{ countdown > 0 ? countdown + ' 秒后重试' : '发送验证码' }}
            </el-button>
          </template>
        </el-input>

        <div v-if="codeSent" class="text-green-500 text-sm mt-1" data-testid="code-sent-message">验证码已发送</div>

      </div>
      <div v-if="(type === 'register' && !waitVerify) || (type === 'reset-password' && waitVerify)"
           class='opacity-35 text-left text-[14px]'>密码请输入至少6个字符，包括字母和数字</div>
      <div v-if="type === 'reset-password' && !waitVerify" class="text-[rgba(0,0,0,0.35)] text-[12px]" data-testid="toggle-to-reset">请重新设置密码，密码请输入至少6个字符，包括字母和数字</div>
      <div class="text-right">
                <span v-if="type === 'login'" class="text-[#0EB698] text-[14px] cursor-pointer" data-testid="forgot-password"
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
                     data-testid="register-button" @click="handleSignup">
            注册
          </el-button>
        </div>
      </template>
      <div v-if="type === 'reset-password'">
        <el-button type="primary" class="w-full" style="height: 45px;" :style="buttonStyle"
                   :disabled="!form.email" @click="handleResetPasswordEmail" data-test="reset-password-button">
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
const codeSent = ref(false);  // 判断验证码是否发送
const countdown = ref(0);
const timer = ref(null);

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
    const response = await axios.post('http://localhost:9000/myHello/login', {
      username: form.username,
      password: form.password,
    });
    if (response.data.message === '登录成功') {
      const token = response.data.token;
      // 存储 token
      localStorage.setItem('token', token);
      userStore.logIn();
      userStore.setUsername(form.username); // 保存 username 到全局状态
      goToHome();
    } else {
      alert(response.data.message || '登录失败，请重试');
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert(error.response?.data.message || '网络错误，请稍后再试');
  }
};

const handleSignup = async () => {
  try {
    // 第一步：验证验证码是否正确
    const verifyResponse = await axios.post('http://localhost:9000/api/verify/check', {
      email: form.email,
      code: form.code,
    });

    if (verifyResponse.data.message === '验证码正确') {
      waitVerify.value = false;  // 验证通过，允许进入注册流程

      try {
        // 第二步：调用注册接口
        const registerResponse = await axios.post('http://localhost:9000/myHello/register', form);
        if (registerResponse.data.success) {
          userStore.logIn();
          userStore.setUsername(form.username);  // 保存用户名到全局状态
          const token = registerResponse.data.token;
          // 存储 token
          localStorage.setItem('token', token);
          goToHome();
        } else {
          alert(registerResponse.data.message || '注册失败，请重试');
        }
      } catch (registerError) {
        console.error('注册失败:', registerError);
        alert(registerError.response?.data?.message || '网络错误，请稍后再试');
      }
    } else {
      alert(verifyResponse.data.message || '验证码验证失败');
    }
  } catch (verifyError) {
    console.error('验证码验证失败:', verifyError);
    alert(verifyError.response?.data?.message || '网络错误，请稍后再试');
  }
};


const handleResetPasswordEmail = async () => {
  try {
    const response = await axios.post('http://localhost:9000/api/verify/check', {
      email: form.email,
      code: form.code,
    });
    if (response.data.message === '验证码正确') {
      waitVerify.value = false;  // 验证成功后可以继续注册/重置密码流程
      try {
        const response = await axios.post('http://localhost:9000/myHello/reset-password', form);
        if (response.data.message === '密码重置成功') {
          userStore.logIn();
          userStore.setUsername(form.username); // 保存 username 到全局状态
          const token = response.data.token;
          localStorage.setItem('token', token);
          goToHome();
        } else {
          alert(response.data.message || '请求失败，请稍后再试');
        }
      } catch (error) {
        console.error('重置密码失败:', error);
        if (error.response && error.response.data) {
          alert(error.response.data.message || '请求失败，请稍后再试');
        } else {
          alert('网络错误，请稍后再试');
        }
      }
    } else {
      alert(response.data.message || '验证码验证失败');
    }
  } catch (error) {
    console.error('验证验证码失败:', error);
    alert(error.response?.data.message || '网络错误，请稍后再试');
  }
};


// const sendCode = async () => {
//   if (countdown.value > 0) return // 避免重复点击
//   try {
//     const response = await axios.post('http://localhost:9000/api/verify/send', { email: form.email });
//     if (response.data.message === '验证码已发送') {
//       codeSent.value = true; // ✅ 显示“验证码已发送”
//       showVerifyInput.value = true;  // 显示验证码输入框
//       countdown.value = 60; // 设置倒计时为 60 秒
//       timer.value = setInterval(() => {
//         countdown.value--
//         if (countdown.value <= 0) {
//           clearInterval(timer.value)
//         }
//       }, 1000)
//     } else {
//       alert(response.data.message || '验证码发送失败');
//     }
//   } catch (error) {
//     console.error('发送验证码失败:', error);
//     alert(error.response?.data.message || '网络错误，请稍后再试');
//   }
// };
const sendCode = async () => {
  if (countdown.value > 0) return;

  try {
    const response = await axios.post('http://localhost:9000/api/verify/send', {
      email: form.email
    });

    if (response.data?.message === '验证码已发送') {
      codeSent.value = true;
      countdown.value = 60;

      // 使用 clearInterval 确保不会重复设置定时器
      if (timer.value) clearInterval(timer.value);

      timer.value = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0 && timer.value) {
          clearInterval(timer.value);
          timer.value = null;
        }
      }, 1000);
    } else {
      // 使用更安全的错误处理
      alert(response.data?.message || '验证码发送失败');
    }
  } catch (error) {
    console.error('发送验证码失败:', error);
    // 安全地获取错误信息
    const errorMessage = error.response?.data?.message ||
        error.message ||
        '网络错误，请稍后再试';
    alert(errorMessage);
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