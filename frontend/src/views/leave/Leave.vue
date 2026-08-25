<template>
  <div>
    <a-page-header title="请假管理" />
    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="apply" tab="申请请假">
        <a-button type="primary" @click="handleApply" style="margin-bottom:16px">提交请假申请</a-button>
        <a-table :columns="myColumns" :data-source="myLeaves" :loading="loading1" rowKey="id" bordered>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="statusColor(record.status)">{{ record.status }}</a-tag>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
      <a-tab-pane key="approve" tab="审批列表">
        <a-table :columns="approveColumns" :data-source="pendingLeaves" :loading="loading2" rowKey="id" bordered>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag color="orange">{{ record.status }}</a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-popconfirm title="确认批准？" @confirm="doApprove(record.id, '已批准')">
                <a-button type="primary" size="small">批准</a-button>
              </a-popconfirm>
              <a-popconfirm title="确认拒绝？" @confirm="doApprove(record.id, '已拒绝')">
                <a-button type="danger" size="small" style="margin-left:8px">拒绝</a-button>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>

    <a-modal v-model:open="applyModal" title="提交请假申请" @ok="submitApply">
      <a-form :model="applyForm" layout="vertical">
        <a-form-item label="请假类型" :rules="[{ required: true, message: '请选择类型' }]">
          <a-select v-model:value="applyForm.leaveType">
            <a-select-option value="ANNUAL">年假</a-select-option>
            <a-select-option value="SICK">病假</a-select-option>
            <a-select-option value="PERSONAL">事假</a-select-option>
            <a-select-option value="MARITAL">婚假</a-select-option>
            <a-select-option value="MATERNITY">产假</a-select-option>
            <a-select-option value="BEREAVEMENT">丧假</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="开始时间" :rules="[{ required: true, message: '请选择开始时间' }]">
          <a-date-picker v-model:value="applyForm.startTime" show-time format="YYYY-MM-DD HH:mm" style="width:100%" />
        </a-form-item>
        <a-form-item label="结束时间" :rules="[{ required: true, message: '请选择结束时间' }]">
          <a-date-picker v-model:value="applyForm.endTime" show-time format="YYYY-MM-DD HH:mm" style="width:100%" />
        </a-form-item>
        <a-form-item label="请假原因">
          <a-textarea v-model:value="applyForm.reason" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { applyLeave, getMyLeaves, getPendingLeaves, approveLeave } from '../../api'

const activeTab = ref('apply')
const myLeaves = ref([])
const pendingLeaves = ref([])
const loading1 = ref(false)
const loading2 = ref(false)
const applyModal = ref(false)
const applyForm = reactive({ leaveType: '', startTime: null, endTime: null, reason: '' })

const myColumns = [
  { title: '请假类型', dataIndex: 'leaveType', key: 'leaveType' },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime' },
  { title: '天数', dataIndex: 'totalDays', key: 'totalDays' },
  { title: '原因', dataIndex: 'reason', key: 'reason' },
  { title: '审批人', dataIndex: 'approverName', key: 'approverName' },
  { title: '状态', dataIndex: 'status', key: 'status' }
]
const approveColumns = [
  { title: '申请人', dataIndex: 'employeeName', key: 'employeeName' },
  { title: '请假类型', dataIndex: 'leaveType', key: 'leaveType' },
  { title: '开始时间', dataIndex: 'startTime', key: 'startTime' },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime' },
  { title: '天数', dataIndex: 'totalDays', key: 'totalDays' },
  { title: '原因', dataIndex: 'reason', key: 'reason' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '操作', key: 'action', width: 140 }
]

function statusColor(s: string) {
  if (s === '已批准') return 'green'
  if (s === '已拒绝') return 'red'
  return 'orange'
}

async function loadMy() {
  loading1.value = true
  try { const res: any = await getMyLeaves(); myLeaves.value = res.data || [] } catch {}
  loading1.value = false
}

async function loadPending() {
  loading2.value = true
  try { const res: any = await getPendingLeaves(); pendingLeaves.value = res.data || [] } catch {}
  loading2.value = false
}

const handleApply = () => {
  Object.assign(applyForm, { leaveType: '', startTime: null, endTime: null, reason: '' })
  applyModal.value = true
}

const submitApply = async () => {
  const payload = {
    leaveType: applyForm.leaveType,
    startTime: applyForm.startTime?.format('YYYY-MM-DD HH:mm:ss'),
    endTime: applyForm.endTime?.format('YYYY-MM-DD HH:mm:ss'),
    reason: applyForm.reason
  }
  await applyLeave(payload)
  message.success('请假申请已提交')
  applyModal.value = false
  loadMy()
}

const doApprove = async (id: number, status: string) => {
  await approveLeave({ id, status })
  message.success('审批成功')
  loadPending()
  loadMy()
}

watch(activeTab, () => {
  if (activeTab.value === 'approve') loadPending()
  else loadMy()
})

onMounted(loadMy)
</script>
