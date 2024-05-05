import { store } from '@/store'

const BattleRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/battle',
    name: 'battle',
    component: () => import('../../views/BattleView.vue'),
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
    })
  }
}

export default BattleRoute
