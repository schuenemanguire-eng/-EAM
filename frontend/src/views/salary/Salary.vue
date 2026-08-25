<template>
  <div>
    <a-page-header title="薪资管理" />
    <div style="margin-bottom:16px;display:flex;gap:8px;align-items:center">
      <a-select v-model:value="queryYear" style="width:100px">
        <a-select-option :value="2026">2026</a-select-option>
        <a-select-option :value="2025">2025</a-select-option>
      </a-select>
      <a-select v-model:value="queryMonth" style="width:80px">
        <a-select-option v-for="m in 12" :key="m" :value="m">{{ m }}月</a-select-option>
      </a-select>
      <a-button @click="loadList">查询</a-button>
      <a-button type="primary" @click="handleAdd">新增薪资</a-button>
    </div>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="{ pageSize: 10 }" rowKey="id" bordered>
      <template #bodyCell="{ column, record }">
        <template v-if="['baseSalary','bonus','allowance','deduction','totalSalary'].includes(column.key)">
          <span :style="{color: column.key === 'totalSalary' ? '#1890ff' : 'inherit', fontWeight: column.key === 'totalSalary' ? 'bold' : 'normal'}">
            ¥ {{ record[column.key] || 0 }}
          </span>
        </template>
        <template v-if="column.key === 'action'">
          <a @click="handleEdit(record)">编辑</a>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="modalOpen" :title="modalTitle" @ok="handleOk">
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="员工" :rules="[{ required: true, message: '请选择员工' }]">
              <a-select v-model:value="form.employeeId">
                <a-select-option v-for="e in empOptions" :key="e.value" :value="e.value">{{ e.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="年份"><a-input-number v-model:value="form.year" :min="2020" :max="2030" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="月份"><a-input-number v-model:value="form.month" :min="1" :max="12" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="基本工资"><a-input-number v-model:value="form.baseSalary" :min="0" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="奖金"><a-input-number v-model:value="form.bonus" :min="0" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="津贴"><a-input-number v-model:value="form.allowance" :min="0" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="扣款"><a-input-number v-model:value="form.deduction" :min="0" style="width:100%" /></a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="发放日期"><a-date-picker v-model:value="form.payDate" format="YYYY-MM-DD" style="width:100%" /></a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getSalaryList, createSalary, updateSalary, getEmployeeList } from '../../api'

const columns = [
  { title: '员工', dataIndex: 'employeeName', key: 'employeeName', width: 100 },
  { title: '年份', dataIndex: 'year', key: 'year', width: 60 },
  { title: '月份', dataIndex: 'month', key: 'month', width: 60 },
  { title: '基本工资', dataIndex: 'baseSalary', key: 'baseSalary', width: 100 },
  { title: '奖金', dataIndex: 'bonus', key: 'bonus', width: 90 },
  { title: '津贴', dataIndex: 'allowance', key: 'allowance', width: 90 },
  { title: '扣款', dataIndex: 'deduction', key: 'deduction', width: 90 },
  { title: '实发工资', dataIndex: 'totalSalary', key: 'totalSalary', width: 110 },
  { title: '发放日期', dataIndex: 'payDate', key: 'payDate', width: 110 },
  { title: '操作', key: 'action', width: 80 }
]
const list = ref([])
const loading = ref(false)
const queryYear = ref(2026)
const queryMonth = ref(8)
const empOptions = ref<any[]>([])
const modalOpen = ref(false)
const modalTitle = ref('新增薪资')
const form = reactive({ id: 0, employeeId: null, year: 2026, month: 8, baseSalary: 0, bonus: 0, allowance: 0, deduction: 0, payDate: dayjs() })

async function loadList() {
  loading.value = true
  try {
    const res: any = await getSalaryList({ year: queryYear.value, month: queryMonth.value })
    list.value = res.data || []
  } catch {}
  loading.value = false
}

async function loadEmployees() {
  try {
    const res: any = await getEmployeeList({})
    empOptions.value = (res.data || []).map((e: any) => ({ label: `${e.name}(${e.employeeNo})`, value: e.id }))
  } catch {}
}

const handleAdd = () => {
  Object.assign(form, { id: 0, employeeId: null, year: queryYear.value, month: queryMonth.value, baseSalary: 0, bonus: 0, allowance: 0, deduction: 0, payDate: dayjs() })
  modalTitle.value = '新增薪资'
  modalOpen.value = true
}

const handleEdit = (r: any) => {
  Object.assign(form, r)
  form.payDate = r.payDate ? dayjs(r.payDate) : null
  modalTitle.value = '编辑薪资'
  modalOpen.value = true
}

const handleOk = async () => {
  const payload = { ...form, payDate: form.payDate ? form.payDate.format('YYYY-MM-DD') : null }
  if (form.id > 0) { await updateSalary(payload) } else { await createSalary(payload) }
  message.success('保存成功')
  modalOpen.value = false
  loadList()
}

onMounted(() => { loadList(); loadEmployees() })
</script>
