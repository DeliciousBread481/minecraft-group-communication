<template>
  <div class="auth-view">
    <el-card class="auth-card">
      <div class="auth-header">
        <div class="auth-logo-container">
          <img src="@/assets/image/icon.jpg" alt="Logo" class="auth-logo">
        </div>
        <h2>Minecraft 疑难杂症交流群</h2>
        <p>登录或注册仅供管理员用户使用</p>
      </div>

      <el-tabs v-model="activeTab" stretch @tab-click="resetForms">
        <el-tab-pane label="登录" name="login" />
        <el-tab-pane label="注册" name="register" />
      </el-tabs>

      <!-- 登录表单 -->
      <div v-if="activeTab === 'login'">
        <el-form
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          @keyup.enter="submitLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名/邮箱"
              prefix-icon="User"
              clearable
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              placeholder="密码"
              type="password"
              prefix-icon="Lock"
              show-password
              class="auth-input"
            />
          </el-form-item>

          <div class="remember-forgot">
            <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
            <el-link
              type="primary"
              :underline="false"
              @click="showForgotPassword"
            >
              忘记密码?
            </el-link>
          </div>

          <el-button
            type="primary"
            class="submit-btn"
            @click="submitLogin"
            :loading="loading.login"
          >
            登录
          </el-button>
        </el-form>
      </div>

      <!-- 注册表单 -->
      <div v-if="activeTab === 'register'">
        <el-form
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          @keyup.enter="submitRegister"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              prefix-icon="UserFilled"
              clearable
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱"
              prefix-icon="Message"
              clearable
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              placeholder="密码"
              type="password"
              prefix-icon="Lock"
              show-password
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              placeholder="确认密码"
              type="password"
              prefix-icon="Lock"
              show-password
              class="auth-input"
            />
          </el-form-item>

          <el-form-item prop="agreement">
            <el-checkbox v-model="registerForm.agreement">
              我已阅读并同意
              <el-link type="primary" @click="showTermsDialog">服务条款</el-link>
              和
              <el-link type="primary" @click="showPrivacyDialog">隐私政策</el-link>
            </el-checkbox>
          </el-form-item>

          <el-button
            type="success"
            class="submit-btn"
            @click="submitRegister"
            :loading="loading.register"
          >
            注册
          </el-button>
        </el-form>
      </div>

      <!-- 返回首页链接 -->
      <div class="back-home">
        <el-link type="primary" :underline="false" @click="goToHome">
          <el-icon><ArrowLeft /></el-icon> 返回首页
        </el-link>
      </div>
    </el-card>

    <!-- 服务条款对话框 -->
    <el-dialog
      v-model="termsDialogVisible"
      title="服务条款"
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="terms-content">
        <h3>服务条款</h3>
        <!-- 条款内容 -->
      </div>
      <template #footer>
        <el-button type="primary" @click="termsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 隐私政策对话框 -->
    <el-dialog
      v-model="privacyDialogVisible"
      title="隐私政策"
      width="80%"
      :close-on-click-modal="false"
    >
      <div class="privacy-content">
        <h3>隐私政策</h3>
        <!-- 隐私政策内容 -->
      </div>
      <template #footer>
        <el-button type="primary" @click="privacyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="forgotPasswordDialogVisible"
      title="找回密码"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="forgotPasswordForm"
        :rules="forgotPasswordRules"
        ref="forgotPasswordFormRef"
      >
        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="forgotPasswordForm.email"
            placeholder="请输入注册邮箱"
            prefix-icon="Message"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="forgotPasswordDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleForgotPassword"
          :loading="loading.forgotPassword"
        >
          发送重置邮件
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/user';
import type { LoginRequest, RegisterRequest } from '@/types/api';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 状态管理
const activeTab = ref('login');
const loginFormRef = ref<FormInstance>();
const registerFormRef = ref<FormInstance>();
const forgotPasswordFormRef = ref<FormInstance>();

// 表单数据
interface LoginFormData {
  username: string;
  password: string;
  remember: boolean;
}

