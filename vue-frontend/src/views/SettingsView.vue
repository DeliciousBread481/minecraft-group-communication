<template>
  <div class="settings-container">
    <h1 class="settings-title">个人设置</h1>

    <div class="flat-menu">
      <div
        class="menu-item"
        :class="{ active: activeMenu === 'profile' }"
        @click="activeMenu = 'profile'"
      >
        个人资料
      </div>
      <div
        class="menu-item"
        :class="{ active: activeMenu === 'security' }"
        @click="activeMenu = 'security'"
      >
        安全选项
      </div>
    </div>

    <div class="settings-content">
      <!-- 个人资料设置 -->
      <div v-if="activeMenu === 'profile'" class="settings-section">
        <form @submit.prevent="saveSettings" class="settings-form">
          <div class="form-group">
            <label for="userid">用户ID</label>
            <div class="userid-container"
                 @mouseenter="handleUserIdMouseEnter"
                 @mouseleave="handleUserIdMouseLeave"
            >
              <div class="masked-userid">
                <span v-if="!showUserId">●●●●●●●●</span>
                <span v-else>{{ userInfo.id }}</span>
              </div>
            </div>
            <small class="text-muted">悬停鼠标1秒后显示用户ID</small>
          </div>

          <div class="form-group">
            <label for="nickname">昵称</label>
            <el-input
              id="nickname"
              v-model="userForm.nickname"
              placeholder="请输入您的昵称"
              class="form-control"
            />
            <small class="text-muted">昵称将显示给其他用户</small>
          </div>

          <div class="form-group">
            <label for="email">电子邮箱</label>
            <el-input
              id="email"
              v-model="userForm.email"
              placeholder="请输入您的邮箱"
              type="text"
              class="form-control"
              @blur="validateEmail"
              :class="{ 'is-invalid': !formState.emailValid }"
            />
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
                  :action="avatarUploadAction"
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
            <el-button
              type="default"
              class="btn btn-secondary"
              @click="resetForm"
            >
              重置
            </el-button>
            <el-button
              type="primary"
              class="btn btn-primary"
              :loading="isSubmitting"
              @click="saveSettings"
            >
              {{ isSubmitting ? '保存中...' : '保存设置' }}
            </el-button>
          </div>
        </form>
      </div>

      <!-- 安全选项设置 -->
      <div v-if="activeMenu === 'security'" class="settings-section">
        <form @submit.prevent="changePassword" class="settings-form">
          <h3 class="form-title">修改密码</h3>

          <div class="form-group">
            <label for="currentPassword">当前密码</label>
            <el-input
              id="currentPassword"
              v-model="passwordForm.oldPassword"
              type="password"
              show-password
              class="form-control"
              placeholder="请输入当前密码"
              required
            />
          </div>

          <div class="form-group">
            <label for="newPassword">新密码</label>
            <el-input
              id="newPassword"
              v-model="passwordForm.newPassword"
              type="password"
              show-password
              class="form-control"
              placeholder="请输入新密码"
              required
              @blur="validateNewPassword"
              :class="{ 'is-invalid': !passwordFormState.newPasswordValid }"
            />
            <div v-if="!passwordFormState.newPasswordValid" class="invalid-feedback">
              新密码长度至少为8位
            </div>
          </div>

          <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <el-input
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              type="password"
              show-password
              class="form-control"
              placeholder="请再次输入新密码"
              required
              @blur="validatePasswordMatch"
              :class="{ 'is-invalid': !passwordFormState.passwordsMatch }"
            />
            <div v-if="!passwordFormState.passwordsMatch" class="invalid-feedback">
              两次输入的密码不一致
            </div>
          </div>

          <div class="form-actions">
            <el-button
              type="default"
              class="btn btn-secondary"
              @click="resetPasswordForm"
            >
              取消
            </el-button>
            <el-button
              type="primary"
              class="btn btn-primary"
              :loading="isChangingPassword"
              :disabled="!passwordFormState.formValid"
              @click="changePassword"
            >
              {{ isChangingPassword ? '提交中...' : '更改密码' }}
            </el-button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage, type UploadFile } from 'element-plus';
import { useUserStore } from '@/store/user';
import { updateUserInfo, updatePassword, updateAvatar } from '@/api/user';
import type { UpdateUserInfo } from '@/types/api';
import { useRouter } from 'vue-router';

const router = useRouter();
const userStore = useUserStore();
const activeMenu = ref('profile');
const uploadRef = ref();
const avatarUploadAction = ref('');

if (!userStore.isAuthenticated) {
  router.push({ name: 'auth', query: { redirect: '/settings' } });
}

// 用户信息
const userInfo = computed(() => userStore.userInfo || {
  id: 0,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  createdAt: '',
  updatedAt: ''
});

