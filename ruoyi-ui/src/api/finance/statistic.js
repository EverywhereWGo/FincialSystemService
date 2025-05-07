import request from '@/utils/financeRequest'

// 添加默认参数处理函数
function ensureUserId(params) {
  const data = { ...params };
  if (!data.userId) {
    console.warn('API调用缺少userId参数，使用默认值1');
    data.userId = 1;
  }
  return data;
}

// 获取月度分类支出统计
export function getCategoryStatistic(params) {
  return request({
    url: '/finance/statistic/category',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取年度收支统计
export function getYearStatistic(params) {
  return request({
    url: '/finance/statistic/year',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取收支趋势
export function getTrendStatistic(params) {
  return request({
    url: '/finance/statistic/trend',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取预算执行情况
export function getBudgetStatistic(params) {
  return request({
    url: '/finance/statistic/budget',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取顶部交易记录
export function getTopTransactions(params) {
  return request({
    url: '/finance/statistic/topTransactions',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取月度收支总额
export function getAmountStatistic(params) {
  return request({
    url: '/finance/transaction/stat/amount',
    method: 'get',
    params: ensureUserId(params)
  })
}

// 获取每日交易记录
export function getDailyTransactions(params) {
  return request({
    url: '/finance/transaction/daily',
    method: 'get',
    params: ensureUserId(params)
  })
}