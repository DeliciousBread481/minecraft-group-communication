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

    <div class="solution-section">
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
          <div class="solution-badge" :style="{ backgroundColor: getCategoryColor(solution.category) }">
            {{ solution.category }}
          </div>
          <h3>{{ solution.title }}</h3>
          <p>{{ truncateDescription(solution.description) }}</p>
          <div class="solution-meta">
            <el-tag size="small" :type="getDifficultyType(solution.difficulty)">
              {{ solution.difficulty }}
            </el-tag>
            <el-tag size="small">{{ solution.version }}</el-tag>
            <span class="update-time">{{ solution.updateTime }}</span>
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
      <div v-if="selectedSolution" class="solution-detail">
        <div class="solution-meta">
          <el-tag type="info">{{ selectedSolution.category }}</el-tag>
          <el-tag :type="getDifficultyType(selectedSolution.difficulty)">
            {{ selectedSolution.difficulty }}
          </el-tag>
          <el-tag>{{ selectedSolution.version }}</el-tag>
          <span class="update-time">最后更新: {{ selectedSolution.updateTime }}</span>
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
  Cpu,
  VideoCamera,
  Files,
  MagicStick,
  Crop,
  FolderOpened,
  Document
} from '@element-plus/icons-vue'

// 定义类型
interface Category {
  id: string;
  name: string;
  icon: Component;
  description: string;
  color: string;
}

interface Solution {
  id: string;
  title: string;
  category: string;
  difficulty: string;
  version: string;
  updateTime: string;
  description: string;
  steps: string[];
  notes?: string;
  images?: string[];
}

// 问题分类数据
const categories = ref<Category[]>([
  {
    id: 'all',
    name: '全部问题',
    icon: MagicStick,
    description: '浏览所有类别的解决方案',
    color: '#9c27b0'
  },
  {
    id: 'startup',
    name: '启动问题',
    icon: Setting,
    description: '游戏启动失败、崩溃等问题',
    color: '#409EFF'
  },
  {
    id: 'mod',
    name: '模组问题',
    icon: Warning,
    description: '模组加载、兼容性问题',
    color: '#E6A23C'
  },
  {
    id: 'network',
    name: '联机问题',
    icon: Connection,
    description: '服务器连接、联机问题',
    color: '#67C23A'
  },
  {
    id: 'performance',
    name: '性能问题',
    icon: Cpu,
    description: '卡顿、帧数低等问题',
    color: '#F56C6C'
  },
  {
    id: 'graphics',
    name: '画面问题',
    icon: VideoCamera,
    description: '画面显示异常、渲染问题',
    color: '#909399'
  },
  {
    id: 'resource',
    name: '资源问题',
    icon: Files,
    description: '材质包、数据包问题',
    color: '#9c27b0'
  }
])

