<template>
  <a-layout class="layout">
    <a-layout-sider v-model:collapsed="collapsed" collapsible theme="dark">
      <div class="logo">员工管理系统</div>
      <a-menu theme="dark" mode="inline" v-model:selectedKeys="selectedKeys" @click="handleMenuClick">
        <a-menu-item key="dashboard"><template #icon><DashboardOutlined /></template>数据看板</a-menu-item>
        <a-menu-item key="dept"><template #icon><ApartmentOutlined /></template>部门管理</a-menu-item>
        <a-menu-item key="position"><template #icon><BankOutlined /></template>职位管理</a-menu-item>
        <a-menu-item key="employee"><template #icon><TeamOutlined /></template>员工管理</a-menu-item>
        <a-menu-item key="attendance"><template #icon><ClockCircleOutlined /></template>考勤管理</a-menu-item>
        <a-menu-item key="leave"><template #icon><CalendarOutlined /></template>请假管理</a-menu-item>
        <a-menu-item key="salary"><template #icon><DollarOutlined /></template>薪资管理</a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <a-dropdown>
          <a class="user-info">
            <UserOutlined /> {{ userStore.user?.employeeName || userStore.user?.username }}
            <span class="role-tag">{{ roleLabel }}</span>
          </a>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="handleLogout"><LogoutOutlined /> 退出登录</a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  DashboardOutlined, ApartmentOutlined, BankOutlined, TeamOutlined,
  ClockCircleOutlined, CalendarOutlined, DollarOutlined, UserOutlined, LogoutOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '../../stores/user'
import { getCurrentUser } from '../../api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)
const selectedKeys = ref([String(route.name || 'dashboard').toLowerCase()])

const roleMap: Record<string, string> = { admin: '管理员', hr: '人事', supervisor: '主管', employee: '员工' }
const roleLabel = computed(() => roleMap[userStore.role] || userStore.role)

// 菜单 key(小写) -> 路由 name(大写)
const menuMap: Record<string, string> = {
  dashboard: 'Dashboard', dept: 'Dept', position: 'Position',
  employee: 'Employee', attendance: 'Attendance', leave: 'Leave', salary: 'Salary'
}

const handleMenuClick = ({ key }: { key: string }) => {
  const name = menuMap[key]
  if (name) router.push({ name: name as any })
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(async () => {
  try {
    const res: any = await getCurrentUser()
    userStore.setUser(res.data)
  } catch {}
})
</script>

<style scoped>
.layout { min-height: 100vh; }
.logo { height: 64px; margin: 16px; color: white; font-size: 18px; font-weight: bold; display: flex; align-items: center; justify-content: center; }
.header { background: white; padding: 0 24px; display: flex; justify-content: flex-end; align-items: center; box-shadow: 0 1px 4px rgba(0,0,0,0.08); }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; font-size: 14px; }
.role-tag { color: #888; font-size: 12px; margin-left: 4px; }
.content { margin: 16px 24px; padding: 24px; background: white; min-height: 280px; }
</style>