interface RegisterFormData {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  agreement: boolean;
}

const loginForm = reactive<LoginFormData>({
  username: '',
  password: '',
  remember: false,
});

const registerForm = reactive<RegisterFormData>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  agreement: false,
});

const forgotPasswordForm = reactive({
  email: ''
});

// 加载状态
const loading = reactive({
  login: false,
  register: false,
  forgotPassword: false
});

// 对话框状态
const termsDialogVisible = ref(false);
const privacyDialogVisible = ref(false);
const forgotPasswordDialogVisible = ref(false);

// 表单验证规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ]
};

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_: unknown, value: string, callback: (error?: Error) => void) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: ['blur', 'change']
    }
  ],
  agreement: [
    {
      validator: (_: unknown, value: boolean, callback: (error?: Error) => void) => {
        if (!value) {
          callback(new Error('请同意服务条款和隐私政策'));
        } else {
          callback();
        }
      },
      trigger: 'change'
    }
  ]
};

const forgotPasswordRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
};

// 生命周期钩子
onMounted(() => {
  // 检查URL中是否有redirect参数
  if (route.query.redirect) {
    ElMessage.info('请先登录以继续访问');
  }

  // 尝试从本地存储恢复记住的用户名
  const rememberedUsername = localStorage.getItem('rememberedUsername');
  if (rememberedUsername) {
    loginForm.username = rememberedUsername;
    loginForm.remember = true;
  }
});

// 表单提交方法
const submitLogin = async () => {
  if (!loginFormRef.value) return;

  const valid = await loginFormRef.value.validate();
  if (!valid) return;

  try {
    loading.login = true;

    const credentials: LoginRequest = {
      username: loginForm.username,
      password: loginForm.password
    };

    const success = await userStore.login(credentials);

    if (success) {
      // 处理"记住我"选项
      if (loginForm.remember) {
        localStorage.setItem('rememberedUsername', loginForm.username);
      } else {
        localStorage.removeItem('rememberedUsername');
      }

      ElMessage.success('登录成功');
      handleLoginRedirect();
    } else {
      ElMessage.error('用户名或密码错误');
    }
  } catch (error: any) {
    handleLoginError(error);
  } finally {
    loading.login = false;
  }
};

const submitRegister = async () => {
  if (!registerFormRef.value) return;

  const valid = await registerFormRef.value.validate();
  if (!valid) return;

  try {
    loading.register = true;

    const userData: RegisterRequest = {
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password
    };

    const success = await userStore.register(userData);

    if (success) {
      ElMessage.success('注册成功，请登录');
      handlePostRegistration();
    } else {
      ElMessage.error('注册失败，请稍后再试');
    }
  } catch (error: any) {
    handleRegistrationError(error);
  } finally {
    loading.register = false;
  }
};

const handleForgotPassword = async () => {
  if (!forgotPasswordFormRef.value) return;

  const valid = await forgotPasswordFormRef.value.validate();
  if (!valid) return;

  try {
    loading.forgotPassword = true;

    //发送不了一点邮件
    await new Promise(resolve => setTimeout(resolve, 1500));

    ElMessage.success('密码重置邮件已发送，请检查您的邮箱');
    forgotPasswordDialogVisible.value = false;
  } catch (error: any) {
    handleForgotPasswordError(error);
  } finally {
    loading.forgotPassword = false;
  }
};

// 辅助方法
const resetForms = () => {
  loginFormRef.value?.resetFields();
  registerFormRef.value?.resetFields();
};

const showTermsDialog = () => {
  termsDialogVisible.value = true;
};

const showPrivacyDialog = () => {
  privacyDialogVisible.value = true;
};

const showForgotPassword = () => {
  forgotPasswordDialogVisible.value = true;
};

const goToHome = () => {
  router.push('/');
};

const handleLoginRedirect = () => {
  const redirectPath = route.query.redirect?.toString() || '/';
  router.push(redirectPath);
};

