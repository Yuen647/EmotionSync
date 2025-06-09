import { mount } from '@vue/test-utils'
import { describe, it, expect, beforeEach } from 'vitest'
import Popup from '@/views/whitenoise/Popup.vue'
import { createTestingPinia } from '@pinia/testing'



// 模拟 Audio 构造函数，防止真实音频加载
class MockAudio {
    paused = true
    loop = false
    volume = 1
    play = vi.fn().mockResolvedValue(undefined)
    pause = vi.fn()
}
globalThis.Audio = MockAudio as any

describe('Popup.vue', () => {
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

    it('should toggle UI visibility', async () => {
        expect(wrapper.vm.isUIVisible).toBe(true)
        wrapper.vm.toggleUI()
        expect(wrapper.vm.isUIVisible).toBe(false)
    })

    it('should toggle dark mode', () => {
        expect(wrapper.vm.isDarkMode).toBe(false)
        wrapper.vm.toggleDarkMode()
        expect(wrapper.vm.isDarkMode).toBe(true)
    })

    it('should switch audio mode', () => {
        expect(wrapper.vm.isAudio).toBe(false)
        wrapper.vm.toggleAudio()
        expect(wrapper.vm.isAudio).toBe(true)
    })

    it('should play single audio', async () => {
        await wrapper.vm.playSingleAudio('test-url.mp3', 'Rain')
        expect(wrapper.vm.isPlaying).toBe(true)
        expect(wrapper.vm.currentAudioName).toBe('Rain')
    })

    it('should emit openEmotion on backToEmotion()', () => {
        wrapper.vm.backToEmotion()
        expect(wrapper.emitted()).toHaveProperty('openEmotion')
    })
})
