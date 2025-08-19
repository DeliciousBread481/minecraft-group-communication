<template>
  <div class="settings-container">
    <h1 class="settings-title">个人设置</h1>

    <div class="flat-menu">
      <div
        class="menu-item"
        :class="{ active: activeMenu === 'profile' }"
        @click="activeMenu = 'profile'"
      >
        <i class="menu-icon icon-user"></i>
        <span class="menu-title">个人资料</span>
      </div>
      <div
        class="menu-item"
        :class="{ active: activeMenu === 'security' }"
        @click="activeMenu = 'security'"
      >
        <i class="menu-icon icon-lock"></i>
        <span class="menu-title">安全选项</span>
      </div>

      <div
        class="menu-item"
        :class="{ active: activeMenu === 'admin' }"
        @click="activeMenu = 'admin'"
      >
        <div class="admin-application-container">
          <i class="menu-icon icon-admin"></i>
          <span class="menu-title">申请成为管理员</span>
        </div>
      </div>
    </div>

    <div class="settings-content">
      <!-- 个人资料设置 -->
      <div v-if="activeMenu === 'profile'" class="settings-section">
        <form @submit.prevent="saveSettings" class="settings-form">
          <div class="form-group">
            <label for="userid">用户ID</label>
            <div
              class="userid-container"
              @mouseenter="handleUserIdMouseEnter"
              @mouseleave="handleUserIdMouseLeave"
            >
              <div class="masked-userid">
                <span v-if="!showUserId">●●●●●●●●</span>
                <span v-else>{{ userStore.userInfo?.id }}</span>
                <i
                  class="copy-icon icon-copy"
                  @click.stop="copyUserId"
                  title="复制用户ID"
                ></i>
              </div>
            </div>
            <small class="text-muted">悬停鼠标1秒后显示用户ID</small>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="nickname">昵称</label>
              <input
                id="nickname"
                v-model="userForm.nickname"
                type="text"
                class="form-control"
                placeholder="请输入您的昵称"
              />
              <small class="text-muted">昵称将显示给其他用户</small>
            </div>

            <div class="form-group">
              <label for="email">电子邮箱</label>
              <input
                id="email"
                v-model="userForm.email"
                type="email"
                class="form-control"
                placeholder="请输入您的邮箱"
                @blur="validateEmail"
                :class="{ 'is-invalid': !formState.emailValid }"
              />
              <div v-if="!formState.emailValid" class="invalid-feedback">
                请输入有效的电子邮箱地址
              </div>
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
                  <i class="icon-user"></i>
                </div>
              </div>
              <div class="avatar-actions">
                <button
                  type="button"
                  class="btn btn--outline"
                  @click="openFilePicker"
                >
                  <i class="icon-upload"></i> 选择图片
                </button>
                <input
                  type="file"
                  ref="avatarInput"
                  class="avatar-input"
                  accept="image/*"
                  @change="handleAvatarUpload"
                >
                <button
                  type="button"
                  class="btn btn--secondary"
                  @click="removeAvatar"
                  :disabled="!userForm.avatar"
                >
                  <i class="icon-delete"></i> 移除
                </button>
              </div>
            </div>
            <small class="text-muted">支持 JPG/PNG 格式，大小不超过 2MB</small>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn--secondary"
              @click="resetForm"
            >
              <i class="icon-reset"></i> 重置
            </button>
            <button
              type="submit"
              class="btn btn--primary"
              :disabled="isSubmitting"
            >
              <i class="icon-save" v-if="!isSubmitting"></i>
              <i class="icon-loading" v-else></i>
              {{ isSubmitting ? '保存中...' : '保存设置' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 安全选项设置 -->
      <div v-if="activeMenu === 'security'" class="settings-section">
        <form @submit.prevent="changePassword" class="settings-form">
          <h3 class="form-title">修改密码</h3>

          <div class="form-row">
            <div class="form-group">
              <label for="currentPassword">当前密码</label>
              <div class="password-input">
                <input
                  id="currentPassword"
                  v-model="passwordForm.oldPassword"
                  :type="showCurrentPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="请输入当前密码"
                />
                <i
                  class="toggle-icon"
                  :class="showCurrentPassword ? 'icon-eye-off' : 'icon-eye'"
                  @click="showCurrentPassword = !showCurrentPassword"
                ></i>
              </div>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="newPassword">新密码</label>
              <div class="password-input">
                <input
                  id="newPassword"
                  v-model="passwordForm.newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="请输入新密码"
                  @blur="validateNewPassword"
                  :class="{ 'is-invalid': !passwordFormState.newPasswordValid }"
                />
                <i
                  class="toggle-icon"
                  :class="showNewPassword ? 'icon-eye-off' : 'icon-eye'"
                  @click="showNewPassword = !showNewPassword"
                ></i>
              </div>
              <div v-if="!passwordFormState.newPasswordValid" class="invalid-feedback">
                密码长度至少为8位，需包含字母和数字
              </div>
            </div>

            <div class="form-group">
              <label for="confirmPassword">确认新密码</label>
              <div class="password-input">
                <input
                  id="confirmPassword"
                  v-model="passwordForm.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="请再次输入新密码"
                  @blur="validatePasswordMatch"
                  :class="{ 'is-invalid': !passwordFormState.passwordsMatch }"
                />
                <i
                  class="toggle-icon"
                  :class="showConfirmPassword ? 'icon-eye-off' : 'icon-eye'"
                  @click="showConfirmPassword = !showConfirmPassword"
                ></i>
              </div>
              <div v-if="!passwordFormState.passwordsMatch" class="invalid-feedback">
                两次输入的密码不一致
              </div>
            </div>
          </div>

          <div class="password-strength" v-if="passwordForm.newPassword">
            <div class="strength-bar" :class="passwordStrengthClass"></div>
            <div class="strength-text">{{ passwordStrengthText }}</div>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn--secondary"
              @click="resetPasswordForm"
            >
              <i class="icon-close"></i> 取消
            </button>
            <button
              type="submit"
              class="btn btn--primary"
              :disabled="isChangingPassword || !passwordFormState.formValid"
            >
              <i class="icon-lock" v-if="!isChangingPassword"></i>
              <i class="icon-loading" v-else></i>
              {{ isChangingPassword ? '提交中...' : '更改密码' }}
            </button>
          </div>
        </form>
      </div>

      <!-- 申请管理员设置 -->
      <div v-if="activeMenu === 'admin'" class="settings-section">
        <div class="admin-application-container">
          <h3 class="form-title">申请成为管理员</h3>

          <div class="application-info">
            <p>
              <i class="icon-info"></i>
              管理员可对群公告/解决方案库进行修改
            </p>
            <p>
              <i class="icon-warning"></i>
              请认真填写申请理由，我们将根据您的申请内容进行审核，如有疑问可咨询开发者
            </p>
          </div>

          <div class="form-group">
            <label for="applicationReason">申请理由</label>
            <textarea
              id="applicationReason"
              v-model="adminForm.reason"
              class="form-control"
              placeholder="请详细说明您申请成为管理员的理由"
              rows="6"
              :disabled="adminForm.isApplied && adminForm.status !== 'rejected'"
            ></textarea>
            <small class="text-muted">请提供至少100字的详细理由</small>
          </div>

          <div class="application-status" v-if="adminForm.isApplied">
            <div class="status-card" :class="adminForm.statusClass">
              <i :class="adminForm.statusIcon"></i>
              <span>{{ adminForm.statusText }}</span>
            </div>

            <div v-if="adminForm.adminFeedback" class="feedback-container">
              <div class="feedback-header">
                <i class="icon-comment"></i>
                <span>管理员反馈</span>
              </div>
              <div class="feedback-content">
                {{ adminForm.adminFeedback }}
              </div>
              <div class="feedback-time">
                反馈时间: {{ adminForm.feedbackTime }}
              </div>
            </div>

            <div v-if="adminForm.lastAppliedAt" class="feedback-time">
              申请时间: {{ adminForm.lastAppliedAt }}
            </div>
          </div>

          <div class="form-actions">
            <button
              type="button"
              class="btn btn--secondary"
              @click="resetAdminForm"
              :disabled="adminForm.isSubmitting"
            >
              <i class="icon-reset"></i> 重置
            </button>
            <button
              type="button"
              class="btn btn--primary"
              :disabled="!canSubmitAdminApplication || adminForm.isSubmitting"
              @click="submitAdminApplication"
            >
              <i class="icon-send" v-if="!adminForm.isSubmitting"></i>
              <i class="icon-loading" v-else></i>
              {{ adminForm.isApplied ? '重新申请' : '提交申请' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useUserStore } from '@/store/user';
import { updateUserInfo, updatePassword, updateAvatar, applyForAdmin, getAdminApplicationStatus } from '@/api/user'
import { showMessage } from '@/utils/message';
import { copyToClipboard } from '@/utils/clipboard';
import type { VoidApiResponse } from '@/types/api'

const userStore = useUserStore();
const activeMenu = ref('profile');
const avatarInput = ref<HTMLInputElement | null>(null);

const isAdmin = computed(() => {
  const roles = userStore.userInfo?.roles || [];
  return roles.some((r: string) =>
    r.toLowerCase() === 'ROLE_DEV' || r.toUpperCase() === 'ROLE_ADMIN'
  );
});

// 初始化用户信息
const userForm = reactive({
  id: userStore.userInfo?.id || 0,
  nickname: userStore.userInfo?.nickname || '',
  email: userStore.userInfo?.email || '',
  avatar: userStore.userInfo?.avatar || '',
});

// 密码表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
});

// 显示状态
const showUserId = ref(false);
const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);
const isSubmitting = ref(false);
const isChangingPassword = ref(false);
let userIdTimer: number | null = null;

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

