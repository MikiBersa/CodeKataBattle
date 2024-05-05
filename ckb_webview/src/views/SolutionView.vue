<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import TestsCollapsible from '@/components/TestsCollapsible.vue';
import StaticAnalysisCollapsible from '@/components/StaticAnalysisCollapsible.vue';
import { computed, ref } from 'vue';
import type { BattleData, GetBattleRequest, Group, ManualEvaluationRequest, StaticAnalysisResult, TestResult } from '@/util/custom_types';
import axios from 'axios';
import { store } from '@/store';
import { useRouter } from 'vue-router';
import { mapStaticResultsToObjects, mapTestResultsToObjects } from '@/util';

const props = defineProps<{
    tournament_title: string,
    battle_title: string,
    group_id: string
}>()

const router = useRouter(); 
const giving_assessment = ref<boolean>(false);
const manual_score = defineModel<number>('score');
const fallback_route = `/battle?tournament=${props.tournament_title}&battle=${props.battle_title}`; 

const API_BASE: string = import.meta.env.VITE_APP_API_BASE;
const GET_BATTLE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_GET_BATTLE_ENDPOINT;
const MANUAL_ASSESSMENT_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_MANUAL_ASSESSMENT_ENDPOINT;

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
const current_group = ref<Group>(); 

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
      if (!battle_data.value.groups) {
        alert("Unable to find group"); 
        router.push(fallback_route); 
      }

      current_group.value = battle_data.value.groups?.find(g => g.id == props.group_id); 
      console.dir(current_group.value)
      if (!current_group.value) {
        alert("Unable to find group"); 
        router.push(fallback_route); 
      }

    })
    .catch((e) => {
      alert(e)
      router.push(fallback_route)
    })
}
loadBattleData()

const disable_submit_review = computed(() => {
    return manual_score.value == undefined || manual_score.value < 0 || manual_score.value > 100
})

const timeliness_score = computed((): number => {
  if (!current_group.value || !current_group.value.evaluation_result || !current_group.value.evaluation_result.timeliness_score) return 0; 
  return current_group.value.evaluation_result.timeliness_score;
})

const tests = computed((): TestResult[] => {
  if (!current_group.value|| !battle_data.value.evaluation_parameters || !current_group.value.evaluation_result.tests_results) return []; 
  return mapTestResultsToObjects(current_group.value.evaluation_result.tests_results); 
})

const static_analysis = computed((): StaticAnalysisResult[] => {
  if (!current_group.value || !current_group.value.evaluation_result || !current_group.value.evaluation_result.static_analysis_results) return []; 
  return mapStaticResultsToObjects(current_group.value.evaluation_result.static_analysis_results); 
})

function giveManualEvaluationRequest() {
    if (!current_group.value) {
        alert("Unable to find group"); 
        router.push(fallback_route); 
    }

    const request_body: ManualEvaluationRequest = {
        tournament_title: props.tournament_title,
        battle_title: props.battle_title,
        group_id: current_group.value!.id,
        points: manual_score.value ? manual_score.value : 0 
    }

    axios.post(
        MANUAL_ASSESSMENT_API_ENDPOINT,
        request_body,
        store.getters.getHeaders 
    )
    .catch(e => alert(e))
    .finally(() => router.push(fallback_route))
}

const fake_code = `
// Fake Java Code for Mockup

public class MockupApp {

    public static void main(String[] args) {
        // Fake data for demonstration
        String[] fakeNames = {"John", "Alice", "Bob", "Eva"};
        int[] fakeAges = {25, 30, 22, 28};

        // Display fake information
        for (int i = 0; i < fakeNames.length; i++) {
            System.out.println("Name: " + fakeNames[i] + ", Age: " + fakeAges[i]);
        }

        // Fake method for processing data
        processFakeData();
    }

    // Fake method for processing data
    private static void processFakeData() {
        System.out.println("Processing fake data... Done!");
    }
}
`
const code = ref<string>(fake_code); // not implemented

</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="view-wrapper">
        <h1 style="grid-column: span 2;">{{ tournament_title }}</h1>
        <h2 style="grid-column: span 2;">{{ `Viewing ${current_group?.leader.username}'s team Solution'` }}</h2>

        <highlightjs :code="code" class="code-box" />

        <div class="flex-wrapper">
            <table>
                <tr class="purple-bg">
                    <td class="gray">Current total score:</td>
                    <td class="align-right">{{ current_group ? `${current_group.total_score}/100` : `0/100`}}</td>
                </tr>
                <tr>
                    <td class="gray">Timeliness:</td>
                    <td class="align-right">{{ current_group? `${timeliness_score}/100` : `0/100` }}</td>
                </tr>
                <tr class="purple-bg">
                    <td class="gray">Manual evaluation:</td>
                    <td class="align-right">Pending</td>
                </tr>
            </table>

            <TestsCollapsible :tests="tests" style="color:white;" />
            <StaticAnalysisCollapsible :static_analysis="static_analysis" style="color:white;" />

            <input v-if="!giving_assessment" type="button" value="Give Manual Assessment"
                @click="giving_assessment = true" />
            <h2 v-if="giving_assessment">Manual assessment</h2>
            
            <div v-if="giving_assessment" >
                <span>Group fork link: <a :href="current_group ? current_group.repository : ''">{{ current_group ? current_group.repository : "Not available" }}</a></span>
                <div class="flex-row" style="margin-top: 2rem; justify-content: space-between;">
                    <div class="input-wrapper" style="max-width: 10rem">
                    <input type="text" placeholder="Insert score" v-model="manual_score" />
                    </div>
                    <span style="margin-right: 1rem;">/100</span>
                    <input type="button" @click.prevent="giving_assessment = false; manual_score = -1" value="Cancel"
                        class="light-purple-border" />
                    <input type="button" @click.prevent="giveManualEvaluationRequest()" value="Submit review" :disabled="disable_submit_review" />
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.view-wrapper {
    grid-template-columns: 1.5fr 1fr;
    gap: 1rem;
}

.code-box {
    overflow: auto;
    max-height: 70vh;
}

.flex-wrapper {
    display: flex;
    flex-direction: column;
}

table {
    color: white;
    width: 100%;
    border-collapse: collapse;
}

.align-right {
    text-align: right;
}

tr td {
    padding: 0.5rem 2rem;
    color: white;
}

.flex-wrapper>* {
    margin-bottom: 2rem;
}

.flex-row {
    display: flex;
    flex-direction: row;
    align-items: center;
}
</style>