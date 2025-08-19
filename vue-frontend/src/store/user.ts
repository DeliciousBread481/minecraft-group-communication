import { defineStore } from 'pinia';
import {
  login as apiLogin,
  logout as apiLogout,
  refreshToken as apiRefreshToken,
  register as apiRegister
} from '@/api/auth';
import { getCurrentUser } from '@/api/user';
import type {
  LoginRequest,
  RegisterRequest,
  AuthApiResponse,
  UserInfoApiResponse
} from '@/types/api';

interface UserState {
  isAuthenticated: boolean;
  accessToken: string | null;
  refreshTokenValue: string | null;
  userInfo: any | null;
  lastUserInfoFetch: number | null;
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    isAuthenticated: false,
    accessToken: localStorage.getItem('accessToken') || null,
    refreshTokenValue: localStorage.getItem('refreshToken') || null,
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    lastUserInfoFetch: null
  }),

  actions: {
    /**
     * 初始化用户状态
     */
    init() {
      this.isAuthenticated = !!this.accessToken;

      // 自动获取用户信息（如果超过5分钟未获取）
      const fiveMinutesAgo = Date.now() - 5 * 60 * 1000;
      if (this.isAuthenticated && (!this.lastUserInfoFetch || this.lastUserInfoFetch < fiveMinutesAgo)) {
        this.fetchUserInfo();
      }
    },

    /**
     * 用户登录
     * @param credentials 登录凭据
     */
    async login(credentials: LoginRequest): Promise<boolean> {
      try {
        const response: AuthApiResponse = await apiLogin(credentials);
        const authData = response.data;
        const { accessToken, refreshToken } = authData;

        // 保存令牌
        this.accessToken = accessToken;
        this.refreshTokenValue = refreshToken;
        this.isAuthenticated = true;

        // 保存到 localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        // 获取并保存用户信息
        await this.fetchUserInfo();

        return true;
      } catch (error) {
        console.error('登录失败:', error);
        this.clearLoginError();
        return false;
      }
    },

    /**
     * 用户注册
     * @param userData 注册数据
     */
    async register(userData: RegisterRequest): Promise<boolean> {
      try {
        const response: AuthApiResponse = await apiRegister(userData);
        const authData = response.data;
        const { accessToken, refreshToken } = authData;

        // 保存令牌
        this.accessToken = accessToken;
        this.refreshTokenValue = refreshToken;
        this.isAuthenticated = true;

        // 保存到 localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        // 获取并保存用户信息
        await this.fetchUserInfo();

        return true;
      } catch (error) {
        console.error('注册失败:', error);
        this.clearLoginError();
        return false;
      }
    },

    /**
     * 获取用户信息
     */
    async fetchUserInfo(): Promise<void> {
      try {
        const response: UserInfoApiResponse = await getCurrentUser();
        this.userInfo = response.data;
        this.lastUserInfoFetch = Date.now();
        localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
      } catch (error) {
        console.error('获取用户信息失败:', error);
        this.clearUserData();
      }
    },

    /**
     * 刷新访问令牌
     */
    async refreshToken(): Promise<boolean> {
      if (!this.refreshTokenValue) {
        await this.logout();
        return false;
      }

      try {
        const response: AuthApiResponse = await apiRefreshToken({
          refreshToken: this.refreshTokenValue
        });
        const authData = response.data;
        const { accessToken, refreshToken } = authData;

        // 更新令牌
        this.accessToken = accessToken;
        this.refreshTokenValue = refreshToken;

        // 保存到 localStorage
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);

        return true;
      } catch (error) {
        console.error('刷新令牌失败:', error);
        await this.logout();
        return false;
      }
    },

    /**
     * 用户登出
     */
    async logout(): Promise<void> {
      try {
        if (this.isAuthenticated) {
          await apiLogout();
        }
      } catch (error) {
        console.error('登出失败:', error);
      } finally {
        this.clearUserData();
      }
    },

    /**
     * 清除用户数据
     */
    clearUserData(): void {
      this.isAuthenticated = false;
      this.accessToken = null;
      this.refreshTokenValue = null;
      this.userInfo = null;
      this.lastUserInfoFetch = null;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('userInfo');
    },

    /**
     * 清除登录错误状态
     */
    clearLoginError(): void {
      this.accessToken = null;
      this.refreshTokenValue = null;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
    }
  }
});