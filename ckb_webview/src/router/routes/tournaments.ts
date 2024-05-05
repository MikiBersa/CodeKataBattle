import { store } from '@/store'

const TournamentsRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/tournaments',
    name: 'tournaments',
    component: () => import('../../views/TournamentsView.vue'),
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

export default TournamentsRoute
