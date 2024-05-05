import { store } from '@/store'

const DashboardRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/dashboard',
    name: 'dashboard',
    component: () => import('../../views/DashboardView.vue'),
    beforeEnter: (to, from, next) => {
      if (
        store.getters.isLoggedIn &&
        (whitelist_accounts === undefined ||
          whitelist_accounts?.includes(store.getters.getAccountType))
      ) {
        next()
      } else {
        next('/') // return to home
      }
    }
  }
}

export default DashboardRoute
