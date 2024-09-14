<template>
  <div class="w-960 flex-center-center">
    <div class="chat-main w-100 flex">
      <div class="chat-com">
        <n-input :style="{ width: '50%' }" />
      <n-button type="primary" ghost>
        搜索
      </n-button>
      </div>
      <div class="chat-body">12</div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

<style lang="scss" scoped>
.chat-main {
  background-color: #630ea1;
  box-shadow: 0 0 0 0.4px $ft-color;
  height: $px-560;
  border-radius: $px-12;
}
.chat-com {
  width: $px-280;
  border-right: $px-1 solid $ft-color-tips;
}
.chat-body {
  width: calc(100% - $px-280 - $px-1);
}
</style>
