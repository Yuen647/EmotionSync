import AboutView from '@/views/about/AboutView.vue'
import ApplyView from '@/views/AIchat/ApplyView.vue'
import Whitenoise from '@/views/whitenoise/Whitenoise.vue'
import LoginView from '@/views/login/LoginView.vue'
import ProfileView from '@/views/profile/ProfileView.vue'
import QuizView from '@/views/quiz/QuizView.vue'
import QuizSelectionView from '@/views/quiz/QuizSelectionView.vue'
import UserView from '@/views/user/UserView.vue'
import GameView from '@/views/game/GameView.vue'
import webData from '@/views/profile/webData.vue'
import PersonalReport from "@/views/profile/PersonalReport.vue";
import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {
            name: 'home',
            path: '/',
            component: UserView,
            redirect: { name: 'about' },
            children: [
                {
                    name: 'about',
                    meta: {
                        title: 'About',
                    },
                    path: 'about',
                    component: AboutView,
                },
                {
                    name: 'AIDigitalHuman',
                    meta: {
                        title: 'AI数字人',
                    },
                    path: 'AIchat',
                    component: ApplyView,
                },
                {
                    name: 'game',
                    meta: {
                        title: '解压小游戏',
                    },
                    path: 'game',
                    component: GameView,
                },
                {
                    name: 'diary',
                    meta: {
                        title: '心理小测试',
                    },
                    path: '/quizselection',
                    component: QuizSelectionView,
                    props: true  // 允许通过 props 传递路由参数
                },
                {
                    name: 'quiz',
                    meta: {
                        title: '心理小测试',
                    },
                    path: '/quiz/:id',
                    component: QuizView,
                },
                {
                    name: 'whitenoise',
                    meta: {
                        title: '解压白噪声',
                    },
                    path: 'whitenoise',
                    component: Whitenoise,
                },
                {
                    name: 'profile',
                    path: '/profile',
                    component: ProfileView,
                },
                {
                    name: 'webData',
                    path: '/webData',
                    component: webData
                },
                {
                    name: 'PersonalReport',
                    path: '/PersonalReport',
                    component: PersonalReport
                },
            ],
        },
        {
            name: 'login',
            path: '/login',
            component: LoginView,
        },
    ],
})

export default router