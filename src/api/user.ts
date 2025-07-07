import api from './index'
import type { AxiosPromise } from 'axios'
// 此api尚未完成无法使用
/**
 * 登录凭证接口
 * @interface
 * @property {string} username - 用户名
 * @property {string} password - 密码
 */
interface LoginCredentials {
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
interface RegisterData {
  username: string;
  password: string;
  email: string;
}

/**
 * API响应结构接口
 * @template T - 响应数据的类型（默认为unknown）
 * @interface
 * @property {number} code - 状态码
 * @property {string} message - 响应消息
 * @property {T} data - 响应数据
 * @property {boolean} success - 请求是否成功
 */
interface ApiResponse<T = unknown> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

/**
 * 用户登录API
 * @function
 * @param {LoginCredentials} credentials - 包含用户名和密码的登录凭证
 * @returns {AxiosPromise<ApiResponse>} - 返回Axios请求Promise对象，响应体符合ApiResponse结构
 */
export const login = (credentials: LoginCredentials): AxiosPromise<ApiResponse> => {
  return api.post('/auth/login', credentials)
}

/**
 * 用户注册API
 * @function
 * @param {RegisterData} userData - 包含用户名、密码和邮箱的注册数据
 * @returns {AxiosPromise<ApiResponse>} - 返回Axios请求Promise对象，响应体符合ApiResponse结构
 */
export const register = (userData: RegisterData): AxiosPromise<ApiResponse> => {
  return api.post('/auth/register', userData)
}
