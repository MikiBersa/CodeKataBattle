<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import axios from 'axios';
import debounce from 'lodash.debounce';
import ProfileFrame from './ProfileFrame.vue';
import IconAdd from '@/components/icons/IconAdd.vue';
import IconRemove from '@/components/icons/IconRemove.vue';
import { invites_store, store } from '@/store';

const props = defineProps<{
    api_url: string,
    placeholder?: string
}>();

// type QueryResult = {
//     username: string,
//     profile_img_url?: string
// };

const query = defineModel<string>('search_query');
const loading = ref<boolean>(false);
const selection = ref<Set<string>>(new Set());
const results = ref<string[]>([]);

watch(query, debounce(async () => {
    if (query.value == '') {
        results.value = []; 
        return; 
    } 
    loading.value = true;

    await axios.get<string[]>(
        `${props.api_url}?username=${query.value}`,
        store.getters.getHeaders
    )
    .then((response) => {
        results.value = response.data; 
    })
    .catch((e) => alert(e.response.data.error_msg))
    .finally(() => loading.value = false)
}, 1000));

function add(user: string) {
    selection.value.add(user); 
    results.value = results.value.filter(user => !selection.value.has(user));
    updateStore();
}

function remove(user: string) {
    selection.value.delete(user); 
    if (results.value.length > 0) results.value = [user , ...results.value];
    updateStore(); 
}

function updateStore() {
    invites_store.dispatch('saveInvites', Array.from(selection.value)); 
}

const disable_send_invite = computed(() => {
    return selection.value.size <= 0;
})
</script>

<template>
    <div class="search-wrapper">
        <div class="input-wrapper">
            <input type="search" :placeholder="placeholder" v-model="query" :disabled="loading" />
        </div>
        <ul class="search-results">
            <li v-for="(result, index) in results" :key="result + index" :class="{'purple-bg': index % 2 == 0}" class="search-result">
                <ProfileFrame :image_url="undefined" :alt="result" />
                <span>{{ result }}</span>
                <IconAdd class="small-icon clickable" @click.prevent="add(result)"/>
            </li>
        </ul>

        <hr style="margin: 1rem 0;">
        <h2 v-if="!disable_send_invite">Selected users</h2>

        <ul class="selected-results">
            <li v-for="(result, index) in selection.values()" :key="result + index" :class="{'purple-bg': index % 2 == 0}" class="search-result">
                <ProfileFrame :image_url="undefined" :alt="result" />
                <span>{{ result }}</span>
                <IconRemove class="small-icon clickable" @click.prevent="remove(result)"/>
            </li>
        </ul>

        <!-- <input type="button" value="Send Invites" @click.prevent="sendInvites()" :disabled="disable_send_invite" style="margin: 1rem 0;"/> -->
    </div>
</template>

<style>

.search-results, .selected-results {
    overflow-y: auto;
    max-height: 20vh; 
    transition: height 0.5s ease-in-out;
}

.search-result {
    display: flex;
    flex-direction: row;
    align-items: center;
}

.search-result > * {
    margin: 0 1rem;
}

ul {
    list-style-type: none;
    padding: 0; 
}

li {
    color: white; 
    padding: 0.5rem 1rem; 
}

.small-icon {
    max-width: 2rem;
    margin-left: auto;
}
</style>