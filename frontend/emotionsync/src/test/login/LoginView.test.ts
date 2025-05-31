// LoginComponent.spec.js
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginComponent from '@/views/login/LoginView.vue'
import axios from 'axios'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia } from 'pinia'
import flushPromises from 'flush-promises';

// 模拟全局 alert
global.alert = vi.fn();

// 创建完整路由配置
const routes = [
    {
        path: '/',
        name: 'home',
        component: { template: '<div>Home</div>' }
    },
    {
        path: '/login',
        name: 'login',
        component: { template: '<div>Login</div>' }
    }
];
// 模拟axios
vi.mock('axios')

// 创建模拟路由
// const routes = [
//     { path: '/', name: 'home' },
//     { path: '/login', name: 'login' }
// ]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 创建Pinia store
const pinia = createPinia()

describe('LoginComponent', () => {
    let wrapper
    const mockRouter = {
        push: vi.fn()
    }

    beforeEach(() => {
        vi.clearAllMocks()
        wrapper = mount(LoginComponent, {
            global: {
                plugins: [router, pinia],
                mocks: {
                    $router: mockRouter
                }
            }
        })
    })

    // 1. 初始状态测试
    describe('初始渲染', () => {
        it('默认显示登录表单', () => {
            expect(wrapper.find('[data-testid="auth-title"]').text()).toBe('登录')
            expect(wrapper.find('input[type="text"]').exists()).toBe(true)
            expect(wrapper.find('input[type="password"]').exists()).toBe(true)
            expect(wrapper.find('[data-testid="forgot-password"]').exists()).toBe(true)
            expect(wrapper.find('button').text()).toBe('登录')
        })

        it.skip('当路由参数为reset-password时显示重置密码表单', async () => {
            // 正确设置路由参数
            router.push({ query: { type: 'reset-password' } })
            await router.isReady()

            const wrapper = mount(LoginComponent, {
                global: {
                    plugins: [router, pinia]
                }
            })

            await wrapper.vm.$nextTick()
            expect(wrapper.vm.type).toBe('reset-password')
            expect(wrapper.find('[data-testid="auth-title"]').text()).toBe('重设密码')
        })
    })

    // 2. 状态切换测试
    describe('状态切换', () => {
        it('从登录切换到注册', async () => {
            await wrapper.find('[data-testid="toggle-to-register"]').trigger('click')
            expect(wrapper.vm.type).toBe('register')
            expect(wrapper.find('[data-testid="auth-title"]').text()).toBe('注册')
        })

        it('从注册切换到登录', async () => {
            wrapper.vm.type = 'register'
            await wrapper.vm.$nextTick()
            await wrapper.find('[data-testid="toggle-to-login"]').trigger('click')
            expect(wrapper.vm.type).toBe('login')
        })

        it('从重置密码切换回登录', async () => {
            wrapper.vm.type = 'reset-password'
            await wrapper.vm.$nextTick()
            await wrapper.find('[data-testid="back-to-login"]').trigger('click')
            expect(wrapper.vm.type).toBe('login')
        })
    })

    // 3. 表单输入测试
    describe('表单输入', () => {
        it('输入用户名和密码', async () => {
            const usernameInput = wrapper.find('input[type="text"]')
            const passwordInput = wrapper.find('input[type="password"]')

            await usernameInput.setValue('000005')
            await passwordInput.setValue('000000')

            expect(wrapper.vm.form.username).toBe('000005')
            expect(wrapper.vm.form.password).toBe('000000')
        })

        it('在注册模式下输入邮箱', async () => {
            wrapper.vm.type = 'register'
            await wrapper.vm.$nextTick()

            const emailInput = wrapper.find('input[type="email"]')
            await emailInput.setValue('test@example.com')

            expect(wrapper.vm.form.email).toBe('test@example.com')
        })
    })

    // 4. 验证码功能测试
    describe('验证码功能', () => {
        beforeEach(() => {
            wrapper.vm.type = 'register'
            wrapper.vm.form.email = 'test@example.com'
        })

        it('发送验证码按钮状态', async () => {
            const sendButton = wrapper.find('.el-button')
            expect(sendButton.text()).toBe('发送验证码')
            expect(sendButton.attributes('disabled')).toBeUndefined()

            // 没有邮箱时禁用
            wrapper.vm.form.email = ''
            await wrapper.vm.$nextTick()
            expect(sendButton.attributes('disabled')).toBeDefined()
        })

        it('成功发送验证码', async () => {
            axios.post.mockResolvedValue({ data: { message: '验证码已发送' } })

            await wrapper.find('.el-button').trigger('click')

            expect(axios.post).toHaveBeenCalledWith(
                'http://localhost:9000/api/verify/send',
                { email: 'test@example.com' }
            )

            await wrapper.vm.$nextTick()
            expect(wrapper.vm.codeSent).toBe(true)
            expect(wrapper.vm.countdown).toBe(60)
        })

        it('验证码发送失败', async () => {
            axios.post.mockRejectedValue({ response: { data: { message: '发送失败' } } })

            await wrapper.find('.el-button').trigger('click')

            await wrapper.vm.$nextTick()
            expect(wrapper.vm.codeSent).toBe(false)
        })

        it('验证码倒计时功能', async () => {
            vi.useFakeTimers();

            // 设置邮箱并触发发送
            wrapper.vm.form.email = 'test@example.com';
            await wrapper.vm.$nextTick();

            // 模拟成功响应
            axios.post.mockResolvedValue({ data: { message: '验证码已发送' } });

            // 触发发送按钮
            await wrapper.find('.el-button').trigger('click');
            await flushPromises(); // 确保异步操作完成

            // 检查初始状态
            expect(wrapper.vm.countdown).toBe(60);

            // 推进59秒
            vi.advanceTimersByTime(59000);
            expect(wrapper.vm.countdown).toBe(1); // 现在应该是1

            // 推进1秒
            vi.advanceTimersByTime(1000);
            expect(wrapper.vm.countdown).toBe(0);
            expect(wrapper.vm.timer).toBeNull(); // 定时器已清除

            vi.useRealTimers();
        });
    })

    // 5. 登录功能测试
    describe('登录功能', () => {
        it('成功登录', async () => {
            axios.post.mockResolvedValue({
                data: {
                    message: '登录成功',
                    token: 'test-token'
                }
            })

            // 设置表单数据
            await wrapper.find('input[type="text"]').setValue('000005')
            await wrapper.find('input[type="password"]').setValue('000000')

            await wrapper.find('button').trigger('click')
            await flushPromises() // 等待所有异步操作完成

            expect(localStorage.getItem('token')).toBe('test-token')
            // expect(mockRouter.push).toHaveBeenCalledWith({ name: 'home' })
        })

        it('登录失败', async () => {
            axios.post.mockRejectedValue({
                response: {
                    data: { message: '无效凭证' }
                }
            })

            wrapper.vm.form.username = '000005'
            wrapper.vm.form.password = '000000'

            await wrapper.find('button').trigger('click')

            await wrapper.vm.$nextTick()
            // 这里可以添加UI状态断言，比如错误提示
        })
    })

    // 6. 注册功能测试
    describe('注册功能', () => {
        beforeEach(() => {
            wrapper.vm.type = 'register'
            wrapper.vm.form = {
                email: 'test1@example.com',
                username: 'newuser1',
                password: 'newpassword123',
                code: '123456'
            }
        })

        it.skip('成功注册', async () => {

            await wrapper.vm.$nextTick();

            // 模拟所有可能的 API 调用
            axios.post.mockImplementation(url => {
                if (url.includes('verify/send')) {
                    // 发送验证码请求 - 不应在注册过程中被调用
                    return Promise.resolve({ data: { message: '验证码已发送' } });
                }
                if (url.includes('verify/check')) {
                    // 验证码验证请求
                    return Promise.resolve({ data: { message: '验证码正确' } });
                }
                if (url.includes('register')) {
                    // 注册请求
                    return Promise.resolve({
                        data: {
                            success: true,
                            token: 'register-token'
                        }
                    });
                }
            });

            // 确保只找到注册按钮
            const registerButton = wrapper.find('[data-testid="register-button"]');
            expect(registerButton.exists()).toBe(true);

            // 触发注册按钮点击
            await registerButton.trigger('click');
            await flushPromises(); // 等待所有异步操作完成
            console.log('axios.post.calls = ', axios.post.mock.calls);

            // 验证 API 调用顺序
            expect(axios.post.mock.calls[0][0]).toBe('http://localhost:9000/api/verify/check');
            expect(axios.post.mock.calls[0][1]).toMatchObject({
                email: wrapper.vm.form.email,
                code: wrapper.vm.form.code
            });

            // 验证 token 存储
            expect(localStorage.getItem('token')).toBe('register-token');
            // 验证路由跳转
            // expect(mockRouter.push).toHaveBeenCalledWith({ name: 'home' })
        });

        it('验证码错误', async () => {
            axios.post.mockResolvedValue({
                data: { message: '验证码错误' }
            })

            await wrapper.find('button').trigger('click')

            await wrapper.vm.$nextTick()
            // 这里可以添加UI状态断言
        })
    })

    // 7. 重置密码功能测试
    describe('重置密码功能', () => {
        beforeEach(() => {
            wrapper.vm.type = 'reset-password'
            wrapper.vm.form = {
                email: 'reset@example.com',
                code: '654321',
                password: 'newpassword'
            }
        })

        it.skip('成功重置密码', async () => {
            // 模拟验证码验证成功
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

            await wrapper.find('button').trigger('click')

            expect(axios.post).toHaveBeenNthCalledWith(
                1,
                'http://localhost:9000/api/verify/check',
                {
                    email: 'reset@example.com',
                    code: '654321'
                }
            )

            expect(axios.post).toHaveBeenNthCalledWith(
                2,
                'http://localhost:9000/myHello/reset-password',
                wrapper.vm.form
            )

            await wrapper.vm.$nextTick()
            expect(localStorage.getItem('token')).toBe('reset-token')
            expect(mockRouter.push).toHaveBeenCalledWith({ name: 'home' })
        })
    })

    // 8. UI状态测试
    describe('UI状态', () => {
        it('注册模式下的密码提示', async () => {
            wrapper.vm.type = 'register'
            await wrapper.vm.$nextTick()
            expect(wrapper.find('.opacity-35').text()).toContain('密码请输入至少6个字符')
        })

        it('重置密码模式下的提示', async () => {
            wrapper.vm.type = 'reset-password'
            wrapper.vm.waitVerify = false
            await wrapper.vm.$nextTick()
            expect(wrapper.find('[data-testid="toggle-to-reset"]').text()).toContain('请重新设置密码')
        })
    })
})