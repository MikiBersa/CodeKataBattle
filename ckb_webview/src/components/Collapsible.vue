<script setup lang="ts">
import { ref } from 'vue';

defineProps<{
    title: string,
    padding?: string
}>()

const open_status = ref(false)
</script>

<template>
    <div class="collapsible-wrapper">
        <div class="purple-bg clickable collapsible-header-wrapper" :style="{ 'padding': padding }">
            <span class="collapsible-title" @click="open_status = !open_status">{{ title }}</span>
            <span :class="{ 'flipped-y': open_status }">v</span>
        </div>
        <div :class="{ 'collapsed': !open_status }" class="slot-wrapper">
            <slot></slot>
        </div>
    </div>
</template>

<style scoped>

.collapsible-wrapper {
    width: 100%;
    padding: 0;
    margin-top: 1rem; 
}

.collapsible-header-wrapper {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 1rem; 
}

.collapsible-title {
    width: 100%; 
    text-align: center;
}

.flipped-y {
    transform: scaleY(-1);
}

.collapsed {
    max-height: 0;
    overflow: hidden;
}

.slot-wrapper {
    display: flex;
    flex-direction: column;
}
</style>