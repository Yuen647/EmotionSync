<template>
    <div class="w-[1120px] mx-auto">
        <div
            class="mt-[70px] relative before:absolute before:mr-[36px] before:w-[16px] before:bg-gradient-to-r before:from-primary before:to-[#43B8EA] before:h-[56px] before:block before:content-['']">
            <div class="flex text-[36px] font-bold leading-none ml-[60px]">
                {{ config.apply.title }}
            </div>
        </div>
        <div class="flex text-[16px] font-bold leading-none ml-[60px]">
          <p>当前用户:{{username}}</p>
        </div>
        <div class="flex text-[16px] font-bold leading-none ml-[60px]">
          <p>当前情绪：{{ mood }}</p>
        </div>
        <div class="chat-container">
            <div class="chat-item">
                <div class="chat-answer">
                {{ start }}
                </div>
            </div>
            <div class="chat-item" v-for="(message, index) in messages" :key="index">
            <!-- 用户发送的消息 -->
            <div class="chat-ask" v-if="message.role==='user'">{{ message.content }}</div>
            <!-- AI 回复的消息 -->
            <div class="chat-answer" v-if="message.role==='system'">{{ message.content }}</div>
            <!-- 渲染与消息对应的音频 -->
            <audio controls v-if="index===0">
              <source src="./start.wav" type="audio/wav"/>
            </audio>
            <div v-for="(src,index1) in audiourllist" :key="index1">
            <!-- 保证audioRef存在可以执行push，第一个不显示，只显示第二个，可能算一个bug -->
              <audio ref="audioRef" controls style="display: none;">
              </audio>
              <audio v-if="message.role==='system'&& index!=0 && index1*2===index" :src="src.src" 
              controls
              :autoplay="index1 === audiourllist.length - 1" 
              @canplay="index1 === audiourllist.length - 1 && $event.target.play()"
              >
              </audio>
            </div>
           </div>
            <el-button type="primary" @click="open">设置情绪</el-button>
            <el-button type="primary" id="start-btn" @click="startRecognition">开始语音输入</el-button>
            <el-button type="primary" @click="savemessage">保存对话</el-button>
            <el-button type="primary" @click="showmessage">查看上一次对话</el-button>
            <el-button type="primary" @click="changemethod">{{auto}}</el-button>
            <el-button type="primary" v-if="auto==='手动发送'" @click="sendmessage">发送</el-button>

            <div>
              <p></p>
              <el-input v-model="result" :disabled="auto==='自动发送'" style="width: 100%" placeholder="Please input" />
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { useConfig } from '@/composables/config';
import {ref, onMounted,watch} from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox, ElButton } from 'element-plus'
import { useUserStore } from '@/store/userStore';

const start="你好！有什么我能帮助你的吗？\n"
const auto=ref("自动发送")
//暂时是这样的，可能还要修改
const user=useUserStore()
const username=ref(user.username)
//添加信息使用.push
const messages=ref(user.messages)
const mood=ref(user.mood)
const audiourllist=ref(user.audiourllist)
const audioBlob = ref<Blob | null>(null);
const audioUrl = ref<string | null>(null);

// 音频 DOM 元素
const audioRef = ref<HTMLAudioElement | null>(null);

// 获取音频流并播放
const fetchAndPlayAudio = async (resulttext) => {
  try {
    const response = await axios.post("http://127.0.0.1:9880",{
    "refer_wav_path": "nahida.wav",
    "prompt_text": "初次见面，我已经关注你很久了。",
    "prompt_language": "zh",
    "text":resulttext,
    "text_language": "zh"
    }, { responseType: "arraybuffer" });
    if (response.status===400) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    console.log(response);

    // 转换为 Blob
    audioBlob.value = new Blob([response.data], { type: "audio/wav" });

    // 创建 Blob URL
    audioUrl.value = URL.createObjectURL(audioBlob.value);

    console.log(audioRef.value);
    // 播放音频
    if (audioRef.value) {
      audioRef.value.src = audioUrl.value;
      audiourllist.value.push({"src":audioUrl.value});
      console.log(audiourllist.value);
      await audioRef.value.play();
      console.log("音频播放成功");
    }
  } catch (error) {
    console.error("获取或播放音频失败:", error);
  }
};

