<template>
  <div>
    <a-page-header title="考勤管理" />
    <a-row :gutter="16" style="margin-bottom:16px">
      <a-col :span="12">
        <a-card title="今日打卡" size="small">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-button type="primary" size="large" block @click="handleClock('in')" :disabled="todayRecord?.clockInTime">上班打卡</a-button>
            </a-col>
            <a-col :span="12" style="padding-left:8px">
              <a-button type="default" size="large" block @click="handleClock('out')" :disabled="!todayRecord?.clockInTime || !!todayRecord?.clockOutTime">下班打卡</a-button>
            </a-col>
          </a-row>
          <div v-if="todayRecord" style="margin-top:12px;text-align:center;color:#666">
            <span>上班: {{ todayRecord.clockInTime ? todayRecord.clockInTime.slice(11,16) : '未打卡' }}</span>
            <span style="margin:0 12px">|</span>
            <span>下班: {{ todayRecord.clockOutTime ? todayRecord.clockOutTime.slice(11,16) : '未打卡' }}</span>
            <a-tag style="margin-left:8px" :color="todayRecord.status === '正常' ? 'green' : 'red'">{{ todayRecord.status }}</a-tag>
          </div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="筛选条件" size="small">
          <a-row :gutter="8">
            <a-col :span="8"><a-date-picker v-model:value="startDate" placeholder="开始日期" @change="loadRecords" /></a-col>
            <a-col :span="8"><a-date-picker v-model:value="endDate" placeholder="结束日期" @change="loadRecords" /></a-col>
            <a-col :span="8"><a-button @click="loadRecords">查询</a-button></a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>

    <a-table :columns="columns" :data-source="records" :loading="loading" :pagination="{ pageSize: 10 }" bordered rowKey="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === '正常' ? 'green' : record.status === '迟到' ? 'orange' : 'red'">{{ record.status }}</a-tag>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { clockIn, getAttendanceRecords, getTodayAttendance } from '../../api'

const columns = [
  { title: '日期', dataIndex: 'date', key: 'date', width: 120 },
  { title: '上班时间', dataIndex: 'clockInTime', key: 'clockInTime', width: 140 },
  { title: '下班时间', dataIndex: 'clockOutTime', key: 'clockOutTime', width: 140 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 }
]
const records = ref([])
const todayRecord = ref(null)
const loading = ref(false)
const startDate = ref(dayjs())
const endDate = ref(dayjs())

async function loadRecords() {
  loading.value = true
  try {
    const params: any = {}
    if (startDate.value) params.start = startDate.value.format('YYYY-MM-DD')
    if (endDate.value) params.end = endDate.value.format('YYYY-MM-DD')
    const res: any = await getAttendanceRecords(params)
    records.value = res.data || []
  } catch {}
  loading.value = false
}

async function loadToday() {
  try {
    const res: any = await getTodayAttendance()
    todayRecord.value = res.data || null
  } catch {}
}

const handleClock = async (type: string) => {
  await clockIn({ clockType: type })
  message.success(type === 'in' ? '上班打卡成功' : '下班打卡成功')
  loadToday()
  loadRecords()
}

onMounted(() => { loadToday(); loadRecords() })
</script>
