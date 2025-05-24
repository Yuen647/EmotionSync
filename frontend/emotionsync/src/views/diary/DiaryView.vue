<template>
  <div class="diary-container">
    <h1 class="text-2xl font-bold mb-6">个人日记</h1>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>{{ loadingMessage }}</p>
    </div>
    
    <!-- 发布日记表单 -->
    <div class="diary-form p-6 bg-white rounded-lg shadow-md mb-6">
      <h2 class="text-xl font-semibold mb-4">记录我的心情</h2>
      
      <!-- 表情选择器 -->
      <div class="mb-4">
        <label class="block text-gray-700 mb-2">选择心情:</label>
        <div class="emoji-picker">
          <button 
            v-for="emoji in emojis" 
            :key="emoji"
            @click="selectEmoji(emoji)"
            class="emoji-btn"
            :class="{ 'selected': selectedEmoji === emoji }"
          >
            {{ emoji }}
          </button>
        </div>
      </div>
      
      <div class="mb-4">
        <label class="block text-gray-700 mb-2">日记内容: <span class="text-red-500">*</span></label>
        <textarea
          v-model="newDiary.description"
          class="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-primary transition"
          rows="4"
          placeholder="写下你今天的心情..."
          maxlength="500"
        ></textarea>
        <div class="flex justify-between items-center mt-1">
          <p class="text-xs text-gray-500">分享你的感受、思考或经历</p>
          <p class="text-xs text-gray-500">{{ newDiary.description.length }}/500</p>
        </div>
      </div>
      
      <!-- 图片上传 -->
      <div class="mb-4">
        <label class="block text-gray-700 mb-2">添加图片: <span class="text-gray-500">(可选)</span></label>
        <div class="image-upload-container">
          <input
            type="file"
            ref="fileInput"
            @change="handleFileUpload"
            accept="image/*"
            class="hidden"
          />
          <button
            @click="$refs.fileInput.click()"
            class="upload-btn"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <span>选择图片</span>
          </button>
          <button
            v-if="newDiary.imageUrls"
            @click="removeImage"
            class="remove-btn"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </div>
      
      <!-- 图片预览 -->
      <div v-if="newDiary.imageUrls" class="mb-4">
        <p class="text-sm text-gray-700 mb-2">图片预览:</p>
        <div class="relative w-full max-h-40 overflow-hidden rounded-md">
          <img 
            :src="newDiary.imageUrls" 
            alt="预览图" 
            class="w-full object-cover"
            @error="handleImageError"
          />
        </div>
      </div>
      
      <div class="flex justify-end">
        <button
          @click="createDiary"
          class="bg-primary text-white px-6 py-2 rounded-md hover:bg-opacity-90 transition-all transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="!newDiary.description.trim() || isSubmitting"
        >
          <span v-if="isSubmitting">发布中...</span>
          <span v-else>发布日记</span>
        </button>
      </div>
    </div>
    
    <!-- 日记列表 -->
    <div class="diary-list">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-semibold">我的日记</h2>
        <button @click="fetchDiaries()" class="text-primary flex items-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          刷新
        </button>
      </div>
      
      <div v-if="diaries.length === 0" class="text-center py-12 bg-white rounded-lg shadow-md">
        <img 
          :src="defaultImageUrl" 
          alt="空状态图片" 
          class="w-24 h-24 mx-auto mb-4 opacity-50"
        />
        <p class="text-gray-500">暂无日记，开始记录你的第一篇日记吧！</p>
      </div>
      
      <div v-else>
        <div class="grid grid-cols-1 gap-4">
          <div
            v-for="diary in paginatedDiaries"
            :key="diary.id"
            class="diary-item p-6 bg-white rounded-lg shadow-md"
          >
            <div class="flex justify-between mb-3">
              <div class="font-medium text-gray-600">{{ formatDate(diary.createdAt) }}</div>
              <div class="flex gap-3">
                <button
                  v-if="isEditing && editingDiary.id === diary.id"
                  @click="cancelEditing"
                  class="text-gray-500 hover:text-gray-700 flex items-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                  取消
                </button>
                <button
                  v-if="isEditing && editingDiary.id === diary.id"
                  @click="updateDiary"
                  class="text-blue-500 hover:text-blue-700 flex items-center"
                  :disabled="isSubmitting"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                  </svg>
                  保存
                </button>
                <button
                  v-else
                  @click="startEditing(diary)"
                  class="text-blue-500 hover:text-blue-700 flex items-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                  </svg>
                  编辑
                </button>
                <button
                  @click="deleteDiary(diary.id)"
                  class="text-red-500 hover:text-red-700 flex items-center"
                  :disabled="isSubmitting"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                  删除
                </button>
              </div>
            </div>
            
            <div v-if="isEditing && editingDiary.id === diary.id" class="bg-gray-50 p-4 rounded-md">
              <div class="mb-3">
                <label class="block text-gray-700 mb-2 text-sm">日记内容: <span class="text-red-500">*</span></label>
                <textarea
                  v-model="editingDiary.description"
                  class="w-full p-3 border rounded-md focus:ring-2 focus:ring-primary focus:border-primary transition"
                  rows="3"
                  maxlength="500"
                ></textarea>
                <div class="flex justify-end mt-1">
                  <p class="text-xs text-gray-500">{{ editingDiary.description.length }}/500</p>
                </div>
              </div>
              <div class="mb-3">
                <label class="block text-gray-700 mb-2 text-sm">图片: <span class="text-gray-500">(可选)</span></label>
                <div class="image-upload-container">
                  <input
                    type="file"
                    ref="editFileInput"
                    @change="handleEditFileUpload"
                    accept="image/*"
                    class="hidden"
                  />
                  <button
                    @click="$refs.editFileInput.click()"
                    class="upload-btn"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span>选择图片</span>
                  </button>
                  <button
                    v-if="editingDiary.imageUrls"
                    @click="removeEditImage"
                    class="remove-btn"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                  </button>
                </div>
              </div>
              <!-- 编辑预览 -->
              <div v-if="editingDiary.imageUrls" class="mb-2">
                <p class="text-xs text-gray-500 mb-1">图片预览:</p>
                <img 
                  :src="editingDiary.imageUrls" 
                  alt="预览图" 
                  class="max-h-32 object-cover rounded-md"
                  @error="handleEditImageError"
                />
              </div>
            </div>
            <div v-else>
              <p class="mb-4 text-gray-800 whitespace-pre-line">{{ diary.description }}</p>
              <img
                v-if="diary.imageUrls"
                :src="diary.imageUrls"
                alt="日记图片"
                class="w-full h-auto rounded-md mt-2 shadow-sm"
                @error="(e) => handleDiaryImageError(e, diary.id)"
              />
            </div>
          </div>
        </div>
        
        <!-- 分页控件 -->
        <div class="flex justify-center items-center mt-6 space-x-2">
          <button
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="px-3 py-1 rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            上一页
          </button>
          
          <div class="flex items-center space-x-1">
            <button
              v-for="page in totalPages"
              :key="page"
              @click="currentPage = page"
              :class="[
                'px-3 py-1 rounded-md border',
                currentPage === page
                  ? 'bg-primary text-white border-primary'
                  : 'border-gray-300 text-gray-600 hover:bg-gray-50'
              ]"
            >
              {{ page }}
            </button>
          </div>
          
          <button
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="px-3 py-1 rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
    
    <!-- 提示弹窗 -->
    <div v-if="notification.show" class="notification" :class="notification.type">
      <p>{{ notification.message }}</p>
      <button @click="notification.show = false" class="close-btn">&times;</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import { useUserStore } from '@/store/userStore';
