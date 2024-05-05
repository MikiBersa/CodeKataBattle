import { store } from '@/store'

const TournamentRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/tournament',
    name: 'tournament',
    component: () => import('../../views/TournamentView.vue'),
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

export default TournamentRoute
