<template>
  <!-- 应用头部导航栏组件 -->
  <el-header class="app-header">
    <!-- 左侧logo区域 -->
    <div class="logo">
      <!-- 网站logo图片 -->
      <img
        src="@/assets/image/icon.jpg"
        alt="logo"
        width="6%"
        height="6%"
        style="padding-right: 4px"
      />
      <!-- 返回首页的导航链接 -->
      <router-link to="/">Minecraft 疑难杂症交流群</router-link>
    </div>

    <!-- 右侧导航区域 -->
    <div class="nav-right">
      <!-- 主题切换按钮 -->
      <el-button type="text" @click="toggleTheme">
        <!-- 根据当前主题显示对应图标 -->
        <el-icon v-if="darkMode"><Sunny /></el-icon>
        <el-icon v-else><Moon /></el-icon>
        <!-- 显示当前主题状态文本 -->
        {{ darkMode ? '浅色模式' : '深色模式' }}
      </el-button>

      <!-- 登录/注册按钮 -->
      <el-button type="text" @click="$emit('show-login')">登录/注册</el-button>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useThemeStore } from '@/store/theme'  // 主题状态管理
import { Sunny, Moon } from '@element-plus/icons-vue' // 主题切换图标

// 使用主题状态管理
const themeStore = useThemeStore()

// 响应式变量：当前是否为深色模式
const darkMode = ref(themeStore.darkMode)

/**
 * 组件挂载后执行
 * 应用当前主题设置
 */
onMounted(() => {
  themeStore.applyTheme()
})

/**
 * 切换主题模式
 * 1. 调用主题仓库的切换方法
 * 2. 更新本地darkMode状态
 */
const toggleTheme = () => {
  themeStore.toggleTheme()                // 切换主题
  darkMode.value = themeStore.darkMode    // 同步主题状态
}
</script>

<style scoped>
/* 头部导航栏样式 */
.app-header {
  position: fixed;       /* 固定定位 */
  top: 0;                /* 顶部对齐 */
  left: 0;               /* 左侧对齐 */
  right: 0;              /* 右侧对齐 */
  height: 60px;          /* 固定高度 */
  z-index: 1000;         /* 确保在顶层 */
  display: flex;         /* 弹性布局 */
  justify-content: space-between; /* 左右两端对齐 */
  align-items: center;   /* 垂直居中 */
  background-color: var(--header-bg-color); /* 使用CSS变量设置背景色 */
  color: #fff;           /* 文字颜色 */
  padding: 0 20px;       /* 左右内边距 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 底部阴影 */
}

/* Logo区域样式 */
.logo a {
  color: #fff;           /* 链接文字颜色 */
  font-size: 20px;       /* 字体大小 */
  font-weight: bold;     /* 粗体 */
  text-decoration: none; /* 无下划线 */
}

/* 右侧导航区域样式 */
.nav-right {
  display: flex;         /* 弹性布局 */
  align-items: center;   /* 垂直居中 */
  gap: 10px;             /* 按钮间距 */
}

/* 文本按钮样式 */
.el-button--text {
  color: #fff;           /* 按钮文字颜色 */
}
</style>
