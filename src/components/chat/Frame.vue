<template>
  <div class='chat-body flex-down' :class="isPhoneUnfold ? 'shrink-frame' : ''">
    <div class='chat-body-overlay' v-if='!isPhoneUnfold && isSmallWin'></div>
    <template v-if='Object.keys(props.user).length === 0'>
      <div class='h-100 w-100 flex-center-center flex-down'>
        <div class='mt-10 ft-16' :style='`color:${computedStyle.fontColorOpt}`'>选择好友，一起聊聊吧！</div>
      </div>
    </template>

    <template v-else-if='isPhoneUnfold || !isSmallWin'>
      <Loader class='loader-abs ml-2' :style='`background-color: ${param.style.mainColor}`'
              v-if='loadingMessage'></Loader>
      <div class='cb-head flex-center' :style='`box-shadow: 0 0 .4px .4px ${computedStyle.fontColorOpt}`'>
        <div class='w-100 flex-center ml-12 ft-over hover-pointer' @click="clickUser(user.uid)">
          <div class='mr-4 ft-13 ls-1' :class="user.wsState === 0 ? 'offline-ft' : 'online-ft'">
            [{{ user.wsState === 0 ? '离线' : '在线' }}]
          </div>
          <div>{{ user.name }}</div>
        </div>
        <n-popover trigger='click' placement='bottom'>
          <template #trigger>
            <div class='cb-head-controls flex-center-only mr-4 hover-pointer'>
              <Svg :width='28' :height='28' name='pointer' :fill='computedStyle.fontColorOpt'></Svg>
            </div>
          </template>
          <span class='hover-pointer' @click='closeChat'>关闭聊天</span>
        </n-popover>
      </div>
      <div class='cb-body over-auto h-100'>
        <div class='cb-body-sc' :style='`color:${computedStyle.fontColorOpt95}`'>
          <div class='flex-center-center ft-13 mt-8' v-if='chatData.length === 0'>
            <div class='cbb-tips' :style='`background-color: ${computedStyle.fontColorOpt35}`'>
              发起你们的第一句聊天吧，比如："你好"
            </div>
          </div>
          <div class='cbb-box' v-for='(item, index) in chatData' :key='index'>
            <!-- 时间 -->
            <div class='flex-center-center ft-13 mb-8' v-if='item.tab'>
              <div class='cbb-tips' :title='item.date' :style='`background-color: ${computedStyle.fontColorOpt35}`'>
                {{ cutChatTime(item.date) }}
              </div>
            </div>
            <!-- 其他事件 -->
            <div class='flex-center-center ft-13 mb-8'
                 v-if='item.sendState === 2 || item.receiveState === 2'>
              <div class='cbb-tips' :style='`background-color: ${computedStyle.fontColorOpt35}`'>
                {{ item.message }}
                <n-button v-if='reMessageId === item.id' text color='#FF6700' class='ml-4' size='tiny' @click='reEdit'>
                  重新编辑
                </n-button>
              </div>
            </div>
            <!-- 消息框 -->
            <template v-else>
              <template v-if='item.sendUid !== user.uid'>
                <div class='flex-down w-100' @click='moreCheckedCallBack(item)' :class='choiceStyle(item.check)'>
                  <div class='cbb-main flex'>
                    <n-checkbox :checked='item.check' @update.self:checked='moreCheckedCallBack(item)'
                                v-if='moreCheckState'></n-checkbox>
                    <div class='user-head flex-center-center mr-4 hover-pointer'
                         :style="'background-color:' + tranColor(user.photo)"
                         v-html='computePhoto(user.photo)' @click="clickUser(user.uid)"></div>
                    <div class='cbbm-box cbbm-box-left flex'
                         :style='`background-color: ${param.style.leftChatBgColor}`'>
                      <span v-if='item.type === 0'>{{ item.message }}</span>
                      <n-image v-else class='chat-image' :src='item.message'/>
                    </div>
                  </div>
                  <div class='w-100 flex' v-if='item.action === 2'>
                    <div class='quote quote-left pd-4 ft-over flex-center'
                         :style='`background-color: ${computedStyle.fontColorOpt35}`'
                         :title='item.quoteMessage'>
                      <template v-for='(ite, index) in getQuoteView(item.quoteMessage)' :key='index'>
                        <span v-if='index === 0'>{{ ite }}</span>
                        <span class="ft-over" v-if='item.quoteType === 0 && index === 1'
                              :title='ite'>{{ ite }}</span>
                        <n-image v-else-if='index === 1' class='quote-image' :src='ite'/>
                      </template>
                    </div>
                  </div>
                </div>
              </template>
              <template v-else>
                <div class='flex-down w-100' @click='moreCheckedCallBack(item)' :class='choiceStyle(item.check)'>
                  <div class='cbb-main pos-r'>
                    <n-checkbox class="pos-a" :checked='item.check'
                                @update.self:checked.stop='moreCheckedCallBack(item)'
                                v-if='moreCheckState'></n-checkbox>
                    <div class='flex-right'>
                      <div
                          class='user-head flex-center-center ml-4'
                          :style="'background-color:' + tranColor(param.userInfo.photo)"
                          v-html='computePhoto(param.userInfo.photo)'></div>
                      <div class='cbbm-box cbbm-box-right flex'
                           :style='`background-color: ${param.style.rightChatBgColor}`'>
                        <span v-if='item.type === 0'>{{ item.message }}</span>
                        <n-image v-else class='chat-image' :src='item.message'/>
                      </div>
                      <!-- 消息状态 -->
                      <div class='flex-end mr-6'>
                        <Svg
                            class='hover-pointer'
                            name='message-fail'
                            fill='red'
                            :width='24'
                            :height='24'
                            v-if='item.state === 2'
                            @click='reissue(item.message, item.type)'></Svg>
                        <Svg class='message-loading' name='message-loading' fill='#d2d2d2' :width='24' :height='24'
                             v-if='item.state === 0'></Svg>
                      </div>
                    </div>
                  </div>
                  <div class='w-100 flex-right' v-if='item.action === 2'>
                    <div class='quote quote-right pd-4  ft-over flex-center'
                         :style='`background-color: ${computedStyle.fontColorOpt35}`'
                         :title='item.quoteMessage'>
                      <template v-for='(ite, index) in getQuoteView(item.quoteMessage)' :key='index'>
                        <span v-if='index === 0'>{{ ite }}</span>
                        <span class="ft-over" v-if='item.quoteType === 0 && index === 1'
                              :title='ite'>{{ ite }}</span>
                        <n-image v-else-if='index === 1' class='quote-image' :src='ite'/>
                      </template>
                    </div>
                  </div>
                </div>
              </template>
            </template>
          </div>
        </div>
      </div>
      <FrameInput :isQuote='isQuote' :clearSendVal='clearSendVal' :moreCheckState='moreCheckState'
                  :saveChecked='saveChecked' :addSendVal='addSendVal' :user='user' :chatData='chatData'
                  @sendMessageEmit='sendMessageEmit' @moreCheckStateEmit='moreCheckStateEmit'
                  @delMessageEmit='delMessageEmit' @cancelMoreCheckedEmit='cancelMoreCheckedEmit'
                  @isActionEmit='isActionEmit' @isQuoteEmit='isQuoteEmit' @sendImageEmit='sendImageEmit'></FrameInput>
    </template>
    <FrameRightBtn :user='user' :needListener='needListener' :chatData='chatData'
                   :moreCheckState='moreCheckState' :userList="userList" @nowCheckDataEmit='nowCheckDataEmit'
                   @isActionEmit='isActionEmit' @isQuoteEmit='isQuoteEmit' @delMessageEmit='delMessageEmit'
                   @moreCheckStateEmit='moreCheckStateEmit' @sendMessageTransmitEmit='sendMessageTransmitEmit'
                   @revocationMessageEmit='revocationMessageEmit'></FrameRightBtn>
  </div>
