import Vue from 'vue'
import {cloneDeep} from 'lodash'

const OBJECT_PROTOTYPE = '[object Object]';
const STORE_KEY_USER_ID = 'id';
const STORE_KEY_USER_ACCOUNT = 'account';
const STORE_KEY_USER_NAME = 'username';
const STORE_KEY_CORP_ID = 'corpId';
const STORE_KEY_TOKEN = 'token';

function protoString(obj) {
  return Object.prototype.toString.call(obj)
}

function vueMerge(to, ...origins) {
  origins.forEach(from => {
    for (const key in from) {
      const value = from[key];
      if (to[key] !== undefined) {
        switch (protoString(value)) {
          case OBJECT_PROTOTYPE:
            vueMerge(to[key], value);
            break;
          default:
            to[key] = value;
            break;
        }
      }
    }
  })
}

const ls = window.localStorage;
const state = {
  id: ls.getItem(STORE_KEY_USER_ID),
  account: ls.getItem(STORE_KEY_USER_ACCOUNT),
  username: ls.getItem(STORE_KEY_USER_NAME),
  corpId: ls.getItem(STORE_KEY_CORP_ID),
  token: ls.getItem(STORE_KEY_TOKEN),
};

const bak = cloneDeep(state);
vueMerge(bak, {
  id: '',
  account: '',
  username: '',
  corpId: '',
  token: '',
});

const mutations = {
  SET_USER_INFO(state, user) {
    vueMerge(state, user);
    if (user.id) {
      ls.setItem(STORE_KEY_USER_ID, state.id);
      ls.setItem(STORE_KEY_USER_ACCOUNT, state.account);
      ls.setItem(STORE_KEY_USER_NAME, state.username);
      ls.setItem(STORE_KEY_CORP_ID, state.corpId);
      ls.setItem(STORE_KEY_TOKEN, state.token);
    }
  },
  CLEAR_USER_INFO(state) {
    vueMerge(state, bak);
    ls.setItem(STORE_KEY_USER_ID, '');
    ls.setItem(STORE_KEY_USER_ACCOUNT, '');
    ls.setItem(STORE_KEY_USER_NAME, '');
    ls.setItem(STORE_KEY_CORP_ID, '');
    ls.setItem(STORE_KEY_TOKEN, '');
  },
  GO_INDEX_ERROR(state) {
    // window.location.href = window.location.origin + '#/redirect404'
  }
};

const actions = {
  setUserInfo({commit}, user) {
    commit('SET_USER_INFO', user)
  },
  initUser({getters, commit}) {
    if (getters.userInfo.id) {
      return Vue.prototype.$http.get('', {
        params: {
          id: getters.userInfo.id
        }
      })
        .then(({MemberInfo}) => {
          commit('SET_USER_INFO', MemberInfo)
        }).catch(err => {
          console.log(err)
        })
    }
  },
  doLogout({commit}) {
    commit('CLEAR_USER_INFO')
  },
  urlIndex({commit}) {
    commit('GO_INDEX_ERROR')
  }
};

const getters = {
  userInfo(state) {
    return state
  }
};

export default {
  state,
  mutations,
  actions,
  getters
}
