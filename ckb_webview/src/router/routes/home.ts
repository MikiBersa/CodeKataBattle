import { store } from '@/store'
import LandingView from '../../views/LandingView.vue'

const HomeRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/',
    name: 'home',
    component: LandingView
  }
}

export default HomeRoute
