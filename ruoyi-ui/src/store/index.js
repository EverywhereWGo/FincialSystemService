import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import dict from './modules/dict'
import user from './modules/user'
import tagsView from './modules/tagsView'
import permission from './modules/permission'
import settings from './modules/settings'
import getters from './getters'
import financeUser from './modules/financeUser'

Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    dict,
    user,
    tagsView,
    permission,
    settings,
    financeUser  // 添加财务系统用户模块
  },
  getters,
  actions: {
    // 使用原系统token访问财务系统
    UseMainToken({ dispatch }) {
      return dispatch('financeUser/UseMainToken')
    }
  }
})

export default store