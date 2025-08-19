import { defineStore } from 'pinia';
import {
  getAllUsers,
  promoteToAdmin,
  revokeAdminRole,
  getPendingApplications,
  approveAdminApplication,
  rejectAdminApplication,
  getPendingSolutions,
  approveSolution,
  rejectSolution
} from '@/api/developer';
import type { UserInfoData, AdminApplicationDTO, SolutionDTO, PageData } from '@/types/api';

interface DeveloperState {
  users: UserInfoData[];
  userPage: PageData<UserInfoData> | null;
  pendingApplications: AdminApplicationDTO[];
  applicationPage: PageData<AdminApplicationDTO> | null;
  pendingSolutions: SolutionDTO[];
  solutionPage: PageData<SolutionDTO> | null;
  loading: boolean;
  currentUserQuery: {
    page: number;
    size: number;
  } | null;
  currentApplicationQuery: {
    page: number;
    size: number;
  } | null;
  currentSolutionQuery: {
    page: number;
    size: number;
  } | null;
}

export const useDeveloperStore = defineStore('developer', {
  state: (): DeveloperState => ({
    users: [],
    userPage: null,
    pendingApplications: [],
    applicationPage: null,
    pendingSolutions: [],
    solutionPage: null,
    loading: false,
    currentUserQuery: null,
    currentApplicationQuery: null,
    currentSolutionQuery: null
  }),
  actions: {
    async fetchAllUsers(page = 0, size = 10) {
      this.loading = true;
      try {
        const res = await getAllUsers(page, size);
        this.userPage = res.data;
        this.users = res.data.content;
        this.currentUserQuery = { page, size };
      } finally {
        this.loading = false;
      }
    },

    async promoteUserToAdmin(userId: number) {
      await promoteToAdmin(userId);
      // 重新获取用户数据保持一致性
      if (this.currentUserQuery) {
        await this.fetchAllUsers(
          this.currentUserQuery.page,
          this.currentUserQuery.size
        );
      }
    },

    async revokeAdminFromUser(userId: number) {
      await revokeAdminRole(userId);
      // 重新获取用户数据保持一致性
      if (this.currentUserQuery) {
        await this.fetchAllUsers(
          this.currentUserQuery.page,
          this.currentUserQuery.size
        );
      }
    },

    async fetchPendingApplications(page = 0, size = 10) {
      const res = await getPendingApplications(page, size);
      this.applicationPage = res.data;
      this.pendingApplications = res.data.content;
      this.currentApplicationQuery = { page, size };
    },

    async approveApplication(id: number) {
      await approveAdminApplication(id);
      // 从待处理列表中移除已处理的申请
      this.pendingApplications = this.pendingApplications.filter(app => app.id !== id);
    },

    async rejectApplication(id: number, reason: string) {
      await rejectAdminApplication(id, reason);
      // 从待处理列表中移除已处理的申请
      this.pendingApplications = this.pendingApplications.filter(app => app.id !== id);
    },

    async fetchPendingSolutions(page = 0, size = 10) {
      const res = await getPendingSolutions(page, size);
      this.solutionPage = res.data;
      this.pendingSolutions = res.data.content;
      this.currentSolutionQuery = { page, size };
    },

    async approveSolutionReview(id: string) {
      await approveSolution(id);
      // 从待处理列表中移除已处理的解决方案
      this.pendingSolutions = this.pendingSolutions.filter(s => s.id !== id);
    },

    async rejectSolutionReview(id: string, reason: string) {
      await rejectSolution(id, reason);
      // 从待处理列表中移除已处理的解决方案
      this.pendingSolutions = this.pendingSolutions.filter(s => s.id !== id);
    }
  }
});