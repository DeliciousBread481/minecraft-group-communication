import api, { handleResponse } from '@/utils/api';
import { handleApiError } from '@/utils/message';
import type { ApiResponse } from '@/types/api';

/**
 * 基础API调用方法，统一处理错误和响应
 */
export const apiCall = async <T>(
  apiFunction: () => Promise<any>,
  errorMessage: string
): Promise<T> => {
  const apiResponse = await apiCallWithResponse<T>(apiFunction, errorMessage);
  return apiResponse.data;
};

export const apiCallWithResponse = async <T>(
  apiFunction: () => Promise<any>,
  errorMessage: string
): Promise<ApiResponse<T>> => {
  try {
    const response = await apiFunction();
    return handleResponse(response);
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

export const apiGetWithResponse = async <T>(
  url: string,
  errorMessage: string,
  config?: any
): Promise<ApiResponse<T>> => {
  return apiCallWithResponse<T>(() => api.get(url, config), errorMessage);
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
  return apiCall<T>(() => api.post(url, data, config), errorMessage || '请求失败');
};

export const apiPostWithResponse = async <T>(
  url: string,
  data?: any,
  errorMessage?: string,
  config?: any
): Promise<ApiResponse<T>> => {
  return apiCallWithResponse<T>(() => api.post(url, data, config), errorMessage || '请求失败');
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
  return apiCall<T>(() => api.put(url, data, config), errorMessage || '请求失败');
};

export const apiPutWithResponse = async <T>(
  url: string,
  data?: any,
  errorMessage?: string,
  config?: any
): Promise<ApiResponse<T>> => {
  return apiCallWithResponse<T>(() => api.put(url, data, config), errorMessage || '请求失败');
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
  return apiCall<T>(() => api.patch(url, data, config), errorMessage || '请求失败');
};

export const apiPatchWithResponse = async <T>(
  url: string,
  data?: any,
  errorMessage?: string,
  config?: any
): Promise<ApiResponse<T>> => {
  return apiCallWithResponse<T>(() => api.patch(url, data, config), errorMessage || '请求失败');
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

export const apiDeleteWithResponse = async <T>(
  url: string,
  errorMessage?: string,
  config?: any
): Promise<ApiResponse<T>> => {
  return apiCallWithResponse<T>(() => api.delete(url, config), errorMessage || '删除失败');
};