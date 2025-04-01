<template>
  <div class="game-selection">
    <div class="game-card" v-for="(game, index) in games" :key="game.name" @mouseover="hoverIndex = index"
      @mouseleave="hoverIndex = -1" @click="onGameClick(game.name)">
      <!-- 默认显示 -->
      <div class="card-content" v-if="hoverIndex === index">
        <img :src="game.image" :alt="game.displayName" class="game-image" />
        <h2 class="game-name">{{ game.displayName }}</h2>
        <p class="game-detail">{{ game.detail }}</p>
      </div>

      <!-- 鼠标悬浮外显示 -->
      <div class="card-content" v-else>
        <img :src="game.image" :alt="game.displayName" class="game-image" />
        <h2 class="game-name">{{ game.displayName }}</h2>
        <p class="game-intro">{{ game.intro }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 从父组件接收 games 数据
const props = defineProps({
  games: {
    type: Array,
    required: true
  }
});

const hoverIndex = ref(-1);

// 父组件传递的 openGame 事件
const emit = defineEmits(['openGame']);

const onGameClick = (gameName) => {
  emit('openGame', gameName);  // 触发父组件的 openGame 方法
};
</script>

<style scoped>
.game-selection {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.game-card {
  width: 250px;
  height: 350px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.3s ease;
  position: relative;
}

.game-card:hover {
  transform: translateY(-10px);
}

.card-content {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  height: 100%;
  overflow: hidden;
}

.game-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 8px;
}

.game-name {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
  margin-bottom: 5px;
}

.game-intro,
.game-detail {
  font-size: 14px;
  color: #777;
  text-align: left;
  line-height: 1.5;
  margin-bottom: 10px;
}

.game-intro {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.game-card:hover .game-intro {
  display: none;
}

.game-detail {
  opacity: 0;
  height: auto;
}

.game-card:hover .game-detail {
  opacity: 1;
}
</style>
