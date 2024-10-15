<template>
  <!-- 聊天框 -->
  <div class="chat-body flex-down" :class="isPhoneUnfold ? 'shrink-frame' : ''">
    <div class="chat-body-overlay" v-if="!isPhoneUnfold && isSmallWin"></div>
    <template v-if="Object.keys(props.user).length === 0">
      <div class="h-100 w-100 flex-center-center flex-down">
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
          <div class="flex-center-center ft-13 ft-color-tips mb-8" v-if="item.tab">
            <div class="cbb-tips">{{ cutChatTime(item.date) }}</div>
          </div>
          <!-- 其他事件 -->
          <div class="flex-center-center ft-13 ft-color-tips mb-8" v-if="item.sendState === 2 || item.receiveState === 2">
            <div class="cbb-tips">对方撤回了一条消息</div>
          </div>
          <!-- 消息框 -->
          <template v-else>
            <div class="cbb-main flex" v-if="item.sendUid === user.receiveUid">
              <div class="user-head flex-center-center mr-4" :style="'background-color:' + tranColor(user.photo)">{{ user.photo }}</div>
              <div class="cbbm-box cbbm-box-left flex">
                <span v-if="item.type === 0">{{ item.message }}</span>
                <n-image v-else class="chat-image" :src="item.message" />
              </div>
            </div>
            <div class="cbb-main flex-right" v-else>
              <div class="user-head flex-center-center ml-4" :style="'background-color:' + tranColor(userInfo.uInfo.photo)">{{ userInfo.uInfo.photo }}</div>
              <div class="cbbm-box cbbm-box-right flex">
                <span v-if="item.type === 0">{{ item.message }}</span>
                <n-image v-else class="chat-image" :src="item.message" />
              </div>
              <div class="flex-end mr-6 hover-pointer" v-if="!item.state" @click="reissue(item.message, item.type)">
                <OfSvg name="message-fail" fill="red" :width="24" :height="24"></OfSvg>
              </div>
            </div>
          </template>
        </div>
      </div>
      <div class="cb-input">
        <!-- 表情包图片等等 -->
        <div class="cb-input-controls flex-center">
          <n-popover trigger="click" raw @update:show="handleUpdateShow">
            <template #trigger>
              <OfSvg :width="24" :height="24" class="hover-pointer ml-12" name="emoji"></OfSvg>
            </template>
            <OfEmoJi @choose="chooseEmoji" />
          </n-popover>
          <n-upload :show-file-list="false" @before-upload="beforeUpload" accept=".png,.jpeg,.jpg">
            <OfSvg :width="24" :height="24" class="hover-pointer ml-12" name="up-image"></OfSvg>
          </n-upload>
        </div>
        <!-- 输入框 -->
        <div class="cb-input-main">
          <n-input
            ref="inputInstRef"
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
    <!-- 右键封装 -->
    <OfRightButton v-if="showRightBtnMessage" :left="rightBtnLeft" :top="rightBtnTop" :list="czList" @choose="chooseRight"></OfRightButton>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui';
const { notification } = createDiscreteApi(['notification']);
import type { UploadFileInfo } from 'naive-ui';
import { eqChat, sendMessage, sendMessageImage } from '~/api/index';
import WebSocketService from '@/utils/WebSocketService';
const webSocketService = inject<WebSocketService>('webSocketService');
const isSmallWin = inject<Ref<boolean>>('isSmallWin') || ref(false);
const userInfo = inject<Ref<any>>('userInfo');

