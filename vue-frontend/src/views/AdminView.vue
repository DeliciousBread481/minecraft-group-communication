<template>
  <div class="admin-page" v-loading="pageLoading">
    <h1 class="text-2xl font-bold mb-4">后台管理页面</h1>

    <!-- 管理员功能 -->
    <el-card v-if="isRole('ROLE_ADMIN')" class="admin-card">
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
    <el-card v-if="isRole('ROLE_DEV')" class="admin-card">
      <h2 class="text-xl font-semibold mb-2">开发者管理</h2>

      <!-- 用户管理 -->
      <div class="mb-4">
        <el-button type="success" @click="loadUsers">刷新用户列表</el-button>
      </div>
      <el-table :data="developerStore.users" style="width: 100%">
        <el-table-column prop="nickname" label="用户名" />
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
    <el-card class="admin-card">
      <h2 class="text-xl font-semibold mb-2">我的信息</h2>
      <div v-if="userStore.userInfo">
        <p><strong>ID：</strong>{{ userStore.userInfo.id }}</p>
        <p><strong>用户名：</strong>{{ userStore.userInfo.username }}</p>
        <p><strong>角色：</strong>{{ userStore.userInfo.roles.join(', ') }}</p>
      </div>
    </el-card>

    <!-- 创建解决方案对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建解决方案" width="700px" >
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="标题" prop="title" class="required-label">
          <el-input v-model="createForm.title" placeholder="请输入解决方案标题" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId" class="required-label">
          <el-select v-model="createForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option label="启动问题" value="startup" />
            <el-option label="联机问题" value="network" />
            <el-option label="模组问题" value="mod" />
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty" class="required-label">
          <el-radio-group v-model="createForm.difficulty">
            <el-radio label="简单" />
            <el-radio label="中等" />
            <el-radio label="困难" />
          </el-radio-group>
        </el-form-item>
        <el-form-item label="适用版本" prop="version" class="required-label">
          <el-input v-model="createForm.version" placeholder="例如 1.20.1" />
        </el-form-item>
        <el-form-item label="描述" prop="description" class="required-label">
          <el-input v-model="createForm.description" type="textarea" :rows="3" placeholder="请输入问题描述" />
        </el-form-item>
        <el-form-item label="补充说明">
          <el-input v-model="createForm.notes" type="textarea" :rows="2" placeholder="可选补充信息" />
        </el-form-item>
        <el-form-item label="解决步骤" prop="steps" class="required-label">
          <div v-for="(step, index) in createForm.steps" :key="index" class="step-input">
            <el-input
              v-model="createForm.steps[index]"
              :placeholder="'步骤 ' + (index + 1)"
            />
            <el-button type="danger" size="small" @click="removeStep(index)" :disabled="createForm.steps.length <= 1">
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addStep">
            添加步骤
          </el-button>
        </el-form-item>
        <el-form-item label="上传图片">
          <input type="file" multiple @change="handleFileUpload" />
          <div class="el-upload__tip">最多可上传5张图片，每张不超过2MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="form-footer">
          <div>已添加 {{ createForm.steps.length }} 个步骤</div>
          <div>
            <el-button @click="createDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitCreate">创建</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from "vue"
import { ElMessageBox, ElMessage } from "element-plus"
import { useUserStore } from "@/store/user"
import { useAdminStore } from "@/store/admin"
import { useDeveloperStore } from "@/store/developer"

const userStore = useUserStore()
const adminStore = useAdminStore()
const developerStore = useDeveloperStore()

const pageLoading = ref(true)
const createDialogVisible = ref(false)
const createFormRef = ref()

// 创建表单数据
const createForm = reactive({
  categoryId: "",
  title: "",
  difficulty: "中等",
  version: "",
  description: "",
  notes: "",
  steps: [""],
  imageFiles: [] as File[]
})

