import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";

const routes: Array<RouteRecordRaw> = [];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

//路由守卫
router.beforeEach((to, from, next) => {
  next();
});

export default router;