const props = defineProps({
  user: {
    type: Object,
    default: {}
  },
  isPhoneUnfold: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['sendCallBack']);

// 消息与上一条消息时间标
const chatTabOne = (data: message) => {
  if (chatData.value.length === 0) data.tab = true;
  else data.tab = countTimeDiff(chatData.value[chatData.value.length - 1].date, data.date, 60) >= 10;
  return data;
};

// 消息列表是否需要时间标
const chatTab = (data: message[]) => {
  for (let i = data.length - 1; i >= 0; i--) {
    data[i].state = true;
    if (i === 0) {
      data[i].tab = true;
      break;
    }
    data[i].tab = countTimeDiff(data[i - 1].date, data[i].date, 60) >= 10;
  }
  return data;
};

// 展开关闭表情
const handleUpdateShow = (state: boolean) => {
  if (state) inputInstRef.value?.focus();
};

// 表情回调
const inputInstRef = ref<any>('');
const chooseEmoji = (val: string) => {
  const inputElement = inputInstRef.value?.$el.querySelector('textarea');
  if (inputElement) {
    const nowPonit = inputElement.selectionStart + val.length;
    sendVal.value = sendVal.value.slice(0, inputElement.selectionStart) + val + sendVal.value.slice(inputElement.selectionStart);
    setTimeout(() => {
      inputElement.focus();
      inputElement.setSelectionRange(nowPonit, nowPonit);
    });
  } else sendVal.value += val;
};

// 图片前置校验
const upLoadCheck = (file: File | null | undefined) => {
  if (!file) {
    tips('error', '文件异常👾');
    return false;
  }
  if ((file?.size ?? 0) / 1024 / 1024 > 4) {
    tips('error', '图片太大了吧，大小不能超过4M🤯');
    return false;
  }
  if (file?.type !== 'image/png' && file?.type !== 'image/jpeg') {
    tips('error', '只能发送png或jpeg格式的图片文件');
    return false;
  }
  return true;
};
// 图片
const beforeUpload = async (data: { file: UploadFileInfo; fileList: UploadFileInfo[] }) => {
  const file = data.file.file as File;
  const result = upLoadCheck(file);
  if (!result) return false;
  const reader = new FileReader();
  reader.onload = function (e: any) {
    const fd = new FormData();
    fd.append('file', file);
    sendMessageImage(fd, props.user.relationUid).then((res) => {
      if (res.code !== 200) res.data = randomNumber();
      pushDataOneCom(res.data, props.user.uid, props.user.receiveUid, 1, e.target.result, res.code === 200);
      emit('sendCallBack', { val: truncate('[图片]'), type: 1 });
      scrollToButtom();
    });
  };
  reader.readAsDataURL(file as any);
  return true;
};

// 接口请求数据
const loding = ref(true);
const chatData = ref([] as any);
const page = ref(1);
const next = ref(false);
const eqChatData = () => {
  eqChatCom();
};
// 数据公共
const eqChatCom = (needBootom: boolean = true) => {
  lodingNewMessage.value = true;
  eqChat(props.user.relationUid, page.value)
    .then((res: Result) => {
      let data = [] as message[];
      if (!res.code || res.code === 403) {
        data = eqChatDataStatic();
      } else {
        data = res.data.data;
        next.value = res.data.next;
        if (next.value) page.value++;
      }
      if (needBootom) chatData.value = chatTab(data);
      else {
        data = chatTab([...data, chatData.value[0]]);
        const oldHeight = document.getElementsByClassName('cb-body')[0].scrollHeight || 0;
        chatData.value.shift();
        chatData.value = [...chatTab(data), ...chatData.value];
        setTimeout(() => {
          let newDom = document.getElementsByClassName('cb-body')[0];
          newDom.scrollTop = newDom.scrollHeight - oldHeight;
        }, 10);
      }
    })
    .finally(() => {
      lodingNewMessage.value = false;
      if (!needBootom) return;
      setTimeout(() => {
        loding.value = false;
        scrollToButtom();
        addListener();
        listenerScrollToTop(true);
      }, 150);
    });
};

//未登录静态数据
const eqChatDataStatic = () => {
  let data = [];
  const initMessage = ['欢迎体验nh-chat👋。需要提醒的是，当前为体验模式，数据不会被保存', '你可以尝试向我发送消息哟，体验不同的功能。'];
  for (let i = 0; i < 2; i++) {
    data.push(addStaticDataCom(new Date(new Date().getTime() - 1000 * 60 * 50 + 1000 * 60 * 11 * i), initMessage[i], 0));
  }
  return data;
};

// 添加静态消息公共
const addStaticDataCom = (date: Date, message: string, type: number) => {
  return {
    date: getTimeFormat(date),
    id: -1,
    message: message,
    receiveState: 1,
    relationUid: -1,
    sendState: 1,
    sendUid: undefined,
    type: type,
    tab: false,
    state: true
  };
};

// 私信
const sendVal = ref('');
const inputMaxNumber = ref(2000);
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
  sendMessage({
    receiveUid: props.user.relationUid,
    message: sendVal.value,
    type: 0
  })
    .then((res) => {
      if (res.code !== 200) {
        res.data = randomNumber();
        if (firstMessage.value) simReissue(res.data);
      }
      pushDataOneCom(res.data, props.user.uid, props.user.receiveUid, 0, sendVal.value, res.code === 200);
      emit('sendCallBack', { val: truncate(sendVal.value), type: 0 });
      sendVal.value = '';
      scrollToButtom();
    })
    .finally(() => {
      firstMessage.value = false;
    });
};

