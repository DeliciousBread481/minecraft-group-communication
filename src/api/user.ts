import api from './index'
import type { AxiosError } from 'axios'

/**
 * 登录凭证接口
 * @interface
 * @property {string} username - 用户名
 * @property {string} password - 密码
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * 用户注册数据接口
 * @interface
 * @property {string} username - 用户名
 * @property {string} password - 密码
 * @property {string} email - 电子邮箱
 */
export interface RegisterData {
  username: string;
  password: string;
  email: string;
}

/**
 * API 响应结构接口
 * @template T - 响应数据的类型（默认为 unknown）
 * @interface
 * @property {number} code - 状态码
 * @property {string} message - 响应消息
 * @property {T} data - 响应数据
 * @property {boolean} success - 请求是否成功
 */
export interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

/**
 * 用户登录响应数据结构
 * @interface
 * @property {string} token - 认证令牌
 * @property {string} refreshToken - 刷新令牌
 * @property {UserInfo} user - 用户信息
 */
export interface LoginResponse {
  token: string;
  refreshToken: string;
  user: UserInfo;
}

/**
 * 用户信息接口
 * @interface
 * @property {number} id - 用户 ID
 * @property {string} username - 用户名
 * @property {string} email - 电子邮箱
 * @property {string} [avatar] - 头像 URL（可选）
 */
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  avatar?: string;
}

/**
 * API 错误对象接口
 * @interface
 * @property {string} message - 错误消息
 * @property {number} code - 错误代码（通常为 HTTP 状态码）
 * @property {AxiosError} originalError - 原始错误对象
 */
export interface ApiError {
  message: string;
  code: number;
  originalError: AxiosError;
}

/**
 * 用户登录 API
 * @function
 * @param {LoginCredentials} credentials - 包含用户名和密码的登录凭证
 * @returns {Promise<ApiResponse<LoginResponse>>} - 返回 Promise 对象，解析为 ApiResponse<LoginResponse>
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const login = async (
  credentials: LoginCredentials
): Promise<ApiResponse<LoginResponse>> => {
  try {
    return await api.post<ApiResponse<LoginResponse>>('/auth/login', credentials)
  } catch (error) {
    // 将错误转换为统一的 ApiError 格式
    throw error as ApiError
  }
}

/**
 * 用户注册 API
 * @function
 * @param {RegisterData} userData - 包含用户名、密码和邮箱的注册数据
 * @returns {Promise<ApiResponse>} - 返回 Promise 对象，解析为 ApiResponse
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const register = async (
  userData: RegisterData
): Promise<ApiResponse> => {
  try {
    return await api.post<ApiResponse>('/auth/register', userData)
  } catch (error) {
    // 将错误转换为统一的 ApiError 格式
    throw error as ApiError
  }
}

/**
 * 刷新访问令牌 API
 * @function
 * @param {string} refreshToken - 刷新令牌
 * @returns {Promise<ApiResponse<{ token: string; refreshToken: string }>>} - 返回 Promise 对象，解析为新的令牌
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const refreshToken = async (
  refreshToken: string
): Promise<ApiResponse<{ token: string; refreshToken: string }>> => {
  try {
    return await api.post<ApiResponse<{ token: string; refreshToken: string }>>('/auth/refresh', {
      refreshToken,
    })
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 获取当前用户信息 API
 * @function
 * @returns {Promise<ApiResponse<UserInfo>>} - 返回 Promise 对象，解析为当前用户信息
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const getCurrentUser = async (): Promise<ApiResponse<UserInfo>> => {
  try {
    return await api.get<ApiResponse<UserInfo>>('/user/me')
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 更新用户信息 API
 * @function
 * @param {Partial<UserInfo>} userData - 要更新的用户信息
 * @returns {Promise<ApiResponse<UserInfo>>} - 返回 Promise 对象，解析为更新后的用户信息
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const updateUser = async (
  userData: Partial<UserInfo>
): Promise<ApiResponse<UserInfo>> => {
  try {
    return await api.patch<ApiResponse<UserInfo>>('/user/me', userData)
  } catch (error) {
    throw error as ApiError
  }
}

/**
 * 用户注销 API
 * @function
 * @returns {Promise<ApiResponse>} - 返回 Promise 对象，解析为注销结果
 * @throws {ApiError} - 当请求失败时抛出 ApiError 对象
 */
export const logout = async (): Promise<ApiResponse> => {
  try {
    return await api.post<ApiResponse>('/auth/logout')
  } catch (error) {
    throw error as ApiError
  }
}