// 密码强度计算
const passwordStrength = computed(() => {
  if (!passwordForm.newPassword) return 0;

  let strength = 0;
  const password = passwordForm.newPassword;

  // 长度评分
  if (password.length >= 8) strength += 1;
  if (password.length >= 12) strength += 1;

  // 复杂度评分
  if (/[A-Z]/.test(password)) strength += 1;
  if (/[a-z]/.test(password)) strength += 1;
  if (/[0-9]/.test(password)) strength += 1;
  if (/[^A-Za-z0-9]/.test(password)) strength += 1;

  return Math.min(strength, 4);
});

const passwordStrengthClass = computed(() => {
  if (passwordStrength.value === 0) return '';
  return `strength-${passwordStrength.value}`;
});

const passwordStrengthText = computed(() => {
  const texts = ['非常弱', '弱', '中等', '强', '非常强'];
  return texts[passwordStrength.value];
});

// 处理用户ID显示
const handleUserIdMouseEnter = () => {
  userIdTimer = setTimeout(() => {
    showUserId.value = true;
  }, 1000);
};

const handleUserIdMouseLeave = () => {
  if (userIdTimer) clearTimeout(userIdTimer);
  showUserId.value = false;
};

// 复制用户ID
const copyUserId = () => {
  if (userStore.userInfo?.id) {
    if (copyToClipboard(userStore.userInfo.id.toString())) {
      showMessage('用户ID已复制到剪贴板', 'success');
    } else {
      showMessage('复制失败，请手动复制', 'warning');
    }
  }
};

