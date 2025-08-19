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

// ===================== 解决方案相关类型 =====================
export interface SolutionDTO {
  id: string;
  categoryId: string;
  categoryName: string;
  title: string;
  difficulty: string;
  version: string;
  description: string;
  notes: string;
  status: string;
  createdAt: string;
  updatedAt: string;
  steps: string[];
  imageUrls: string[];
}

export interface SolutionCreateDTO {
  categoryId: string;
  title: string;
  difficulty: string;
  version: string;
  description: string;
  notes: string;
  steps: string[];
  imageUrls: string[];
}

export interface SolutionUpdateDTO {
  title: string;
  difficulty: string;
  version: string;
  description: string;
  notes: string;
  steps: string[];
  imageUrls: string[];
}

export interface CategoryDTO {
  id: string;
  name: string;
  icon: string;
  description: string;
  color: string;
}

// ===================== 管理员申请相关类型 =====================
export interface AdminApplicationDTO {
  id: number;
  userId: number;
  username: string;
  email: string;
  status: string;
  processorUsername: string;
  createdAt: string;
  processedAt: string;
}

export interface AdminApplicationStatus {
  id: number;
  userId: number;
  status: string;
  reason: string;
  feedback?: string | null;
  createdAt: string;
  updatedAt: string;
}


// ===================== API响应类型 =====================
export type SolutionApiResponse = ApiResponse<SolutionDTO>;
export type SolutionListApiResponse = ApiResponse<PageData<SolutionDTO>>;
export type CategoryListApiResponse = ApiResponse<CategoryDTO[]>;
export type AdminApplicationPageApiResponse = ApiResponse<PageData<AdminApplicationDTO>>;
export type AdminApplicationStatusApiResponse = ApiResponse<AdminApplicationStatus>;
export type SolutionPageApiResponse = ApiResponse<PageData<SolutionDTO>>;
export type UserPageApiResponse = ApiResponse<PageData<UserInfoData>>;

// 分页数据结构
export interface PageData<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}