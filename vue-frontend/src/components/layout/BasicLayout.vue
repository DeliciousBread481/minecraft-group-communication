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

<style scoped lang="scss">
@use '@/styles/components' as *;
</style>