import { store } from '@/store'

const SolutionRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/solution',
    name: 'solution',
    component: () => import('../../views/SolutionView.vue'),
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
      tournament_title: route.query.tournament,
      battle_title: route.query.battle,
      group_id: route.query.group
    })
  }
}

export default SolutionRoute