const handlePostRegistration = () => {
  // 保存邮箱用于登录表单
  const registeredEmail = registerForm.email;

  // 重置注册表单
  registerFormRef.value?.resetFields();

  // 切换到登录标签页
  activeTab.value = 'login';

  // 填充登录表单的用户名/邮箱字段
  loginForm.username = registeredEmail;
};

// 错误处理方法
const handleLoginError = (error: any) => {
  let errorMsg = '登录失败，请稍后再试';

  if (error.response?.data?.message) {
    errorMsg = error.response.data.message;
  } else if (error.message) {
    errorMsg = error.message;
  }

  ElMessage.error(errorMsg);
  console.error('登录错误:', error);
};

const handleRegistrationError = (error: any) => {
  let errorMsg = '注册失败，请检查输入信息';

  if (error.response?.data?.message) {
    errorMsg = error.response.data.message;
  } else if (error.message) {
    errorMsg = error.message;
  }

  ElMessage.error(errorMsg);
  console.error('注册错误:', error);
};

const handleForgotPasswordError = (error: any) => {
  let errorMsg = '发送重置邮件失败，请稍后再试';

  if (error.response?.data?.message) {
    errorMsg = error.response.data.message;
  } else if (error.message) {
    errorMsg = error.message;
  }

  ElMessage.error(errorMsg);
  console.error('发送重置邮件失败:', error);
};
</script>

<style scoped lang="scss">
.auth-view {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  min-height: 100%;
  padding: 1.5rem;
  background: var(--bg-secondary-color);
}

.auth-card {
  width: 100%;
  max-width: 500px;
  border-radius: 1rem;
  overflow: hidden;
  box-shadow: 0 10px 30px var(--shadow-color);
  background-color: var(--bg-color);
  border: 1px solid var(--border-color);
  transition: all 0.3s;
}

.auth-header {
  text-align: center;
  padding: 1.5rem 1.5rem 1rem;

  h2 {
    margin-bottom: 0.5rem;
    color: var(--text-color);
    font-size: 1.5rem;
  }

  p {
    color: var(--text-secondary-color);
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
  }
}

.auth-logo-container {
  width: 90px;
  height: 90px;
  margin: 0 auto 1rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: var(--primary-light);
  border: 2px solid var(--primary-color);
}

.auth-logo {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  object-fit: cover;
  background-color: white;
}

:deep(.el-tabs) {
  padding: 0 1.5rem;
}

.auth-input {
  margin-bottom: 0.5rem;

  :deep(.el-input__wrapper) {
    padding: 0.8rem 1rem;
    border-radius: 0.75rem;
  }
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  padding: 0 0.25rem;
}

.submit-btn {
  width: 100%;
  height: 2.75rem;
  font-size: 1rem;
  font-weight: 500;
  border-radius: 0.75rem;
  margin-top: 0.25rem;
}

.back-home {
  margin-top: 1.5rem;
  text-align: center;
  padding-top: 1.25rem;
  border-top: 1px solid var(--border-color);
  color: var(--text-secondary-color);

  .el-link {
    font-size: 0.95rem;

    .el-icon {
      margin-right: 0.25rem;
      vertical-align: middle;
    }
  }
}

.terms-content, .privacy-content {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0.5rem 1rem;
  line-height: 1.6;

  h3 {
    text-align: center;
    margin-bottom: 1.5rem;
    color: var(--primary-color);
  }

  h4 {
    margin-top: 1.25rem;
    margin-bottom: 0.5rem;
    color: var(--text-color);
  }

  p {
    margin-bottom: 0.75rem;
    color: var(--text-secondary-color);
  }
}

@media (max-width: 600px) {
  .auth-view {
    padding: 1rem;
  }

  .auth-card {
    border-radius: 0.875rem;
  }

  .auth-header h2 {
    font-size: 1.3rem;
  }

  .auth-logo-container {
    width: 80px;
    height: 80px;
  }

  .auth-logo {
    width: 60px;
    height: 60px;
  }

  :deep(.el-tabs) {
    padding: 0 1rem;
  }
}
</style>