<script setup lang="ts">
import { AccountType, type CardInfo, type DashboardResponse, type NotificationDetails } from '@/util/custom_types';
import Navbar from '@/components/Navbar.vue';
import NotificationCard from '@/components/NotificationCard.vue';
import MyTournamentCard from '@/components/MyTournamentCard.vue';
import MyGroupCard from '@/components/MyGroupCard.vue';
import { useRouter } from 'vue-router';
import { store } from '@/store';
import axios from 'axios';
import { ref } from 'vue';
import { mapNotificationType } from '@/util';

const router = useRouter()

const account_type: string = store.getters.getAccountType;
const notifications = ref<NotificationDetails[]>([]);
const cards_contents = ref<CardInfo[]>([]);

const API_BASE: string = import.meta.env.VITE_APP_API_BASE; 
const DASHBOARD_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_DASHBOARD_ENDPOINT; 

function loadDashboardData() {
    axios.get<DashboardResponse>(
        DASHBOARD_API_ENDPOINT, 
        store.getters.getHeaders
    ).then((response) => {
        response.data.notifications.forEach(n => {
            // NO context mapping -> would require more info from server
            n.display_type = mapNotificationType(n.type).display
        }); 
        console.dir(response.data)
        notifications.value = response.data.notifications; 
        cards_contents.value = response.data.cards; 
    }).catch(e => alert(e))
}
loadDashboardData(); 
</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <h1>Dashboard</h1>
        <input type="button" @click.prevent="router.push('/tournaments')" value="View Tournaments" />

        <h2>{{ account_type === AccountType.Student ? 'My Groups' : 'My Tournaments' }}</h2>
        <h2>Notifications</h2>

        <div class="left-column">
            <div class="cards-wrapper">
                <span v-if="cards_contents.length < 1">Nothing to see here 😶‍🌫️</span>
                <MyTournamentCard v-if="account_type === AccountType.Educator" v-for="card in cards_contents"
                    :key="card.tournament_title" 
                    :tournament="card" />
                <MyGroupCard v-if="account_type === AccountType.Student" v-for="card in cards_contents"
                    :key="card.tournament_title"
                    :group="card" />
            </div>
        </div>
        <div class="right-column">
            <div class="notifications-wrapper">
                <span v-if="notifications.length < 1">No notifications</span>
                <NotificationCard v-if="notifications.length > 0" v-for="notification in notifications"
                    :key="notification.id"
                    :notification="notification" />
            </div>
            <div class="buttons-wrapper" style="margin-top: 1rem;">
                <input type="button" @click="router.push('/invites')" value="Manage Invitations" />
                <input v-if="account_type == AccountType.Educator" type="button" @click="router.push('/new-tournament')"
                    value="Create new Tournament" />
                <input type="button" :disabled="true" value="View Profile" />
            </div>
        </div>
    </div>
</template>

<style scoped>
.view-wrapper {
    grid-template-rows: repeat(auto, 3);
    gap: 1rem 2rem; 
}

a:not(.buttons-wrapper a) {
    margin: 2rem;
    width: auto;
}

.buttons-wrapper a {
    margin-top: 1rem;
}

.right-column {
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    max-height: 60vh;
}

.notifications-wrapper {
    overflow-y: auto;
    height: 45vh;
}

.left-column {
    max-height: 60vh;
}

.cards-wrapper {
    height: 100%;
    overflow-y: auto;
}

.cards-wrapper div {
    margin-bottom: 1rem;
}

.buttons-wrapper>* {
    margin-bottom: 1rem;
    width: 100%;
}</style>