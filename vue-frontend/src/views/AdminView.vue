<template>
  <div class="admin-page" v-loading="pageLoading">
    <h1 class="text-2xl font-bold mb-4">后台管理页面</h1>

    <!-- 管理员功能 -->
    <el-card v-if="isRole('ROLE_ADMIN')" class="mb-6">
      <h2 class="text-xl font-semibold mb-2">解决方案管理</h2>
      <div class="mb-4">
        <el-button type="primary" @click="openCreateDialog">创建解决方案</el-button>
        <el-button type="success" @click="loadMySolutions">刷新我的解决方案</el-button>
      </div>
      <el-table :data="adminStore.solutions" style="width: 100%">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="submitReview(row.id)">提交审核</el-button>
            <el-button type="danger" size="small" @click="confirmDeleteSolution(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 开发者功能 -->
    <el-card v-if="isRole('ROLE_DEV')" class="mb-6">
      <h2 class="text-xl font-semibold mb-2">开发者管理</h2>

      <!-- 用户管理 -->
      <div class="mb-4">
        <el-button type="success" @click="loadUsers">刷新用户列表</el-button>
      </div>
      <el-table :data="developerStore.users" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="roles" label="角色">
          <template #default="{ row }">{{ row.roles.join(", ") }}</template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button
              v-if="!row.roles.includes('ROLE_ADMIN')"
              type="primary"
              size="small"
              @click="confirmPromote(row.id)"
            >
              提升为管理员
            </el-button>
            <el-button
              v-else
              type="danger"
              size="small"
              @click="confirmRevoke(row.id)"
            >
              撤销管理员
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 管理员申请 -->
      <div class="mt-6">
        <el-button type="success" @click="loadAdminApplications">刷新管理员申请</el-button>
      </div>
      <el-table :data="developerStore.pendingApplications" style="width: 100%" class="mt-2">
        <el-table-column prop="applicantUsername" label="申请用户" />
        <el-table-column prop="reason" label="申请理由" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="approveApp(row.id)">批准</el-button>
            <el-button type="danger" size="small" @click="rejectApp(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 待审核解决方案 -->
      <div class="mt-6">
        <el-button type="success" @click="loadPendingSolutions">刷新待审核解决方案</el-button>
      </div>
      <el-table :data="developerStore.pendingSolutions" style="width: 100%" class="mt-2">
        <el-table-column prop="title" label="解决方案标题" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="approveSol(row.id)">批准</el-button>
            <el-button type="danger" size="small" @click="rejectSol(row.id)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 公共信息 -->
    <el-card>
      <h2 class="text-xl font-semibold mb-2">我的信息</h2>
      <div v-if="userStore.userInfo">
        <p><strong>ID：</strong>{{ userStore.userInfo.id }}</p>
        <p><strong>用户名：</strong>{{ userStore.userInfo.username }}</p>
        <p><strong>角色：</strong>{{ userStore.userInfo.roles.join(', ') }}</p>
      </div>
    </el-card>

    <!-- 创建解决方案对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建解决方案" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="createForm.title" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="createForm.categoryId" placeholder="请选择分类">
            <el-option label="启动问题" value="startup" />
            <el-option label="联机问题" value="network" />
            <el-option label="模组问题" value="mod" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-radio-group v-model="createForm.difficulty">
            <el-radio label="简单" />
            <el-radio label="中等" />
            <el-radio label="困难" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="适用版本">
          <el-input v-model="createForm.version" placeholder="例如 1.20.1" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="createForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="createForm.notes" type="textarea" />
        </el-form-item>
        <el-form-item label="步骤">
          <el-input
            v-for="(step, index) in createForm.steps"
            :key="index"
            v-model="createForm.steps[index]"
            placeholder="请输入解决步骤"
            class="mb-2"
          />
          <el-button type="primary" size="small" @click="createForm.steps.push('')">
            添加步骤
          </el-button>
        </el-form-item>
        <el-form-item label="上传图片">
          <input type="file" multiple @change="handleFileUpload" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { ElMessageBox } from "element-plus"
import { useUserStore } from "@/store/user"
import { useAdminStore } from "@/store/admin"
import { useDeveloperStore } from "@/store/developer"

const userStore = useUserStore()
const adminStore = useAdminStore()
const developerStore = useDeveloperStore()

const pageLoading = ref(true)

// 角色判断
const isRole = (role: string) => {
  const roles = userStore.userInfo?.roles || []
  return roles.includes(role)
}

// 管理员方法
const loadMySolutions = async () => {
  await adminStore.fetchSolutions(0, 10)
}
const submitReview = async (id: string) => {
  await adminStore.submitSolutionReview(id)
  await loadMySolutions()
}
const confirmDeleteSolution = (id: string) => {
  ElMessageBox.confirm("确定要删除该解决方案吗？", "提示", {
    type: "warning"
  }).then(async () => {
    await adminStore.deleteExistingSolution(id)
    await loadMySolutions()
  })
}

// 创建解决方案
const createDialogVisible = ref(false)
const createForm = ref({
  categoryId: "",
  title: "",
  difficulty: "中等",
  version: "",
  description: "",
  notes: "",
  steps: [] as string[],
  imageFiles: [] as File[]
})
const openCreateDialog = () => {
  createDialogVisible.value = true
}
const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files) {
    createForm.value.imageFiles = Array.from(input.files)
  }
}
const submitCreate = async () => {
  await adminStore.createNewSolution(createForm.value)
  createDialogVisible.value = false
  await loadMySolutions()
}

// 开发者方法（增加确认框）
const loadUsers = async () => {
  await developerStore.fetchAllUsers(0, 10)
}
const confirmPromote = (id: number) => {
  ElMessageBox.confirm("确定要将该用户提升为管理员吗？", "提示", {
    type: "warning"
  }).then(async () => {
    await developerStore.promoteUserToAdmin(id)
    await loadUsers()
  })
}
const confirmRevoke = (id: number) => {
  ElMessageBox.confirm("确定要撤销该用户的管理员权限吗？", "提示", {
    type: "warning"
  }).then(async () => {
    await developerStore.revokeAdminFromUser(id)
    await loadUsers()
  })
}
const loadAdminApplications = async () => {
  await developerStore.fetchPendingApplications(0, 10)
}
const approveApp = async (id: number) => {
  await developerStore.approveApplication(id)
  await loadAdminApplications()
}
const rejectApp = async (id: number) => {
  await developerStore.rejectApplication(id, "不符合条件")
  await loadAdminApplications()
}
const loadPendingSolutions = async () => {
  await developerStore.fetchPendingSolutions(0, 10)
}
const approveSol = async (id: string) => {
  await developerStore.approveSolutionReview(id)
  await loadPendingSolutions()
}
const rejectSol = async (id: string) => {
  await developerStore.rejectSolutionReview(id, "理由不足")
  await loadPendingSolutions()
}

// 页面初始化统一加载
onMounted(async () => {
  try {
    const tasks: Promise<any>[] = []
    if (isRole("ROLE_ADMIN")) {
      tasks.push(loadMySolutions())
    }
    if (isRole("ROLE_DEV")) {
      tasks.push(loadUsers(), loadAdminApplications(), loadPendingSolutions())
    }
    await Promise.all(tasks)
  } finally {
    pageLoading.value = false
  }
})
</script>

<style scoped>
.admin-page {
  padding: 16px;
}
</style>
