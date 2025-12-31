import { apiGet, apiPost, apiPatch } from '@/utils/apiBase';
import type {
  AdminApplicationStatusApiResponse,
  StringApiResponse,
  UpdateUserInfo,
  UserInfoApiResponse,
  VoidApiResponse
} from '@/types/api';

/**
 * 获取当前登录用户信息
 * GET /api/user/me
 */
export const getCurrentUser = (): Promise<UserInfoApiResponse> => {
  return apiGet<UserInfoApiResponse>('/user/me', '获取用户信息失败');
};

/**
 * 更新当前用户信息
 * PATCH /api/user/me
 */
export const updateUserInfo = (updateData: UpdateUserInfo): Promise<UserInfoApiResponse> => {
  return apiPatch<UserInfoApiResponse>('/user/me', updateData, '更新用户信息失败');
};

/**
 * 修改当前用户密码
 * POST /api/user/me/password
 */
export const updatePassword = (
  oldPassword: string,
  newPassword: string
): Promise<VoidApiResponse> => {
  return apiPost<VoidApiResponse>('/user/me/password', null, '修改密码失败', {
    params: { oldPassword, newPassword }
  });
};

/**
 * 更新用户头像
 * POST /api/user/me/avatar
 */
export const updateAvatar = (file: File): Promise<StringApiResponse> => {
  const formData = new FormData();
  formData.append('avatar', file);

  return apiPost<StringApiResponse>('/user/me/avatar', formData, '更新头像失败', {
    headers: { 'Content-Type': 'multipart/form-data' }
  });
};

/**
 * 申请成为管理员
 * POST /api/user/apply-for-admin
 */
export const applyForAdmin = (reason: string): Promise<VoidApiResponse> => {
  return apiPost<VoidApiResponse>('/user/apply-for-admin', null, '申请成为管理员失败', {
    params: { reason }
  });
};

/**
 * 获取管理员申请状态
 * GET /api/user/admin-application/status
 */
export const getAdminApplicationStatus = (): Promise<AdminApplicationStatusApiResponse> => {
  return apiGet<AdminApplicationStatusApiResponse>('/user/admin-application/status', '获取管理员申请状态失败');
};