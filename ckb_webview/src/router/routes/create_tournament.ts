import { store } from '@/store'

const CreateTournamentRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/new-tournament',
    name: 'new tournament',
    component: () => import('../../views/CreateTournamentView.vue'),
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

export default CreateTournamentRoute
