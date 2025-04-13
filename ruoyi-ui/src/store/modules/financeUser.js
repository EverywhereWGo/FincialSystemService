import { login, logout } from '@/api/finance/auth'
import { getFinanceToken, setFinanceToken, removeFinanceToken } from '@/utils/financeAuth'
import { getToken } from '@/utils/auth'
import { getDeviceInfo } from '@/utils/device'

const financeUser = {
  state: {
    token: getFinanceToken(),
    user: JSON.parse(localStorage.getItem('financeUser')) || {},
    expiryTime: localStorage.getItem('financeTokenExpiry') || 0,
    usingMainToken: false // 标记是否使用原系统token
  },

  mutations: {
    SET_FINANCE_TOKEN: (state, token) => {
      state.token = token
    },
    SET_FINANCE_USER: (state, user) => {
      state.user = user
    },
    SET_FINANCE_EXPIRY: (state, expiryTime) => {
      state.expiryTime = expiryTime
    },
    SET_USING_MAIN_TOKEN: (state, status) => {
      state.usingMainToken = status
    }
  },

  actions: {
    // 财务系统登录
    FinanceLogin({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const deviceInfo = userInfo.deviceInfo || getDeviceInfo()
      
      return new Promise((resolve, reject) => {
        login(username, password, deviceInfo).then(res => {
          if (res.code === 200) {
            setFinanceToken(res.data.token)
            commit('SET_FINANCE_TOKEN', res.data.token)
            commit('SET_FINANCE_USER', res.data.user)
            commit('SET_FINANCE_EXPIRY', res.data.expiryTime)
            commit('SET_USING_MAIN_TOKEN', false)
            localStorage.setItem('financeUser', JSON.stringify(res.data.user))
            localStorage.setItem('financeTokenExpiry', res.data.expiryTime)
          }
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 使用原系统token访问财务系统
    UseMainToken({ commit, rootGetters }) {
      return new Promise(resolve => {
        // 默认设置使用主系统token的标志
        commit('SET_USING_MAIN_TOKEN', true);
        // 使用默认token访问API
        resolve();
      })
    },

    // 财务系统登出
    FinanceLogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        // 如果使用的是原系统token，直接清除状态，不调用后端
        if (state.usingMainToken) {
          commit('SET_FINANCE_TOKEN', '')
          commit('SET_FINANCE_USER', {})
          commit('SET_FINANCE_EXPIRY', 0)
          commit('SET_USING_MAIN_TOKEN', false)
          removeFinanceToken()
          localStorage.removeItem('financeUser')
          localStorage.removeItem('financeTokenExpiry')
          resolve()
          return;
        }
        
        // 常规财务系统登出
        logout(state.token, state.user.id).then(() => {
          commit('SET_FINANCE_TOKEN', '')
          commit('SET_FINANCE_USER', {})
          commit('SET_FINANCE_EXPIRY', 0)
          commit('SET_USING_MAIN_TOKEN', false)
          removeFinanceToken()
          localStorage.removeItem('financeUser')
          localStorage.removeItem('financeTokenExpiry')
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端登出
    FinanceFedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_FINANCE_TOKEN', '')
        commit('SET_FINANCE_USER', {})
        commit('SET_FINANCE_EXPIRY', 0)
        commit('SET_USING_MAIN_TOKEN', false)
        removeFinanceToken()
        localStorage.removeItem('financeUser')
        localStorage.removeItem('financeTokenExpiry')
        resolve()
      })
    }
  }
}

export default financeUser