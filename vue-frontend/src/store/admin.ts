import { defineStore } from 'pinia';
import {
  getUserById,
  createSolution,
  updateSolution,
  deleteSolution,
  submitSolutionForReview,
  getAdminSolutions,
  getSolutionById,
  getAllCategories
} from '@/api/admin';
import type {
  UserInfoData,
  SolutionDTO,
  SolutionCreateDTO,
  SolutionUpdateDTO,
  CategoryDTO,
  PageData
} from '@/types/api';

interface AdminState {
  currentUser: UserInfoData | null;
  solutions: SolutionDTO[];
  solutionPage: PageData<SolutionDTO> | null;
  currentSolution: SolutionDTO | null;
  categories: CategoryDTO[];
}

export const useAdminStore = defineStore('admin', {
  state: (): AdminState => ({
    currentUser: null,
    solutions: [],
    solutionPage: null,
    currentSolution: null,
    categories: []
  }),
  actions: {
    // 获取用户信息
    async fetchUser(userId: number) {
      try {
        const response = await getUserById(userId);
        this.currentUser = response.data;
      } catch (error) {
        console.error('获取用户信息失败:', error);
        throw error;
      }
    },

    // 创建解决方案
    async createNewSolution(createDTO: SolutionCreateDTO) {
      try {
        const response = await createSolution(createDTO);
        return response.data;
      } catch (error) {
        console.error('创建解决方案失败:', error);
        throw error;
      }
    },

    // 更新解决方案
    async updateExistingSolution(solutionId: string, updateDTO: SolutionUpdateDTO) {
      try {
        const response = await updateSolution(solutionId, updateDTO);
        this.currentSolution = response.data;
        return response.data;
      } catch (error) {
        console.error('更新解决方案失败:', error);
        throw error;
      }
    },

    // 删除解决方案
    async deleteExistingSolution(solutionId: string) {
      try {
        await deleteSolution(solutionId);
        // 从列表中移除
        this.solutions = this.solutions.filter(s => s.id !== solutionId);
      } catch (error) {
        console.error('删除解决方案失败:', error);
        throw error;
      }
    },

    // 提交解决方案审核
    async submitSolutionReview(solutionId: string) {
      try {
        await submitSolutionForReview(solutionId);
        // 更新状态
        if (this.currentSolution?.id === solutionId) {
          this.currentSolution.status = '待审核';
        }
      } catch (error) {
        console.error('提交解决方案审核失败:', error);
        throw error;
      }
    },

    // 获取解决方案列表
    async fetchSolutions(page: number = 0, size: number = 10, status?: string) {
      try {
        const response = await getAdminSolutions(page, size, status);
        this.solutionPage = response.data;
        this.solutions = response.data.content;
      } catch (error) {
        console.error('获取解决方案列表失败:', error);
        throw error;
      }
    },

    // 获取解决方案详情
    async fetchSolutionById(solutionId: string) {
      try {
        const response = await getSolutionById(solutionId);
        this.currentSolution = response.data;
      } catch (error) {
        console.error('获取解决方案详情失败:', error);
        throw error;
      }
    },

    // 获取所有分类
    async fetchAllCategories() {
      try {
        const response = await getAllCategories();
        this.categories = response.data;
      } catch (error) {
        console.error('获取分类列表失败:', error);
        throw error;
      }
    }
  }
});