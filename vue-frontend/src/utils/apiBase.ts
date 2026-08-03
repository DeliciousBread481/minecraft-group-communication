import api, { handleResponse } from '@/utils/api';
import { handleApiError } from '@/utils/message';
import type { ApiResponse } from '@/types/api';

const buildRequestConfig = (config?: any, data?: any) => {
  if (!(data instanceof FormData)) {
    return config;
  }

  const requestConfig = { ...(config || {}) };
  requestConfig.headers = {
    ...(requestConfig.headers || {})
  };

  delete requestConfig.headers['Content-Type'];
  delete requestConfig.headers['content-type'];

  return requestConfig;
};

/**
 * 基础API调用方法，统一处理错误和响应
 */
export const apiCall = async <T>(
  apiFunction: () => Promise<any>,
  errorMessage: string
): Promise<T> => {
  try {
    const response = await apiFunction();
    const apiResponse: ApiResponse<T> = handleResponse(response);
    return apiResponse.data;
  } catch (error: any) {
    handleApiError(error, errorMessage);
    throw error; // 重新抛出错误，因为 handleApiError 会抛出
  }
};

/**
 * GET请求封装
 */
export const apiGet = async <T>(
  url: string,
  errorMessage: string,
  config?: any
): Promise<T> => {
  return apiCall<T>(() => api.get(url, config), errorMessage);
};

/**
 * POST请求封装
 */
export const apiPost = async <T>(
 url: string,
 data?: any,
 errorMessage?: string,
 config?: any
): Promise<T> => {
 const requestConfig = buildRequestConfig(config, data);
 return apiCall<T>(() => api.post(url, data, requestConfig), errorMessage || '请求失败');
};

/**
 * PUT请求封装
 */
export const apiPut = async <T>(
  url: string,
  data?: any,
  errorMessage?: string,
  config?: any
): Promise<T> => {
  const requestConfig = buildRequestConfig(config, data);
  return apiCall<T>(() => api.put(url, data, requestConfig), errorMessage || '请求失败');
};

/**
 * PATCH请求封装
 */
export const apiPatch = async <T>(
  url: string,
  data?: any,
  errorMessage?: string,
  config?: any
): Promise<T> => {
  const requestConfig = buildRequestConfig(config, data);
  return apiCall<T>(() => api.patch(url, data, requestConfig), errorMessage || '请求失败');
};

/**
 * DELETE请求封装
 */
export const apiDelete = async <T>(
  url: string,
  errorMessage?: string,
  config?: any
): Promise<T> => {
  return apiCall<T>(() => api.delete(url, config), errorMessage || '删除失败');
};