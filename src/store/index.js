import Vue from 'vue'
import Vuex from 'vuex'
import loading from './modules/loading'
import user from './modules/user'

Vue.use(Vuex)

const store = new Vuex.Store({
  strict: process.env.NODE_ENV !== 'production',
  modules: {
    loading,
    user
  }
})

export default store
