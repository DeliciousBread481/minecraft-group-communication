// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import BasicLayout from '@/components/layout/BasicLayout.vue'
import HomeView from '@/views/HomeView.vue'
import AuthView from '@/views/AuthView.vue'
import NoticeView from '@/views/NoticeView.vue'
import SolutionView from '@/views/SolutionView.vue'
import { useUserStore } from '@/store/user'
import type { UserInfo } from '@/types/user'
import { ADMIN_ROLES } from '@/types/user'

// 路由数组
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: BasicLayout,
    children: [
      { path: '', name: 'home', component: HomeView, meta: { title: '首页' } },
      { path: '/auth', name: 'auth', component: AuthView, meta: { fullScreenAuth: true, public: true } },
      { path: '/notice', name: 'notice', component: NoticeView, meta: { title: '群公告文档' } },
      { path: '/solutions', name: 'solutions', component: SolutionView, meta: { title: '解决方案' } },
      {
        path: '/settings',
        name: 'settings',
        component: () => import('@/views/SettingsView.vue'),
        meta: { title: '个人设置', requiresAuth: true }
      },
      {
        path: '/admin',
        name: 'admin',
        component: () => import('@/views/AdminView.vue'),
        meta: { title: '管理员面板', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

// 检查管理员权限
const checkAdminPermission = (userInfo?: UserInfo): boolean => {
  const roles: string[] = userInfo?.roles?.map(r => r.toUpperCase()) || []
  return roles.some(r => ADMIN_ROLES.includes(r))
}

// 创建 router
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 导航守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const meta = to.meta

  // 设置页面标题
  if (meta?.title) {
    document.title = `${meta.title} - Minecraft 疑难杂症交流群`
  }

  // 已认证用户访问登录页，重定向首页
  if (to.name === 'auth' && userStore.isAuthenticated) return next('/')

  // 需要管理员权限但用户不是管理员
  if (meta?.requiresAdmin && !checkAdminPermission(userStore.userInfo)) return next('/')

  // 需要认证但未登录
  if (meta?.requiresAuth && !userStore.isAuthenticated) return next({ name: 'auth', query: { redirect: to.fullPath } })

  // 其他情况允许访问
  next()
})

export default router
