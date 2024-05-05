import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { store } from './store'
import 'highlight.js/styles/github-dark.css'
import 'highlight.js/lib/common'
import hljsVuePlugin from '@highlightjs/vue-plugin'

const app = createApp(App)

app.use(router)
app.use(store)
app.use(hljsVuePlugin)

app.mount('#app')
