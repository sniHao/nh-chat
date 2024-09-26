<template>
  <div class="home-bg-color ft-color-white">
    <IndexNav></IndexNav>
    <div class="over-auto main-body">
      <NuxtPage />
    </div>
  </div>
</template>

<script setup lang="ts">
// const { $ws } = useNuxtApp();
import { useHomeStore } from './store/index';

onMounted(() => {
  // console.log($ws, '==');
  const ws = new WebSocket('ws://127.0.0.1:8087/socket.chat/' + useHomeStore().token);
  ws.onopen = () => {
    console.log('WebSocket 已连接');
  };
  ws.onmessage = (event) => {
    console.log('收到消息:', event.data);
    // 你可以在这里处理接收到的消息，比如通过 Vuex 存储状态或调用其他方法
  };
  ws.onerror = (error) => {
    console.error('WebSocket 发生错误:', error);
  };
  ws.onclose = () => {
    console.log('WebSocket 已关闭');
    // 此处可以添加重连逻辑
  };
  
});
</script>
<style lang="scss" scoped>
.main-body {
  height: 100vh;
}
</style>
