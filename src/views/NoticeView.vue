<script lang="ts">
import AsideNav from '../components/AsideNav.vue'

export default {
  name: 'NoticeView',
  components: {
    AsideNav
  }
}
</script>

<template>
  <div>
    <el-container>
      <!-- 左侧导航栏 -->
      <el-aside width="250px" class="left-aside">
        <AsideNav />
      </el-aside>
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