// 模拟回复
const firstMessage = ref(true);
const simReissue = (id: number) => {
  setTimeout(() => {
    const message = '嘿嘿，我是一款好用、不夸张的聊天框架哟🥰';
    pushDataOneCom(id, props.user.receiveUid, props.user.uid, 0, message, true);
    emit('sendCallBack', { val: truncate(message), type: 0 });
    scrollToButtom();
  }, 2000);
};

// 推送单条消息
const pushDataOneCom = (id: number, sendUid: number, relationUid: number, type: number, message: string, state: boolean) => {
  chatData.value.push(
    chatTabOne({
      id: id,
      sendUid: sendUid,
      relationUid: relationUid,
      type: type,
      sendState: 1,
      receiveState: 1,
      date: getTimeFormat(new Date()),
      message: message,
      tab: false,
      state: state
    })
  );
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

// 发送失败重试
const reissue = async (message: string, type: number) => {
  if (type === 1) {
    const response = await fetch(message);
    const blob = await response.blob();
    const file = new File([blob], 'chat-image', { type: blob.type });
    console.log(file);
    beforeUpload({ file: { file: file } as UploadFileInfo, fileList: [] });
    return;
  }
  sendVal.value = message;
  sendInfo();
};

// 滚动条居底
const scrollToButtom = () => {
  setTimeout(() => {
    let scrollDom = document.getElementsByClassName('cb-body')[0];
    scrollDom.scrollTop = scrollDom.scrollHeight;
  }, 100);
};

// 上拉拉取消息
const lodingNewMessage = ref(false);
const scrollToTop = () => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  if (scrollDom.scrollTop < 30 && next.value && !lodingNewMessage.value) {
    eqChatCom(false);
  }
};
// 监听滚动条
const listenerScrollToTop = (state: boolean) => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  if (!scrollDom) return;
  const throttledScrollToTop = throttle(scrollToTop, 50);
  if (state) scrollDom.addEventListener('scroll', throttledScrollToTop);
  else scrollDom.removeEventListener('scroll', throttledScrollToTop);
};

// 初始化数据
const initData = () => {
  chatData.value = [];
  loding.value = true;
  page.value = 1;
  sendVal.value = '';
};

onMounted(() => {
  console.log(webSocketService, '===');
});

