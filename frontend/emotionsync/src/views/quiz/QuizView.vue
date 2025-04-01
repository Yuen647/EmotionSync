<template>
  <div class="container">
    <div v-if="testStatus === true">
      <h2>测试结束</h2>
      <p>你的人格类型是：{{ getResult() }}</p>
      <p>感谢参与测试！</p>
      <button @click="restartTest">重新测试</button>
      <button @click="goMenu">返回选择页面</button>
    </div>
    <div v-else>
      <div v-for="(question, index) in questions" :key="index">
        <h2>{{ index + 1 }}. {{ question.description }}</h2>
        <div class="options">
          不认同--
          <span v-for="level in levels" :key="level" class="option" @click="selectAnswer(index, level)"
            :style="{ backgroundColor: getColor(level), borderColor: getBorderColor(index, level) }" />
          --认同
        </div>
      </div>
      <button @click="submitTest">提交测试</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import axios from "axios";
import router from '@/router';
import { useUserStore } from '@/store/userStore';

interface answer {
  E: number; // 外向
  I: number; // 内向
  S: number; // 感觉
  N: number; // 直觉
  T: number; // 思考
  F: number; // 感情
  J: number; // 判断
  P: number; // 知觉
}

const username = useUserStore().username;
const levels = [-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5]; // 认同程度
let questions = ref<{ questionId: number, quizId: number, description: string, dimension: string }[]>([]);

let testStatus = ref(false);
let userAnswers = ref<number[]>(new Array(questions.value.length).fill(0)); // 存储用户的答案
let answers = ref<answer>({ "E": 0, "I": 0, "S": 0, "N": 0, "T": 0, "F": 0, "J": 0, "P": 0 }); // 存储答案

const selectAnswer = (index: number, level: number) => {
  userAnswers.value[index] = level; // 更新用户选择的答案
};

const submitTest = async () => {
  for (let i = 0; i < questions.value.length; i++) {
    const selection = userAnswers.value[i];
    if (selection !== undefined) {
      const question = questions.value[i];

      // 根据选择的认同程度更新得分
      if (question.dimension === "E") {
        answers.value.E += selection; // 认同程度累加到E
        answers.value.I -= selection; // 认同程度减少到I
      } else if (question.dimension === "I") {
        answers.value.I += selection; // 认同程度累加到I
        answers.value.E -= selection; // 认同程度减少到E
      } else if (question.dimension === "S") {
        answers.value.S += selection; // 认同程度累加到S
        answers.value.N -= selection; // 认同程度减少到N
      } else if (question.dimension === "N") {
        answers.value.N += selection; // 认同程度累加到N
        answers.value.S -= selection; // 认同程度减少到S
      } else if (question.dimension === "T") {
        answers.value.T += selection; // 认同程度累加到T
        answers.value.F -= selection; // 认同程度减少到F
      } else if (question.dimension === "F") {
        answers.value.F += selection; // 认同程度累加到F
        answers.value.T -= selection; // 认同程度减少到T
      } else if (question.dimension === "J") {
        answers.value.J += selection; // 认同程度累加到J
        answers.value.P -= selection; // 认同程度减少到P
      } else if (question.dimension === "P") {
        answers.value.P += selection; // 认同程度累加到P
        answers.value.J -= selection; // 认同程度减少到J
      }
    } else {
      alert("第"+ (i+1) +"题未作答，请选择答案");
      return;
    }
  }
  if (username !== "" && username !== undefined)
    await axios.post("http://localhost:9000/quiz/submitQuiz", {
      quizId: router.currentRoute.value.params.id,
      username: username,
      E: answers.value.E,
      I: answers.value.I,
      S: answers.value.S,
      N: answers.value.N,
      T: answers.value.T,
      F: answers.value.F,
      J: answers.value.J,
      P: answers.value.P
    });
  testStatus.value = true;
}

const getResult = () => {
  const { E, I, S, N, T, F, J, P } = answers.value;
  return (E > I ? 'E' : 'I') +
    (S > N ? 'S' : 'N') +
    (T > F ? 'T' : 'F') +
    (J > P ? 'J' : 'P');
};

const restartTest = () => {
  userAnswers.value = new Array(questions.value.length).fill(0);
  answers.value = {
    E: 0,
    I: 0,
    S: 0,
    N: 0,
    T: 0,
    F: 0,
    J: 0,
    P: 0,
  };
  testStatus.value = false;
};

const goMenu = () => {
  router.push({ name: 'quizselection' });
}

const getColor = (level: number) => {
  // 根据级别返回对应的颜色
  const colorMap: Record<number, string> = {
    '-5': 'rgba(5,170,245,0.8)',
    '-4': 'rgba(20,190,200,0.8)',
    '-3': 'rgba(50,200,150,0.8)',
    '-2': 'rgba(95,200,115,0.8)',
    '-1': 'rgba(145,200,80,0.8)',
    0: 'rgba(195,200,80,0.8)',
    1: 'rgba(200,180,80,0.8)',
    2: 'rgba(200,160,80,0.8)',
    3: 'rgba(200,145,80,0.8)',
    4: 'rgba(200,115,80,0.8)',
    5: 'rgba(200,80,80,0.8)'
  };
  return colorMap[level] || '#ffffff'; // 默认颜色为白色
};

const getBorderColor = (index: number, level: number) => {
  if (userAnswers.value[index] === level)
    return '#8e8e8e';
  return 'rgba(255,255,255,0)';
}

onMounted(async () => {
  const response = await axios.post("http://localhost:9000/quiz/getQuestions",
    { id: router.currentRoute.value.params.id });
  questions.value = response.data;
});
</script>

<style scoped>
.container {
  justify-self: center;
  justify-content: center;
  text-align: center;
  font-family: Arial, sans-serif;
  margin-top: 66px;
}

.options {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 60px;
}

.option {
  padding: 8px;
  width: 50px;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
  background-color: #5d8bbd;
  border-style: solid;
  border-width: 3px;
}

.option:hover {
  transform: scale(1.05);
}

button {
  margin: 20px;
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style>