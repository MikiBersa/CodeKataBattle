<script setup lang="ts">
</script>

<template>
    <Transition name="fade" appear :duration="500">
    <div class="welcome-wrapper">
        <h1 class="welcome-title">CodeKataBattle</h1>
        
        <div class="welcome-message">
            Challenge yourself and others! <br/>
            An educational coding platform to learn, improve and compete <br/>
            while writing code. 
        </div>

        <div class="buttons-wrapper">
            <a href="/register" class="button light-purple-bg rounded-border">Create account ></a>
            <a href="/login" class="button light-purple-border rounded-border">Login ></a>
        </div>
    </div>
    </Transition>
</template>

<style scoped>
    .welcome-wrapper {
        opacity: 1;
        grid-column: span 2;
        padding: 3rem 5rem; 
        width: 100%;
        display: grid;
        grid-template-columns: auto 1fr;
        grid-template-rows: repeat(4, auto);
        gap: 1rem; 
        row-gap: 2rem;
    }

    .welcome-title {
        grid-column: 1;
        grid-row: 1;
        color: var(--custom-white);
        font-weight: medium;
        font-size: 40px;
    }

    .welcome-message {
        grid-column: 1;
        grid-row: 2;
        width: auto;
    }

    .welcome-bg {
        position: absolute;
        z-index: -1;
        transform: scaleX(-1); 
    }

    .buttons-wrapper {
        display: flex;
        flex-direction: row;
        grid-column: 1;
        grid-row: 3;
        justify-content: flex-start;
        gap: 2rem;
        flex-wrap: wrap;
    }


    .fade-enter-active,
    .fade-leave-active {
        transition: all 0.5s ease-in-out;
    }

    .fade-enter-from,
    .fade-leave-to {
        opacity: 0; 
    }
</style>


