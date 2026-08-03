import { apiGet } from '@/utils/apiBase';
import type {
  CategoryDTO,
  SolutionDTO,
  SolutionPageDTO
} from '@/types/api';

/**
 * 获取已发布的解决方案。
 * GET /api/solutions
 */
export const getPublishedSolutions = (
  page: number,
  size: number
): Promise<SolutionPageDTO> => {
  return apiGet('/solutions', '获取解决方案列表失败', { params: { page, size } });
};

/**
 * 获取已发布解决方案详情。
 * GET /api/solutions/{solutionId}
 */
export const getPublishedSolutionById = (solutionId: string): Promise<SolutionDTO> => {
  return apiGet(`/solutions/${solutionId}`, '获取解决方案详情失败');
};

/**
 * 获取解决方案分类。
 * GET /api/solutions/categories
 */
export const getSolutionCategories = (): Promise<CategoryDTO[]> => {
  return apiGet('/solutions/categories', '获取分类列表失败');
};
