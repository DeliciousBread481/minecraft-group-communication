<template>
  <div class="settings-container">
    <h1 class="settings-title">个人设置</h1>

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
        <div v-if="activeMenu === 'security'">
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
    </el-card>
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

// 菜单选择处理
const handleMenuSelect = (key: string) => {
  activeMenu.value = key;
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

  // 验证文件是否为图片
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请上传图片文件');
    return;
  }

  // 限制文件大小（2MB）
  const maxSize = 2 * 1024 * 1024; // 2MB
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过2MB');
    return;
  }

  // 创建预览
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
    // 显示详细的错误信息
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
    // 只更新已更改的字段
    const updateData: UpdateUserInfo = {};

    if (userForm.email !== userInfo.value.email) {
      updateData.email = userForm.email;
    }

    if (userForm.nickname !== userInfo.value.nickname) {
      updateData.nickname = userForm.nickname;
    }

    if (userForm.avatar !== userInfo.value.avatar) {
      // 检查是否为Base64数据URL(从文件上传获得)
      if (userForm.avatar && userForm.avatar.startsWith('data:image')) {
        try {
          // 创建一个File对象用于上传
          const blob = await fetch(userForm.avatar).then(r => r.blob());
          const file = new File([blob], 'avatar.png', { type: 'image/png' });

          // 调用头像上传API
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

    // 仅当有更改时才提交
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
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;

  @media (min-width: 1400px) {
    max-width: 1400px;
  }
}

.settings-title {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--text-color);
  font-size: 1.8rem;
}

.menu-card {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px var(--shadow-color);

  min-width: 500px;
  width: 100%;

  @media (min-width: 768px) {
    max-width: 800px;
    margin: 0 auto;
  }

  @media (min-width: 992px) {
    max-width: 1000px;
  }

  @media (min-width: 1200px) {
    max-width: 1200px;
  }
}

.card-form-container {
  padding: 2rem;

  @media (min-width: 768px) {
    padding: 3rem;
  }
}

.settings-form {
  @media (min-width: 768px) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;

    .form-group:last-child {
      grid-column: 1 / -1;
    }

    .form-actions {
      grid-column: 1 / -1;
    }
  }

  .form-group {
    margin-bottom: 1.5rem;

    label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: 500;
      color: var(--text-color);
    }

    .text-muted {
      display: block;
      margin-top: 0.5rem;
      color: var(--text-secondary-color);
      font-size: 0.85rem;
    }
  }
}

.userid-container {
  cursor: pointer;

  .masked-userid {
    padding: 0.75rem;
    background-color: var(--bg-secondary-color);
    border: 1px solid var(--border-color);
    border-radius: 8px;
    color: var(--text-secondary-color);
    transition: background-color 0.3s;

    &:hover {
      background-color: var(--hover-color);
    }
  }
}

.avatar-layout {
  display: flex;
  gap: 1.5rem;
  align-items: center;

  @media (min-width: 768px) {
    gap: 2.5rem;
  }

  .avatar-preview-container {
    flex: 0 0 120px;

    @media (min-width: 768px) {
      flex: 0 0 150px;
    }

    .avatar-preview, .avatar-placeholder {
      width: 120px;
      height: 120px;
      border-radius: 50%;
      overflow: hidden;
      border: 1px solid var(--border-color);
      background-color: var(--bg-secondary-color);
      display: flex;
      align-items: center;
      justify-content: center;

      @media (min-width: 768px) {
        width: 150px;
        height: 150px;
      }
    }

    .avatar-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;

  @media (min-width: 768px) {
    gap: 1.5rem;
  }
}

.btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;

  &-primary {
    background-color: var(--primary-color);
    color: white;
    border: none;

    &:hover {
      background-color: var(--primary-hover-color);
    }

    &:disabled {
      background-color: var(--primary-disabled-color);
      cursor: not-allowed;
    }
  }

  &-secondary {
    background-color: transparent;
    color: var(--text-color);
    border: 1px solid var(--border-color);

    &:hover {
      background-color: var(--hover-color);
    }
  }

  @media (min-width: 768px) {
    padding: 0.9rem 1.8rem;
    font-size: 1rem;
  }
}

.invalid-feedback {
  color: var(--error-color);
  font-size: 0.85rem;
  margin-top: 0.25rem;
}

.is-invalid {
  border-color: var(--error-color) !important;

  &:focus {
    box-shadow: 0 0 0 2px rgba(var(--error-color-rgb), 0.2);
  }
}

@media (max-width: 767px) {
  .settings-container {
    padding: 1rem;
  }

  .menu-card {
    min-width: unset;
  }

  .card-form-container {
    padding: 1.5rem;
  }

  .avatar-layout {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>