import api from '@/utils/api';
import { handleResponse } from '@/utils/api';
import type {
  AuthApiResponse,
  VoidApiResponse,
  LoginRequest,
  RegisterRequest,
  RefreshRequest
} from '@/types/api'

/**
 * 用户注册
 * @param data 注册请求数据
 */
export const register = async (data: RegisterRequest): Promise<AuthApiResponse> => {
  try {
    const response = await api.post('/auth/register', data);
    return handleResponse<AuthResponseData>(response);
  } catch (error: any) {
    if (error.response) {
      const status = error.response.status;
      if (status === 409) {
        throw new Error('用户名或邮箱已被使用');
      } else if (status === 400) {
        throw new Error('无效的注册信息');
      }
    }
    throw new Error('注册失败');
  }
};

/**
 * 用户登录
 * @param data 登录请求数据
 */
export const login = async (data: LoginRequest): Promise<AuthApiResponse> => {
  try {
    const response = await api.post('/auth/login', data);
    return handleResponse<AuthResponseData>(response);
  } catch (error: any) {
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        throw new Error('用户名或密码错误');
      } else if (status === 400) {
        throw new Error('无效的登录信息');
      }
    }
    throw new Error('登录失败');
  }
};

/**
 * 刷新访问令牌
 * @param data 刷新令牌请求数据
 */
export const refreshToken = async (data: RefreshRequest): Promise<AuthApiResponse> => {
  try {
    const response = await api.post('/auth/refresh-token', data);
    return handleResponse<AuthResponseData>(response);
  } catch (error: any) {
    if (error.response) {
      const status = error.response.status;
      if (status === 401) {
        throw new Error('刷新令牌无效或已过期');
      }
    }
    throw new Error('刷新令牌失败');
  }
};

/**
 * 用户登出
 */
export const logout = async (): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/auth/logout');
    return handleResponse<void>(response);
  } catch (error: any) {
    // 登出失败时返回模拟成功响应
    return {
      success: true,
      status: 200,
      message: '登出成功',
      data: undefined
    } as VoidApiResponse;
  }
};

/**
 * 撤销令牌
 * @param username 用户名
 */
export const revokeToken = async (username: string): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/auth/revoke-token', null, {
      params: { username }
    });
    return handleResponse<void>(response);
  } catch (error: any) {
    throw new Error('撤销令牌失败');
  }
};

/**
 * 认证响应数据结构
 */
export interface AuthResponseData {
  accessToken: string;
  refreshToken: string;
}