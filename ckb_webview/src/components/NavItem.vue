<script setup lang="ts">
defineProps<{
    url: string,
}>()
</script>

<template>
    <div> 
        <RouterLink :to="url">
            <slot></slot>
        </RouterLink>
    </div>
</template>

<style scoped>
a {
    padding: 1rem 2rem; 
}
</style>