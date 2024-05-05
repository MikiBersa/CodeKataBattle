<script setup lang="ts">
import type { LeaderboardEntry } from '@/util/custom_types';
import { computed, useSSRContext } from 'vue';

const props = defineProps<{
    entries: LeaderboardEntry[],
    limit?: number,
}>()

const ordered_entries = computed(() => {
    const copy = [...props.entries]
    copy.sort((a, b) => b.score - a.score); 
    return copy.slice(0, props.limit ? props.limit : props.entries.length);
})
</script>

<template>
    <table class="leaderboard">
        <template v-for="(entry, index) in ordered_entries" :key="entry.name + index">
            <tr :class="{ 'purple-bg': index % 2 == 0 }">
                <td class="gray">{{ `${index + 1}.` }}</td>
                <td class="clickable">{{ entry.name }}</td>
                <td><div class="icon-wrapper"><img v-if="index == 0 && entry.score > 0" src="@/assets/crown.png" class="crown-icon"/></div></td>
                <td class="flex">{{ entry.score }}<span class="gray">&nbsp; pts</span></td>
            </tr>
        </template>
    </table>
</template>

<style scoped>
.leaderboard {
    width: 100%; 
}

tr {
    width: 100%;
}

td {
    width: auto;
    text-align: left;
}

.flex {
    display: flex; 
    flex-direction: row;
    align-content: flex-end;
    justify-content: center;
}

.crown-icon {
    width: 20px;
    height: auto; 
}

.icon-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>