// 解决方案数据
const solutions = ref<Solution[]>([
  {
    id: 's1',
    title: '游戏启动崩溃：Exit Code 1',
    category: '启动问题',
    difficulty: '中等',
    version: '1.16+',
    updateTime: '2023-10-18',
    description: '在启动Minecraft时，游戏崩溃并显示Exit Code 1错误。这通常是由于Java版本不兼容、显卡驱动问题或内存分配不足引起的。',
    steps: [
      '检查Java版本是否与Minecraft版本兼容（1.17+需要Java 16+）',
      '删除或重命名.minecraft文件夹中的options.txt文件',
      '更新显卡驱动程序到最新版本',
      '在启动器中分配更多内存（建议4GB以上）',
      '尝试使用不同的Java版本（如OpenJDK）',
      '检查mods文件夹是否有损坏或不兼容的模组'
    ],
    notes: '如果使用模组，请检查模组兼容性，确保所有模组都适用于当前Minecraft版本',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Exit+Code+1+Error',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Java+Version+Settings'
    ]
  },
  {
    id: 's2',
    title: '联机时出现"Connection Timed Out"错误',
    category: '联机问题',
    difficulty: '简单',
    version: '全版本',
    updateTime: '2023-10-15',
    description: '尝试加入服务器时出现连接超时错误，这可能是由于网络问题、服务器设置错误或防火墙阻止引起的。',
    steps: [
      '检查服务器IP地址和端口是否正确',
      '确认服务器是否在线运行',
      '检查本地网络连接是否正常',
      '暂时禁用防火墙和杀毒软件测试',
      '尝试使用VPN连接服务器',
      '如果是自建服务器，检查端口转发设置是否正确'
    ],
    notes: '如果使用路由器，请确保端口转发规则正确设置，并且服务器使用静态IP',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Connection+Error',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Port+Forwarding'
    ]
  },
  {
    id: 's3',
    title: '游戏帧数低、卡顿',
    category: '性能问题',
    difficulty: '中等',
    version: '1.18+',
    updateTime: '2023-10-10',
    description: '游戏运行时帧数低，出现卡顿现象，尤其是在加载新区块或使用光影时。',
    steps: [
      '在视频设置中降低渲染距离（建议8-12区块）',
      '关闭精美图像和云层选项',
      '安装性能优化模组（如OptiFine、Sodium或Lithium）',
      '更新显卡驱动程序到最新版本',
      '在启动器中分配更多内存（但不要超过系统可用内存的70%）',
      '关闭后台运行的其他程序释放系统资源',
      '使用低分辨率资源包'
    ],
    notes: '对于1.18+版本，建议至少分配4GB内存，使用SSD硬盘可显著提升区块加载速度',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Performance+Settings',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Memory+Allocation'
    ]
  },
  {
    id: 's4',
    title: '模组加载后游戏崩溃',
    category: '模组问题',
    difficulty: '简单',
    version: '全版本',
    updateTime: '2023-10-05',
    description: '安装模组后游戏无法启动或启动后崩溃。',
    steps: [
      '检查模组是否与当前Minecraft版本兼容',
      '确认模组加载器（Forge/Fabric）版本正确',
      '检查模组依赖是否满足',
      '逐个添加模组以找出冲突模组',
      '查看游戏日志文件定位具体错误'
    ],
    notes: '使用模组管理器如CurseForge可以避免版本冲突',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Mod+Compatibility',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Mod+Loader'
    ]
  },
  {
    id: 's5',
    title: '游戏画面闪烁或出现异常',
    category: '画面问题',
    difficulty: '中等',
    version: '1.19+',
    updateTime: '2023-09-28',
    description: '游戏画面出现闪烁、纹理错误或显示异常。',
    steps: [
      '更新显卡驱动程序到最新版本',
      '在视频设置中关闭抗锯齿和各向异性过滤',
      '尝试不同的图形模式（如从Fancy切换到Fast）',
      '重置视频设置到默认值',
      '删除或更换当前使用的资源包',
      '检查是否安装了不兼容的光影或模组'
    ],
    notes: '如果使用光影包，请尝试更换为其他光影或更新光影包版本',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Graphics+Error',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Video+Settings'
    ]
  },
  {
    id: 's6',
    title: '材质包无法加载',
    category: '资源问题',
    difficulty: '简单',
    version: '全版本',
    updateTime: '2023-09-20',
    description: '材质包无法正确加载，游戏内显示默认材质。',
    steps: [
      '检查材质包是否与当前Minecraft版本兼容',
      '确认材质包文件格式正确（应为.zip文件）',
      '将材质包放入resourcepacks文件夹',
      '在游戏内资源包选项中启用该材质包',
      '检查材质包是否依赖其他资源',
      '尝试使用其他材质包测试'
    ],
    notes: '高分辨率材质包可能需要安装OptiFine才能正确加载',
    images: [
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Resource+Pack',
      'https://via.placeholder.com/600x300/4a86e8/ffffff?text=Texture+Settings'
    ]
  }
])

const searchQuery = ref('')
const activeCategory = ref('all')
const solutionDialogVisible = ref(false)
const selectedSolution = ref<Solution | null>(null)

// 计算属性
const activeCategoryName = computed(() => {
  const category = categories.value.find(c => c.id === activeCategory.value)
  return category ? category.name : '全部问题'
})

const filteredSolutions = computed(() => {
  let result = [...solutions.value]

  // 分类过滤
  if (activeCategory.value !== 'all') {
    const categoryName = categories.value.find(c => c.id === activeCategory.value)?.name
    if (categoryName) {
      result = result.filter(sol => sol.category === categoryName)
    }
  }

  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(sol =>
      sol.title.toLowerCase().includes(query) ||
      sol.description.toLowerCase().includes(query) ||
      sol.steps.some(step => step.toLowerCase().includes(query)))
  }

  return result
})

// 方法
const selectCategory = (categoryId: string) => {
  activeCategory.value = categoryId
}

const openSolution = (solution: Solution) => {
  selectedSolution.value = { ...solution }
  solutionDialogVisible.value = true
}

const resetFilters = () => {
  activeCategory.value = 'all'
  searchQuery.value = ''
}

const truncateDescription = (text: string) => {
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

const getCategoryColor = (categoryName: string) => {
  const category = categories.value.find(c => c.name === categoryName)
  return category ? category.color : '#409EFF'
}

const getDifficultyType = (difficulty: string) => {
  switch(difficulty) {
    case '简单': return 'success'
    case '中等': return 'warning'
    case '困难': return 'danger'
    default: return 'info'
  }
}

onMounted(() => {
  // 初始化数据
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

.categories-grid {
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
  color: var(--primary-color);
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

.solutions-grid {
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
</style>