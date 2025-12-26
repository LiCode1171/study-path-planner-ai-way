import { IObject } from "@/types/interface";
import { useAppStore } from "@/store";
import { getToken } from "@/utils/cache";
import { getBaseRouteToMeta, registerToRouter } from "@/utils/router";
import NProgress from "nprogress";
import "nprogress/nprogress.css";
import {
  createRouter,
  createWebHashHistory,
  RouteLocationNormalized,
  RouteRecordRaw
} from "vue-router";
import baseRoutes from "./base";
import emits from "@/utils/emits";
import { EMitt } from "@/constants/enum";

interface dynamicRouteParams {
  path: string;
  query?: IObject;
  mete?: IObject;
}

NProgress.configure({ showSpinner: false });

const router = createRouter({
  history: createWebHashHistory(), //createWebHashHistory() hash模式
  routes: baseRoutes
});

// 路由加载前
router.beforeEach((to, from, next) => {
  //外链
  if (to.meta.isNewPage) {
    if (to.query.pop !== "true") {
      next(undefined);
      return false;
    }
  }

  const store = useAppStore();

  //token
  const token = getToken();
  const isPop = to.query.pop === "true"; //新窗口打开内页
  NProgress.start();
  if (to.path !== "/login") {
    if (store.state.routes.length) {
      if (to.name === "error") {
        const isMatched = autoRegisterDynamicToRouterAndNext(to);
        if (!isMatched) {
          store.updateState({ appIsRender: true, appIsReady: true });
          next();
        }
      } else {
        if (!to.query.pop) {
          const routeMeta: IObject = store.state.routeToMeta[to.path];
          emits.emit(EMitt.OnPushMenuToTabs, {
            label: to.query._mt || routeMeta.title || to.path,
            value: to.fullPath,
            mete: routeMeta
          });
        }
        store.updateState({ appIsRender: true, appIsReady: true });
        next();
      }
    } else {
      if (token) {
        store.initApp().then((res: Array<RouteRecordRaw>) => {
          const mergeRoute = baseRoutes.concat(res);
          router.options.routes = mergeRoute;
          registerToRouter(router, mergeRoute);
          if (!to.matched.length) {
            registerDynamicToRouterAndNext({ path: to.path, query: to.query });
          }
          store.updateState({
            appIsReady: true,
            routes: mergeRoute,
            routeToMeta: { ...store.state.routeToMeta, ...getBaseRouteToMeta(baseRoutes) }
          });
          setTimeout(() => {
            store.updateState({ appIsRender: true, appIsLogin: true });
          }, 600);
          next({ ...to, replace: true });
        });
      } else {
        if (isPop) {
          if (!to.matched.length) {
            registerDynamicToRouterAndNext({ path: to.path, query: to.query });
            store.updateState({ appIsRender: true, appIsReady: true });
            next(to.fullPath);
          } else {
            store.updateState({ appIsRender: true, appIsReady: true });
            if (to.meta.requiresAuth) {
              next("/login");
            } else {
              next();
            }
          }
        } else {
          next("/login");
        }
      }
    }
  } else {
    store.updateState({ appIsReady: true, appIsRender: true });
    next();
  }
});

// 路由加载后
router.afterEach(() => {
  NProgress.done();
});

// 路由守卫：自动刷新学习计划页面（haili）
router.beforeEach((to, from, next) => {
  // 如果目标路由设置了autoRefresh标记，且是从其他页面跳转过来
  if (to.meta.autoRefresh && to.name === 'StudentStudyPlan' && from.path !== to.path) {
    // 延迟执行，确保组件已经加载
    setTimeout(() => {
      const componentInstance = router.currentRoute.value.matched[0]?.components?.default;
      if (componentInstance && typeof componentInstance === 'object') {
        // 调用组件的loadStudyPlan方法
        const instance = componentInstance as any;
        if (instance.loadStudyPlan) {
          instance.loadStudyPlan();
          console.log('自动刷新学习计划页面');
        }
      }
    }, 100);
  }
  next();
});

// 路由守卫：学习概览页面数据检查（haili）
router.beforeEach((to, from, next) => {
  // 如果访问学习概览页面
  if (to.name === 'StudentDashboard') {
    const token = getToken();
    if (!token && to.meta.requiresAuth) {
      // 如果没有token且需要认证，跳转到登录页
      next('/login');
      return;
    }
  }
  next();
});

/**
 * 获取系统视图路径映射
 * @returns
 */
export const getSysRouteMap = (): IObject => {
  return import.meta.glob("/src/views/**/*.vue");
};

/**
 * 根据路由path转换为系统视图组件路径
 * @param path
 * @returns
 */
export const toSysViewComponentPath = (path: string): string => {
  path = path.replace("_", "-");
  return `/src/views${path}.vue`;
};
/**
 * 自动注册路由
 * @param to
 * @returns
 */
const autoRegisterDynamicToRouterAndNext = (to: RouteLocationNormalized): boolean => {
  if (to.redirectedFrom) {
    const path = to.redirectedFrom.path;
    const component = matchedSysRouteComponent(path);
    if (component) {
      registerToRouter(router, [
        {
          path: path,
          name: path,
          component,
          redirect: ""
        }
      ]);
      router.push(to.redirectedFrom);
      return true;
    }
  }
  return false;
};

/**
 * 寻找视图组件
 * @param path
 * @returns
 */
const matchedSysRouteComponent = (path: string): any => {
  const sysRouteMap = getSysRouteMap();
  const component = sysRouteMap[toSysViewComponentPath(path)];
  if (!component) {
    console.error("实时注册动态路由失败，未找到组件路径", path);
  }
  return component;
};

/**
 * 实时注册动态路由并直接跳转过去
 * @param route
 */
export const registerDynamicToRouterAndNext = (route: dynamicRouteParams): void => {
  const component = matchedSysRouteComponent(route.path);
  const newRoute: RouteRecordRaw = {
    path: route.path,
    name: route.path,
    component,
    redirect: !component ? { path: "/error", query: { to: 404 }, replace: true } : ""
  };
  registerToRouter(router, [newRoute]);
  router.push(route);
};

export default router;
