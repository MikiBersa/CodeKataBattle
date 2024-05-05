<script setup lang="ts">
import type { StaticAnalysisResult } from '@/util/custom_types';
import CollapsibleVue from './Collapsible.vue';

defineProps<{
    static_analysis: StaticAnalysisResult[]
}>()
</script>

<template>
    <CollapsibleVue title="Static Analysis Parameters">
            <template v-if="static_analysis.length > 0" v-for="param in static_analysis" :key="param">
                <div class="flex-row">
                    <span class="test-wrapper">{{ param.name }}</span>
                    <span class="test-wrapper">{{ `${param.score}/100` }}</span>
                </div>
            </template>
            <template v-else>
                <span class="gray test-wrapper">No results currently available</span>
            </template>
    </CollapsibleVue>
</template>

<style scoped>
.test-wrapper {
    padding: 0.5rem;
}

.flex-row {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;
}
</style>