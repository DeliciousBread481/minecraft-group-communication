import api from '@/utils/api';
import { handleResponse } from '@/utils/api';
import type {
  AdminApplicationPageApiResponse, UserPageApiResponse, SolutionPageApiResponse
} from '@/types/api'

// ===================== 用户管理接口 =====================

/**
 * 获取所有用户信息
 * GET /api/developer/users
 * @param page
 * @param size
 */
export const getAllUsers = async (page: number, size: number): Promise<UserPageApiResponse> => {
  try {
    const response = await api.get('/developer/users', {
      params: { page, size }
    });
    return handleResponse(response);
  } catch (error: any) {
    throw new Error('获取用户列表失败');
  }
};

/**
 * 将普通用户提升为管理员
 * PUT /api/developer/users/{userId}/promote-to-admin
 * @param userId 用户ID
 */
export const promoteToAdmin = async (userId: number): Promise<void> => {
  try {
    await api.put(`/developer/users/${userId}/promote-to-admin`);
  } catch (error: any) {
    throw new Error('提升用户为管理员失败');
  }
};

/**
 * 撤销用户的管理员权限
 * PUT /api/developer/users/{userId}/revoke-admin
 * @param userId 用户ID
 */
export const revokeAdminRole = async (userId: number): Promise<void> => {
  try {
    await api.put(`/developer/users/${userId}/revoke-admin`);
  } catch (error: any) {
    throw new Error('撤销管理员权限失败');
  }
};

// ===================== 管理员申请管理接口 =====================

/**
 * 获取待处理的管理员申请列表
 * GET /api/developer/admin-applications/pending
 * @param page
 * @param size
 */
export const getPendingApplications = async (page: number, size: number): Promise<AdminApplicationPageApiResponse> => {
  try {
    const response = await api.get('/developer/admin-applications/pending', {
      params: { page, size }
    });
    return handleResponse(response);
  } catch (error: any) {
    throw new Error('获取申请列表失败');
  }
};

/**
 * 批准管理员申请
 * PUT /api/developer/admin-applications/{applicationId}/approve
 * @param applicationId 申请ID
 */
export const approveAdminApplication = async (applicationId: number): Promise<void> => {
  try {
    await api.put(`/developer/admin-applications/${applicationId}/approve`);
  } catch (error: any) {
    throw new Error('批准申请失败');
  }
};

/**
 * 拒绝管理员申请
 * PUT /api/developer/admin-applications/{applicationId}/reject
 * @param applicationId 申请ID
 * @param reason 拒绝理由
 */
export const rejectAdminApplication = async (applicationId: number, reason: string): Promise<void> => {
  try {
    await api.put(`/developer/admin-applications/${applicationId}/reject`, null, {
      params: { reason }
    });
  } catch (error: any) {
    throw new Error('拒绝申请失败');
  }
};

// ===================== 解决方案审核接口 =====================

/**
 * 获取待审核的解决方案
 * GET /api/developer/solutions/pending
 * @param page
 * @param size
 */
export const getPendingSolutions = async (page: number, size: number): Promise<SolutionPageApiResponse> => {
  try {
    const response = await api.get('/developer/solutions/pending', {
      params: { page, size }
    });
    return handleResponse(response);
  } catch (error: any) {
    throw new Error('获取待审核解决方案失败');
  }
};

/**
 * 批准解决方案
 * PUT /api/developer/solutions/{solutionId}/approve
 * @param solutionId 解决方案ID
 */
export const approveSolution = async (solutionId: string): Promise<void> => {
  try {
    await api.put(`/developer/solutions/${solutionId}/approve`);
  } catch (error: any) {
    throw new Error('批准解决方案失败');
  }
};

/**
 * 拒绝解决方案
 * PUT /api/developer/solutions/{solutionId}/reject
 * @param solutionId 解决方案ID
 * @param reason 拒绝理由
 */
export const rejectSolution = async (solutionId: string, reason: string): Promise<void> => {
  try {
    await api.put(`/developer/solutions/${solutionId}/reject`, null, {
      params: { reason }
    });
  } catch (error: any) {
    throw new Error('拒绝解决方案失败');
  }
};