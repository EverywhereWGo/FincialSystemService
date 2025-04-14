import Vue from 'vue'
import Router from 'vue-router'

/* Layout */
import Layout from '@/layout'

Vue.use(Router)

/**
 * 财务系统路由
 */
export const financeRoutes = [
  {
    path: '/finance/login',
    component: () => import('@/views/finance/login/index'),
    hidden: true
  },
  {
    path: '/finance/register',
    component: () => import('@/views/finance/login/register'),
    hidden: true
  },
  {
    path: '/finance/transaction',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/transaction/index'),
        name: 'Transaction',
        meta: { title: '交易记录', icon: 'money' }
      }
    ]
  },
  {
    path: '/finance/category',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/category/index'),
        name: 'Category',
        meta: { title: '分类管理', icon: 'dict' }
      }
    ]
  },
  {
    path: '/finance/budget',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/budget/index'),
        name: 'Budget',
        meta: { title: '预算管理', icon: 'chart' }
      }
    ]
  },
  {
    path: '/finance/statistics',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/statistics/index'),
        name: 'Statistics',
        meta: { title: '统计报表', icon: 'chart' }
      }
    ]
  },
  {
    path: '/finance/notification',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/notification/index'),
        name: 'Notification',
        meta: { title: '通知管理', icon: 'message' }
      }
    ]
  },
  {
    path: '/finance/profile',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '',
        component: () => import('@/views/finance/profile/index'),
        name: 'FinanceProfile',
        meta: { title: '个人中心', icon: 'user' }
      }
    ]
  }
]
