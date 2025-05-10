<template>
  <div class="game-selection">
    <div
      class="game-card"
      v-for="(game, index) in games"
      :key="game.name"
      @mouseover="hoverIndex = index"
      @mouseleave="hoverIndex = -1"
    >
      <!-- 左侧：图片 -->
      <img :src="game.image" :alt="game.displayName" class="game-image" />

      <!-- 中间：信息 -->
      <div class="game-info">
        <h2 class="game-name">{{ game.displayName }}</h2>
        <p class="game-description">
          {{ hoverIndex === index ? game.detail : game.intro }}
        </p>
      </div>

      <!-- 右侧：按钮 -->
      <div class="game-action">
        <button class="start-btn" @click="onGameClick(game.name)">
          开始游戏
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  games: {
    type: Array,
    required: true
  }
});

const emit = defineEmits(['openGame']);
const hoverIndex = ref(-1);

const onGameClick = (gameName) => {
  emit('openGame', gameName);
};
</script>

<style scoped>
.game-selection {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.game-card {
  display: flex;
  align-items: center;
  width: 800px;
  padding: 16px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s;
}

.game-card:hover {
  transform: translateY(-5px);
}

.game-image {
  width: 160px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 20px;
}

.game-info {
  flex: 1;
  text-align: left;
}

.game-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 8px;
}

.game-description {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

.game-action {
  margin-left: 20px;
}

.start-btn {
  padding: 8px 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.start-btn:hover {
  background-color: #45a049;
}
</style>
