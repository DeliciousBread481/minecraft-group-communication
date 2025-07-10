<template>
  <!-- 应用头部导航栏组件 -->
  <el-header class="app-header">
    <!-- 左侧logo区域 -->
    <el-button type="text" @click="$router.push('/')">
      <div class="logo" style="max-width: 225px;">
        <!-- 网站logo图片 -->
        <img
          src="@/assets/image/icon.jpg"
          alt="logo"
          width="25%"
          height="25%"
          style="padding-right: 4px"
        />
        <el-text class="home-link-text">Minecraft 疑难杂症交流群</el-text>
      </div>
    </el-button>

    <!-- 右侧导航区域 -->
    <div class="nav-right">
      <el-button
        v-for="button in buttons"
        :key="button.text"
        :type="button.type"
        text
        :style="{ color: '#409eff !important' }"
        @click="$router.push('/notice')"
      >
        {{button.text}}
      </el-button>
      <!-- 主题切换开关 -->
      <el-switch
        v-model="darkMode"
        :active-action-icon="Moon"
        :inactive-action-icon="Sunny"
        @change="toggleTheme"
        style="--el-switch-on-color: #304156;"
      />
      <!-- 登录/注册按钮 -->
      <el-button
        text
        @click="$emit('show-login')"
        :style="{ color: '#409eff !important' }"
      >
        登录/注册
      </el-button>
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

const buttons = [
  {type: 'default', text: '群公告文档'},
  {type: 'default', text: '解决方案'},
]
</script>

<style scoped>
/* 头部导航栏样式 */
.app-header {
  position: fixed;       /* 固定定位 */
  top: 0;                /* 顶部对齐 */
  left: 0;               /* 左侧对齐 */
  right: 0;              /* 右侧对齐 */
  height: 6%;          /* 固定高度 */
  z-index: 1000;         /* 确保在顶层 */
  display: flex;         /* 弹性布局 */
  justify-content: space-between; /* 左右两端对齐 */
  align-items: center;   /* 垂直居中 */
  background-color: var(--header-bg-color); /* 使用CSS变量设置背景色 */
  color: #409EFF;           /* 文字颜色 */
  padding: 0 20px;       /* 左右内边距 */
  box-shadow: var(--el-box-shadow); /* 底部阴影 */
}

/* Logo区域样式 */
.logo {
  color: #fff;           /* 链接文字颜色 */
  display: flex;         /* 添加弹性布局 */
  align-items: center; /* 垂直居中 */
  width: 1px；
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

.home-link-text {
  color: #4099f4;
  font-size: "40px";
  align-items: center;
  height: 100%;
  font-weight: bold;
}
</style>
