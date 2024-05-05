import { store } from '@/store'

const ManageTournamentRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/manage-tournament',
    name: 'manage tournament',
    component: () => import('../../views/ManageTournamentView.vue'),
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
    },
    props: (route) => ({
      title: route.query.title
    })
  }
}

export default ManageTournamentRoute
