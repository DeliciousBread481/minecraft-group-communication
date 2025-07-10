<!-- components/layout/BasicLayout -->
<template>
  <div class="layout-container">
    <!-- 顶部导航栏 -->
    <AppHeader class="header" @show-login="showLoginDialog" />

    <!-- 主体内容区 -->
    <div class="main-container">
      <!-- 左侧导航栏 -->
      <Appsidebar class="left-aside" />

      <!-- 右侧内容区 -->
      <div class="right-aside">
        <router-view></router-view>
      </div>
    </div>

    <!-- 登录和注册弹窗 -->
    <LoginDialog v-model:visible="loginDialogVisible" />
    <RegisterDialog v-model:visible="registerDialogVisible" />
  </div>
</template>

<script lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import Appsidebar from './AppSidebar.vue'
import LoginDialog from '@/components/auth/LoginDialog.vue'
import RegisterDialog from '@/components/auth/RegisterDialog.vue'
import { ref } from 'vue'

export default {
  name: 'NoticeView',
  components: {
    Appsidebar,
    AppHeader,
    LoginDialog,
    RegisterDialog
  },
  setup() {
    const loginDialogVisible = ref(false)
    const registerDialogVisible = ref(false)

    const showLoginDialog = () => {
      loginDialogVisible.value = true
    }

    const showRegisterDialog = () => {
      registerDialogVisible.value = true
    }

    return {
      loginDialogVisible,
      registerDialogVisible,
      showLoginDialog,
      showRegisterDialog
    }
  },
}
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  height: 6vh;
  box-shadow: var(--el-box-shadow-light);
  z-index: 2;
}

.main-container {
  display: flex;
  flex: 1;
  min-height: 0;
}

.left-aside {
  flex: 0 0 200px;
  height: 100%;
  overflow-y: auto;
  box-shadow: var(--el-box-shadow-light);
  z-index: 1;
}

.right-aside {
  flex: 1;
  display: flex;
  min-width: 0;
}

.right-aside > .el-main {
  padding: 5px;
  background-color: var(--bg-color);
  width: 100%;
  height: 100%;
}
</style>
