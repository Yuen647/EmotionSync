import {describe, it, expect, beforeEach} from 'vitest'
import { mount } from '@vue/test-utils'
import WhiteNoisePage from '@/views/whitenoise/Whitenoise.vue'
import Popup from "@/views/whitenoise/Popup.vue";
import {createTestingPinia} from "@pinia/testing";

describe('Whitenoise.vue', () => {
    let wrapper

    beforeEach(() => {
        wrapper = mount(Popup, {
            global: {
                plugins: [
                    createTestingPinia({
                        stubActions: true, // 可选：防止真实请求
                        createSpy: vi.fn    // 如果你想监视 action 被调用
                    })
                ]
            }
        })
    })
    it('初始应显示 EmotionSelect', () => {
        const wrapper = mount(WhiteNoisePage)
        expect(wrapper.findComponent({ name: 'EmotionSelect' }).exists()).toBe(true)
    })

    it('选择情绪后应显示推荐列表', async () => {
        const wrapper = mount(WhiteNoisePage)

        // 触发 EmotionSelect 的 emotionSelected 事件
        await wrapper.findComponent({ name: 'EmotionSelect' }).vm.$emit('emotionSelected', {
            emotion: 'relaxed',
            recommendedAudios: [
                { audioName: 'Rain', audioSrc: 'rain.mp3', audioIcon: 'rain.svg' },
                { audioName: 'Waves', audioSrc: 'waves.mp3', audioIcon: 'waves.svg' }
            ]
        })

        // 检查 RecommandSound 是否显示
        expect(wrapper.findComponent({ name: 'RecommandSound' }).exists()).toBe(true)
        expect(wrapper.findComponent({ name: 'EmotionSelect' }).exists()).toBe(false)
    })

    it('点击 openPopup 应显示 Popup', async () => {
        const wrapper = mount(WhiteNoisePage)

        // 先模拟 emotion 选择
        await wrapper.findComponent({ name: 'EmotionSelect' }).vm.$emit('emotionSelected', {
            emotion: 'calm',
            recommendedAudios: []
        })

        // 模拟点击了 openPopup
        await wrapper.findComponent({ name: 'RecommandSound' }).vm.$emit('openPopup', 'calm')

        expect(wrapper.findComponent({ name: 'Popup' }).exists()).toBe(true)
    })

    it('Popup 点击 openEmotion 应返回到 EmotionSelect', async () => {
        const wrapper = mount(WhiteNoisePage)

        // 模拟进入 popup 状态
        await wrapper.findComponent({ name: 'EmotionSelect' }).vm.$emit('emotionSelected', {
            emotion: 'focus',
            recommendedAudios: []
        })
        await wrapper.findComponent({ name: 'RecommandSound' }).vm.$emit('openPopup', 'focus')

        // 模拟 popup 返回
        await wrapper.findComponent({ name: 'Popup' }).vm.$emit('openEmotion')

        // 应回到 EmotionSelect
        expect(wrapper.findComponent({ name: 'EmotionSelect' }).exists()).toBe(true)
        expect(wrapper.findComponent({ name: 'RecommandSound' }).exists()).toBe(false)
        expect(wrapper.findComponent({ name: 'Popup' }).exists()).toBe(false)
    })
})
