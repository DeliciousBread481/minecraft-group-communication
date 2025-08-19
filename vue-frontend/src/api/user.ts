import api, { handleResponse } from '@/utils/api';
import { handleApiError } from '@/utils/message';
import type {
  AdminApplicationStatus,
  AdminApplicationStatusApiResponse,
  StringApiResponse,
  UpdateUserInfo,
  UserInfoApiResponse,
  UserInfoData,
  VoidApiResponse
} from '@/types/api';

/**
 * 获取当前登录用户信息
 * GET /api/user/me
 */
export const getCurrentUser = async (): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.get('/user/me');
    return handleResponse<UserInfoData>(response);
  } catch (error: any) {
    return handleApiError(error, '获取用户信息失败');
  }
};

/**
 * 更新当前用户信息
 * PATCH /api/user/me
 */
export const updateUserInfo = async (updateData: UpdateUserInfo): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.patch('/user/me', updateData);
    return handleResponse<UserInfoData>(response);
  } catch (error: any) {
    return handleApiError(error, '更新用户信息失败');
  }
};

/**
 * 修改当前用户密码
 * POST /api/user/me/password
 */
export const updatePassword = async (
  oldPassword: string,
  newPassword: string
): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/user/me/password', null, {
      params: { oldPassword, newPassword }
    });
    return handleResponse<void>(response);
  } catch (error: any) {
    return handleApiError(error, '修改密码失败');
  }
};

/**
 * 更新用户头像
 * POST /api/user/me/avatar
 */
export const updateAvatar = async (file: File): Promise<StringApiResponse> => {
  try {
    const formData = new FormData();
    formData.append('avatar', file);

    const response = await api.post('/user/me/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
    return handleResponse<string>(response);
  } catch (error: any) {
    return handleApiError(error, '更新头像失败');
  }
};

/**
 * 申请成为管理员
 * POST /api/user/apply-for-admin
 */
export const applyForAdmin = async (reason: string): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/user/apply-for-admin', null, {
      params: { reason }
    });
    return handleResponse<void>(response);
  } catch (error: any) {
    return handleApiError(error, '申请成为管理员失败');
  }
};

/**
 * 获取管理员申请状态
 * GET /api/user/admin-application/status
 */
export const getAdminApplicationStatus = async (): Promise<AdminApplicationStatusApiResponse> => {
  try {
    const response = await api.get('/user/admin-application/status');
    return handleResponse<AdminApplicationStatus>(response);
  } catch (error: any) {
    return handleApiError(error, '获取管理员申请状态失败');
  }
};