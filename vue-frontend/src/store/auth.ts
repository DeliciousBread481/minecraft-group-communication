import { defineStore } from 'pinia';
import { useUserStore } from '@/store/user';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('accessToken') || null,
    isAuthenticated: !!localStorage.getItem('accessToken')
  }),

  actions: {
    /**
     * 初始化认证状态
     */
    init() {
      const token = localStorage.getItem('accessToken');
      if (token) {
        this.token = token;
        this.isAuthenticated = true;
      }
    },

    /**
     * 设置认证令牌
     * @param token 认证令牌
     */
    setToken(token: string) {
      this.token = token;
      this.isAuthenticated = true;
      localStorage.setItem('accessToken', token);
    },

    /**
     * 清除认证令牌
     */
    clearToken() {
      this.token = null;
      this.isAuthenticated = false;
      localStorage.removeItem('accessToken');
    },

    /**
     * 刷新访问令牌
     */
    async refreshToken(): Promise<boolean> {
      const userStore = useUserStore();
      return await userStore.refreshToken();
    },

    /**
     * 用户登出
     */
    async logout() {
      try {
        const userStore = useUserStore();
        await userStore.logout();
      } finally {
        this.clearToken();
      }
    }
  }
});