import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/views/HomeView.vue")
  },
  {
    path: "/notice",
    name: "Notice",
    component: () => import("@/views/NoticeView.vue")
  },
  {
    path: "/user",
    name: "User",
    component: () => import("@/views/UserView.vue")
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router;
