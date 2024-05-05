<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import { RouterView } from 'vue-router';
</script>

<template>
    <header>
        <Navbar />
    </header>
    <div class="wrapper">
        <RouterView />
    </div>
</template>

<style scoped>
.wrapper {
    grid-column: span 2;
    display: flex;
    align-content: center;
    justify-content: center;
}
</style>