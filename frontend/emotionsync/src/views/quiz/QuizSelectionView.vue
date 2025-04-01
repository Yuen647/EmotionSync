<template>
    <div class="test-list">
        <div class="test-item" v-for="(item, index) in quizNames">
            <h1>{{ item.name }}</h1>
            <button @click="enterTest(quizNames[index].id)">{{ buttonText[index] }}</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import axios from 'axios';
import router from '@/router';
import { useUserStore } from '@/store/userStore';

const quizNames = ref<{ name: string; id: number }[]>([{name:"MBTI小测试 16题",id:1},{name:"MBTI进阶测试 58题",id:2}]);
const buttonText = ref<string[]>(["开始测试","开始测试"]);

const enterTest = (id: number) => {
    router.push({ name: 'quiz', params: { id: id } });
}

// 在组件挂载时获取测验名称和按钮文字
onMounted(async () => {
    const res = await axios.post('http://localhost:9000/quiz/getQuizNames');
    quizNames.value = res.data;
    buttonText.value = new Array(quizNames.value.length).fill('开始测试(游客)');
    if (useUserStore().loggedIn === 1) {
        for (let i = 0; i < quizNames.value.length; i++) {
            const res = await axios.post('http://localhost:9000/quiz/getQuizResult', {
                username: useUserStore().username,
                quizId: quizNames.value[i].id
            });
            buttonText.value[i] = res.data ? '重新测试' : '开始测试';
        }
    }
    console.log("quizNames: ", quizNames.value);
    console.log("loggedIn: ", useUserStore().loggedIn);
    console.log("buttonText: ", buttonText.value);
});
</script>

<style scoped>
.test-item {
    justify-self: center;
    width: 900px;
    margin: 20px;
    border: 1px solid #ccc;
    border-radius: 10px;
    padding: 100px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 20px;
    background-color: #b7ebfb18;
}

button {
    margin-top: 20px;
    height: 66px;
    width: 200px;
    font-size: 24px;
    background-color: #90d6d1b8;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    justify-self: right;
}

button:hover {
    background-color: #5bdad2b8;
}
</style>