// 重置表单
const resetForm = () => {
  userForm.nickname = userStore.userInfo?.nickname || '';
  userForm.email = userStore.userInfo?.email || '';
  userForm.avatar = userStore.userInfo?.avatar || '';
  formState.emailValid = true;
};

// 打开文件选择器
const openFilePicker = () => {
  if (avatarInput.value) {
    avatarInput.value.click();
  }
};

// 处理头像上传
const handleAvatarUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (!input.files || input.files.length === 0) return;

  const file = input.files[0];
  if (!file.type.match('image.*')) {
    showMessage('请上传图片文件', 'error');
    return;
  }

  if (file.size > 2 * 1024 * 1024) {
    showMessage('图片大小不能超过2MB', 'error');
    return;
  }

  const reader = new FileReader();
  reader.onload = (e) => {
    if (e.target?.result) {
      userForm.avatar = e.target.result as string;
    }
  };
  reader.readAsDataURL(file);

  // 重置输入，允许重复选择相同文件
  input.value = '';
};

// 移除头像
const removeAvatar = () => {
  userForm.avatar = '';
};

// 重置密码表单
const resetPasswordForm = () => {
  passwordForm.oldPassword = '';
  passwordForm.newPassword = '';
  passwordForm.confirmPassword = '';
  passwordFormState.newPasswordValid = true;
  passwordFormState.passwordsMatch = true;
};

