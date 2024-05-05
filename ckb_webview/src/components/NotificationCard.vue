<script setup lang="ts">
import type { NotificationDetails } from '@/util/custom_types';
import IconDelete from './icons/IconDelete.vue';

defineProps<{
    notification: NotificationDetails, 
}>()
</script>

<template>
    <div class="notification-wrapper">
        <div class="notification-type">{{ notification.display_type }}</div>
        <div class="delete-icon"><IconDelete /></div>
        <div class="notification-message">{{ notification.message }}</div>
        <div class="view-in-ctx"><a :href="notification.context_url" >View in context ></a></div>
    </div>
</template>

<style scoped>
.notification-wrapper {
    background-color: var(--custom-purple2);
    display: grid;
    grid-template-columns: 2fr 1fr;
    grid-template-rows: 1fr auto 1fr;
    padding: 1rem;
    margin-bottom: 1rem;   
}

.notification-message, .view-in-ctx {
    grid-column: span 2;
}

.notification-message {
    color: white; 
}

.view-in-ctx {
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: right;
}

.delete-icon {
    display: flex;
    align-items: center;
    justify-content: right;
}

.delete-icon svg {
    width: 30px;
    height: 30px;
    padding: 0.2rem;
}

.view-in-ctx a {
    padding: 0;
}

.view-in-ctx a:hover {
    cursor: pointer;
}
</style>