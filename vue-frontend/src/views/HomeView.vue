<template>
  <div class="home-view">
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">Minecraft 疑难杂症交流群</h1>
        <p class="hero-subtitle">不知道该写啥</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/solutions')">
            查看解决方案
          </el-button>
          <el-button size="large" @click="$router.push('/notice')">
            查看群公告
          </el-button>
        </div>
      </div>
    </div>

    <div class="features-section">
      <el-row :gutter="30">
        <el-col :xs="24" :sm="12" :md="8" v-for="(feature, index) in features" :key="index">
          <el-card class="feature-card">
            <div class="feature-icon">
              <el-icon :size="40">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="stats-section">
      <el-card>
        <template #header>
          <div class="section-header">
            <h2>社区统计</h2>
            <p>了解我们的成长与贡献</p>
          </div>
        </template>

        <div class="stats-grid">
          <div class="stat-item" v-for="(stat, index) in stats" :key="index">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </div>

        <div class="chart-container">
          <h3>问题解决趋势</h3>
          <div class="chart-placeholder">
            <el-icon :size="60"><DataAnalysis /></el-icon>
            <p>数据可视化图表</p>
          </div>
        </div>
      </el-card>
    </div>

    <div class="recent-section">
      <el-card>
        <template #header>
          <div class="section-header">
            <h2>最近解决的问题</h2>
            <el-link type="primary" :underline="false" @click="$router.push('/solutions')">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </template>

        <el-table :data="tableData" style="width: 100%" height="300">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="problem" label="问题描述" />
          <el-table-column prop="solution" label="解决方案" />
          <el-table-column prop="contributor" label="贡献者" width="120" />
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  QuestionFilled,
  Document,
  Collection,
  ArrowRight,
  DataAnalysis
} from '@element-plus/icons-vue'

const features = [
  {
    icon: Document,
    title: "群公告文档",
    description: "最新群规、重要通知和活动信息，及时掌握社区动态"
  },
  {
    icon: Collection,
    title: "解决方案库",
    description: "收集整理了数百个常见问题的解决方案，分类清晰便于查找"
  },
  {
    icon: QuestionFilled,
    title: "常见问题",
    description: "新手常见问题快速解答，助你快速入门Minecraft"
  }
]

const stats = [
  { value: "---", label: "已解决问题" },
  { value: "1,546", label: "社区成员" },
  { value: "1000%", label: "解决率" },
  { value: "36小时", label: "平均解决时间" }
]

const tableData = [
  { date: '2023-06-01', problem: '游戏崩溃：退出时Java进程未结束', solution: '更新Java版本至17.0.6', contributor: '张三' },
  { date: '2023-06-02', problem: '材质包加载异常导致方块透明', solution: '禁用高清修复光影兼容模式', contributor: '李四' },
  { date: '2023-06-03', problem: '服务器TPS过低，卡顿严重', solution: '优化实体生成逻辑和红石电路', contributor: '王五' },
  { date: '2023-06-04', problem: '模组冲突导致物品栏异常', solution: '更新Inventory Tweaks模组', contributor: '赵六' },
  { date: '2023-06-05', problem: '生物AI异常，不会移动攻击', solution: '重置生物AI行为配置文件', contributor: '小明' }
]
</script>

<style scoped lang="scss">
.home-view {
  width: 100%;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.hero-banner {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-hover-color) 100%);
  border-radius: 16px;
  padding: 60px 40px;
  margin-bottom: 40px;
  text-align: center;
  color: white;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);

  .hero-title {
    font-size: 2.5rem;
    font-weight: 700;
    margin-bottom: 20px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  }

  .hero-subtitle {
    font-size: 1.25rem;
    max-width: 700px;
    margin: 0 auto 30px;
    opacity: 0.9;
  }

  .hero-actions {
    display: flex;
    justify-content: center;
    gap: 20px;

    .el-button {
      min-width: 160px;
      height: 48px;
      font-size: 1.1rem;
      font-weight: 500;
      border-radius: 12px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
      }
    }
  }
}

