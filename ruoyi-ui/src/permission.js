import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { getFinanceToken } from '@/utils/financeAuth'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/finance/login', '/finance/register'] // 免登录白名单

router.beforeEach((to, from, next) => {
  NProgress.start()
  
  // 判断是否是财务系统路由
  const isFinanceRoute = to.path.startsWith('/finance/')
  const isFinanceLogin = to.path === '/finance/login'
  const isFinanceRegister = to.path === '/finance/register'
  
  // 调试信息
  console.log('路由权限判断：', {
    path: to.path,
    isFinanceRoute,
    hasMainToken: !!getToken(),
    hasFinanceToken: !!getFinanceToken()
  });
  
  if (isFinanceRoute && !isFinanceLogin && !isFinanceRegister) {
    // 财务系统路由处理
    if (getToken() && !getFinanceToken()) {
      // 如果有原系统token但没有财务系统token，初始化财务用户信息
      console.log('使用原系统token访问财务系统，初始化财务用户信息');
      store.dispatch('UseMainToken').then(() => {
        console.log('允许访问财务系统路由：', to.path);
        next();
      });
    } else if (getFinanceToken()) {
      // 有财务系统token，直接访问
      console.log('使用财务系统token访问');
      next();
    } else {
      // 没有任何token则跳转到财务登录页面
      console.log('未登录，重定向到财务系统登录页');
      next(`/finance/login?redirect=${to.fullPath}`);
      NProgress.done();
    }
  } else {
    // 常规系统路由处理
    if (getToken()) {
      /* has token*/
      if (to.path === '/login') {
        next({ path: '/' })
        NProgress.done()
      } else {
        if (store.getters.roles.length === 0) {
          isRelogin.show = true
          // 判断当前用户是否已拉取完user_info信息
          store.dispatch('GetInfo').then(() => {
            isRelogin.show = false
            store.dispatch('GenerateRoutes').then(accessRoutes => {
              // 根据roles权限生成可访问的路由表
              router.addRoutes(accessRoutes) // 动态添加可访问路由表
              next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
            })
          }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
        } else {
          next()
        }
      }
    } else {
      // 没有token
      if (whiteList.indexOf(to.path) !== -1) {
        // 在免登录白名单，直接进入
        next()
      } else {
        next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
        NProgress.done()
      }
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})