<script setup lang="ts">
import { AccountType, type BattleInfo, type LeaderboardEntry, type TournamentGetResponse, type TournamentSubscriberRequest } from '@/util/custom_types';
import Navbar from '@/components/Navbar.vue';
import BattleEntry from '@/components/BattleEntry.vue';
import Leaderboard from '@/components/Leaderboard.vue';
import { useRouter } from 'vue-router';
import { computed, ref, watch } from 'vue';
import { store } from '@/store';
import { dateExpired, toDate, formatDate } from '@/util';
import axios from 'axios';
import debounce from 'lodash.debounce';

const props = defineProps<{
    title: string // tournament title
}>()

const router = useRouter();
const tournament_data = ref<TournamentGetResponse>(); 
const is_open = ref<boolean>(false);
const battles = ref<BattleInfo[]>([]);
const leaderboard = ref<LeaderboardEntry[]>([]); // TODO: test this
const account_type = store.getters.getAccountType;
const query = defineModel<string>('search_query');
const loading = ref<boolean>(false); 

const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const GET_TOURNAMENT_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_TOURNAMENT_ENDPOINT;
const SUBSCRIBE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SUBSCRIBE_TO_TOURNAMENT_ENDPOINT; 
const SEARCH_BATTLE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SEARCH_BATTLE_ENDPOINT; 

const disable_subscribe = computed(() => {
  return (tournament_data.value && dateExpired(tournament_data.value?.subscription_deadline)) || 
    tournament_data.value?.already_subscribed || 
    account_type != AccountType.Student || 
    !is_open.value; 
})

const disable_manage_tournament = computed(() => {
    const current_username: string = store.getters.getUsername; 
    return tournament_data.value?.managers.find(m => m == current_username) == undefined || account_type != AccountType.Educator
})

const getTournament = async () => {
    try {
        const { data, status } = await axios.get<TournamentGetResponse>(
            `${GET_TOURNAMENT_API_ENDPOINT}?tournamentTitle=${encodeURI(props.title)}`, 
            store.getters.getHeaders
        );
        tournament_data.value = data; 
        is_open.value = data._open; // for some readon there is no "is" in front of "_open" in the server's response
        battles.value = data.battles ? data.battles : [];
        leaderboard.value = data.leaderboard ? data.leaderboard : [];  
    } catch (error) {
        router.go(-1);
        alert("An error has occurred");
    }
}
getTournament()

function subscribeToTournamentRequest() {
    const request_body: TournamentSubscriberRequest = {
        title: props.title // tournament title
    }

    if (tournament_data.value?.already_subscribed != undefined)
        tournament_data.value.already_subscribed = true; 

    axios.post(
        SUBSCRIBE_API_ENDPOINT, 
        request_body, 
        store.getters.getHeaders
    )
    .then(() => {
        router.go(0); // reload tournament information
    })
    .catch(e => alert(e.response.data.error_msg))
    // should disable subscribe button after this request. needs a server-side check. 
}

watch(query, debounce(async () => {
    let query_string = ""
    if (query.value) {
       query_string = query.value; 
    } 
    loading.value = true;

    const { data, status } = await axios.get<BattleInfo[]>(
        `${SEARCH_BATTLE_API_ENDPOINT}?tournamentTitle=${props.title}&battleTitle=${encodeURI(query_string)}`, 
        store.getters.getHeaders
    );
    battles.value = data;
    if (data == undefined) {
        battles.value = [];
    }
}, 1000));
</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <div class="grid-wrapper">
            <input type="button" value="< Back" @click="$router.push('/tournaments')" />
            <div class="input-wrapper">
                <input type="search" name="search" id="search" placeholder="Search battles" v-model="query" />
            </div>
        </div>
        <input v-if="!disable_subscribe" type="button" value="Subscribe to Tournament" @click="subscribeToTournamentRequest()" />
        <input v-if="!disable_manage_tournament" type="button" value="Manage Tournament"
            @click="$router.push(`/manage-tournament?title=${title}`)" />

        <h1 style="grid-column: span 2;">{{ title + (is_open ? '' : ' (closed)')}}</h1>
        <div class="left-column">
            <table class="battles-list">
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
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="100%" class="table-hr"></td>
                    </tr>
                    <tr v-if="battles.length < 1">
                        <td>No results found</td>
                    </tr>
                    <BattleEntry v-for="(battle, index) in battles" :key="battle.battle_title + index"
                        :tournament_title="battle.tournament_title"
                        :battle_title="battle.battle_title"
                        :is_open="battle.is_open" 
                        :is_enrollment_open="new Date() < toDate(battle.enrollment_deadline)"
                        :enrollment_deadline="formatDate(battle.enrollment_deadline)"
                        :enrolled_groups="battle.enrolled_groups" :has_background="index % 2 === 0" />
                </tbody>
            </table>
        </div>

        <div class="right-column">
            <Leaderboard :entries="leaderboard" :limit="10" />

            <h2>Badges</h2>
        </div>
    </div>
</template>

<style scoped>
.view-wrapper {
    grid-template-rows: repeat(auto, 3);
}

.grid-wrapper {
    display: grid;
    grid-template-columns: 1fr 4fr;
    gap: 1rem;
}

table {
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

.left-column {
    overflow-y: auto;
    max-height: 60vh;
}
</style>