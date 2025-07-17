<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useUserStore } from '@/store/user';

const userStore = useUserStore();

// 在应用启动时检查用户登录状态
onMounted(async () => {
  if (userStore.token) {
    // 如果本地存储中有token，尝试获取用户信息
    try {
      await userStore.fetchCurrentUser();
    } catch (error) {
      console.error('Failed to restore user session:', error);
      // 如果获取用户信息失败，尝试刷新token
      const refreshSuccess = await userStore.refreshAuthToken();
      if (refreshSuccess) {
        // 刷新token成功后，重新获取用户信息
        await userStore.fetchCurrentUser();
      } else {
        // 如果刷新token失败，清除用户状态
        userStore.userLogout();
      }
    }
  }
});
</script>

<style>
#app {
  height: 100vh;
  overflow: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
  Arial, sans-serif;
}
</style>
