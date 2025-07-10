import { createRouter, createWebHistory } from 'vue-router'
import HelloWorld from '@/components/HelloWorld.vue'
const NotFound = () => import('@/views/404.vue')
const CallApi = () => import('@/views/CallApi.vue')
const APIRequest = () => import('@/views/APIRequest.vue')

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HelloWorld
    },
    {
        path: '/call-api',
        name: 'CallApi',
        component: CallApi
    },
    {
        path: '/APIRequest',
        name: 'APIRequest',
        component: APIRequest
    },
    {
        path: '/:catchAll(.*)',
        name: 'NotFound',
        component: NotFound
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router
