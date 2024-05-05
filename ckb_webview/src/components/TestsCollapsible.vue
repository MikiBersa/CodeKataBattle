<script setup lang="ts">
import { computed } from 'vue';
import CollapsibleVue from './Collapsible.vue';
import IconRedX from '@/components/icons/IconRedX.vue';
import IconGreenCheck from '@/components/icons/IconGreenCheck.vue';
import { TestStatus, type TestResult } from '@/util/custom_types';

const props = defineProps<{
    tests: TestResult[]
}>()

const passed_tests = computed(() => {
    return props.tests.filter((test) => test.status === TestStatus.PASSED);
});

const failed_tests = computed(() => {
    return props.tests.filter((test) => test.status === TestStatus.FAILED);
});
</script>

<template>
    <CollapsibleVue v-if="tests.length > 0" title="Test cases">
        <CollapsibleVue title="Passed tests" padding="0.5rem">
            <template v-for="test in passed_tests" :key="test">
                <div class="flex-row">
                    <IconGreenCheck class="small-icon" />
                    <span class="test-wrapper">{{ test.testName }}</span>
                </div>
            </template>
        </CollapsibleVue>

        <CollapsibleVue title="Failed tests" padding="0.5rem">
            <template v-for="test in failed_tests" :key="test">
                <div class="flex-row">
                    <IconRedX class="small-icon" />
                    <span class="test-wrapper">{{ test.testName }}</span>
                </div>
            </template>
        </CollapsibleVue>
    </CollapsibleVue>

    <CollapsibleVue v-else title="Test cases">
        <span class="gray test-wrapper">No results currently available</span>
    </CollapsibleVue>
</template>

<style scoped>
.small-icon {
    width: 1rem;
    margin: 0;
}

.test-wrapper {
    padding: 0.5rem;
}

.flex-row {
    display: flex;
    flex-direction: row;
    align-items: center;
}
</style>