import axios from 'axios';
import defaultImg from '@/assets/relaxed.png';

const userStore = useUserStore();
const defaultImageUrl = computed(() => defaultImg);
const useDefaultImg = ref(false);

// 表情选择器
const emojis = ['😊', '😢', '😡', '😴', '😍', '🤔', '😎', '😭', '😤', '😇'];
const selectedEmoji = ref('');

// 选择表情
const selectEmoji = (emoji) => {
  selectedEmoji.value = emoji;
  if (!newDiary.value.description) {
    newDiary.value.description = emoji + ' ';
  } else {
    newDiary.value.description = emoji + ' ' + newDiary.value.description;
  }
};

// 加载状态
const loading = ref(false);
const loadingMessage = ref('加载中...');
const isSubmitting = ref(false);

// 提示信息
const notification = ref({
  show: false,
  message: '',
  type: 'success',
  timeout: null
});

// 显示提示
const showNotification = (message, type = 'success', duration = 3000) => {
  if (notification.value.timeout) {
    clearTimeout(notification.value.timeout);
  }
  
  notification.value.message = message;
  notification.value.type = type;
  notification.value.show = true;
  
  notification.value.timeout = setTimeout(() => {
    notification.value.show = false;
  }, duration);
};

// 图片上传相关
const fileInput = ref(null);
const editFileInput = ref(null);

