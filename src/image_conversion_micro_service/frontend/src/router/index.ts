import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import UseNowView from '@/views/UseNowView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/usenow',
      name: 'usenow',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/UseNowView.vue'),
    },
    {
      path: '/openapi',
      name: 'openapi',
      component: () => import('@/views/OpenApiViewer.vue')
    },
  ],
})

export default router
