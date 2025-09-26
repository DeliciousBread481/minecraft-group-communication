import { useThemeStore } from '@/store/theme'

export function checkThemeStatus() {
  const themeStore = useThemeStore()

  console.group('主题状态检查')
  console.log('Store 状态:', themeStore.darkMode)
  console.log('localStorage:', localStorage.getItem('darkMode'))
  console.log('HTML 类:', document.documentElement.className)
  console.log('系统偏好:', window.matchMedia('(prefers-color-scheme: dark)').matches)

  // 检查CSS变量
  const rootStyles = getComputedStyle(document.documentElement)
  console.log('--bg-color:', rootStyles.getPropertyValue('--bg-color'))
  console.log('--text-color:', rootStyles.getPropertyValue('--text-color'))

  console.groupEnd()

  return {
    storeState: themeStore.darkMode,
    localStorageState: localStorage.getItem('darkMode'),
    htmlClasses: document.documentElement.className,
    cssVariables: {
      bgColor: rootStyles.getPropertyValue('--bg-color'),
      textColor: rootStyles.getPropertyValue('--text-color')
    }
  }
}

// 在应用启动时调用
export function initThemeCheck() {
  setTimeout(() => {
    checkThemeStatus()
  }, 1000)
}