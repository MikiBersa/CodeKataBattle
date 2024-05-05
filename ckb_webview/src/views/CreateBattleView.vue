<script setup lang="ts">
import Navbar from '@/components/Navbar.vue'
import { ImplementedLanguages, EvalParameters, toDate, dateExpired } from '@/util'
import { computed, ref } from 'vue'
import { store } from '@/store'
import axios from 'axios'
import type { BattleCreationRequest } from '@/util/custom_types'
import router from '@/router'

const props = defineProps<{
  tournament_title: string
}>()

const title = defineModel<string>('title', { default: '' })
const min_group_size = defineModel<number>('min_size', { default: 0 })
const max_group_size = defineModel<number>('max_size', { default: 0 })
const problem_description = defineModel<string>('description', { default: '' })
const registration_deadline = defineModel<string>('reg_deadline', { default: '' })
const submission_deadline = defineModel<string>('sub_deadline', { default: '' })
const require_manual_evaluation = defineModel<boolean>('man_eval', { default: false })
const evaluation_parameters = ref<string[]>([])
const tests_file_name = defineModel<string>('tests_file', { default: '' })
const proj_language = defineModel<string>('language', { default: '' })
const project_files = ref()

const API_BASE: string = import.meta.env.VITE_APP_API_BASE
const CREATE_BATTLE_API_ENDPOINT: string = API_BASE + import.meta.env.VITE_APP_API_CREATE_BATTLE_ENDPOINT

function createBattleRequest() {
  const request_body: BattleCreationRequest = {
    tournament_title: props.tournament_title,
    battle_title: title.value,
    min_group_size: min_group_size.value,
    max_group_size: max_group_size.value,
    description: problem_description.value,
    enrollment_deadline: toDate(registration_deadline.value).toISOString(),
    submission_deadline: toDate(submission_deadline.value).toISOString(),
    manual_evaluation: require_manual_evaluation.value,
    evaluation_parameters: evaluation_parameters.value.map((p) => p.toUpperCase()),
    tests_file_name: tests_file_name.value,
    project_language: proj_language.value
  }

  const form_data: FormData = new FormData(); 
  form_data.append('file', project_files.value, project_files.value.name); 

  // Need to convert to Blob otherwise headers can't get parsed correctly 
  const json_blob = new Blob([JSON.stringify(request_body)], {
    type: 'application/json'
  });
  form_data.append('request', json_blob, 'request'); 
  
  axios
    .post(
      CREATE_BATTLE_API_ENDPOINT, 
      form_data, { 
        headers: {
          'Content-Type': 'multipart/form-data',
          'Authorization': `Bearer ${store.getters.getToken}`
        }
      })
    .then(() => router.push(`/battle?tournament=${props.tournament_title}&battle=${title.value}`))
    .catch((e) => {
      alert(e);
    })
}

function onFilesSelected($event: Event) {
  const target = $event.target as HTMLInputElement; 
  if (target && target.files) {
    project_files.value = target.files[0]; 
  }
}

const enable_create_battle = computed(() => {
  return (
    props.tournament_title != '' &&
    title.value != '' &&
    (min_group_size.value as number) > 0 &&
    (max_group_size.value as number)> 0 &&
    (min_group_size.value as number) <= (max_group_size.value as number) &&
    problem_description.value != '' &&
    registration_deadline.value != '' &&
    submission_deadline.value != '' &&
    toDate(registration_deadline.value) < toDate(submission_deadline.value) &&
    !dateExpired(registration_deadline.value) && // the other is implied by the above statement
    evaluation_parameters.value != null &&
    tests_file_name.value != '' &&
    proj_language.value != '' && 
    project_files.value != undefined
  )
})
</script>

