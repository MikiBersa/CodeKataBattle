<script setup lang="ts">
import { dateExpired, formatDate } from '@/util';
import ProfileFrame from './ProfileFrame.vue';
import IconCopy from './icons/IconCopy.vue';
import type { CardInfoStudent } from '@/util/custom_types';
import { store } from '@/store'; 

const props = defineProps<{
    group: CardInfoStudent
}>()

async function copyTokenToClipboard() {
  await navigator.clipboard.writeText(props.group.API_Token);
  alert("Token copied to clipboard"); 
}
</script>

<template>
    <div class="card-wrapper">
        <div class="group-info-header">
            <div class="title-wrapper">
                <span class="gray clickable" @click="$router.push(`/tournament?title=${encodeURI(group.tournament_title)}`)">
                    {{ group.tournament_title }}
                </span>
                <span class="clickable" @click="$router.push(`/battle?tournament=${encodeURI(group.tournament_title)}&battle=${encodeURI(group.battle_title)}`)">
                    {{ group.battle_title }}
                </span>
            </div>
            <div class="students-wrapper">
                <ProfileFrame v-for="student in group.students.slice(0, 3)" :key="student.username" :alt="student.username"/>
                <span v-if="group.students.length > 3">+ {{ group.students.length - 3 }} other{{ group.students.length - 3 > 1 ? 's' : '' }}</span>
            </div>   
        </div>
        <div>
            <span class="gray">Current score: &nbsp;</span><span>{{ `${group.current_group_score}/100` }}</span>
        </div>
        <div>
            <span class="gray">Last update: &nbsp;</span><span>{{ group.last_update ? formatDate(group.last_update) : "No updates"}}</span>
        </div>
        <div>
            <span class="gray">Submission deadline: &nbsp;</span><span>{{ 
                dateExpired(group.submission_deadline) ? 
                    'Closed' : formatDate(group.submission_deadline)
            }}</span>
        </div>
        <div>
            <div v-if="group.group_leader == store.getters.getUsername" class="flex-row">
                <span v-if="group.API_Token" class="gray">API Token: &nbsp;</span>
                <input type="text" :value="group.API_Token" readonly />
                <IconCopy class="clickable small-icon" @click.prevent="copyTokenToClipboard()"/>
            </div>
        </div>
    </div>
</template>

<style scoped>
.card-wrapper {
    background-color: var(--custom-purple2);
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto 1fr 1fr;
    padding: 1rem;
    color: white;
}

.group-info-header {
    grid-column: span 2;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-bottom: 0.5rem;
}

.gray {
    opacity: 0.5;
}

.card-wrapper div:not(.group-info-header) {
    font-size: 12px;
    margin: 0.2rem 0;
}

.students-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 1rem;
}

.students-wrapper .profile-frame:not(.students-wrapper:first-child) {
    position: relative; 
    margin: auto 0 auto -0.5rem;
}

.students-wrapper span {
    margin: 0 1rem; 
}

.title-wrapper {
    display: flex;
    flex-direction: column;
}

.title-wrapper span {
    font-size: 16px;
}

.small-icon {
    max-width: 2rem;
    margin-left: auto;
    padding: 0.5rem; 
}

.small-icon:hover {
  background: var(--custom-purple);
}

.flex-row {
    display: flex;
    flex-direction: row;
}

.flex-row > * {
    margin: auto 0;
}

input[type="text"] {
    padding: 0.2rem;
    max-width: 20rem;
}
</style>