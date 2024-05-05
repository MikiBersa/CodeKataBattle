<script setup lang="ts">
import { AccountType, type RegisterRequest } from '@/util/custom_types';
import { computed } from 'vue';
import axios from 'axios';
import { store } from '@/store';
import { useRouter } from 'vue-router';

const router = useRouter();
const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const REGISTER_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_REGISTER_ENDPOINT;

const show_toggle = defineModel<boolean>();
const username = defineModel<string>('username', { default: '' });
const email = defineModel<string>('email', { default: '' });
const password = defineModel<string>('password', { default: '' });
const confirm_password = defineModel<string>('confirm_password', { default: '' });
const account_type = defineModel<string>('account_type', { default: '' });

const valid_form = computed(() => {
    return username.value != ''
        && email.value != ''
        && password.value != ''
        && confirm_password.value != ''
        && account_type.value != ''
        && password.value == confirm_password.value
})

function register() {
    const request_body: RegisterRequest = {
        username: username.value,
        email: email.value,
        password: password.value,
        account_type: account_type.value
    };

    axios.post(REGISTER_API_ENDPOINT, request_body)
        .then((response) => {
            store.dispatch('saveToken', response.data.token);
            store.dispatch('saveUserData', request_body);

            if (store.getters.isLoggedIn) {
                router.push('/dashboard');
            }
        }).catch((e) => {
            alert(e);
            router.push('/');
        });
}
</script>

<template>
    <form class="login-form" @submit.prevent="register">
        <div class="wrapper">
            <h2>Create new account</h2>

            <div class="input-wrapper">
                <label for="username">Username*</label>
                <input type="text" name="username" id="username" placeholder="Insert username" v-model="username" />
                <div class="username-wrapper">
                    <span class="warning">
                        Must not contain special characters ($,%,!,etc.)
                    </span>
                    <span class="info">
                        *This is the public name that will be shown to other users of the platform
                    </span>
                </div>
            </div>

            <div class="input-wrapper">
                <label for="username">E-mail Address</label>
                <input type="text" name="email" id="email" placeholder="Insert e-mail address" v-model="email" />
            </div>

            <div class="input-wrapper">
                <label for="password">Password</label>
                <input v-if="show_toggle" type="text" name="password" id="password" placeholder="Insert password"
                    v-model="password" />
                <input v-else type="password" name="password" id="password" placeholder="Insert password"
                    v-model="password" />
            </div>

            <div class="input-wrapper">
                <label for="confirm_password">Confirm Password</label>
                <input v-if="show_toggle" type="text" name="confirm_password" id="confirm_password"
                    placeholder="Confirm password" v-model="confirm_password" />
                <input v-else type="password" name="confirm_password" id="confirm_password" placeholder="Confirm password"
                    v-model="confirm_password" />
                <div class="show-psw-wrapper">
                    <input type="checkbox" name="show-psw" v-model="show_toggle" /> <label for="show-psw">Show
                        password</label>
                </div>
            </div>

            <div class="account-sel-wrapper">
                <label>Account type</label>
                <div class="radio-wrapper">
                    <div>
                        <input type="radio" :value="AccountType.Student" name="account-sel" v-model="account_type" /> <label
                            for="student">Student
                            Account</label>
                    </div>
                    <div>
                        <input type="radio" :value="AccountType.Educator" name="account-sel" v-model="account_type" />
                        <label for="educator">Educator
                            Account</label>
                    </div>
                </div>
            </div>
            <button type="submit" class="button light-purple-bg" :disabled="!valid_form">Create Account</button>
            <div class="bottom-wrapper">
                <span id="already-registered">Already have an account?</span>
                <a href="/login" id="login">Login</a>
            </div>
        </div>
    </form>
</template>

<style scoped>
h2 {
    font-size: 24px;
    font-weight: bold;
    color: var(--custom-white)
}

.bottom-wrapper a {
    padding: 0;
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

.bottom-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: center;
    gap: 0.5rem;
}

#already-registered {
    opacity: 0.25;
    font-weight: lighter;
}

#login {
    color: var(--custom-white);
    font-weight: bold;
}

.username-wrapper {
    width: 100%;
    display: flex;
    flex-direction: column;
    color: var(--custom-white);
    font-size: 12px;
}

.radio-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}

.radio-wrapper div {
    display: flex;
    flex-direction: row;
    font-weight: lighter;
    width: 50%;
}

label:not(.radio-wrapper label) {
    font-weight: bold;
}
</style>