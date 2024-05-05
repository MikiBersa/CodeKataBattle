<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import InviteSearch from '@/components/InviteSearch.vue';
import { computed } from 'vue';
import { invites_store, store } from '@/store';
import { dateExpired, toDate } from '@/util';
import { useRouter } from 'vue-router';
import axios from 'axios'
import type { TournamentCreationRequest } from '@/util/custom_types';

const router = useRouter();
const title = defineModel<string>('title', { default: '' });
const subscription_deadline = defineModel<string>('sub_deadline', { default: '' });

const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const SEARCH_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SEARCH_USER_ENDPOINT;
const CREATE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_CREATE_TOURNAMENT_ENDPOINT;

const valid_input = computed(() => {
    return title.value != '' && subscription_deadline.value != '' && !dateExpired(subscription_deadline.value);
});

function submitCreateRequest() {
    const request_body: TournamentCreationRequest = {
        title: title.value,
        subscription_deadline: toDate(subscription_deadline.value).toISOString(),
        invited_managers: invites_store.getters.getInvites
    };

    axios.post(
        CREATE_API_ENDPOINT,
        request_body,
        store.getters.getHeaders 
    ).then(() => {
        invites_store.dispatch('clearInvites');
        router.push(`/tournament?title=${encodeURI(title.value)}`);
    }).catch((e) => alert(e));
}

</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <input type="button" value="< Cancel" @click="$router.go(-1)" class="cancel-button" />
        <h1 style="grid-column: span 2;">Create new Tournament</h1>

        <div style="grid-column: 2; grid-row: 3 / span 9;">
            <label for="search">Invite collaborators</label>
            <InviteSearch :api_url="SEARCH_API_ENDPOINT" placeholder="Search for collaboratos to manage tournament" />
        </div>

        <div class="inputs-wrapper">
            <div class="input-wrapper">
                <label for="title">Tournament title</label>
                <input type="text" name="title" id="title" placeholder="Insert tournament title" v-model="title" />
                <span class="warning">Must not contain special characters ($,%,!,etc.)</span>
            </div>

            <div class="input-wrapper">
                <label for="sub_deadline">Subscription deadline</label>
                <input type="datetime-local" name="sub_deadline" id="sub_deadline" v-model="subscription_deadline" />
            </div>
        </div>

        <h2 style="margin-top: 4rem">Badges</h2>
        <input type="button" :disabled="true" value="+ New Badge" />
        <input type="button" :disabled="!valid_input" style="max-width: 100%;" value="Create Tournament"
            @click="submitCreateRequest" />
    </div>
</template>

<style scoped>
.view-wrapper {
    grid-template-rows: repeat(auto, 9);
    gap: 1rem 3rem;
}

.cancel-button {
    width: auto;
    max-width: 10rem;
    grid-column: span 2;
}

.inputs-wrapper {
    display: grid;
    grid-template-columns: 2fr 1fr;
    gap: 2rem;
}

/* doesn't seem to work */
::-webkit-calendar-picker-indicator {
    filter: invert(1);
}

input {
    color-scheme: dark;
}

input[type="button"]:not(.cancel-button) {
    max-width: 20rem;
}
</style>