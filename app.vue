<template>
  <div class="home-bg-color ft-color-white">
    <IndexNav @showLogin="showLoginEvent"></IndexNav>
    <div class="over-auto main-body">
      <NuxtPage />
    </div>
    <LoginMain v-if="showLogin" @showLogin="showLoginEvent"></LoginMain>
  </div>
</template>

<script setup lang="ts">
import WebSocketService from './utils/WebSocketService';
import { createDiscreteApi } from 'naive-ui';
import { userInfo, eqUserBasics } from '~/api/index';
const { notification } = createDiscreteApi(['notification']);
const router = useRouter();
const store = useStore();
// ws状态全局
const ws = new WebSocketService(store.token);
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
  if (msg.message.length > 30) msg.message = msg.message.substring(0, 30) + '...';
  if (msg.type === 1) msg.message = '[图片]';
  eqUserBasics(msg.receiveUid).then((res: Result) => {
    let name = res.code === 200 ? res.data.name : '未知用户';
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

// 查看消息详情
const goChat = (id: number) => {
  store.setGoChat(id);
  router.push('/try');
};

// 登录框
const showLogin = ref(false);
const showLoginEvent = (val: { win: boolean; state: boolean }) => {
  showLogin.value = val.win;
  if (val.state) location.reload();
};

// 窗口大小变化
let isSmallWin = ref(false);
const resize = () => {
  isSmallWin.value = window.screen.width < 510 || window.innerWidth < 510;
};
provide('isSmallWin', isSmallWin);

// 用户数据
const data = ref({
  state: true,
  uInfo: { photo: '哈' } as any
});
const eqUser = () => {
  userInfo().then((res) => {
    if (res.code !== 200) return (data.value.state = true);
    data.value.uInfo = res.data;
    data.value.state = false;
  });
};
provide('userInfo', data);

onMounted(() => {
  ws.connect();
  resize();
  eqUser();
  window.addEventListener('resize', resize);
});
onUnmounted(() => {
  ws.close();
  window.removeEventListener('resize', resize);
});
</script>
<style lang="scss">
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
