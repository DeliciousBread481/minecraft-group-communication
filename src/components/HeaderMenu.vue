<script setup lang="ts">
import { User } from '@element-plus/icons-vue';

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
}
</script>

<template>
  <el-menu
    :default-active="1"
    class="el-menu-demo"
    mode="horizontal"
    :ellipsis="false"
    @select="handleSelect"
  >
    <el-menu-item index="1" @click="$router.push('/')">
      <img src="../assets/image/icon.jpg" alt="logo" width="70%" height="70%" />
      <el-text class="bond-font">&emsp;Minecraft 疑难杂症交流群</el-text>
    </el-menu-item>
    <el-menu-item index="2" @click="$router.push('/notice')">
      报错群文档
    </el-menu-item>
    <el-menu-item index="3">
      解决方案
    </el-menu-item>
    <el-menu-item index="4" @click="$router.push('/user')">
      <el-icon><User /></el-icon>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>

.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}

.el-menu--horizontal {
  --el-menu-horizontal-height: 6vh;
}

.bond-font {
  font-size: large;
  font-weight: bold;
}
</style>
