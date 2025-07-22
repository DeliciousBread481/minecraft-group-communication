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

      <el-button
        v-if="!isAuthenticated"
        text
        class="nav-button"
        @click="goToAuthPage"
      >
        登录/注册
      </el-button>

      <el-dropdown :size="30" v-if="isAuthenticated">
        <span class="user-info">
          <el-avatar :size="30" :src="userStore.userInfo?.avatar" />
          <span class="username">{{ userStore.userInfo?.username }}</span>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="$router.push('/settings')">个人设置</el-dropdown-item>
            <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
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
import { ElMessage } from 'element-plus';

const themeStore = useThemeStore()
const userStore = useUserStore()
const router = useRouter()

// 添加计算属性检查认证状态
const isAuthenticated = computed(() => userStore.isAuthenticated)

const darkMode = computed(() => themeStore.darkMode)

const toggleTheme = () => {
  themeStore.toggleTheme()
}

const goToAuthPage = () => {
  router.push('/auth')
}

// 添加登出处理方法
const handleLogout = async () => {
  try {
    await userStore.logout();
    await router.push('/');
    ElMessage.success('您已成功退出登录');
  } catch (error) {
    console.error('登出失败:', error);
    ElMessage.error('登出失败，请重试');
  }
};

const buttons = [
  { type: 'default', text: '群公告文档', path: '/notice' },
  { type: 'default', text: '解决方案', path: '/solutions' },
]

onMounted(() => {
  // 初始化时检查用户认证状态
  if (userStore.isAuthenticated) {
    userStore.fetchUserInfo();
  }
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