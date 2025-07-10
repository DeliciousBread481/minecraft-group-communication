<!-- components/layout/AppSidebar -->
<template>
  <!-- 应用侧边栏导航组件 -->
  <el-aside class="app-sidebar">
    <!-- 垂直导航菜单 -->
    <el-menu
      default-active="1"
      class="el-menu-vertical-demo"
      background-color="var(--sidebar-bg-color)"
      text-color="var(--sidebar-text-color)"
      :active-text-color="sidebarTextColor"
      router
    >
    <!-- 注意事项菜单项 -->
    <el-menu-item index="1">
      <el-icon><Document color="yellow" /></el-icon>
      <span style="color: var(--warning-text-color)">必读注意事项！</span>
    </el-menu-item>
    <el-menu-item index="2">
      <template #title>
        <el-icon><CircleCloseFilled /></el-icon>
        <span>游戏崩溃</span>
      </template>
    </el-menu-item>

    <!-- 游戏联机菜单项 -->
    <el-menu-item index="3">
      <el-icon><Switch /></el-icon>
      <span>游戏联机</span>
    </el-menu-item>

    <!-- 游戏无响应菜单项 -->
    <el-menu-item index="4">
      <el-icon><Loading /></el-icon>
      <span>游戏无响应</span>
    </el-menu-item>

    <!-- 游戏卡顿菜单项 -->
    <el-menu-item index="5">
      <el-icon><Lightning /></el-icon>
      <span>游戏卡顿</span>
    </el-menu-item>

    <!-- 启动器问题菜单项 -->
    <el-menu-item index="6">
      <el-icon><VideoPlay /></el-icon>
      <span>启动器问题</span>
    </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 导入Element Plus图标组件
import {
  Document,              // 必读注意事项
  CircleCloseFilled,  // 崩溃图标（实心圆叉）
  Switch,             // 切换/联机图标
  Loading,            // 加载/无响应图标
  Lightning,          // 闪电/卡顿图标
  VideoPlay,           // 播放/启动器图标
} from '@element-plus/icons-vue'
import { useThemeStore } from '@/store/theme'
import { computed } from 'vue'

// 使用主题状态管理
const themeStore = useThemeStore()

// 计算属性：根据当前主题模式返回侧边栏文字颜色
const sidebarTextColor = computed(() => {
  return themeStore.darkMode ? '#409EFF' : 'white'
})
</script>

<style scoped>
.app-sidebar {
  position: fixed; /* or absolute, depending on your layout */
  top: 6%;
  left: 0;
  width: 20%; /* adjust based on your design */
  height: 94%;
  z-index: 1; /* ensure it's lower than other elements if necessary */
  box-shadow: var(--el-box-shadow); /* add a shadow for better visibility */
}

.el-menu--vertical > .el-menu-item.is-active {
  border-left: 4px solid #409EFF;
  background-color: #f5faff60;
  transition: border-left 0.3s, background-color 0.3s;
}

.el-menu--vertical > .el-menu-item:hover {
  border-left: 2px solid #4ea7ff6e;
  transition: border-left 0.2s;
}

</style>
