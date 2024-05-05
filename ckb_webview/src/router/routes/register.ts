import { store } from '@/store'

const RegisterRoute = (whitelist_accounts?: string[]) => {
  return {
    path: '/register',
    name: 'register',
    component: () => import('../../views/AccessView.vue'),
    children: [
      {
        path: '',
        name: 'register',  
        component: () => import('../../components/RegisterForm.vue')
      }
    ]
  }
}

export default RegisterRoute
