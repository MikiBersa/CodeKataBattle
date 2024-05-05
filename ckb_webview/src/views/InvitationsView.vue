<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import InviteCardVue from '@/components/InviteCard.vue';
import { computed, ref } from 'vue';
import { AccountType, InviteCard, type InviteStatusUpdateRequest } from '@/util/custom_types';
import { store } from '@/store';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter(); 
const invites = ref<InviteCard[]>([]); 
const account_type = store.getters.getAccountType; 

const API_BASE: string = import.meta.env.VITE_APP_API_BASE; 
const GET_INVITES_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_INVITES_ENDPOINT; 
const UPDATE_INVITE_STATUS_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_UPDATE_INVITE_STATUS_ENDPOINT; 

const educator_invite_card = (invite: InviteCard) => {
    return {
        title: invite.tournament_title,
        message: `${invite.sender} has invited you to manage tournament “${invite.tournament_title}”.`,
        accept_callback: () => acceptInvite(invite),
        reject_callback: () => rejectInvite(invite)
    };
}

const student_invite_card = (invite: InviteCard) => {
    return {
        title: invite.battle_title,
        subtitle: invite.tournament_title,
        message: `${invite.sender} has invited you to their team for the battle “${invite.battle_title}”.`,
        accept_callback: () => acceptInvite(invite),
        reject_callback: () => rejectInvite(invite)
    };
}

const invite_card_adapter = (invite: InviteCard) => {
    return account_type === AccountType.Educator ? educator_invite_card(invite) : student_invite_card(invite); 
}

function sendInviteUpdateRequest(invite: InviteCard, accepted: boolean) {
    const request_body: InviteStatusUpdateRequest = {
        invite_id: invite.invite_id,
        tournament_id: invite.tournament_id,
        accepted: accepted
    }

    axios.post(
        UPDATE_INVITE_STATUS_API_ENDPOINT, 
        request_body, 
        store.getters.getHeaders 
    )
    .then(() => router.go(0)) // reload 
    .catch(e => alert(e)); 
}

function acceptInvite(invite: InviteCard) {
    sendInviteUpdateRequest(invite, true); 
}

function rejectInvite(invite: InviteCard) {
    sendInviteUpdateRequest(invite, false); 
}

const adapted_invites = computed(() => {
    return invites.value.map(invite => invite_card_adapter(invite)); 
})

function fetchUserInvites() {
    axios.get<InviteCard[]>(
        GET_INVITES_API_ENDPOINT, 
        store.getters.getHeaders
    ).then((response) => {
        invites.value = response.data; 
    }).catch(e => alert(e)); 
}
fetchUserInvites();
</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <input type="button" value="< Back" @click="$router.go(-1)" style="grid-column: span 2;" />
        <h1 style="grid-column: span 2;">Invitations</h1>
        <div class="cards-wrapper">
            <span v-if="invites.length < 1">No invites</span>
            <InviteCardVue v-for="invite in adapted_invites" :key="invite.title" :invite_info="invite" />
        </div>
    </div>
</template>

<style scoped>
.view-wrapper {
    grid-template-rows: repeat(auto, 3);
}

input[type="button"] {
    max-width: 10rem;
}

.cards-wrapper {
    overflow-y: auto;
    max-height: 60vh;
}
</style>