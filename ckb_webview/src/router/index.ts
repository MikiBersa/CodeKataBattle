import { createRouter, createWebHistory } from 'vue-router'
import { AccountType } from '@/util/custom_types'

import HomeRoute from './routes/home'
import LoginRoute from './routes/login'
import RegisterRoute from './routes/register'
import DashboardRoute from './routes/dashboard'
import TournamentsRoute from './routes/tournaments'
import TournamentRoute from './routes/tournament'
import BattleRoute from './routes/battle'
import CreateTournamentRoute from './routes/create_tournament'
import InvitesRoute from './routes/invites'
import ManageTournamentRoute from './routes/manage_tournament'
import CreateBattleRoute from './routes/create_battle'
import SolutionRoute from './routes/solution'
import { store } from '@/store'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    HomeRoute(), 
    LoginRoute(),
    RegisterRoute(),
    DashboardRoute([AccountType.Educator, AccountType.Student]),
    TournamentsRoute([AccountType.Educator, AccountType.Student]), 
    TournamentRoute([AccountType.Educator, AccountType.Student]),
    BattleRoute([AccountType.Educator, AccountType.Student]),
    CreateTournamentRoute([AccountType.Educator]),
    InvitesRoute([AccountType.Educator, AccountType.Student]),
    ManageTournamentRoute([AccountType.Educator]),
    CreateBattleRoute([AccountType.Educator]),
    SolutionRoute([AccountType.Educator])
  ]
})

router.beforeEach((to, from, next) => {
  if (['/login', '/register', '/'].includes(to.path) || store.getters.isLoggedIn) next();
  else next('/');
})

export default router
