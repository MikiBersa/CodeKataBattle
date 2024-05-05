import { store } from '@/store'

const InvitesRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/invites',
    name: 'invites',
    component: () => import('../../views/InvitationsView.vue'),
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

export default InvitesRoute
