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
@use '@/styles/components/header' as *;
</style>