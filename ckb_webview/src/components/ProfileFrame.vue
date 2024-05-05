<script setup lang="ts">
import IconProfile from './icons/IconProfile.vue';

defineProps<{
    image_url?: string,
    alt: string
}>()
</script>

<template>
    <div v-if="image_url && image_url != undefined && image_url != ''" class="profile-frame">
        <img :src="image_url" :title="alt" />
    </div>
    <div v-else class="profile-frame">
        <IconProfile />
    </div>
</template>

<style scoped>
.profile-frame {
    border-radius: 50%;
    overflow: hidden;
    width: 2rem;
    height: 2rem;
    border: 1px solid white;
}

.profile-frame img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}
</style>