// 表单验证规则
const createRules = {
  title: [
    { required: true, message: '请输入解决方案标题', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  version: [
    { required: true, message: '请输入适用版本', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入问题描述', trigger: 'blur' }
  ],
  steps: [
    {
      validator: (rule: any, value: string[], callback: any) => {
        if (!value || value.length === 0) {
          callback(new Error('至少需要一个解决步骤'));
        } else if (value.some(step => !step.trim())) {
          callback(new Error('解决步骤不能为空'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
}

// 角色判断
const isRole = (role: string) => {
  const roles = userStore.userInfo?.roles || []
  return roles.includes(role)
}

// 步骤管理
const addStep = () => {
  createForm.steps.push("")
}

const removeStep = (index: number) => {
  if (createForm.steps.length > 1) {
    createForm.steps.splice(index, 1)
  }
}

// 打开创建对话框
const openCreateDialog = () => {
  createForm.categoryId = ""
  createForm.title = ""
  createForm.difficulty = "中等"
  createForm.version = ""
  createForm.description = ""
  createForm.notes = ""
  createForm.steps = [""]
  createForm.imageFiles = []

  if (createFormRef.value) {
    createFormRef.value.clearValidate()
  }

  createDialogVisible.value = true
}

// 文件上传处理
const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files) {
    createForm.imageFiles = Array.from(input.files)
  }
}

// 提交创建
const submitCreate = async () => {
  if (!createFormRef.value) return

  const valid = await createFormRef.value.validate()
  if (!valid) return

  // 过滤空步骤
  createForm.steps = createForm.steps.filter(step => step.trim() !== '')

  // 确保至少有一个步骤
  if (createForm.steps.length === 0) {
    ElMessage.error('至少需要一个解决步骤')
    return
  }

  pageLoading.value = true

  try {
    await adminStore.createNewSolution(createForm)
    ElMessage.success('解决方案创建成功')
    createDialogVisible.value = false
    await loadMySolutions()
  } catch (error: any) {
    ElMessage.error('创建解决方案失败: ' + error.message)
  } finally {
    pageLoading.value = false
  }
}

// 管理员方法
const loadMySolutions = async () => {
  try {
    await adminStore.fetchSolutions(0, 10)
  } catch (error: any) {
    ElMessage.error('加载解决方案失败: ' + error.message)
  }
}

const submitReview = async (id: string) => {
  try {
    await adminStore.submitSolutionReview(id)
    ElMessage.success('已提交审核')
    await loadMySolutions()
  } catch (error: any) {
    ElMessage.error('提交审核失败: ' + error.message)
  }
}

const confirmDeleteSolution = (id: string) => {
  ElMessageBox.confirm("确定要删除该解决方案吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await adminStore.deleteExistingSolution(id)
      ElMessage.success('解决方案已删除')
      await loadMySolutions()
    } catch (error: any) {
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {})
}

// 开发者方法
const loadUsers = async () => {
  try {
    await developerStore.fetchAllUsers(0, 10)
  } catch (error: any) {
    ElMessage.error('加载用户列表失败: ' + error.message)
  }
}

const confirmPromote = (id: number) => {
  ElMessageBox.confirm("确定要将该用户提升为管理员吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await developerStore.promoteUserToAdmin(id)
      ElMessage.success('已提升为管理员')
      await loadUsers()
    } catch (error: any) {
      ElMessage.error('提升失败: ' + error.message)
    }
  }).catch(() => {})
}

const confirmRevoke = (id: number) => {
  ElMessageBox.confirm("确定要撤销该用户的管理员权限吗？", "提示", {
    type: "warning"
  }).then(async () => {
    try {
      await developerStore.revokeAdminFromUser(id)
      ElMessage.success('已撤销管理员权限')
      await loadUsers()
    } catch (error: any) {
      ElMessage.error('撤销失败: ' + error.message)
    }
  }).catch(() => {})
}

const loadAdminApplications = async () => {
  try {
    await developerStore.fetchPendingApplications(0, 10)
  } catch (error: any) {
    ElMessage.error('加载管理员申请失败: ' + error.message)
  }
}

const approveApp = async (id: number) => {
  try {
    await developerStore.approveApplication(id)
    ElMessage.success('已批准申请')
    await loadAdminApplications()
  } catch (error: any) {
    ElMessage.error('批准失败: ' + error.message)
  }
}

const rejectApp = async (id: number) => {
  try {
    await developerStore.rejectApplication(id, "不符合条件")
    ElMessage.success('已拒绝申请')
    await loadAdminApplications()
  } catch (error: any) {
    ElMessage.error('拒绝失败: ' + error.message)
  }
}

const loadPendingSolutions = async () => {
  try {
    await developerStore.fetchPendingSolutions(0, 10)
  } catch (error: any) {
    ElMessage.error('加载待审核解决方案失败: ' + error.message)
  }
}

const approveSol = async (id: string) => {
  try {
    await developerStore.approveSolutionReview(id)
    ElMessage.success('已批准解决方案')
    await loadPendingSolutions()
  } catch (error: any) {
    ElMessage.error('批准失败: ' + error.message)
  }
}

const rejectSol = async (id: string) => {
  try {
    await developerStore.rejectSolutionReview(id, "理由不足")
    ElMessage.success('已拒绝解决方案')
    await loadPendingSolutions()
  } catch (error: any) {
    ElMessage.error('拒绝失败: ' + error.message)
  }
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
  } catch (error: any) {
    ElMessage.error('初始化数据失败: ' + error.message)
  } finally {
    pageLoading.value = false
  }
})
</script>

<style scoped lang="scss">
.admin-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  background: var(bg-secondary-color);
}

.admin-card{
  width: 100%;
  max-width: 80%;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 10px 30px var(--shadow-color);
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  transition: all 0.3s;
  color: var(--text-color);
}

.el-table {
  --el-table-bg-color: var(--bg-color);
  --el-table-border-color: var(--border-color);
  --el-table-header-bg-color: var(--bg-secondary-color);
  --el-table-tr-bg-color: var(--bg-color);
  --el-table-text-color: var(--text-color);
  --el-table-header-text-color: var(--text-color);
  --el-table-row-hover-bg-color: var(--hover-color);
  border-radius: 0 0 12px 12px;
  }

.step-input {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.step-input .el-input {
  flex: 1;
  margin-right: 10px;
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}

</style>