</template>

<script setup lang='ts'>
import {createDiscreteApi} from "naive-ui";

const {notification} = createDiscreteApi(['notification']);
import {countTimeDiff, cutChatTime, getTimeFormat} from '@/utils/TimeUtil';
import {eqChatDataStatic} from '@/utils/staticUtils';
import {throttle} from '@/utils/domUtils';
import {tranColor, truncate, tips, computePhoto, randomNumber, getQuoteView} from '@/utils/OtherUtils';
import {eqChat, sendMessage, sendMessageImage, revocationMessage, delMessage} from '@/api/index';
import Svg from '../of/Svg.vue';
import Loader from '../of/Loader.vue';
import FrameInput from './FrameInput.vue';
import FrameRightBtn from './FrameRightBtn.vue';

const webSocketService = inject('webSocketService') as any;
const isSmallWin = inject<Ref<boolean>>('isSmallWin') || ref(false);
const param = inject<Ref<chatProps>>('param') as chatProps | any;
const computedStyle = inject<Ref<any>>('computedStyle') as any;

watchEffect(() => {
  document.documentElement.style.setProperty('--left-after', param.style.leftChatBgColor);
  document.documentElement.style.setProperty('--right-after', param.style.rightChatBgColor);
})

const props = defineProps({
  user: {
    type: Object,
    default: {}
  },
  isPhoneUnfold: {
    type: Boolean,
    default: false
  },
  userList: {
    type: Array,
    default: [] as Relation[]
  },
});
const emit = defineEmits(['sendCallBack', 'closeChat', 'clickUser']);

