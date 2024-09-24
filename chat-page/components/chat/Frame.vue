<template>
  <!-- 聊天框 -->
  <div class="chat-body flex-down">
    <template v-if="Object.keys(props.user).length === 0">
      <div class="h-100 flex-center-center flex-down">
        <div class="ft-color-tips mt-10 ft-16">选择好友，一起聊聊吧！</div>
      </div>
    </template>
    <template v-else>
      <OfLoader class="loader-abs" v-if="loding"></OfLoader>
      <OfLoader class="loader-abs" style="background-color: #2c334485" v-if="lodingNewMessage"></OfLoader>
      <div class="cb-head flex-center">
        <div class="cb-head-controls"></div>
        <div class="w-100 flex-center-center">{{ user.name }}</div>
        <div class="cb-head-controls flex-center-onely mr-4 hover-pointer">
          <OfSvg :width="28" :height="28" name="pointer"></OfSvg>
        </div>
      </div>
      <div class="cb-body over-auto h-100">
        <div class="cbb-box" v-for="(item, index) in chatData" :key="index">
          <!-- 时间 -->
          <div class="flex-center-center ft-13 ft-color-tips mb-8">
            <div class="cbb-tips">{{ item.time }}</div>
          </div>
          <!-- 其他事件 -->
          <div class="flex-center-center ft-13 ft-color-tips mb-8" v-if="item.messageType === 2">
            <div class="cbb-tips">对方撤回了一条消息</div>
          </div>
          <!-- 消息框 -->
          <template v-else>
            <div class="cbb-main flex" v-if="item.id == 0">
              <div class="user-head flex-center-center mr-4">N</div>
              <div class="cbbm-box cbbm-box-left flex">
                <span v-if="item.messageType !== 2 && item.messageType !== 8">{{ item.message }}</span>
                <n-image v-else class="chat-image" src="https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg" />
              </div>
            </div>
            <div class="cbb-main flex-right" v-else>
              <div class="user-head flex-center-center ml-4">N</div>
              <div class="cbbm-box cbbm-box-right flex">
                <span v-if="item.messageType !== 3 && item.messageType !== 11">{{ item.message }}</span>
                <n-image v-else class="chat-image" src="https://07akioni.oss-cn-beijing.aliyuncs.com/07akioni.jpeg" />
              </div>
            </div>
          </template>
        </div>
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

// 接口请求数据
const loding = ref(true);
const chatData = ref([] as any);
const eqChat = (uid: number) => {
  for (let i = 0; i < uid + 2; i++) {
    chatData.value.push({
      id: i % 2,
      messageType: i,
      messageState: i,
      message: '嘻嘻嘻嘻嘻嘻🤖' + i,
      time: '12:00',
      timeState: i % 2
    });
  }
  setTimeout(() => {
    loding.value = false;
    scrollToButtom();
    listenerScrollToTop(true);
  }, 150);
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
  chatData.value.push({
    id: 999,
    messageType: 0,
    messageState: 0,
    message: sendVal.value,
    time: '12:00',
    timeState: 2
  });
  sendVal.value = '';
  setTimeout(() => {
    scrollToButtom();
  }, 10);
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

// 滚动条居底
const scrollToButtom = () => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  scrollDom.scrollTop = scrollDom.scrollHeight;
};

// 监听上拉拉取消息
const hasNewMessage = ref(true);
const lodingNewMessage = ref(false);

const scrollToTop = () => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  if (scrollDom.scrollTop < 30 && hasNewMessage.value && !lodingNewMessage.value) {
    lodingNewMessage.value = true;
    setTimeout(() => {
      console.log('请求新数据');
      chatData.value = [
        {
          id: 999,
          messageType: 0,
          messageState: 0,
          message: '信心写信心写',
          time: '12:00',
          timeState: 2
        },
        ...chatData.value
      ];
      hasNewMessage.value = false;
      lodingNewMessage.value = false;
    }, 1000);
  }
};
// 监听滚动条
const listenerScrollToTop = (state: boolean) => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  const throttledScrollToTop = throttle(scrollToTop, 50);
  if (state) scrollDom.addEventListener('scroll', throttledScrollToTop);
  else scrollDom.removeEventListener('scroll', throttledScrollToTop);
};

// 初始化数据
const initData = () => {
  chatData.value = [];
  loding.value = true;
  hasNewMessage.value = true;
};

onMounted(() => {});
// 选择用户做出改变
watch(
  () => props.user,
  () => {
    initData();
    eqChat(props.user.id);
    console.log('userInfo', props.user, Object.keys(props.user).length);
  }
);
onBeforeUnmount(() => {
  listenerScrollToTop(false);
});
</script>

<style lang="scss" scoped>
.loader-abs {
  position: absolute;
  background-color: #2c3344;
  z-index: 6;
  top: 0;
  right: 0;
  left: 0;
  border-radius: $px-12;
}
:deep(.chat-image img) {
  max-width: 100%;
  max-height: $px-320;
}
.user-head {
  border-radius: $px-6;
}
.cbbm-box-left {
  background-color: $ft-color-op-8;
  margin-left: $px-12;
}
.cbbm-box-left::after {
  content: '';
  width: 0px;
  height: 0px;
  border: $px-10 solid;
  border-left: $px-10 solid transparent;
  border-right: $px-10 solid $ft-color-op-8;
  border-bottom: $px-10 solid transparent;
  border-top: $px-10 solid transparent;
  position: absolute;
  left: -$px-20;
  top: $px-11;
}
.cbbm-box-right {
  background-color: $ft-color-2-op-8;
  margin-right: $px-12;
}
.cbbm-box-right::after {
  content: '';
  width: 0px;
  height: 0px;
  border: $px-10 solid;
  border-left: $px-10 solid $ft-color-2-op-8;
  border-right: $px-10 solid transparent;
  border-bottom: $px-10 solid transparent;
  border-top: $px-10 solid transparent;
  position: absolute;
  right: -$px-20;
  top: $px-11;
}
.cbbm-box {
  min-width: $px-38;
  max-width: calc(50% - $px-48);
  min-height: $px-42;
  padding: $px-9 $px-8;
  box-sizing: border-box;
  position: relative;
  border-radius: $px-8;
  word-wrap: break-word;
  word-break: break-all;
  overflow-wrap: break-word;
}

.cbb-main {
  min-height: $px-42;
  margin-bottom: $px-12;
}
.cbb-tips {
  border-radius: $px-4;
  background-color: #8fa2c366;
  padding: $px-2 $px-6;
  box-sizing: border-box;
}
.cbb-box {
  padding: $px-8 $px-12;
}
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
.cb-body:hover::-webkit-scrollbar {
  width: $px-4;
}
.cb-body::-webkit-scrollbar {
  width: 0px;
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
  position: relative;
}
</style>
