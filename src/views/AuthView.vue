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

      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        show-icon
        closable
        class="error-alert"
        @close="errorMessage = ''"
      />

      <el-tabs v-model="activeTab" stretch @tab-click="resetForms">
        <el-tab-pane label="登录" name="login"></el-tab-pane>
        <el-tab-pane label="注册" name="register"></el-tab-pane>
      </el-tabs>

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
            <el-link type="primary" :underline="false" @click="showForgotPassword">忘记密码?</el-link>
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
              我已阅读并同意 <el-link type="primary" @click="showTermsDialog">服务条款</el-link> 和 <el-link type="primary" @click="showPrivacyDialog">隐私政策</el-link>
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

      <div class="back-home">
        <el-link type="primary" :underline="false" @click="goToHome">
          <el-icon><ArrowLeft /></el-icon> 返回首页
        </el-link>
      </div>
    </el-card>

    <!-- 条款对话框 -->
    <el-dialog v-model="termsDialogVisible" title="服务条款" width="80%">
      <div v-html="termsContent"></div>
      <template #footer>
        <el-button type="primary" @click="termsDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 隐私政策对话框 -->
    <el-dialog v-model="privacyDialogVisible" title="隐私政策" width="80%">
      <div v-html="privacyContent"></div>
      <template #footer>
        <el-button type="primary" @click="privacyDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 忘记密码对话框 -->
    <el-dialog v-model="forgotPasswordDialogVisible" title="找回密码" width="500px">
      <el-form :model="forgotPasswordForm" :rules="forgotPasswordRules" ref="forgotPasswordFormRef">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="forgotPasswordForm.email" placeholder="请输入注册邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="forgotPasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleForgotPassword">发送重置邮件</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/user';
import type { LoginCredentials, RegisterData } from '@/api/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const activeTab = ref('login');
const loginFormRef = ref();
const registerFormRef = ref();
const forgotPasswordFormRef = ref();
const errorMessage = ref('');

// 表单数据
const loginForm = reactive<LoginCredentials>({
  username: '',
  password: '',
});

const registerForm = reactive<RegisterData>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
});

// 忘记密码表单
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

// 条款内容
const termsContent = ref(`
  <h3>服务条款</h3>
  <p>欢迎使用Minecraft疑难杂症交流群平台！</p>
  <p>.</p>
`);

// 隐私政策内容
const privacyContent = ref(`
  <h3>隐私政策</h3>
  <p>隐私保护...</p>
`);

// 验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在3到20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在6到20个字符', trigger: 'blur' }
  ]
};

const registerRules = {
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
      validator: (rule: unknown, value: string, callback: (error?: Error) => void) => {
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
      validator: (rule: unknown, value: boolean, callback: (error?: Error) => void) => {
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

const forgotPasswordRules = {
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ]
};

// 提交登录
const submitLogin = async () => {
  if (!loginFormRef.value) return;

  loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.login = true;
      errorMessage.value = '';

      try {
        const credentials: LoginCredentials = {
          username: loginForm.username,
          password: loginForm.password
        };

        // 调用用户存储的登录方法
        const success = await userStore.userLogin(credentials);

        if (success) {
          ElMessage.success('登录成功');
          const redirect = route.query.redirect?.toString() || '/';
          await router.push(redirect);
        } else {
          errorMessage.value = '用户名或密码错误';
        }
      } catch (error: unknown) {
        // 处理未知错误类型
        if (error instanceof Error) {
          errorMessage.value = error.message;
        } else {
          errorMessage.value = '登录失败，请稍后再试';
        }
        console.error('登录错误:', error);
      } finally {
        loading.login = false;
      }
    }
  });
};

