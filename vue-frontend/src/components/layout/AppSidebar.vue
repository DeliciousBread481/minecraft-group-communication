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

<style scoped lang="scss">
.app-sidebar {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--bg-color);
  color: var(--text-color);
  transition: background-color 0.3s;

  .sidebar-menu {
    flex: 1;
    padding: 1rem 0;

    .menu-item {
      display: flex;
      align-items: center;
      padding: 1rem 1.5rem;
      cursor: pointer;
      transition: all 0.3s;
      margin: 0.25rem 1rem;
      border-radius: 8px;

      &:hover {
        background-color: var(--hover-color);
      }

      &.active {
        background-color: var(--primary-color);
        color: white;

        .menu-icon {
          color: white;
        }
      }

      .menu-icon {
        width: 40px;
        height: 40px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--text-color);
        transition: color 0.3s;
      }

      .menu-title {
        font-size: 0.875rem;
        font-weight: 500;
        margin-left: 0.5rem;
        transition: color 0.3s;
      }
    }
  }

  .sidebar-footer {
    padding: 1rem;
    border-top: 1px solid var(--border-color);
    transition: border-color 0.3s;

    .feedback-btn {
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--text-color);
      padding: 0.5rem;
      transition: color 0.3s;

      &:hover {
        color: var(--primary-color);
      }

      span {
        margin-left: 0.25rem;
      }
    }
  }

  @media (max-width: 900px) {
    .menu-title, .feedback-btn span {
      display: none;
    }

    .menu-icon {
      margin: 0 auto;
    }

    .sidebar-footer {
      padding: 0.5rem 0.25rem;
    }
  }
}
</style>