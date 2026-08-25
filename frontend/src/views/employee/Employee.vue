<template>
  <div>
    <a-page-header title="员工管理" />
    <div style="margin-bottom:16px;display:flex;gap:8px">
      <a-input-search v-model:value="keyword" placeholder="搜索姓名/工号" @search="loadList" style="width:240px" />
      <a-select v-model:value="selectedDept" placeholder="按部门筛选" style="width:200px" allow-clear @change="loadList">
        <a-select-option v-for="d in deptOptions" :key="d.value" :value="d.value">{{ d.label }}</a-select-option>
      </a-select>
      <a-button type="primary" @click="handleAdd">新增员工</a-button>
    </div>
    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="{ pageSize: 10 }" bordered rowKey="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'gender'">
          <span>{{ record.gender === 1 ? '男' : record.gender === 2 ? '女' : '-' }}</span>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">{{ record.status === 1 ? '在职' : '离职' }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm v-if="record.status === 1" title="确认办理离职？" @confirm="quit(record.id)">
            <a style="color:orange">离职</a>
          </a-popconfirm>
          <a-divider type="vertical" />
          <a-popconfirm title="确认删除？" @confirm="handleDelete(record.id)">
            <a style="color:red">删除</a>
          </a-popconfirm>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="modalOpen" :title="modalTitle" :width="640" @ok="handleOk">
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="姓名" :rules="[{ required: true, message: '请输入姓名' }]">
              <a-input v-model:value="form.name" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="工号" :rules="[{ required: true, message: '请输入工号' }]">
              <a-input v-model:value="form.employeeNo" placeholder="如 EMP011" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="部门">
              <a-select v-model:value="form.deptId">
                <a-select-option v-for="d in deptOptions" :key="d.value" :value="d.value">{{ d.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="职位">
              <a-select v-model:value="form.positionId">
                <a-select-option v-for="p in posOptions" :key="p.value" :value="p.value">{{ p.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="入职日期">
              <a-date-picker v-model:value="form.entryDate" format="YYYY-MM-DD" style="width:100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态">
              <a-radio-group v-model:value="form.status">
                <a-radio :value="1">在职</a-radio>
                <a-radio :value="0">离职</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="手机">
              <a-input v-model:value="form.phone" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱">
              <a-input v-model:value="form.email" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="性别">
              <a-select v-model:value="form.gender">
                <a-select-option :value="1">男</a-select-option>
                <a-select-option :value="2">女</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="出生日期">
              <a-date-picker v-model:value="form.birthday" format="YYYY-MM-DD" style="width:100%" />
            </a-form-item>
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
import { getEmployeeList, createEmployee, updateEmployee, deleteEmployee, quitEmployee, getDeptTree, getPositionList } from '../../api'

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name', width: 100 },
  { title: '工号', dataIndex: 'employeeNo', key: 'employeeNo', width: 110 },
  { title: '部门', dataIndex: 'deptName', key: 'deptName', width: 120 },
  { title: '职位', dataIndex: 'positionName', key: 'positionName', width: 130 },
  { title: '入职日期', dataIndex: 'entryDate', key: 'entryDate', width: 110 },
  { title: '手机', dataIndex: 'phone', key: 'phone', width: 120 },
  { title: '性别', dataIndex: 'gender', key: 'gender', width: 60 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 70 },
  { title: '操作', key: 'action', width: 150 }
]
const list = ref([])
const loading = ref(false)
const keyword = ref('')
const selectedDept = ref(null)
const deptOptions = ref<any[]>([])
const posOptions = ref<any[]>([])
const modalOpen = ref(false)
const modalTitle = ref('新增员工')
const form = reactive({ id: 0, name: '', employeeNo: '', deptId: null, positionId: null, entryDate: dayjs(), status: 1, phone: '', email: '', gender: 1, birthday: null })

async function loadList() {
  loading.value = true
  try {
    const res: any = await getEmployeeList({ keyword: keyword.value || undefined, deptId: selectedDept.value || undefined })
    list.value = res.data || []
  } catch {}
  loading.value = false
}

async function loadDeptsAndPos() {
  try {
    const [dRes, pRes]: any[] = await Promise.all([getDeptTree(), getPositionList()])
    deptOptions.value = []
    ;(dRes.data || []).forEach((d: any) => {
      deptOptions.value.push({ label: d.name, value: d.id })
      if (d.children) d.children.forEach((c: any) => deptOptions.value.push({ label: '  ' + c.name, value: c.id }))
    })
    posOptions.value = (pRes.data || []).map((p: any) => ({ label: p.name, value: p.id }))
  } catch {}
}

const handleAdd = () => {
  Object.assign(form, { id: 0, name: '', employeeNo: '', deptId: null, positionId: null, entryDate: dayjs(), status: 1, phone: '', email: '', gender: 1, birthday: null })
  modalTitle.value = '新增员工'
  modalOpen.value = true
}

const handleEdit = (r: any) => {
  Object.assign(form, r)
  form.entryDate = r.entryDate ? dayjs(r.entryDate) : null
  form.birthday = r.birthday ? dayjs(r.birthday) : null
  modalTitle.value = '编辑员工'
  modalOpen.value = true
}

const handleDelete = async (id: number) => {
  await deleteEmployee(id)
  message.success('删除成功')
  loadList()
}

const quit = async (id: number) => {
  await quitEmployee(id)
  message.success('离职办理成功')
  loadList()
}

const handleOk = async () => {
  const payload = {
    ...form,
    entryDate: form.entryDate ? form.entryDate.format('YYYY-MM-DD') : null,
    birthday: form.birthday ? form.birthday.format('YYYY-MM-DD') : null
  }
  if (form.id > 0) {
    await updateEmployee(payload)
  } else {
    await createEmployee(payload)
  }
  message.success('保存成功')
  modalOpen.value = false
  loadList()
}

onMounted(() => { loadList(); loadDeptsAndPos() })
</script>
