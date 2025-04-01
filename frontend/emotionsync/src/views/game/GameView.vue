<template>
  <div class="game-container">
    <h1 class="title">解压小游戏</h1>
    <p class="description">
      欢迎！亲爱的玩家： {{ userName || '游客' }} ，猜你想玩：{{ recommendedGame }}
    </p>
    <!-- 游戏选择 -->
    <GameSelection :games="games" @openGame="openGame" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import GameSelection from './GameSelection.vue';
import { useUserStore } from '@/store/userStore';

const games = ref([ // 游戏列表
  {
    name: '2048',
    displayName: '2048',
    intro: '经典休闲益智游戏，挑战思维极限！',
    detail: '《2048》是一款经典的游戏，玩法简单但充满挑战。通过滑动方块来合并相同的数字，最终目标是合并出2048这个数字。在合并的过程中，需要进行策略规划。尽情挑战自己的逻辑思维，看看能不能打破自己的记录！',
    image: 'faa/public/2048/meta/2048-game.png',
  },
  {
    name: 'memory-match',
    displayName: '记忆配对\nmemory-match',
    intro: '锻炼你的记忆力和反应力，放松自己！',
    detail: '《记忆配对》是一款经典的记忆力训练游戏。你需要翻开卡片，找到匹配的对，挑战你的记忆和反应速度。这个游戏源自18世纪法国宫廷贵族，是一种古老而富有挑战性的益智游戏，适合任何想要挑战自己记忆力的玩家！',
    image: 'faa/public/memory-match/images/fevicon.png',
  },
  {
    name: 'ctr',
    displayName: '割绳子\nctr',
    intro: '动脑筋，割绳子，喂糖果给Om Nom！',
    detail: '《割绳子》是一款富有创意的物理益智游戏，玩家的目标是让糖果掉到可爱的角色Om Nom的嘴里。在愉快的音乐和独特的关卡设计中，你需要运用逻辑思维来解决各种各样的物理难题，游戏玩法简单但富有深度，带给你无尽的乐趣！',
    image: 'faa/public/ctr/images/ctr.jpg',
  }
]);

const userName = useUserStore().username; // 获取当前用户名

const recommendedGame = ref(''); // 默认推荐2048

// 获取用户游戏状态（从后端获取游戏时长和最高分）
const getUserGameState = async (gameName) => {
  try {
    console.log(`正在从后端获取用户【${userName}】在【${gameName}】游戏中的状态...`);
    const response = await axios.get(`http://localhost:9000/api/gamestate/${userName}/${gameName}`);

    if (response.data) {
      console.log('成功获取游戏状态:', response.data);
      // 返回最高分和游戏时长
      return {
        highestScore: response.data.highestScore || 0, // 如果没有最高分，默认为0
        gameDuration: response.data.gameDuration || 0   // 默认游戏时长为0
      };
    } else {
      console.log('没有找到用户的游戏状态，使用默认值');
      return { highestScore: 0, gameDuration: 0 };
    }
  } catch (error) {
    console.error('获取游戏状态失败:', error);
    return { highestScore: 0, gameDuration: 0 }; // 如果出错，返回默认值
  }
};

// 获取当前游戏分数
const getGameCurrentScore = (gameWindow) => {
  if (gameWindow) {
    const scoreContainer = gameWindow.document.querySelector('.score-container');
    return scoreContainer ? parseInt(scoreContainer.innerText, 10) : 0;
  }
  return 0;
};

// 创建 LocalStorageManager 实例
class LocalStorageManager {
  constructor() {
    this.bestScoreKey = "bestScore";
    this.storage = window.localStorage || window.fakeStorage; // 使用本地存储
  }

  setBestScore(score) {
    this.storage.setItem(this.bestScoreKey, score);
  }

  getBestScore() {
    return parseInt(this.storage.getItem(this.bestScoreKey) || 0, 10);
  }
}

// 更新本地存储中的最高分
const updateLocalHighestScore = (newScore) => {
  const localStorageManager = new LocalStorageManager();
  localStorageManager.setBestScore(newScore);
  console.log(`本地最高分已更新为: ${newScore}`);
};

