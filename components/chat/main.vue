<template>
  <div class="w-960 flex-center-center mt-nav" :class="isSmallWin ? 'w-100' : ''">
    <div class="chat-main w-100 flex" :class="newInfo ? 'new-info ' : ''">
      <div class="chat-com flex-down" :class="isPhoneUnfold ? 'shrink' : ''">
        <!-- 搜索 -->
        <div class="pd-12" :class="isPhoneUnfold ? 'shrink' : ''">
          <n-input-group>
            <n-input
              style="--n-border-hover: 1px solid #ff6700; --n-border-focus: 1px solid #ff6700; --n-box-shadow-focus: 0 0 0 2px rgba(255, 103, 0, 0.2)"
              :style="{ width: '100%' }"
              placeholder="输入Ta的邮箱-发起聊天"
              v-model:value="searchVal"
              clearable />
            <n-input-group-label class="hover-pointer" @click="goSearch" color="#ff6700">搜索</n-input-group-label>
          </n-input-group>
        </div>
        <!-- 通讯列表 -->
        <div class="over-auto w-100 h-100 user-list">
          <template v-if="userList.length === 0">
            <div class="flex-down-center h-100 flex-center-onely">
              <SvgNullChat></SvgNullChat>
              <div class="ft-color-tips">聊天通讯空空的</div>
            </div>
          </template>
          <div v-else class="pd-zy-12 hover-pointer user" :class="userClass(item)" v-for="(item, index) in userList" :key="item.id" @click="showChat(item)">
            <div class="user-box pd-sx-6 flex-center-zy">
              <n-badge :value="item.notRead" :max="99" :offset="[-5, 5]">
                <div class="user-head flex-center-center" :style="'background-color:' + tranColor(item.photo)">
                  <template v-if="item.photo.length > 5">
                    <img :src="item.photo" />
                  </template>
                  <template v-else>
                    {{ item.photo }}
                  </template>
                </div>
              </n-badge>
              <div class="user-main">
                <div class="flex-center-zy">
                  <div class="ft-color-white ft-16 ft-b">{{ item.name }}</div>
                  <div class="ft-13">
                    {{ reckonTime(item.lastMessageDate) }}
                  </div>
                </div>
                <div class="ft-13 ft-over">
                  {{ item.lastMessage }}
                </div>
              </div>
            </div>
          </div>
        </div>
        <!-- phone 通过nav全局引入-->
        <div class="min-icon" v-if="isSmallWin">
          <OfSvg
            :width="32"
            :height="32"
            name="close"
            fill="#ff6700"
            viewBox="0 0 1139 1024"
            v-if="!isPhoneUnfold"
            @click="isPhoneCallBack(isPhoneUnfold)"></OfSvg>
          <OfSvg :width="32" :height="32" name="open" fill="#ff6700" viewBox="0 0 1139 1024" v-else @click="isPhoneCallBack(isPhoneUnfold)"></OfSvg>
        </div>
      </div>
      <!-- 聊天框 -->
      <ChatFrame :user="nowUser" :isPhoneUnfold="isPhoneUnfold" @sendCallBack="sendCallBack" @closeChat="closeChat"></ChatFrame>
    </div>
    <!-- 弹框 -->
    <n-modal v-model:show="showModal">
      <n-card style="width: 600px" :bordered="false" size="huge" role="dialog" aria-modal="true">
        <template v-if="!hasUser">
          <div class="flex-down-center">
            <OfSvg :width="64" :height="64" name="no-user" viewBox="0 0 1139 1024"></OfSvg>
            <span class="ft-16 ft-color-tips mt-12">未找到该用户</span>
          </div>
        </template>
        <div class="flex-center-zy" v-else>
          <div class="flex-center">
            <div class="user-head flex-center-center" :style="'background-color:' + tranColor(hasUser.photo)">{{ hasUser.photo }}</div>
            <div class="ft-16 ft-b ml-10">{{ hasUser.name }}</div>
          </div>
          <n-button round strong type="primary" color="#9300ff" @click="sendChat(hasUser.uid)">发起聊天</n-button>
        </div>
      </n-card>
    </n-modal>
    <n-modal
      v-model:show="showDelModel"
      preset="dialog"
      title="是否确认删除该聊天"
      positive-text="确认"
      negative-text="点错啦"
      @positive-click="confirmDelChat"></n-modal>
    <!-- 右键封装 -->
    <OfRightButton v-if="showRightBtn" :left="rightBtnLeft" :top="rightBtnTop" :list="czList" @choose="chooseRight"></OfRightButton>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui';
import { eqRelation, eqUserMail, goChat, delChat, topChat } from '~/api/index';
import { isEmail } from '~/utils/OtherUtils';
import WebSocketService from '@/utils/WebSocketService';
const webSocketService = inject<WebSocketService>('webSocketService') as WebSocketService;
const { dialog } = createDiscreteApi(['dialog']);
const store = useStore();
const isSmallWin = inject<Ref<boolean>>('isSmallWin') || ref(false);

// 通讯样式
const userClass = (item: Relation) => {
  return {
    check: item.id === checkId.value,
    userTop: item.top === 1
  };
};
// 手机状态下展开和收缩
const isPhoneUnfold = ref(false);
const isPhoneCallBack = (state: boolean) => {
  isPhoneUnfold.value = !state;
};

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
    if (ofNewMessage) ofNewMessage({ message: message, type: res.type, receiveUid: res.uid });
    newInfo.value = true;
    setTimeout(() => {
      newInfo.value = false;
    }, 600);
  }
  sortData();
};