// 添加图片压缩函数
const compressImage = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = (event) => {
      const img = new Image();
      img.src = event.target?.result as string;
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        
        // 限制最大尺寸为800px
        const MAX_SIZE = 800;
        if (width > height && width > MAX_SIZE) {
          height = Math.round((height * MAX_SIZE) / width);
          width = MAX_SIZE;
        } else if (height > MAX_SIZE) {
          width = Math.round((width * MAX_SIZE) / height);
          height = MAX_SIZE;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        ctx?.drawImage(img, 0, 0, width, height);
        
        // 压缩图片质量
        const compressedBase64 = canvas.toDataURL('image/jpeg', 0.7);
        resolve(compressedBase64);
      };
      img.onerror = reject;
    };
    reader.onerror = reject;
  });
};

// 修改图片上传处理函数
const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    showNotification('请选择图片文件', 'warning');
    return;
  }
  
  // 检查文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    showNotification('图片大小不能超过2MB', 'warning');
    return;
  }
  
  try {
    loading.value = true;
    loadingMessage.value = '正在处理图片...';
    
    // 压缩图片并转换为Base64
    const compressedImage = await compressImage(file);
    newDiary.value.imageUrls = compressedImage;
    
    showNotification('图片处理成功', 'success');
  } catch (error) {
    console.error('图片处理失败:', error);
    showNotification('图片处理失败，请重试', 'error');
  } finally {
    loading.value = false;
  }
};

// 修改编辑时的图片上传处理函数
const handleEditFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;
  
  // 检查文件类型
  if (!file.type.startsWith('image/')) {
    showNotification('请选择图片文件', 'warning');
    return;
  }
  
  // 检查文件大小（限制为2MB）
  if (file.size > 2 * 1024 * 1024) {
    showNotification('图片大小不能超过2MB', 'warning');
    return;
  }
  
  try {
    loading.value = true;
    loadingMessage.value = '正在处理图片...';
    
    // 压缩图片并转换为Base64
    const compressedImage = await compressImage(file);
    editingDiary.value.imageUrls = compressedImage;
    
    showNotification('图片处理成功', 'success');
  } catch (error) {
    console.error('图片处理失败:', error);
    showNotification('图片处理失败，请重试', 'error');
  } finally {
    loading.value = false;
  }
};

const removeImage = () => {
  newDiary.value.imageUrls = '';
};

const removeEditImage = () => {
  editingDiary.value.imageUrls = '';
};

// 新日记表单数据
const newDiary = ref({
  description: '',
  imageUrls: ''
});

// 日记列表
const diaries = ref([]);

