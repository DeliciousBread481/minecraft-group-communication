<script lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import Appsidebar from '../components/layout/AppSidebar.vue'
import LoginDialog from '@/components/auth/LoginDialog.vue'
import RegisterDialog from '@/components/auth/RegisterDialog.vue'
import { ref } from 'vue'

export default {
  name: 'NoticeView',
  components: {
    Appsidebar,AppHeader, LoginDialog, RegisterDialog
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

<template>
  <div class="layout-container">
    <el-container>
      <AppHeader @show-login="showLoginDialog" />

      <LoginDialog
        v-model:visible="loginDialogVisible" />
      <RegisterDialog v-model:visible="registerDialogVisible" />
    </el-container>
    <el-container>
      <!-- 左侧导航栏 -->
      <Appsidebar />
      <!-- 内容区 -->
      <el-main>Main</el-main>
    </el-container>
  </div>
</template>

<style scoped>
.layout-container {
  width: 100%;
  margin: 0;
  padding: 0;
}

.main-container {
  min-height: 100vh;
}

.header {
  padding: 0;
  margin: 0;
  height: 6vh;
  box-shadow: var(--el-box-shadow-light);
  z-index: 2;
}

.left-aside {
  padding: 0;
  margin: 0;
  height: 94vh;
  box-shadow: var(--el-box-shadow-light);
  z-index: 1;
}

.right-aside {
  padding: 0;
  margin: 0;
}
</style>
