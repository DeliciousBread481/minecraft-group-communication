import { apiGet, apiPost } from '@/utils/apiBase';
import { handleApiError } from '@/utils/message';
import type {
  AdminApplicationDTO,
  PageData,
  SolutionDTO,
  UserInfoData
} from '@/types/api';

// ===================== 用户管理接口 =====================

/**
 * 获取所有用户信息
 * GET /api/developer/users
 */
export const getAllUsers = async (page: number, size: number): Promise<PageData<UserInfoData>> => {
  try {
    return await apiGet<PageData<UserInfoData>>('/developer/users', '获取用户列表失败', { params: { page, size } });
  } catch (error: any) {
    return handleApiError(error, '获取用户列表失败');
  }
};

/**
 * 将普通用户提升为管理员
 * POST /api/developer/users/{userId}/promote
 */
export const promoteToAdmin = async (userId: number): Promise<void> => {
  try {
    await apiPost<void>(`/developer/users/${userId}/promote`, undefined, '提升用户为管理员失败');
  } catch (error: any) {
    return handleApiError(error, '提升用户为管理员失败');
  }
};

/**
 * 撤销用户的管理员权限
 * POST /api/developer/users/{userId}/revoke-admin
 */
export const revokeAdminRole = async (userId: number): Promise<void> => {
  try {
    await apiPost<void>(`/developer/users/${userId}/revoke-admin`, undefined, '撤销管理员权限失败');
  } catch (error: any) {
    return handleApiError(error, '撤销管理员权限失败');
  }
};

// ===================== 管理员申请管理接口 =====================

/**
 * 获取待处理的管理员申请列表
 * GET /api/developer/admin-applications/pending
 */
export const getPendingApplications = async (page: number, size: number): Promise<PageData<AdminApplicationDTO>> => {
  try {
    return await apiGet<PageData<AdminApplicationDTO>>('/developer/admin-applications/pending', '获取申请列表失败', { params: { page, size } });
  } catch (error: any) {
    return handleApiError(error, '获取申请列表失败');
  }
};

/**
 * 批准管理员申请
 * POST /api/developer/admin-applications/{applicationId}/approve
 */
export const approveAdminApplication = async (applicationId: number): Promise<void> => {
  try {
    await apiPost<void>(`/developer/admin-applications/${applicationId}/approve`, undefined, '批准申请失败');
  } catch (error: any) {
    return handleApiError(error, '批准申请失败');
  }
};

/**
 * 拒绝管理员申请
 * POST /api/developer/admin-applications/{applicationId}/reject
 */
export const rejectAdminApplication = async (applicationId: number, feedback: string): Promise<void> => {
  try {
    await apiPost<void>(`/developer/admin-applications/${applicationId}/reject`, null, '拒绝申请失败', {
      params: { feedback }
    });
  } catch (error: any) {
    return handleApiError(error, '拒绝申请失败');
  }
};

// ===================== 解决方案审核接口 =====================

/**
 * 获取待审核的解决方案
 * GET /api/developer/solutions/pending
 */
export const getPendingSolutions = async (page: number, size: number): Promise<PageData<SolutionDTO>> => {
  try {
    return await apiGet<PageData<SolutionDTO>>('/developer/solutions/pending', '获取待审核解决方案失败', { params: { page, size } });
  } catch (error: any) {
    return handleApiError(error, '获取待审核解决方案失败');
  }
};

/**
 * 批准解决方案
 * POST /api/developer/solutions/{solutionId}/approve
 */
export const approveSolution = async (solutionId: string): Promise<void> => {
  try {
    await apiPost<void>(`/developer/solutions/${solutionId}/approve`, undefined, '批准解决方案失败');
  } catch (error: any) {
    return handleApiError(error, '批准解决方案失败');
  }
};

/**
 * 拒绝解决方案
 * POST /api/developer/solutions/{solutionId}/reject
 */
export const rejectSolution = async (solutionId: string, reason: string): Promise<void> => {
  try {
    await apiPost<void>(`/developer/solutions/${solutionId}/reject`, null, '拒绝解决方案失败', { params: { reason } });
  } catch (error: any) {
    return handleApiError(error, '拒绝解决方案失败');
  }
};