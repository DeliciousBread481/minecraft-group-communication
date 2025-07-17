<template>
  <div class="settings-container">
    <h1 class="settings-title">个人设置</h1>

    <!-- 成功消息 -->
    <div v-if="showSuccess" class="alert alert-success">
      设置已成功保存
    </div>

    <!-- 错误消息 -->
    <div v-if="showError" class="alert alert-danger">
      {{ errorMessage }}
    </div>

    <el-card class="menu-card">
      <el-menu mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect">
        <el-menu-item index="profile">个人资料</el-menu-item>
        <el-menu-item index="security">安全选项</el-menu-item>
      </el-menu>

      <div class="card-form-container">
        <!-- 个人资料设置 -->
        <div v-if="activeMenu === 'profile'">
          <form @submit.prevent="saveSettings" class="settings-form">
          <div class="form-group">
            <label for="userid">用户ID</label>
            <div class="userid-container"
              @mouseenter="handleUserIdMouseEnter"
              @mouseleave="handleUserIdMouseLeave"
            >
              <div class="masked-userid">
                <span v-if="!showUserId">●●●●●●●●</span>
                <span v-else>{{ userForm.username }}</span>
              </div>
            </div>
            <small class="text-muted">悬停鼠标1秒后显示用户ID</small>
          </div>

          <div class="form-group">
            <label for="nickname">昵称</label>
            <input
              type="text"
              id="nickname"
              v-model="userForm.nickname"
              class="form-control"
              placeholder="请输入您的昵称"
            >
            <small class="text-muted">昵称将显示给其他用户</small>
          </div>

          <div class="form-group">
            <label for="email">电子邮箱</label>
            <input
              type="email"
              id="email"
              v-model="userForm.email"
              class="form-control"
              @blur="validateEmail"
              :class="{ 'is-invalid': !formState.emailValid }"
            >
            <div v-if="!formState.emailValid" class="invalid-feedback">
              请输入有效的电子邮箱地址
            </div>
          </div>

          <div class="form-group">
            <label for="avatar">上传头像</label>
            <div class="avatar-layout">
              <div class="avatar-preview-container">
                <div class="avatar-preview" v-if="userForm.avatar">
                  <img :src="userForm.avatar" alt="头像预览" class="avatar-image">
                </div>
                <div v-else class="avatar-placeholder">
                  <span>无头像</span>
                </div>
              </div>
              <div class="avatar-upload-container">
                <el-upload
                  ref="uploadRef"
                  class="upload-demo"
                  action="/api/users/avatar"
                  :limit="1"
                  :show-file-list="false"
                  :on-change="handleAvatarUpload"
                  :auto-upload="false"
                >
                  <template #trigger>
                    <el-button type="primary">选择文件</el-button>
                  </template>
                </el-upload>
              </div>
            </div>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn-secondary"
              @click="resetForm"
            >
              重置
            </button>
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="isSubmitting"
            >
              {{ isSubmitting ? '保存中...' : '保存设置' }}
            </button>
          </div>
        </form>
        </div>

        <!-- 安全选项设置 -->
        <div v-if="activeMenu === 'security'">
          <form @submit.prevent="changePassword" class="settings-form">
          <h3 class="form-title">修改密码</h3>

          <div class="form-group">
            <label for="currentPassword">当前密码</label>
            <input
              type="password"
              id="currentPassword"
              v-model="passwordForm.currentPassword"
              class="form-control"
              required
            >
          </div>

          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input
              type="password"
              id="newPassword"
              v-model="passwordForm.newPassword"
              class="form-control"
              required
              @blur="validateNewPassword"
              :class="{ 'is-invalid': !passwordFormState.newPasswordValid }"
            >
            <div v-if="!passwordFormState.newPasswordValid" class="invalid-feedback">
              新密码长度至少为8位
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              class="form-control"
              required
              @blur="validatePasswordMatch"
              :class="{ 'is-invalid': !passwordFormState.passwordsMatch }"
            >
            <div v-if="!passwordFormState.passwordsMatch" class="invalid-feedback">
              两次输入的密码不一致
            </div>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn-secondary"
              @click="resetPasswordForm"
            >
              取消
            </button>
            <button
              type="submit"
              class="btn btn-primary"
              :disabled="isChangingPassword || !passwordFormState.formValid"
            >
              {{ isChangingPassword ? '提交中...' : '更改密码' }}
            </button>
          </div>
        </form>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue';
