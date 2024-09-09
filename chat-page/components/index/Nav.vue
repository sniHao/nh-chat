<template>
  <div class="main-nav flex-center-center" ref="containerRef">
    <div class="nav flex-center-zy pd-6">
      <div class="flex-center">
        <div class="ft-26 ft-b home-ft-color">nh-chat</div>
        <div class="ml-26 ft-color-tips-1">演示</div>
        <div class="ml-16 ft-color-tips-2">说明文档</div>
      </div>
      <div class="flex">
        <div>cut</div>
        <div class="ml-16">github</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const containerRef = ref<HTMLElement | undefined>(undefined);
</script>

<style lang="scss" scoped>
.main-nav {
  position: fixed;
  top: 0;
  height: 4rem;
  width: 100%;
  background-color: #17172b;
  z-index: 999;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}
.nav {
  width: 80rem;
}
</style>
