<template>
  <div class="main-body flex-center-onely">
    <div class="body">123</div>
    <!-- <div class="lottiePop-p" ref="lottiePop"></div> -->
  </div>
</template>

<script setup lang="ts">
import lottie from "lottie-web";
const lottiePop = ref();
onMounted(() => {
  // 在这里执行需要在挂载后执行的逻辑
  console.log("组件已挂载", lottie);
  lottie.loadAnimation({
    container: lottiePop.value,
    renderer: "svg",
    loop: true,
    autoplay: true,
    path: "https://assets1.lottiefiles.com/packages/lf20_Au6z826BEy.json",
  });
});
</script>

<style lang="scss" scoped>
.main-body {
  margin-top: 4rem;
  min-height: calc(100vh - 4rem);
}
.body {
  width: 1320px;
}
.lottiePop-p {
  height: 180px;
}
</style>
