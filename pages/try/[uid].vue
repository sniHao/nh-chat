<template>
  <div class="body w-100 h-100 flex-center-onely">
    <ChatMain></ChatMain>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router';
const route = useRoute();
console.log(route.params.uid, '用户id是？');
</script>

<style lang="scss" scoped>
.body {
  padding: $px-96 $px-32;
  box-sizing: border-box;
}
</style>
