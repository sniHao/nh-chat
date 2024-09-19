<template>
  <div class="w-960 flex-center-center">
    <div class="chat-main w-100 flex" :class="newInfo ? 'new-info ' : ''">
      <div class="chat-com flex-down">
        <!-- 搜索 -->
        <div class="pd-12">
          <n-input-group>
            <n-input
              style="--n-border-hover: 1px solid #ff6700; --n-border-focus: 1px solid #ff6700; --n-box-shadow-focus: 0 0 0 2px rgba(255, 103, 0, 0.2)"
              :style="{ width: '100%' }"
              placeholder="输入Ta的邮箱"
              v-model:value="searchVal"
              clearable />
            <n-input-group-label class="hover-pointer" @click="goSearch" color="#ff6700">搜索</n-input-group-label>
          </n-input-group>
        </div>
        <!-- 通讯列表 -->
        <div class="over-auto w-100 user-list">
          <div class="pd-zy-12" v-for="i in 10" :key="i">
            <div class="user-box pd-sx-6 flex-center-zy">
              <div class="user-head flex-center-center">N</div>
              <div class="user-main">
                <div class="flex-center-zy">
                  <div class="ft-color-white ft-16 ft-b">昵称</div>
                  <div class="ft-13">7-5</div>
                </div>
                <div class="ft-13 ft-over">最后聊天内容最后聊天内容最后聊天内容最后聊天内容</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 聊天框 -->
      <div class="chat-body">12</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);

const searchVal = ref('');
const goSearch = () => {
  const emailPattern = /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/;
  if (!emailPattern.test(searchVal.value)) {
    notification['error']({
      content: '请输入正确的邮箱格式',
      duration: 2500,
      keepAliveOnHover: true
    });
    return;
  }
};
const newInfo = ref(false);
onMounted(() => {
  document.getElementsByClassName('user-list')[0].addEventListener('contextmenu', function (e) {
    e.preventDefault();
    console.log('右键菜单', e);
  });
  newInfo.value = true;
  setTimeout(() => {
    newInfo.value = false;
  }, 1500);
});
</script>

<style lang="scss" scoped>
:deep(.n-input__input-el) {
  color: $ft-color !important;
}

.n-input:not(.n-input--disabled).n-input--focus {
  background-color: rgb(255 255 255 / 4%);
}

.n-input:hover {
  box-shadow: unset !important;
  border: unset !important;
}

.n-input {
  border-radius: $px-6;
  background-color: rgb(255 255 255 / 12%);
}

.user-main {
  width: calc(100% - 3rem - 0.375rem);
  color: $ft-color-tips;
}

.user-head {
  width: $px-48;
  height: $px-48;
  border-radius: 50%;
  background-color: rgb(255 255 255 / 12%);
}

.user-box {
  border-bottom: $px-1 solid rgb(210 210 210 / 50%);
}

.chat-main {
  background-color: rgb(255 255 255 / 12%);
  box-shadow: 0 0 0 $px-1-5 $ft-color-tips;
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

.new-info {
  animation: new-info-animation 0.4s infinite;
}

@keyframes new-info-animation {
  0%,
  100% {
    box-shadow: 0 0 $px-4 $px-4 $ft-color;
  }
  50% {
    box-shadow: 0 0 0 0 $ft-color;
  }
}
</style>
