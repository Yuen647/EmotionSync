// GameView.spec.js
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import GameView from '@/views/game/GameView.vue'
import GameSelection from '@/views/game/GameSelection.vue'
import axios from 'axios'
import { createRouter, createWebHistory } from 'vue-router'
import { createPinia, setActivePinia } from 'pinia'

// 模拟 useUserStore，始终返回 username = 'testUser'
vi.mock('@/store/userStore', () => {
  return {
    useUserStore: () => ({
      username: 'testUser'
    })
  }
})

// 完全模拟 axios
vi.mock('axios')

// 因为组件内会调用 window.open 和 setInterval，需要在测试后清理
let originalWindowOpen
let originalSetInterval
let originalClearInterval

describe('GameView.vue', () => {
  let wrapper
  let router
  let pinia

  beforeEach(async () => {
    // 清除所有 mock
    vi.clearAllMocks()

    // 激活 Pinia
    pinia = createPinia()
    setActivePinia(pinia)

    // 创建一个简单的 router（无需真正导航，只是为了满足组件依赖）
    router = createRouter({
      history: createWebHistory(),
      routes: []
    })

    // 替换 window.open，返回一个模拟的“窗口”
    originalWindowOpen = window.open
    window.open = vi.fn(() => {
      return {
        closed: false,
        document: {
          querySelector: () => null
        }
      }
    })

    // 替换 setInterval / clearInterval
    originalSetInterval = global.setInterval
    originalClearInterval = global.clearInterval
    global.setInterval = vi.fn((fn, ms) => {
      // Return a fake timer id (number)
      const id = Math.floor(Math.random() * 1000) + 1
      return id
    })
    global.clearInterval = vi.fn()

    // 挂载组件
    wrapper = mount(GameView, {
      global: {
        plugins: [router, pinia],
        // 由于内部直接 import 了 GameSelection.vue，无需额外 mocks
      }
    })

    // 等待 onMounted 中的异步调用
    await flushPromises()
  })

  afterEach(() => {
    // 恢复全局替换
    window.open = originalWindowOpen
    global.setInterval = originalSetInterval
    global.clearInterval = originalClearInterval
  })

  // —— 1. 测试初始渲染 —— 
  describe('初始渲染', () => {
    it('应当渲染“解压小游戏”标题及三张 benefit 卡片', () => {
      // 检查主标题
      const title = wrapper.find('h1.title')
      expect(title.exists()).toBe(true)
      expect(title.text()).toBe('解压小游戏')

      // 检查 benefit 卡片数量
      const benefitCards = wrapper.findAll('.benefit-card')
      // benefits 数组长度为 3
      expect(benefitCards.length).toBe(3)

      // 验证第一张卡片的内容
      const firstTitle = benefitCards[0].find('h3.card-title').text()
      expect(firstTitle).toBe('减少焦虑压力')
      const firstDesc = benefitCards[0].find('p.card-description').text()
      expect(firstDesc).toContain('小游戏通过轻松互动帮助你分散注意力，舒缓紧张情绪。')
    })


    it('用户姓名正确显示为 “testUser”，若推荐游戏存在则以高亮形式显示', async () => {
      // 模拟 getUserGameState：第一个 gameDuration 为 10，第二个为 20，第三个为 5
      axios.get.mockResolvedValueOnce({ data: { highestScore: 100, gameDuration: 10 } })
      axios.get.mockResolvedValueOnce({ data: { highestScore: 200, gameDuration: 20 } })
      axios.get.mockResolvedValueOnce({ data: { highestScore: 50, gameDuration: 5 } })
      // 还要再为 fetchGameHistory 做一次返回（空数组）
      axios.get.mockResolvedValueOnce({ data: [] })

      // 重新挂载组件以触发 onMounted 流程
      const wrapper2 = mount(GameView, {
        global: {
          plugins: [router, pinia]
        }
      })
      await flushPromises()

      // 此时 recommendedGame 应该算出“memory-match”（20 秒最长）
      const desc = wrapper2.find('p.description')
      expect(desc.text()).toContain('亲爱的玩家： testUser ，猜你想玩：')

      const rec = wrapper2.find('span.recommended-game')
      expect(rec.exists()).toBe(true)
      expect(rec.text()).toBe('memory-match')
      // 此时 Spinner <a-spin> 消失
      expect(wrapper2.findComponent({ name: 'ASpin' }).exists()).toBe(false)
    })
  })

  // —— 2. 测试“游玩历史记录”部分 —— 
  describe('游玩历史记录', () => {
    it('当后端返回空数组时，应当显示“暂无游玩记录”', async () => {
      // 我们在 beforeEach 已经模拟过一次空数组返回。但为了保险，再次手动检查：
      axios.get.mockResolvedValueOnce({ data: [] })
      const wrapper3 = mount(GameView, {
        global: {
          plugins: [router, pinia]
        }
      })
      await flushPromises()

      // 此时应该找到 class="history-card" 且其内部文本为 “暂无游玩记录”
      const emptyCard = wrapper3.find('.history-card p')
      expect(emptyCard.exists()).toBe(true)
      expect(emptyCard.text()).toBe('暂无游玩记录')
    })

    it('当后端返回若干条游玩记录时，应当合并统计并渲染多个 statistic 卡片', async () => {
      // 准备一组假数据：用户在同一个游戏类型下玩了两次，另一个游戏玩了一次
      const fakeHistory = [
        {
          userName: 'testUser',
          gameType: '2048',
          highestScore: 120,
          gameDuration: 30,
          startTime: 1650000000000 // 时间戳毫秒
        },
        {
          userName: 'testUser',
          gameType: '2048',
          highestScore: 200,
          gameDuration: 45,
          startTime: 1650001000000
        },
        {
          userName: 'testUser',
          gameType: 'memory-match',
          highestScore: 80,
          gameDuration: 25,
          startTime: 1650002000000
        }
      ]

      // 第一次被 getRecommendedGame 使用的 axios.get 会被消费掉，我们先 mock 三个 getUserGameState 用的 axios.get
      axios.get
        // 串联 mock：memory-match、ctr、2048（顺序随数组顺序，但因为我们这里只关心 fetchGameHistory，我们直接忽略前面 onMounted 中调用的几次）
        .mockResolvedValueOnce({ data: { highestScore: 0, gameDuration: 0 } })
        .mockResolvedValueOnce({ data: { highestScore: 0, gameDuration: 0 } })
        .mockResolvedValueOnce({ data: { highestScore: 0, gameDuration: 0 } })
        // 紧接着 fetchGameHistory 的调用
        .mockResolvedValueOnce({ data: fakeHistory })

      const wrapper4 = mount(GameView, {
        global: {
          plugins: [router, pinia]
        }
      })
      await flushPromises()

      // 此时 gameStats 计算应当返回 2 条记录：2048 和 memory-match
      const statCards = wrapper4.findAll('.history-card')
      // 第一张卡片可能是 2048，第二张是 memory-match，总数为 2
      expect(statCards.length).toBe(2)

      // 验证 2048 的统计
      const card2048 = statCards.find((c) => c.find('h3').text() === '2048')
      expect(card2048).toBeTruthy()
      // 2048 共玩了 2 次，totalDuration = 30+45 = 75，highestScore = max(120,200)=200
      expect(card2048.find('p:nth-of-type(1)').text()).toContain('游玩次数：2')
      expect(card2048.find('p:nth-of-type(2)').text()).toContain('总时长：75 秒')
      expect(card2048.find('p:nth-of-type(3)').text()).toContain('最高分：200')
      // 最近一次 startTime 应该是 1650001000000（第二条记录），在 formatDate 里会转换成本地字符串
      const lastPlayText = card2048.find('p:nth-of-type(4)').text()
      expect(lastPlayText).toContain('最近一次：')
      // 我们只验证“最近一次：”前缀存在即可
      expect(lastPlayText.startsWith('最近一次：')).toBe(true)

      // 验证 memory-match 的统计
      const cardMem = statCards.find((c) => c.find('h3').text() === 'memory-match')
      expect(cardMem).toBeTruthy()
      expect(cardMem.find('p:nth-of-type(1)').text()).toContain('游玩次数：1')
      expect(cardMem.find('p:nth-of-type(2)').text()).toContain('总时长：25 秒')
      expect(cardMem.find('p:nth-of-type(3)').text()).toContain('最高分：80')
    })
  })

  // —— 3. 测试子组件 GameSelection.vue —— 
  describe('GameSelection.vue 子组件交互', () => {
    let wrapperGS

    beforeEach(() => {
      // 单独挂载子组件，模拟传入三个游戏
      const gamesProp = [
        { name: '2048', displayName: '2048', intro: '简介 2048', detail: '详情 2048', image: '2048.png' },
        { name: 'memory-match', displayName: '记忆配对', intro: '简介 mem', detail: '详情 mem', image: 'mem.png' },
        { name: 'ctr', displayName: '割绳子', intro: '简介 ctr', detail: '详情 ctr', image: 'ctr.png' }
      ]

      wrapperGS = mount(GameSelection, {
        props: {
          games: gamesProp
        }
      })
    })

    it('应当渲染与传入 games 数量相同的 game-card', () => {
      const cards = wrapperGS.findAll('.game-card')
      expect(cards.length).toBe(3)
      // 第一张卡片标题应为 “2048”
      const firstName = cards[0].find('h2.game-name').text()
      expect(firstName).toBe('2048')
    })

    it('当鼠标 hover 在卡片上时，应切换显示 game.detail，否则显示 game.intro', async () => {
      const cards = wrapperGS.findAll('.game-card')
      const secondCard = cards[1]
      // 初始应展示 intro
      expect(secondCard.find('p.game-description').text()).toBe('简介 mem')

      // 模拟鼠标移入
      await secondCard.trigger('mouseover')
      expect(secondCard.find('p.game-description').text()).toBe('详情 mem')

      // 模拟鼠标移出
      await secondCard.trigger('mouseleave')
      expect(secondCard.find('p.game-description').text()).toBe('简介 mem')
    })

    it('点击 “开始游戏” 按钮时，应当触发 openGame 事件并携带对应 game.name', async () => {
      const cards = wrapperGS.findAll('.game-card')
      const firstButton = cards[0].find('button.start-btn')
      await firstButton.trigger('click')
      // 子组件应该触发 'openGame' 事件，载荷为 '2048'
      expect(wrapperGS.emitted('openGame')).toBeTruthy()
      expect(wrapperGS.emitted('openGame')[0]).toEqual(['2048'])
    })
  })

  // —— 4. 测试 openGame 流程 —— 
  describe('openGame 方法', () => {
    it('调用 openGame 时，打开新窗口', async () => {
      // 1. 先模拟 getUserGameState（实际上 axios.get）返回的内容
      axios.get.mockResolvedValueOnce({
        data: { highestScore: 300, gameDuration: 50 }
      })

      // 此时调用父组件实例上的 openGame
      const vm = wrapper.vm

      // 调用 openGame
      await vm.openGame('2048')

      // 检查 window.open 被调用
      expect(window.open).toHaveBeenCalledWith('/faa/public/2048/index.html', '_blank')
    })
  })
})
