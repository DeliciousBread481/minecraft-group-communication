import { defineStore } from 'pinia';
import {
  getUserById,
  createSolution,
  updateSolution,
  deleteSolution,
  submitSolutionForReview,
  getAdminSolutions,
  getSolutionById
} from '@/api/admin';
import { getSolutionCategories } from '@/api/solution';
import type {
  UserInfoData,
  SolutionDTO,
  SolutionCreateDTO,
  SolutionUpdateDTO,
  CategoryDTO,
  PageData
} from '@/types/api';
import { showSuccess, safeExecute, showConfirm } from '@/utils/message'
import { normalizePageData } from './payload';

interface AdminState {
  currentUser: UserInfoData | null;
  solutions: SolutionDTO[];
  solutionPage: PageData<SolutionDTO> | null;
  currentSolution: SolutionDTO | null;
  categories: CategoryDTO[];
  loading: boolean;
  currentQuery: {
    page: number;
    size: number;
    status?: string;
  } | null;
}

export const useAdminStore = defineStore('admin', {
  state: (): AdminState => ({
    currentUser: null,
    solutions: [],
    solutionPage: null,
    currentSolution: null,
    categories: [],
    loading: false,
    currentQuery: null
  }),
  actions: {
    async fetchUser(userId: number) {
      this.loading = true;
      try {
        const res = await safeExecute(
          () => getUserById(userId),
          '获取用户信息失败'
        );
        this.currentUser = res ?? null;
      } finally {
        this.loading = false;
      }
    },

    async fetchSolutions(page = 0, size = 10, status?: string) {
      this.loading = true;
      try {
        const res = await safeExecute(
          () => getAdminSolutions(page, size, status),
          '获取解决方案列表失败'
        );
        const pageData = normalizePageData(res);
        this.solutionPage = pageData;
        this.solutions = pageData.content;
        this.currentQuery = { page, size, status };
      } finally {
        this.loading = false;
      }
    },

    async fetchSolutionById(solutionId: string) {
      const res = await safeExecute(
        () => getSolutionById(solutionId),
        '获取解决方案详情失败'
      );
      this.currentSolution = res ?? null;
    },

    async createNewSolution(dto: SolutionCreateDTO) {
      const res = await safeExecute(
        () => createSolution(dto),
        '创建解决方案失败'
      );

      if (res) {
        showSuccess('解决方案创建成功');

        // 如果有当前查询条件，则重新获取数据保持一致性
        if (this.currentQuery) {
          await this.fetchSolutions(
            this.currentQuery.page,
            this.currentQuery.size,
            this.currentQuery.status
          );
        }
        return res ?? null;
      }
      return null;
    },

    async updateExistingSolution(id: string, dto: SolutionUpdateDTO) {
      const res = await safeExecute(
        () => updateSolution(id, dto),
        '更新解决方案失败'
      );

      if (res) {
        showSuccess('解决方案更新成功');
        this.currentSolution = res ?? null;

        // 如果有当前查询条件，则重新获取数据保持一致性
        if (this.currentQuery) {
          await this.fetchSolutions(
            this.currentQuery.page,
            this.currentQuery.size,
            this.currentQuery.status
          );
        }
        return res ?? null;
      }
      return null;
    },

    async deleteExistingSolution(id: string) {
      const confirmed = await showConfirm('确定要删除这个解决方案吗？', '确认删除');
      if (!confirmed) return false;

      const res = await safeExecute(
        () => deleteSolution(id),
        '删除解决方案失败'
      );

      if (res) {
        showSuccess('解决方案删除成功');

        // 如果有当前查询条件，则重新获取数据保持一致性
        if (this.currentQuery) {
          await this.fetchSolutions(
            this.currentQuery.page,
            this.currentQuery.size,
            this.currentQuery.status
          );
        }
        return true;
      }
      return false;
    },

    async submitSolutionReview(id: string) {
      const confirmed = await showConfirm('确定要提交审核吗？', '确认提交');
      if (!confirmed) return false;

      const res = await safeExecute(
        () => submitSolutionForReview(id),
        '提交审核失败'
      );

      if (res) {
        showSuccess('解决方案已提交审核');

        // 如果有当前查询条件，则重新获取数据保持一致性
        if (this.currentQuery) {
          await this.fetchSolutions(
            this.currentQuery.page,
            this.currentQuery.size,
            this.currentQuery.status
          );
        }
        return true;
      }
      return false;
    },

    async fetchAllCategories() {
      const res = await safeExecute(
        () => getSolutionCategories(),
        '获取分类列表失败'
      );

      this.categories = res ?? [];
    },

    // 重置状态
    resetState() {
      this.currentUser = null;
      this.solutions = [];
      this.solutionPage = null;
      this.currentSolution = null;
      this.categories = [];
      this.loading = false;
      this.currentQuery = null;
    }
  }
});