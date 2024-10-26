import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
// import { eqManageMenu, eqBtnRole, checkHealth } from "@/api";
// import { useHomeStore } from "@/store";

const routes: Array<RouteRecordRaw> = [];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

//路由守卫
router.beforeEach((to, from, next) => {
  next();
  //   const useStore = useHomeStore();
  //   if (to.path === "/login") return next();
  //   //先校验是否登录如果没有直接打回登录路由
  //   checkHealth().then((res: Result) => {
  //     if (res.code === 888) return next("/login");
  //     //默认空页
  //     if (to.name === undefined) next("/home/nupage");
  //     //获取权限列表
  //     eqManageMenu().then(async (res: Result) => {
  //       if (res.code === 888) return next("/login");
  //       if (to.name == "NoPage") next();
  //       useStore.saveMneus(JSON.stringify(res.data));
  //       if (!hasRoute(res.data, to.path)) next("/home/target");
  //       //获取页面按钮权限
  //       eqBtnRole(to.path).then((res: Result) => {
  //         if (res.code === 888) return next("/login");
  //         useStore.saveBtn(JSON.stringify(res.data));
  //         next();
  //       });
  //       next();
  //     });
  //   });
});

/**
 * 路由校验
 * @param menus 菜单集
 * @param goRoute 跳转路由
 * @returns
 */
const hasRoute = (menus: Array<object>, goRoute: string): boolean => {
  for (let i = 0; i < menus.length; i++) {
    const item = menus[i] as any;
    if (item.childrenMenu.length != 0)
      if (hasRoute(item.childrenMenu, goRoute)) return true;
    if (item.menuRoute == goRoute) return true;
  }
  return false;
};
export default router;