// 编辑状态
const isEditing = ref(false);
const editingDiary = ref({
  id: null,
  description: '',
  imageUrls: ''
});

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false;
  editingDiary.value = {
    id: null,
    description: '',
    imageUrls: ''
  };
};

// 分页相关
const currentPage = ref(1);
const pageSize = ref(5); // 每页显示5条日记

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(diaries.value.length / pageSize.value);
});

// 计算当前页的日记
const paginatedDiaries = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return diaries.value.slice(start, end);
});

// 修改获取日记列表函数
const fetchDiaries = async (retryCount = 0) => {
  // 检查用户登录状态
  if (userStore.loggedIn !== 1) {
    console.log('用户未登录，无法获取日记列表');
    return;
  }
  
  // 检查用户名是否存在
  if (!userStore.username || userStore.username.trim() === '') {
    console.error('用户名为空，无法获取日记列表');
    return;
  }
  
  loading.value = true;
  loadingMessage.value = '正在获取日记列表...';
  
  try {
    // 确保请求与API期望的格式完全匹配
    console.log('正在请求日记列表，用户名:', userStore.username);
    
    // 尝试不同的API端点格式
    let endpoint = '';
    let requestBody = {};
    
    // 根据重试次数尝试不同的API端点格式
    if (retryCount === 0) {
      endpoint = 'http://localhost:9000/api/notes/list';
      requestBody = { username: userStore.username };
    } else if (retryCount === 1) {
      // 尝试另一种可能的接口格式
      endpoint = `http://localhost:9000/api/notes/list/${userStore.username}`;
      requestBody = {};
    } else {
      // 再尝试一种格式
      endpoint = 'http://localhost:9000/api/notes';
      requestBody = { username: userStore.username, action: 'list' };
    }
    
    console.log(`尝试API端点 (重试${retryCount}):`, endpoint);
    const response = await axios.post(endpoint, requestBody);
    
    console.log('API响应数据:', response.data); 
    
    // 修改数据处理逻辑
    if (response.data === null || response.data === undefined) {
      console.error('API响应为空');
      showNotification('获取日记失败，请稍后再试', 'error');
      return;
    }
    
    // 检查返回数据格式
    if (typeof response.data === 'object') {
      let diaryData = [];
      
      // 如果直接返回日记对象数组
      if (Array.isArray(response.data) && response.data.length > 0 && response.data[0].id) {
        diaryData = response.data;
      }
      // 如果有success字段
      else if ('success' in response.data) {
        if (response.data.success) {
          diaryData = response.data.data || [];
        } else {
          console.error('获取日记失败:', response.data?.message || '未知错误');
          showNotification('获取日记失败: ' + (response.data?.message || '未知错误'), 'error');
          return;
        }
      } 
      // 如果直接返回数组
      else if (Array.isArray(response.data)) {
        diaryData = response.data;
      }
      // 其他情况
      else {
        console.error('无法识别的API响应格式:', response.data);
        if (retryCount < 2) {
          console.log(`尝试不同的API格式，第${retryCount + 1}次重试...`);
          loading.value = false;
          return fetchDiaries(retryCount + 1);
        } else {
          showNotification('获取日记失败，无法识别的数据格式', 'error');
          return;
        }
      }
      
      // 按创建时间排序（最新的在前）
      diaries.value = diaryData.sort((a, b) => {
        const dateA = new Date(a.createdAt).getTime();
        const dateB = new Date(b.createdAt).getTime();
        return dateB - dateA;
      });
      
      // 重置到第一页
      currentPage.value = 1;
      
      console.log('成功获取日记列表，数量:', diaries.value.length);
    } else {
      console.error('API响应不是对象类型:', typeof response.data);
      showNotification('获取日记失败，无效的响应格式', 'error');
    }
  } catch (error) {
    console.error('获取日记异常:', error);
    if (retryCount < 2) {
      console.log(`第${retryCount + 1}次重试获取日记列表...`);
      setTimeout(() => {
        fetchDiaries(retryCount + 1);
      }, 1000);
    } else {
      showNotification('获取日记失败，请检查网络连接', 'error');
    }
  } finally {
    loading.value = false;
  }
};

