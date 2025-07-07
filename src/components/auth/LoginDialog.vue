<template>
  <!-- 登录对话框组件 -->
  <el-dialog
    v-model="dialogVisible"
    title="系统登录"
    width="400px"
    :close-on-click-modal="false"
  >
    <!-- 登录表单 -->
    <el-form
      ref="loginFormRef"
      :model="form"
      :rules="rules"
      @submit.prevent="handleSubmit"
    >
      <!-- 用户名输入框 -->
      <el-form-item prop="username">
        <el-input
          v-model="form.username"
          placeholder="请输入用户名"
          prefix-icon="User"
        />
      </el-form-item>

      <!-- 密码输入框 -->
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          prefix-icon="Lock"
          show-password
        />
      </el-form-item>

      <!-- 登录按钮 -->
      <el-form-item>
        <el-button
          type="primary"
          class="login-btn"
          native-type="submit"
          :loading="loading"
        >
          登录
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 对话框底部区域 -->
    <template #footer>
      <div class="dialog-footer">
        <!-- 注册新账号按钮 -->
        <el-button type="text" @click="showRegister">注册新账号</el-button>
        <!-- 忘记密码按钮 (功能待实现) -->
        <el-button type="text">忘记密码?</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useUserStore } from '@/store/user'
import type { FormInstance } from 'element-plus'

/**
 * 组件属性定义
 * @property {boolean} visible - 控制对话框显示/隐藏的状态
 */
const props = defineProps<{
  visible: boolean
}>()

/**
 * 组件事件定义
 * @emits {'update:visible'} - 更新visible属性的事件
 * @emits {'register'} - 请求显示注册对话框的事件
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'register'): void
}>()

// 使用用户状态管理库
const userStore = useUserStore()

// 登录按钮的加载状态
const loading = ref(false)

// 登录表单数据
const form = ref({
  username: '',  // 用户名输入
  password: ''   // 密码输入
})

// 对话框显示状态（与props.visible同步）
const dialogVisible = ref(props.visible)

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }, // 用户名必填验证
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' } // 用户名长度验证
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }, // 密码必填验证
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' } // 密码长度验证
  ]
}

// 表单实例引用（用于调用Element Plus的表单方法）
const loginFormRef = ref<FormInstance>()

/**
 * 监听props.visible属性的变化
 * 当父组件传入的visible值变化时，更新本地dialogVisible状态
 */
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
})

/**
 * 监听dialogVisible的变化
 * 当对话框显示状态改变时，通知父组件更新visible属性
 */
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

/**
 * 处理登录表单提交
 * 1. 执行表单验证
 * 2. 调用用户登录接口
 * 3. 处理登录结果
 */
const handleSubmit = async () => {
  // 确保表单引用存在
  if (!loginFormRef.value) return

  try {
    // 执行表单验证
    await loginFormRef.value.validate()

    // 设置加载状态
    loading.value = true

    // 调用用户登录方法
    const success = await userStore.userLogin(form.value)

    // 登录成功后关闭对话框
    if (success) {
      dialogVisible.value = false
    }
  } catch (error) {
    // 登录失败处理
    console.error('登录失败:', error)
  } finally {
    // 重置加载状态
    loading.value = false
  }
}

/**
 * 显示注册对话框
 * 触发'register'事件通知父组件
 */
const showRegister = () => {
  emit('register')
}
</script>
