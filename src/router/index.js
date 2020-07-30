import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/views/login/Login.vue'
import ResetPwd from '@/views/login/ResetPwd.vue'
import MainPage from '@/views/HomePage.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/reset-pwd',
      name: 'ResetPwd',
      component: ResetPwd
    },
    {
      path: '/mainPage',
      name: 'MainPage',
      component: MainPage,
      children: [
        {
          path: '/user',
          name: '用户管理',
          meta: {
            requireAuth: true,
            showSubTitle: false,
          },
          component: resolve => require(['@/views/user/UserManager.vue'], resolve)
        },
        {
          path: '/user/edit/:type/:userId',
          name: '用户编辑',
          meta: {
            requireAuth: true,
            showSubTitle: true,
          },
          component: resolve => require(['@/views/user/UserInfo.vue'], resolve)
        },
        {
          path: '/user/resetPwd',
          name: '重置密码',
          meta: {
            requireAuth: true,
            showSubTitle: true,
          },
          component: resolve => require(['@/views/user/ResetPwd.vue'], resolve)
        },
        {
          path: '/client',
          name: '客户端管理',
          meta: {
            requireAuth: true,
            showSubTitle: false,
          },
          component: resolve => require(['@/views/client/ClientManager.vue'], resolve)
        },
        {
          /*type:1 添加 2 编辑*/
          path: '/client/edit/:type/:deviceId',
          name: '设备管理',
          meta: {
            requireAuth: true,
            showSubTitle: true,
          },
          component: resolve => require(['@/views/client/DeviceInfo.vue'], resolve)
        },
        {
          path: '/client/log/:deviceId',
          name: '设备日志',
          meta: {
            requireAuth: true,
            showSubTitle: true,
          },
          component: resolve => require(['@/views/client/DeviceLog.vue'], resolve)
        },
        {
          path: '/sheet',
          name: '报表',
          meta: {
            requireAuth: true,
            showSubTitle: false,
          },
          component: resolve => require(['@/views/sheet/SheetManager.vue'], resolve)
        },
        {
          path: '/diary',
          name: '日志管理',
          meta: {
            requireAuth: true,
            showSubTitle: false,
          },
          component: resolve => require(['@/views/diary/DiaryManager.vue'], resolve)
        },
      ]
    }
  ]
})