// 点击用户昵称和头像回调
const clickUser = (user: Relation) => {
  emit('clickUser', user);
};

// 删除消息
const delMessageEmit = (ids: number[]) => {
  delMessage(ids).then((res: Result) => {
    if (res.code !== 200) {
      if (!param.experienceMode) return tips('error', res.msg);
      tips('warning', '体验环境，并没有真正的删除哦');
    }
    chatData.value = chatData.value.filter((item: message) => !ids.includes(item.id));
    if (chatData.value.length === 0) emit('sendCallBack', {val: truncate('消息被删除'), type: 0});
    else {
      const lastData = chatData.value[chatData.value.length - 1];
      emit('sendCallBack', {
        val: truncate(lastData.type === 1 ? '[图片]' : lastData.message),
        type: lastData.type
      });
    }
  });
};

// 多选
const moreCheckStateEmit = (state: boolean) => {
  moreCheckState.value = state;
};

// 取消多选回调
const cancelMoreCheckedEmit = () => {
  saveChecked.value = [];
  chatData.value.forEach((item: message) => {
    item.check = false;
  });
};

// 多选的值回调
const nowCheckDataEmit = (data: message) => {
  moreCheckedCallBack(data, true);
};

// 撤回消息回调
const reMessage = ref('');
const reMessageId = ref(-1);
const revocationMessageEmit = (data: message) => {
  if (data.type === 0) {
    reMessage.value = data.message;
    reMessageId.value = data.id;
  }
  revocationMessage(data.id).then((res: Result) => {
    if (res.code !== 200) {
      if (!param.experienceMode) return tips('error', res.msg);
    }
    data.message = '你撤回了一条消息';
    data.sendState = 2;
    emit('sendCallBack', {val: truncate('你撤回了一条消息'), type: 0});
  });
};
// ===================================其他功能===================================//

const choiceStyle = (choice: boolean) => {
  return {
    'is-choice': choice && moreCheckState.value,
    'is-checked': moreCheckState.value
  };
};
// 关闭聊天
const closeChat = () => {
  if (nowChatUid) nowChatUid(-99);
  emit('closeChat');
};

// 消息与上一条消息时间标
const chatTabOne = (data: message) => {
  if (chatData.value.length === 0) data.tab = true;
  else data.tab = countTimeDiff(chatData.value[chatData.value.length - 1].date, data.date, 60) >= 10;
  return data;
};

// 消息列表是否需要时间标
const chatTab = (data: message[]) => {
  for (let i = data.length - 1; i >= 0; i--) {
    data[i].state = 1;
    data[i].check = false;
    if (i === 0) {
      data[i].tab = true;
      break;
    }
    data[i].tab = countTimeDiff(data[i - 1].date, data[i].date, 60) >= 10;
  }
  return data;
};

// 滚动条居底
const scrollToBottom = () => {
  setTimeout(() => {
    let scrollDom = document.getElementsByClassName('cb-body')[0];
    scrollDom.scrollTop = scrollDom.scrollHeight;
  }, 100);
};

// 监听滚动条
const listenerScrollToTop = (state: boolean) => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  if (!scrollDom) return;
  const throttledScrollToTop = throttle(scrollToTop, 50);
  if (state) scrollDom.addEventListener('scroll', throttledScrollToTop);
  else scrollDom.removeEventListener('scroll', throttledScrollToTop);
};

