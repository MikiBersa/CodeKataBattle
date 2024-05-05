<script setup lang="ts">
    import type { SimpleUserProfile } from '@/util/custom_types';
import ProfileFrame from './ProfileFrame.vue';

    defineProps<{
        is_open: boolean,
        title: string,
        is_subscription_open: boolean 
        subscription_deadline: string,
        subscribed_students: number, 
        educators: SimpleUserProfile[],
        has_background: boolean
    }>()

    const TEXT_LIMIT: number = 50
</script>

<template>
    <tr class="entry-wrapper" :class="{ 'purple-bg': has_background }">
        <td>
            <span :class="{ gray: !is_open }">{{ is_open ? "Open" : "Closed" }}</span>
        </td>
        <td>
            <span class="clickable" @click="$router.push(`/tournament?title=${encodeURIComponent(title)}`)">{{ title.substring(0, TEXT_LIMIT) }}{{ title.length > TEXT_LIMIT ? '...' : '' }}</span>
        </td>
        <td>
            <span :class="{ gray: !is_subscription_open }">{{ is_subscription_open ? subscription_deadline : "Closed" }}</span>
        </td>
        <td>
            <span>{{ subscribed_students }}</span>
        </td>
        <td>
            <div class="educators-wrapper">
                <ProfileFrame v-for="educator in educators.slice(0, 3)" :key="educator.username" :image_url="educator.profile_img_url" :alt="educator.username"/>
                <span v-if="educators.length > 3">+ {{ educators.length - 3 }} other{{ educators.length - 3 > 1 ? 's' : '' }}</span>
            </div>
        </td>
    </tr>
</template>

<style scoped>
.entry-wrapper {
    color: white;
}

.educators-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: flex-start;
    padding: 0 1rem;
}

.educators-wrapper .profile-frame:not(.educators-wrapper:first-child) {
    position: relative; 
    margin: auto 0 auto -0.5rem;
}

.educators-wrapper span {
    margin: 0 1rem; 
}

td {
    padding: 0.5rem 1rem; 
}

</style>