<template>
  <div class='chat-main flex'
       :style='`width:${param.width};max-width:60rem;height:${param.height};
       background-color:${param.style.mainColor};box-shadow: 0 0 0 1.5px ${computedStyle.fontColorOpt};
       color:${param.style.fontColor}`'
       :class='mainClass()'>
    <div class='chat-com flex-down' :class="isPhoneUnfold ? 'shrink' : ''"
         :style='`box-shadow:0.8px 0 0.8px ${computedStyle.fontColorOpt}`'>
      <!-- 搜索 -->
      <div class='pd-12' :class="isPhoneUnfold ? 'shrink' : ''">
        <n-input-group>
          <n-input
              :style='`--n-border:1px solid ${computedStyle.fontColorOpt};--n-border-hover: 1px solid ${computedStyle.fontColorOpt};
              --n-border-focus: 1px solid ${computedStyle.fontColorOpt};
              --n-placeholder-color:${computedStyle.fontColorOpt};
              --n-text-color:${param.style.fontColor}`'
              :placeholder='param.inputTips'
              v-model:value='searchVal'
              clearable/>
          <n-input-group-label class='hover-pointer' @click='goSearch'
                               :style='`--n-group-label-text-color:${param.style.fontColor};
                               --n-group-label-border:1px solid ${param.style.fontColor};
                               --n-group-label-color:${param.style.mainColor}`'>搜 索
          </n-input-group-label>
        </n-input-group>
      </div>
      <!-- 通讯列表 -->
      <div class='over-auto w-100 h-100 user-list'>
        <template v-if='userList.length === 0'>
          <div class='flex-down-center h-100 flex-center-only'>
            <NullChat></NullChat>
            <div :style='`color:${computedStyle.fontColorOpt}`'>聊天通讯空空的</div>
          </div>
        </template>
        <div v-else class='user-list-sc'>
          <div class='pd-zy-12 hover-pointer user' :class='userClass(item)' v-for='item in userList'
               :key='item.id' @click='showChat(item)'>
            <div class='user-box pd-sx-6 flex-center-zy'
                 :style='`border-bottom: 1px solid ${computedStyle.fontColorOpt35}`'>
              <n-badge :value='item.notRead' :max='99' :offset='[-5, 5]'>
                <div class='user-head flex-center-center' :style="'background-color:' + tranColor(item.photo)"
                     v-html='computePhoto(item.photo)'></div>
              </n-badge>
              <div class='user-main' :style="`color:${param.style.fontColor}`">
                <div class='flex-center-zy'>
                  <div class="ft-16 ft-b w-auto" :title="item.name">
                    {{ item.name }}
                  </div>
                  <div v-if="item.top === 1" class="ml-8 ft-13 pd-zy-6" style="white-space: nowrap;border-radius: 10px"
                       :style="`border:2px solid ${param.style.leftChatBgColor};color:${param.style.leftChatBgColor}`">
                    置顶
                  </div>
                </div>
                <div class="ft-13 flex-center mt-4">
                  <div class="ft-over w-auto" :title="item.lastMessage">
                    {{ item.lastMessage }}
                  </div>
                  <div class="ml-8" :title='item.lastMessageDate' style="white-space: nowrap;">
                    {{ reckonTime(item.lastMessageDate) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- phone 通过nav全局引入-->
      <div class='min-icon' v-if='isSmallWin'>
        <Svg :width='32' :height='32' name='close' fill='#ff6700' viewBox='0 0 1139 1024' v-if='!isPhoneUnfold'
             @click='isPhoneCallBack(isPhoneUnfold)'></Svg>
        <Svg :width='32' :height='32' name='open' fill='#ff6700' viewBox='0 0 1139 1024' v-else
             @click='isPhoneCallBack(isPhoneUnfold)'></Svg>
      </div>
    </div>
    <!-- 聊天框 -->
    <Frame :user='nowUser' :userList="userList" :isPhoneUnfold='isPhoneUnfold' @sendCallBack='sendCallBack'
           @closeChat='closeChat' @clickUser="clickUser"></Frame>
  </div>
  <!-- 弹框 -->
  <n-modal v-model:show='showModal'>
    <n-card style='width: 600px; max-height: 380px' :bordered='false' size='huge' role='dialog' aria-modal='true'
            class='over-auto'>
      <template v-if='param.searchUserResult.length === 0'>
        <div class='flex-down-center'>
          <Svg :width='64' :height='64' name='no-user' viewBox='0 0 1139 1024'></Svg>
          <span class='ft-16 ft-color-tips mt-12'>未找到该用户</span>
        </div>
      </template>
      <div class='flex-center-zy mb-18' v-else v-for='(item, index) in param.searchUserResult' :key='index'>
        <div class='flex-center'>
          <div class='user-head flex-center-center' :style="'background-color:' + tranColor(item.photo)"
               v-html='computePhoto(item.photo)'></div>
          <div class='ft-16 ft-b ml-10'>{{ item.name }}</div>
        </div>
        <n-button round strong type='primary' color='#9300ff' @click='sendChat(item.uid)'>发起聊天</n-button>
      </div>
    </n-card>
  </n-modal>
  <n-modal
      v-model:show='showDelModel'
      preset='dialog'
      title='是否确认删除该聊天'
      positive-text='确认'
      negative-text='点错啦'
      @positive-click='confirmDelChat'></n-modal>
  <!-- 右键封装 -->
  <RightButton v-if='showRightBtn' :left='rightBtnLeft' :top='rightBtnTop' :list='czList'
               :style='`color:${param.style.fontColor}`'
               :bgColor='computedStyle.mainColorOpt'
               @choose='chooseRight'></RightButton>
</template>

<script setup lang='ts'>
import {eqRelation, goChat, delChat, topChat, clearNotRead} from '@/api/index';
import {tips, tranColor, setUser, truncate, computePhoto} from '@/utils/OtherUtils';
import {countTimeDiff, reckonTime, getTimeFormat} from '@/utils/TimeUtil';
import {staticUserData, welcome} from '@/utils/staticUtils';
import RightButton from '../of/RightButton.vue';
import Frame from './Frame.vue';
import NullChat from '../svg/NullChat.vue';
import Svg from '../of/Svg.vue';
import WebSocketService from '@/utils/WebSocketService';

const webSocketService = inject<WebSocketService>('webSocketService') as WebSocketService;
const isSmallWin = inject<Ref<boolean>>('isSmallWin') || ref(false);
const param = inject<Ref<chatProps>>('param') as chatProps | any;
const computedStyle = inject<Ref<any>>('computedStyle') as any;
const emit = defineEmits(['searchUser', 'clickUser']);
// ===================================其他功能===================================//
// 基本样式
const mainClass = () => {
  return {
    'w-100': isSmallWin.value,
    'new-info': newInfo.value
  };
};

// 通讯样式
const userClass = (item: Relation) => {
  return {
    check: item.id === checkId.value
  };
};

// 手机状态下展开和收缩
const isPhoneUnfold = ref(false);
const isPhoneCallBack = (state: boolean) => {
  isPhoneUnfold.value = !state;
};

// 消息闪烁
const newInfo = ref(false);
const ofNewInfo = () => {
  if (!param.messageFlicker) return;
  newInfo.value = true;
  setTimeout(() => {
    newInfo.value = false;
  }, 600);
};

// 点击用户返回ID
const clickUser = (id: number) => {
  emit('clickUser', id);
};
// ===================================聊天Frame.vue回调===================================//
// 发送消息回调
const ofNewMessage = inject<(message: any) => void>('ofNewMessage');
const sendCallBack = (res: { val: string; type: number; uid: number }) => {
  let message = res.val;
  if (res.type === 1) message = '[图片]';
  for (let i = 0; i < userList.value.length; i++) {
    if (userList.value[i].id === checkId.value) {
      userList.value[i].lastMessage = message;
      userList.value[i].lastMessageDate = getTimeFormat(new Date());
      break;
    }
  }
  if (res.uid) {
    if (ofNewMessage) ofNewMessage({message: message, type: res.type, receiveUid: res.uid});
    ofNewInfo();
  }
  sortData();
};

// 关闭回调
const closeChat = () => {
  checkId.value = -99;
  nowUser.value = {};
};

// ===================================搜索操作===================================//
// 搜索
const showModal = ref(false);
const searchVal = ref('');
const goSearch = () => {
  if (!searchVal.value.trim()) return;
  showModal.value = true;
  emit('searchUser', searchVal.value);
};

// 发起聊天
const sendChat = (uid: number) => {
  goChat(uid).then((res: Result) => {
    if (res.code !== 200) return tips('error', res.msg);
    setUser([res.data], param.eqUserInfo).then((data: any) => {
      addTopList(data[0]);
      showChat(data[0]);
      sortData();
      searchVal.value = '';
    });
  });
  showModal.value = false;
};

// 添加到顶部通讯录
const addTopList = (data: Relation) => {
  let pointer = 0;
  let hasUserPointer = -1;
  for (let i = 0; i < userList.value.length; i++) {
    if (userList.value[i].top !== 1) pointer = i;
    if (userList.value[i].id === data.id) hasUserPointer = i;
  }
  if (hasUserPointer !== -1) userList.value.splice(hasUserPointer, 1);
  userList.value.splice(pointer, 0, data);
};

// 选择用户
const checkId = ref(-99);
const nowUser = ref({} as any);
const showChat = (user: any) => {
  if (nowUser.value?.id === user.id) return;
  checkId.value = user.id;
  nowUser.value = user;
  user.notRead = 0;
  if (isSmallWin.value) isPhoneUnfold.value = true;
};

// ===================================静态数据【用于体验时】===================================//
// 静态用户通讯录
const staticUser = () => {
  userList.value = staticUserData();
  sortData();
};

// ===================================获取通讯录数据===================================//
// 查询用户通讯录
const userList = ref([] as Relation[]);
const eqUserList = () => {
  eqRelation()
      .then((res: Result): void => {
        if (res.code !== 200 && param.experienceMode) {
          welcome();
          staticUser();
          return;
        }
        setUser(res.data, param.eqUserInfo).then((res: any) => {
          userList.value = res;
          sortData();
        });
      })
      .finally(() => addListener());
};

// ===================================右键操作===================================//
// 创建监听
const tapAndHold = ref(false);
const addListener = () => {
  const parentDiv = document.querySelector('.user-list') as HTMLElement;
  parentDiv.addEventListener('contextmenu', listenerUser);
  let longPressTimer: NodeJS.Timeout;
  parentDiv.addEventListener('mousedown', (e: MouseEvent) => {
    longPressTimer = setTimeout(() => {
      listenerUser(e);
      tapAndHold.value = true;
      setTimeout(() => {
        tapAndHold.value = false;
      }, 500);
    }, 500);
  });
  parentDiv.addEventListener('mouseup', () => {
    clearTimeout(longPressTimer);
  });
  parentDiv.addEventListener('mouseleave', () => {
    clearTimeout(longPressTimer);
  });
};

// 选择右键内容回调
const chooseRight = (item: any) => {
  item.incident();
  showRightBtn.value = false;
};
// 监听列表右键
const rightBtnLeft = ref(0);
const rightBtnTop = ref(0);
const czList = ref();
const showRightBtn = ref(false);
const nowCheckData = ref({} as Relation);
const listenerUser = (e: MouseEvent) => {
  e.preventDefault();
  const parentDiv = document.querySelector('.user-list-sc');
  if (!parentDiv) return;
  let target = e.target as HTMLElement;
  let index = 0;
  if (target.classList.contains('user-list-sc')) return;
  if (target.classList.contains('user')) {
    const allSonBoxes = Array.from(parentDiv.children);
    index = allSonBoxes.indexOf(target);
  } else {
    while (target !== parentDiv) {
      if (target.classList.contains('user')) {
        const allSonBoxes = Array.from(parentDiv.children);
        index = allSonBoxes.indexOf(target);
      }
      target = target.parentElement as HTMLElement;
    }
  }
  showRightBtn.value = true;
  rightBtnLeft.value = e.x;
  rightBtnTop.value = e.y;
  nowCheckData.value = userList.value[index];
  const top = userList.value[index].top;
  czList.value = [
    {
      id: 0,
      name: top === 0 ? '置顶聊天' : '取消置顶',
      incident: () => topChatGo(top === 0 ? 1 : 0)
    },
    {id: 0, name: '删除聊天', incident: () => delChatGo()}
  ];
};

// 置顶聊天
const topChatGo = (state: number) => {
  topChat(nowCheckData.value.id, state).then(() => {
    const index = userList.value.findIndex((item) => item.id === nowCheckData.value.id);
    const [data] = userList.value.splice(index, 1);
    data.top = state;
    userList.value.unshift(data);
    sortData();
  });
};

// 排序数据
const sortData = () => {
  userList.value.sort((a, b) => {
    if (a.top === 1 && b.top !== 1) return -1;
    if (b.top === 1 && a.top !== 1) return 1;
    return countTimeDiff(a.lastMessageDate, b.lastMessageDate, 1000) > 0 ? 1 : -1;
  });
};

// 删除聊天
const showDelModel = ref(false);
const delChatGo = () => {
  showDelModel.value = true;
};
const confirmDelChat = () => {
  delChat(nowCheckData.value.id).then((res) => {
    if (res.code !== 200) {
      if (!param.experienceMode) return tips('error', res.msg);
      tips('warning', '体验环境，并没有真正的删除哦');
    }
    showDelModel.value = false;
    userList.value = userList.value.filter((item) => item.id !== nowCheckData.value.id);
    if (nowUser.value.id === nowCheckData.value.id) nowUser.value = {};
    tips('success', res.msg);
  });
};

// 销毁监听
const clearListener = () => {
  const parentDiv = document.querySelector('.user-list') as HTMLElement;
  parentDiv.removeEventListener('contextmenu', listenerUser);
};

// 关闭右键
const closeRightBtn = () => {
  if (!tapAndHold.value) showRightBtn.value = false;
};

// 关闭右键公共方法
const closeRightBtnCom = (state: boolean) => {
  const dom = document.getElementsByClassName('chat-main')[0];
  state ? dom.addEventListener('click', closeRightBtn) : dom.removeEventListener('click', closeRightBtn);
};

// ===================================组件初始化操作===================================//
const goUid = ref(sessionStorage.getItem('go_chat_uid'));
watch(
    () => goUid.value,
    () => {
      if (!goUid.value) return;
      upChat();
    }
);
// 跳聊天
const upChat = () => {
  if (!goUid.value) return;
  sendChat(+goUid.value);
  setTimeout(() => {
    sessionStorage.removeItem('go_chat_uid');
  }, 50);
};

// 来新消息了
const ws = ref(webSocketService);
watch(
    () => ws.value?.pushCount,
    () => {
      const data = ws.value.newMessage;
      let newData = userList.value.filter((item) => item.relationUid === data.receiveUid)[0];
      newData.lastMessage = truncate(data.message) ?? '-';
      if (Object.keys(nowUser.value).length === 0 || nowUser.value.relationUid !== data.receiveUid) {
        if (data.state !== 2) newData.notRead++;
        ofNewInfo();
      } else {
        ws.value.send(JSON.stringify(nowUser.value.id));
        clearNotRead(nowUser.value.relationUid)
      }
    }
);

// 监听悬浮是否存在滚动条
const listenerHover = () => {
  const dom = document.getElementsByClassName('user-list')[0];
  dom.addEventListener('mouseenter', () => {
    const hasScrollBar = dom.scrollHeight > dom.clientHeight;
    if (hasScrollBar) dom.classList.add('user-list-up');
  });
  dom.addEventListener('mouseleave', () => {
    dom.classList.remove('user-list-up');
  });
};

onMounted(() => {
  if (goUid.value) upChat();
  eqUserList();
  closeRightBtnCom(true);
  listenerHover();
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
});
</script>

<style lang='scss' scoped>

.shrink {
  width: $px-36 !important;
  flex-direction: unset !important;
}

.min-icon {
  position: absolute;
  right: -1px;
  bottom: 50%;
}

.user-list {
  padding-bottom: $px-4;
}

.user-list:hover::-webkit-scrollbar {
  width: $px-4;
}

.user-list::-webkit-scrollbar {
  width: 0;
}

.user-list-sc {
  padding: 0 $px-4;
}

.user-list-up:hover .user-list-sc {
  padding-right: 0;
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

.user:hover {
  background-color: $bg-color-hover;
}

.check {
  background-color: $bg-color-hover;
}

.user-main {
  width: calc(100% - $px-48 - $px-5);
}

.user:last-child .user-box:first-child {
  border-bottom: unset;
}

.chat-main {
  border-radius: $px-12;
}

.chat-com {
  position: relative;
  overflow: hidden;
  width: $px-280;
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