// 用户表单数据
const userForm = reactive({
  id: userInfo.value.id,
  username: userInfo.value.username,
  nickname: userInfo.value.nickname || '',
  email: userInfo.value.email || '',
  avatar: userInfo.value.avatar || '',
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 表单状态
const formState = reactive({
  emailValid: true,
  formValid: true
});

// 密码表单状态
const passwordFormState = reactive({
  newPasswordValid: true,
  passwordsMatch: true,
  formValid: true
});

// 加载状态
const isSubmitting = ref(false);
const isChangingPassword = ref(false);
const showUserId = ref(false);
let userIdTimer: number | null = null;

// 处理用户ID显示
const handleUserIdMouseEnter = () => {
  if (userIdTimer !== null) {
    clearTimeout(userIdTimer);
  }
  userIdTimer = window.setTimeout(() => {
    showUserId.value = true;
  }, 1000);
};

const handleUserIdMouseLeave = () => {
  if (userIdTimer !== null) {
    clearTimeout(userIdTimer);
    userIdTimer = null;
  }
  showUserId.value = false;
};

// 重置表单
const resetForm = () => {
  userForm.username = userInfo.value.username;
  userForm.nickname = userInfo.value.nickname || '';
  userForm.email = userInfo.value.email || '';
  userForm.avatar = userInfo.value.avatar || '';
  formState.emailValid = true;
  formState.formValid = true;
};

// 处理头像上传
const handleAvatarUpload = (uploadFile: UploadFile) => {
  const file = uploadFile.raw;
  if (!file) return;

  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件');
    return;
  }

  const maxSize = 2 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }

  const reader = new FileReader();
  reader.onload = (e: ProgressEvent<FileReader>) => {
    if (e.target && typeof e.target.result === 'string') {
      userForm.avatar = e.target.result;
    } else {
      console.error('读取文件失败或结果不是字符串');
      ElMessage.error('读取图片失败，请重试');
    }
  };
  reader.readAsDataURL(file);
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordFormState.newPasswordValid = true;
  passwordFormState.passwordsMatch = true;
  passwordFormState.formValid = true;
};

// 验证邮箱
const validateEmail = () => {
  if (!userForm.email) {
    formState.emailValid = true;
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  formState.emailValid = emailRegex.test(userForm.email);
  formState.formValid = formState.emailValid;
};

// 验证新密码
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
    Boolean(passwordForm.oldPassword);
};

// 修改密码
const changePassword = async () => {
  validateNewPassword();
  validatePasswordMatch();

  if (!passwordFormState.formValid) {
    ElMessage.error('请检查表单中的错误');
    return;
  }

  try {
    isChangingPassword.value = true;

    const response = await updatePassword(
      passwordForm.oldPassword,
      passwordForm.newPassword
    );

    if (response.success) {
      ElMessage.success('密码修改成功');
      resetPasswordForm();
    } else {
      ElMessage.error(response.message || '密码修改失败，请检查当前密码是否正确');
    }
  } catch (error: any) {
    let errorMsg = '密码修改失败，请稍后再试';
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
    console.error('修改密码错误:', error);
  } finally {
    isChangingPassword.value = false;
  }
};

