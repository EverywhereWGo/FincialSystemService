import request from '@/utils/financeRequest'

// 查询分类规则列表
export function listCategoryRule(query) {
  return request({
    url: '/finance/categoryRule/list',
    method: 'get',
    params: query
  })
}

// 查询分类规则详细
export function getCategoryRule(id) {
  return request({
    url: '/finance/categoryRule/' + id,
    method: 'get'
  })
}

// 新增分类规则
export function addCategoryRule(data) {
  return request({
    url: '/finance/categoryRule',
    method: 'post',
    data: data
  })
}

// 修改分类规则
export function updateCategoryRule(data) {
  return request({
    url: '/finance/categoryRule',
    method: 'put',
    data: data
  })
}

// 删除分类规则
export function delCategoryRule(id) {
  return request({
    url: '/finance/categoryRule/' + id,
    method: 'delete'
  })
}

// 导出分类规则
export function exportCategoryRule(query) {
  return request({
    url: '/finance/categoryRule/export',
    method: 'get',
    params: query
  })
}