import { apiPost } from '@/utils/apiBase';
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
    return await apiPost<AuthApiResponse>('/auth/register', data, '注册失败');
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
    return await apiPost<AuthApiResponse>('/auth/login', data, '登录失败');
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
    return await apiPost<AuthApiResponse>('/auth/refresh-token', data, '刷新令牌失败');
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
  return apiPost<VoidApiResponse>('/auth/logout', undefined, '登出失败');
};

/**
 * 撤销令牌
 * @param username 用户名
 */
export const revokeToken = async (username: string): Promise<VoidApiResponse> => {
  try {
    return await apiPost<VoidApiResponse>('/auth/revoke-token', null, '撤销令牌失败', {
      params: { username }
    });
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