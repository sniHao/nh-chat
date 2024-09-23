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
        <div class="cb-body over-auto h-100">
          <div class="cbb-box">
            <!-- 时间 -->
            <div class="flex-center-center ft-13 ft-color-tips mb-8">
              <div class="cbb-tips">上午12：00</div>
            </div>
            <!-- 其他事件 -->
            <div class="flex-center-center ft-13 ft-color-tips mb-8">
              <div class="cbb-tips">对方撤回了一条消息</div>
            </div>
            <!-- 消息框 -->
            <div class="cbb-main flex">
              <div class="cbbm-box cbbm-box-left">1xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</div>
            </div>
            <div class="cbb-main flex-right">
              <div class="cbbm-box cbbm-box-right">12xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</div>
            </div>
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
const eqChat = () => {
  loding.value = true;
  setTimeout(() => {
    loding.value = false;
  }, 1000);
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
// 选择用户做出改变
watchEffect(() => {
  if (Object.keys(props.user).length === 0) return;
  eqChat();
  console.log('userInfo', props.user, Object.keys(props.user).length);
});
</script>

<style lang="scss" scoped>
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
  top: $px-14;
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
  top: $px-14;
}
.cbbm-box {
  min-width: $px-38;
  max-width: 50%;
  min-height: $px-48;
  padding: $px-12 $px-8;
  box-sizing: border-box;
  position: relative;
  border-radius: $px-8;
  word-wrap: break-word;
}

.cbb-main {
  min-height: $px-48;
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
}
</style>
