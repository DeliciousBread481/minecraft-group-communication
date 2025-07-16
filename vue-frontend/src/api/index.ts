import axios, { type AxiosError, type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import {
  AppException,
  NetworkException,
  ApiBusinessException,
  TokenException,
  TokenRefreshException,
  InvalidTokenFormatException,
  TokenMissingException
} from './exceptions'

// 环境变量处理
const isProduction = import.meta.env.PROD
const baseURL = isProduction
  ? 'https://api.yourdomain.com/crashAPI/api'
  : '/crashAPI/api'

// 创建axios实例
export const api: AxiosInstance = axios.create({
  baseURL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

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

interface ExtendedAxiosRequestConfig extends AxiosRequestConfig {
  _retry?: boolean;
}

/**
 * 检查有效的JWT格式
 * @param token JWT令牌字符串
 * @returns 是否为有效格式
 */
export function isValidJwt(token: string | null): boolean {
  if (!token) return false;

  try {
    const parts = token.split('.');
    if (parts.length !== 3) return false;

    // 验证头部和负载是否为有效JSON
    JSON.parse(atob(parts[0]));
    JSON.parse(atob(parts[1]));

    return true;
  } catch {
    return false;
  }
}

// 请求拦截器：自动添加JWT令牌
api.interceptors.request.use((config: AxiosRequestConfig) => {
  try {
    const token = localStorage.getItem('accessToken');

    // 验证令牌格式
    if (token && !isValidJwt(token)) {
      throw new InvalidTokenFormatException('access');
    }

    if (token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  } catch (error) {
    if (error instanceof TokenException) {
      console.error(error.message);
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
}, (error) => {
  return Promise.reject(new NetworkException('Request configuration failed', error));
});


let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

const subscribeTokenRefresh = (cb: (token: string) => void) => {
  refreshSubscribers.push(cb);
};

const onRefreshed = (token: string) => {
  refreshSubscribers.forEach(cb => cb(token));
  refreshSubscribers = [];
};

const handleUnauthorizedError = async (error: AxiosError) => {
  const originalRequest = error.config as ExtendedAxiosRequestConfig;

  if (error.response?.status === 401 && !originalRequest._retry) {
    if (isRefreshing) {
      return new Promise((resolve) => {
        subscribeTokenRefresh((newToken) => {
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers.Authorization = `Bearer ${newToken}`;
          resolve(api(originalRequest));
        });
      });
    }

    originalRequest._retry = true;
    isRefreshing = true;

    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new TokenMissingException('refresh');
      }

      if (!isValidJwt(refreshToken)) {
        throw new InvalidTokenFormatException('refresh');
      }

      const refreshResponse = await api.post<ApiResponse<{
        accessToken: string;
        refreshToken: string;
      }>>('/auth/refresh-token', { refreshToken });

      if (refreshResponse.data.success) {
        const { accessToken: newToken, refreshToken: newRefreshToken } = refreshResponse.data.data;

        if (!isValidJwt(newToken)) {
          throw new InvalidTokenFormatException('access');
        }

        if (!isValidJwt(newRefreshToken)) {
          throw new InvalidTokenFormatException('refresh');
        }

        localStorage.setItem('accessToken', newToken);
        localStorage.setItem('refreshToken', newRefreshToken);

        originalRequest.headers = originalRequest.headers || {};
        originalRequest.headers.Authorization = `Bearer ${newToken}`;

        onRefreshed(newToken);
        return api(originalRequest);
      } else {
        throw new TokenRefreshException(refreshResponse.data.message);
      }
    } catch (refreshError) {
      if (refreshError instanceof TokenException) {
        console.error('Token refresh failed:', refreshError.message);
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '/login';
      }
      return Promise.reject(refreshError);
    } finally {
      isRefreshing = false;
    }
  }

  return Promise.reject(error);
};

// 定义错误响应数据类型
interface ErrorResponseData {
  message?: string;
  error?: string;
}

api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    try {
      if (error.response?.status === 401) {
        return await handleUnauthorizedError(error);
      }

      const responseData = error.response?.data as ErrorResponseData | undefined;
      const message = responseData?.message || responseData?.error || 'Network Error';
      const status = error.response?.status || 500;

      throw new ApiBusinessException(status, message, error);
    } catch (appError) {
      return Promise.reject(appError);
    }
  }
);

export const apiRequest = async <T>(
  config: AxiosRequestConfig
): Promise<ApiResponse<T>> => {
  try {
    const response = await api(config);
    return response.data as ApiResponse<T>;
  } catch (error) {
    if (error instanceof AppException) {
      throw error;
    }
    throw new NetworkException('API request failed', error as Error);
  }
};
