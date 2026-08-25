<template>
  <div>
    <a-page-header title="部门管理" />
    <a-button type="primary" @click="handleAdd" style="margin-bottom:16px">新增部门</a-button>
    <a-tree :tree-data="treeData" :show-line="true" block-node :expand-all="true">
      <template #title="{ dataRef }">
        <span>{{ dataRef.name }}</span>
        <a @click.stop.prevent="handleEdit(dataRef)" style="margin-left:8px">编辑</a>
        <a @click.stop.prevent="handleDelete(dataRef)" style="color:red;margin-left:4px">删除</a>
      </template>
    </a-tree>

    <a-modal v-model:open="modalOpen" :title="modalTitle" @ok="handleOk">
      <a-form :model="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="部门名称" :rules="[{ required: true, message: '请输入名称' }]">
              <a-input v-model:value="form.name" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="父部门">
              <a-select v-model:value="form.parentId" allow-clear placeholder="选择父部门">
                <a-select-option :value="0">（根部门）</a-select-option>
                <a-select-option v-for="d in deptList" :key="d.id" :value="d.id">{{ d.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input-number v-model:value="form.sort" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态">
              <a-radio-group v-model:value="form.status">
                <a-radio :value="1">启用</a-radio>
                <a-radio :value="0">禁用</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getDeptTree, createDept, updateDept, deleteDept } from '../../api'

interface DeptNode {
  id: number
  name: string
  parentId: number
  sort: number
  status: number
  key?: string
  children?: DeptNode[]
}

const treeData = ref<DeptNode[]>([])
const deptList = ref<DeptNode[]>([])
const modalOpen = ref(false)
const modalTitle = ref('新增部门')
const form = reactive({ id: 0, name: '', parentId: 0, sort: 0, status: 1 })

function loadTree() {
  getDeptTree().then((res: any) => {
    treeData.value = res.data || []
    deptList.value = flattenTree(res.data || [])
  })
}

function flattenTree(nodes: DeptNode[]): DeptNode[] {
  const result: DeptNode[] = []
  nodes.forEach(n => {
    result.push(n)
    if (n.children) result.push(...flattenTree(n.children))
  })
  return result
}

const handleAdd = () => {
  Object.assign(form, { id: 0, name: '', parentId: 0, sort: 0, status: 1 })
  modalTitle.value = '新增部门'
  modalOpen.value = true
}

const handleEdit = (node: DeptNode) => {
  Object.assign(form, { id: node.id, name: node.name, parentId: node.parentId, sort: node.sort, status: node.status })
  modalTitle.value = '编辑部门'
  modalOpen.value = true
}

const handleDelete = (node: DeptNode) => {
  Modal.confirm({ title: '确认删除', content: `确定要删除部门 "${node.name}" 吗？`, onOk: async () => {
    await deleteDept(node.id)
    message.success('删除成功')
    loadTree()
  }})
}

const handleOk = async () => {
  if (form.id > 0) await updateDept(form)
  else await createDept(form)
  message.success('保存成功')
  modalOpen.value = false
  loadTree()
}

onMounted(loadTree)
</script>
