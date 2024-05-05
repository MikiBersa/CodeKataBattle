<script setup lang="ts">
import {
  AccountType,
  type BattleData,
  EvalParameter,
  type BattleEnrollmentRequest,
  type GetBattleRequest,
type GroupInviteRequest,
type SetGroupRepositoryRequest,
type LeaderboardEntry,
type StaticAnalysisResult,
type TestResult
} from '@/util/custom_types'
import Navbar from '@/components/Navbar.vue'
import Leaderboard from '@/components/Leaderboard.vue'
import TestsCollapsible from '@/components/TestsCollapsible.vue'
import StaticAnalysisCollapsible from '@/components/StaticAnalysisCollapsible.vue'
import InviteCard from '@/components/InviteCard.vue'
import UsersList from '@/components/UsersList.vue'
import InviteSearch from '@/components/InviteSearch.vue'
import IconEdit from '@/components/icons/IconEdit.vue'
import IconCopy from '@/components/icons/IconCopy.vue'
import { invites_store, store } from '@/store'
import { computed, ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { dateExpired, formatDate, mapStaticResultsToObjects, mapTestResultsToObjects } from '@/util'
import axios from 'axios'

const router = useRouter()
const selected_tab = ref(0)
const account_type: string = store.getters.getAccountType

const API_BASE: string = import.meta.env.VITE_APP_API_BASE
const GET_BATTLE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_BATTLE_ENDPOINT
const ENROLL_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_ENROLL_IN_BATTLE_ENDPOINT
const SEARCH_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SEARCH_USER_ENDPOINT
const GROUP_INVITE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GROUP_INVITE_ENDPOINT
const SET_FORK_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_SET_FORK_URL_ENDPOINT

const battle_data = ref<BattleData>({
  title: '',
  repository: '',
  description: '',
  language: '',
  evaluation_parameters: [],
  manual_evaluation: false,
  submission_deadline: '',
  enrollment_deadline: '',
  min_group_size: 0,
  max_group_size: 0,
  leaderboard: []
})
const invites = ref<any[]>([])
const inviting = ref<boolean>(false)
const creating_group = ref<boolean>(false)
const fork_link = ref<string>(''); 
const previous_fork_link = ref<string>(''); 

const props = defineProps<{
  tournament_title: string
  battle_title: string
}>()

const enrollment_closed = computed(() => {
  return dateExpired(battle_data.value.enrollment_deadline)
})

const disable_send_invite = computed(() => {
  return invites_store.getters.getInvites.length <= 0
})

const disable_set_fork = computed(() => {
  return fork_link.value == previous_fork_link.value || dateExpired(battle_data.value.submission_deadline)
})

const group_members = computed(() => {
  if (!battle_data.value.group) return []; 
  const group = battle_data.value.group; 
  return group.members.map(m => ({
    name: m.username, 
    role: m.username == group.leader.username ? 'leader' : "" 
  }))
}) 

const pending_invites = computed(() => {
  if (!battle_data.value.group) return []; 
  const group = battle_data.value.group; 
  return group.pending_invites.map(m => ({
    name: m.username
  }))
}) 

const timeliness_score = computed((): number => {
  if (!battle_data.value.group || !battle_data.value.group.evaluation_result || !battle_data.value.group.evaluation_result.timeliness_score) return 0; 
  return battle_data.value.group.evaluation_result.timeliness_score;
})

const tests = computed((): TestResult[] => {
  if (!battle_data.value.group || !battle_data.value.evaluation_parameters || !battle_data.value.group.evaluation_result.tests_results) return []; 
  return mapTestResultsToObjects(battle_data.value.group.evaluation_result.tests_results); 
})

const static_analysis = computed((): StaticAnalysisResult[] => {
  if (!battle_data.value.group || !battle_data.value.group.evaluation_result || !battle_data.value.group.evaluation_result.static_analysis_results) return []; 
  return mapStaticResultsToObjects(battle_data.value.group.evaluation_result.static_analysis_results); 
})

const manual_assessment = computed((): number => {
  if (!battle_data.value.group || !battle_data.value.group.evaluation_result || !battle_data.value.group.evaluation_result.manual_assessment_score) return 0; 
  return battle_data.value.group.evaluation_result.manual_assessment_score; 
})

const battle_groups = computed((): LeaderboardEntry[] => {
  return battle_data.value.leaderboard.map(entry => ({
    name: `${entry.name}'s team`,
    score: entry.score
  })); 
})

const clearInviteStorage = () => {
  invites_store.dispatch('clearInvites')
}

function loadBattleData() {
  const request: GetBattleRequest = {
    tournament_title: props.tournament_title,
    battle_title: props.battle_title
  }

  const request_params = [
      `tournamentTitle=${encodeURI(request.tournament_title)}`, 
      `battleTitle=${encodeURI(request.battle_title)}`
  ]

  axios
    .get<BattleData>(
      `${GET_BATTLE_API_ENDPOINT}?${request_params.join('&')}`,
      store.getters.getHeaders
    )
    .then((response) => {
      battle_data.value = response.data;  
      if (battle_data.value.group) {
        fork_link.value = previous_fork_link.value = battle_data.value.group?.repository; 
      }
    })
    .catch((e) => {
      alert(e)
      router.push("/")
    })
}
loadBattleData()

function sendInvites() {
  const invites_usernames: string[] = invites_store.getters.getInvites; 
  const invites_to_send: GroupInviteRequest[] = invites_usernames.map(username => ({
    tournament_title: props.tournament_title, 
    battle_title: props.battle_title, 
    username: username
  })); 

  // TODO: change to a single request -> need to change server route
  invites_to_send.forEach(invite => {
    axios.post(
      GROUP_INVITE_API_ENDPOINT, 
      invite, 
      store.getters.getHeaders
    ).catch(e => alert(e.response.data.error_msg))
  })

  clearInviteStorage()
  inviting.value = false; 
  router.go(0); // Reload page to see updates
}

function enrollInBattleRequest() {
  const invites: string[] = invites_store.getters.getInvites

  const request_body: BattleEnrollmentRequest = {
    tournament_title: props.tournament_title,
    battle_title: props.battle_title,
    invited_members: invites
  }

  axios
    .post(
      ENROLL_API_ENDPOINT, 
      request_body, 
      store.getters.getHeaders
    )
    .then(() => {
      invites_store.dispatch('clearInvites')
      creating_group.value = false 

      // reload page 
      router.go(0)
    })
    .catch((e) => alert(e.response.data.error_msg))
}

function setGroupRepositoryRequest() {
  if (!battle_data.value.group) return; 
  
  const request_body: SetGroupRepositoryRequest = {
    tournament_title: props.tournament_title,
    group_id: battle_data.value.group?.id,
    repository: fork_link.value,
    group_leader: battle_data.value.group.leader.username
  }

  axios.post(
    SET_FORK_API_ENDPOINT, 
    request_body,
    store.getters.getHeaders
  ).then(() => {
    previous_fork_link.value = fork_link.value; 
  }).catch(e => alert(e))
}

async function copyTokenToClipboard() {
  if (!battle_data.value.group) return; 
  await navigator.clipboard.writeText(battle_data.value.group.API_Token);
  alert("Token copied to clipboard"); 
}
</script>

<template>
  <header>
    <Navbar />
  </header>
  <div class="view-wrapper">
    <div style="grid-column: span 2; width: fit-content;" class="flex-row">
      <input type="button" value="< Back" @click.prevent="$router.push(`/tournament?title=${tournament_title}`)" style="max-width: 10rem; margin-right: 2rem;"/>
      <h1>{{ battle_title }}</h1>
    </div>

    <div class="right-column">
      <table class="tabs">
        <thead>
          <tr>
            <th
              class="clickable"
              :class="{ highlighted: selected_tab == 0 }"
              @click="selected_tab = 0"
            >
              Details
            </th>
            <th v-if="account_type == AccountType.Student"
              class="clickable"
              :class="{ highlighted: selected_tab == 1 }"
              @click="selected_tab = 1"
            >
              Group
            </th>
            <th
              class="clickable"
              :class="{ highlighted: selected_tab == 2 }"
              @click="selected_tab = 2"
            >
              Scoring
            </th>
            <th
              class="clickable"
              :class="{ highlighted: selected_tab == 3 }"
              @click="selected_tab = 3"
            >
              Leaderboard
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td colspan="100%" class="table-hr"></td>
          </tr>
        </tbody>
      </table>
      <table class="tabs-instances">
        <template v-if="selected_tab == 0">
          <template v-if="!inviting">
            <tr class="purple-bg">
              <td class="gray">Group size:</td>
              <td class="align-right">
                {{
                  battle_data.min_group_size != battle_data.max_group_size
                    ? `${battle_data.min_group_size} to ${battle_data.max_group_size} memebers`
                    : `${battle_data.max_group_size} members`
                }}
              </td>
            </tr>
            <tr>
              <td class="gray">Enrollment deadline:</td>
              <td class="align-right">{{ formatDate(battle_data.enrollment_deadline) }}</td>
            </tr>
            <tr class="purple-bg">
              <td class="gray">Submission deadline:</td>
              <td class="align-right">{{ formatDate(battle_data.submission_deadline) }}</td>
            </tr>
            <tr>
              <td class="gray">Language:</td>
              <td class="align-right">{{ battle_data.language }}</td>
            </tr>
            <tr class="purple-bg">
              <td class="gray">Static Analysis:</td>
              <td v-if="battle_data.evaluation_parameters.length < 1" class="align-right">
                <span>None</span>
              </td>
              <td v-else class="align-right">
                <template v-for="param in battle_data.evaluation_parameters" :key="param">
                  <span>{{ param }}</span> <br />
                </template>
              </td>
            </tr>
            <tr>
              <td class="gray">Manual assessment:</td>
              <td class="align-right">{{ battle_data.manual_evaluation ? 'Required' : 'No' }}</td>
            </tr>
            <tr class="purple-bg">
              <td class="gray">GitHub repository:</td>
              <td v-if="!battle_data.repository" class="align-right">Not available</td>
              <td v-else class="align-right"><a :href="battle_data.repository">{{ battle_data.repository }}</a></td>
            </tr>
            <tr>
              <td colspan="100%" class="table-hr"></td>
            </tr>
          </template>
        </template>

        <template v-if="selected_tab == 1 && account_type == AccountType.Student">
          <tr>
            <td colspan="100%" class="flex-col" style="padding: 0">
              <h2>Battle group</h2>
              <div v-if="dateExpired(battle_data.enrollment_deadline) && battle_data.group && battle_data.group.leader.username == store.getters.getUsername" class="flex-row">
                <span class="gray">Group Repository:</span>
                <div class="flex-row" style="width: fit-content;">
                  <div class="input-wrapper">
                    <input type="text" v-model="fork_link" placeholder="Forked repository URL" style="min-width: 25rem;" :readonly="dateExpired(battle_data.submission_deadline)"/>
                  </div>
                  <input type="button" value="Set Fork"
                    :disabled="disable_set_fork" 
                    style="margin-left: 1rem; max-width: 7rem;" 
                    @click.prevent="setGroupRepositoryRequest()"
                  />
                </div>
              </div>

              <div v-if="battle_data.group && battle_data.group.leader.email == store.getters.getUsername" class="flex-row">
                <span class="gray">API Token: </span>
                <span v-if="!battle_data.group">Not available</span>
                <div class="flex-row" style="justify-content: flex-end; width: 75%;">
                  <div v-if="battle_data.group" class="input-wrapper" style="width: 100%;">
                    <input type="text" :value="battle_data.group.API_Token" readonly />
                  </div>
                  <IconCopy class="clickable small-icon" 
                    @click.prevent="copyTokenToClipboard()"
                  />
                </div>
              </div>

              <template v-if="!battle_data.group && !enrollment_closed && !creating_group">
                <input
                  type="button"
                  @click="creating_group = true"
                  value="Create new Group"
                  style="width: 100%"
                />
                <div class="cards-wrapper">
                  <span v-if="invites.length < 1">No invites</span>
                  <InviteCard v-for="invite in invites" :key="invite" :invite_info="invite" />
                </div>
              </template>

              <template v-if="battle_data.group && !inviting">
                <UsersList :users="group_members" />
                <input
                  v-if="!enrollment_closed && battle_data.group.leader.username == store.getters.getUsername"
                  type="button"
                  @click="inviting = true"
                  value="Invite students"
                />
                <h2>Pending invites</h2>
                <UsersList v-if="!enrollment_closed" :users="pending_invites" />
              </template>

              <template v-if="inviting">
                <input
                  type="button"
                  value="< Cancel"
                  @click="
                    inviting = false;
                    clearInviteStorage(); 
                  "
                />
                <InviteSearch
                  :api_url="SEARCH_API_ENDPOINT"
                  placeholder="Search students to add to your group"
                />

                <input
                  type="button"
                  value="Send Invites"
                  @click.prevent="sendInvites()"
                  :disabled="disable_send_invite"
                  style="margin: 1rem 0"
                />
              </template>
            </td>
          </tr>
        </template>


        <!-- Scoring tab from students' POV -->
        <template v-if="selected_tab == 2 && account_type == AccountType.Student">
          <tr class="purple-bg">
            <td class="gray">Submission deadline:</td>
            <td class="align-right">{{ formatDate(battle_data.submission_deadline) }}</td>
          </tr>
          <tr>
            <td class="gray">Timeliness:</td>
            <td class="align-right">
              {{ battle_data.group ? `${timeliness_score}/100` : `0/100` }}
            </td>
          </tr>
          <tr v-if="battle_data.manual_evaluation" class="purple-bg">
            <td class="gray">Manual evaluation:</td>
            <td class="align-right">
              {{ battle_data.group ? `${manual_assessment}/100` : `0/100` }}
            </td>
          </tr>
          <tr v-if="battle_data.group">
            <td colspan="100%" style="padding: 0">
              <TestsCollapsible :tests="tests" style="width: 100%" />
            </td>
          </tr>
          <tr v-if="battle_data.group">
            <td colspan="100%" style="padding: 0">
              <StaticAnalysisCollapsible
                :static_analysis="static_analysis"
                style="width: 100%"
              />
            </td>
          </tr>
        </template>

        <!-- Scoring tab from educators' POV -->
        <template v-if="selected_tab == 2 && account_type == AccountType.Educator">
          <h2>Groups</h2>
          <table>
            <thead>
              <tr>
                <th>Group name</th>
                <th>Evaluation Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colspan="100%" class="table-hr" style="padding: 0"></td>
              </tr>
              <template v-for="(group, index) in battle_data.groups" :key="group + index">
                <tr :class="{ 'purple-bg': index % 2 == 0 }">
                  <td>{{ `${group.leader.username}'s team` }}</td>
                  <td v-if="group.done_manual_evaluation" class="gray">Consolidated</td>
                  <td v-else class="flex-row">
                    <span>Pending</span>
                    <RouterLink v-if="!group.done_manual_evaluation && dateExpired(battle_data.submission_deadline) && battle_data.manual_evaluation"
                      :to="`/solution?tournament=${encodeURI(props.tournament_title)}&battle=${encodeURI(battle_data.title)}&group=${encodeURI(group.id)}`"
                      class="icon-link"
                    >
                      <IconEdit style="max-width: 20px" />
                    </RouterLink>
                  </td>
                </tr>
              </template>
            </tbody>
          </table>
        </template>

        <template v-if="selected_tab == 3">
          <tr>
            <td colspan="100%">
              <div class="leaderboard-wrapper">
                <Leaderboard :entries="battle_groups" />
              </div>
            </td>
          </tr>
          <!-- <tr v-if="account_type == AccountType.Student">
            <td colspan="100%">
              {{ `Your team is currently at rank ${-1}/${battle_data.groups.length}` }}
            </td>
          </tr> -->
        </template>
      </table>
    </div>

    <div class="left-column" v-if="!creating_group">
      <h2>Problem description</h2>
      <div class="description-wrapper">{{ battle_data.description }}</div>
    </div>
    <div class="left-column" v-else>
      
      <div class="create-group-wrapper">
        <h2>Forming group</h2>
        <input
          type="button"
          value="< Cancel"
          style="max-width: 5rem"
          @click.prevent="
            creating_group = false; 
            clearInviteStorage();
          "
        />
        <InviteSearch
          :api_url="SEARCH_API_ENDPOINT"
          placeholder="Search students to add to your group"
        />
        <input type="button" value="Enroll into Battle" @click.prevent="enrollInBattleRequest()" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.view-wrapper {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(auto, 3);
  gap: 1rem;
}

.left-column {
  grid-row: 2 / span 2;
  grid-column: 1;
}

.description-wrapper {
  overflow-y: auto;
  max-height: 60vh;
} 

.right-column {
  grid-row: 2 / span 2;
  grid-column: 2;
}

table {
  border-spacing: 0 0.5rem;
  width: 100%;
}

.table-hr {
  opacity: 0.5;
  border-bottom: 1px solid var(--custom-white);
}

.highlighted {
  color: white;
}

.leaderboard-wrapper {
  width: 100%;
  overflow-y: auto;
  max-height: 50vh;
}

.align-right {
  text-align: right;
}

.tabs-instances tr td {
  padding: 0.5rem 2rem;
  color: white;
}

.flex-row {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
}

.flex-col > * {
  margin-bottom: 1rem;
}

.icon-link {
  padding: 0;
  width: 20px;
  height: 20px;
}

.cards-wrapper {
  overflow-y: auto;
  max-height: 20vh;
}

.create-group-wrapper > * {
  margin-bottom: 2rem;
}

.small-icon {
    max-width: 2rem;
    margin-left: auto;
    padding: 0.5rem; 
}

.small-icon:hover {
  background: var(--custom-purple);
}
</style>
