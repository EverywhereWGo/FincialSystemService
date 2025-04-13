import request from '@/utils/financeRequest'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/finance/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(id) {
  return request({
    url: '/finance/user/' + id,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/finance/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/finance/user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(id) {
  return request({
    url: '/finance/user/' + id,
    method: 'delete'
  })
}

// 重置用户密码
export function resetUserPwd(id, password) {
  const data = {
    id,
    password
  }
  return request({
    url: '/finance/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 导出用户
export function exportUser(query) {
  return request({
    url: '/finance/user/export',
    method: 'get',
    params: query
  })
}