// 监听页码变化
watch(currentPage, (newPage) => {
  // 如果当前页大于总页数，重置到最后一页
  if (newPage > totalPages.value) {
    currentPage.value = totalPages.value;
  }
  // 如果当前页小于1，重置到第一页
  if (newPage < 1) {
    currentPage.value = 1;
  }
});

// 创建新日记
const createDiary = async () => {
  if (userStore.loggedIn !== 1) {
    showNotification('请先登录', 'error');
    return;
  }
  
  if (!userStore.username || userStore.username.trim() === '') {
    showNotification('用户名不存在，请重新登录', 'error');
    return;
  }
  
  if (!newDiary.value.description.trim()) {
    showNotification('请输入日记内容', 'warning');
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    console.log('正在创建日记，用户名:', userStore.username);
    
    const response = await axios.post('http://localhost:9000/api/notes/create', {
      username: userStore.username,
      description: newDiary.value.description,
      imageUrls: newDiary.value.imageUrls || '' // 直接发送Base64图片数据
    });
    
    console.log('创建日记响应:', response.data);
    
    if (response.data && response.data.id) {
      showNotification('发布成功！', 'success');
      newDiary.value.description = '';
      newDiary.value.imageUrls = '';
      selectedEmoji.value = '';
      fetchDiaries(); // 刷新列表
    } 
    else if (response.data && response.data.success) {
      showNotification('发布成功！', 'success');
      newDiary.value.description = '';
      newDiary.value.imageUrls = '';
      selectedEmoji.value = '';
      fetchDiaries(); // 刷新列表
    } 
    else {
      showNotification('发布失败: ' + (response.data?.message || '未知错误'), 'error');
    }
  } catch (error) {
    console.error('发布日记异常:', error);
    showNotification('发布失败，请稍后再试', 'error');
  } finally {
    isSubmitting.value = false;
  }
};

// 开始编辑
const startEditing = (diary) => {
  isEditing.value = true;
  editingDiary.value = {
    id: diary.id,
    description: diary.description,
    imageUrls: diary.imageUrls || ''
  };
};

// 更新日记
const updateDiary = async () => {
  if (!editingDiary.value.description.trim()) {
    showNotification('日记内容不能为空', 'warning');
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    console.log('正在更新日记，ID:', editingDiary.value.id);
    
    const response = await axios.post('http://localhost:9000/api/notes/update', {
      id: editingDiary.value.id,
      note: {
        description: editingDiary.value.description,
        imageUrls: editingDiary.value.imageUrls || '' // 直接发送Base64图片数据
      }
    });
    
    console.log('更新日记响应:', response.data);
    
    if (response.data && response.data.success) {
      showNotification('更新成功！', 'success');
      isEditing.value = false;
      fetchDiaries(); // 刷新列表
    } 
    else if (response.data && (response.data.id || response.data.updatedAt)) {
      showNotification('更新成功！', 'success');
      isEditing.value = false;
      fetchDiaries(); // 刷新列表
    }
    else if (response.status === 200 || response.statusText === 'OK') {
      showNotification('更新成功！', 'success');
      isEditing.value = false;
      fetchDiaries(); // 刷新列表
    }
    else {
      showNotification('更新失败: ' + (response.data?.message || '未知错误'), 'error');
    }
  } catch (error) {
    console.error('更新日记异常:', error);
    showNotification('更新失败，请稍后再试', 'error');
  } finally {
    isSubmitting.value = false;
  }
};

