<template>
  <!-- 应用侧边栏导航组件 -->
  <el-aside class="app-sidebar">
    <!-- 垂直导航菜单 -->
    <el-menu
      default-active="1"
    class="el-menu-vertical-demo"
    background-color="var(--sidebar-bg-color)"
    text-color="var(--sidebar-text-color)"
    active-text-color="var(--sidebar-active-text-color)"
    >
    <!-- 注意事项菜单项（高亮显示） -->
    <el-menu-item index="1">
      <el-icon><House /></el-icon>
      <span style="color: var(--warning-text-color)">必读注意事项！</span>
    </el-menu-item>
    <el-sub-menu index="2">
      <template #title>
        <el-icon><CircleCloseFilled /></el-icon>
        <span>游戏崩溃</span>
      </template>
      <!-- 子菜单项 -->
      <el-menu-item index="2-1">闪退</el-menu-item>
      <el-menu-item index="2-2">启动失败</el-menu-item>
    </el-sub-menu>

    <!-- 游戏联机菜单项 -->
    <el-menu-item index="3">
      <el-icon><Switch /></el-icon>
      <span>游戏联机</span>
    </el-menu-item>

    <!-- 游戏无响应菜单项 -->
    <el-menu-item index="4">
      <el-icon><Loading /></el-icon>
      <span>游戏无响应</span>
    </el-menu-item>

    <!-- 游戏卡顿菜单项 -->
    <el-menu-item index="5">
      <el-icon><Lightning /></el-icon>
      <span>游戏卡顿</span>
    </el-menu-item>

    <!-- 启动器问题菜单项 -->
    <el-menu-item index="6">
      <el-icon><VideoPlay /></el-icon>
      <span>启动器问题</span>
    </el-menu-item>
    </el-menu>
  </el-aside>
</template>

<script setup lang="ts">
// 导入Element Plus图标组件
import {
  House,              // 首页图标
  CircleCloseFilled,  // 崩溃图标（实心圆叉）
  Switch,             // 切换/联机图标
  Loading,            // 加载/无响应图标
  Lightning,          // 闪电/卡顿图标
  VideoPlay           // 播放/启动器图标
} from '@element-plus/icons-vue'
</script>
