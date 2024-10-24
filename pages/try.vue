<template>
  <div class="body w-100 h-100 flex-center-onely">
    <ChatMain></ChatMain>
  </div>
</template>

<script setup lang="ts">
import { notes } from '~/api/index';
onMounted(() => {
  notes();
});
</script>

<style lang="scss" scoped>
.body {
  padding: $px-96 $px-32;
  box-sizing: border-box;
}
</style>