// 删除日记
const deleteDiary = async (id) => {
  if (!confirm('确定要删除这篇日记吗？\n删除后将无法恢复。')) {
    return;
  }
  
  isSubmitting.value = true;
  
  try {
    console.log('正在删除日记，ID:', id);
    
    const response = await axios.post('http://localhost:9000/api/notes/delete', {
      id: id
    });
    
    console.log('删除日记响应:', response.data);
    
    // 检查不同的响应格式
    if (response.data && response.data.success) {
      // 标准成功响应
      showNotification('删除成功！', 'success');
      fetchDiaries(); // 刷新列表
    }
    else if (response.status === 200 || response.statusText === 'OK') {
      // 状态码为200表示成功
      showNotification('删除成功！', 'success');
      fetchDiaries(); // 刷新列表
    }
    else if (response.data === true || response.data === 'success') {
      // 直接返回true或success字符串
      showNotification('删除成功！', 'success');
      fetchDiaries(); // 刷新列表
    }
    else {
      showNotification('删除失败: ' + (response.data?.message || '未知错误'), 'error');
    }
  } catch (error) {
    console.error('删除日记异常:', error);
    showNotification('删除失败，请稍后再试', 'error');
  } finally {
    isSubmitting.value = false;
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '刚刚';
  
  try {
    const date = new Date(dateString);
    
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return '未知时间';
    }
    
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    
    // 如果是今天，显示"今天 HH:MM"
    if (date.toDateString() === now.toDateString()) {
      return `今天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    // 如果是昨天，显示"昨天 HH:MM"
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    if (date.toDateString() === yesterday.toDateString()) {
      return `昨天 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    }
    
    // 否则显示完整日期时间
    return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
  } catch (e) {
    console.error('日期格式化错误:', e);
    return dateString || '未知时间';
  }
};

// 页面加载时获取日记列表
onMounted(() => {
  fetchDiaries();
});
</script>

<style scoped>
.diary-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.diary-form {
  background-color: #f9f9f9;
  border: 1px solid #eaeaea;
  transition: all 0.3s ease;
}

.diary-form:hover {
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.diary-item {
  transition: all 0.3s ease;
  border-left: 4px solid transparent;
}

.diary-item:hover {
  transform: translateY(-2px);
  border-left: 4px solid #4CAF50;
}

/* 表情选择器 */
.emoji-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.emoji-btn {
  font-size: 24px;
  padding: 8px;
  border: 1px solid #eaeaea;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.emoji-btn:hover {
  transform: scale(1.1);
  background: #f5f5f5;
}

.emoji-btn.selected {
  background: #e3f2fd;
  border-color: #2196f3;
}

/* 图片上传 */
.image-upload-container {
  position: relative;
}

.image-upload-container input[type="file"] {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  opacity: 0;
  cursor: pointer;
}

.upload-btn {
  position: relative;
  z-index: 1;
}

/* 添加图片预览的样式 */
.image-preview {
  position: relative;
  margin-top: 10px;
}

.image-preview img {
  max-width: 100%;
  max-height: 300px;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 加载状态 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 提示弹窗 */
.notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 5px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 300px;
  z-index: 1001;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
  animation: slideIn 0.3s ease;
}

.notification.success {
  background-color: #4CAF50;
}

.notification.error {
  background-color: #f44336;
}

.notification.warning {
  background-color: #ff9800;
}

.notification .close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  margin-left: 10px;
}

@keyframes slideIn {
  from { transform: translateX(100%); opacity: 0; }
  to { transform: translateX(0); opacity: 1; }
}

/* 移动端适配 */
@media (max-width: 640px) {
  .diary-container {
    padding: 10px;
  }
  
  .diary-form {
    padding: 15px;
  }
  
  .emoji-picker {
    gap: 4px;
  }
  
  .emoji-btn {
    font-size: 20px;
    padding: 6px;
  }
  
  .image-upload-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .upload-btn {
    width: 100%;
    justify-content: center;
  }
  
  .remove-btn {
    width: 100%;
    display: flex;
    justify-content: center;
  }
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 0.5rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.375rem;
  color: #4a5568;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background-color: #f7fafc;
}

.page-btn.active {
  background-color: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style> 