// ===================== 通用响应类型 =====================
export interface ApiResponse<T = any> {
  success: boolean;
  status: number;
  code?: number;
  message: string;
  data: T;
}

// ===================== 鉴权相关类型 =====================
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

// ===================== 用户信息相关类型 =====================
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

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// ===================== 解决方案相关类型 =====================
export interface SolutionDTO {
  id: string;
  title: string;
  categoryId: string;
  categoryName: string;
  difficulty: string;
  version: string;
  updateTime: string;
  description: string;
  notes: string;
  steps: string[];
  images: string[];
}

export interface SolutionCreateDTO {
  categoryId: string;
  title: string;
  difficulty: string;
  version: string;
  description: string;
  notes: string;
  steps: string[];
  imageFiles: File[];
}

export interface SolutionUpdateDTO {
  title: string;
  difficulty: string;
  version: string;
  description: string;
  notes: string;
  steps: string[];
  imageFiles: File[];
  existingImageUrls: string[];
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
  applicantUsername: string;
  status: string;
  reason: string;
  feedback: string;
  processorUsername: string;
  createdAt: string;
  processedAt: string;
}

export interface AdminApplicationStatus {
  id: number;
  applicantId: number;
  status: string;
  reason: string;
  feedback: string;
  createdAt: string;
}

// ===================== API 响应类型 =====================
export type AuthApiResponse = ApiResponse<AuthResponseData>;
export type UserInfoApiResponse = ApiResponse<UserInfoData>;
export type VoidApiResponse = ApiResponse<void>;
export type StringApiResponse = ApiResponse<string>;

export type SolutionApiResponse = ApiResponse<SolutionDTO>;
export type SolutionListApiResponse = ApiResponse<PageData<SolutionDTO>>;
export type CategoryListApiResponse = ApiResponse<CategoryDTO[]>;
export type AdminApplicationPageApiResponse = ApiResponse<PageData<AdminApplicationDTO>>;
export type AdminApplicationStatusApiResponse = ApiResponse<AdminApplicationStatus>;
export type SolutionPageApiResponse = ApiResponse<PageData<SolutionDTO>>;
export type UserPageApiResponse = ApiResponse<PageData<UserInfoData>>;

// ===================== 分页数据结构 =====================
export interface PageData<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}