// ===================================发送消息===================================//
const isActionEmit = (data: number) => {
  isAction.value = data;
};
const isQuoteEmit = (data: {
  id: number,
  type: number,
  message: string
}) => {
  isQuote.value = data;
};

// 发送消息前置处理
const sendInfoPre = (sendVal: string): boolean => {
  if (sendVal.length === 0) return false;
  if (sendVal.split('\n').length > 20) {
    notification['error']({
      content: '超出长度20行的限制',
      duration: 2500,
      keepAliveOnHover: true
    });
    return false;
  }
  return true;
};
// 发送图片前置校验
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

// 发送消息
const isQuote = ref({
  id: 0,
  type: 0,
  message: ''
});
const isAction = ref(0);
const clearSendVal = ref(false);
const sendInfo = (message: string, relationUid: number, action: number) => {
  if (!sendInfoPre(message)) return;
  const quoteObj = {
    action: action,
    quoteId: isQuote.value.id,
    quoteType: isQuote.value.type,
    quoteMessage: isQuote.value.message
  };
  let pointer = -99;
  if (relationUid === props.user.relationUid) pointer = pushDataOneCom(-88, props.user.uid, relationUid, 0, message, 0, quoteObj);
  emit('sendCallBack', {val: truncate(message), type: 0});
  clearSendVal.value = true;
  scrollToBottom();
  sendMessage({
    receiveUid: relationUid,
    message: message,
    type: 0,
    action: action,
    quoteId: isQuote.value.id
  })
      .then((res: Result) => {
        if (res.code !== 200) {
          res.data = randomNumber();
          if (param.experienceMode && firstMessage.value) simReissue(3, message);
        }
        if (pointer !== -99) {
          chatData.value[pointer].id = res.data;
          chatData.value[pointer].state = res.code === 200 ? 1 : 2;
        }
      })
      .finally(() => {
        firstMessage.value = false;
        clearQuote();
      });
};

// 发送消息回调
const sendMessageEmit = (data: string) => {
  sendInfo(data, props.user.relationUid, isAction.value);
};
// 发送消息回调-转发
const sendMessageTransmitEmit = (data: { type: number, message: string, uid: number }) => {
  if (data.type === 1) {
    getImage(data.message).then((res: any) => beforeUpload(res, data.uid, 1))
  } else sendInfo(data.message, data.uid, 1);
};

// 推送单条消息
const pushDataOneCom = (id: number, sendUid: number, receiveUid: number, type: number, message: string, state: number, quoteObj: messageQuote, sendState: number = 1): number => {
  chatData.value.push(
      chatTabOne({
        id: id,
        sendUid: sendUid,
        receiveUid: receiveUid,
        type: type,
        sendState: sendState,
        receiveState: 1,
        date: getTimeFormat(new Date()),
        message: message,
        tab: false,
        state: state,
        check: false,
        action: quoteObj.action,
        quoteId: quoteObj.quoteId,
        quoteType: quoteObj.quoteType,
        quoteMessage: quoteObj.quoteMessage
      })
  );
  return chatData.value.length - 1;
};

// 通过url获取图片文件
const getImage = (url: string) => {
  return new Promise(async (resolve) => {
    const response = await fetch(url);
    const blob = await response.blob();
    const file = new File([blob], 'chat-image.png', {type: blob.type});
    resolve(file);
  })
};

// 发送失败重试
const reissue = async (message: string, type: number) => {
  if (type === 1) {
    getImage(message).then((res: any) => beforeUpload(res, props.user.relationUid, isAction.value))
    return;
  }
  sendInfo(message, props.user.relationUid, isAction.value);
};

// 发送图片回调
const sendImageEmit = (file: File) => {
  beforeUpload(file, props.user.relationUid, isAction.value);
};

