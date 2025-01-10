<template>
  <Main></Main>
</template>

<script setup lang='ts'>
import { setFilterColor} from '@/utils/OtherUtils';
import Main from '@/components/chat/Main.vue';

const props = defineProps({
  baseUrl: {
    type: String,
    default: 'https://www.snihao.top/nh'
  },
  inputTips: {
    type: String,
    default: '输入Ta的账号-发起聊天'
  },
  width: {
    type: String,
    default: '60rem'
  },
  height: {
    type: String,
    default: '40.625rem'
  },
  style: {
    type: Object,
    default: () => ({
      mainColor: '#2C3344', fontColor: '#e9e9e9',
      leftChatBgColor: 'rgb(213, 92, 14)', rightChatBgColor: 'rgb(126, 10, 218)'
    })
  },
  system: {
    type: Object,
    default: () => ({})
  },
  searchUserResult: {
    type: Array,
    default: []
  },
  userInfo: {
    type: Object as unknown as userInfo,
    default: () => ({uid: -1, name: '默认', photo: '默'})
  },
  messageFlicker: {
    type: Boolean,
    default: true
  },
  experienceMode: {
    type: Boolean,
    default: true
  },
  startChat: {
    type: Object as unknown as { uid: number, status: Boolean },
    default: {}
  }
} as any);
provide('param', props as chatProps);

const computedStyle = computed(() => {
  return {
    'mainColorOpt': setFilterColor(props.style.mainColor),
    'fontColorOpt': setFilterColor(props.style.fontColor),
    'fontColorOpt95': setFilterColor(props.style.fontColor, 0.95),
    'fontColorOpt35': setFilterColor(props.style.fontColor, 0.35),
    'leftChatColorOpt': setFilterColor(props.style.leftChatBgColor, 0.5),
    'rightChatColorOpt': setFilterColor(props.style.rightChatBgColor, 0.5)
  };
});
provide('computedStyle', computedStyle);

// 窗口大小变化
let isSmallWin = ref(false);
const resize = () => {
  isSmallWin.value = window.screen.width < 510 || window.innerWidth < 510;
};
provide('isSmallWin', isSmallWin);

// 初始化组件
const initModule = () => {
  sessionStorage.setItem('baseUrl', props.baseUrl);
};

onMounted(() => {
  initModule();
  resize();
  window.addEventListener('resize', resize);
});
onUnmounted(() => {
  // ws.close();
  window.removeEventListener('resize', resize);
});
</script>
<style lang='scss'>
@use '@/assets/css/constant.scss';
@use '@/assets/css/nh.scss';

body {
  padding: 0;
  margin: 0;
}

.offline-ft {
  color: #b3b7c3;
}

.online-ft {
  color: #03e903;
}

.offline {
  background-color: #b3b7c3;
}


.online {
  background-color: #03e903;
}

.home-bg-color {
  background-color: $bg-color;
}

.home-ft-color-2 {
  color: $ft-color-2;
}

.home-ft-color {
  color: $ft-color;
}

.ft-color-white {
  color: white;
}

.ft-color-tips {
  color: $ft-color-tips;
}

.ft-color-tips-2 {
  color: $ft-color-tips-2;
}

.ft-color-tips-3 {
  color: $ft-color-tips-3;
}

.ft-color-tips-4 {
  color: $ft-color-tips-4;
}

.hover-btn {
  box-sizing: border-box;
  border-style: solid;
  border-width: $px-2;
  border-color: #ffffff00;
}

.hover-btn:hover {
  background-color: $ft-color-op-7;
  cursor: pointer;
  border-radius: $px-6;
  border-color: $ft-color-tips;
}

.hover-ft:hover {
  color: $ft-color;
  cursor: pointer;
}

.mt-nav {
  margin-top: $px-64;
}

.user-head {
  width: $px-42;
  height: $px-42;
  border-radius: $px-6;
  background-color: rgb(255 255 255 / 12%);
  color: white;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  box-shadow: 0 0 $px-1 $px-0-5;

  img {
    width: 100%;
    height: 100%;
  }
}

@keyframes circle {
  0% {
    rotate: 0deg;
  }
  to {
    rotate: 360deg;
  }
}

.ant-modal div[aria-hidden='true'] {
  display: none !important;
}

.tips-link {
  margin-left: $px-6;
  color: #9300ff;
  font-weight: 600;
  cursor: pointer;
}

.main-body {
  height: 100vh;
}
</style>
