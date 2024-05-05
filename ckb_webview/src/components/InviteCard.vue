<script setup lang="ts">
import { type InviteCard } from '@/util/custom_types';
import ProfileFrame from './ProfileFrame.vue';

type InviteInfo = {
    title: string,
    subtitle?: string,
    message: string,
    accept_callback: () => void,
    reject_callback: () => void
    // users: UserInfo[]
}

defineProps<{
    invite_info: InviteInfo
}>()
</script>

<template>
    <div class="invite-wrapper purple-bg">
        <div class="titles-wrapper">
            <h4 v-if="invite_info.subtitle">{{ invite_info.subtitle }}</h4>
            <h3>{{ invite_info.title }}</h3>
        </div>
        <div class="users-wrapper">
            <!-- TODO: optional, not implemented -->
            <!-- <ProfileFrame v-for="user in invite_info.users.slice(0, 3)" :image_url="user.profile_img_url" :alt="user.username" />
            <span v-if="invite_info.users.length > 3">+ {{ invite_info.users.length - 3 }} other{{ invite_info.users.length - 3 > 1 ? 's' : '' }}</span> -->
        </div>
        <span>{{ invite_info.message }}</span>
        <div class="buttons-wrapper">
            <input type="button" value="Accept" @click.prevent="invite_info.accept_callback()"/>
            <input type="button" value="Reject" class="light-purple-border" @click.prevent="invite_info.reject_callback()"/>
        </div>
    </div>
</template>

<style scoped>
.invite-wrapper {
    display: grid;
    grid-template-columns: 2fr 1fr;
    grid-template-rows: repeat(auto, 2);
    gap: 1rem;
    padding: 1rem;
    margin-bottom: 2rem;
}

.titles-wrapper {
    display: flex;
    flex-direction: column;
}

h3,
h4 {
    color: white;
}

h4 {
    font-size: 14px;
}

.users-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    align-items: center;
    padding: 0 1rem;
}

.users-wrapper .profile-frame:not(.users-wrapper:first-child) {
    position: relative; 
    margin: auto 0 auto -0.5rem;
}

.users-wrapper span {
    margin: 0 1rem; 
}

.buttons-wrapper {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
}

.buttons-wrapper > * {
    margin: 0.5rem;
}
</style>