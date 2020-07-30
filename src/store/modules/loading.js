let timeout

const state = {
  loading: false
}

const mutations = {
  CHANGE(state, status) {
    state.loading = status
  }
}

const actions = {
  changeLoading({commit}, status) {
    if (timeout) {
      clearTimeout(timeout)
    }
    if (status) {
      timeout = setTimeout(() => {
        commit('CHANGE', true)
      }, 500)
    } else {
      commit('CHANGE', false)
    }
  }
}

const getters = {
  loadingStatus(state) {
    return state.loading
  }
}

export default {
  state,
  mutations,
  actions,
  getters
}
