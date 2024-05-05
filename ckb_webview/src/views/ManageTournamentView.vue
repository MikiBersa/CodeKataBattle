<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import BattleEntry from '@/components/BattleEntry.vue';
import UsersList from '@/components/UsersList.vue';
import InviteSearch from '@/components/InviteSearch.vue';
import { ref, computed } from 'vue';
import { invites_store, store } from '@/store';
import router from '@/router';
import axios from 'axios';
import { TournamentGetResponse, type BattleInfo, type ManagerInviteRequest, type PendingInvite, type SimpleUserProfile } from '@/util/custom_types';
import { toDate, formatDate, dateExpired } from '@/util';

const creator = ref<string>();
const managers = ref<SimpleUserProfile[]>([]);
const pending_invites = ref<PendingInvite[]>([]);
const battles = ref<BattleInfo[]>([]);
const inviting = ref<boolean>(false);
const is_open = ref<boolean>(false);

const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const SERACH_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SEARCH_USER_ENDPOINT;
const GET_TOURNAMENT_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_TOURNAMENT_ENDPOINT;
const MANAGER_INVITE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_MANAGER_INVITE_ENDPOINT; 
const CLOSE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_CLOSE_TOURNAMENT_ENDPOINT; 

const props = defineProps<{
    title: string, // tournament title
}>();

const adapt_managers = computed(() => {
    return managers.value.map((user) => ({
        name: user.username,
        profile_img_url: user.profile_img_url,
        role: user.username == creator.value?.username ? 'host' : ''
    }));
});

const adapt_invites = computed(() => {
    if (!pending_invites.value) return []; 
    return pending_invites.value.map((user) => ({
        name: user.username
    }));
});

const getTournament = async () => {
    try {
        const { data, status } = await axios.get<TournamentGetResponse>(`${GET_TOURNAMENT_API_ENDPOINT}?tournamentTitle=${encodeURI(props.title)}`, store.getters.getHeaders);
        is_open.value = data._open; // "is" gets cut from the response
        creator.value = data.creator;
        managers.value = data.managers ? data.managers.map(m => ({username: m})) : [];
        pending_invites.value = data.pending_invites; 
        battles.value = data.battles ? data.battles : [];
    } catch (error) {
        router.go(-1);
        alert("An error has occurred");
    }
}
getTournament()

const disable_send_invite = computed(() => {
    return invites_store.getters.getInvites.length <= 0;
});

const clearInviteStorage = () => {
    invites_store.dispatch('clearInvites');
};

function sendInvites() {
  const invites_usernames: string[] = invites_store.getters.getInvites; 
  const invites_to_send: ManagerInviteRequest[] = invites_usernames.map(username => ({
    tournament_title: props.title, 
    username: username
  })); 

  // TODO: change to a signle request -> need to change server route
  invites_to_send.forEach(invite => {
    axios.post(
      MANAGER_INVITE_API_ENDPOINT, 
      invite, 
      store.getters.getHeaders
    ).catch(e => alert(e.response.data.error_msg))
  })

  clearInviteStorage()
  inviting.value = false; 
  router.go(0); 
}

function closeTournamentRequest() {
    axios.post(
        `${CLOSE_API_ENDPOINT}?tournamentTitle=${props.title}`,
        {},
        store.getters.getHeaders
    ).then(() => router.go(0))
    .catch(e => alert(e.response.data.error_msg))
}
</script>

<template>
  <header>
    <Navbar />
  </header>
  <div class="view-wrapper">
    <div class="grid-wrapper">
      <input type="button" value="< Back" @click="$router.go(-1)" />
      <h2>Manage Tournament</h2>
    </div>

    <h1 style="grid-column: span 2">{{ title }}</h1>
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
          <BattleEntry
            v-for="(battle, index) in battles"
            :key="battle.battle_title + index"
            :tournament_title="battle.tournament_title"
            :battle_title="battle.battle_title"
            :is_open="battle.is_open"
            :is_enrollment_open="new Date() < toDate(battle.enrollment_deadline)"
            :enrollment_deadline="formatDate(battle.enrollment_deadline)"
            :enrolled_groups="battle.enrolled_groups"
            :has_background="index % 2 === 0"
          />
        </tbody>
      </table>
    </div>

    <div class="right-column">
      <template v-if="!inviting">
        <input v-if="is_open"
          type="button"
          value="Create new Battle"
          @click="$router.push(`/new-battle?tournament=${title}`)"
          style="width: 100%"
        />
        <input v-if="is_open && creator == store.getters.getUsername" type="button" value="Close Tournament" @click="closeTournamentRequest()" style="width: 100%" />
        <h2>Badges</h2>
        <input v-if="is_open" type="button" value="+ New Badge" :disabled="true" />
        <h2>Managers</h2>
        <UsersList :users="adapt_managers" />
        <h2 v-if="is_open">Pending invites</h2>
        <UsersList v-if="is_open" :users="adapt_invites" />

        <input v-if="is_open && creator == store.getters.getUsername"
          type="button"
          value="Invite collaborators"
          @click="inviting = true;"
          style="width: 100%"
        />
      </template>
      <template v-else>
        <input
          type="button"
          value="< Cancel"
          @click="
            inviting = false;
            clearInviteStorage();
          "
        />
        <InviteSearch 
          :api_url="SERACH_API_ENDPOINT"
          placeholder="Search for collaborators to manage the tournament"
        />
        <input
          type="button"
          value="Send Invites"
          @click.prevent="sendInvites()"
          :disabled="disable_send_invite"
          style="margin: 1rem 0"
        />
      </template>
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

.right-column > * {
  margin-bottom: 1rem;
}

.manager-wrapper {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0.5rem;
}

.manager-wrapper > * {
  margin: auto 1rem;
}

.managers-wrapper {
  overflow-y: auto;
  max-height: 20vh;
}
</style>
