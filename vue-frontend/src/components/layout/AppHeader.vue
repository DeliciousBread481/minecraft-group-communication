<template>
  <el-header class="app-header">
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

    <div class="nav-right">
      <el-button
        v-for="button in buttons"
        :key="button.text"
        :type="button.type"
        text
        class="nav-button"
        @click="$router.push(button.path)"
      >
        {{button.text}}
      </el-button>

      <el-switch
        v-model="darkMode"
        :active-action-icon="Moon"
        :inactive-action-icon="Sunny"
        @change="toggleTheme"

      />
<!--      <el-button-->
<!--        class="theme-toggle"-->
<!--        @click="toggleTheme"-->
<!--        :icon="darkMode ? Sunny : Moon"-->
<!--        style="&#45;&#45;el-switch-on-color: #304156;"-->
<!--      >-->
<!--        {{ darkMode ? '亮色模式' : '暗色模式' }}-->
<!--      </el-button>-->

      <el-button
        v-if="!userStore.isAuthenticated"
        text
        class="nav-button"
        @click="goToAuthPage"
      >
        登录/注册
      </el-button>

      <el-dropdown :size="30" v-else>
        <span class="user-info">
          <el-avatar :size="30" :src="userStore.userInfo?.avatar" />
          <span class="username">{{ userStore.userInfo?.username }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/settings')">个人设置</el-dropdown-item>
            <el-dropdown-item @click="userStore.userLogout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useThemeStore } from '@/store/theme'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Sunny, Moon } from '@element-plus/icons-vue'

const themeStore = useThemeStore()
const userStore = useUserStore()
const router = useRouter()

const darkMode = computed(() => themeStore.darkMode)

const toggleTheme = () => {
  themeStore.toggleTheme()
  console.log('主题切换按钮被点击')
}

const goToAuthPage = () => {
  router.push('/auth')
}

const buttons = [
  { type: 'default', text: '群公告文档', path: '/notice' },
  { type: 'default', text: '解决方案', path: '/solutions' },
]

// 添加调试信息
onMounted(() => {
  console.log('组件挂载完成，当前主题:', darkMode.value ? '暗色' : '亮色')
})
</script>

<style scoped lang="scss">
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 1.5rem;
  background-color: var(--bg-color);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 12px 0 var(--shadow-color);
  transition: all 0.3s;

  .logo {
    display: flex;
    align-items: center;
    gap: 0.5rem;

    .home-link-text {
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--text-color);
    }

    .logo-img {
      height: 80%;
      max-height: 40px;
      width: auto;
      aspect-ratio: 1/1;
      object-fit: contain;
      border-radius: 8px;
    }
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: 1rem;

    .nav-button {
      padding: 0.5rem 1rem;
      transition: background-color 0.3s;
      color: var(--text-color);
      font-weight: 500;

      &:hover {
        color: var(--primary-color);
        background-color: var(--hover-color);
      }
    }

    .theme-toggle {
      padding: 0.5rem 1rem;
      color: var(--text-color);
      border: 1px solid var(--border-color);
      background-color: var(--bg-secondary-color);

      &:hover {
        background-color: var(--hover-color);
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      cursor: pointer;

      .username {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        max-width: 120px;
        color: var(--text-color);
      }
    }
  }

  @media (max-width: 900px) {
    padding: 0 1rem;

    .home-link-text {
      font-size: 1.125rem !important;
    }

    .nav-button:not(.mobile-menu-btn) {
      display: none;
    }

    .theme-toggle {
      padding: 0.5rem;
      span {
        display: none;
      }
    }
  }

  @media (max-width: 600px) {
    .home-link-text {
      display: none;
    }
  }
}
</style>