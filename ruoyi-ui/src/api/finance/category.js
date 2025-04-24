import request from '@/utils/financeRequest'

// 查询分类列表
export function listCategory(query) {
  return request({
    url: '/finance/category/list',
    method: 'get',
    params: query
  })
}

// 查询分类详细
export function getCategory(id) {
  return request({
    url: '/finance/category/' + id,
    method: 'get'
  })
}

// 新增分类
export function addCategory(data) {
  return request({
    url: '/finance/category',
    method: 'post',
    data: data
  })
}

// 修改分类
export function updateCategory(data) {
  return request({
    url: '/finance/category',
    method: 'put',
    data: data
  })
}

// 删除分类
export function delCategory(id) {
  return request({
    url: '/finance/category/' + id,
    method: 'delete'
  })
}

// 按类型查询分类
export function listCategoryByType(type) {
  return request({
    url: '/finance/category/type/' + type,
    method: 'get'
  })
}

// 导出分类
export function exportCategory(query) {
  return request({
    url: '/finance/category/export',
    method: 'get',
    params: query
  })
}

// 上传分类图标
export function uploadCategoryIcon(data) {
  return request({
    url: '/finance/category/icon',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 