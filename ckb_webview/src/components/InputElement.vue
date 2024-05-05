<script setup lang="ts">
defineProps<{
    type: string,
    name: string,
    label: string,
    placeholder: string,
    show_text?: any,
    model?: any
}>()
</script>

<template>
    <div class="input-wrapper">
        <label v-if="label" :for="name">{{ label }}</label>
        <input v-if="show_text" type="text" :name="name" :id="name" :placeholder="placeholder" :v-model="model"/>    
        <input v-else :type="type" :name="name" :id="name" :placeholder="placeholder" :v-model="model"/>    
        <slot></slot>
    </div>
</template>

<style scoped>
input {
    width: 100%; 
    font-family: inherit;
    font-weight: light;
    background-color: transparent;
    border: 1px solid var(--custom-white);
    padding: 0.75rem; 
    color: var(--custom-white);
}

::placeholder {
    opacity: 0.75;
    color: var(--custom-white);
}

.input-wrapper {
    margin: 0.2rem 0;
}

label {
    color: white;
}
</style>