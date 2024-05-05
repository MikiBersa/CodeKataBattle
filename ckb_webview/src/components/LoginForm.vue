<script setup lang="ts">
import { computed } from 'vue'
import axios from 'axios'
import { store } from '@/store'
import { useRouter } from 'vue-router'
import type { LoginRequest } from '@/util/custom_types'

const router = useRouter()
const API_BASE: string = import.meta.env.VITE_APP_API_BASE 
const LOGIN_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_LOGIN_ENDPOINT;
const USER_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_USER_ENDPOINT;

const show_toggle = defineModel<boolean>();
const username = defineModel<string>('username', { default: '' });
const password = defineModel<string>('password', { default: '' });

const valid_form = computed((): boolean => {
  return username.value != '' && password.value != ''
})

function login() {
  const request_body: LoginRequest = {
    email_or_username: username.value,
    password: password.value
  }

  axios
    .post(LOGIN_API_ENDPOINT, request_body, store.getters.getHeaders)
    .then((response) => {
      store.dispatch('saveToken', response.data.token);

      axios.get(USER_API_ENDPOINT, store.getters.getHeaders).then((response) => {
        store.dispatch('saveUserData', response.data); 
      }).catch(e => alert(e)); 

      if (store.getters.isLoggedIn) {
        router.push('/dashboard');
      }
    })
    .catch((e) => {
      alert(`Invalid credentials: ${e}`)
    }); 
}
</script>

<template>
  <form class="login-form" @submit.prevent="login">
    <div class="wrapper">
      <h2>Login to account</h2>
      <div class="input-wrapper">
        <label for="username">Username or E-mail</label>
        <input
          type="text"
          name="username"
          id="username"
          placeholder="Insert username or e-mail"
          v-model="username"
        />
      </div>

      <div class="input-wrapper">
        <label for="password">Password</label>
        <input
          v-if="show_toggle"
          type="text"
          name="password"
          id="password"
          placeholder="Insert password"
          v-model="password"
        />
        <input
          v-else
          type="password"
          name="password"
          id="password"
          placeholder="Insert password"
          v-model="password"
        />
        <div class="show-psw-wrapper">
          <input type="checkbox" name="show-psw" v-model="show_toggle" />
          <label for="show-psw">Show password</label>
        </div>
      </div>

      <button type="submit" class="button light-purple-bg" :disabled="!valid_form">
        Login to Account
      </button>
      <div class="bottom-wrapper">
        <a href="/register" id="forgot-psw">Forgot password?</a>
        <a href="/register" id="register">Register</a>
      </div>
    </div>
  </form>
</template>

<style scoped>
h2 {
  font-size: 24px;
  font-weight: medium;
  color: var(--custom-white);
}

.wrapper {
  color: var(--custom-white);
  background-color: var(--custom-purple2);
  display: grid;
  grid-template-columns: 1fr;
  grid-template-rows: repeat(auto, 5);
  row-gap: 0.5rem;
  padding: 2rem;
  max-width: 450px;
}

.show-psw-wrapper {
  display: flex;
  flex-direction: row;
  font-size: 12px;
  font-weight: lighter;
  align-items: center;
}

.bottom-wrapper a {
  padding: 0;
}

.bottom-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

#forgot-psw {
  font-weight: lighter;
}

#register {
  color: var(--custom-white);
  font-weight: bold;
}
</style>
