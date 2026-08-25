<template>
  <div>
    <a-page-header title="数据看板" />
    <a-row :gutter="[16,16]">
      <a-col :span="6" v-for="item in dashboardStats" :key="item.label">
        <a-card :bordered="false">
          <a-statistic :title="item.label" :value="item.value" :value-style="{color: item.color, fontSize: '28px'}" />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '../../api'

const dashboardStats = ref([
  { label: '员工总数', value: 0, color: '#1890ff' },
  { label: '在职员工', value: 0, color: '#52c41a' },
  { label: '本月新入职', value: 0, color: '#faad14' },
  { label: '部门数量', value: 0, color: '#722ed1' },
  { label: '待审批请假', value: 0, color: '#f5222d' },
  { label: '今日打卡', value: 0, color: '#13c2c2' },
  { label: '今日迟到', value: 0, color: '#eb2f96' }
])

onMounted(async () => {
  try {
    const res: any = await getDashboard({})
    const data = res.data
    if (data) {
      const values = [data.totalEmployees, data.activeEmployees, data.newThisMonth, data.totalDepartments, data.pendingLeaves, data.todayClockInCount, data.lateCountToday]
      dashboardStats.value.forEach((item, i) => { item.value = values[i] || 0 })
    }
  } catch {}
})
</script>
