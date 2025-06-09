import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginComponent from '@/views/login/LoginView.vue'
import { createPinia } from 'pinia'
import axios from 'axios'
import flushPromises from 'flush-promises'
import { createRouter, createWebHistory } from 'vue-router'

// 模拟路由和组件
const routes = [
  { path: '/', name: 'home', component: { template: '<div>Home</div>' } },
  { path: '/login', name: 'login', component: LoginComponent }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

vi.mock('axios')

// 模拟 alert
global.alert = vi.fn()

describe('LoginComponent 集成测试', () => {
  let wrapper

  beforeEach(async () => {
    vi.clearAllMocks()
    const pinia = createPinia()

    wrapper = mount(LoginComponent, {
      global: {
        plugins: [pinia, router]
      },
      attachTo: document.body // 需要挂载到 document 才能测试真实表单交互
    })

    await router.isReady()
  })

  it('用户完整登录流程：填写表单 -> 登录成功 -> 跳转首页', async () => {
    axios.post.mockResolvedValue({
      data: { message: '登录成功', token: 'mock-token' }
    })

    await wrapper.find('input[type="text"]').setValue('000001')
    await wrapper.find('input[type="password"]').setValue('123456')

    await wrapper.find('button').trigger('click')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe('mock-token')
    // 检查页面跳转是否正确
    expect(router.currentRoute.value.name).toBe('home')
  })

  it('用户完整注册流程：切换到注册 -> 填写表单 -> 验证码 -> 注册成功', async () => {
    axios.post.mockImplementation(url => {
      if (url.includes('verify/check')) {
        return Promise.resolve({ data: { message: '验证码正确' } })
      }
      if (url.includes('register')) {
        return Promise.resolve({
          data: {
            success: true,
            message: '注册成功',
            token: 'register-token'
          }
        })
      }
    })

    // 切换到注册页
    await wrapper.find('[data-testid="toggle-to-register"]').trigger('click')

    await wrapper.find('input[type="text"]').setValue('newuser')
    await wrapper.find('input[type="password"]').setValue('newpass')
    await wrapper.find('input[type="email"]').setValue('reg@example.com')

    wrapper.vm.form.code = '123456'

    await wrapper.find('[data-testid="register-button"]').trigger('click')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe('register-token')
    expect(router.currentRoute.value.name).toBe('home')
  })

  it('用户完整重置密码流程：切换 -> 填写 -> 重置成功', async () => {
    axios.post.mockImplementation(url => {
      if (url.includes('verify/check')) {
        return Promise.resolve({ data: { message: '验证码正确' } })
      }
      if (url.includes('reset-password')) {
        return Promise.resolve({
          data: {
            message: '密码重置成功',
            token: 'reset-token'
          }
        })
      }
    })

    wrapper.vm.type = 'reset-password'
    await wrapper.vm.$nextTick()

    await wrapper.find('input[type="email"]').setValue('reset@example.com')
    await wrapper.find('input[type="password"]').setValue('newpass123')
    wrapper.vm.form.code = '654321'

    await wrapper.find('[data-testid="reset-password-button"]').trigger('click')
    await flushPromises()

    expect(localStorage.getItem('token')).toBe('reset-token')
    expect(router.currentRoute.value.name).toBe('home')
  })
})
