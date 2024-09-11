<template>
  <div class="main-body">
    <div class="body w-100 flex-center-onely">
      <div class="w-960">
        <div class="flex-center-zy">
          <div class="flex-down">
            <span class="ft-56 ft-b title-nh">nh-chat</span>
            <span class="ft-48 ft-b ft-color-tips-4">完整的源代码</span>
            <span class="ft-48 ft-b ft-color-tips-4">清晰的使用文档</span>
            <span class="ft-26 ft-color-tips-3">好用、不夸张的聊天框架🥰</span>
            <div class="mt-16">
              <n-button round strong type="primary" color="rgb(255, 103, 0)">
                在线体验
              </n-button>
              <n-button
                round
                strong
                type="primary"
                color="#9300ff"
                class="ml-14"
              >
                使用手册
              </n-button>
            </div>
          </div>
          <div class="lottiePop-p" ref="lottiePop"></div>
        </div>
      </div>
    </div>
    <!-- 12 -->
    <div class="w-100 flex-center-onely">
      <div class="w-1280">
        <div class="flex-wrap">
          <div class="box card pd-6 mr-14 mb-14" v-for="i in 10">
            <h2>123</h2>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import lottie from "lottie-web";
const lottiePop = ref();
onMounted(() => {
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
.title-nh {
  background: -webkit-linear-gradient(280deg, $ft-color 20%, $ft-color-2);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}
.main-body {
  min-height: calc(100vh - $px-4);
}
.body {
  padding: $px-96 $px-32;
  box-sizing: border-box;
}
.box {
  box-shadow: 0px 0px 0.8px 0.8px white;
  background-color: $bg-color;
  overflow: hidden;
}
.lottiePop-p {
  height: $px-240;
}
.card {
  width: $px-240;
  border-radius: $px-4;
}
</style>