// 提交注册
const submitRegister = async () => {
  if (!registerFormRef.value) return;

  registerFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.register = true;
      errorMessage.value = '';

      try {
        const userData: RegisterData = {
          username: registerForm.username,
          email: registerForm.email,
          password: registerForm.password
        };

        // 调用用户存储的注册方法
        const success = await userStore.userRegister(userData);

        if (success) {
          ElMessage.success('注册成功，请登录');

          // 自动填充登录表单
          loginForm.username = registerForm.email;
          activeTab.value = 'login';

          // 重置注册表单
          registerFormRef.value.resetFields();
        } else {
          errorMessage.value = '注册失败，请稍后再试';
        }
      } catch (error: unknown) {
        // 处理未知错误类型
        if (error instanceof Error) {
          errorMessage.value = error.message;
        } else {
          errorMessage.value = '注册失败，请检查输入信息';
        }
        console.error('注册错误:', error);
      } finally {
        loading.register = false;
      }
    }
  });
};

// 显示服务条款
const showTermsDialog = () => {
  termsDialogVisible.value = true;
};

// 显示隐私政策
const showPrivacyDialog = () => {
  privacyDialogVisible.value = true;
};

// 显示忘记密码对话框
const showForgotPassword = () => {
  forgotPasswordDialogVisible.value = true;
};

// 处理忘记密码请求
const handleForgotPassword = async () => {
  if (!forgotPasswordFormRef.value) return;

  forgotPasswordFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.forgotPassword = true;

      try {
        // 调用API发送密码重置邮件
        ElMessage.success('密码重置邮件已发送，请检查您的邮箱');
        forgotPasswordDialogVisible.value = false;
      } catch (error: unknown) {
        // 处理未知错误类型
        let errorMsg = '发送重置邮件失败，请稍后再试';
        if (error instanceof Error) {
          errorMsg = error.message;
        }
        ElMessage.error(errorMsg);
        console.error('发送重置邮件失败:', error);
      } finally {
        loading.forgotPassword = false;
      }
    }
  });
};
// 切换标签时重置表单
const resetForms = () => {
  if (loginFormRef.value) {
    loginFormRef.value.resetFields();
  }
  if (registerFormRef.value) {
    registerFormRef.value.resetFields();
  }
  errorMessage.value = '';
};

// 返回首页
const goToHome = () => {
  router.push('/');
};
</script>

<style scoped>
.auth-view {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 20px;
  background: var(--auth-bg);
}

.auth-card {
  width: 100%;
  max-width: 500px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  background-color: var(--auth-card-bg);
  border: 1px solid var(--auth-border);
}

.auth-header {
  text-align: center;
  padding: 30px 20px 20px;
}

.auth-logo-container {
  width: 90px;
  height: 90px;
  margin: 0 auto 15px;
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
}

.auth-header h2 {
  margin-bottom: 5px;
  color: var(--text-color);
}

.auth-header p {
  color: var(--text-secondary);
  margin-bottom: 20px;
}

.auth-input :deep(.el-input__wrapper) {
  background-color: var(--input-bg);
  border: 1px solid var(--border-color);
  box-shadow: none;
}

.auth-input :deep(.el-input__wrapper:hover) {
  border-color: var(--primary-color);
}

.submit-btn {
  width: 100%;
  height: 45px;
  font-size: 16px;
  margin-top: 10px;
  font-weight: bold;
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.auth-divider {
  position: relative;
  margin: 25px 0;
  text-align: center;
  color: var(--text-secondary);
}

.auth-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: var(--border-color);
  z-index: 1;
}

.auth-divider span {
  position: relative;
  display: inline-block;
  padding: 0 15px;
  background-color: var(--card-bg);
  color: var(--text-secondary);
  z-index: 2;
}

.social-auth {
  display: flex;
  gap: 15px;
  margin-top: 20px;
}

.social-btn {
  flex: 1;
  height: 45px;
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.social-btn.github {
  background-color: #333;
  color: white;
  border: none;
}

.social-btn.github:hover {
  background-color: #444;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.social-btn.google {
  background-color: #4285F4;
  color: white;
  border: none;
}

.social-btn.google:hover {
  background-color: #5390f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.back-home {
  margin-top: 25px;
  text-align: center;
  padding-top: 15px;
  border-top: 1px solid var(--border-color);
}

@media (max-width: 600px) {
  .auth-view {
    padding: 10px;
  }

  .auth-card {
    border-radius: 12px;
  }

  .auth-header h2 {
    font-size: 1.4rem;
  }

  .social-auth {
    flex-direction: column;
  }
}
</style>
