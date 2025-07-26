import api from '@/utils/api';
import { handleResponse } from '@/utils/api';
import type {
  UserInfoApiResponse,
  VoidApiResponse,
  StringApiResponse,
  UpdateUserInfo, UserInfoData
} from '@/types/api'

/**
 * 获取当前登录用户信息
 * GET /api/user/me
 */
export const getCurrentUser = async (): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.get('/user/me');
    return handleResponse<UserInfoData>(response);
  } catch (error: any) {
    throw new Error('获取用户信息失败');
  }
};

/**
 * 更新当前用户信息
 * PATCH /api/user/me
 * @param updateData 更新数据 (UserInfo类型)
 */
export const updateUserInfo = async (updateData: UpdateUserInfo): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.patch('/user/me', updateData);
    return handleResponse<UserInfoData>(response);
  } catch (error: any) {
    throw new Error('更新用户信息失败');
  }
};

/**
 * 修改当前用户密码
 * POST /api/user/me/password
 * @param oldPassword 原密码
 * @param newPassword 新密码
 */
export const updatePassword = async (
  oldPassword: string,
  newPassword: string
): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/user/me/password', null, {
      params: {
        oldPassword,
        newPassword
      }
    });
    return handleResponse<void>(response);
  } catch (error: any) {
    throw new Error('修改密码失败');
  }
};

/**
 * 更新用户头像
 * POST /api/user/me/avatar
 * @param file 头像文件
 */
export const updateAvatar = async (file: File): Promise<StringApiResponse> => {
  try {
    const formData = new FormData();
    formData.append('avatar', file);

    const response = await api.post('/user/me/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return handleResponse<string>(response);
  } catch (error: any) {
    throw new Error('更新头像失败');
  }
};

/**
 * 申请成为管理员
 * POST /api/user/apply-for-admin
 * @param reason 申请理由
 */
export const applyForAdminRole = async (reason: string): Promise<VoidApiResponse> => {
  try {
    const response = await api.post('/user/apply-for-admin', null, {
      params: { reason }
    });
    return handleResponse<void>(response);
  } catch (error: any) {
    throw new Error('申请成为管理员失败');
  }
};

/**
 * 根据ID获取用户信息（管理员）
 * GET /api/admin/users/{userId}
 * @param userId 用户ID
 */
export const getUserById = async (userId: number): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.get(`/admin/users/${userId}`);
    return handleResponse<UserInfoData>(response);
  } catch (error: any) {
    throw new Error('获取用户信息失败');
  }
};