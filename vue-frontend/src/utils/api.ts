import axios, { type AxiosInstance, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/store/auth';
import { ElMessage } from 'element-plus';
import router from '@/router';

// 创建 Axios 实例
const api: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/crashapi/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加认证 token
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (config.method?.toUpperCase() === 'OPTIONS') {
      return config;
    }
    if (config.url?.includes('/auth/refresh-token')) {
      return config;
    }
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  response => response,
  async error => {
    const authStore = useAuthStore();
    const originalRequest = error.config;

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshSuccess = await authStore.refreshToken();

        if (refreshSuccess) {
          const newToken = localStorage.getItem('accessToken');
          if (newToken) {
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
          }
          return api(originalRequest);
        }
      } catch (refreshError) {
        console.error('令牌刷新失败:', refreshError);
        // 刷新失败，执行登出
        await authStore.logout();
        ElMessage.error('会话已过期，请重新登录');
        await router.push({ name: 'auth', query: { redirect: router.currentRoute.value.fullPath } });
      }
    }

    // 处理403禁止访问错误
    if (error.response?.status === 403) {
      ElMessage.error('您没有访问此资源的权限');
    }

    // 处理其他错误
    return Promise.reject(error);
  }
);

/**
 * 处理 API 响应
 * @param response Axios 响应
 * @returns API 响应数据
 */
export function handleResponse<T>(response: AxiosResponse): ApiResponse<T> {
  // 处理空响应的情况
  if (response.status === 204) {
    return {
      success: true,
      status: 204,
      message: 'No Content',
      data: undefined as any
    };
  }

  if (response.data && typeof response.data === 'object') {
    return response.data as ApiResponse<T>;
  }

  throw new Error('Invalid API response');
}

export interface ApiResponse<T = any> {
  success: boolean;
  status: number;
  code?: number;
  message: string;
  data: T;
}

export default api;