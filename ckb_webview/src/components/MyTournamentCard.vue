<script setup lang="ts">
import { dateExpired, formatDate } from '@/util';
import ProfileFrame from './ProfileFrame.vue';
import type { CardInfoEducator } from '@/util/custom_types';

defineProps<{
    tournament: CardInfoEducator
}>()
</script>

<template>
    <div class="card-wrapper">
        <div class="tournament-info-header">
            <span class="clickable" @click="$router.push(`/tournament?title=${encodeURI(tournament.tournament_title)}`)">
                {{ tournament.tournament_title + (tournament.is_open ? '' : ' (closed)') }}
            </span>
            <div class="educators-wrapper">
                <ProfileFrame v-for="educator in tournament.educators.slice(0, 3)" :key="educator.username" :alt="educator.username"/>
                <span v-if="tournament.educators.length > 3">+ {{ tournament.educators.length - 3 }} other{{ tournament.educators.length - 3 > 1 ? 's' : '' }}</span>
            </div>   
        </div>
        <div>
            <span class="gray">Subscribed students: &nbsp;</span><span>{{ tournament.subscribed_students_count }}</span>
        </div>
        <div>
            <span class="gray">Subscription deadline: &nbsp;</span><span>{{ 
                dateExpired(tournament.subscription_deadline)  ? 
                    'Closed' : formatDate(tournament.subscription_deadline)
            }}</span>
        </div>
        <div>
            <span class="gray">Number of battles: &nbsp;</span><span>{{ tournament.number_of_battles }}</span>
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

.tournament-info-header {
    grid-column: span 2;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    margin-bottom: 0.5rem;
}

.gray {
    opacity: 0.5;
}

.card-wrapper div:not(.tournament-info-header) {
    font-size: 12px;
    margin: 0.2rem 0;
}

.educators-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    align-items: center;
    padding: 0 1rem;
}

.educators-wrapper .profile-frame:not(.educators-wrapper:first-child) {
    position: relative; 
    margin: auto 0 auto -0.5rem;
}

.educators-wrapper span {
    margin: 0 1rem; 
}

</style>