// 发送图片
const beforeUpload = async (file: File, relationUid: number, action: number): Promise<boolean> => {
  const result = upLoadCheck(file);
  if (!result) return false;
  const reader = new FileReader();
  reader.onload = function (e: any) {
    const fd = new FormData();
    fd.append('file', file);
    fd.append('action', action + '');
    fd.append('quoteId', isQuote.value.id + '');
    fd.append('receiveUid', relationUid + '');
    const quoteObj = {
      action: isAction.value,
      quoteId: isQuote.value.id,
      quoteType: isQuote.value.type,
      quoteMessage: isQuote.value.message
    };
    let pointer = -99;
    if (relationUid === props.user.relationUid) pointer = pushDataOneCom(-88, props.user.uid, props.user.receiveUid, 1, e.target.result, 0, quoteObj);
    emit('sendCallBack', {val: truncate('[图片]'), type: 1});
    scrollToBottom();
    clearQuote();
    sendMessageImage(fd).then((res) => {
      if (res.code !== 200) res.data = randomNumber();
      if (pointer !== -99) {
        chatData.value[pointer].id = res.data;
        chatData.value[pointer].state = res.code === 200 ? 1 : 2;
      }
    });
  };
  reader.readAsDataURL(file as any);
  return true;
};

// 清除动作
const clearQuote = () => {
  isAction.value = 0;
  isQuote.value = {id: 0, type: 0, message: ''};
};

// ===================================静态数据【用于体验时】===================================//
// 模拟回复
const firstMessage = ref(true);
const simReissue = (id: number, repMessage: string) => {
  setTimeout(() => {
    const message = '嘿嘿，我是一款好用、不夸张的聊天框架哟🥰';
    const quoteObj = {
      action: 2,
      quoteId: id,
      quoteType: 0,
      quoteMessage: '我：' + repMessage
    };
    pushDataOneCom(id, props.user.relationUid, props.user.uid, 0, message, 1, quoteObj);
    emit('sendCallBack', {val: truncate(message), type: 0, uid: props.user.relationUid});
    scrollToBottom();
  }, 2000);
};

// ===================================获取聊天数据===================================//
// 获取聊天数据
const chatData = ref([] as any);
provide('chatData', chatData.value);
const page = ref(1);
const next = ref(false);
const eqChatData = () => {
  eqChatCom();
};

// 数据公共处理
const eqChatCom = (needBottom: boolean = true) => {
  loadingMessage.value = true;
  eqChat(props.user.relationUid, page.value)
      .then((res: Result) => {
        let data = [] as message[];
        if (res.code !== 200) {
          if (!param.experienceMode) return tips('error', res.msg);
          data = eqChatDataStatic();
        } else {
          data = res.data.data;
          next.value = res.data.next;
          if (next.value) page.value++;
        }
        if (needBottom) chatData.value = chatTab(data);
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
        needListener.value = false;
        setTimeout(() => {
          needListener.value = true;
          if (!needBottom) return loadingMessage.value = false;
          scrollToBottom();
          setTimeout(() => {
            loadingMessage.value = false;
          }, 100)
          listenerScrollToTop(true);
        }, 150);
      });
};

// 上拉拉取消息
const loadingMessage = ref(false);
const scrollToTop = () => {
  let scrollDom = document.getElementsByClassName('cb-body')[0];
  if (scrollDom.scrollTop < 30 && next.value && !loadingMessage.value) {
    eqChatCom(false);
  }
};

// ===================================右键操作===================================//
const needListener = ref(false);
// 重新编辑
const addSendVal = ref('');
const reEdit = () => {
  addSendVal.value = reMessage.value;
};

// 多选操作
const moreCheckState = ref(false);
const saveChecked = ref([] as number[]);
const moreCheckedCallBack = (data: message, needFilter?: boolean) => {
  needFilter = needFilter || false;
  if (!data.check) saveChecked.value.push(data.id);
  else saveChecked.value.splice(saveChecked.value.indexOf(data.id), 1);
  if (!needFilter) {
    data.check = !data.check;
    return;
  }
  chatData.value.filter((item: message) => {
    if (item.id === data.id) {
      item.check = !data.check;
      return true;
    }
  });
};
// ===================================组件初始化操作===================================//
// 初始化数据
const initData = () => {
  isQuote.value = {id: 0, type: 0, message: ''};
  clearSendVal.value = false;
  chatData.value = [];
  loadingMessage.value = false;
  page.value = 1;
  moreCheckState.value = false;
  saveChecked.value = [];
  setTimeout(() => {
    clearSendVal.value = true;
  }, 10);
};

