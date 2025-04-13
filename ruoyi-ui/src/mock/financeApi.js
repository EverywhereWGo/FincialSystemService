import axios from 'axios'
import { getToken } from '@/utils/auth'
import { getFinanceToken } from '@/utils/financeAuth'
import Mock from 'mockjs'

// 仅当有原系统token但没有财务系统token时模拟数据
const shouldMock = () => {
  return getToken() && !getFinanceToken();
}

// 用户列表模拟数据
const mockUserList = () => {
  return {
    code: 200,
    msg: "操作成功",
    total: 2,
    rows: [
      {
        id: 1, 
        username: "admin", 
        nickname: "管理员", 
        name: "系统管理员", 
        email: "admin@example.com", 
        phone: "13800138000", 
        role: "admin", 
        wechat: "", 
        qq: "", 
        lastLoginTime: "2023-01-01 00:00:00", 
        status: "0"
      },
      {
        id: 2, 
        username: "test", 
        nickname: "测试用户", 
        name: "测试用户", 
        email: "test@example.com", 
        phone: "13900139000", 
        role: "user", 
        wechat: "", 
        qq: "", 
        lastLoginTime: "2023-01-01 00:00:00", 
        status: "0"
      }
    ]
  }
}

// 登录历史模拟数据
const mockLoginHistory = () => {
  return {
    code: 200,
    msg: "操作成功",
    total: 2,
    rows: [
      {
        id: 1,
        userId: 1,
        loginTime: "2023-01-01 12:00:00",
        ipAddress: "127.0.0.1",
        deviceInfo: "Chrome on Windows",
        success: true
      },
      {
        id: 2,
        userId: 1,
        loginTime: "2023-01-02 12:00:00",
        ipAddress: "127.0.0.1",
        deviceInfo: "Chrome on Windows",
        success: true
      }
    ]
  }
}

// 分类模拟数据
const mockCategories = () => {
  return {
    code: 200,
    msg: "操作成功",
    data: [
      { id: 1, name: "餐饮", type: 1, icon: "food", color: "#FF5722" },
      { id: 2, name: "购物", type: 1, icon: "shopping", color: "#4CAF50" },
      { id: 3, name: "交通", type: 1, icon: "traffic", color: "#2196F3" },
      { id: 4, name: "住房", type: 1, icon: "house", color: "#9C27B0" },
      { id: 6, name: "工资", type: 2, icon: "salary", color: "#3F51B5" },
      { id: 7, name: "奖金", type: 2, icon: "bonus", color: "#E91E63" }
    ]
  }
}

// 交易记录模拟数据
const mockTransactions = () => {
  return {
    code: 200,
    msg: "操作成功",
    total: 2,
    rows: [
      {
        id: 1,
        userId: 1,
        categoryId: 1,
        categoryName: "餐饮",
        amount: 100.00,
        type: 1,
        transactionTime: Date.now() - 86400000,
        note: "午餐费用"
      },
      {
        id: 2,
        userId: 1,
        categoryId: 6,
        categoryName: "工资",
        amount: 5000.00,
        type: 2,
        transactionTime: Date.now() - 86400000 * 30,
        note: "月薪"
      }
    ]
  }
}

// 预算模拟数据
const mockBudgets = () => {
  return {
    code: 200,
    msg: "操作成功",
    total: 2,
    rows: [
      {
        id: 1,
        userId: 1,
        categoryId: 1,
        categoryName: "餐饮",
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        amount: 1000.00,
        spentAmount: 500.00,
        warningThreshold: 80.00
      },
      {
        id: 2,
        userId: 1,
        categoryId: 2,
        categoryName: "购物",
        year: new Date().getFullYear(),
        month: new Date().getMonth() + 1,
        amount: 2000.00,
        spentAmount: 1000.00,
        warningThreshold: 80.00
      }
    ]
  }
}

// 通知模拟数据
const mockNotifications = () => {
  return {
    code: 200,
    msg: "操作成功",
    total: 1,
    rows: [
      {
        id: 1,
        userId: 1,
        title: "预算提醒",
        content: "您的餐饮预算已经使用了50%",
        type: "budget_warning",
        isRead: 0,
        createTime: "2023-01-01 12:00:00"
      }
    ]
  }
}

// 统计数据模拟
const mockStatistics = () => {
  return {
    code: 200,
    msg: "操作成功",
    data: {
      totalIncome: 10000,
      totalExpense: 5000,
      balance: 5000,
      categories: [
        { categoryId: 1, categoryName: "餐饮", amount: 2000, percentage: 40 },
        { categoryId: 2, categoryName: "购物", amount: 1500, percentage: 30 },
        { categoryId: 3, categoryName: "交通", amount: 1000, percentage: 20 },
        { categoryId: 4, categoryName: "住房", amount: 500, percentage: 10 }
      ]
    }
  }
}

// 使用正则表达式拦截特定的API请求路径
const setupMock = () => {
  // 检查是否需要启用模拟
  if (!shouldMock()) {
    return;
  }

  console.log('启用财务系统API模拟数据');

  // 用户管理接口
  Mock.mock(/\/finance\/user\/list.*/, 'get', mockUserList);
  
  // 登录历史接口
  Mock.mock(/\/finance\/loginHistory\/list.*/, 'get', mockLoginHistory);
  Mock.mock(/\/finance\/loginHistory\/latest.*/, 'get', () => ({
    code: 200,
    msg: "操作成功",
    data: {
      loginTime: "2023-01-02 12:00:00",
      ipAddress: "127.0.0.1",
      deviceInfo: "Chrome on Windows"
    }
  }));
  
  // 分类接口
  Mock.mock(/\/finance\/category\/list.*/, 'get', mockCategories);
  Mock.mock(/\/finance\/category\/type\/.*/, 'get', mockCategories);
  
  // 交易记录接口
  Mock.mock(/\/finance\/transaction\/list.*/, 'get', mockTransactions);
  
  // 预算接口
  Mock.mock(/\/finance\/budget\/list.*/, 'get', mockBudgets);
  
  // 通知接口
  Mock.mock(/\/finance\/notification\/list.*/, 'get', mockNotifications);
  
  // 统计接口
  Mock.mock(/\/finance\/statistic\/.*/, 'get', mockStatistics);
  
  // 通用的增删改接口
  const successResponse = { code: 200, msg: "操作成功" };
  Mock.mock(/\/finance\/user/, 'post', successResponse);
  Mock.mock(/\/finance\/user/, 'put', successResponse);
  Mock.mock(/\/finance\/user\/.*/, 'delete', successResponse);
  
  Mock.mock(/\/finance\/category/, 'post', successResponse);
  Mock.mock(/\/finance\/category/, 'put', successResponse);
  Mock.mock(/\/finance\/category\/.*/, 'delete', successResponse);
  
  Mock.mock(/\/finance\/transaction/, 'post', successResponse);
  Mock.mock(/\/finance\/transaction/, 'put', successResponse);
  Mock.mock(/\/finance\/transaction\/.*/, 'delete', successResponse);
  
  Mock.mock(/\/finance\/budget/, 'post', successResponse);
  Mock.mock(/\/finance\/budget/, 'put', successResponse);
  Mock.mock(/\/finance\/budget\/.*/, 'delete', successResponse);
  
  Mock.mock(/\/finance\/notification/, 'post', successResponse);
  Mock.mock(/\/finance\/notification/, 'put', successResponse);
  Mock.mock(/\/finance\/notification\/.*/, 'delete', successResponse);
  Mock.mock(/\/finance\/notification\/read\/.*/, 'put', successResponse);
}

export default {
  setupMock
}