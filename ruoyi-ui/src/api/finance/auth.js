import request from '@/utils/financeRequest'

// 登录方法
export function login(username, password, deviceInfo) {
  return request({
    url: '/finance/auth/login',
    headers: {
      isToken: false
    },
    method: 'post',
    data: { username, password, deviceInfo }
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/finance/auth/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 登出方法
export function logout(token, userId) {
  return request({
    url: '/finance/auth/logout',
    method: 'post',
    data: { token, userId }
  })
}

// 获取用户详细信息
export function getUserInfo(id) {
  return request({
    url: '/finance/user/' + id,
    method: 'get'
  })
}

// 修改用户密码
export function updateUserPassword(data) {
  return request({
    url: '/finance/user/resetPwd',
    method: 'put',
    data: data
  })
}

// 获取用户登录历史
export function getLoginHistory(userId) {
  return request({
    url: '/finance/loginHistory/user',
    method: 'get',
    params: {
      userId
    }
  })
}

// 获取最近一次登录记录
export function getLatestLogin(userId) {
  return request({
    url: '/finance/loginHistory/latest',
    method: 'get',
    params: {
      userId
    }
  })
}

// 注销账号
export function unregister(userId) {
  return request({
    url: '/finance/auth/unregister',
    method: 'post',
    data: { userId }
  })
}