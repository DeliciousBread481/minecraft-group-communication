import { apiGet, apiPost, apiPut, apiDelete } from '@/utils/apiBase';
import type {
  UserInfoApiResponse,
  SolutionApiResponse,
  SolutionListApiResponse,
  SolutionCreateDTO,
  SolutionUpdateDTO,
  CategoryListApiResponse,
  VoidApiResponse
} from '@/types/api';

// ===================== 用户管理接口 =====================

/**
 * 根据ID获取用户信息
 * GET /api/admin/users/{userId}
 */
export const getUserById = (userId: number): Promise<UserInfoApiResponse> => {
  return apiGet(`/admin/users/${userId}`, '获取用户信息失败');
};

// ===================== 解决方案管理接口 =====================

/**
 * 创建解决方案
 * POST /api/admin/solutions
 */
export const createSolution = (createDTO: SolutionCreateDTO): Promise<SolutionApiResponse> => {
  return apiPost('/admin/solutions', createDTO, '创建解决方案失败');
};

/**
 * 更新解决方案
 * PUT /api/admin/solutions/{solutionId}
 */
export const updateSolution = (
  solutionId: string,
  updateDTO: SolutionUpdateDTO
): Promise<SolutionApiResponse> => {
  return apiPut(`/admin/solutions/${solutionId}`, updateDTO, '更新解决方案失败');
};

/**
 * 删除解决方案
 * DELETE /api/admin/solutions/{solutionId}
 */
export const deleteSolution = (solutionId: string): Promise<VoidApiResponse> => {
  return apiDelete(`/admin/solutions/${solutionId}`, '删除解决方案失败');
};

/**
 * 提交解决方案审核
 * POST /api/admin/solutions/{solutionId}/submit-review
 */
export const submitSolutionForReview = (solutionId: string): Promise<VoidApiResponse> => {
  return apiPost(`/admin/solutions/${solutionId}/submit-review`, undefined, '提交解决方案审核失败');
};

/**
 * 获取管理员创建的解决方案列表
 * GET /api/admin/solutions/my
 */
export const getAdminSolutions = (
  page: number,
  size: number,
  status?: string
): Promise<SolutionListApiResponse> => {
  const params: Record<string, any> = { page, size };
  if (status) params.status = status;
  
  return apiGet('/admin/solutions/my', '获取解决方案列表失败', { params });
};

/**
 * 获取解决方案详情
 * GET /api/admin/solutions/{solutionId}
 */
export const getSolutionById = (solutionId: string): Promise<SolutionApiResponse> => {
  return apiGet(`/admin/solutions/${solutionId}`, '获取解决方案详情失败');
};

// ===================== 分类管理接口 =====================

/**
 * 获取所有问题分类
 * GET /api/admin/categories
 */
export const getAllCategories = (): Promise<CategoryListApiResponse> => {
  return apiGet('/admin/categories', '获取分类列表失败');
};