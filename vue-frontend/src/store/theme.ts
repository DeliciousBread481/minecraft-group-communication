//store/theme.ts
import { defineStore } from 'pinia'
import { onMounted, ref } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  // 初始化主题状态
  const darkMode = ref(false)

  // 初始化函数
  const initTheme = () => {
    // 1. 从localStorage获取保存的主题
    const savedTheme = localStorage.getItem('darkMode')
    if (savedTheme) {
      darkMode.value = savedTheme === 'true'
      return
    }

    // 2. 如果没有保存的主题，使用系统偏好
    darkMode.value = window.matchMedia('(prefers-color-scheme: dark)').matches

    // 3. 保存初始主题状态
    localStorage.setItem('darkMode', darkMode.value.toString())
  }

  // 切换主题函数
  const toggleTheme = () => {
    darkMode.value = !darkMode.value
    localStorage.setItem('darkMode', darkMode.value.toString())
    applyTheme(darkMode.value)
    console.log('主题已切换:', darkMode.value ? '暗色' : '亮色')
  }

  // 应用主题到DOM
  const applyTheme = (isDark: boolean) => {
    if (isDark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
  }

  // 初始化主题
  onMounted(() => {
    initTheme()
    applyTheme(darkMode.value)
    console.log('主题初始化完成:', darkMode.value ? '暗色' : '亮色')
  })

  return {
    darkMode,
    toggleTheme,
  }
})