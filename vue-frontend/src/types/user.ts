export interface UserInfo {
  roles?: string[]
}

export const ROLE_DEV = 'ROLE_DEV'
export const ROLE_ADMIN = 'ROLE_ADMIN'
export const ADMIN_ROLES = [ROLE_DEV, ROLE_ADMIN]

export const hasRole = (roles?: string[], targetRole?: string): boolean => {
  if (!targetRole) return false
  return (roles || []).some(role => role?.toUpperCase() === targetRole.toUpperCase())
}

export const hasAnyRole = (roles?: string[], targetRoles: string[] = []): boolean => {
  return targetRoles.some(targetRole => hasRole(roles, targetRole))
}
