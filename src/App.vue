<script setup lang="ts">
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { RouterLink, RouterView } from 'vue-router'
import HeaderMenu from './components/HeaderMenu.vue'
</script>

<template>
  <div class="layout-container">
    <el-container class="main-container">
      <!-- 顶部栏 -->
      <el-header class="header"><HeaderMenu /></el-header>
      <el-container>
        <!-- 主要内容区域 -->
        <RouterView />
      </el-container>
    </el-container>
  </div>
</template>

<style scoped>
.layout-container {
  height: 100vh;
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
