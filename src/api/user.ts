import api from './index'
import type { AxiosError } from 'axios'

/**
 * 登录凭证接口
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * 用户注册数据接口
 */
export interface RegisterData {
  username: string;
  password: string;
  email: string;
}

/**
 * API 响应结构接口
 * @template T - 响应数据的类型
 */
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

/**
 * 用户登录响应数据结构
 */
export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: UserInfo;
}

/**
 * 用户信息接口
 */
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  avatar?: string;
}

/**
 * API 错误对象接口
 */
export interface ApiError {
  message: string;
  code: number;
  originalError: AxiosError;
}

/**
 * 用户登录 API
 */
export const login = async (
  credentials: LoginCredentials
): Promise<ApiResponse<LoginResponse>> => {
  try {
    return (await api.post('/auth/login', credentials)) as unknown as ApiResponse<LoginResponse>
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 用户注册 API
 */
export const register = async (
  userData: RegisterData
): Promise<ApiResponse> => {
  try {
    return (await api.post('/auth/register', userData)) as unknown as ApiResponse
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 刷新访问令牌 API
 */
export const refreshToken = async (
  refreshToken: string
): Promise<ApiResponse<{ token: string; refreshToken: string }>> => {
  try {
    return (await api.post('/auth/refresh', { refreshToken })) as unknown as ApiResponse<{
      token: string
      refreshToken: string
    }>
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 获取当前用户信息 API
 */
export const getCurrentUser = async (): Promise<ApiResponse<UserInfo>> => {
  try {
    return (await api.get('/user/me')) as unknown as ApiResponse<UserInfo>
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 更新用户信息 API
 */
export const updateUser = async (
  userData: Partial<UserInfo>
): Promise<ApiResponse<UserInfo>> => {
  try {
    return (await api.patch('/user/me', userData)) as unknown as ApiResponse<UserInfo>
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 用户注销 API
 */
export const logout = async (): Promise<ApiResponse> => {
  try {
    return (await api.post('/auth/logout')) as unknown as ApiResponse
  } catch (error) {
    throw error as ApiError
  }
}
