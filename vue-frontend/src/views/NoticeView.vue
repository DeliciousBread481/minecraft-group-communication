<template>
  <div class="notice-view">
    <!-- 面包屑导航 -->
    <div class="breadcrumb-section">
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

    <!-- 主内容区域 -->
    <div class="content-container">
      <!-- 分类选择区域 -->
      <div class="category-section" v-if="!selectedSubCategoryId">
        <div class="section-header">
          <h2>
            <el-icon v-if="!selectedCategoryId"><Bottom /></el-icon>
            <el-icon v-else><Right /></el-icon>
            <span v-if="!selectedCategoryId">选择你的问题类型：</span>
            <span v-else>{{ getSelectedCategoryName() }}</span>
          </h2>
        </div>

        <!-- 主分类列表 -->
        <transition name="slide-fade">
          <div v-if="!selectedCategoryId" class="category-grid">
            <el-card
              v-for="category in categories"
              :key="category.id"
              class="category-card"
              @click="selectCategory(category.id)"
              shadow="hover"
            >
              <div class="category-icon" :style="{ backgroundColor: category.color }">
                <el-icon size="24"><component :is="category.icon" /></el-icon>
              </div>
              <h3>{{ category.name }}</h3>
              <p>{{ category.description }}</p>
            </el-card>
          </div>
        </transition>

        <!-- 子分类选择区域 -->
        <transition name="slide-fade">
          <div v-if="selectedCategoryId" class="sub-category-section">
            <div class="section-header">
              <h2>
                <el-icon><Bottom /></el-icon>
                选择符合具体情况的问题
                <el-button
                  v-if="selectedCategoryId === 'disconnect'"
                  type="warning"
                  @click="showDisconnectInfoManually"
                  class="info-button"
                >
                  <el-icon><Warning /></el-icon>
                  <span>注意事项</span>
                </el-button>
                <el-button
                  type="primary"
                  @click="backToCategories"
                  class="back-button"
                >
                  <el-icon><ArrowLeft /></el-icon>
                  <span>上一步</span>
                </el-button>
              </h2>
            </div>

            <!-- 游戏崩溃类子分类 -->
            <div v-if="selectedCategoryId === 'crash'" class="category-grid">
              <el-card
                v-for="subCategory in subCategories.crash"
                :key="subCategory.id"
                class="category-card"
                @click="selectSubCategory(subCategory.id)"
                shadow="hover"
              >
                <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                  <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                </div>
                <h3>{{ subCategory.name }}</h3>
                <p>{{ subCategory.description }}</p>
              </el-card>
            </div>

            <!-- 连接失败类子分类 -->
            <div v-if="selectedCategoryId === 'disconnect'" class="category-grid">
              <el-card
                v-for="subCategory in subCategories.disconnect"
                :key="subCategory.id"
                class="category-card"
                @click="selectSubCategory(subCategory.id)"
                shadow="hover"
              >
                <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                  <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                </div>
                <h3>{{ subCategory.name }}</h3>
                <p>{{ subCategory.description }}</p>
              </el-card>
            </div>

            <!-- 启动器问题子分类 -->
            <div v-if="selectedCategoryId === 'laucher'" class="category-grid">
              <el-card
                v-for="subCategory in subCategories.laucher"
                :key="subCategory.id"
                class="category-card"
                @click="selectSubCategory(subCategory.id)"
                shadow="hover"
              >
                <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                  <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                </div>
                <h3>{{ subCategory.name }}</h3>
                <p>{{ subCategory.description }}</p>
              </el-card>
            </div>

            <!-- 其他问题子分类 -->
            <div v-if="selectedCategoryId === 'other'" class="category-grid">
              <el-card
                v-for="subCategory in subCategories.other"
                :key="subCategory.id"
                class="category-card"
                @click="selectSubCategory(subCategory.id)"
                shadow="hover"
              >
                <div class="category-icon" :style="{ backgroundColor: subCategory.color }">
                  <el-icon size="24"><component :is="subCategory.icon" /></el-icon>
                </div>
                <h3>{{ subCategory.name }}</h3>
                <p>{{ subCategory.description }}</p>
              </el-card>
            </div>
          </div>
        </transition>
      </div>

      <!-- 文档显示区域 -->
      <transition name="slide-fade">
        <div v-if="selectedSubCategoryId" class="document-section">
          <div class="document-header">
            <el-button type="primary" @click="backToSubCategories" class="back-button">
              <el-icon><ArrowLeft /></el-icon>
              <span>返回</span>
            </el-button>
            <h2>{{ getSelectedSubCategoryName() }}</h2>
          </div>

          <el-card class="document-card" shadow="never">
            <!-- 文档进度指示器 -->
            <div class="document-progress">
              <div class="progress-indicator">
                <div
                  v-for="(step, index) in documentSteps"
                  :key="index"
                  class="progress-step"
                  :class="{
                    'active': index === activeDocStep,
                    'completed': index < activeDocStep
                  }"
                  @click="setActiveDocStep(index)"
                >
                  <div class="step-number">{{ index + 1 }}</div>
                  <div class="step-title">{{ step.title }}</div>
                </div>
              </div>
              <div class="progress-bar">
                <div class="progress-fill" :style="{ width: progressWidth }"></div>
              </div>
            </div>

            <!-- 文档内容 -->
            <div class="document-content" v-loading="isLoading">
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
                <el-icon><ArrowLeft /></el-icon> 上一步
              </el-button>
              <el-button
                @click="navigateDocStep(1)"
                :disabled="activeDocStep >= documentSteps.length - 1"
                type="primary"
              >
                下一步 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </el-card>
        </div>
      </transition>
    </div>

    <!-- 连接失败类弹窗 -->
    <el-dialog
      v-model="showDisconnectDialog"
      title="连接失败类问题注意事项"
      width="60%"
      top="5vh"
    >
      <div class="dialog-content" v-loading="isDialogLoading">
        <div class="markdown-body" v-html="disconnectInfoHtml"></div>
      </div>
      <template #footer>
        <el-button
          type="primary"
          @click="closeDisconnectDialog"
          :disabled="countdownValue > 0"
        >
          {{ countdownValue > 0 ? `请仔细阅读 等待 ${countdownValue} 秒` : '我已了解' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onUnmounted, computed } from 'vue'
import {
  Bottom, Right, ArrowLeft, ArrowRight, Warning
} from "@element-plus/icons-vue";
import { marked } from 'marked';
import axios from 'axios';
import { categories } from "@/components/knowledge/ques"
import { crashCategories } from "@/components/knowledge/crash";
import { disconnectCategories } from "@/components/knowledge/disconnect";
import { lauchercategories } from "@/components/knowledge/laucher";
import { otherCategories } from "@/components/knowledge/other";

// 分类状态
const selectedCategoryId = ref<string>("");
const selectedSubCategoryId = ref<string>("");

// 文档内容状态
const markdownHtml = ref<string>("");
const isLoading = ref<boolean>(false);
const documentSteps = ref<any[]>([]);
const activeDocStep = ref<number>(0);

// 弹窗相关状态
const showDisconnectDialog = ref<boolean>(false);
const disconnectInfoHtml = ref<string>("");
const isDialogLoading = ref<boolean>(false);
const countdownValue = ref<number>(0);
const hasReadDisconnectInfo = ref<boolean>(false);
let countdownTimer: number | null = null;

// 子分类数据
const subCategories = reactive({
  crash: crashCategories,
  disconnect: disconnectCategories,
  laucher: lauchercategories,
  other: otherCategories,
});

// 计算文档进度条宽度
const progressWidth = computed(() => {
  if (documentSteps.value.length <= 1) return '100%';
  return `${(activeDocStep.value / (documentSteps.value.length - 1)) * 100}%`;
});

// 选择主分类
const selectCategory = (categoryId: string) => {
  selectedCategoryId.value = categoryId;
  selectedSubCategoryId.value = "";

  // 当选择连接失败类时，如果没读过则显示弹窗
  if (categoryId === "disconnect" && !hasReadDisconnectInfo.value) {
    loadDisconnectInfoDialog(true);
  }
}

// 选择子分类
const selectSubCategory = (subCategoryId: string) => {
  selectedSubCategoryId.value = subCategoryId;
  const currentSubCategory = getCurrentSubCategory();

  // 设置文档步骤
  if (currentSubCategory && currentSubCategory.docSteps) {
    documentSteps.value = currentSubCategory.docSteps;
  } else {
    // 默认文档步骤
    documentSteps.value = [
      { title: "问题描述", icon: "Document", docSuffix: "_desc" },
      { title: "原因分析", icon: "InfoFilled", docSuffix: "_cause" },
      { title: "解决方案", icon: "SuccessFilled", docSuffix: "_solution" }
    ];
  }

  activeDocStep.value = 0;
  loadMarkdownDocument();
}

// 加载Markdown文档
const loadMarkdownDocument = async () => {
  if (!selectedSubCategoryId.value) return;

  isLoading.value = true;
  try {
    const currentSubCategory = getCurrentSubCategory();
    if (!currentSubCategory || !currentSubCategory.docPath) {
      markdownHtml.value = "<h1>文档不存在</h1>";
      return;
    }

    const currentStep = documentSteps.value[activeDocStep.value];
    const docSuffix = currentStep ? currentStep.docSuffix : "_desc";
    const basePath = currentSubCategory.docPath.replace(/\.md$/, '');
    const docPath = `${basePath}${docSuffix}.md`;

    const response = await axios.get(docPath);
    markdownHtml.value = marked(response.data);
  } catch (error) {
    console.error('加载文档失败:', error);
    markdownHtml.value = "<h1>加载文档失败</h1><p>请稍后再试</p>";
  } finally {
    isLoading.value = false;
  }
};

// 获取当前子分类
const getCurrentSubCategory = () => {
  if (!selectedCategoryId.value || !selectedSubCategoryId.value) return null;
  const category = subCategories[selectedCategoryId.value as keyof typeof subCategories];
  return category.find(sc => sc.id === selectedSubCategoryId.value);
};

// 文档导航
const navigateDocStep = (step: number) => {
  const newStep = activeDocStep.value + step;
  if (newStep >= 0 && newStep < documentSteps.value.length) {
    activeDocStep.value = newStep;
    loadMarkdownDocument();
  }
}

// 设置文档步骤
const setActiveDocStep = (step: number) => {
  if (step >= 0 && step < documentSteps.value.length) {
    activeDocStep.value = step;
    loadMarkdownDocument();
  }
}

// 导航函数
const backToCategories = () => {
  selectedCategoryId.value = "";
  selectedSubCategoryId.value = "";
}

const backToSubCategories = () => {
  selectedSubCategoryId.value = "";
}

// 获取分类名称
const getSelectedCategoryName = () => {
  return categories.find(c => c.id === selectedCategoryId.value)?.name || "";
}

const getSelectedSubCategoryName = () => {
  const currentSubCategory = getCurrentSubCategory();
  return currentSubCategory?.name || "";
}

// 显示连接失败信息弹窗
const showDisconnectInfoManually = () => {
  loadDisconnectInfoDialog(false);
};

// 加载连接失败信息
const loadDisconnectInfoDialog = async (needCountdown: boolean = true) => {
  showDisconnectDialog.value = true;
  isDialogLoading.value = true;
  countdownValue.value = needCountdown ? 8 : 0;

  try {
    const response = await axios.get('/docs/disconnect_info.md');
    disconnectInfoHtml.value = marked(response.data);
    startCountdown();
  } catch (error) {
    console.error('加载连接失败信息失败:', error);
    disconnectInfoHtml.value = "<h1>加载失败</h1><p>无法加载连接失败信息，请稍后再试。</p>";
  } finally {
    isDialogLoading.value = false;
  }
};

// 倒计时逻辑
const startCountdown = () => {
  if (countdownTimer !== null) clearInterval(countdownTimer);

  countdownTimer = setInterval(() => {
    if (countdownValue.value > 0) {
      countdownValue.value--;
    } else {
      if (countdownTimer !== null) clearInterval(countdownTimer);
    }
  }, 1000) as unknown as number;
};

// 关闭弹窗
const closeDisconnectDialog = () => {
  showDisconnectDialog.value = false;
  hasReadDisconnectInfo.value = true;
  if (countdownTimer !== null) clearInterval(countdownTimer);
};

// 组件卸载时清除定时器
onUnmounted(() => {
  if (countdownTimer !== null) clearInterval(countdownTimer);
});
</script>

<style scoped lang="scss">
.notice-view {
  width: 100%;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.breadcrumb-section {
  padding: 15px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--border-color);

  .el-breadcrumb__item {
    cursor: pointer;
    transition: color 0.3s;

    &:hover {
      color: var(--primary-color);
    }

    &:last-child {
      font-weight: bold;
      color: var(--text-color);
    }
  }
}

.content-container {
  width: 100%;
  position: relative;
  min-height: 70vh;
}

.category-section {
  .section-header {
    margin-bottom: 25px;

    h2 {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 1.6rem;
      color: var(--text-color);
      padding-bottom: 10px;
      border-bottom: 2px solid var(--border-light-color);

      .el-icon {
        color: var(--primary-color);
      }
    }
  }
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 25px;
}

.category-card {
  height: 100%;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px var(--shadow-color);
    border-color: var(--primary-color);
  }

  .category-icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 auto 15px;
    color: white;
    transition: transform 0.3s ease;
  }

  h3 {
    font-size: 1.2rem;
    text-align: center;
    margin-bottom: 10px;
    color: var(--text-color);
  }

  p {
    color: var(--text-secondary-color);
    text-align: center;
    font-size: 0.95rem;
    line-height: 1.6;
  }
}

