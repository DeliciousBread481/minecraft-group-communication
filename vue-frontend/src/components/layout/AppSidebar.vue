<template>
  <div class="app-sidebar">
    <div class="sidebar-menu">
      <div
        v-for="item in menuItems"
        :key="item.path"
        class="menu-item"
        :class="{ 'active': item.path === '/' ? $route.path === '/' : $route.path.startsWith(item.path) }"
        @click="navigateTo(item.path)"
      >
        <el-tooltip effect="dark" :content="item.title" placement="right">
          <div class="menu-icon">
            <el-icon :size="20">
              <component :is="item.icon" />
            </el-icon>
          </div>
        </el-tooltip>
        <span class="menu-title">{{ item.title }}</span>
      </div>
    </div>

    <div class="sidebar-footer">
      <el-button
        type="text"
        class="feedback-btn"
        @click="openFeedback"
      >
        <el-icon><ChatLineRound /></el-icon>
        <span>反馈建议</span>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  HomeFilled,
  Notebook,
  CollectionTag,
  QuestionFilled,
  ChatLineRound
} from '@element-plus/icons-vue'

const router = useRouter()

const menuItems = ref([
  { path: '/', title: '首页', icon: HomeFilled },
  { path: '/notice', title: '群公告文档', icon: Notebook },
  { path: '/solutions', title: '解决方案', icon: CollectionTag },
  { path: '/faq', title: '常见问题', icon: QuestionFilled },
])

const navigateTo = (path: string) => {
  router.push(path)
}

const openFeedback = () => {
  console.log('打开反馈表单')
}
</script>

<style scoped>
.app-sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color var(--transition-speed);
}

.sidebar-menu {
  flex: 1;
  padding: 15px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all var(--transition-speed);
  margin: 5px 10px;
  border-radius: 8px;
}

.menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-item.active {
  background-color: var(--primary-color);
  color: white;
}

.menu-item.active .menu-icon {
  color: white;
}

.menu-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  transition: color var(--transition-speed);
}

.menu-title {
  font-size: 14px;
  font-weight: 500;
  margin-left: 10px;
  transition: color var(--transition-speed);
}

.sidebar-footer {
  padding: 15px;
  border-top: 1px solid var(--border-color);
  transition: border-color var(--transition-speed);
}

.feedback-btn {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color);
  padding: 10px;
  transition: color var(--transition-speed);
}

.feedback-btn:hover {
  color: var(--text-color);
}

.feedback-btn span {
  margin-left: 8px;
}

@media (max-width: 768px) {
  .menu-title {
    display: none;
  }

  .feedback-btn span {
    display: none;
  }

  .menu-icon {
    margin: 0 auto;
  }

  .sidebar-footer {
    padding: 10px 5px;
  }
}
</style>
