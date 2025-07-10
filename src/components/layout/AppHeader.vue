<!--components/layout/AppHeader.vue-->
<template>
  <!-- 应用头部导航栏组件 -->
  <el-header class="app-header">
    <!-- 左侧logo区域 -->
    <el-button type="text" @click="$router.push('/')" class="logo-btn">
      <div class="logo">
        <img
          src="@/assets/image/icon.jpg"
          alt="logo"
          class="logo-img"
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
        class="nav-button"
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
        class="nav-button"
        @click="$emit('show-login')"
      >
        登录/注册
      </el-button>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useThemeStore } from '@/store/theme'
import { Sunny, Moon } from '@element-plus/icons-vue'

const themeStore = useThemeStore()
const darkMode = ref(themeStore.darkMode)

onMounted(() => {
  themeStore.applyTheme()
})

const toggleTheme = () => {
  themeStore.toggleTheme()
  darkMode.value = themeStore.darkMode
}

const buttons = [
  {type: 'default', text: '群公告文档'},
  {type: 'default', text: '解决方案'},
]
</script>

<style scoped>
/* 头部导航栏样式 */
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 6vh;
  min-height: 50px;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--header-bg-color);
  color: #409EFF;
  padding: 0 20px;
  box-shadow: var(--el-box-shadow);
}

/* Logo区域样式 */
.logo {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 8px;
}

/* 图片自适应样式 */
.logo-img {
  height: 80%;
  max-height: 40px;
  width: auto;
  aspect-ratio: 1/1;
  object-fit: contain;
}

/* 文本链接样式 */
.home-link-text {
  color: #4099f4;
  font-weight: bold;
  /* 响应式字体大小 */
  font-size: clamp(1.2rem, 2.5vw, 1.8rem);
  white-space: nowrap;
}

/* 右侧导航区域样式 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 导航按钮样式 */
.nav-button {
  font-size: clamp(0.8rem, 1vw, 1rem);
  white-space: nowrap;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .home-link-text {
    font-size: clamp(1.1rem, 2.2vw, 1.5rem);
  }

  .logo-img {
    height: 75%;
  }
}

@media (max-width: 768px) {
  .app-header {
    padding: 0 15px;
  }

  .home-link-text {
    font-size: clamp(1rem, 2vw, 1.3rem);
  }

  .nav-button {
    font-size: clamp(0.75rem, 1.5vw, 0.9rem);
  }

  .logo {
    gap: 5px;
  }
}

@media (max-width: 480px) {
  .home-link-text {
    font-size: clamp(0.9rem, 4vw, 1.1rem);
  }

  .logo-img {
    height: 70%;
    max-height: 35px;
  }

  .nav-right {
    gap: 6px;
  }
}
</style>
