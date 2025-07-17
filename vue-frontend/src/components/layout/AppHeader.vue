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
        style="--el-switch-on-color: #304156;"
      />

      <el-button
        v-if="!userStore.isAuthenticated"
        text
        class="nav-button"sw
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
import { ref, onMounted } from 'vue'
import { useThemeStore } from '@/store/theme'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { Sunny, Moon } from '@element-plus/icons-vue'

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const emit = defineEmits(['toggle-menu'])

const themeStore = useThemeStore()
const userStore = useUserStore()
const router = useRouter()
const darkMode = ref(themeStore.darkMode)
const isMobile = ref(false)

onMounted(() => {
  themeStore.applyTheme()
  isMobile.value = window.innerWidth < 768
})

const toggleTheme = () => {
  themeStore.toggleTheme()
  darkMode.value = themeStore.darkMode
}

const goToAuthPage = () => {
  router.push('/auth')
}

const buttons = [
  { type: 'default', text: '群公告文档', path: '/notice' },
  { type: 'default', text: '解决方案', path: '/solutions' },
]
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  min-height: 60px;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-color);
  color: white;
  padding: 0 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid var(--border-color);
  transition: background-color var(--transition-speed),
  border-color var(--transition-speed);
}

.logo {
  display: flex;
  align-items: center;
  height: 100%;
  gap: 2px;
}

.logo-img {
  height: 80%;
  max-height: 40px;
  width: auto;
  aspect-ratio: 1/1;
  object-fit: contain;
  border-radius: 8px;
}

.home-link-text {
  color: var(--text-color);
  font-weight: bold;
  font-size: 1.4rem;
  white-space: nowrap;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.nav-button {
  font-size: 1rem;
  font-weight: 500;
  white-space: nowrap;
  color: white;
}

.nav-button:hover {
  color: var(--primary-color);
}

.mobile-menu-btn {
  display: none;
  color: rgba(255, 255, 255, 0.555);
  font-size: 1.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: white;
}

.username {
  font-weight: 500;
}

@media (max-width: 992px) {
  .home-link-text {
    font-size: 1.2rem;
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
    font-size: 1.1rem;
  }

  .nav-button {
    font-size: 0.9rem;
    padding: 8px 10px;
  }

  .nav-right {
    gap: 8px;
  }

  .mobile-menu-btn {
    display: block;
    margin-left: 10px;
  }
}

@media (max-width: 480px) {
  .home-link-text {
    display: none;
  }

  .logo-img {
    height: 70%;
    max-height: 35px;
  }

  .nav-button:not(.mobile-menu-btn) {
    display: none;
  }
}
</style>
