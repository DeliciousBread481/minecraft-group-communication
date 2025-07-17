import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import {
  login,
  register,
  refreshToken,
  getCurrentUser,
  logout,
  updateUser
} from '@/api/user'
import type {
  LoginCredentials,
  RegisterData,
  UserInfo
} from '@/api/user'

// 创建并导出用户状态存储
export const useUserStore = defineStore('user', () => {
  // 初始化状态
  const token = ref(localStorage.getItem('accessToken') || '')
  const refreshTokenValue = ref(localStorage.getItem('refreshToken') || '')
  const userInfo = ref<UserInfo | null>(
    JSON.parse(localStorage.getItem('user') || 'null')
  )

  // 监听状态变化并保存到 localStorage
  watch(token, (newToken) => {
    if (newToken) localStorage.setItem('accessToken', newToken)
    else localStorage.removeItem('accessToken')
  })

  watch(refreshTokenValue, (newRefreshToken) => {
    if (newRefreshToken) localStorage.setItem('refreshToken', newRefreshToken)
    else localStorage.removeItem('refreshToken')
  })

  watch(userInfo, (newUserInfo) => {
    if (newUserInfo) localStorage.setItem('user', JSON.stringify(newUserInfo))
    else localStorage.removeItem('user')
  }, { deep: true })

  const isAuthenticated = computed(() => !!token.value)

  // 用户登录方法
  const userLogin = async (credentials: LoginCredentials) => {
    try {
      const response = await login(credentials)

      if (response.success) {
        token.value = response.data.accessToken
        refreshTokenValue.value = response.data.refreshToken
        userInfo.value = response.data.user
        return true
      }
      return false
    } catch (error) {
      console.error('登录失败:', error)
      return false
    }
  }

  // 用户注册方法
  const userRegister = async (userData: RegisterData) => {
    try {
      const response = await register(userData)

      return response.success;

    } catch (error) {
      console.error('注册失败:', error)
      return false
    }
  }

  // 用户注销方法
  const userLogout = async () => {
    try {
      await logout()
    } catch (error) {
      console.error('注销请求失败:', error)
    } finally {
      token.value = ''
      refreshTokenValue.value = ''
      userInfo.value = null
      // 确保所有localStorage项被清除
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
    }
  }

  // 更新用户信息
  const updateUserInfo = async (newUserInfo: Partial<UserInfo>) => {
    try {
      const response = await updateUser(newUserInfo)

      if (response.success && userInfo.value) {
        // 合并更新用户信息
        userInfo.value = { ...userInfo.value, ...newUserInfo }
        return true
      }
      return false
    } catch (error) {
      console.error('更新用户信息失败:', error)
      return false
    }
  }

  // 刷新令牌
  const refreshAuthToken = async () => {
    try {
      if (!refreshTokenValue.value) return false;

      // 确保localStorage中的refreshToken值是最新的
      localStorage.setItem('refreshToken', refreshTokenValue.value);

      // 不传参数调用refreshToken，它会从localStorage获取refreshToken
      const response = await refreshToken()

      if (response.success) {
        token.value = response.data.accessToken
        refreshTokenValue.value = response.data.refreshToken
        return true
      }
      return false
    } catch (error) {
      console.error('Token刷新失败:', error)
      await userLogout()
      return false
    }
  }

  // 获取当前用户信息
  const fetchCurrentUser = async () => {
    try {
      const response = await getCurrentUser()

      if (response.success) {
        userInfo.value = response.data
        return true
      }
      return false
    } catch (error) {
      console.error('获取用户信息失败:', error)
      return false
    }
  }

  // 暴露状态和方法给组件使用
  return {
    token,
    refreshToken: refreshTokenValue,
    userInfo,
    isAuthenticated,
    userLogin,
    userRegister,
    userLogout,
    updateUserInfo,
    refreshAuthToken,
    fetchCurrentUser
  }
})