.features-section {
  margin-bottom: 40px;

  .feature-card {
    height: 100%;
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
    border: none;
    background-color: var(--bg-secondary-color);
    box-shadow: 0 4px 12px var(--shadow-color);

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 20px var(--shadow-color);
    }

    .feature-icon {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 80px;
      height: 80px;
      margin: 0 auto 20px;
      border-radius: 50%;
      background-color: rgba(var(--primary-color-rgb), 0.1);
      color: var(--primary-color);
    }

    .feature-title {
      font-size: 1.3rem;
      text-align: center;
      margin-bottom: 12px;
      color: var(--text-color);
    }

    .feature-description {
      color: var(--text-secondary-color);
      text-align: center;
      font-size: 0.95rem;
      line-height: 1.6;
    }
  }
}

.stats-section,
.recent-section {
  margin-bottom: 40px;

  .el-card {
    border-radius: 16px;
    overflow: hidden;
    border: none;
    box-shadow: 0 4px 12px var(--shadow-color);
    background-color: var(--bg-color);
  }

  .section-header {
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid var(--border-color);

    h2 {
      font-size: 1.5rem;
      color: var(--text-color);
      margin: 0;
    }

    p {
      color: var(--text-secondary-color);
      margin: 5px 0 0;
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  padding: 30px;

  .stat-item {
    text-align: center;
    padding: 20px;
    background-color: var(--bg-secondary-color);
    border-radius: 12px;
    transition: all 0.3s ease;

    &:hover {
      background-color: var(--hover-color);
      transform: translateY(-3px);
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 700;
      color: var(--primary-color);
      margin-bottom: 8px;
    }

    .stat-label {
      font-size: 0.95rem;
      color: var(--text-secondary-color);
    }
  }
}

.chart-container {
  padding: 0 30px 30px;

  h3 {
    font-size: 1.2rem;
    color: var(--text-color);
    margin-bottom: 20px;
  }

  .chart-placeholder {
    height: 300px;
    background-color: var(--bg-secondary-color);
    border-radius: 12px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: var(--text-secondary-color);

    p {
      margin-top: 15px;
      font-size: 1.1rem;
    }
  }
}

.recent-section {
  .el-table {
    --el-table-border-color: var(--border-color);
    --el-table-header-bg-color: var(--bg-secondary-color);
    --el-table-tr-bg-color: var(--bg-color);
    --el-table-text-color: var(--text-color);
    --el-table-header-text-color: var(--text-color);
    --el-table-row-hover-bg-color: var(--hover-color);
    border-radius: 0 0 12px 12px;
  }
}

@media (max-width: 992px) {
  .hero-banner {
    padding: 50px 30px;

    .hero-title {
      font-size: 2rem;
    }

    .hero-subtitle {
      font-size: 1.1rem;
    }
  }

  .hero-actions .el-button {
    min-width: 140px;
    height: 44px;
    font-size: 1rem;
  }
}

@media (max-width: 768px) {
  .hero-banner {
    padding: 40px 20px;

    .hero-title {
      font-size: 1.7rem;
    }

    .hero-subtitle {
      font-size: 1rem;
      margin-bottom: 25px;
    }
  }

  .hero-actions {
    flex-direction: column;
    gap: 15px !important;
    align-items: center;

    .el-button {
      width: 100%;
      max-width: 300px;
    }
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    padding: 20px;
    gap: 15px;
  }

  .chart-placeholder {
    height: 250px !important;
  }
}

@media (max-width: 480px) {
  .hero-banner {
    padding: 30px 15px;
    border-radius: 12px;

    .hero-title {
      font-size: 1.5rem;
    }
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start !important;

    .el-link {
      margin-top: 10px;
      align-self: flex-end;
    }
  }
}
</style>