<template>
  <div class="layout-container">
    <AppHeader @show-login="showLoginDialog" />
    <div class="main-container">
      <el-scrollbar class="scrollbar">
        <el-space
          direction="vertical"
          wrap :size="550"
          justify-content="center"
        >
          <el-space
            direction="vertical"
            wrap :size="80"
            justify-content="center"
          >
            <el-text class="main-text">
              选择你要了解的内容
            </el-text>
            <el-card
              style="width: 480px; background-color: #409eff"
              shadow="hover"
              @click="$router.push('/notice')"
            >
              <el-text class="card-text">
                群公告文档
              </el-text>
            </el-card>
            <el-card
              style="width: 480px; background-color: #409eff"
              shadow="hover"
              @click="$router.push('/notice')"
            >
              <el-text class="card-text">
                解决方案
              </el-text>
            </el-card>
          </el-space>

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
      </el-scrollbar>
    </div>

    <LoginDialog
      v-model:visible="loginDialogVisible"
      @register="showRegisterDialog"
    />
    <RegisterDialog v-model:visible="registerDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AppHeader from '@/components/layout/AppHeader.vue'
//import Sidebar from '@/components/layout/AppSidebar.vue'
import LoginDialog from '@/components/auth/LoginDialog.vue'
import RegisterDialog from '@/components/auth/RegisterDialog.vue'

const loginDialogVisible = ref(false)
const registerDialogVisible = ref(false)
const tableData = [
  { date: '2023-06-01', name: 'John', action: '你好' },
  { date: '2023-06-02', name: 'Alice', action: '再见' },
  { date: '2023-06-03', name: 'Bob', action: '啊啊啊啊' },
]

const showLoginDialog = () => {
  loginDialogVisible.value = true
}

const showRegisterDialog = () => {
  registerDialogVisible.value = true
}
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.main-container {
  display: flex;
  flex: 1;
  overflow: hidden;
  margin-top: 60px;
  width: 100%;
  top: 6%;
  margin-left: auto;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */
  text-align: center; /* 文本居中 */
}

.main-text {
  font-size: 72px;
  font-weight: bold;
  margin-top: 40%;
}

.card-text {
  font-size: 30px;
  font-weight: bold;
  color: white;
}

.scrollbar {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 94%;
  width: 100%;
  margin: 10px;
  text-align: center;
  border-radius: 4px;
  color: var(--el-color-primary);
}

.home-content-container {
  flex: 1;
  padding: 20px;
  width: 100%;
  overflow-y: auto;
  background-color: var(--bg-color);
}

.content-container {
  flex: 1;
  padding: 20px;
  width: 100%;
  top: 6%;
  overflow-y: auto;
  background-color: var(--bg-color);
}

.content {
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-card {
  margin-top: 20px;
  background-color: var(--card-bg-color);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--card-header-bg-color);
  padding: 15px 20px;
  border-bottom: 1px solid var(--border-color);
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
  background-color: var(--card-bg-color);
  border: 1px solid var(--border-color);
  border-radius: 4px;
  padding: 15px;
  text-align: center;
}

.stat-label {
  font-size: 14px;
  color: var(--text-color);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--sidebar-active-text-color);
}

.chart-container {
  margin-top: 30px;
}

.chart-placeholder {
  height: 300px;
  background-color: var(--chart-bg-color);
  border: 1px dashed var(--border-color);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  margin-top: 10px;
}

.data-table {
  margin-top: 30px;
}
</style>