// 验证邮箱
const validateEmail = () => {
  if (!userForm.email) {
    formState.emailValid = true;
    return;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  formState.emailValid = emailRegex.test(userForm.email);
};

// 验证新密码
const validateNewPassword = () => {
  if (!passwordForm.newPassword) {
    passwordFormState.newPasswordValid = true;
    return;
  }

  passwordFormState.newPasswordValid =
    passwordForm.newPassword.length >= 8 &&
    /[A-Za-z]/.test(passwordForm.newPassword) &&
    /[0-9]/.test(passwordForm.newPassword);

  validatePasswordMatch();
};

// 验证密码匹配
const validatePasswordMatch = () => {
  passwordFormState.passwordsMatch =
    passwordForm.newPassword === passwordForm.confirmPassword;

  passwordFormState.formValid =
    passwordFormState.newPasswordValid &&
    passwordFormState.passwordsMatch;
};

// 修改密码
const changePassword = async () => {
  if (!passwordFormState.formValid) {
    showMessage('请检查表单中的错误', 'error');
    return;
  }

  try {
    isChangingPassword.value = true;
    const response = await updatePassword(
      passwordForm.oldPassword,
      passwordForm.newPassword
    );

    if (response.success) {
      showMessage('密码修改成功', 'success');
      resetPasswordForm();
    } else {
      showMessage(response.message || '密码修改失败', 'error');
    }
  } catch (error) {
    showMessage('密码修改失败，请稍后再试', 'error');
    console.error('修改密码错误:', error);
  } finally {
    isChangingPassword.value = false;
  }
};

// 保存用户设置
const saveSettings = async () => {
  validateEmail();
  if (!formState.emailValid) {
    showMessage('请检查表单中的错误', 'error');
    return;
  }

  try {
    isSubmitting.value = true;
    const updateData: Record<string, any> = {};

    // 检查哪些字段有变化
    if (userForm.nickname !== userStore.userInfo?.nickname) {
      updateData.nickname = userForm.nickname;
    }

    if (userForm.email !== userStore.userInfo?.email) {
      updateData.email = userForm.email;
    }

    // 单独处理头像上传
    let avatarUpdated = false;
    if (userForm.avatar && userForm.avatar !== userStore.userInfo?.avatar) {
      if (userForm.avatar.startsWith('data:image')) {
        // 转换base64为Blob
        const blob = await fetch(userForm.avatar).then(r => r.blob());
        const file = new File([blob], 'avatar.png', { type: 'image/png' });

        // 上传头像
        const avatarResponse = await updateAvatar(file);
        if (avatarResponse.success && avatarResponse.data) {
          updateData.avatar = avatarResponse.data;
          avatarUpdated = true;
        } else {
          throw new Error(avatarResponse.message || '头像上传失败');
        }
      } else {
        // 如果是URL，直接使用
        updateData.avatar = userForm.avatar;
        avatarUpdated = true;
      }
    }

    if (Object.keys(updateData).length > 0) {
      const response = await updateUserInfo(updateData);

      if (response.success) {
        showMessage('设置保存成功', 'success');
        // 更新本地存储
        if (avatarUpdated) {
          userForm.avatar = updateData.avatar;
        }
        // 刷新用户信息
        await userStore.fetchUserInfo();
      } else {
        showMessage(response.message || '保存设置失败', 'error');
      }
    } else {
      showMessage('没有更改需要保存', 'info');
    }
  } catch (error: any) {
    showMessage(error.message || '保存设置失败，请稍后再试', 'error');
    console.error('更新设置错误:', error);
  } finally {
    isSubmitting.value = false;
  }
};

// 管理员申请表单
const adminForm = reactive({
  reason: '',
  isApplied: false,
  status: 'pending', // 'pending' | 'approved' | 'rejected'
  statusText: '',
  statusIcon: '',
  statusClass: '',
  adminFeedback: '',
  feedbackTime: '',
  isSubmitting: false,
  lastAppliedAt: '',
});

// --- 管理员申请逻辑 ---
const canSubmitAdminApplication = computed(() => adminForm.reason.length >= 100);

const fetchAdminApplicationStatus = async () => {
  try {
    const response = await getAdminApplicationStatus();
    if (response.success && response.data) {
      adminForm.isApplied = true;
      adminForm.status = response.data.status;
      adminForm.adminFeedback = response.data.feedback || '';
      adminForm.lastAppliedAt = response.data.createdAt
        ? new Date(response.data.createdAt).toLocaleString()
        : '';
      updateStatusDisplay();
    } else {
      adminForm.isApplied = false;
    }
  } catch (error) {
    console.error('获取管理员申请状态失败:', error);
    showMessage('获取申请状态失败，请稍后再试', 'error');
  }
};

const updateStatusDisplay = () => {
  switch (adminForm.status) {
    case 'approved':
      adminForm.statusText = '申请已通过';
      adminForm.statusIcon = 'icon-check-circle';
      adminForm.statusClass = 'status-approved';
      break;
    case 'rejected':
      adminForm.statusText = '申请被拒绝';
      adminForm.statusIcon = 'icon-close-circle';
      adminForm.statusClass = 'status-rejected';
      break;
    default:
      adminForm.statusText = '审核中';
      adminForm.statusIcon = 'icon-time';
      adminForm.statusClass = 'status-pending';
      break;
  }
};

const submitAdminApplication = async () => {
  if (!canSubmitAdminApplication.value) {
    showMessage('请填写至少100字的申请理由', 'warning');
    return;
  }
  try {
    adminForm.isSubmitting = true;
    const response: VoidApiResponse = await applyForAdmin(adminForm.reason);
    if (response.success) {
      showMessage('申请已提交，请等待审核', 'success');
      adminForm.isApplied = true;
      adminForm.status = 'pending';
      adminForm.lastAppliedAt = new Date().toLocaleString();
      updateStatusDisplay();
      await fetchAdminApplicationStatus();
    } else {
      showMessage(response.message || '申请提交失败，请稍后再试', 'error');
    }
  } catch (error) {
    console.error('提交管理员申请失败:', error);
    showMessage('申请提交失败，请稍后再试', 'error');
  } finally {
    adminForm.isSubmitting = false;
  }
};

const resetAdminForm = () => {
  adminForm.reason = '';
  adminForm.isApplied = false;
  adminForm.status = 'pending';
  adminForm.adminFeedback = '';
  adminForm.feedbackTime = '';
  adminForm.lastAppliedAt = '';
};

// --- 生命周期 & 监听 ---
onMounted(() => {
  resetForm();
  if (!isAdmin.value) {
    fetchAdminApplicationStatus();
  }
});

watch(
  () => userStore.userInfo,
  (newUser) => {
    if (newUser && !isAdmin.value) {
      fetchAdminApplicationStatus();
    }
  },
  { immediate: false }
);
</script>

<style scoped lang="scss">
@use '@/styles/core/variables' as var;
@use '@/styles/core/mixins' as mix;

.settings-container {
  padding: var.$spacing-xl;
  max-width: 800px;
  margin: 0 auto;
  background-color: var(--bg-color);
  color: var(--text-color);
}

.settings-title {
  text-align: center;
  margin-bottom: var.$spacing-xl;
  font-size: var.$font-size-xxl;
  font-weight: 600;
  color: var(--heading-color);
}

.flat-menu {
  display: flex;
  background: var(--menu-bg-color);
  border-radius: var.$border-radius-md;
  overflow: hidden;
  margin-bottom: var.$spacing-lg;
  box-shadow: 0 2px 8px var(--shadow-color);
}

.menu-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var.$spacing-md;
  font-size: var.$font-size-sm;
  color: var(--text-secondary-color);
  cursor: pointer;
  transition: all var.$transition-speed;

  &:hover {
    background-color: var(--hover-color);
    color: var(--text-color);
  }

  &.active {
    background: var(--primary-color);
    color: white;
    box-shadow: 0 0 10px rgba(var(--primary-color-rgb), 0.3);

    .menu-icon {
      color: white;
    }
  }
}

