import request from '@/utils/financeRequest'

// 查询通知列表
export function listNotification(query) {
  return request({
    url: '/finance/notification/list',
    method: 'get',
    params: query
  })
}

// 查询通知详细
export function getNotification(id) {
  return request({
    url: '/finance/notification/' + id,
    method: 'get'
  })
}

// 新增通知
export function addNotification(data) {
  return request({
    url: '/finance/notification',
    method: 'post',
    data: data
  })
}

// 修改通知
export function updateNotification(data) {
  return request({
    url: '/finance/notification',
    method: 'put',
    data: data
  })
}

// 删除通知
export function delNotification(id) {
  return request({
    url: '/finance/notification/' + id,
    method: 'delete'
  })
}

// 查询未读通知
export function getUnreadNotifications() {
  return request({
    url: '/finance/notification/unread',
    method: 'get'
  })
}

// 标记通知为已读
export function markAsRead(id) {
  return request({
    url: '/finance/notification/read/' + id,
    method: 'put'
  })
}

// 标记所有通知为已读
export function markAllAsRead() {
  return request({
    url: '/finance/notification/read/all',
    method: 'put'
  })
}

// 导出通知
export function exportNotification(query) {
  return request({
    url: '/finance/notification/export',
    method: 'get',
    params: query
  })
}