.sub-category-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    flex-wrap: wrap;
    gap: 15px;

    .info-button {
      margin-left: auto;
    }

    .back-button {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.document-section {
  .document-header {
    display: flex;
    align-items: center;
    gap: 20px;
    margin-bottom: 25px;

    h2 {
      font-size: 1.7rem;
      color: var(--text-color);
      margin: 0;
    }

    .back-button {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.document-card {
  border-radius: 12px;
  overflow: hidden;
  background-color: var(--bg-color);
  box-shadow: 0 4px 12px var(--shadow-color);

  .document-progress {
    padding: 15px 20px;
    background-color: var(--bg-secondary-color);
    border-bottom: 1px solid var(--border-color);

    .progress-indicator {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
    }

    .progress-step {
      display: flex;
      flex-direction: column;
      align-items: center;
      cursor: pointer;
      transition: all 0.3s;
      flex: 1;

      .step-number {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        background-color: var(--border-light-color);
        color: var(--text-secondary-color);
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        margin-bottom: 8px;
        transition: all 0.3s;
      }

      .step-title {
        font-size: 0.9rem;
        color: var(--text-secondary-color);
        transition: all 0.3s;
        text-align: center;
      }

      &.active {
        .step-number {
          background-color: var(--primary-color);
          color: white;
        }

        .step-title {
          color: var(--primary-color);
          font-weight: 500;
        }
      }

      &.completed {
        .step-number {
          background-color: var(--success-color);
          color: white;
        }
      }
    }

    .progress-bar {
      height: 6px;
      background-color: var(--border-light-color);
      border-radius: 3px;
      overflow: hidden;

      .progress-fill {
        height: 100%;
        background-color: var(--primary-color);
        border-radius: 3px;
        transition: width 0.4s ease-in-out;
      }
    }
  }

  .document-content {
    padding: 25px;

    .markdown-body {
      line-height: 1.8;
      color: var(--text-color);

      h1, h2, h3, h4 {
        color: var(--heading-color);
        margin-top: 1.5rem;
        margin-bottom: 1rem;
        padding-bottom: 0.5rem;
        border-bottom: 1px solid var(--border-light-color);
      }

      p {
        margin-bottom: 1.2rem;
      }

      ul, ol {
        padding-left: 1.8rem;
        margin-bottom: 1.2rem;
      }

      li {
        margin-bottom: 0.6rem;
      }

      code {
        background-color: var(--bg-secondary-color);
        padding: 0.2rem 0.4rem;
        border-radius: 4px;
        font-family: monospace;
      }

      pre {
        background-color: var(--bg-secondary-color);
        padding: 1rem;
        border-radius: 8px;
        overflow-x: auto;
        margin-bottom: 1.5rem;

        code {
          background: none;
          padding: 0;
        }
      }

      table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 1.5rem;

        th, td {
          padding: 0.8rem;
          border: 1px solid var(--border-color);
        }

        th {
          background-color: var(--bg-secondary-color);
          font-weight: 600;
        }
      }
    }
  }

  .document-navigation {
    display: flex;
    justify-content: space-between;
    padding: 20px;
    border-top: 1px solid var(--border-color);
  }
}

.dialog-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 15px;
}

/* 过渡动画 */
.slide-fade-enter-active, .slide-fade-leave-active {
  transition: all 0.5s ease;
}

.slide-fade-enter-from, .slide-fade-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

@media (max-width: 992px) {
  .category-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 20px;
  }

  .document-navigation {
    flex-direction: column;
    gap: 15px;

    .el-button {
      width: 100%;
    }
  }
}

@media (max-width: 768px) {
  .category-grid {
    grid-template-columns: 1fr;
  }

  .sub-category-section .section-header {
    flex-direction: column;
    align-items: flex-start;

    .info-button, .back-button {
      width: 100%;
      margin-left: 0;
      margin-top: 10px;
      justify-content: center;
    }
  }

  .progress-step {
    .step-title {
      font-size: 0.8rem;
    }
  }
}

@media (max-width: 480px) {
  .notice-view {
    padding: 15px 10px;
  }

  .document-progress {
    padding: 10px 15px !important;

    .progress-step {
      .step-title {
        font-size: 0.75rem;
      }
    }
  }

  .document-content {
    padding: 15px !important;
  }
}
</style>