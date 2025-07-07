<template>
  <!-- 登录表单组件 -->
  <el-form
    :model="form"
    :rules="rules"
    ref="loginFormRef"
    @submit.prevent="handleSubmit"
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
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

/**
 * 组件事件定义
 * @emits {'submit'} - 表单验证通过后提交登录数据的事件
 */
const emit = defineEmits(['submit'])

// 登录按钮的加载状态
const loading = ref(false)

// 登录表单数据
const form = ref({
  username: '',
  password: ''
})

// 表单验证规则配置
const rules = ref<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }, // 必填验证
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' } // 长度验证
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }, // 必填验证
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' } // 长度验证
  ]
})

// 表单实例引用（用于调用Element Plus的表单方法）
const loginFormRef = ref<FormInstance>()

/**
 * 处理表单提交
 * 1. 执行表单验证
 * 2. 触发提交事件
 */
const handleSubmit = async () => {
  // 确保表单引用存在
  if (!loginFormRef.value) return

  try {
    // 执行表单验证
    await loginFormRef.value.validate()

    // 设置加载状态
    loading.value = true

    // 触发提交事件，传递表单数据
    emit('submit', form.value)
  } catch (error) {
    // 表单验证失败处理
    console.error('表单验证失败:', error)
  } finally {
    // 重置加载状态
    loading.value = false
  }
}
</script>

<style scoped>
/* 登录按钮样式 */
.login-btn {
  width: 100%;
  height: 40px;
  margin-top: 10px;
}
</style>
