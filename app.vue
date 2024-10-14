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
  () => ws.newMessage.uid,
  () => {
    // 收到消息对象不是当前聊天对象则推消息
    if (nowUid.value !== ws.newMessage.uid) ofNewMessage(ws.newMessage);
  }
);
// 新消息处理
const ofNewMessage = (msg: any) => {
  console.log('推消息咯', msg);
  if (msg.value.length > 30) msg.value = msg.value.substring(0, 30) + '...';
  notification['info']({
    content: '你收到了一条来自' + msg.uid + '的消息',
    meta: () => h('div', null, [msg.value, h('span', { onClick: goChat(msg.uid), class: 'tips-link' }, '查看详情')]),
    duration: 2500,
    keepAliveOnHover: true
  });
};

// 查看消息详情
const goChat = (id: number) => {
  router.push('/try/' + id);
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

onMounted(() => {
  ws.connect();
  resize();
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
