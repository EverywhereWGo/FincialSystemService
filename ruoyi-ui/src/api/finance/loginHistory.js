import request from '@/utils/financeRequest'

// 查询登录历史列表
export function listLoginHistory(query) {
  return request({
    url: '/finance/loginHistory/list',
    method: 'get',
    params: query
  })
}

// 查询登录历史详细
export function getLoginHistory(id) {
  return request({
    url: '/finance/loginHistory/' + id,
    method: 'get'
  })
}

// 删除登录历史
export function delLoginHistory(id) {
  return request({
    url: '/finance/loginHistory/' + id,
    method: 'delete'
  })
}

// 导出登录历史
export function exportLoginHistory(query) {
  return request({
    url: '/finance/loginHistory/export',
    method: 'get',
    params: query
  })
}