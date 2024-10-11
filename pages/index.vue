<template>
  <div class="body w-100 flex-center-onely">
    <div class="w-960">
      <div class="flex-center-zy">
        <div class="flex-down w-min-360">
          <span class="ft-56 ft-b title-nh">nh-chat</span>
          <span class="ft-48 ft-b ft-color-tips-4">完整的源代码</span>
          <span class="ft-48 ft-b ft-color-tips-4">清晰的说明文档</span>
          <span class="ft-26 ft-color-tips-3">好用、不夸张的聊天框架🥰</span>
          <div class="mt-16">
            <n-button round strong type="primary" color="#ff6700" @click="goTry">在线体验</n-button>
            <n-button round strong type="primary" color="#9300ff" class="ml-14">使用手册</n-button>
          </div>
        </div>
        <div class="lottiePop-p" ref="lottiePop"></div>
      </div>
    </div>
  </div>
  <!-- 简介 -->
  <div class="w-100 flex-center-onely">
    <div class="w-1280">
      <div class="flex-center-center ft-28 ft-color-white mb-16">采用前沿技术路线</div>
      <div class="flex-wrap flex-center-onely">
        <div class="box card flex-down-center pd-6 ml-7 mr-7 mb-14" v-for="(item, index) in technology" :key="index">
          <div class="ft-28 home-ft-color mb-14">{{ item.name }}</div>
          <n-timeline>
            <n-timeline-item color="rgb(147, 0, 255)" :content="ite" v-for="(ite, index) in item.list" :key="index" />
          </n-timeline>
        </div>
      </div>
      <div class="flex-center-center ft-28 ft-color-white mb-16 mt-20">赞助商</div>
      <div class="flex-wrap flex-center-center">
        <div class="flex-center-center pd-6 ml-10 mr-10" style="width: unset" v-for="(item, index) in sponsor" :key="index">
          <div class="ft-28 home-ft-color-2 hover-pointer">{{ item.name }}</div>
        </div>
        <n-button color="rgb(147, 0, 255)" size="large" dashed round>成为赞助商</n-button>
      </div>
    </div>
  </div>
  <!-- end -->
  <div class="w-100 flex-center-onely">
    <div class="w-100">
      <n-divider style="--n-color: rgb(255 103 0/ 30%);margin-bottom: unset;" />
      <div class="flex-center-center pd-sx-12">
        <span class="ft-color-tips">备案号xxx</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import lottie from 'lottie-web';
const router = useRouter();

const goTry = () => {
  router.push('/try');
};

const technology = ref([
  {
    name: '前端🤖',
    list: ['Nuxt3', 'Vue3', 'Vite', 'TypeScript', 'Pinia', 'Naive UI', 'Lottie']
  },
  {
    name: '后端🚀',
    list: ['JDK21', 'SpringBoot3', 'MyBatis-Plus', 'SpringSecurity', 'JWT', 'Swagger3']
  },
  {
    name: '消息👨‍🚀',
    list: ['WebSocket']
  },
  {
    name: '存储🙈',
    list: ['Mysql', 'Redis']
  }
]);
const sponsor = ref([
  { name: 'Github', url: '' },
  { name: 'Gitee', url: '' }
]);
const lottiePop = ref();
onMounted(() => {
  lottie.loadAnimation({
    container: lottiePop.value,
    renderer: 'svg',
    loop: true,
    autoplay: true,
    path: 'https://assets1.lottiefiles.com/packages/lf20_Au6z826BEy.json'
  });
});
</script>

<style lang="scss" scoped>
:deep(.n-timeline-item-content__content) {
  color: $ft-color-tips !important;
}
:deep(.n-timeline-item-timeline__line) {
  background-color: $ft-color-2 !important;
}
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
  width: $px-280;
  border-radius: $px-8;
  background-color: rgb(255 255 255 / 12%);
  box-shadow: 0 0 0 $px-0-5 rgb(210, 210, 210);
}
</style>
