import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    darkMode: localStorage.getItem('darkMode') === "true" || false
  }),

  actions: {
    toggleTheme() {
      this.darkMode = !this.darkMode
      localStorage.setItem('darkMode', this.darkMode.toString())
      this.applyTheme()
    },

    applyTheme() {
      document.getElementById('theme-style')?.remove()

      const link = document.createElement('link')
      link.id = 'theme-style'
      link.rel = 'stylesheet'
      link.href = this.darkMode
        ? '/src/styles/dark-theme.css'
        : '/src/styles/light-theme.css'

      document.head.appendChild(link)
    }
  },

  persist: {
    paths: ['darkMode'],
    storage: localStorage
  }
})
