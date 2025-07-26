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
import type {
  UserInfoData,
  AdminApplicationDTO,
  SolutionDTO,
  PageData
} from '@/types/api';

interface DeveloperState {
  users: UserInfoData[];
  userPage: PageData<UserInfoData> | null;
  pendingApplications: AdminApplicationDTO[];
  applicationPage: PageData<AdminApplicationDTO> | null;
  pendingSolutions: SolutionDTO[];
  solutionPage: PageData<SolutionDTO> | null;
}

export const useDeveloperStore = defineStore('developer', {
  state: (): DeveloperState => ({
    users: [],
    userPage: null,
    pendingApplications: [],
    applicationPage: null,
    pendingSolutions: [],
    solutionPage: null
  }),
  actions: {
    // 获取所有用户
    async fetchAllUsers(page: number = 0, size: number = 10) {
      try {
        const response = await getAllUsers(page, size);
        this.userPage = response.data;
        this.users = response.data.content;
      } catch (error) {
        console.error('获取用户列表失败:', error);
        throw error;
      }
    },
    // 提升用户为管理员
    async promoteUserToAdmin(userId: number) {
      try {
        await promoteToAdmin(userId);
        // 更新用户状态
        const user = this.users.find(u => u.id === userId);
        if (user) {
          if (!user.roles) user.roles = [];
          user.roles.push('ROLE_ADMIN');
        }
      } catch (error) {
        console.error('提升用户为管理员失败:', error);
        throw error;
      }
    },

    // 撤销用户管理员权限
    async revokeAdminFromUser(userId: number) {
      try {
        await revokeAdminRole(userId);
        // 更新用户状态
        const user = this.users.find(u => u.id === userId);
        if (user && user.roles) {
          user.roles = user.roles.filter(role => role !== 'ROLE_ADMIN');
        }
      } catch (error) {
        console.error('撤销管理员权限失败:', error);
        throw error;
      }
    },

    // 获取待处理的管理员申请
    async fetchPendingApplications(page: number = 0, size: number = 10) {
      try {
        const response = await getPendingApplications(page, size);
        this.applicationPage = response.data;
        this.pendingApplications = response.data.content;
      } catch (error) {
        console.error('获取申请列表失败:', error);
        throw error;
      }
    },

    // 批准管理员申请
    async approveApplication(applicationId: number) {
      try {
        await approveAdminApplication(applicationId);
        // 更新申请状态
        const application = this.pendingApplications.find(a => a.id === applicationId);
        if (application) {
          application.status = 'APPROVED';
        }
      } catch (error) {
        console.error('批准申请失败:', error);
        throw error;
      }
    },

    // 拒绝管理员申请
    async rejectApplication(applicationId: number, reason: string) {
      try {
        await rejectAdminApplication(applicationId, reason);
        // 更新申请状态
        const application = this.pendingApplications.find(a => a.id === applicationId);
        if (application) {
          application.status = 'REJECTED';
        }
      } catch (error) {
        console.error('拒绝申请失败:', error);
        throw error;
      }
    },

    // 获取待审核的解决方案
    async fetchPendingSolutions(page: number = 0, size: number = 10) {
      try {
        const response = await getPendingSolutions(page, size);
        this.solutionPage = response.data;
        this.pendingSolutions = response.data.content;
      } catch (error) {
        console.error('获取待审核解决方案失败:', error);
        throw error;
      }
    },

    // 批准解决方案
    async approveSolutionReview(solutionId: string) {
      try {
        await approveSolution(solutionId);
        // 更新解决方案状态
        const solution = this.pendingSolutions.find(s => s.id === solutionId);
        if (solution) {
          solution.status = '已发布';
        }
      } catch (error) {
        console.error('批准解决方案失败:', error);
        throw error;
      }
    },

    // 拒绝解决方案
    async rejectSolutionReview(solutionId: string, reason: string) {
      try {
        await rejectSolution(solutionId, reason);
        // 更新解决方案状态
        const solution = this.pendingSolutions.find(s => s.id === solutionId);
        if (solution) {
          solution.status = '草稿';
        }
      } catch (error) {
        console.error('拒绝解决方案失败:', error);
        throw error;
      }
    }
  }
});