const config = useConfig();
const result=ref("");
// 检查浏览器是否支持 SpeechRecognition
const recognitionSupported = "webkitSpeechRecognition" in window;
let startRecognition: any;

if (recognitionSupported) {
  // 初始化语音识别实例
  const recognition = new webkitSpeechRecognition();
  recognition.lang = "zh-CN"; // 设置语言为中文

  // 定义开始语音识别的方法
  startRecognition = () => {
      recognition.start(); // 开始识别
      result.value = "正在识别";
      recognition.onresult = (event: { results: { transcript: any; }[][]; }) => {
        const speechResult = event.results[0][0].transcript;
        result.value = speechResult;
        sendmessage();
     };

      recognition.onerror = (event) => {
        ElMessageBox.alert('存在错误', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          ElMessage({
            type: 'error',
            message: `${event.error} `,
          })
        },
      })
      };
    };
  }
  else {
    console.error("浏览器不支持 SpeechRecognition");
    // 定义一个占位函数防止报错
    startRecognition = () => {
      result.value = "当前浏览器不支持语音识别功能。";
    };
  }

const changemethod=()=>{
  if(auto.value==="自动发送"){
    auto.value="手动发送";
    result.value='';
  }
  else{
    auto.value="自动发送";
  }
}

const sendmessage=()=>{
  messages.value.push({"role": "user", "content": result.value});
  axios.post("http://localhost:9000/chat/send",messages.value).then(res=>{
    messages.value.push({"role": "system", "content": res.data});
    fetchAndPlayAudio(res.data)
    console.log(messages);
    //清空输入框
    result.value='';
  })

}

const savemessage=()=>{
  // 转换成字符串
  const messageString = messages.value
    .map(item => `${item.role}: ${item.content}`) // 将每个对象变成 "role: content" 格式
    .join("\n"); // 用换行符拼接成一个完整的字符串
  axios.post("http://localhost:9000/chat/add",{username:username.value,chatmessage:messageString}).then(res=>{
    if (res.status === 200){
      ElMessageBox.alert('已经保存', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          ElMessage({
            type: 'info',
            message: `已保存至用户名:${username.value} `,
          })
        },
      })
    }
    else{
      ElMessageBox.alert('保存出错', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          ElMessage({
            type: 'error',
            message: `错误`,
          })
        },
      })
    }
  })
}

const showmessage=()=>{
  axios.post("http://localhost:9000/chat/get",{username:username.value}).then(res=>{
    if (res.status === 200){
      ElMessageBox.alert("上一次对话如下：\n"+res.data, '查看', {
        confirmButtonText: '确定',
        callback: () => {
          ElMessage({
            type: 'success',
            message: `已成功查询 `,
          })
        },
      })
    }
    else{
      ElMessageBox.alert('提取错误，未找到上一次对话', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          ElMessage({
            type: 'error',
            message: `错误`,
          })
        },
      })
    }
  })
}
const open = () => {
  ElMessageBox.prompt('你当前的情绪为', 'Tip', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
    .then(({ value }) => {
      user.setMood(value);
      mood.value=value;
      axios.post("http://localhost:9000/chat/getMood",{"mood":mood.value})
      ElMessage({
        type: 'success',
        message: `你当前的情绪是${value}`,
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '取消更改',
      })
    })
}
</script>
<style lang="less" scoped>
:deep(.my-content) {
    display: inline-block;
    background: #EFEFEF;
    width: 807px;
}

:deep(.my-label) {
    display: inline-block;
    width: 186px;
}
.chat-container {
  padding: 20px;
  background-color: #f0f4f8;
}

.chat-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 16px;
}

.chat-ask {
  /* 右对齐，模拟用户的消息 */
  align-self: flex-end;
  background: #419fff;
  color: #fff;
  border-radius: 12px 2px 12px 12px;
  padding: 8px;
  max-width: 80%; /* 确保内容不超过屏幕宽度 */
  word-break: break-all; /* 长单词自动换行 */
  margin-bottom: 10px;
}
.chat-answer {
  /* 左对齐，模拟AI的回复 */
  align-self: flex-start;
  background: #daedff;
  color: #333;
  border-radius: 12px;
  padding: 8px;
  max-width: 80%; /* 确保内容不超过屏幕宽度 */
  word-break: break-all; /* 长单词自动换行 */
}
.dialog-footer {
  text-align: right;
}
</style>