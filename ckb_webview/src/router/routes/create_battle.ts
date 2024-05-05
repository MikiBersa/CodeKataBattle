import { store } from '@/store'

const CreateBattleRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/new-battle',
    name: 'create new battle',
    component: () => import('../../views/CreateBattleView.vue'),
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
        tournament_title: route.query.tournament
    })
  }
}

export default CreateBattleRoute
