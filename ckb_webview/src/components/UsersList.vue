<script setup lang="ts">
import ProfileFrame from './ProfileFrame.vue';
defineProps<{
    users: { name: string, profile_img_url?: string, role?: string }[]
}>()
</script>

<template>
    <div class="users-wrapper">
        <template v-for="(user, index) in users">
            <div class="user-wrapper" :class="{ 'purple-bg': index % 2 == 0 }">
                <ProfileFrame :alt="user.name" :image_url="user.profile_img_url" />
                <span>{{ user.name }}</span>
                <span v-if="user.role" class="gray" style="margin-left: auto;">{{ user.role }}</span>
            </div>
        </template>
    </div>
</template>

<style scoped>
.user-wrapper {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0.5rem;
}

.user-wrapper>* {
    margin: auto 1rem;
}

.users-wrapper {
    overflow-y: auto;
    max-height: 20vh;
}
</style>