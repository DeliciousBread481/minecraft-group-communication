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
@use '@/styles/components' as *;
</style>