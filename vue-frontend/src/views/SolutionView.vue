<template>
  <div class="solution-view">
    <div class="solution-header">
      <div class="minecraft-title">
        <div class="minecraft-icon">
          <el-icon><Crop /></el-icon>
        </div>
        <h1>Minecraft 解决方案库</h1>
      </div>
      <p>快速找到游戏问题的解决方法</p>

      <div class="search-container">
        <el-input
          v-model="searchQuery"
          placeholder="搜索问题关键词..."
          clearable
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>
    </div>

    <div class="category-section">
      <h2><el-icon><FolderOpened /></el-icon> 问题分类</h2>
      <div class="category-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          :class="{ 'active': activeCategory === category.id }"
          @click="selectCategory(category.id)"
        >
          <div class="category-icon" :style="{ backgroundColor: category.color }">
            <el-icon size="24"><component :is="category.icon" /></el-icon>
          </div>
          <h3>{{ category.name }}</h3>
          <p>{{ category.description }}</p>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="solution-section">
      <div class="solution-header">
        <div class="solution-header-title">
          <h2><el-icon><Document /></el-icon> {{ activeCategoryName }} 解决方案</h2>
        </div>
        <div class="result-count">{{ filteredSolutions.length }} 个解决方案</div>
      </div>

      <div class="solution-grid">
        <div
          v-for="solution in filteredSolutions"
          :key="solution.id"
          class="solution-card"
          @click="openSolution(solution)"
        >
          <div class="solution-badge" :style="{ backgroundColor: getCategoryColor(solution.categoryName) }">
            {{ solution.categoryName }}
          </div>
          <h3>{{ solution.title }}</h3>
          <p>{{ truncateDescription(solution.description) }}</p>
          <div class="solution-meta">
            <el-tag size="small" :type="getDifficultyType(solution.difficulty)">
              {{ solution.difficulty }}
            </el-tag>
            <el-tag size="small">{{ solution.version }}</el-tag>
            <span class="update-time">{{ formatDate(solution.updateTime) }}</span>
          </div>
        </div>
      </div>

      <div v-if="filteredSolutions.length === 0" class="no-results">
        <el-empty description="没有找到相关解决方案" />
        <el-button type="primary" @click="resetFilters">重置筛选条件</el-button>
      </div>
    </div>

    <el-dialog
      v-model="solutionDialogVisible"
      :title="selectedSolution?.title || '解决方案详情'"
      width="80%"
      top="5vh"
    >
      <div v-if="selectedSolution" v-loading="detailLoading" class="solution-detail">
        <div class="solution-meta">
          <el-tag type="info">{{ selectedSolution.categoryName }}</el-tag>
          <el-tag :type="getDifficultyType(selectedSolution.difficulty)">
            {{ selectedSolution.difficulty }}
          </el-tag>
          <el-tag>{{ selectedSolution.version }}</el-tag>
          <span class="update-time">最后更新: {{ formatDate(selectedSolution.updateTime) }}</span>
        </div>

        <h3>问题描述</h3>
        <p>{{ selectedSolution.description }}</p>

        <h3>解决方案</h3>
        <ol class="solution-steps">
          <li v-for="(step, index) in selectedSolution.steps" :key="index">
            {{ step }}
          </li>
        </ol>

        <div v-if="selectedSolution.notes" class="solution-notes">
          <h3>注意事项</h3>
          <p>{{ selectedSolution.notes }}</p>
        </div>

        <div v-if="selectedSolution.images" class="solution-images">
          <el-image
            v-for="(img, idx) in selectedSolution.images"
            :key="idx"
            :src="img"
            :preview-src-list="selectedSolution.images"
            fit="cover"
            class="solution-image"
          />
        </div>
      </div>

      <template #footer>
        <el-button @click="solutionDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, type Component } from 'vue'
import {
  Search,
  Setting,
  Warning,
  Connection,
  MagicStick,
  Crop,
  FolderOpened,
  Document
} from '@element-plus/icons-vue'
import {
  getPublishedSolutionById,
  getPublishedSolutions,
  getSolutionCategories
} from '@/api/solution'
import type { CategoryDTO, SolutionDTO } from '@/types/api'

interface CategoryCard extends Omit<CategoryDTO, 'icon'> {
  icon: Component;
}

const ALL_CATEGORY_ID = 'all'
const PAGE_SIZE = 100
const categoryIcons: Record<string, Component> = {
  setting: Setting,
  warning: Warning,
  connection: Connection,
  magicstick: MagicStick
}
const categoryIcon = (icon: string): Component => categoryIcons[icon.toLowerCase()] ?? FolderOpened

const apiCategories = ref<CategoryDTO[]>([])
const solutions = ref<SolutionDTO[]>([])
const loading = ref(false)
const detailLoading = ref(false)

const searchQuery = ref('')
const activeCategory = ref(ALL_CATEGORY_ID)
const solutionDialogVisible = ref(false)
const selectedSolution = ref<SolutionDTO | null>(null)

