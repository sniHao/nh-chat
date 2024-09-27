<template>
  <div class="home-bg-color ft-color-white">
    <IndexNav></IndexNav>
    <div class="over-auto main-body">
      <NuxtPage />
    </div>
  </div>
</template>

<script setup lang="ts">
import WebSocketService from './utils/WebSocketService';
import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);
const router = useRouter();
const store = useStore();
// ws状态全局
const ws = new WebSocketService('ws://127.0.0.1:8087/socket.chat/' + store.token);
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

onMounted(() => {
  ws.connect();
});
onUnmounted(() => {
  ws.close();
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
