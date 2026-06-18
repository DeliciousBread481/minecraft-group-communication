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
        <el-breadcrumb-item
          v-if="selectedLauncherId"
          @click="backToLauncherSelection"
        >
          {{ getSelectedLauncherName() }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 主内容区域 -->
    <div class="content-container">
      <!-- 分类选择区域 -->
      <transition name="slide-fade" mode="out-in">
        <div :key="!!(selectedSubCategoryId && selectedLauncherId) ? 'document' : 'categories'">
          <div class="category-section" v-if="!(selectedSubCategoryId && selectedLauncherId)">
            <transition name="slide-fade" mode="out-in">
              <div :key="selectedSubCategoryId ? 'launcher' : (selectedCategoryId ? 'sub' : 'main')">
                <div v-if="selectedSubCategoryId && !selectedLauncherId">
                  <div class="section-header">
                    <h2>
                      <el-icon><Right /></el-icon>
                      {{ getSelectedCategoryName() }}
                    </h2>
                  </div>
                  <div class="section-header">
                    <h2>
                      <el-icon><Right /></el-icon>
                      {{ getSelectedSubCategoryName() }}
                    </h2>
                  </div>
                  <div class="section-header">
                    <h2>
                      <el-icon><Bottom /></el-icon>
                      你是使用的哪个启动器？
                      <el-button
                        type="primary"
                        @click="backToSubCategories"
                        class="back-button"
                      >
                        <el-icon><ArrowLeft /></el-icon>
                        <span>上一步</span>
                      </el-button>
                    </h2>
                  </div>
                  <div class="category-grid">
                    <el-card
                      v-for="launcher in currentLaunchers"
                      :key="launcher.id"
                      class="category-card"
                      @click="selectLauncher(launcher.id)"
                      shadow="hover"
                    >
                      <div class="category-icon" :style="{ backgroundColor: launcher.color }">
                        <el-icon size="24"><Monitor /></el-icon>
                      </div>
                      <h3>{{ launcher.name }}</h3>
                      <p>{{ launcher.description }}</p>
                    </el-card>
                  </div>
                </div>

                <div v-else>
                  <div class="section-header">
                    <h2>
                      <el-icon v-if="!selectedCategoryId"><Bottom /></el-icon>
                      <el-icon v-else><Right /></el-icon>
                      <span v-if="!selectedCategoryId">选择你的问题类型：</span>
                      <span v-else>{{ getSelectedCategoryName() }}</span>
                    </h2>
                  </div>

                  <!-- 主分类列表 -->
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

                  <!-- 子分类选择区域 -->
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
                </div>

              </div>
            </transition>
          </div>    
          <div v-else class="document-section"> 
            <div class="section-header">
              <h2>
                <el-icon><Right /></el-icon>
                {{ getSelectedSubCategoryName() }}
                <el-button type="primary" @click="backFromDocument" class="back-button">
                  <el-icon><ArrowLeft /></el-icon>
                  <span>返回</span>
                </el-button>
              </h2>
            </div>

            <el-card class="document-card" shadow="never">
              <!-- 文档进度指示器 -->
              <div class="document-progress">  
                <div class="progress-steps">  
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
                  <div class="progress-bar">  
                    <div class="progress-fill" :style="{ width: progressWidth }"></div>  
                  </div>  
                </div>  
              </div>

              <!-- 文档内容 -->
              <div class="document-content" v-loading="isLoading">
                <!-- JSON 模式渲染 -->
                <div v-if="jsonBlocks.length > 0" class="json-content">
                  <template v-for="(block, blockIndex) in jsonBlocks" :key="blockIndex">
                    <!-- 文本块 -->
                    <div
                      v-if="block.type === 'text'"
                      class="json-block-text markdown-body"
                      v-html="block.content"
                    ></div>
                    <!-- 提示/警告块 -->
                    <el-alert
                      v-else-if="block.type === 'alert'"
                      :title="block.content"
                      :type="block.level || 'info'"
                      show-icon
                      :closable="false"
                      class="json-block-alert"
                    />
                    <!-- 输入框块 -->
                    <div v-else-if="block.type === 'input'" class="json-block-input">
                      <label class="json-input-label">{{ block.label }}</label>
                      <el-input
                        v-model="inputValues[block.key]"
                        :placeholder="block.placeholder"
                        :type="block.multiline ? 'textarea' : 'text'"
                        :rows="block.rows || 4"
                      />
                    </div>
                    <!-- 步骤列表块 -->
                    <div v-else-if="block.type === 'steps'" class="json-block-steps">  
                      <el-steps direction="vertical" :active="block.items.length" :space="45">  
                        <el-step  
                          v-for="(item, i) in block.items"  
                          :key="i"  
                          :title="typeof item === 'string' ? item : item.title"  
                        >  
                          <template v-if="typeof item !== 'string'" #description>  
                            <div v-if="item.image" class="json-step-image">
                              <img
                                :src="item.image.src"
                                :alt="item.image.alt || ''"
                                style="max-width: 100%; border-radius: 6px; border: 1px solid var(--el-border-color); margin-top: 8px;"
                              />
                              <p v-if="item.image.caption" class="json-image-caption">{{ item.image.caption }}</p>
                            </div>
                            <div v-if="item.code" class="json-block-code" style="margin-top: 8px;">
                              <span v-if="item.code.lang" class="json-code-lang">{{ item.code.lang }}</span>
                              <button
                                class="json-code-copy-btn"
                                @click="copyCode(item.code.content, `step-${i}`)"
                                :class="{ copied: copiedCodeIndex === `step-${i}` }"
                              >
                                {{ copiedCodeIndex === `step-${i}` ? '已复制' : '复制' }}
                              </button>
                              <div class="json-code-content">
                                <pre><code>{{ item.code.content }}</code></pre>
                              </div>
                            </div>
                            <div v-if="item.description" class="json-step-desc" v-html="linkify(item.description)"></div>
                            <div v-if="item.url" class="json-step-link">
                              <a :href="item.url" target="_blank" rel="noopener noreferrer">{{ item.urlText || item.url }}</a>
                            </div>
                          </template>  
                        </el-step>  
                      </el-steps>  
                    </div>
                    <div v-else-if="block.type === 'image'" class="json-block-image">
                      <img
                        :src="block.src"
                        :alt="block.alt || ''"
                        :style="{ maxWidth: block.maxWidth || '100%' }"
                      />
                      <p v-if="block.caption" class="json-image-caption">{{ block.caption }}</p>
                    </div>
                    <div v-else-if="block.type === 'code'" class="json-block-code">
                      <span v-if="block.lang" class="json-code-lang">{{ block.lang }}</span>
                      <button
                        class="json-code-copy-btn"
                        @click="copyCode(block.content, blockIndex)"
                        :class="{ copied: copiedCodeIndex === blockIndex }"
                      >
                        {{ copiedCodeIndex === blockIndex ? '已复制' : '复制' }}
                      </button>
                      <div class="json-code-content">
                        <pre><code>{{ block.content }}</code></pre>
                      </div>
                    </div>
                  </template>
                </div>
                <!-- Markdown 模式渲染（原有） -->
                <div v-else class="markdown-body" v-html="markdownHtml"></div>
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
                  v-if="activeDocStep < documentSteps.length - 1"
                  @click="navigateDocStep(1)"
                  type="primary"
                >
                  下一步 <el-icon><ArrowRight /></el-icon>
                </el-button>
                <el-button
                  v-else
                  @click="handleComplete"
                  type="success"
                >
                  完成 <el-icon><Check /></el-icon>
                </el-button>
              </div>
              <!-- 总结信息弹窗 -->
              <el-dialog
                v-model="showSummary"
                title="总结信息"
                width="600px"
                :close-on-click-modal="false"
              >
                <el-alert
                  title="请将错误报告压缩包和下方总结信息一起发送到群内，方便管理员排查问题。"
                  type="warning"
                  show-icon
                  :closable="false"
                  style="margin-bottom: 16px;"
                />
                <el-input
                  v-model="summaryText"
                  type="textarea"
                  :rows="8"
                  readonly
                />
                <template #footer>
                  <el-button type="primary" @click="copySummary">
                    <el-icon><CopyDocument /></el-icon>
                    一键复制
                  </el-button>
                  <el-button @click="showSummary = false">关闭</el-button>
                </template>
              </el-dialog>
            </el-card>
          </div>    
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
        <template v-for="(block, blockIndex) in disconnectBlocks" :key="blockIndex">
          <div v-if="block.type === 'text'" class="json-block-text markdown-body" v-html="block.content"></div>
          <el-alert
            v-else-if="block.type === 'alert'"
            :title="block.content"
            :type="block.level || 'info'"
            show-icon
            :closable="false"
            class="json-block-alert"
          />
          <div v-else-if="block.type === 'steps'" class="json-block-steps">
            <el-steps direction="vertical" :active="block.items.length">
              <el-step
                v-for="(item, i) in block.items"
                :key="i"
                :title="typeof item === 'string' ? item : item.title"
              >
                <template v-if="typeof item !== 'string'" #description>
                  <div v-if="item.image" class="json-step-image">
                    <img :src="item.image.src" :alt="item.image.alt || ''"
                      style="max-width: 100%; border-radius: 6px; border: 1px solid var(--el-border-color); margin-top: 8px;" />
                    <p v-if="item.image.caption" class="json-image-caption">{{ item.image.caption }}</p>
                  </div>
                  <div v-if="item.code" class="json-block-code json-step-inner-block">
                    <span v-if="item.code.lang" class="json-code-lang">{{ item.code.lang }}</span>
                    <button class="json-code-copy-btn" @click="copyCode(item.code.content, `dstep-${i}`)"
                      :class="{ copied: copiedCodeIndex === `dstep-${i}` }">
                      {{ copiedCodeIndex === `dstep-${i}` ? '已复制' : '复制' }}
                    </button>
                    <div class="json-code-content">
                      <pre><code>{{ item.code.content }}</code></pre>
                    </div>
                  </div>
                  <div v-if="item.description" class="json-step-desc" v-html="linkify(item.description)"></div>
                  <div v-if="item.url" class="json-step-link">
                    <a :href="item.url" target="_blank" rel="noopener noreferrer">{{ item.urlText || item.url }}</a>
                  </div>
                </template>
              </el-step>
            </el-steps>
          </div>
          <div v-else-if="block.type === 'image'" class="json-block-image">
            <img :src="block.src" :alt="block.alt || ''" :style="{ maxWidth: block.maxWidth || '100%' }" />
            <p v-if="block.caption" class="json-image-caption">{{ block.caption }}</p>
          </div>
        </template>
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
import { ElMessage } from 'element-plus';
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
const jsonBlocks = ref<any[]>([]);
const inputValues = ref<Record<string, string>>({});
const inputLabels = ref<Record<string, string>>({});
const showSummary = ref<boolean>(false);
const summaryText = ref<string>('');
const isLoading = ref<boolean>(false);
const documentSteps = ref<any[]>([]);
const activeDocStep = ref<number>(0);
const copiedCodeIndex = ref<string | number | null>(null);

// 弹窗相关状态
const showDisconnectDialog = ref<boolean>(false);
const disconnectBlocks = ref<any[]>([]);
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

const selectedLauncherId = ref<string>("");
const launchersAll = [
  { id: 'pcl2', name: 'PCL2/PCLCE启动器', icon: 'Monitor', color: '#2444fd', description: '使用 PCL2 或 PCLCE 启动器' },
  { id: 'hmcl', name: 'HMCL启动器', icon: 'Monitor', color: '#bb633a', description: '使用 HMCL 启动器' },
  { id: 'fcl', name: 'FCL启动器', icon: 'Monitor', color: '#9b0093', description: '使用 FCL 启动器（手机端）' },
  { id: 'server', name: '服务端', icon: 'Monitor', color: '#707070', description: '使用服务端核心启动的' }
]
const launchersNoFCL = [
  { id: 'pcl2', name: 'PCL2/PCLCE启动器', icon: 'Monitor', color: '#2444fd', description: '使用 PCL2 或 PCLCE 启动器' },
  { id: 'hmcl', name: 'HMCL启动器', icon: 'Monitor', color: '#bb633a', description: '使用 HMCL 启动器' },
]
const currentLaunchers = computed(() => {
  if (selectedCategoryId.value === 'disconnect') {
    if (selectedSubCategoryId.value === 'disconnect_decoder') {
      return launchersAll.filter(l => l.id !== 'server');
    }
    if (selectedSubCategoryId.value === 'disconnect_encoder') {
      const serverLauncher = launchersAll.find(l => l.id === 'server');
      return serverLauncher
        ? [serverLauncher, ...launchersAll.filter(l => l.id !== 'server')]
        : launchersAll;
    }
    return launchersAll;
  }
  return launchersNoFCL;
})

// 计算文档进度条宽度
const progressWidth = computed(() => {
  if (documentSteps.value.length <= 1) return '100%';
  return `${(activeDocStep.value / (documentSteps.value.length - 1)) * 100}%`;
});

// 选择主分类
const selectCategory = (categoryId: string) => {
  if (window.getSelection()?.toString()) return;
  selectedCategoryId.value = categoryId;
  selectedSubCategoryId.value = "";

  // 当选择连接失败类时，如果没读过则显示弹窗
  if (categoryId === "disconnect" && !hasReadDisconnectInfo.value) {
    loadDisconnectInfoDialog(true);
  }
}

// 选择子分类
const selectSubCategory = (subCategoryId: string) => {
  if (window.getSelection()?.toString()) return;
  selectedSubCategoryId.value = subCategoryId;
  selectedLauncherId.value = "";
}

const selectLauncher = (launcherId: string) => {
  if (window.getSelection()?.toString()) return;
  selectedLauncherId.value = launcherId;
  showSummary.value = false;
  summaryText.value = '';
  inputValues.value = {};
  inputLabels.value = {};
  const currentSubCategory = getCurrentSubCategory() as any;
  if (currentSubCategory?.launcherDocs) {
    const launcherDoc = currentSubCategory.launcherDocs[launcherId as 'pcl2' | 'hmcl' | 'fcl'];
    if (launcherDoc) {
      documentSteps.value = launcherDoc.docSteps;
    }
  }
  activeDocStep.value = 0;
  loadMarkdownDocument();
}

// 加载Markdown文档
const loadMarkdownDocument = async () => {
  if (!selectedSubCategoryId.value) return;
  if (selectedCategoryId.value === 'crash' && !selectedLauncherId.value) return;

  isLoading.value = true;
  markdownHtml.value = "";
  jsonBlocks.value = [];

  try {
    const currentSubCategory = getCurrentSubCategory() as any;
    if (!currentSubCategory) {
      markdownHtml.value = "<h1>文档不存在</h1>";
      return;
    }

    const currentStep = documentSteps.value[activeDocStep.value];

    // json 模式：步骤上有 jsonPath
    if (currentStep?.jsonPath) {
      const response = await axios.get(currentStep.jsonPath);
      jsonBlocks.value = response.data.blocks || [];
      inputValues.value = {};
        response.data.blocks?.forEach((block: any) => {
          if (block.type === 'input' && block.key && block.label) {
            inputLabels.value[block.key] = block.label;
          }
        });
      return;
    }

    // md 模式：通过 docPath + docSuffix 拼接
    let docPath: string;
    if (selectedCategoryId.value === 'crash' && currentSubCategory.launcherDocs) {
      const launcherDoc = currentSubCategory.launcherDocs[selectedLauncherId.value as 'pcl2' | 'hmcl' | 'fcl'];
      if (!launcherDoc?.docPath) {
        markdownHtml.value = "<h1>文档不存在</h1>";
        return;
      }
      const docSuffix = currentStep ? currentStep.docSuffix : "_desc";
      const basePath = launcherDoc.docPath.replace(/\.md$/, '');
      docPath = `${basePath}${docSuffix}.md`;
    } else {
      if (!currentSubCategory.docPath) {
        markdownHtml.value = "<h1>文档不存在</h1>";
        return;
      }
      const docSuffix = currentStep ? currentStep.docSuffix : "_desc";
      const basePath = currentSubCategory.docPath.replace(/\.md$/, '');
      docPath = `${basePath}${docSuffix}.md`;
    }

    const response = await axios.get(docPath);
    markdownHtml.value = marked(response.data) as string;
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

// 完成并生成总结
const handleComplete = () => {
  const lines: string[] = [];
  lines.push(`问题类型：${getSelectedCategoryName()} - ${getSelectedSubCategoryName()}`);
  if (selectedLauncherId.value) {
    lines.push(`启动器：${getSelectedLauncherName()}`);
  }

  const hasInputs = Object.keys(inputValues.value).some(k => inputValues.value[k]);
  if (hasInputs) {
    for (const key of Object.keys(inputValues.value)) {
      const value = inputValues.value[key];
      if (value) {
        const label = inputLabels.value[key] || key;
        lines.push(`${label}：\n${value}`);
      }
    }
  }

  summaryText.value = lines.join('\n');
  showSummary.value = true;
}

// 复制总结到剪贴板
const copySummary = async () => {
  try {
    await navigator.clipboard.writeText(summaryText.value);
    ElMessage.success('已复制到剪贴板');
  } catch (error) {
    ElMessage.error('复制失败，请手动复制');
  }
}

const copyCode = async (content: string, index: string | number) => {
  try {
    await navigator.clipboard.writeText(content);
    copiedCodeIndex.value = index;
    setTimeout(() => { copiedCodeIndex.value = null; }, 2000);
  } catch {

  }
};

// 将文本中的 URL 转为可点击的 HTML 链接（简单处理，保留换行）
const linkify = (text?: string): string => {
  if (!text) return '';
  const escaped = String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return escaped.replace(urlRegex, (url) => {
    return `<a href="${url}" target="_blank" rel="noopener noreferrer">${url}</a>`;
  }).replace(/\n/g, '<br/>');
};

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
  selectedLauncherId.value = "";
  showSummary.value = false;
  summaryText.value = '';
  inputValues.value = {};
  inputLabels.value = {};
}

const backFromDocument = () => {  
  showSummary.value = false;
  summaryText.value = '';
  inputValues.value = {};
  inputLabels.value = {};
  if (selectedCategoryId.value === 'crash') {
    selectedLauncherId.value = "";
  } else {
    selectedSubCategoryId.value = "";
  }
}

const getSelectedLauncherName = () => {
  return launchersAll.find(l => l.id === selectedLauncherId.value)?.name || "";
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

const backToLauncherSelection = () => {
  selectedLauncherId.value = "";
  showSummary.value = false;
  summaryText.value = '';
  inputValues.value = {};
  inputLabels.value = {};
}

// 显示连接失败信息弹窗
const showDisconnectInfoManually = () => {
  loadDisconnectInfoDialog(false);
};

// 加载连接失败信息
const loadDisconnectInfoDialog = async (needCountdown: boolean = true) => {
  showDisconnectDialog.value = true;
  isDialogLoading.value = true;
  countdownValue.value = needCountdown ? 6 : 0;

  try {
    const response = await axios.get('/docs/disconnect_tip.json');
    disconnectBlocks.value = response.data.blocks || [];
    startCountdown();
  } catch (error) {
    console.error('加载连接失败信息失败:', error);
    disconnectBlocks.value = [{ type: 'alert', level: 'error', content: '加载失败，请稍后再试。' }];
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
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
}

.breadcrumb-section {
  padding-bottom: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.category-card {
  padding: var(--spacing-lg);
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 12px var(--shadow-color);
  transition: all var(--transition-speed);
  cursor: pointer;

  &:hover {
    border-color: var(--primary-color);
    box-shadow: 0 8px 20px var(--shadow-color);
  }
}

.category-icon {
  width: 60px;
  height: 60px;
  margin-bottom: var(--spacing-md);
  background-color: var(--primary-light-color);
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bg-color);
  font-size: var(--font-size-xl);
}

.json-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
  max-width: 100%;
}

.markdown-body .md-alert {
  padding: 8px 16px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  margin: 8px 0;
}

.markdown-body .md-alert-info {
  background-color: var(--el-color-info-light-9);
  border-left: 4px solid var(--el-color-info);
  color: var(--el-color-info);
}

.markdown-body .md-alert-warning {
  background-color: var(--el-color-warning-light-9);
  border-left: 4px solid var(--el-color-warning);
  color: var(--el-color-warning-dark-2);
}

.markdown-body .md-alert-success {
  background-color: var(--el-color-success-light-9);
  border-left: 4px solid var(--el-color-success);
  color: var(--el-color-success-dark-2);
}

.markdown-body .md-alert-error {
  background-color: var(--el-color-danger-light-9);
  border-left: 4px solid var(--el-color-danger);
  color: var(--el-color-danger-dark-2);
}

h3 {
  color: var(--text-color);
  margin-bottom: var(--spacing-sm);
}

p {
  color: var(--text-secondary-color);
}

.document-container {
  margin-top: var(--spacing-xl);
}

.document-card {
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 12px var(--shadow-color);
  overflow: hidden;
  backface-visibility: hidden;

  :deep(.el-card__body) {
    min-width: 0;
    overflow: hidden;
  }
}

.document-progress {
  padding: var(--spacing-md);
  background-color: var(--bg-secondary-color);
  border-bottom: 1px solid var(--border-color);
  position: relative;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 1;

  &.active {
    .step-number {
      background-color: var(--primary-color);
      color: white;
    }

    .step-title {
      color: var(--primary-color);
    }
  }

  &.completed .step-number {
    background-color: var(--primary-color);
  }
}

.step-number {
  width: 30px;
  height: 30px;
  border-radius: var(--border-radius-circle);
  background-color: var(--bg-secondary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-xs);
  color: var(--text-secondary-color);
  font-weight: 500;
}

.step-title {
  font-size: var(--font-size-sm);
  color: var(--text-secondary-color);
  white-space: nowrap;
}

.progress-bar {
  position: absolute;
  top: 15px;
  left: 15px;
  right: 15px;
  height: 2px;
  background-color: var(--border-light-color);
  z-index: 0;
}

.progress-fill {
  height: 100%;
  background-color: var(--primary-color);
  transition: width var(--transition-speed);
}

.document-content {
  padding: var(--spacing-xl);
  color: var(--text-color);
  line-height: 1.8;
  min-width: 0;
  max-width: 100%;
  overflow-x: hidden;
}

.document-navigation {
  padding: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  border-top: 1px solid var(--border-color);

  .el-button {
    padding: var(--spacing-md) var(--spacing-lg);
    background-color: var(--primary-color);
    color: white;
    border: none;
    border-radius: var(--border-radius-md);

    &:hover {
      background-color: var(--primary-hover-color);
    }
  }
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 7px;
  color: var(--text-color);
  min-width: 0;
  max-width: 100%;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
  will-change: transform, opacity;
}
.slide-fade-leave-active {
  transition: all 0.2s ease-in;
  will-change: transform, opacity;
}
.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(12px);
}
.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

.content-container {  
  overflow: hidden;  
}

.json-block-code {
  position: relative;
  min-width: 0;
  width: 100%;
  max-width: 100%;
  background-color: var(--bg-secondary-color);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.json-code-content {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  overflow-x: auto;
  overflow-y: hidden;
  -webkit-overflow-scrolling: touch;
}

.json-code-content pre {
  display: block;
  width: max-content;
  min-width: 100%;
  margin: 0;
  padding: 22px 20px 20px 16px;
  white-space: pre;
  box-sizing: border-box;
  word-break: normal;
}

.json-code-content code {
  display: block;
  white-space: pre;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.75;
  color: var(--text-color);
}

.json-block-steps {
  min-width: 0;
  max-width: 100%;

  :deep(.el-step__main) {
    min-width: 0;
    flex: 1;
  }

  :deep(.el-step__description) {
    min-width: 0;
    max-width: 100%;
    padding-top: 6px;
  }
}

.json-code-lang {
  position: absolute;
  top: 8px;
  left: 12px;
  font-size: 11px;
  color: var(--text-secondary-color);
  font-family: monospace;
  opacity: 0.6;
}

.json-code-copy-btn {
  position: absolute;
  top: 6px;
  right: 8px;
  padding: 3px 10px;
  font-size: 12px;
  background-color: rgba(128, 128, 128, 0.2);
  color: var(--text-secondary-color);
  border: 1px solid rgba(128, 128, 128, 0.25);
  border-radius: var(--border-radius-sm);
  cursor: pointer;
  backdrop-filter: blur(4px);
  opacity: 0;
  transition: opacity 0.2s, background-color 0.2s;
}

.json-block-code:hover .json-code-copy-btn {
  opacity: 1;
}

.json-code-copy-btn.copied {
  background-color: rgba(103, 194, 58, 0.25);
  color: var(--el-color-success);
  border-color: rgba(103, 194, 58, 0.3);
}

.json-step-desc {
  margin-top: 10px;
  font-size: 14px;
  line-height: 1.75;
  color: var(--text-color);
  white-space: pre-wrap;
}

.json-step-desc a,
.json-step-link a {
  font-size: 17px;
  color: var(--primary-color);
  text-decoration: underline;
  word-break: break-word;
}

.json-step-link {
  margin-top: 8px;
}

.json-step-inner-block {
  margin-top: 12px;
}

.json-block-steps .el-step {
  margin-bottom: 16px;
}

.json-block-steps .el-step:last-child {
  margin-bottom: 0;
}
</style>