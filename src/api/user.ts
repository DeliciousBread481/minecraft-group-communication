import { apiRequest, isValidJwt } from './index'
import type { ApiResponse } from './index'
import {
  AppException,
  AuthException,
  TokenException,
  TokenRefreshException,
  ApiBusinessException,
  InvalidTokenFormatException,
  TokenMissingException,
  UserNotAuthenticatedException
} from './exceptions'

/**
 * 登录凭证接口
 */
export interface LoginCredentials {
  username: string;
  password: string;
}

/**
 * 用户注册数据接口
 */
export interface RegisterData {
  username: string;
  password: string;
  email: string;
}

/**
 * 用户信息接口
 */
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  avatar?: string;
}

/**
 * 认证令牌接口
 */
export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  message: string;
}

/**
 * 用户登录响应数据结构
 */
export interface LoginResponse extends AuthTokens {
  user: UserInfo;
}

/**
 * 刷新令牌请求接口
 */
export interface RefreshRequest {
  refreshToken: string;
}

/**
 * 用户登录 API
 */
export const login = async (
  credentials: LoginCredentials
): Promise<ApiResponse<LoginResponse>> => {
  try {
    const response = await apiRequest<LoginResponse>({
      method: 'POST',
      url: '/auth/login',
      data: credentials
    });

    if (response.success) {
      const { accessToken, refreshToken } = response.data;

      if (!isValidJwt(accessToken)) {
        throw new InvalidTokenFormatException('access');
      }

      if (!isValidJwt(refreshToken)) {
        throw new InvalidTokenFormatException('refresh');
      }

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);

      if (response.data.user) {
        localStorage.setItem('user', JSON.stringify(response.data.user));
      }
    }

    return response;
  } catch (error) {
    if (error instanceof TokenException) {
      throw new AuthException('Authentication failed due to invalid token', error);
    }
    throw error;
  }
};

/**
 * 刷新访问令牌 API
 */
export const refreshToken = async (): Promise<ApiResponse<AuthTokens>> => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');

    if (!refreshToken) {
      throw new TokenMissingException('refresh');
    }

    if (!isValidJwt(refreshToken)) {
      throw new InvalidTokenFormatException('refresh');
    }

    const response = await apiRequest<AuthTokens>({
      method: 'POST',
      url: '/auth/refresh-token',
      data: { refreshToken } as RefreshRequest
    });

    if (response.success) {
      const { accessToken, refreshToken: newRefreshToken } = response.data;

      if (!isValidJwt(accessToken)) {
        throw new InvalidTokenFormatException('access');
      }

      if (!isValidJwt(newRefreshToken)) {
        throw new InvalidTokenFormatException('refresh');
      }

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
    }

    return response;
  } catch (error) {
    if (error instanceof TokenException) {
      throw new TokenRefreshException('Token refresh failed', error);
    }
    throw error;
  }
};

/**
 * 用户注册 API
 */
export const register = async (
  userData: RegisterData
): Promise<ApiResponse<AuthTokens>> => {
  try {
    const response = await apiRequest<AuthTokens>({
      method: 'POST',
      url: '/auth/register',
      data: userData
    });

    if (response.success) {
      const { accessToken, refreshToken } = response.data;

      if (isValidJwt(accessToken) && isValidJwt(refreshToken)) {
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
      } else {
        throw new InvalidTokenFormatException('access or refresh');
      }
    }

    return response;
  } catch (error) {
    if (error instanceof ApiBusinessException) {
      throw new AppException(`Registration failed: ${error.message}`, error.code, error);
    }
    throw error;
  }
};

/**
 * 令牌撤销 API
 */
export const revokeToken = async (): Promise<ApiResponse> => {
  try {
    const user = localStorage.getItem('user');
    if (!user) throw new UserNotAuthenticatedException();

    const username = JSON.parse(user).username;
    const response = await apiRequest({
      method: 'POST',
      url: '/auth/revoke-token',
      params: { username }
    });

    if (response.success) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      localStorage.removeItem('user');
    }

    return response;
  } catch (error) {
    if (error instanceof AuthException) {
      throw new AppException('Token revocation failed', 401, error);
    }
    throw error;
  }
};

/**
 * 获取当前用户信息 API
 */
export const getCurrentUser = async (): Promise<ApiResponse<UserInfo>> => {
  try {
    return await apiRequest<UserInfo>({
      method: 'GET',
      url: '/user/me'
    });
  } catch (error) {
    if (error instanceof ApiBusinessException) {
      throw new AppException('Failed to fetch user information', error.code, error);
    }
    throw error;
  }
};

/**
 * 更新用户信息 API
 */
export const updateUser = async (
  userData: Partial<UserInfo>
): Promise<ApiResponse<UserInfo>> => {
  try {
    return await apiRequest<UserInfo>({
      method: 'PATCH',
      url: '/user/me',
      data: userData
    });
  } catch (error) {
    if (error instanceof ApiBusinessException) {
      throw new AppException('Failed to update user information', error.code, error);
    }
    throw error;
  }
};

/**
 * 用户注销 API
 */
export const logout = async (): Promise<void> => {
  try {
    try {
      await revokeToken();
    } catch (revokeError) {
      console.warn('Token revocation failed during logout:', revokeError);
    }

    try {
      await apiRequest({
        method: 'POST',
        url: '/auth/logout',
      });
    } catch (logoutError) {
      console.warn('Logout API call failed:', logoutError);
    }
  } catch (error) {
    console.error('Logout process encountered an error:', error);
    throw new AppException('Logout failed', 0, error as Error);
  } finally {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }
};
