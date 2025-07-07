// 导入Pinia的defineStore函数，用于创建状态存储
import { defineStore } from 'pinia'
// 导入Vue的响应式API
import { ref, computed } from 'vue'
// 导入用户相关的API接口函数
import { login, register } from '@/api/user'
// 导入Axios的响应类型
import type { AxiosResponse } from 'axios'

// 定义API响应数据的通用接口
interface ApiResponse<T = unknown> {
  code: number;        // 状态码
  message: string;     // 响应消息
  data: T;             // 响应数据（泛型）
  success: boolean;    // 请求是否成功
}

// 定义登录响应数据的接口
interface LoginResponseData {
  token: string;       // 认证令牌
  user: {              // 用户信息对象
    id: number;        // 用户ID
    username: string;  // 用户名
    email: string;     // 邮箱
    avatar?: string;   // 头像（可选）
  };
}

// 创建并导出用户状态存储
export const useUserStore = defineStore('user', () => {
  // 使用ref创建响应式token状态，初始值从localStorage获取或为空字符串
  const token = ref(localStorage.getItem('token') || '')

  // 使用ref创建响应式用户信息状态，初始值从localStorage解析获取或为null
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性：用户是否已认证（根据token是否存在）
  const isAuthenticated = computed(() => !!token.value)

  // 用户登录方法
  const userLogin = async (credentials: { username: string; password: string }) => {
    try {
      // 调用登录API，指定响应类型为ApiResponse<LoginResponseData>
      const response = await login(credentials) as AxiosResponse<ApiResponse<LoginResponseData>>

      // 检查API响应是否成功
      if (response.data.success) {
        // 更新token和用户信息
        token.value = response.data.data.token
        userInfo.value = response.data.data.user

        // 将token和用户信息持久化到localStorage
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))

        // 登录成功返回true
        return true
      }
      // API响应失败返回false
      return false
    } catch (error) {
      // 捕获并记录登录过程中的错误
      console.error('登录失败:', error)
      // 登录失败返回false
      return false
    }
  }

  // 用户注册方法
  const userRegister = async (userData: { username: string; password: string; email: string }) => {
    try {
      // 调用注册API
      const response = await register(userData) as AxiosResponse<ApiResponse>

      // 检查API响应是否成功
      if (response.data.success) {
        // 注册成功返回true
        return true
      }
      // API响应失败返回false
      return false
    } catch (error) {
      // 捕获并记录注册过程中的错误
      console.error('注册失败:', error)
      // 注册失败返回false
      return false
    }
  }

  // 用户注销方法
  const userLogout = () => {
    // 清空token和用户信息
    token.value = ''
    userInfo.value = null

    // 从localStorage中移除token和用户信息
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 暴露状态和方法给组件使用
  return {
    token,               // 认证令牌
    userInfo,            // 用户信息对象
    isAuthenticated,     // 认证状态计算属性
    userLogin,           // 登录方法
    userRegister,        // 注册方法
    userLogout           // 注销方法
  }
})
