<template>
  <Main></Main>
</template>

<script setup lang='ts'>
import WebSocketService from '@/utils/WebSocketService';
import {createDiscreteApi} from 'naive-ui';

const {notification} = createDiscreteApi(['notification']);
import {useRouter} from 'vue-router';
import {setUser, setFilterColor} from '@/utils/OtherUtils';
import Main from '@/components/chat/Main.vue';

const router = useRouter();

const props = defineProps({
  baseUrl: {
    type: String,
    // default: 'http://localhost:8087'
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
  eqUserInfo: {
    type: String,
    default: 'https://www.snihao.top/nh/user/eqUserBasics'
  },
  userInfo: {
    type: Object as unknown as userInfo,
    default: () => ({uid: -1, name: '默认', photo: '默'})
  },
  socketUrl: {
    type: String,
    // default: 'ws://localhost:8087/socket.chat/'
    default: 'wss://www.snihao.top/wss/socket.chat/'
  },
  token: {
    type: String,
    default: ''
  },
  chatRoute: {
    type: String,
    default: '/'
  },
  messageFlicker: {
    type: Boolean,
    default: true
  },
  experienceMode: {
    type: Boolean,
    default: true
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

// ws状态全局
const ws = new WebSocketService(props.socketUrl + props.token);
provide('webSocketService', ws);

// 获取当前聊天的uid
const nowUid = ref(-1);
const getNowChatUid = (uid: number) => {
  nowUid.value = uid;
};
provide('getNowChatUid', getNowChatUid);

watch(
    () => ws.pushCount,
    () => {
      if (nowUid.value !== ws.newMessage.receiveUid) ofNewMessage(ws.newMessage);
    },
    {
      deep: true
    }
);
// 新消息处理
let saveInfo = ref([] as any);
const ofNewMessage = (msg: any) => {
  if (msg.mid === -1) return;
  if (msg.message.length > 30) msg.message = msg.message.substring(0, 30) + '...';
  if (msg.type === 1) msg.message = '[图片]';
  setUser([msg], props.eqUserInfo).then((res: any) => {
    let name = res[0].name;
    if (saveInfo.value.length > 3) {
      saveInfo.value.shift();
      saveInfo.value[0].destroy();
    }
    const n = notification['info']({
      content: '你收到了一条来自 ' + name + ' 的消息',
      meta: () =>
          h('div', null, [
            msg.message,
            h(
                'span',
                {
                  onClick: (event) => {
                    event.stopPropagation();
                    n.destroy();
                    goChat(msg.receiveUid);
                  },
                  class: 'tips-link'
                },
                '查看详情'
            )
          ]),
      duration: 3500,
      keepAliveOnHover: true
    });
    saveInfo.value.push(n);
  });
};
provide('ofNewMessage', ofNewMessage);

// 查看消息详情
const goChat = (id: number) => {
  sessionStorage.setItem('go_chat_uid', id.toString());
  router.push(props.chatRoute);
};

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

const handleBeforeUnload = (event: BeforeUnloadEvent) => {
  ws.close();
};

onMounted(() => {
  initModule();
  ws.connect();
  resize();
  window.addEventListener('resize', resize);
  window.addEventListener('beforeunload', handleBeforeUnload);
});
onUnmounted(() => {
  ws.close();
  window.removeEventListener('resize', resize);
  window.removeEventListener('beforeunload', handleBeforeUnload);
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
