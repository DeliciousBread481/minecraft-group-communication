import { defineStore } from 'pinia'
import { ref, watch } from 'vue'

export const useThemeStore = defineStore('theme', () => {
  const darkMode = ref(localStorage.getItem('darkMode') === "true" || false)
  // 监听状态变化并保存到 localStorage
  watch(darkMode, (newValue) => {
    localStorage.setItem('darkMode', newValue.toString())
  })

  function toggleTheme() {
    darkMode.value = !darkMode.value
    applyTheme()
  }

  function applyTheme() {
    document.getElementById('theme-style')?.remove()

    const link = document.createElement('link')
    link.id = 'theme-style'
    link.rel = 'stylesheet'
    link.href = darkMode.value
      ? '/src/styles/dark-theme.css'
      : '/src/styles/light-theme.css'

    document.head.appendChild(link)
  }

  // 初始化时应用主题
  applyTheme()

  return {
    darkMode,
    toggleTheme,
    applyTheme
  }
})
