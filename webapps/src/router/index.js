import { createRouter, createWebHistory } from 'vue-router'
import NotFound from '../views/404.vue' // 导入404组件

const routes = [
    // 你的其他路由...
    {
        path: '/home',
        name: 'home',
        component: () => import('../views/HomeView.vue')
    },

    // 添加404路由 - 必须放在最后
    {
        path: '/:pathMatch(.*)*', // 匹配所有路径
        name: 'not-found',
        component: NotFound
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router