// 关闭回调
const closeChat = () => {
  checkId.value = -99;
  nowUser.value = {};
};

// 发起聊天
const sendChat = (uid: number) => {
  goChat(uid).then((res: Result) => {
    if (res.code !== 200) return tips('error', res.msg);
    addTopList(res.data);
    showChat(res.data);
    sortData();
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

// 搜索
const showModal = ref(false);
const searchVal = ref('');
const hasUser = ref<any>();
const goSearch = () => {
  if (!isEmail(searchVal.value)) return tips('error', '请输入正确的邮箱格式📫');
  hasUser.value = {};
  eqUserMail(searchVal.value).then((res) => {
    if (res.code !== 200) return tips('error', res.msg);
    hasUser.value = res.data;
    showModal.value = true;
  });
};

// 查询用户通讯录
const userList = ref([] as Relation[]);
const eqUserList = () => {
  eqRelation()
    .then((res: Result): void => {
      if (!res.code || res.code === 403) {
        welcome();
        staticUser();
        return;
      }
      userList.value = res.data;
      sortData();
    })
    .finally(() => addListener());
};

// 静态用户通讯录
const staticUser = () => {
  userList.value = [];
  for (let i = 0; i < 2; i++) {
    userList.value.push({
      id: i,
      uid: -1,
      relationUid: 2,
      lastMessage: '你可以尝试向我发送消息哟，体验不同的功能。',
      notRead: 1,
      top: i,
      lastMessageDate: getTimeFormat(new Date(new Date().getTime() - 1000 * 60 * i)),
      name: '体验官H-' + (i + 1),
      photo: '体'
    });
  }
  sortData();
};

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
  const parentDiv = document.querySelector('.user-list');
  if (!parentDiv) return;
  let target = e.target as HTMLElement;
  let index = 0;
  if (target.classList.contains('user-list')) return;
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
    { id: 0, name: top === 0 ? '置顶聊天' : '取消置顶', incident: () => topChatGo(top === 0 ? 1 : 0) },
    { id: 0, name: '删除聊天', incident: () => delChatGo() }
  ];
};

// 置顶聊天
const topChatGo = (state: number) => {
  topChat(nowCheckData.value.id, state).then((res) => {
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
    if (a.top === 1 && b.top !== 1) {
      return -1;
    }
    if (b.top === 1 && a.top !== 1) {
      return 1;
    }
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
    showDelModel.value = false;
    userList.value = userList.value.filter((item) => item.id !== nowCheckData.value.id);
    if (nowUser.value.id === nowCheckData.value.id) nowUser.value = {};
    if (res.code !== 200) tips('warning', '体验环境，并没有真正的删除哦');
    else tips('success', res.msg);
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

// 提示
const welcome = () => {
  if (store.not_tips == 'false')
    dialog.warning({
      title: 'Hi~👋',
      content: '您当前为体验模式，数据不会保留。建议您登录后与好友开启实时聊天哦🎉',
      positiveText: '别提醒我了',
      onPositiveClick: () => {
        store.stopTips();
      },
      negativeText: '知道啦'
    });
};

watch(
  () => store.go_chat_u,
  () => {
    if (store.go_chat_u === -99) return;
    upChat();
  }
);
// 跳聊天
const upChat = () => {
  sendChat(store.go_chat_u);
  setTimeout(() => {
    store.initGoChat();
  }, 50);
};

// 来新消息了
const newInfo = ref(false);
const ws = ref(webSocketService);
watch(
  () => ws.value?.pushCount,
  () => {
    const data = ws.value.newMessage;
    let newData = userList.value.filter((item) => item.relationUid === data.receiveUid)[0];
    newData.lastMessage = truncate(data.message) ?? '-';
    if (Object.keys(nowUser.value).length === 0 || nowUser.value.relationUid !== data.receiveUid) {
      newInfo.value = true;
      if (data.state !== 2) newData.notRead++;
      setTimeout(() => {
        newInfo.value = false;
      }, 600);
    } else {
      ws.value.send(JSON.stringify(nowUser.value.id));
    }
  }
);

onMounted(() => {
  if (store.go_chat_u !== -99) upChat();
  eqUserList();
  closeRightBtnCom(true);
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
});
</script>

<style lang="scss" scoped>
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
  width: 0px;
}
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

.userTop {
  background-color: $ft-color-2-op-2 !important;
}

.user:hover {
  background-color: $bg-color-hover;
}

.check {
  background-color: $bg-color-hover;
}

.user-main {
  width: calc(100% - 3rem - 0.375rem);
  color: $ft-color-tips;
}

.user-box {
  border-bottom: $px-1 solid rgb(210 210 210 / 50%);
}
.user:last-child .user-box:first-child {
  border-bottom: unset;
}

.chat-main {
  background-color: rgb(255 255 255 / 12%);
  box-shadow: 0 0 0 $px-1-5 $ft-color-tips;
  height: $px-650;
  border-radius: $px-12;
}

.chat-com {
  position: relative;
  overflow: hidden;
  width: $px-280;
  border-right: $px-1 solid $ft-color-tips;
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
