<script setup lang="ts">
import { CircleCloseFilled, Lightning, Loading, Switch, VideoPlay, Warning } from '@element-plus/icons-vue'
import { ref } from 'vue'

/*import { ref } from 'vue'
原先做的可收缩侧边栏的方法
const boolaside = ref(false);
function PoO() {
  if(boolaside.value == false){
    boolaside.value  = true;
  }else{
    boolaside.value = false;
  }
}
  */

const activeIndex = ref('1');

const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath);
}
</script>

<template>
  <el-menu
    :default-active="activeIndex"
    class="el-menu-vertical-demo"
    mode="vertical"
    @select="handleSelect"
  >
    <el-menu-item index="1">
      <template #title>
        <el-icon>
          <Warning color="red" />
        </el-icon>
        <span class="notice-font">必读注意事项！</span>
      </template>
    </el-menu-item>
    <el-menu-item index="2">
      <template #title>
        <el-icon><CircleCloseFilled /></el-icon>
        <span>游戏崩溃/闪退/启动失败</span>
      </template>
    </el-menu-item>
    <el-menu-item index="3">
      <template #title>
        <el-icon><Switch /></el-icon>
        <span>游戏联机/连接服务器失败</span>
      </template>
    </el-menu-item>
    <el-menu-item index="4">
      <template #title>
        <el-icon><Loading /></el-icon>
        <span>游戏无响应/卡死</span>
      </template>
    </el-menu-item>
    <el-menu-item index="5">
      <template #title>
        <el-icon><Lightning /></el-icon>
        <span>游戏卡顿严重</span>
      </template>
    </el-menu-item>
    <el-menu-item index="6">
      <template #title>
        <el-icon><VideoPlay /></el-icon>
        <span>启动器使用出现问题</span>
      </template>
    </el-menu-item>
  </el-menu>
</template>

<style>

/*
.el-menu-vertical-demo > .el-menu--collapse {
  width: 250px;
  max-height: 10vh;
}
  */

.notice-font{
  color: red;
  font-weight: bold;
}

.el-menu--vertical > .el-menu-item.is-active {
  border-left: 4px solid #409EFF;
  background-color: #ecf5ff;
  transition: border-left 0.3s, background-color 0.3s;
}

.el-menu--vertical > .el-menu-item:hover {
  border-left: 2px solid #909399;
  transition: border-left 0.2s;
}

</style>
