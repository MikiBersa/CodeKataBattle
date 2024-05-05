<script setup lang="ts">
import Navbar from '@/components/Navbar.vue';
import Welcome from '../components/Welcome.vue'
</script>

<template>
    <header>
        <Navbar />
    </header>
    <img src="@/assets/landing_bg.png" class="home-bg" />
    <main>
        <Welcome />
    </main>
</template>

<style scoped>
main {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
}

.home-bg {
    position: absolute;
    top: -10vh;
    left: 0;
    z-index: -1;
    width: 100%;
    transform: scaleX(-1);
}
</style>