<template>
  <div class="main-nav flex-center-center">
    <div class="nav flex-center-zy pd-6">
      <div class="flex-center ft-b">
        <div class="ft-26 home-ft-color hover-pointer" @click="goHome">nh-chat</div>
        <div class="ml-26 ft-color-tips hover-ft" @click="goGithub">演示</div>
        <div class="ml-16 ft-color-tips hover-ft" @click="goDocs">说明文档</div>
      </div>
      <div class="flex">
        <div class="flex-center-center pd-6 hover-btn" @click="switchMode">
          <OfSvg :width="26" :height="26" name="system-dark" v-if="modeStyle"></OfSvg>
          <OfSvg :width="26" :height="26" name="system-light" v-else></OfSvg>
        </div>
        <div class="flex-center-center pd-6 hover-btn">
          <OfSvg :width="28" :height="28" viewBox="0 0 512 512" name="github"></OfSvg>
          <span class="ml-4 ft-color-tips ft-b">{{ githubCount }}</span>
        </div>
        <div class="flex-center-center pd-6 hover-btn" @click="goLogin">
          <span class="ft-color-tips ft-b">登录 | 注册</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const githubCount = ref('12k');
const router = useRouter();
const emit = defineEmits(['showLogin']);
// 跳首页
const goHome = () => {
  router.push('/');
};
// 演示
const goGithub = () => {
  window.open('https://www.baidu.com', '_blank');
};
// 说明文档
const goDocs = () => {
  router.push('/docs');
};
// 登录
const goLogin = () => {
  emit('showLogin', true);
};

// 切换主题
const modeStyle = ref(true);
const switchMode = () => {
  modeStyle.value = !modeStyle.value;
};
</script>

<style lang="scss" scoped>
.main-nav {
  position: fixed;
  top: 0;
  height: $px-64;
  width: calc(100vw - 0.4rem); //减去滚动条宽度
  background-color: #0f172a99;
  backdrop-filter: blur($px-4);
  z-index: 9;
  box-shadow: 0 $px-2 $px-5 rgba(0, 0, 0, 0.2);
}
.nav {
  width: $px-1320;
}
</style>
