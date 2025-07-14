<template>
  <el-container direction="vertical">
    <div class="breadcrumb-view">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item @click="backToCategories">
          群公告文档
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="selectedCategoryId" @click="backToCategories">
          {{ getSelectedCategoryName() }}
        </el-breadcrumb-item>
        <el-breadcrumb-item v-if="selectedSubCategoryId" @click="backToSubCategories">
          {{ getSelectedSubCategoryName() }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="main-content">
      <!-- 第一层分类 -->
      <transition name="slide-fade-left">
        <div class="category-section" v-if="!selectedSubCategoryId">
          <h2>
            <transition name="rotate-icon">
              <el-icon v-if="!selectedCategoryId"><Bottom /></el-icon>
              <el-icon v-else><Right /></el-icon>
            </transition>
            <span v-if="!selectedCategoryId">选择你的问题类型：</span>
            <span v-else>{{ getSelectedCategoryName() }}</span>
          </h2>

          <transition name="slide-up">
            <div v-if="!selectedCategoryId" class="category-grid">
              <div
                v-for="category in categories"
                :key="category.id"
                class="category-card"
                @click="selectCategory(category.id)"
              >
                <div class="category-icon" :style="{ backgroundColor: category.color }">
                  <el-icon size="24"><component :is="category.icon" /></el-icon>
                </div>
                <h3>{{ category.name }}</h3>
                <p>{{ category.description }}</p>
              </div>
            </div>
          </transition>

          <transition name="slide-down">
            <div v-if="selectedCategoryId" class="sub-category-section">
              <h2>
                <el-icon><Bottom /></el-icon>
                选择符合具体情况的问题
                <el-button text bg @click="backToCategories" class="back-button right-button">
                  <el-icon><ArrowLeftBold /></el-icon>
                  <h3 color="#409EFF">上一步</h3>
                </el-button>
              </h2>

              <!-- 游戏崩溃类子分类 -->
              <div v-if="selectedCategoryId === 'crash'" class="category-grid">
                <div
                  v-for="subCategory in subCategories.crash"
                  :key="subCategory.id"
                  class="category-card"
                  @click="selectSubCategory(subCategory.id)"
                >
                  <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                    <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                  </div>
                  <h3>{{ subCategory.name }}</h3>
                  <p>{{ subCategory.description }}</p>
                </div>
              </div>

              <!-- 连接失败类子分类 -->
              <div v-if="selectedCategoryId === 'disconnect'" class="category-grid">
                <div
                  v-for="subCategory in subCategories.disconnect"
                  :key="subCategory.id"
                  class="category-card"
                  @click="selectSubCategory(subCategory.id)"
                >
                  <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                    <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                  </div>
                  <h3>{{ subCategory.name }}</h3>
                  <p>{{ subCategory.description }}</p>
                </div>
              </div>

              <!-- 启动器问题子分类 -->
              <div v-if="selectedCategoryId === 'laucher'" class="category-grid">
                <div
                  v-for="subCategory in subCategories.laucher"
                  :key="subCategory.id"
                  class="category-card"
                  @click="selectSubCategory(subCategory.id)"
                >
                  <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                    <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                  </div>
                  <h3>{{ subCategory.name }}</h3>
                  <p>{{ subCategory.description }}</p>
                </div>
              </div>

              <!-- 其他问题子分类 -->
              <div v-if="selectedCategoryId === 'other'" class="category-grid">
                <div
                  v-for="subCategory in subCategories.other"
                  :key="subCategory.id"
                  class="category-card"
                  @click="selectSubCategory(subCategory.id)"
                >
                  <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                    <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                  </div>
                  <h3>{{ subCategory.name }}</h3>
                  <p>{{ subCategory.description }}</p>
                </div>
              </div>
            </div>
          </transition>
        </div>
      </transition>

      <!-- 文档显示区域 -->
      <transition name="slide-fade-right">
        <div v-if="selectedSubCategoryId" class="document-section">
          <div class="document-header">
            <el-button type="primary" @click="backToSubCategories" class="back-button">
              <el-icon><ArrowLeftBold /></el-icon>
              <span>返回</span>
            </el-button>
            <h2>{{ getSelectedSubCategoryName() }}</h2>
          </div>

          <div class="document-content" v-loading="isLoading">
            <el-card class="markdown-card">
              <div class="markdown-body" v-html="markdownHtml"></div>
            </el-card>
          </div>
        </div>
      </transition>
    </div>
  </el-container>
</template>

<script setup lang="ts">
import { Bottom, CircleCloseFilled, More, Switch, VideoPlay, QuestionFilled, WarningFilled, Close, Connection, Right, ArrowLeftBold } from "@element-plus/icons-vue";
import { ref, reactive, type Component, onMounted, watch } from "vue";
import { marked } from 'marked';
import axios from 'axios';

const ques = ref("")
const selectedCategoryId = ref("")
const selectedSubCategoryId = ref("")
const markdownContent = ref("")
const markdownHtml = ref("")
const isLoading = ref(false)

// 定义类型
interface Category {
  id: string;
  name: string;
  icon: Component;
  description: string;
  color: string;
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
interface SubCategory {
  id: string;
  name: string;
  icon: Component;
  description: string;
  color: string;
  docPath?: string;
}

const categories = ref<Category[]>([
  {
    id: 'crash',
    name: '游戏崩溃类',
    icon: CircleCloseFilled,
    description: '游戏启动失败 游戏闪退 游戏崩溃等',
    color: '#9c27b0'
  },
  {
    id: 'disconnect',
    name: '连接失败类',
    icon: Switch,
    description: '联机失败 连接服务器失败 单人存档被踢出等',
    color: '#409EFF'
  },
  {
    id: 'laucher',
    name: '启动器问题',
    icon: VideoPlay,
    description: '启动器闪退 启动器崩溃 启动器无法启动等',
    color: '#E6A23C'
  },
  {
    id: 'other',
    name: '其他问题',
    icon: More,
    description: '其他问题',
    color: '#67C23A'
  }
])

// 子分类数据
const subCategories = reactive({
  crash: [
    {
      id: 'crash_startup',
      name: '启动崩溃',
      icon: Close,
      description: '游戏无法启动或启动过程中崩溃',
      color: '#9c27b0',
      docPath: '/docs/crash_startup.md'
    },
    {
      id: 'crash_ingame',
      name: '游戏内崩溃',
      icon: CircleCloseFilled,
      description: '游戏运行中突然崩溃或黑屏',
      color: '#9c27b0',
      docPath: '/docs/crash_ingame.md'
    },
    {
      id: 'crash_mod',
      name: 'MOD兼容性问题',
      icon: WarningFilled,
      description: 'MOD加载失败或MOD冲突导致崩溃',
      color: '#9c27b0',
      docPath: '/docs/crash_mod.md'
    }
  ],
  disconnect: [
    {
      id: 'disconnect_server',
      name: '服务器连接失败',
      icon: Connection,
      description: '无法连接到服务器或连接中断',
      color: '#409EFF',
      docPath: '/docs/disconnect_server.md'
    },
    {
      id: 'disconnect_timeout',
      name: '连接超时',
      icon: Switch,
      description: '连接服务器时超时',
      color: '#409EFF',
      docPath: '/docs/disconnect_timeout.md'
    },
    {
      id: 'disconnect_kicked',
      name: '被踢出服务器',
      icon: Close,
      description: '连接后被服务器踢出',
      color: '#409EFF',
      docPath: '/docs/disconnect_kicked.md'
    },
    {
      id: 'disconnect_lan',
      name: '局域网连接问题',
      icon: Connection,
      description: '无法通过局域网连接到其他玩家',
      color: '#409EFF',
      docPath: '/docs/disconnect_lan.md'
    }
  ],
  laucher: [
    {
      id: 'launcher_crash',
      name: '启动器崩溃',
      icon: CircleCloseFilled,
      description: '启动器无法打开或使用过程中崩溃',
      color: '#E6A23C',
      docPath: '/docs/launcher_crash.md'
    },
    {
      id: 'launcher_login',
      name: '账号登录问题',
      icon: QuestionFilled,
      description: '无法登录账号或认证失败',
      color: '#E6A23C',
      docPath: '/docs/launcher_login.md'
    },
    {
      id: 'launcher_download',
      name: '下载问题',
      icon: WarningFilled,
      description: '游戏文件或资源下载失败',
      color: '#E6A23C',
      docPath: '/docs/launcher_download.md'
    }
  ],
  other: [
    {
      id: 'other_performance',
      name: '性能问题',
      icon: WarningFilled,
      description: '游戏卡顿、FPS低或内存占用过高',
      color: '#67C23A',
      docPath: '/docs/other_performance.md'
    },
    {
      id: 'other_graphics',
      name: '画面问题',
      icon: QuestionFilled,
      description: '画面异常、材质错误或光影问题',
      color: '#67C23A',
      docPath: '/docs/other_graphics.md'
    },
    {
      id: 'other_sound',
      name: '声音问题',
      icon: More,
      description: '无声音、声音异常或音效缺失',
      color: '#67C23A',
      docPath: '/docs/other_sound.md'
    }
  ]
})

// 监听子分类选择变化，加载对应的Markdown文档
watch(selectedSubCategoryId, async (newVal) => {
  if (newVal) {
    await loadMarkdownDocument();
  }
});

// 加载Markdown文档
const loadMarkdownDocument = async () => {
  if (!selectedSubCategoryId.value) return;

  isLoading.value = true;
  try {
    // 获取当前子分类
    const currentCategory = getCurrentSubCategory();
    if (!currentCategory || !currentCategory.docPath) {
      markdownContent.value = "# 文档不存在";
      markdownHtml.value = "<h1>文档不存在</h1>";
      return;
    }

    // 加载文档
    const response = await axios.get(currentCategory.docPath);
    markdownContent.value = response.data;
    markdownHtml.value = marked(response.data);
  } catch (error) {
    console.error('加载Markdown文档失败:', error);
    markdownContent.value = "# 加载文档失败";
    markdownHtml.value = "<h1>加载文档失败</h1><p>请稍后再试</p>";
  } finally {
    isLoading.value = false;
  }
};

// 获取当前子分类
const getCurrentSubCategory = () => {
  if (!selectedCategoryId.value || !selectedSubCategoryId.value) return null;

  const categoryId = selectedCategoryId.value as keyof typeof subCategories;
  return subCategories[categoryId].find(sc => sc.id === selectedSubCategoryId.value);
};

const selectCategory = (categoryId: string) => {
  ques.value = categoryId;
  selectedCategoryId.value = categoryId;
  selectedSubCategoryId.value = "";
}

const selectSubCategory = (subCategoryId: string) => {
  selectedSubCategoryId.value = subCategoryId;
  // 记录已选的子分类
  console.log('选择了子分类:', subCategoryId);
}

const backToCategories = () => {
  const documentSection = document.querySelector('.document-section');
  const subCategorySection = document.querySelector('.sub-category-section');

  if (documentSection && selectedSubCategoryId.value) {
    documentSection.classList.add('leaving');

    setTimeout(() => {
      selectedCategoryId.value = "";
      selectedSubCategoryId.value = "";
      ques.value = "";
    }, 500);
  }

  else if (subCategorySection && selectedCategoryId.value) {
    setTimeout(() => {
      selectedCategoryId.value = "";
      selectedSubCategoryId.value = "";
      ques.value = "";
    }, 300);
  }
  else {
    selectedCategoryId.value = "";
    selectedSubCategoryId.value = "";
    ques.value = "";
  }
}

const backToSubCategories = () => {
  const documentSection = document.querySelector('.document-section');
  if (documentSection) {
    documentSection.classList.add('leaving');
    setTimeout(() => {
      selectedSubCategoryId.value = "";
    }, 500);
  } else {
    selectedSubCategoryId.value = "";
  }
}

const getSelectedCategoryName = () => {
  const category = categories.value.find(c => c.id === selectedCategoryId.value);
  return category ? category.name : "";
}

const getSelectedSubCategoryName = () => {
  const currentSubCategory = getCurrentSubCategory();
  return currentSubCategory ? currentSubCategory.name : "";
}

// 初始化
onMounted(() => {
  // 可以在这里初始化一些配置
});
</script>

<style scoped>
.solution-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb-view {
  width: 100%;
  height: 40px;
  padding: 10px 0;
  margin-bottom: 10px;
}

.el-breadcrumb__item {
  cursor: pointer;
}

.el-breadcrumb__item:hover .el-breadcrumb__inner {
  color: #409EFF;
}

.el-breadcrumb__item .el-breadcrumb__inner {
  font-weight: normal;
}
.el-breadcrumb__item:last-child .el-breadcrumb__inner {
  font-weight: bold;
}

.main-content {
  width: 100%;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 100%;
  padding: 20px 0;
}

.category-section, .sub-category-section, .document-section {
  position: absolute;
  width: 100%;
  left: 0;
  right: 0;
}

.slide-fade-left-enter-from,
.slide-fade-left-leave-to {
  transform: translateX(-50px) translateY(0) !important;
}

/* 向左滑动淡出效果 */
.slide-fade-left-enter-active,
.slide-fade-left-leave-active {
  transition: all 0.3s ease;
  position: absolute;
  width: 100%;
}

.slide-fade-left-enter-active {
  transition-delay: 0.38s;
}


.slide-fade-left-enter-from {
  opacity: 0;
}

.slide-fade-left-leave-to {
  opacity: 0;
}

/* 向右滑动淡入效果 */
.slide-fade-right-enter-active,
.slide-fade-right-leave-active {
  transition: all 0.3s ease;
}


.slide-fade-right-enter-from {
  transform: translateX(50px);
  opacity: 0;
}

.slide-fade-right-enter-active {
  transition-delay: 0.38s;
}

.slide-fade-right-leave-to {
  transform: translateX(50px); /* 向右滑出 */
  opacity: 0;
}

/* 向上滑动过渡效果 */
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-active {
  transition-delay: 0.38s;
}

.slide-up-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-up-leave-to {
  transform: translateY(-100%);
  opacity: 0;
}

/* 向下滑动过渡效果 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-active {
  transition-delay: 0.38s;
}

.slide-down-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-down-leave-to {
  transform: translateY(100%);
  opacity: 0;
}

.back-button {
  margin-right: 10px;
}

.right-button {
  margin-left: auto;
  background-color: #409EFF !important;
  color: white !important;
  border-color: #409EFF !important;
}

.right-button h3 {
  color: white !important;
  margin: 0;
  font-size: 14px;
  font-weight: normal;
}

.right-button .el-icon {
  color: white !important;
}

.category-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #f0f2f5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #409EFF;
}

.category-card.active {
  border-color: #409EFF;
  background-color: #f0f7ff;
}

.category-card:hover .category-icon {
  transform: scale(1.1);
}

.category-card h3 {
  font-size: 1.3rem;
  margin-bottom: 10px;
  color: #2c3e50;
}

.category-card p {
  color: #7f8c8d;
  font-size: 0.95rem;
  line-height: 1.5;
}

.category-section {
  position: absolute;
  left: 0;
  right: 0;
  z-index: 1;
}

.sub-category-section {
  position: absolute;
  left: 0;
  right: 0;
  z-index: 1;
}

.category-section h2 {
  font-size: 1.8rem;
  margin-bottom: 25px;
  color: #2c3e50;
  position: relative;
  padding-left: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.category-section h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 5px;
  bottom: 5px;
  width: 5px;
  background-color: #409EFF;
  border-radius: 3px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.category-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  color: white;
  transition: transform 0.3s ease;
}

.category-card:hover .category-icon {
  transform: scale(1.1);
}

.sub-category-section h2 {
  font-size: 1.8rem;
  margin-bottom: 25px;
  color: #2c3e50;
  position: relative;
  padding-left: 15px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.sub-category-section h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 5px;
  bottom: 5px;
  width: 5px;
  background-color: #409EFF;
  border-radius: 3px;
}

/* 文档部分样式 */
.document-section {
  width: 100%;
  padding: 20px;
  position: absolute;
  left: 0;
  right: 0;
}

/* 文档区域离开动画 */
.document-section.leaving {
  animation: slideRightOut 0.5s forwards;
}

@keyframes slideRightOut {
  from {
    transform: translateX(0) translateY(0);
    opacity: 1;
  }
  to {
    transform: translateX(50px) translateY(0);
    opacity: 0;
  }
}

.document-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
}

.document-header h2 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin: 0;
}

