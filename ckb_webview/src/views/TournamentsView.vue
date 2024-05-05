<script setup lang="ts">
import { type TournamentsListEntry } from '@/util/custom_types.ts'
import Navbar from '@/components/Navbar.vue';
import TournamentEntry from '@/components/TournamentEntry.vue';
import axios from 'axios';
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { store } from '@/store';
import { dateExpired, formatDate } from '@/util';
import debounce from 'lodash.debounce';

const router = useRouter();
const query = defineModel<string>('search_query');
const loading = ref<boolean>(false);
const tournaments = ref<TournamentsListEntry[]>([]);

const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const GET_TOURNAMENTS_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_TOURNAMENTS_ENDPOINT;
const SEARCH_TOURNAMENT_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SEARCH_TOURNAMENT_ENDPOINT;

const getTournaments = async () => {
    try {
        const { data, status } = await axios.get<TournamentsListEntry[]>(
            GET_TOURNAMENTS_API_ENDPOINT, 
            store.getters.getHeaders
        );

        tournaments.value = data;
        if (data == undefined) {
            tournaments.value = [];
        }
    } catch (error) {
        router.go(-1);
        alert("An error has occurred");
    }
}
getTournaments()

watch(query, debounce(async () => {
    if (!query.value || query.value == '') {
        getTournaments(); 
        return; 
    } 
    loading.value = true;

    const { data, status } = await axios.get<TournamentsListEntry[]>(
        `${SEARCH_TOURNAMENT_API_ENDPOINT}?tournamentTitle=${encodeURI(query.value)}`, 
        store.getters.getHeaders
    );
    tournaments.value = data;
}, 1000));
</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <div class="input-wrapper">
            <input type="search" name="search-bar" id="search" placeholder="Search tournaments"
                v-model="query" />
        </div>
        <input type="button" value="Dashboard >" @click.prevent="router.push('/dashboard')"
            style="max-width: 15rem; margin-left: auto;" />

        <h1>Tournaments</h1> <span></span>
        <table class="tournaments-list">
            <thead>
                <tr>
                    <th>Status</th>
                    <th>Title</th>
                    <th>
                        Subscription <br />
                        Deadline
                    </th>
                    <th>
                        Subscribed <br />
                        Students
                    </th>
                    <th>Educators</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <td colspan="100%" class="table-hr"></td>
                </tr>
                <tr v-if="tournaments.length < 1">
                    <td>No results found</td>
                </tr>
                <TournamentEntry v-for="(tournament, index) in tournaments" :id="tournament.title + '' + index"
                    :key="tournament.title"
                    :is_open="tournament.is_open" :title="tournament.title"
                    :is_subscription_open="!dateExpired(tournament.subscription_deadline)"
                    :subscription_deadline="formatDate(tournament.subscription_deadline)"
                    :subscribed_students="tournament.subscribed_students" :educators="tournament.educators.map(e => ({username: e}))"
                    :has_background="index % 2 === 0" />
            </tbody>
        </table>
    </div>
</template>

<style scoped>
table {
    grid-column: span 2;
    border-spacing: 0 0.5rem;
    width: 100%;
}

th {
    text-align: left;
    padding: 0 1rem;
}

.table-hr {
    opacity: 0.5;
    border-bottom: 1px solid var(--custom-white);
}

.view-wrapper {
    grid-template-columns: 1fr 1fr;
    grid-template-rows: repeat(auto, 3);
}
</style>