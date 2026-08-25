<template>
  <div>
    <a-page-header title="职位管理" />
    <a-button type="primary" @click="handleAdd" style="margin-bottom:16px">新增职位</a-button>
    <a-table :columns="columns" :data-source="list" :pagination="pagination" :loading="loading" bordered rowKey="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'rank'">
          <a-tag color="blue">{{ record.rank || '-' }}</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a-popconfirm title="确认删除？" @confirm="handleDelete(record.id)">
            <a style="color:red">删除</a>
          </a-popconfirm>
        </template>
      </template>
    </a-table>

    <a-modal v-model:open="modalOpen" :title="modalTitle" @ok="handleOk">
      <a-form :model="form" layout="vertical">
        <a-form-item label="职位名称" :rules="[{ required: true, message: '请输入职位名称' }]">
          <a-input v-model:value="form.name" />
        </a-form-item>
        <a-form-item label="所属部门">
          <a-select v-model:value="form.deptId" :options="deptOptions" allow-clear />
        </a-form-item>
        <a-form-item label="职级">
          <a-select v-model:value="form.rank" :options="rankOptions" allow-clear />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { getPositionList, createPosition, updatePosition, deletePosition, getDeptTree } from '../../api'

const columns = [
  { title: '职位名称', dataIndex: 'name', key: 'name' },
  { title: '所属部门', dataIndex: 'deptName', key: 'deptName' },
  { title: '职级', dataIndex: 'rank', key: 'rank' }
]
const list = ref([])
const loading = ref(false)
const modalOpen = ref(false)
const modalTitle = ref('新增职位')
const form = reactive({ id: 0, name: '', deptId: null, rank: '' })
const deptOptions = ref<any[]>([])
const rankOptions = ref([{ label: 'P1', value: 'P1' }, { label: 'P2', value: 'P2' }, { label: 'P3', value: 'P3' }, { label: 'P4', value: 'P4' }, { label: 'P5', value: 'P5' }, { label: 'P6', value: 'P6' }, { label: 'P7', value: 'P7' }, { label: 'P8', value: 'P8' }])
const pagination = ref({ pageSize: 10, showSizeChanger: true })

async function loadList() {
  loading.value = true
  try {
    const res: any = await getPositionList()
    list.value = res.data || []
  } catch {}
  loading.value = false
}

async function loadDepts() {
  try {
    const res: any = await getDeptTree()
    deptOptions.value = []
    ;(res.data || []).forEach((d: any) => { deptOptions.value.push({ label: d.name, value: d.id }); if (d.children) d.children.forEach((c: any) => deptOptions.value.push({ label: '  ' + c.name, value: c.id })) })
  } catch {}
}

const handleAdd = () => { Object.assign(form, { id: 0, name: '', deptId: null, rank: '' }); modalTitle.value = '新增职位'; modalOpen.value = true }
const handleEdit = (r: any) => { Object.assign(form, r); modalTitle.value = '编辑职位'; modalOpen.value = true }
const handleDelete = async (id: number) => { await deletePosition(id); message.success('删除成功'); loadList() }
const handleOk = async () => {
  if (form.id > 0) { await updatePosition(form) } else { await createPosition(form) }
  message.success('保存成功'); modalOpen.value = false; loadList()
}

onMounted(() => { loadList(); loadDepts() })
</script>
