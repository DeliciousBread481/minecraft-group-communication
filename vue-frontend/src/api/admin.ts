import { apiGet, apiPost, apiPut, apiDelete } from '@/utils/apiBase';
import type {
  PageData,
  SolutionCreateDTO,
  SolutionDTO,
  SolutionUpdateDTO,
  UserInfoData
} from '@/types/api';

const buildSolutionFormData = (dto: SolutionCreateDTO | SolutionUpdateDTO): FormData => {
  const formData = new FormData();

  if ('categoryId' in dto) {
    formData.append('categoryId', dto.categoryId);
  }

  formData.append('title', dto.title);
  formData.append('difficulty', dto.difficulty);
  formData.append('version', dto.version);
  formData.append('description', dto.description);
  formData.append('notes', dto.notes);

  dto.steps.forEach((step) => {
    formData.append('steps', step);
  });

  dto.imageFiles.forEach((file) => {
    formData.append('imageFiles', file);
  });

  if ('existingImageUrls' in dto) {
    dto.existingImageUrls.forEach((url) => {
      formData.append('existingImageUrls', url);
    });
  }

  return formData;
};

// ===================== 用户管理接口 =====================

/**
 * 根据ID获取用户信息
 * GET /api/admin/users/{userId}
 */
export const getUserById = (userId: number): Promise<UserInfoData> => {
  return apiGet(`/admin/users/${userId}`, '获取用户信息失败');
};

// ===================== 解决方案管理接口 =====================

/**
 * 创建解决方案
 * POST /api/admin/solutions
 */
export const createSolution = (createDTO: SolutionCreateDTO): Promise<SolutionDTO> => {
  const formData = buildSolutionFormData(createDTO);
  return apiPost('/admin/solutions', formData, '创建解决方案失败');
};

/**
 * 更新解决方案
 * PUT /api/admin/solutions/{solutionId}
 */
export const updateSolution = (
  solutionId: string,
  updateDTO: SolutionUpdateDTO
): Promise<SolutionDTO> => {
  const formData = buildSolutionFormData(updateDTO);
  return apiPut(`/admin/solutions/${solutionId}`, formData, '更新解决方案失败');
};

/**
 * 删除解决方案
 * DELETE /api/admin/solutions/{solutionId}
 */
export const deleteSolution = (solutionId: string): Promise<void> => {
  return apiDelete(`/admin/solutions/${solutionId}`, '删除解决方案失败');
};

/**
 * 提交解决方案审核
 * POST /api/admin/solutions/{solutionId}/submit-review
 */
export const submitSolutionForReview = (solutionId: string): Promise<void> => {
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
): Promise<PageData<SolutionDTO>> => {
  const params: Record<string, any> = { page, size };
  if (status) params.status = status;

  return apiGet('/admin/solutions/my', '获取解决方案列表失败', { params });
};

/**
 * 获取解决方案详情
 * GET /api/admin/solutions/{solutionId}
 */
export const getSolutionById = (solutionId: string): Promise<SolutionDTO> => {
  return apiGet(`/admin/solutions/${solutionId}`, '获取解决方案详情失败');
};
