<script setup lang="ts">
    defineProps<{
        tournament_title: string,
        battle_title: string,
        is_open: boolean,
        is_enrollment_open: boolean 
        enrollment_deadline: string,
        enrolled_groups: number, 
        has_background: boolean
    }>(); 

    const TEXT_LIMIT: number = 30
</script>

<template>
    <tr class="entry-wrapper" :class="{ 'purple-bg': has_background }">
        <td>
            <span :class="{ gray: !is_open }">{{ is_open ? "Open" : "Closed" }}</span>
        </td>
        <td>
            <span class="clickable" @click="$router.push(`/battle?tournament=${encodeURIComponent(tournament_title)}&battle=${encodeURI(battle_title)}`)">{{ battle_title.substring(0, TEXT_LIMIT) }}{{ battle_title.length > TEXT_LIMIT ? '...' : '' }}</span>
        </td>
        <td>
            <span :class="{ gray: !is_enrollment_open }">{{ is_enrollment_open ? enrollment_deadline : "Closed" }}</span>
        </td>
        <td>
            <span>{{ enrolled_groups }}</span>
        </td>
    </tr>
</template>

<style scoped>
.entry-wrapper {
    color: white;
}

td {
    padding: 0.5rem 1rem; 
}
</style>