// 保存用户设置
const saveSettings = async () => {
  validateEmail();
  if (!formState.formValid) {
    ElMessage.error('请检查表单中的错误');
    return;
  }

  try {
    isSubmitting.value = true;
    const updateData: UpdateUserInfo = {};

    if (userForm.email !== userInfo.value.email) {
      updateData.email = userForm.email;
    }

    if (userForm.nickname !== userInfo.value.nickname) {
      updateData.nickname = userForm.nickname;
    }

    if (userForm.avatar !== userInfo.value.avatar) {
      if (userForm.avatar && userForm.avatar.startsWith('data:image')) {
        try {
          const blob = await fetch(userForm.avatar).then(r => r.blob());
          const file = new File([blob], 'avatar.png', { type: 'image/png' });

          const avatarResponse = await updateAvatar(file);
          if (avatarResponse.success && avatarResponse.data) {
            userForm.avatar = avatarResponse.data;
            updateData.avatar = userForm.avatar;
          } else {
            throw new Error(avatarResponse.message || '头像上传失败');
          }
        } catch (error) {
          console.error('头像上传失败:', error);
          ElMessage.error('头像上传失败，请重试');
          return;
        }
      } else {
        updateData.avatar = userForm.avatar;
      }
    }

    if (Object.keys(updateData).length > 0) {
      const response = await updateUserInfo(updateData);

      if (response.success) {
        ElMessage.success('设置保存成功');
        await userStore.fetchUserInfo();
      } else {
        ElMessage.error(response.message || '保存设置失败，请稍后再试');
      }
    } else {
      ElMessage.success('没有更改需要保存');
    }
  } catch (error: any) {
    let errorMsg = '保存设置失败，请稍后再试';
    if (error.response?.data?.message) {
      errorMsg = error.response.data.message;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
    console.error('更新设置错误:', error);
  } finally {
    isSubmitting.value = false;
  }
};

// 页面加载时获取用户信息
onMounted(async () => {
  if (userStore.isAuthenticated && !userStore.userInfo) {
    await userStore.fetchUserInfo();
  }
  resetForm();
});
</script>

<style scoped lang="scss">
.settings-container {
  padding: var(--spacing-xl);
  max-width: 1200px;
  margin: 0 auto;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.settings-title {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  font-size: var(--font-size-xxl);
  font-weight: 600;
  color: var(--heading-color);
}

.flat-menu {
  display: flex;
  background: var(--menu-bg-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm);
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 4px 6px var(--shadow-color);
}

.menu-item {
  flex: 1;
  text-align: center;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: var(--font-size-md);
  font-weight: 500;
  color: var(--menu-text-color);
  cursor: pointer;
  transition: all var(--transition-speed);
  border-radius: var(--border-radius-sm);

  &:hover {
    background-color: var(--menu-hover-bg-color);
    color: var(--text-color);
  }

  &.active {
    background: var(--menu-active-bg-color);
    color: var(--menu-active-text-color);
    box-shadow: 0 4px 6px rgba(var(--primary-color-rgb), 0.12);
    font-weight: 600;
  }
}

.settings-content {
  background: var(--card-bg-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  box-shadow: 0 10px 25px -5px var(--card-shadow-color);
  border: 1px solid var(--card-border-color);
}

.settings-section {
  padding: var(--spacing-md);
}

.settings-form {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);
}

.form-group {
  margin-bottom: var(--spacing-lg);

  label {
    display: block;
    margin-bottom: var(--spacing-sm);
    font-weight: 500;
    color: var(--text-color);
    font-size: var(--font-size-sm);
  }
}

.userid-container {
  cursor: pointer;

  .masked-userid {
    padding: var(--spacing-md) var(--spacing-lg);
    background: var(--bg-secondary-color);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius-sm);
    color: var(--text-secondary-color);
    transition: background var(--transition-speed);
    font-family: monospace;

    &:hover {
      background-color: var(--hover-color);
    }
  }
}

.text-muted {
  display: block;
  margin-top: var(--spacing-xs);
  color: var(--text-secondary-color);
  font-size: var(--font-size-xs);
}

.form-control {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-sm);
  background-color: var(--input-bg-color);
  border: 1px solid var(--input-border-color);
  color: var(--text-color);
  transition: border-color var(--transition-speed);
  font-size: var(--font-size-md);

  &:hover {
    border-color: var(--input-hover-border-color);
  }

  &:focus {
    border-color: var(--input-focus-border-color);
    outline: none;
  }
}

.avatar-layout {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;

  .avatar-preview-container {
    flex-shrink: 0;

    .avatar-preview, .avatar-placeholder {
      width: 120px;
      height: 120px;
      border-radius: var(--border-radius-circle);
      border: 1px solid var(--border-color);
      background: var(--bg-secondary-color);
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }
}

.form-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.invalid-feedback {
  color: var(--error-color);
  font-size: var(--font-size-xs);
  margin-top: var(--spacing-xs);
}

.is-invalid {
  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px var(--error-color) inset !important;
  }
}

.form-title {
  font-size: var(--font-size-xl);
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-color);
  color: var(--heading-color);
}

.btn {
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-sm);
  font-weight: 500;
  transition: all var(--transition-speed);
  font-size: var(--font-size-md);
  cursor: pointer;
  border: 1px solid transparent;

  &--primary {
    background-color: var(--primary-color);
    color: white;

    &:hover {
      background-color: var(--primary-hover-color);
    }

    &:disabled {
      background-color: var(--disabled-bg-color);
      color: var(--disabled-text-color);
      cursor: not-allowed;
    }
  }

  &--secondary {
    background-color: transparent;
    color: var(--text-color);
    border-color: var(--border-color);

    &:hover {
      background-color: var(--hover-color);
    }
  }
}

@media (max-width: 900px) {
  .settings-form {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .settings-container {
    padding: var(--spacing-md);
  }

  .avatar-layout {
    flex-direction: column;
    align-items: flex-start;
  }

  .flat-menu {
    .menu-item {
      padding: var(--spacing-sm) var(--spacing-md);
      font-size: var(--font-size-sm);
    }
  }

  .form-actions {
    flex-direction: column;

    .btn {
      width: 100%;
      margin-bottom: var(--spacing-sm);
    }
  }
}
</style>