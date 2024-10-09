<template>
  <div class="w-960 flex-center-center mt-nav">
    <div class="chat-main w-100 flex" :class="newInfo ? 'new-info ' : ''">
      <div class="chat-com flex-down">
        <!-- 搜索 -->
        <div class="pd-12">
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
          <div
            v-else
            class="pd-zy-12 hover-pointer user"
            :class="item.id === checkId ? 'check' : ''"
            v-for="(item, index) in userList"
            :key="index"
            @click="showChat(item)">
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
      </div>
      <!-- 聊天框 -->
      <ChatFrame :user="nowUser" @sendCallBack="sendCallBack"></ChatFrame>
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
    <!-- 右键封装 -->
    <OfRightButton v-if="showRightBtn" :left="rightBtnLeft" :top="rightBtnTop" :list="czList" @choose="chooseRight"></OfRightButton>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from 'naive-ui';
import { eqRelation, eqUserMail, goChat } from '~/api/index';
import { isEmail } from '~/utils/OtherUtils';
const { dialog } = createDiscreteApi(['dialog']);
const store = useStore();

// 发送消息回调
const sendCallBack = (res: { val: string; type: number }) => {
  let message = res.val;
  if (res.type === 1) message = '[图片]';
  for (let i = 0; i < userList.value.length; i++) {
    if (userList.value[i].id === checkId.value) {
      userList.value[i].lastMessage = message;
      userList.value[i].lastMessageDate = getTimeFormat(new Date());
      break;
    }
  }
};

// 发起聊天
const sendChat = (uid: number) => {
  goChat(uid).then((res: Result) => {
    if (res.code !== 200) return tips('error', res.msg);
    addTopList(res.data);
    showChat(res.data);
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
const checkId = ref(-1);
const nowUser = ref({} as any);
const showChat = (user: any) => {
  if (nowUser.value?.id === user.id) return;
  checkId.value = user.id;
  nowUser.value = user;
  user.notRead = 0;
};

// 搜索
const showModal = ref(false);
const searchVal = ref('');
const hasUser = ref<any>();
const goSearch = () => {
  if (!isEmail(searchVal.value)) return tips('error', '请输入正确的邮箱格式📫');
  hasUser.value = {};
  // 发送接口查询用户
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
        userList.value = [
          {
            id: 1,
            uid: 1,
            relationUid: 2,
            lastMessage: '欢迎使用nh-caht',
            notRead: 1,
            top: 1,
            lastMessageDate: getTimeFormat(new Date()),
            name: '体验官小H',
            photo: '体'
          }
        ];
        return;
      }
      userList.value = res.data;
    })
    .finally(() => addListener());
};

// 创建监听
const userListDom = ref([] as any);
const addListener = () => {
  userListDom.value = Array.from(document.getElementsByClassName('user-box'));
  userListDom.value.forEach((item: any, index: number) => {
    item.addEventListener('contextmenu', (e: MouseEvent) => listenerUser(e, index));
  });
};

// 选择右键内容回调
const chooseRight = (item: any) => {
  showRightBtn.value = false;
};
// 监听列表右键
const rightBtnLeft = ref(0);
const rightBtnTop = ref(0);
const czList = ref();
const showRightBtn = ref(false);
const listenerUser = (e: MouseEvent, index: number) => {
  e.preventDefault();
  showRightBtn.value = true;
  // userList.value[index]
  rightBtnLeft.value = e.x;
  rightBtnTop.value = e.y;
  czList.value = [{ id: 0, name: '置顶消息' }];
};

// 销毁监听
const clearListener = () => {
  userListDom.value.forEach((item: any, index: number) => {
    item.removeEventListener('contextmenu', (e: MouseEvent) => listenerUser(e, index));
  });
};

// 关闭右键
const closeRightBtn = () => {
  showRightBtn.value = false;
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

// 来新消息了
const newInfo = ref(false);
onMounted(() => {
  newInfo.value = true;
  setTimeout(() => {
    newInfo.value = false;
  }, 1500);
  eqUserList();
  closeRightBtnCom(true);
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
});
</script>

<style lang="scss" scoped>
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