<template>
  <header>
    <Navbar />
  </header>
  <div class="view-wrapper">
    <div style="display: flex; flex-direction: row; align-items: center; grid-column: span 2;">
      <input
        type="button"
        value="< Cancel"
        @click="$router.go(-1)"
        style="max-width: 10rem; margin-right: 2rem;"
      />
      <h1 style="">Create new Battle</h1>
    </div>
    <div class="inputs-wrapper">
      <div class="input-wrapper" style="grid-column: span 2">
        <label for="title">Battle title</label>
        <input
          type="text"
          name="title"
          id="title"
          placeholder="Insert battle title"
          v-model="title"
        />
        <span class="warning">Must not contain special characters ($,%,!,etc.)</span>
      </div>

      <div class="input-wrapper">
        <label for="min_size">Minimum group size</label>
        <input
          type="text"
          name="min_size"
          id="min_size"
          placeholder="Insert min size"
          v-model="min_group_size"
        />
      </div>
      <div class="input-wrapper">
        <label for="max_size">Maximum group size</label>
        <input
          type="text"
          name="max_size"
          id="max_size"
          placeholder="Insert max size"
          v-model="max_group_size"
        />
      </div>

      <div class="input-wrapper">
        <label for="reg_deadline">Registration deadline</label>
        <input
          type="datetime-local"
          name="reg_deadline"
          id="reg_deadline"
          v-model="registration_deadline"
        />
      </div>
      <div class="input-wrapper">
        <label for="sub_deadline">Submission deadline</label>
        <input
          type="datetime-local"
          name="sub_deadline"
          id="sub_deadline"
          v-model="submission_deadline"
        />
      </div>

      <div class="input-wrapper">
        <label for="tests_file">Tests File Name*</label>
        <input type="text" name="tests_file" id="tests_file" v-model="tests_file_name" />
        <span class="info"
          >*This is the name of the file containing the tests to run inside the project (without extension)</span
        >
      </div>
      <div class="input-wrapper">
        <label for="language">Project language</label>
        <select name="language" id="language" v-model="proj_language">
          <option
            v-for="language in ImplementedLanguages"
            :key="language.val"
            :value="language.val"
            class="purple-bg"
          >
            {{ language.display }}
          </option>
        </select>
      </div>
    </div>

    <div class="textarea-wrapper">
      <label for="description">Problem description</label>
      <textarea
        name="description"
        placeholder="Insert problem description"
        v-model="problem_description"
      >
      </textarea>
    </div>

    <div style="grid-row: span 2" class="flex-wrapper">
      <div class="checkbox-wrapper">
        <label for="manual_assessemnt">Require manual assessment</label>
        <input type="checkbox" name="manual_assessment" v-model="require_manual_evaluation" />
      </div>

      <span style="color: white">Static evaluation parameters</span>
      <template v-for="param in EvalParameters" :key="param">
        <div class="checkbox-wrapper" style="margin-left: 2rem">
          <input type="checkbox" :id="param" :value="param" v-model="evaluation_parameters" />
          <label :for="param">{{ param }}</label>
        </div>
      </template>

      <input
        type="button"
        value="Create Battle"
        :disabled="!enable_create_battle"
        style="width: 100%; padding: 1rem; align-self: flex-end; margin-top: 1rem;"
        @click.prevent="createBattleRequest()"
      />
    </div>

    <div class="upload-wrapper">
      <label>Project files</label>
      <div class="drop-area" @dragover.prevent="" @drop.prevent="">
        <p>Upload project files (.zip)</p>
        <input type="file" @change="onFilesSelected($event)"/>
      </div>
    </div>
  </div>
</template>

<style scoped>
.view-wrapper {
  grid-template-columns: 1fr 1fr;
  grid-template-rows: repeat(auto, 6);
  gap: 1rem 2rem;
}

.inputs-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem 2rem;
}

.checkbox-wrapper {
  color: white;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.checkbox-wrapper > * {
  margin-right: 1rem;
}

.textarea-wrapper,
.upload-wrapper {
  color: white;
  height: 100%;
  display: flex;
  flex-direction: column;
}

textarea {
  width: 100%;
  height: 100%;
  color: white;
  font-family: inherit;
  font-size: inherit;
  background-color: transparent;
  border: 1px solid white;
  resize: none;
}

.drop-area {
  border: 2px dashed #ccc;
  padding: 20px;
  text-align: center;
  cursor: pointer;
}

.flex-wrapper {
  display: grid;
  flex-direction: column;
}

.flex-wrapper > * {
  height: fit-content;
}
</style>