.menu-icon {
  font-size: var.$font-size-lg;
  margin-bottom: var.$spacing-xs;
}

.menu-title {
  font-weight: 500;
}

.settings-content {
  background: var(--card-bg-color);
  border-radius: var.$border-radius-lg;
  padding: var.$spacing-xl;
  box-shadow: 0 4px 20px var(--card-shadow-color);
  border: 1px solid var(--card-border-color);
}

.settings-section {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.settings-form {
  display: grid;
  gap: var.$spacing-lg;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: var.$spacing-lg;
}

.form-group {
  margin-bottom: var.$spacing-lg;
}

label {
  display: block;
  margin-bottom: var.$spacing-sm;
  font-weight: 500;
  color: var(--text-color);
  font-size: var.$font-size-sm;
}

.userid-container {
  cursor: pointer;
  position: relative;
}

.masked-userid {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var.$spacing-md var.$spacing-lg;
  background: var(--bg-secondary-color);
  border: 1px solid var(--border-color);
  border-radius: var.$border-radius-sm;
  color: var(--text-secondary-color);
  transition: all var.$transition-speed;
  font-family: monospace;

  &:hover {
    background-color: var(--hover-color);
    border-color: var(--primary-color);
  }
}

.copy-icon {
  color: var(--text-secondary-color);
  cursor: pointer;
  transition: color var.$transition-speed;

  &:hover {
    color: var(--primary-color);
  }
}

.text-muted {
  display: block;
  margin-top: var.$spacing-xs;
  color: var(--text-secondary-color);
  font-size: var.$font-size-xs;
}

.form-control {
  width: 100%;
  padding: var.$spacing-sm var.$spacing-md;
  border-radius: var.$border-radius-sm;
  background-color: var(--input-bg-color);
  border: 1px solid var(--input-border-color);
  color: var(--text-color);
  transition: border-color var.$transition-speed;
  font-size: var.$font-size-md;

  &:hover {
    border-color: var(--input-hover-border-color);
  }

  &:focus {
    border-color: var(--input-focus-border-color);
    outline: none;
    box-shadow: 0 0 0 2px rgba(var(--primary-color-rgb), 0.1);
  }
}

.is-invalid {
  border-color: var(--error-color);

  &:focus {
    box-shadow: 0 0 0 2px rgba(var(--error-color-rgb), 0.1);
  }
}

.invalid-feedback {
  color: var(--error-color);
  font-size: var.$font-size-xs;
  margin-top: var.$spacing-xs;
}

.avatar-layout {
  display: flex;
  gap: var.$spacing-lg;
  align-items: center;

  @include mix.respond-to(tablet) {
    flex-direction: column;
    align-items: flex-start;
  }
}

.avatar-preview-container {
  flex-shrink: 0;
}

.avatar-preview, .avatar-placeholder {
  width: 120px;
  height: 120px;
  border-radius: var.$border-radius-circle;
  border: 1px solid var(--border-color);
  background: var(--bg-secondary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  .avatar-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .icon-user {
    font-size: 48px;
    color: var(--text-secondary-color);
  }
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: var.$spacing-sm;
}

.avatar-input {
  display: none;
}

.password-input {
  position: relative;

  .toggle-icon {
    position: absolute;
    right: var.$spacing-md;
    top: 50%;
    transform: translateY(-50%);
    color: var(--text-secondary-color);
    cursor: pointer;
    transition: color var.$transition-speed;

    &:hover {
      color: var(--primary-color);
    }
  }
}

.password-strength {
  margin-top: var.$spacing-md;

  .strength-bar {
    height: 6px;
    border-radius: var.$border-radius-sm;
    background-color: var(--border-color);
    overflow: hidden;
    margin-bottom: var.$spacing-xs;

    &.strength-1 {
      background: linear-gradient(90deg, var(--error-color) 25%, var(--border-color) 25%);
    }

    &.strength-2 {
      background: linear-gradient(90deg, var(--warning-color) 50%, var(--border-color) 50%);
    }

    &.strength-3 {
      background: linear-gradient(90deg, var(--success-color) 75%, var(--border-color) 75%);
    }

    &.strength-4 {
      background-color: var(--success-color);
    }
  }

  .strength-text {
    font-size: var.$font-size-xs;
    text-align: right;
    color: var(--text-secondary-color);
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var.$spacing-md;
  margin-top: var.$spacing-xl;
  padding-top: var.$spacing-lg;
  border-top: 1px solid var(--border-color);

  .btn {
    min-width: 120px;
  }
}

.form-title {
  font-size: var.$font-size-xl;
  margin-bottom: var.$spacing-lg;
  padding-bottom: var.$spacing-sm;
  border-bottom: 1px solid var(--border-color);
  color: var(--heading-color);
}

.icon-loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@include mix.respond-to(tablet) {
  .settings-container {
    padding: var.$spacing-md;
  }

  .settings-content {
    padding: var.$spacing-lg;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column;

    .btn {
      width: 100%;
    }
  }
}

@include mix.respond-to(phone) {
  .flat-menu {
    .menu-title {
      font-size: var.$font-size-xs;
    }
  }
}

.admin-application-container {
  max-width: 700px;
  margin: 0 auto;
  padding: var.$spacing-md;
}

.application-info {
  background-color: var(--bg-secondary-color);
  border-radius: var.$border-radius-md;
  padding: var.$spacing-md;
  margin-bottom: var.$spacing-lg;

  p {
    display: flex;
    align-items: flex-start;
    margin-bottom: var.$spacing-sm;

    i {
      margin-right: var.$spacing-sm;
      font-size: var.$font-size-lg;
      min-width: 24px;
    }

    &:first-child i {
      color: var(--info-color);
    }

    &:last-child i {
      color: var(--warning-color);
    }
  }
}

.status-card {
  display: flex;
  align-items: center;
  padding: var.$spacing-md;
  border-radius: var.$border-radius-md;
  font-weight: 500;
  margin-bottom: var.$spacing-md;

  i {
    font-size: var.$font-size-xl;
    margin-right: var.$spacing-sm;
  }

  &.status-pending {
    background-color: rgba(var(--info-color-rgb), 0.1);
    color: var(--info-color);
    border-left: 4px solid var(--info-color);
  }

  &.status-approved {
    background-color: rgba(var(--success-color-rgb), 0.1);
    color: var(--success-color);
    border-left: 4px solid var(--success-color);
  }

  &.status-rejected {
    background-color: rgba(var(--error-color-rgb), 0.1);
    color: var(--error-color);
    border-left: 4px solid var(--error-color);
  }
}

.feedback-container {
  margin-top: var.$spacing-lg;
  padding: var.$spacing-md;
  background-color: var(--bg-secondary-color);
  border-radius: var.$border-radius-md;
  border-left: 3px solid var(--primary-color);
}

.feedback-header {
  display: flex;
  align-items: center;
  margin-bottom: var.$spacing-sm;
  font-weight: 500;
  color: var(--primary-color);

  i {
    margin-right: var.$spacing-sm;
  }
}

.feedback-content {
  line-height: 1.6;
  padding: var.$spacing-sm;
  background-color: var(--bg-tertiary-color);
  border-radius: var.$border-radius-sm;
  white-space: pre-wrap;
}

.feedback-time {
  text-align: right;
  font-size: var.$font-size-xs;
  color: var(--text-secondary-color);
  margin-top: var.$spacing-xs;
}

// 响应式调整
@include mix.respond-to(tablet) {
  .admin-application-container {
    padding: 0;
  }

  .application-info {
    padding: var.$spacing-sm;
  }
}
</style>