// 选择用户做出改变
const nowChatUid = inject<(uid: number) => void>('getNowChatUid');
watch(
    () => props.user,
    () => {
      if (Object.keys(props.user).length === 0) return;
      initData();
      eqChatData();
      nextTick(() => {
        listenerHover();
      });
      if (nowChatUid) nowChatUid(props.user.relationUid);
    }
);

const ws = ref(webSocketService);
watch(
    () => ws.value?.pushCount,
    () => {
      const data = ws.value.newMessage;
      if (Object.keys(props.user).length === 0 || props.user.relationUid !== data.receiveUid) return;
      emit('sendCallBack', {val: truncate(data.message), type: data.type});
      if (data.state === 2) {
        let newData = chatData.value.filter((item: message) => item.id === data.mid)[0];
        newData.sendState = 2;
        newData.message = '对方撤回了一条消息';
      } else {
        const quoteObj = {
          action: 0,
          quoteId: 0,
          quoteType: 0,
          quoteMessage: ''
        };
        pushDataOneCom(data.mid, data.receiveUid, props.user.uid, data.type, data.message, 1, quoteObj, data.state);
        scrollToBottom();
      }
    }
);

// 监听悬浮是否存在滚动条
const listenerHover = () => {
  const dom = document.getElementsByClassName('cb-body')[0];
  dom.addEventListener('mouseenter', () => {
    const hasScrollBar = dom.scrollHeight > dom.clientHeight;
    if (hasScrollBar) dom.classList.add('cb-body-up');
  });
  dom.addEventListener('mouseleave', () => {
    dom.classList.remove('cb-body-up');
  });
};

// 组件初开始和结束
onBeforeUnmount(() => {
  listenerScrollToTop(false);
  if (nowChatUid) nowChatUid(-99);
});

</script>

<style lang='scss' scoped>
.quote-image {
  max-height: $px-24;
  max-width: $px-24;
}

:deep(.quote-image img) {
  width: 100% !important;
  height: 100% !important;
  object-fit: contain !important;
}

.quote-left {
  margin-left: $px-56;
}

.quote-right {
  margin-right: $px-56;
}

.quote {
  border-radius: $px-6;
  max-width: 50%;
}

.is-choice {
  background-color: #4a505d;
}

.is-checked:hover {
  background-color: #4a505d;
  cursor: pointer;
}

:deep(.n-checkbox-box-wrapper) {
  margin-top: $px-14;
  margin-right: $px-10;
}

.shrink-frame {
  width: calc(100% - $px-1) !important;
}

.message-loading {
  animation: circle 1s linear infinite;
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

.cbbm-box-left {
  margin-left: $px-10;
}

:root {
  --left-after: $ft-color-hui-1;
  --right-after: $ft-color-2-hui-1;
}

.cbbm-box-left::after {
  content: '';
  width: 0;
  height: 0;
  border: $px-10 solid;
  border-left: $px-10 solid transparent;
  border-right: $px-10 solid var(--left-after);
  border-bottom: $px-10 solid transparent;
  border-top: $px-10 solid transparent;
  position: absolute;
  left: -$px-16;
  top: $px-11;
}

.cbbm-box-right {
  margin-right: $px-10;
}

.cbbm-box-right::after {
  content: '';
  width: 0;
  height: 0;
  border: $px-10 solid;
  border-left: $px-10 solid var(--right-after);
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

  span {
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: anywhere;
    white-space: pre-wrap;
  }
}

.cbb-main {
  width: 100%;
  min-height: $px-42;
  margin-top: $px-6;
  margin-bottom: $px-6;
}

.cbb-tips {
  border-radius: $px-4;
  padding: $px-2 $px-6;
  box-sizing: border-box;
}

.cbb-box {
  padding: $px-8 $px-12;
}


.cb-body {
  height: calc(100% - $px-2 - $px-48 - $px-160);
}

.cb-body:hover::-webkit-scrollbar {
  width: $px-4;
}

.cb-body::-webkit-scrollbar {
  width: 0;
}

.cb-body-sc {
  padding-right: $px-4;
}

.cb-body-up:hover .cb-body-sc {
  padding-right: 0;
}

.cb-head {
  z-index: 6;
  height: $px-48;

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
  left: $px-1;
  width: 100%;
  height: 100%;
  background-color: rgb(44 51 68);
  border-radius: $px-12;
  z-index: 2;
}
</style>
