// 导入Pinia的defineStore函数，用于创建状态存储
import { defineStore } from 'pinia'

// 定义并导出一个名为'useThemeStore'的Pinia store
export const useThemeStore = defineStore('theme', {
  // 状态定义
  state: () => ({
    // 从localStorage读取darkMode值，若不存在则默认为false
    darkMode: localStorage.getItem('darkMode') === "true" || false
  }),

  // 操作方法定义
  actions: {
    // 切换主题模式
    toggleTheme() {
      // 反转当前暗黑模式状态
      this.darkMode = !this.darkMode
      // 将新状态持久化存储到localStorage
      localStorage.setItem('darkMode', this.darkMode.toString())
      // 应用新的主题样式
      this.applyTheme()
    },

    // 应用当前主题到CSS变量
    applyTheme() {
      // 获取文档根元素
      const root = document.documentElement

      // 暗黑模式配置
      if (this.darkMode) {
        root.style.setProperty('--bg-color', '#1a1a1a')                     // 背景色
        root.style.setProperty('--text-color', '#e6e6e6')                   // 文本色
        root.style.setProperty('--border-color', '#4d4d4d')                 // 边框色
        root.style.setProperty('--header-bg-color', '#121212')              // 头部背景
        root.style.setProperty('--sidebar-bg-color', '#1f1f1f')             // 侧边栏背景
        root.style.setProperty('--sidebar-text-color', '#a0a0a0')           // 侧边栏文本
        root.style.setProperty('--sidebar-active-text-color', '#66b3ff')    // 侧边栏激活项文本
        root.style.setProperty('--card-bg-color', '#242424')               // 卡片背景
        root.style.setProperty('--card-header-bg-color', '#333333')         // 卡片头部背景
        root.style.setProperty('--chart-bg-color', '#333333')              // 图表背景
      }
      // 明亮模式配置
      else {
        root.style.setProperty('--bg-color', '#ffffff')                     // 背景色
        root.style.setProperty('--text-color', '#ffffff')                   // 文本色
        root.style.setProperty('--border-color', '#dcdfe6')                 // 边框色
        root.style.setProperty('--header-bg-color', '#ffffff')              // 头部背景
        root.style.setProperty('--sidebar-bg-color', '#409eff')             // 侧边栏背景
        root.style.setProperty('--sidebar-text-color', '#ffffff')           // 侧边栏文本
        root.style.setProperty('--sidebar-active-text-color', '#409EFF')    // 侧边栏激活项文本
        root.style.setProperty('--card-bg-color', '#ffffff')                // 卡片背景
        root.style.setProperty('--card-header-bg-color', '#f5f7fa')         // 卡片头部背景
        root.style.setProperty('--chart-bg-color', '#f5f7fa')               // 图表背景
      }
    }
  }
})