.markdown-card {
  width: 100%;
  margin-bottom: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.markdown-body {
  padding: 20px;
  line-height: 1.7;
  color: #2c3e50;
}

.markdown-body h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h2 {
  font-size: 1.5rem;
  margin: 1.5rem 0 1rem;
  padding-bottom: 0.3rem;
  border-bottom: 1px solid #eaecef;
}

.markdown-body h3 {
  font-size: 1.25rem;
  margin: 1.2rem 0 0.8rem;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 2rem;
}

.markdown-body li {
  margin: 0.5rem 0;
}

.markdown-body code {
  padding: 0.2em 0.4em;
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace;
}

.markdown-body pre {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
}

.markdown-body pre code {
  padding: 0;
  background-color: transparent;
}

.markdown-body a {
  color: #409EFF;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body table {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
}

.markdown-body table th,
.markdown-body table td {
  padding: 8px 12px;
  border: 1px solid #dfe2e5;
}

.markdown-body table th {
  background-color: #f6f8fa;
  font-weight: 600;
}

/* 图标旋转动画 */
.rotate-icon-enter-active,
.rotate-icon-leave-active {
  transition: all 0.3s ease;
}

.rotate-icon-enter-from,
.rotate-icon-leave-to {
  transform: rotate(-90deg);
  opacity: 0;
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .document-section {
    padding: 10px;
  }
}
</style>