import { useUserStore } from '@/store/user';
import type { UserInfo } from '@/api/user';
import { useRouter } from 'vue-router';

// 获取用户存储
const userStore = useUserStore();
const router = useRouter();

// 如果用户未登录，重定向到登录页面
if (!userStore.isAuthenticated) {
  router.push({ name: 'auth', query: { redirect: '/settings' } });
}

// 监听用户认证状态，当用户退出登录时重定向到登录页面
watch(() => userStore.isAuthenticated, (isAuthenticated) => {
  if (!isAuthenticated) {
    router.push({ name: 'auth' });
  }
});

const originalUserInfo = computed(() => userStore.userInfo || {
  id: 0,
  username: '',
  nickname: '',
  email: '',
  avatar: ''
});

// 用于表单的用户信息对象
const userForm = reactive({
  username: originalUserInfo.value.username || '',
  nickname: originalUserInfo.value.nickname || '',
  email: originalUserInfo.value.email || '',
  avatar: originalUserInfo.value.avatar || '',
});

// 密码表单对象
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 密码表单状态
const passwordFormState = reactive({
  newPasswordValid: true,
  passwordsMatch: true,
  formValid: true
});

// 是否显示成功消息
const showSuccess = ref(false);
// 是否显示错误消息
const showError = ref(false);
// 错误消息内容
const errorMessage = ref('');
// 表单提交状态
const isSubmitting = ref(false);
// 密码更改状态
const isChangingPassword = ref(false);
// 活跃菜单
const activeMenu = ref('profile');
// 用户ID显示的定时器ID
const userIdTimer = ref<number | null>(null);
// 是否显示用户ID
const showUserId = ref(false);

// 处理用户ID的显示与隐藏
const handleUserIdMouseEnter = () => {
  // 清除可能存在的之前的定时器
  if (userIdTimer.value !== null) {
    clearTimeout(userIdTimer.value);
  }
  // 设置新的定时器
  userIdTimer.value = window.setTimeout(() => {
    showUserId.value = true;
  }, 1000);
};

const handleUserIdMouseLeave = () => {
  if (userIdTimer.value !== null) {
    clearTimeout(userIdTimer.value);
    userIdTimer.value = null;
  }
  showUserId.value = false;
};

// 处理菜单选择
const handleMenuSelect = (key: string) => {
  activeMenu.value = key;
  // 切换菜单时清除任何消息
  showSuccess.value = false;
  showError.value = false;
};

// 表单验证状态
const formState = reactive({
  emailValid: true,
  formValid: true
});

// 重置表单
const resetForm = () => {
  userForm.username = originalUserInfo.value.username || '';
  userForm.nickname = originalUserInfo.value.nickname || '';
  userForm.email = originalUserInfo.value.email || '';
  userForm.avatar = originalUserInfo.value.avatar || '';
  formState.emailValid = true;
  formState.formValid = true;
};

// 处理头像文件上传
const handleAvatarUpload = (uploadFile: any) => {
  const file = uploadFile.raw;
  if (!file) return;

  // 验证文件是否为图片
  if (!file.type.startsWith('image/')) {
    showError.value = true;
    errorMessage.value = '请上传图片文件';
    return;
  }

  // 限制文件大小（2MB）
  const maxSize = 2 * 1024 * 1024; // 2MB
  if (file.size > maxSize) {
    showError.value = true;
    errorMessage.value = '图片大小不能超过2MB';
    return;
  }

  // 创建本地文件URL并预览
  const reader = new FileReader();
  reader.onload = (e: ProgressEvent<FileReader>) => {
    if (e.target && typeof e.target.result === 'string') {
      userForm.avatar = e.target.result;
    } else {
      console.error('读取文件失败或结果不是字符串');
      showError.value = true;
      errorMessage.value = '读取图片失败，请重试';
    }
  };
  reader.readAsDataURL(file);
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.currentPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordFormState.newPasswordValid = true;
  passwordFormState.passwordsMatch = true;
  passwordFormState.formValid = true;
};

