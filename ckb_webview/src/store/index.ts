import { createStore } from 'vuex'
import { AccountType } from '@/util/custom_types'
import VuexPersistence from 'vuex-persist'

function stringToAccountType(value: string): AccountType {
  return Object.values(AccountType).includes(value as AccountType)
    ? (value as AccountType)
    : AccountType.None
}

declare interface State {
  token: string | null
  user_data: {
    account_type: AccountType
    username: string
  }
}

declare interface InvitesState {
  invites: string[]
}

const user_data_vuexLocal = new VuexPersistence<State>({
  storage: window.localStorage
})
export const store = createStore<State>({
  state: {
    token: null,
    user_data: {
      account_type: AccountType.None,
      username: ''
    }
  },
  mutations: {
    setToken(state, token) {
      state.token = token
    },
    setUserData(state, user_data) {
      const account_type: AccountType = stringToAccountType(user_data.account_type)
      state.user_data = {
        account_type: account_type,
        username: user_data.username
      }
    },
    clearToken(state) {
      state.token = null
    }
  },
  actions: {
    saveToken({ commit }, token) {
      commit('setToken', token)
    },
    saveUserData({ commit }, user_data) {
      commit('setUserData', user_data)
    },
    clearToken({ commit }) {
      commit('clearToken')
    }
  },
  getters: {
    getToken: (state) => state.token,
    isLoggedIn: (state) => !!state.token,
    getUsername: (state) => state.user_data.username,
    getAccountType: (state) => state.user_data.account_type,
    getHeaders: (state) => ({
      headers: {
        'Authorization': `Bearer ${state.token}`,
        // 'Content-Type': 'application/json',
        // 'Access-Control-Allow-Origin': '*',
        // 'Origin': 'localhost'
      }
    }),
    getUserData: (state) => state.user_data
  },
  plugins: [user_data_vuexLocal.plugin]
})

export const invites_store = createStore<InvitesState>({
  state: {
    invites: []
  },
  mutations: {
    storeInvites(state, invites) {
      state.invites = invites
    },
    clearInvites(state) {
      state.invites = []
    }
  },
  actions: {
    saveInvites({ commit }, invites) {
      commit('storeInvites', invites)
    },
    clearInvites({ commit }) {
      commit('clearInvites')
    }
  },
  getters: {
    getInvites: (state) => state.invites
  }
})
