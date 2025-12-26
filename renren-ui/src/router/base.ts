import Layout from "@/layout/layout.vue";
import Error from "@/views/error.vue";
import { RouteRecordRaw } from "vue-router";
import Login from "@/views/login.vue";
import Iframe from "@/views/iframe.vue";

/**
 * 框架基础路由
 */
const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    component: Layout,
    redirect: "/home",
    meta: { title: "工作台", icon: "icon-desktop" },
    children: [
      {
        path: "/home",
        component: () => import("@/views/home.vue"),
        meta: { title: "主页", icon: "icon-home" }
      }
    ]
  },
  // 添加前台学生router（haili）
    {
    path: "/student",
    component: Layout,
    redirect: "/student/dashboard",
    meta: { title: "学习中心", icon: "icon-education" },
    children: [
      {
        path: "/dashboard",
        component: () => import("@/views/student/student-ui/dashboard.vue"),
        name: "StudentDashboard",
        meta: { title: "学习概览", icon: "icon-dashboard", requiresAuth: true }
      },
      {
        path: "/questionnaire",
        component: () => import("@/views/student/student-ui/questionnaire.vue"),
        name: "StudentQuestionnaire",
        meta: { title: "学情诊断", icon: "icon-form", requiresAuth: true }
      },
      {
        path: "/study-plan",
        component: () => import("@/views/student/student-ui/study-plan.vue"),
        name: "StudentStudyPlan",
        meta: { title: "我的学习路径", icon: "icon-tree", requiresAuth: true, autoRefresh: true }
      },
      {
        path: "/resources",
        component: () => import("@/views/student/student-ui/resources.vue"),
        name: "StudentResources",
        meta: { title: "学习资源", icon: "icon-link", requiresAuth: true }
      }
    ]
  },
  {
    path: "/login",
    component: Login,
    meta: { title: "登录", isNavigationMenu: false }
  },
  {
    path: "/user/password",
    component: () => import("@/views/sys/user-update-password.vue"),
    meta: { title: "修改密码", requiresAuth: true, isNavigationMenu: false }
  },
  {
    path: "/iframe/:id?",
    component: Iframe,
    meta: { title: "iframe", isNavigationMenu: false }
  },
  {
    path: "/error",
    name: "error",
    component: Error,
    meta: { title: "错误页面", isNavigationMenu: false }
  },
  {
    path: "/:path(.*)*",
    redirect: { path: "/error", query: { to: 404 }, replace: true },
    meta: { isNavigationMenu: false }
  }
];

export default routes;
