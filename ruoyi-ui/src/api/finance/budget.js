import request from '@/utils/financeRequest'

// 查询预算列表
export function listBudget(query) {
  return request({
    url: '/finance/budget/list',
    method: 'get',
    params: query
  })
}

// 查询预算详细
export function getBudget(id) {
  return request({
    url: '/finance/budget/' + id,
    method: 'get'
  })
}

// 新增预算
export function addBudget(data) {
  return request({
    url: '/finance/budget',
    method: 'post',
    data: data
  })
}

// 修改预算
export function updateBudget(data) {
  return request({
    url: '/finance/budget',
    method: 'put',
    data: data
  })
}

// 删除预算
export function delBudget(id) {
  return request({
    url: '/finance/budget/' + id,
    method: 'delete'
  })
}

// 查询用户指定月份的预算
export function getBudgetsByMonth(userId, year, month) {
  return request({
    url: '/finance/budget/month',
    method: 'get',
    params: {
      userId,
      year,
      month
    }
  })
}

// 查询超出预警阈值的预算
export function getWarningBudgets(userId, year, month) {
  return request({
    url: '/finance/budget/warning',
    method: 'get',
    params: {
      userId,
      year,
      month
    }
  })
}

// 导出预算
export function exportBudget(query) {
  return request({
    url: '/finance/budget/export',
    method: 'get',
    params: query
  })
}