import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    redirect: '/dashboard',
    component: () => import('../views/layout/Layout.vue'),
    children: [
      { path: '/dashboard', name: 'Dashboard', component: () => import('../views/dashboard/Dashboard.vue'), meta: { title: '数据看板' } },
      { path: '/dept', name: 'Dept', component: () => import('../views/dept/Dept.vue'), meta: { title: '部门管理' } },
      { path: '/position', name: 'Position', component: () => import('../views/position/Position.vue'), meta: { title: '职位管理' } },
      { path: '/employee', name: 'Employee', component: () => import('../views/employee/Employee.vue'), meta: { title: '员工管理' } },
      { path: '/attendance', name: 'Attendance', component: () => import('../views/attendance/Attendance.vue'), meta: { title: '考勤管理' } },
      { path: '/leave', name: 'Leave', component: () => import('../views/leave/Leave.vue'), meta: { title: '请假管理' } },
      { path: '/salary', name: 'Salary', component: () => import('../views/salary/Salary.vue'), meta: { title: '薪资管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.name !== 'Login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
