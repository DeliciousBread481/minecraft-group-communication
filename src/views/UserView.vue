<template>
  <div class="user-view">
    <el-space direction="vertical" wrap :size="30">
      <el-text class="bond-font">用户注册与登录</el-text>
      <form @submit.prevent="isRegistering ? register() : login()">
        <el-space direction="vertical" wrap :size="10">
          <div>
            <label for="username">用户名:</label>
            <input type="text" id="username" v-model="username" required>
          </div>
          <div>
            <label for="password">密码:</label>
            <input type="password" id="password" v-model="password" required>
          </div>
          <div v-if="isRegistering">
            <label for="confirm-password">确认密码:</label>
            <input type="password" id="confirm-password" v-model="confirmPassword" required>
          </div>
          <el-space direction="vertical" wrap :size="30">
            <el-button type="primary" class="button">{{ isRegistering ? '注册' : '登录' }}</el-button>
            <el-button @click="toggleForm" class="button">{{ isRegistering ? '已有账号? 登录' : '没有账号? 注册' }}</el-button>
          </el-space>
        </el-space>
      </form>
    </el-space>
  </div>
</template>

<script lang="ts">
export default {
  name: 'UserView',
  data() {
    return {
      isRegistering: false,
      username: '',
      password: '',
      confirmPassword: ''
    };
  },
  methods: {
    toggleForm() {
      this.isRegistering = !this.isRegistering;
    },
    register() {
      if (this.password !== this.confirmPassword) {
        alert('密码和确认密码不匹配');
        return;
      }
      // 在这里添加注册逻辑
      console.log('注册信息:', { username: this.username, password: this.password });
      // 注册成功后，可以重置表单
      this.resetForm();
    },
    login() {
      // 在这里添加登录逻辑
      console.log('登录信息:', { username: this.username, password: this.password });
      // 登录成功后，可以重置表单
      this.resetForm();
    },
    resetForm() {
      this.username = '';
      this.password = '';
      this.confirmPassword = '';
    }
  }
};
</script>

<style scoped>
.user-view {
  max-width: 400px;
  padding: 60px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: var(--el-box-shadow-light);
  display: flex;
  flex-direction: column;
  align-items: center;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.bond-font {
  font-size: 30px;
  font-weight: bold;
}

form div {
  margin-bottom: 10px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}

button {
  min-width: 250px;
  width: 100%;
  padding: 10px;
}
</style>
