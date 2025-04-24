import request from '@/utils/financeRequest'

// 查询交易记录列表
export function listTransaction(query) {
  return request({
    url: '/finance/transaction/list',
    method: 'get',
    params: query
  })
}

// 查询交易记录详细
export function getTransaction(id) {
  return request({
    url: '/finance/transaction/' + id,
    method: 'get'
  })
}

// 新增交易记录
export function addTransaction(data) {
  return request({
    url: '/finance/transaction',
    method: 'post',
    data: data
  })
}

// 修改交易记录
export function updateTransaction(data) {
  return request({
    url: '/finance/transaction',
    method: 'put',
    data: data
  })
}

// 删除交易记录
export function delTransaction(id) {
  return request({
    url: '/finance/transaction/' + id,
    method: 'delete'
  })
}

// 查询按月交易记录
export function getTransactionsByMonth(userId, startTime, endTime, type) {
  return request({
    url: '/finance/transaction/month',
    method: 'get',
    params: {
      userId,
      startTime,
      endTime,
      type
    }
  })
}

// 查询年度统计数据
export function getYearStat(userId, year) {
  return request({
    url: '/finance/transaction/stat/year',
    method: 'get',
    params: {
      userId,
      year
    }
  })
}

// 查询月度统计数据
export function getMonthStat(userId, startTime, endTime, type) {
  return request({
    url: '/finance/transaction/stat/month',
    method: 'get',
    params: {
      userId,
      startTime,
      endTime,
      type
    }
  })
}

// 查询月度收支总额
export function getMonthAmount(userId, startTime, endTime) {
  return request({
    url: '/finance/transaction/stat/amount',
    method: 'get',
    params: {
      userId,
      startTime,
      endTime
    }
  })
}

// 导出交易记录
export function exportTransaction(query) {
  return request({
    url: '/finance/transaction/export',
    method: 'get',
    params: query
  })
}

// 上传交易票据图片
export function uploadTransactionImage(data) {
  return request({
    url: '/finance/transaction/image',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 