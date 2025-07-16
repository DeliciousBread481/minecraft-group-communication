<template>
  <div class="layout-container">
    <AppHeader class="header" @toggle-menu="toggleMobileMenu" />

    <div class="main-container">
      <AppSidebar
        class="left-aside"
        :class="{ 'mobile-hidden': isMobileMenuHidden }"
      />

      <div class="right-aside" :class="{ 'auth-mode': isAuthPage }">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import AppHeader from './AppHeader.vue'
import AppSidebar from './AppSidebar.vue'

const route = useRoute()
const isMobileMenuHidden = ref(false)

const isAuthPage = computed(() => route.path.startsWith('/auth'))

const checkMobile = () => {
  isMobileMenuHidden.value = window.innerWidth < 768 && !isAuthPage.value
}

const toggleMobileMenu = () => {
  isMobileMenuHidden.value = !isMobileMenuHidden.value
}

onMounted(() => {
  window.addEventListener('resize', checkMobile)
  checkMobile()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.header {
  height: 60px;
  flex-shrink: 0;
  z-index: 100;
  position: fixed;
  width: 100%;
  top: 0;
}

.main-container {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
  margin-top: 60px;
}

.left-aside {
  width: 220px;
  flex-shrink: 0;
  height: 100%;
  overflow-y: auto;
  border-right: 1px solid var(--border-color);
  background-color: var(--bg-color);
  transition: width 0.3s ease;
}

.right-aside {
  flex: 1;
  display: flex;
  min-width: 0;
  padding: 20px;
  background-color: var(--bg-color);
  overflow: auto;
}

/* 认证页面全屏模式 */
.right-aside.auth-mode {
  padding: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.dark .right-aside.auth-mode {
  background: linear-gradient(135deg, #1a2b3c 0%, #2c3e50 100%);
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .left-aside {
    width: 60px;
    position: fixed;
    z-index: 99;
    height: calc(100vh - 60px);
  }

  .right-aside {
    padding: 15px;
  }
}
</style>
