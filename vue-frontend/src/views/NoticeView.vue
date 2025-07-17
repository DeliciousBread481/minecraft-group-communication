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
                <el-button v-if="selectedCategoryId === 'disconnect'"
                  type="warning"
                  @click="showDisconnectInfoManually"
                  class="info-button">
                  <el-icon><Warning /></el-icon>
                  <span>注意事项</span>
                </el-button>
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
              <!-- 文档内容独立进度条 -->
              <div class="document-progress">
                <div class="progress-indicator">
                  <div class="progress-step"
                       v-for="(step, index) in getDocSteps(getSelectedSubCategoryName(), getSelectedCategoryName())"
                       :key="index"
                       :class="{
                         'active': index === activeDocStep,
                         'completed': index < activeDocStep
                       }"
                       @click="activeDocStep = index">
                    <div class="step-number">{{ index + 1 }}</div>
                    <div class="step-title">{{ step.title }}</div>
                  </div>
                </div>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: progressWidth }"></div>
                </div>
              </div>

              <div class="markdown-scroll-container">
                <div class="markdown-body" v-html="markdownHtml"></div>
              </div>

              <!-- 文档导航按钮 -->
              <div class="document-navigation">
                <el-button
                  @click="navigateDocStep(-1)"
                  :disabled="activeDocStep === 0"
                  type="primary"
                  plain
                >
                  <el-icon><ArrowLeftBold /></el-icon> 上一步
                </el-button>
                <el-button
                  @click="navigateDocStep(1)"
                  :disabled="activeDocStep >= documentSteps.length - 1"
                  type="primary"
                >
                  下一步 <el-icon><ArrowRightBold /></el-icon>
                </el-button>
              </div>
            </el-card>
          </div>
          <!-- 可以在这里添加el-steps -->
        </div>
      </transition>
    </div>

    <!-- 连接失败类弹窗 -->
    <el-dialog
      v-model="showDisconnectDialog"
      title="连接失败类问题"
      width="60%"
      top="5vh"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
    >

      <div class="markdown-body" v-html="disconnectInfoHtml" v-loading="isDialogLoading"></div>
      <template #footer>
        <span class="dialog-footer">
          <el-button
            type="primary"
            @click="closeDisconnectDialog"
            :disabled="countdownValue > 0">
            {{ countdownValue > 0 ? `请仔细阅读 等待 ${countdownValue} 秒` : '我已了解' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup lang="ts">
import { Bottom, Right, ArrowLeftBold, ArrowRightBold, Warning, Loading, Document, InfoFilled, SuccessFilled } from "@element-plus/icons-vue";
import { ref, reactive, type Component, onMounted, watch, onUnmounted, computed } from "vue";
import { categories } from "@/components/knowledge/ques"
import { marked } from 'marked';
import axios from 'axios';
import { crashCategories } from "@/components/knowledge/crash";
import { disconnectCategories } from "@/components/knowledge/disconnect";
import { lauchercategories } from "@/components/knowledge/laucher";
import { otherCategories } from "@/components/knowledge/other";

const ques = ref("")
const selectedCategoryId = ref("")
const selectedSubCategoryId = ref("")
const markdownContent = ref("")
const markdownHtml = ref("")
const isLoading = ref(false)

// 弹窗相关
const showDisconnectDialog = ref(false)
const disconnectInfoContent = ref("")
const disconnectInfoHtml = ref("")
const isDialogLoading = ref(false)
const countdownValue = ref(0)
let countdownTimer: number | null = null
// 已读状态记录
const hasReadDisconnectInfo = ref(false)

// 文档页进度条相关
const activeDocStep = ref(0) // 文档页当前激活的步骤
// 每个步骤对应一个独立的Markdown文档
// 现在documentSteps会动态设置，这里只提供一个空的初始值
const documentSteps = ref<{title: string, description: string, icon: string, docSuffix: string}[]>([])
function getDocSteps(subcategoriesname: string, categoriesname: string): {title: string, description: string, icon: string, docSuffix: string}[] {
  const subCategories = [crashCategories, disconnectCategories, lauchercategories, otherCategories]
  for(let i=0; i<categories.value.length; i++) {
    if(categoriesname === categories.value[i].name) {
      for(let j=0; j<subCategories[i].length; j++) {
        if(subcategoriesname === subCategories[i][j].name) {
          return subCategories[i][j].docSteps;
        }
      }
    }
  }
  return [];
}

// eslint-disable-next-line @typescript-eslint/no-unused-vars
interface SubCategory {
  id: string;
  name: string;
  icon: Component;
  description: string;
  color: string;
  docPath?: string;
  docSteps?: {
    title: string;
    description: string;
    icon: string;
    docSuffix: string;
  }[];
}

// 子分类数据
const subCategories = reactive({
  crash: crashCategories as SubCategory[],
  disconnect: disconnectCategories as SubCategory[],
  laucher: lauchercategories as SubCategory[],
  other: otherCategories as SubCategory[],
})

// 监听子分类选择变化，加载对应的Markdown文档
watch(selectedSubCategoryId, async (newVal) => {
  if (newVal) {
    await loadMarkdownDocument();
  }
});

// 监听文档步骤变化，加载对应步骤的Markdown文档
watch(activeDocStep, async () => {
  if (selectedSubCategoryId.value) {
    await loadMarkdownDocument();
  }
});

// 计算文档进度条宽度
const progressWidth = computed(() => {
  if (documentSteps.value.length <= 1) return '100%';
  // 计算完成的百分比
  const totalSteps = documentSteps.value.length - 1; // 总步数减1（0到length-1）
  const currentProgress = Math.min(activeDocStep.value, totalSteps); // 确保不超过最大步数
  return `${(currentProgress / totalSteps) * 100}%`;
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

    // 获取当前步骤的文档后缀
    const currentStep = documentSteps.value[activeDocStep.value];
    const docSuffix = currentStep ? currentStep.docSuffix : "_desc"; // 默认为描述文档

    // 确保docPath存在
    if (!currentCategory.docPath) {
      markdownContent.value = "# 文档路径未定义";
      markdownHtml.value = "<h1>文档路径未定义</h1><p>该问题类型尚未配置文档路径</p>";
      return;
    }

    // 构建文档路径，基础路径+后缀
    // 从docPath中提取基本路径（不含.md扩展名）
    const basePath = currentCategory.docPath.replace(/\.md$/, '');
    const docPath = `${basePath}${docSuffix}.md`;

    console.log('加载文档路径:', docPath); // 调试用，上线前可以移除

    // 加载文档
    const response = await axios.get(docPath);
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

  // 当选择连接失败类时，如果没读过则显示弹窗
  if (categoryId === "disconnect" && !hasReadDisconnectInfo.value) {
    loadDisconnectInfoDialog(true); // true表示是自动弹出，需要倒计时
  }
}

const selectSubCategory = (subCategoryId: string) => {
  selectedSubCategoryId.value = subCategoryId;
  // 记录已选的子分类
  console.log('选择了子分类:', subCategoryId);

  // 获取当前选中的子分类
  const currentSubCategory = getCurrentSubCategory();

  // 如果子分类有自定义的文档步骤，使用它
  if (currentSubCategory && 'docSteps' in currentSubCategory) {
    // 使用自定义文档步骤
    setDocumentSteps(currentSubCategory.docSteps as any[], 0);
  } else {
    // 使用默认步骤并重置文档步骤到第一步
    setDocumentSteps([
      {
        title: "问题描述",
        description: "了解问题的表现",
        icon: "Document",
        docSuffix: "_desc"
      },
      {
        title: "原因分析",
        description: "问题产生的原因",
        icon: "InfoFilled",
        docSuffix: "_cause"
      },
      {
        title: "解决方案",
        description: "可行的解决步骤",
        icon: "Loading",
        docSuffix: "_solution"
      }
    ], 0);
  }
}

// 文档导航函数 - 上一步/下一步
const navigateDocStep = async (step: number) => {
  // 计算新的步骤索引
  const newStepIndex = activeDocStep.value + step;

  // 检查边界
  if (newStepIndex < 0 || newStepIndex >= documentSteps.value.length) {
    return;
  }

  // 更新当前步骤
  activeDocStep.value = newStepIndex;

  // 重新加载对应步骤的文档
  await loadMarkdownDocument();
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

// 手动显示弹窗
const showDisconnectInfoManually = () => {
  loadDisconnectInfoDialog(false); // false表示手动点击，不需要倒计时
};

// 加载连接失败类弹窗内容
const loadDisconnectInfoDialog = async (needCountdown: boolean = true) => {
  showDisconnectDialog.value = true;
  isDialogLoading.value = true;
  countdownValue.value = needCountdown ? 8 : 0; // 只有需要倒计时时才设置8秒

  try {
    // 加载markdown文件
    const response = await axios.get('/docs/disconnect_info.md');
    disconnectInfoContent.value = response.data;
    disconnectInfoHtml.value = marked(response.data);

    // 开始倒计时
    startCountdown();


  } catch (error) {
    console.error('加载连接失败信息失败:', error);
    disconnectInfoContent.value = "# 加载失败\n\n无法加载连接失败信息，请稍后再试。";
    disconnectInfoHtml.value = marked(disconnectInfoContent.value);
  } finally {
    isDialogLoading.value = false;
  }
};

// 倒计时逻辑
const startCountdown = () => {
  // 清除之前的定时器
  if (countdownTimer !== null) {
    clearInterval(countdownTimer);
  }

  // 设置新的定时器
  countdownTimer = setInterval(() => {
    if (countdownValue.value > 0) {
      countdownValue.value--;
    } else {
      // 倒计时结束，清除定时器
      if (countdownTimer !== null) {
        clearInterval(countdownTimer);
        countdownTimer = null;
      }
      // 进度条已移除
    }
  }, 1000) as unknown as number;
};

// 关闭弹窗
const closeDisconnectDialog = () => {
  showDisconnectDialog.value = false;
  // 标记为已读
  hasReadDisconnectInfo.value = true;
  // 清除定时器
  if (countdownTimer !== null) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
  // 进度条已移除，不需要重置步骤
};

// 组件卸载时清除定时器
onUnmounted(() => {
  if (countdownTimer !== null) {
    clearInterval(countdownTimer);
    countdownTimer = null;
  }
});

// 动态设置文档页进度条步骤
const setDocumentSteps = (steps: {title: string, description: string, icon: string, docSuffix: string}[], startStep: number = 0) => {
  documentSteps.value = steps;
  activeDocStep.value = startStep;
};

// 手动设置文档当前进度步骤
const setActiveDocStep = (step: number) => {
  if (step >= 0 && step <= documentSteps.value.length) {
    activeDocStep.value = step;
  }
};

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

.info-button {
  margin-left: 15px;
  margin-right: auto;
  display: flex;
  align-items: center;
  gap: 5px;
}

.dialog-steps {
  margin-bottom: 20px;
  padding: 10px 0;
}

.document-steps {
  margin: 10px 0 25px;
  padding: 15px 0;
  background-color: #f8f9fa;
  border-radius: 8px;
}

/* 定制步骤条样式 */
.el-step__title {
  font-size: 14px !important;
}

.el-step__description {
  font-size: 12px !important;
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

/* 文档导航按钮样式 */
.document-navigation {
  display: flex;
  justify-content: space-between;
  padding: 20px;
  border-top: 1px solid #eaecef;
  margin-top: 20px;
}

.document-navigation .el-button {
  min-width: 110px;
}

/* 自定义进度条步骤样式 */
:deep(.el-step__head.is-process) {
  color: #409EFF;
  border-color: #409EFF;
}

:deep(.el-step__title.is-process) {
  color: #409EFF;
}

:deep(.el-step__description.is-process) {
  color: #409EFF;
}

/* 文档内容独立进度条样式 */
.document-progress {
  margin-bottom: 20px;
  padding: 15px 0;
}

.progress-indicator {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  position: relative;
  width: 33.33%;
  transition: all 0.3s;
}

.progress-step .step-number {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #ebeef5;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 5px;
  transition: all 0.3s;
}

.progress-step .step-title {
  font-size: 14px;
  color: #909399;
  transition: all 0.3s;
}

.progress-step.active .step-number {
  background-color: #409EFF;
  color: white;
}

.progress-step.active .step-title {
  color: #409EFF;
  font-weight: bold;
}

.progress-step.completed .step-number {
  background-color: #67C23A;
  color: white;
}

.progress-bar {
  height: 6px;
  background-color: #ebeef5;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #409EFF;
  border-radius: 3px;
  transition: width 0.4s ease-in-out;
}

/* 文档滚动容器样式 */
.markdown-scroll-container {
  max-height: 500px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 0 5px;
  margin-bottom: 15px;

  /* 自定义滚动条样式 */
  scrollbar-width: thin;
  scrollbar-color: #C0C4CC #F2F6FC;
}

/* Webkit浏览器的滚动条样式 */
.markdown-scroll-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.markdown-scroll-container::-webkit-scrollbar-thumb {
  background-color: #C0C4CC;
  border-radius: 3px;
}

.markdown-scroll-container::-webkit-scrollbar-track {
  background-color: #F2F6FC;
  border-radius: 3px;
}

/* 悬停时的滚动条样式 */
.markdown-scroll-container:hover::-webkit-scrollbar-thumb {
  background-color: #909399;
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }

  .document-section {
    padding: 10px;
  }

  .document-navigation {
    flex-direction: column;
    gap: 10px;
  }

  .document-navigation .el-button {
    width: 100%;
  }
}
</style>
