import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '@/components/layout/BasicLayout.vue'
import HomeView from '@/views/HomeView.vue'
import AuthView from '@/views/AuthView.vue'
import NoticeView from '@/views/NoticeView.vue'
import SolutionView from '@/views/SolutionView.vue'
import { useUserStore } from '@/store/user'
// 设置页面使用懒加载方式导入

const routes = [
  {
    path: '/',
    component: BasicLayout,
    children: [
      {
        path: '',
        name: 'home',
        component: HomeView,
        meta: { title: '首页' }
      },
      {
        path: '/auth',
        name: 'auth',
        component: AuthView,
        meta: {
          fullScreenAuth: true,
          public: true
        }
      },
      {
        path: '/notice',
        name: 'notice',
        component: NoticeView,
        meta: { title: '群公告文档' }
      },
      {
        path: '/solutions',
        name: 'solutions',
        component: SolutionView,
        meta: { title: '解决方案' }
      },
      {
        path: '/settings',
        name: 'settings',
        component: () => import('@/views/SettingsView.vue'),
        meta: { 
          title: '个人设置',
          requiresAuth: true 
        }
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由导航守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  to.matched.some(record => record.meta.public)
// 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - Minecraft 疑难杂症交流群`
  }

  // 已认证用户访问登录页，重定向到首页
  if (to.name === 'auth' && userStore.isAuthenticated) {
    return next('/')
  }

  // 需要认证但未登录
  if (requiresAuth && !userStore.isAuthenticated) {
    next({
      name: 'auth',
      query: { redirect: to.fullPath }
    })
  }
  // 公开路由或已认证
  else {
    next()
  }
})

export default router