const categories = computed<CategoryCard[]>(() => [
  {
    id: ALL_CATEGORY_ID,
    name: '全部问题',
    icon: MagicStick,
    description: '浏览所有类别的解决方案',
    color: '#9c27b0'
  },
  ...apiCategories.value.map((category) => ({
    ...category,
    icon: categoryIcon(category.icon)
  }))
])

const activeCategoryName = computed(() => {
  const category = categories.value.find(c => c.id === activeCategory.value)
  return category ? category.name : '全部问题'
})

const filteredSolutions = computed(() => {
  let result = [...solutions.value]

  // 分类过滤
  if (activeCategory.value !== ALL_CATEGORY_ID) {
    result = result.filter((solution) => solution.categoryId === activeCategory.value)
  }

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter((solution) =>
      solution.title.toLowerCase().includes(query) ||
      solution.description.toLowerCase().includes(query) ||
      solution.steps.some((step) => step.toLowerCase().includes(query)))
  }

  return result
})

// 方法
const selectCategory = (categoryId: string) => {
  activeCategory.value = categoryId
}

const openSolution = async (solution: SolutionDTO) => {
  selectedSolution.value = solution
  solutionDialogVisible.value = true
  detailLoading.value = true
  try {
    selectedSolution.value = await getPublishedSolutionById(solution.id)
  } finally {
    detailLoading.value = false
  }
}

const resetFilters = () => {
  activeCategory.value = ALL_CATEGORY_ID
  searchQuery.value = ''
}

const truncateDescription = (text: string) => {
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

const getCategoryColor = (categoryName: string | null | undefined) => {
  const category = categories.value.find((item) => item.name === categoryName)
  return category ? category.color : '#409EFF'
}

const getDifficultyType = (difficulty: string) => {
  switch (difficulty) {
    case '简单': return 'success'
    case '中等': return 'warning'
    case '困难': return 'danger'
    default: return 'info'
  }
}

const formatDate = (dateTime: string) => new Date(dateTime).toLocaleDateString('zh-CN')

const loadSolutions = async () => {
  loading.value = true
  try {
    const page = await getPublishedSolutions(0, PAGE_SIZE)
    solutions.value = page.content
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  apiCategories.value = await getSolutionCategories()
}

onMounted(async () => {
  await Promise.all([loadSolutions(), loadCategories()])
})
</script>

<style scoped lang="scss">
.solution-view {
  background-color: var(--bg-color);
  padding: var(--spacing-lg);
}

.solution-header {
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  background: linear-gradient(135deg, var(--bg-secondary-color) 0%, var(--border-light-color) 100%);
  border: 1px solid var(--border-color);
}

.dark .solution-header {
  background: linear-gradient(135deg, #1a2b3c 0%, #2c3e50 100%);
}

.minecraft-title {
  text-align: center;
  margin-bottom: var(--spacing-xl);

  h1 {
    font-size: var(--font-size-xxl);
    color: var(--heading-color);
    margin-bottom: var(--spacing-sm);
  }

  p {
    font-size: var(--font-size-lg);
    color: var(--text-secondary-color);
    max-width: 800px;
    margin: 0 auto;
  }
}

.search-container {
  max-width: 600px;
  margin: 0 auto;
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
  text-align: center;

  &:hover {
    border-color: var(--primary-color);
    box-shadow: 0 8px 20px var(--shadow-color);
  }
}

.category-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto var(--spacing-md);
  background-color: var(--primary-light-color);
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--bg-color);
  font-size: var(--font-size-xxl);
}

h3 {
  color: var(--text-color);
  margin-bottom: var(--spacing-sm);
}

p {
  color: var(--text-secondary-color);
}

.solution-section {
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 12px var(--shadow-color);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
}

.solution-header-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);

  h2 {
    color: var(--heading-color);
  }
}

.result-count {
  color: var(--text-secondary-color);
}

.solution-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.solution-card {
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

.solution-badge {
  display: inline-block;
  padding: var(--spacing-xs) var(--spacing-sm);
  background-color: var(--primary-color);
  color: white;
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-xs);
  font-weight: 500;
  margin-bottom: var(--spacing-sm);
}

.solution-title {
  font-size: var(--font-size-lg);
  color: var(--text-color);
  margin-bottom: var(--spacing-sm);
}

.solution-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);

  .el-tag {
    background-color: var(--bg-secondary-color);
    color: var(--text-color);
    border: 1px solid var(--border-color);
  }
}

.update-time {
  color: var(--text-secondary-color);
  font-size: var(--font-size-sm);
}

.solution-detail {
  padding: var(--spacing-xl);
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 12px var(--shadow-color);
  color: var(--text-color);
  line-height: 1.8;
}

.solution-steps {
  margin: var(--spacing-lg) 0;

  li {
    margin-bottom: var(--spacing-md);
    color: var(--text-color);
  }
}

.solution-notes {
  padding: var(--spacing-md);
  background-color: var(--primary-light-color);
  border-left: 4px solid var(--primary-color);
  border-radius: var(--border-radius-sm);
  margin: var(--spacing-lg) 0;
}

.el-button{
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 12px var(--shadow-color);
  transition: all var(--transition-speed);

  &:hover {
    border-color: var(--primary-hover-color);
    box-shadow: 0 8px 20px var(--shadow-color);
  }
}
</style>