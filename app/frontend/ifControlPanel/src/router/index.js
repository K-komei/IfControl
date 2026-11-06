import { createRouter, createWebHistory } from 'vue-router'

const itemMasterPath = '/app/online/ItemMasterSearch'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: itemMasterPath,
    },
    {
      path: itemMasterPath,
      name: 'item-master-search',
      component: () => import('../components/ItemMasterSearch.vue'),
    },
    {
      path: '/app/online/salesList',
      name: 'sales-list',
      component: () => import('../components/salesList.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: itemMasterPath,
    },
  ],
})

export default router
