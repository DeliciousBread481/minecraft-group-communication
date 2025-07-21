<!--<template>-->
<!--  <div v-if="showDebugger" class="theme-debugger">-->
<!--    <div class="debug-header">-->
<!--      <h3>主题调试器</h3>-->
<!--      <el-button @click="showDebugger = false" size="small" circle>-->
<!--        <el-icon><Close /></el-icon>-->
<!--      </el-button>-->
<!--    </div>-->

<!--    <div class="debug-info">-->
<!--      <p><strong>当前主题:</strong> {{ isDarkMode ? '暗色模式' : '亮色模式' }}</p>-->
<!--      <p><strong>HTML 类:</strong> {{ htmlClasses }}</p>-->
<!--      <p><strong>localStorage:</strong> darkMode = {{ localStorageTheme }}</p>-->
<!--      <p><strong>系统偏好:</strong> {{ systemPreference }}</p>-->
<!--    </div>-->

<!--    <div class="debug-actions">-->
<!--      <el-button @click="forceLightTheme">强制亮色主题</el-button>-->
<!--      <el-button @click="forceDarkTheme">强制暗色主题</el-button>-->
<!--      <el-button @click="resetTheme">重置主题</el-button>-->
<!--      <el-button @click="clearLocalStorage">清除 localStorage</el-button>-->
<!--    </div>-->

<!--    <div class="color-samples">-->
<!--      <div class="color-sample" :style="{ background: 'var(&#45;&#45;bg-color)' }">-->
<!--        &#45;&#45;bg-color-->
<!--      </div>-->
<!--      <div class="color-sample" :style="{ background: 'var(&#45;&#45;text-color)' }">-->
<!--        &#45;&#45;text-color-->
<!--      </div>-->
<!--      <div class="color-sample" :style="{ background: 'var(&#45;&#45;primary-color)' }">-->
<!--        &#45;&#45;primary-color-->
<!--      </div>-->
<!--    </div>-->
<!--  </div>-->

<!--  <div v-else class="debug-toggle" @click="showDebugger = true">-->
<!--    <el-icon></el-icon>-->
<!--  </div>-->
<!--</template>-->

<!--<script setup lang="ts">-->
<!--import { ref, computed, onMounted } from 'vue'-->
<!--import { useThemeStore } from '@/store/theme'-->
<!--import { Close,} from '@element-plus/icons-vue'-->

<!--const themeStore = useThemeStore()-->
<!--const showDebugger = ref(true)-->

<!--const isDarkMode = computed(() => themeStore.darkMode)-->
<!--const htmlClasses = computed(() => document.documentElement.className)-->
<!--const localStorageTheme = computed(() => localStorage.getItem('darkMode'))-->
<!--const systemPreference = computed(() =>-->
<!--  window.matchMedia('(prefers-color-scheme: dark)').matches-->
<!--    ? '暗色' : '亮色'-->
<!--)-->

<!--const forceLightTheme = () => {-->
<!--  themeStore.darkMode = false-->
<!--  localStorage.setItem('darkMode', 'false')-->
<!--  document.documentElement.classList.remove('dark')-->
<!--}-->

<!--const forceDarkTheme = () => {-->
<!--  themeStore.darkMode = true-->
<!--  localStorage.setItem('darkMode', 'true')-->
<!--  document.documentElement.classList.add('dark')-->
<!--}-->

<!--const resetTheme = () => {-->
<!--  localStorage.removeItem('darkMode')-->
<!--  window.location.reload()-->
<!--}-->

<!--const clearLocalStorage = () => {-->
<!--  localStorage.clear()-->
<!--  window.location.reload()-->
<!--}-->
<!--</script>-->

<!--<style scoped>-->
<!--.theme-debugger {-->
<!--  position: fixed;-->
<!--  bottom: 20px;-->
<!--  right: 20px;-->
<!--  width: 300px;-->
<!--  background: rgba(255, 255, 255, 0.9);-->
<!--  border: 1px solid #dcdfe6;-->
<!--  border-radius: 8px;-->
<!--  padding: 15px;-->
<!--  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);-->
<!--  z-index: 9999;-->
<!--  backdrop-filter: blur(5px);-->
<!--}-->

<!--.debug-header {-->
<!--  display: flex;-->
<!--  justify-content: space-between;-->
<!--  align-items: center;-->
<!--  margin-bottom: 10px;-->
<!--}-->

<!--.debug-info p {-->
<!--  margin: 8px 0;-->
<!--  font-size: 14px;-->
<!--}-->

<!--.debug-actions {-->
<!--  margin-top: 15px;-->
<!--  display: flex;-->
<!--  flex-wrap: wrap;-->
<!--  gap: 8px;-->
<!--}-->

<!--.debug-actions .el-button {-->
<!--  margin: 2px;-->
<!--  font-size: 12px;-->
<!--  padding: 6px 10px;-->
<!--}-->

<!--.color-samples {-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--  margin-top: 15px;-->
<!--}-->

<!--.color-sample {-->
<!--  width: 80px;-->
<!--  height: 40px;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  font-size: 10px;-->
<!--  border-radius: 4px;-->
<!--  border: 1px solid #ddd;-->
<!--  color: white;-->
<!--  text-shadow: 0 0 2px rgba(0,0,0,0.7);-->
<!--  font-weight: bold;-->
<!--}-->

<!--.debug-toggle {-->
<!--  position: fixed;-->
<!--  bottom: 20px;-->
<!--  right: 20px;-->
<!--  width: 40px;-->
<!--  height: 40px;-->
<!--  background: rgba(255, 255, 255, 0.9);-->
<!--  border-radius: 50%;-->
<!--  display: flex;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  cursor: pointer;-->
<!--  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);-->
<!--  z-index: 9998;-->
<!--  backdrop-filter: blur(5px);-->
<!--}-->

<!--.dark .theme-debugger,-->
<!--.dark .debug-toggle {-->
<!--  background: rgba(30, 30, 30, 0.9);-->
<!--  color: #fff;-->
<!--}-->

<!--.dark .color-sample {-->
<!--  border-color: #444;-->
<!--}-->
<!--</style>-->