<template>
  <!-- 聊天框 -->
  <div class="chat-body flex-down">
    <template v-if="Object.keys(props.user).length === 0">
        <div class="h-100 flex-center-center flex-down">
            <div class="ft-color-tips mt-10 ft-16">选择好友，一起聊聊吧！</div>
        </div>
    </template>
    <template v-else>
      <OfLoader v-if="loding"></OfLoader>
      <template v-else>
        <div class="cb-head flex-center">
          <div class="cb-head-controls"></div>
          <div class="w-100 flex-center-center">{{ user.name }}</div>
          <div class="cb-head-controls flex-center-onely mr-4 hover-pointer">
            <OfSvg :width="28" :height="28" name="pointer"></OfSvg>
          </div>
        </div>
        <div class="cb-body h-100">
          <div></div>
          <div></div>
          <div class="cb-body-controls">x</div>
        </div>
        <div class="cb-input">
          <!-- 表情包图片等等 -->
          <div class="cb-input-controls flex-center">
            <OfSvg :width="24" :height="24" class="hover-pointer ml-12" name="emoji"></OfSvg>
            <OfSvg :width="24" :height="24" class="hover-pointer ml-12" name="up-image"></OfSvg>
          </div>
          <!-- 输入框 -->
          <div class="cb-input-main">
            <n-input
              style="--n-border: unset; --n-border-hover: unset; --n-border-focus: unset; --n-box-shadow-focus: unset"
              v-model:value="sendVal"
              type="textarea"
              @keydown.enter.native="handleKeyUp"
              @input="valChange"
              placeholder="说点啥..." />
          </div>
          <div class="cb-input-go flex-center-zy pd-zy-6 ft-color-tips">
            <div>{{ sendVal.length }} / {{ inputMaxNumber }}</div>
            <div class="hover-pointer" @click="sendInfo">按Enter键发送</div>
          </div>
        </div>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);

const props = defineProps({
  user: {
    type: Object,
    default: {}
  }
});
// 选择用户做出改变
watchEffect(() => {
  if (Object.keys(props.user).length === 0) return;
  eqChat();
  console.log('userInfo', props.user, Object.keys(props.user).length);
});

// 接口请求数据
const loding = ref(true);
const eqChat = () => {
  setTimeout(() => {
    loding.value = false;
    console.log(0.0);
  }, 2000);
};

// 私信
const sendVal = ref('');
const inputMaxNumber = ref(950);
// Enter事件
const handleKeyUp = (e: KeyboardEvent) => {
  if (e.key !== 'Enter') return;
  if (e.shiftKey) return;
  if (e.altKey) return (sendVal.value += '\n');
  e.preventDefault();
  sendInfo();
};
// 输入框字数限制
const valChange = () => {
  if (sendVal.value.length > inputMaxNumber.value) return (sendVal.value = sendVal.value.slice(0, inputMaxNumber.value));
};
// 发送消息
const sendInfo = () => {
  if (!sendInfoPre()) return;
  console.log('发送？', sendVal.value);
  sendVal.value = '';
};
// 发送消息前置处理
const sendInfoPre = () => {
  if (sendVal.value.length === 0) return false;
  if (sendVal.value.split('\n').length > 20) {
    notification['error']({
      content: '超出长度20行的限制',
      duration: 2500,
      keepAliveOnHover: true
    });
    return false;
  }
  return true;
};

onMounted(() => {});
</script>

<style lang="scss" scoped>
.cb-input {
  height: $px-160;
  border-top: $px-1 solid $ft-color-tips;
  .cb-input-main {
    height: calc(100% - $px-32 - $px-32);
    background-color: #2c3344;
    .n-input {
      height: 100%;
      background-color: unset;
    }
    .n-input:not(.n-input--disabled).n-input--focus {
      background-color: unset;
    }
    :deep(.n-input__textarea-el) {
      color: $ft-color !important;
    }
    :deep(.n-input-wrapper) {
      resize: unset;
    }
    :deep(.n-scrollbar-rail__scrollbar) {
      --n-scrollbar-color: rgba(255, 103, 0, 0.5);
      --n-scrollbar-color-hover: rgba(255, 103, 0);
    }
  }
  .cb-input-go,
  .cb-input-controls {
    height: $px-32;
  }
}

.cb-body {
  height: calc(100% - $px-2 - $px-48 - $px-160);
}

.cb-head {
  height: $px-48;
  border-bottom: $px-1 solid $ft-color-tips;
  .cb-head-controls {
    width: $px-32;
  }
}

.chat-body {
  width: calc(100% - $px-280 - $px-1);
}
</style>