// 右键监听
const addListener = () => {
  const parentDiv = document.querySelector('.cb-body') as HTMLElement;
  parentDiv.addEventListener('contextmenu', listenerMessage);
};
// 右键事件
const rightBtnLeft = ref(0);
const rightBtnTop = ref(0);
const czList = ref();
const showRightBtnMessage = ref(false);
const nowCheckData = ref({} as Relation);
const listenerMessage = (e: MouseEvent) => {
  e.preventDefault();
  const parentDiv = document.querySelector('.cb-body');
  if (!parentDiv) return;
  let target = e.target as HTMLElement;
  let index = -1;
  if (target.classList.contains('cbbm-box')) {
    const allSonBoxes = Array.from(parentDiv.children);
    index = allSonBoxes.indexOf(eqFather(target));
  } else {
    while (target !== parentDiv) {
      if (target.classList.contains('cbbm-box')) {
        const allSonBoxes = Array.from(parentDiv.children);
        index = allSonBoxes.indexOf(eqFather(target));
      }
      target = target.parentElement as HTMLElement;
    }
  }
  if (index === -1) return;
  showRightBtnMessage.value = true;
  rightBtnLeft.value = e.x;
  rightBtnTop.value = e.y;
  nowCheckData.value = chatData.value[index];
  czList.value = [
    { id: 0, name: '删除聊天', incident: () => delMessageGo() },
    { id: 0, name: '撤回', incident: () => delMessageGo() },
    { id: 0, name: '复制', incident: () => delMessageGo() }
  ];
};
// 获取父级dom
const eqFather = (target: HTMLElement) => {
  while (!target.classList.contains('cbb-box')) {
    target = target.parentElement as HTMLElement;
  }
  return target;
};

// 选择右键内容回调
const chooseRight = (item: any) => {
  item.incident();
  showRightBtnMessage.value = false;
};

// 删除聊天
const delMessageGo = () => {
  showRightBtnMessage.value = false;
};

// 销毁监听
const clearListener = () => {
  const parentDiv = document.querySelector('.cb-body') as HTMLElement;
  parentDiv.removeEventListener('contextmenu', listenerMessage);
};

// 关闭右键
const closeRightBtn = () => {
  showRightBtnMessage.value = false;
};

// 关闭右键公共方法
const closeRightBtnCom = (state: boolean) => {
  const dom = document.getElementsByClassName('chat-main')[0];
  state ? dom.addEventListener('click', closeRightBtn) : dom.removeEventListener('click', closeRightBtn);
};

// 选择用户做出改变
const nowChatUid = inject<(uid: number) => void>('getNowChatUid');
watch(
  () => props.user,
  () => {
    if (Object.keys(props.user).length === 0) return;
    initData();
    eqChatData();
    if (nowChatUid) nowChatUid(props.user.id);
  }
);
onMounted(() => {
  closeRightBtnCom(true);
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
  listenerScrollToTop(false);
});
</script>

<style lang="scss" scoped>
.shrink-frame {
  width: calc(100% - $px-1) !important;
}
.message-loading {
  animation: circle 1s linear infinite;
}
:deep(.n-upload) {
  width: unset;
}
:deep(.n-upload-trigger) {
  display: flex;
}
:deep(.n-upload-trigger + .n-upload-file-list) {
  margin-top: unset;
}
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
  background-color: $ft-color-hui-1;
  margin-left: $px-10;
}
.cbbm-box-left::after {
  content: '';
  width: 0px;
  height: 0px;
  border: $px-10 solid;
  border-left: $px-10 solid transparent;
  border-right: $px-10 solid $ft-color-hui-1;
  border-bottom: $px-10 solid transparent;
  border-top: $px-10 solid transparent;
  position: absolute;
  left: -$px-16;
  top: $px-11;
}
.cbbm-box-right {
  background-color: $ft-color-2-hui-1;
  margin-right: $px-10;
}
.cbbm-box-right::after {
  content: '';
  width: 0px;
  height: 0px;
  border: $px-10 solid;
  border-left: $px-10 solid $ft-color-2-hui-1;
  border-right: $px-10 solid transparent;
  border-bottom: $px-10 solid transparent;
  border-top: $px-10 solid transparent;
  position: absolute;
  right: -$px-16;
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
  white-space: pre-wrap;
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
      color: white !important;
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
  overflow: hidden;
}
.chat-body-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgb(44 51 68);
  border-radius: $px-12;
  z-index: 2;
}
</style>
