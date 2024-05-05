import { store } from '@/store'

const LoginRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/login',
    name: 'login',
    component: () => import('../../views/AccessView.vue'),
    children: [
      {
        path: '',
        name: 'login',
        component: () => import('../../components/LoginForm.vue')
      }
    ]
  }
}

export default LoginRoute
