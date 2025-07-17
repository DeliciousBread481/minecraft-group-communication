<template>
  <div class="home-view">
    <el-space
      direction="vertical"
      wrap :size="550"
      justify-content="center"
      alignment="center"
    >

      <div class="home-content-container">
        <div class="content">
          <el-card class="dashboard-card">
            <template #header>
              <div class="card-header">
                <span>门门都精通</span>
              </div>
            </template>
            <div class="card-content">
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-label">头发去无踪</div>
                  <div class="stat-value">10086</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">我的才华</div>
                  <div class="stat-value">12306</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">复制</div>
                  <div class="stat-value">111111</div>
                </div>
                <div class="stat-item">
                  <div class="stat-label">粘贴</div>
                  <div class="stat-value">126748</div>
                </div>
              </div>

              <div class="chart-container">
                <h3>好饿</h3>
                <div class="chart-placeholder">好累</div>
              </div>

              <div class="data-table">
                <h3>好困</h3>
                <el-table :data="tableData" style="width: 100%">
                  <el-table-column prop="date" label="日期" width="180" />
                  <el-table-column prop="name" label="用户名" width="180" />
                  <el-table-column prop="action" label="操作" />
                </el-table>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </el-space>
  </div>
</template>

<script setup lang="ts">

const tableData = [
  { date: '2023-06-01', name: 'John', action: '你好' },
  { date: '2023-06-02', name: 'Alice', action: '再见' },
  { date: '2023-06-03', name: 'Bob', action: '啊啊啊啊' },
]
</script>

<style scoped>
.home-view {
  width: 100%;
  padding: 20px;
  display: flex;
  justify-content: center;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
  margin-top: 60px;
  width: 100%;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.main-text {
  font-size: 72px;
  font-weight: bold;
  margin-top: 40px;
  color: var(--text-color);
  transition: color var(--transition-speed);
}

.card-text {
  font-size: 30px;
  font-weight: bold;
  color: white;
}

.action-card {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.action-card:hover {
  transform: translateY(-5px);
}

.home-content-container {
  flex: 1;
  padding: 20px;
  width: 100%;
  overflow-y: auto;
  background-color: var(--bg-color);
  transition: background-color var(--transition-speed);
}

.content {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-card {
  margin-top: 20px;
  background-color: var(--bg-color);
  transition: background-color var(--transition-speed);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-color);
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
  transition: background-color var(--transition-speed),
  border-color var(--transition-speed);
}

.card-content {
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.stat-item {
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  padding: 15px;
  text-align: center;
  transition: background-color var(--transition-speed),
  border-color var(--transition-speed);
}

.stat-label {
  font-size: 14px;
  color: var(--text-color);
  margin-bottom: 8px;
  transition: color var(--transition-speed);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--text-color);
  transition: color var(--transition-speed);
}

.chart-container {
  margin-top: 30px;
}

.chart-placeholder {
  height: 300px;
  background-color: var(--bg-color);
  border: 1px dashed var(--border-color);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  margin-top: 10px;
  transition: background-color var(--transition-speed),
  border-color var(--transition-speed),
  color var(--transition-speed);
}

.data-table {
  margin-top: 30px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-text {
    font-size: 60px;
  }
}

@media (max-width: 768px) {
  .main-text {
    font-size: 48px;
    margin-top: 20px;
  }

  .card-text {
    font-size: 24px;
  }

  .action-card {
    width: 100% !important;
    max-width: 400px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .main-text {
    font-size: 36px;
  }

  .card-text {
    font-size: 20px;
  }
}
</style>
