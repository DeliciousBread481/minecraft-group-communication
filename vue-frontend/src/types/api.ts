export interface ApiResponse<T = any> {
  success: boolean;
  status: number;
  code?: number;
  message: string;
  data: T;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface RefreshRequest {
  refreshToken: string;
}

export interface AuthResponseData {
  accessToken: string;
  refreshToken: string;
}

// 用户信息数据结构
export interface UserInfoData {
  id: number;
  username: string;
  email: string;
  nickname?: string;
  avatar?: string;
  createdAt: string;
  updatedAt: string;
  enabled: boolean;
  roles?: string[];
}

// 更新用户信息类型
export interface UpdateUserInfo {
  id?: number;
  username?: string;
  email?: string;
  nickname?: string;
  avatar?: string;
  createdAt?: string;
  updatedAt?: string;
  enabled?: boolean;
  roles?: string[];
}

// 更新密码参数
export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export type AuthApiResponse = ApiResponse<AuthResponseData>;
export type UserInfoApiResponse = ApiResponse<UserInfoData>;
export type VoidApiResponse = ApiResponse<void>;
export type StringApiResponse = ApiResponse<string>;