// 打开游戏并记录开始时间
const openGame = async (gameName) => {
  // 获取后端游戏状态
  const gameState = await getUserGameState(gameName);

  let gameStartTime = sessionStorage.getItem(`${gameName}_startTime`);
  if (!gameStartTime) {
    gameStartTime = new Date().getTime() / 1000;
    sessionStorage.setItem(`${gameName}_startTime`, gameStartTime);
  }

  const previousGameDuration = gameState.gameDuration || 0;

  const baseUrl = '/faa/public/';
  const gameUrl = `${baseUrl}${gameName}/index.html`;

  const gameWindow = window.open(gameUrl, '_blank');

  let gameHighestScore = 0;

  // 初始化时从后端获取最高分并更新本地存储
  const userHighestScore = gameState.highestScore;  // 从返回的数据中提取最高分
  updateLocalHighestScore(userHighestScore);

  // 检测窗口状态
  const checkWindowClosed = setInterval(() => {
    if (gameWindow && gameWindow.closed) {

      // 计算时长
      const gameEndTime = new Date().getTime() / 1000;
      const currentGameDuration = Math.round(gameEndTime - gameStartTime);
      const totalGameDuration = previousGameDuration + currentGameDuration;

      // 获取游戏结束时的分数
      const localStorageManager = new LocalStorageManager();
      let finalScore = localStorageManager.getBestScore();

      if (gameName === '2048') {
        const currentScore = getGameCurrentScore(gameWindow); // 获取当前分数
        console.log(`当前分数：${currentScore}`);
        if (currentScore > finalScore) {
          localStorageManager.setBestScore(currentScore);
          finalScore = currentScore;
          console.log('数据库存储的最高分已更新为:', currentScore);
        }
      }

      // 存储数据
      const gameStateData = {
        userName,
        gameType: gameName,
        highestScore: finalScore,
        gameDuration: currentGameDuration,
      };

      // 将游戏数据发送到后端
      axios.post('http://localhost:9000/api/gamestate/add', gameStateData)
        .then(response => {
          console.log('游戏数据已成功保存:', response.data);
        })
        .catch(error => {
          console.error('游戏数据保存失败:', error);
        });

      sessionStorage.removeItem(`${gameName}_startTime`);
      clearInterval(checkWindowClosed);
    }
  }, 1000); // 每秒检查窗口是否关闭
};

// 动态推荐时长最长的游戏
const getRecommendedGame = async () => {
  if (userName) {
    try {
      const gameStates = await Promise.all(
        games.value.map(game => getUserGameState(game.name))
      );

      const gameDurations = gameStates.map((state, index) => ({
        gameName: games.value[index].name,
        gameDuration: state.gameDuration,
      }));

      const longestGame = gameDurations.reduce((max, current) =>
        current.gameDuration > max.gameDuration ? current : max
      );

      recommendedGame.value = longestGame.gameDuration > 0 ? longestGame.gameName : '2048';
    } catch (error) {
      console.error('获取推荐游戏失败:', error);
      recommendedGame.value = '2048'; // 默认推荐2048
    }
  }
  else {
    recommendedGame.value = '2048';
  }

};

// 获取推荐游戏
onMounted(() => {
  getRecommendedGame();

});

</script>

<style scoped>
.game-container {
  text-align: center;
  margin-top: 30px;
  font-family: 'Arial', sans-serif;
  /* 设置字体 */
}

.title {
  font-size: 36px;
  /* 设置标题字体大小 */
  font-weight: bold;
  color: #2c3e50;
  /* 设置字体颜色 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
  /* 字体阴影效果 */
  margin-bottom: 10px;
}

.description {
  font-size: 18px;
  /* 设置描述文字大小 */
  color: #7f8c8d;
  /* 设置字体颜色 */
  margin-bottom: 30px;
  font-style: italic;
  /* 斜体样式 */
}

.game-selection-button {
  font-size: 20px;
  font-weight: bold;
  padding: 10px 20px;
  background-color: #3498db;
  /* 按钮背景色 */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.game-selection-button:hover {
  background-color: #2980b9;
  /* 悬停效果 */
}

.recommended-game {
  font-size: 22px;
  font-weight: bold;
  color: #e74c3c;
  /* 推荐游戏的字体颜色 */
  margin-top: 20px;
}
</style>
