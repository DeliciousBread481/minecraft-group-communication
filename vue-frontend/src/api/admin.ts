import api, { handleResponse } from '@/utils/api';
import { handleApiError } from '@/utils/message';
import type {
  UserInfoApiResponse,
  SolutionApiResponse,
  SolutionListApiResponse,
  SolutionCreateDTO,
  SolutionUpdateDTO,
  CategoryListApiResponse
} from '@/types/api';

// ===================== 用户管理接口 =====================

/**
 * 根据ID获取用户信息
 * GET /api/admin/users/{userId}
 */
export const getUserById = async (userId: number): Promise<UserInfoApiResponse> => {
  try {
    const response = await api.get(`/admin/users/${userId}`);
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '获取用户信息失败');
  }
};

// ===================== 解决方案管理接口 =====================

/**
 * 创建解决方案
 * POST /api/admin/solutions
 */
export const createSolution = async (createDTO: SolutionCreateDTO): Promise<SolutionApiResponse> => {
  try {
    const response = await api.post('/admin/solutions', createDTO);
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '创建解决方案失败');
  }
};

/**
 * 更新解决方案
 * PUT /api/admin/solutions/{solutionId}
 */
export const updateSolution = async (
  solutionId: string,
  updateDTO: SolutionUpdateDTO
): Promise<SolutionApiResponse> => {
  try {
    const response = await api.put(`/admin/solutions/${solutionId}`, updateDTO);
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '更新解决方案失败');
  }
};

/**
 * 删除解决方案
 * DELETE /api/admin/solutions/{solutionId}
 */
export const deleteSolution = async (solutionId: string): Promise<void> => {
  try {
    await api.delete(`/admin/solutions/${solutionId}`);
  } catch (error: any) {
    return handleApiError(error, '删除解决方案失败');
  }
};

/**
 * 提交解决方案审核
 * POST /api/admin/solutions/{solutionId}/submit-review
 */
export const submitSolutionForReview = async (solutionId: string): Promise<void> => {
  try {
    await api.post(`/admin/solutions/${solutionId}/submit-review`);
  } catch (error: any) {
    return handleApiError(error, '提交解决方案审核失败');
  }
};

/**
 * 获取管理员创建的解决方案列表
 * GET /api/admin/solutions/my
 */
export const getAdminSolutions = async (
  page: number,
  size: number,
  status?: string
): Promise<SolutionListApiResponse> => {
  try {
    const params: Record<string, any> = { page, size };
    if (status) params.status = status;

    const response = await api.get('/admin/solutions/my', { params });
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '获取解决方案列表失败');
  }
};

/**
 * 获取解决方案详情
 * GET /api/admin/solutions/{solutionId}
 */
export const getSolutionById = async (solutionId: string): Promise<SolutionApiResponse> => {
  try {
    const response = await api.get(`/admin/solutions/${solutionId}`);
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '获取解决方案详情失败');
  }
};

// ===================== 分类管理接口 =====================

/**
 * 获取所有问题分类
 * GET /api/admin/categories
 */
export const getAllCategories = async (): Promise<CategoryListApiResponse> => {
  try {
    const response = await api.get('/admin/categories');
    return handleResponse(response);
  } catch (error: any) {
    return handleApiError(error, '获取分类列表失败');
  }
};