// 验证邮箱格式
const validateEmail = () => {
  if (!userForm.email) {
    formState.emailValid = true;
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  formState.emailValid = emailRegex.test(userForm.email);
  formState.formValid = formState.emailValid;
};

// 验证新密码强度
const validateNewPassword = () => {
  passwordFormState.newPasswordValid = passwordForm.newPassword.length >= 8;
  updatePasswordFormValidity();
};

// 验证密码匹配
const validatePasswordMatch = () => {
  passwordFormState.passwordsMatch = passwordForm.newPassword === passwordForm.confirmPassword;
  updatePasswordFormValidity();
};

// 更新密码表单有效性
const updatePasswordFormValidity = () => {
  passwordFormState.formValid =
    passwordFormState.newPasswordValid &&
    passwordFormState.passwordsMatch &&
    Boolean(passwordForm.currentPassword);
};

// 修改密码
const changePassword = async () => {
  validateNewPassword();
  validatePasswordMatch();

  if (!passwordFormState.formValid) {
    showError.value = true;
    errorMessage.value = '请检查表单中的错误';
    return;
  }

  try {
    isChangingPassword.value = true;

    // 这里应该调用API来修改密码
    // const success = await userStore.changePassword({
    //   currentPassword: passwordForm.currentPassword,
    //   newPassword: passwordForm.newPassword
    // });

    // 模拟成功响应
    const success = true;

    if (success) {
      showSuccess.value = true;
      showError.value = false;
      resetPasswordForm();

      // 3秒后隐藏成功消息
      setTimeout(() => {
        showSuccess.value = false;
      }, 3000);
    } else {
      showError.value = true;
      errorMessage.value = '密码修改失败，请检查当前密码是否正确';
    }
  } catch (error) {
    showError.value = true;
    errorMessage.value = '发生错误，请稍后再试';
    console.error('修改密码错误:', error);
  } finally {
    isChangingPassword.value = false;
  }
};

// 保存用户设置
const saveSettings = async () => {
  // 验证邮箱
  validateEmail();
  if (!formState.formValid) {
    showError.value = true;
    errorMessage.value = '请检查表单中的错误';
    return;
  }

  try {
    isSubmitting.value = true;
    // 只更新已更改的字段
    const updateData: Partial<UserInfo> = {};

    if (userForm.email !== originalUserInfo.value.email) {
      updateData.email = userForm.email;
    }

    if (userForm.nickname !== originalUserInfo.value.nickname) {
      updateData.nickname = userForm.nickname;
    }

    if (userForm.avatar !== originalUserInfo.value.avatar) {
      // 检查是否为Base64数据URL(从文件上传获得)
      if (userForm.avatar && userForm.avatar.startsWith('data:image')) {
        // 这里将Base64转换为文件并保存到本地
        // 在实际项目中，你需要调用后端API来保存文件
        const fileName = `avatar_${userStore.userInfo?.id}_${Date.now()}.png`;
        const filePath = `/avatars/${fileName}`;

        // 添加注释说明实际项目中如何处理文件保存
        console.log('实际项目中，这里应调用后端API将Base64图片数据保存到服务器');
        console.log('保存路径应为:', filePath);

        // 为了演示，我们仍然使用Base64数据作为头像URL
        updateData.avatar = userForm.avatar;

        // 可以在这里添加更多代码来处理文件保存
        // 例如:
        // const response = await apiService.uploadAvatar({
        //   userId: userStore.userInfo?.id,
        //   imageData: userForm.avatar
        // });
        // updateData.avatar = response.avatarUrl;
      } else {
        updateData.avatar = userForm.avatar;
      }
    }

    // 仅当有更改时才提交
    if (Object.keys(updateData).length > 0) {
      const success = await userStore.updateUserInfo(updateData);

      if (success) {
        showSuccess.value = true;
        showError.value = false;

        // 3秒后隐藏成功消息
        setTimeout(() => {
          showSuccess.value = false;
        }, 3000);
      } else {
        showError.value = true;
        errorMessage.value = '保存设置失败，请稍后再试';
      }
    } else {
      // 没有更改，显示成功消息
      showSuccess.value = true;
      // 3秒后隐藏成功消息
      setTimeout(() => {
        showSuccess.value = false;
      }, 3000);
    }
  } catch (error) {
    showError.value = true;
    errorMessage.value = '发生错误，请稍后再试';
    console.error('更新设置错误:', error);
  } finally {
    isSubmitting.value = false;
  }
};

// 页面加载时从store获取最新的用户信息
onMounted(async () => {
  if (userStore.isAuthenticated) {
    await userStore.fetchCurrentUser();
    resetForm();
  }
});
</script>

<style scoped>
.settings-container {
  min-width: 90%;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.settings-title {
  margin-bottom: 2rem;
  color: #333;
  font-weight: 600;
}

.menu-card {
  margin-bottom: 2rem;
  width: 100%;
}

.card-form-container {
  padding: 2rem;
}

.avatar-layout {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.avatar-upload-container {
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  flex: 1;
}

.avatar-preview-container {
  margin-top: 0;
}

.settings-form {
  background: transparent;
  padding: 0;
  box-shadow: none;
}

/* 保留原来的网格样式作为参考 */
/* .settings-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 2rem;
}

.security-settings {
  grid-column: 1 / -1;
} */

@media (max-width: 992px) {
  .card-form-container {
    padding: 1.5rem;
  }
}

.settings-form {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
}

.form-title {
  margin-bottom: 1.5rem;
  color: #333;
  font-weight: 500;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:focus {
  border-color: #4a9df6;
  box-shadow: 0 0 0 3px rgba(74, 157, 246, 0.25);
  outline: none;
}

.form-control.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #4a9df6;
  color: white;
}

.btn-primary:hover {
  background-color: #3a8de6;
}

.btn-primary:disabled {
  background-color: #9ac8f8;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #e9ecef;
  color: #212529;
}

.btn-secondary:hover {
  background-color: #dde2e6;
}

.btn-outline-danger {
  color: #dc3545;
  border: 1px solid #dc3545;
  background: transparent;
  width: 100%;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  color: white;
}

.alert {
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  background-color: #f8f9fa;
  padding: 1rem;
  border-bottom: 1px solid #ddd;
  font-weight: 600;
}

.card-body {
  padding: 1.5rem;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #ddd;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  font-size: 0.875rem;
}

.text-muted {
  color: #6c757d;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}


.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-control:focus {
  border-color: #4a9df6;
  box-shadow: 0 0 0 3px rgba(74, 157, 246, 0.25);
  outline: none;
}

.form-control.is-invalid {
  border-color: #dc3545;
}

.invalid-feedback {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #4a9df6;
  color: white;
}

.btn-primary:hover {
  background-color: #3a8de6;
}

.btn-primary:disabled {
  background-color: #9ac8f8;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #e9ecef;
  color: #212529;
}

.btn-secondary:hover {
  background-color: #dde2e6;
}

.btn-outline-danger {
  color: #dc3545;
  border: 1px solid #dc3545;
  background: transparent;
  width: 100%;
}

.btn-outline-danger:hover {
  background-color: #dc3545;
  color: white;
}

.alert {
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1.5rem;
}

.alert-success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alert-danger {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  background-color: #f8f9fa;
  padding: 1rem;
  border-bottom: 1px solid #ddd;
  font-weight: 600;
}

.card-body {
  padding: 1.5rem;
}

.avatar-preview {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 1px solid #ddd;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #e9ecef;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
  font-size: 0.875rem;
}

.userid-container {
  cursor: pointer;
  position: relative;
}

.masked-userid {
  display: inline-block;
  padding: 0.75rem;
  background-color: #f8f9fa;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #6c757d;
  width: 100%;
  cursor: pointer;
}

.masked-userid:hover {
  min-height: 44px;
  background-color: #e9ecef;
}

/* 用户ID悬浮效果过渡 */
.masked-userid span {
  transition: all 0.2s ease;
}

/* 已在上面定义过 */
</style>
