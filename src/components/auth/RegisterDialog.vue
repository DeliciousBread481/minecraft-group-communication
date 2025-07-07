<template>
  <!-- 用户注册对话框组件 -->
  <el-dialog
    v-model="dialogVisible"
    title="用户注册"
    width="450px"
    :close-on-click-modal="false"
  >
  <!-- 注册表单 -->
  <el-form
    ref="registerFormRef"
    :model="form"
    :rules="rules"
    @submit.prevent="handleRegister"
  >
  <!-- 用户名输入项 -->
  <el-form-item prop="username">
    <el-input
      v-model="form.username"
      placeholder="请输入用户名"
      prefix-icon="User"
    />
  </el-form-item>

  <!-- 密码输入项 -->
  <el-form-item prop="password">
    <el-input
      v-model="form.password"
      type="password"
      placeholder="请输入密码"
      prefix-icon="Lock"
      show-password
    />
  </el-form-item>

  <!-- 确认密码输入项 -->
  <el-form-item prop="confirmPassword">
    <el-input
      v-model="form.confirmPassword"
      type="password"
      placeholder="请确认密码"
      prefix-icon="Lock"
      show-password
    />
  </el-form-item>

  <!-- 邮箱输入项 -->
  <el-form-item prop="email">
    <el-input
      v-model="form.email"
      placeholder="请输入邮箱"
      prefix-icon="Message"
    />
  </el-form-item>

  <!-- 注册按钮 -->
  <el-form-item>
    <el-button
      type="primary"
      class="register-btn"
      native-type="submit"
      :loading="loading"
    >
    注册
    </el-button>
  </el-form-item>
  </el-form>

  <!-- 对话框底部区域 -->
  <template #footer>
    <div class="dialog-footer">
      <!-- 返回按钮 -->
      <el-button type="text" @click="dialogVisible = false">返回</el-button>
    </div>
  </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useUserStore } from '@/store/user'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'  // 用于显示消息提示
import type { RuleItem } from 'async-validator' // 表单验证规则类型

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
 */
const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
}>()

// 使用用户状态管理
const userStore = useUserStore()

// 注册按钮的加载状态
const loading = ref(false)

// 注册表单数据
const form = ref({
  username: '',          // 用户名
  password: '',          // 密码
  confirmPassword: '',   // 确认密码
  email: ''              // 邮箱
})

/**
 * 自定义密码验证规则
 * 验证两次输入的密码是否一致
 * @param {RuleItem} _rule - 验证规则对象
 * @param {string} value - 当前字段的值（确认密码）
 * @param {(error?: Error) => void} callback - 验证结果回调
 */
const validatePassword = (
  _rule: RuleItem,
  value: string,
  callback: (error?: Error) => void
) => {
  if (value !== form.value.password) {
    // 密码不一致时返回错误
    callback(new Error('两次输入密码不一致!'))
  } else {
    // 验证通过
    callback()
  }
}

// 表单验证规则配置
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }, // 必填验证
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' } // 长度验证
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }, // 必填验证
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' } // 长度验证
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' }, // 必填验证
    { validator: validatePassword, trigger: 'blur' } // 自定义密码一致性验证
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' }, // 必填验证
    {
      type: 'email', // 邮箱格式验证
      message: '请输入正确的邮箱地址',
      trigger: ['blur', 'change'] // 触发验证的时机
    }
  ]
}

// 表单实例引用
const registerFormRef = ref<FormInstance>()

// 对话框显示状态（同步外部visible属性）
const dialogVisible = ref(props.visible)

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
 * 处理注册表单提交
 * 1. 执行表单验证
 * 2. 调用用户注册接口
 * 3. 处理注册结果
 */
const handleRegister = async () => {
  // 确保表单引用存在
  if (!registerFormRef.value) return

  try {
    // 执行表单验证
    await registerFormRef.value.validate()

    // 设置加载状态
    loading.value = true

    // 调用用户注册方法
    const success = await userStore.userRegister({
      username: form.value.username,
      password: form.value.password,
      email: form.value.email
    })

    // 根据注册结果进行相应处理
    if (success) {
      // 注册成功提示
      ElMessage.success('注册成功！')
      // 关闭对话框
      dialogVisible.value = false
    } else {
      // 注册失败提示
      ElMessage.error('注册失败，请重试')
    }
  } catch (error) {
    // 捕获并处理异常
    console.error('注册失败:', error)
    // 显示错误提示
    ElMessage.error('注册失败，请重试')
  } finally {
    // 重置加载状态
    loading.value = false
  }
}
</script>
