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
      <Loader
        class="loader-abs"
        style="background-color: #2c334485"
        v-if="lodingMessage"
      ></Loader>
      <div class="cb-head flex-center">
        <div class="cb-head-controls"></div>
        <div class="w-100 flex-center-center">{{ user.name }}</div>
        <n-popover trigger="click" placement="bottom">
          <template #trigger>
            <div class="cb-head-controls flex-center-onely mr-4 hover-pointer">
              <Svg :width="28" :height="28" name="pointer"></Svg>
            </div>
          </template>
          <span class="hover-pointer" @click="closeChat">关闭聊天</span>
        </n-popover>
      </div>
      <div class="cb-body over-auto h-100">
        <!-- 时间 -->
        <div
          class="flex-center-center ft-13 ft-color-tips mt-8"
          v-if="chatData.length === 0"
        >
          <div class="cbb-tips">发起你们的第一句聊天吧，比如："你好"</div>
        </div>
        <div class="cbb-box" v-for="(item, index) in chatData" :key="index">
          <!-- 时间 -->
          <div
            class="flex-center-center ft-13 ft-color-tips mb-8"
            v-if="item.tab"
          >
            <div class="cbb-tips">{{ cutChatTime(item.date) }}</div>
          </div>
          <!-- 其他事件 -->
          <div
            class="flex-center-center ft-13 ft-color-tips mb-8"
            v-if="item.sendState === 2 || item.receiveState === 2"
          >
            <div class="cbb-tips">
              {{ item.message }}
              <n-button
                v-if="reMessageId === item.id"
                text
                color="#FF6700"
                class="ml-4"
                size="tiny"
                @click="reEdit"
                >重新编辑</n-button
              >
            </div>
          </div>
          <!-- 消息框 -->
          <template v-else>
            <template v-if="item.sendUid !== user.uid">
              <div
                class="flex w-100"
                @click="moreCheckedCallBack(item)"
                :class="moreCheckState ? 'is-checked' : ''"
              >
                <n-checkbox
                  :checked="item.check"
                  @update.self:checked="moreCheckedCallBack(item)"
                  v-if="moreCheckState"
                ></n-checkbox>
                <div class="cbb-main flex">
                  <div
                    class="user-head flex-center-center mr-4"
                    :style="'background-color:' + tranColor(user.photo)"
                    v-html="computePhoto(user.photo)"
                  ></div>
                  <div class="cbbm-box cbbm-box-left flex">
                    <span v-if="item.type === 0">{{ item.message }}</span>
                    <n-image v-else class="chat-image" :src="item.message" />
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div
                class="flex w-100"
                @click="moreCheckedCallBack(item)"
                :class="moreCheckState ? 'is-checked' : ''"
              >
                <n-checkbox
                  :checked="item.check"
                  @update.self:checked.stop="moreCheckedCallBack(item)"
                  v-if="moreCheckState"
                ></n-checkbox>
                <div class="cbb-main flex-right">
                  <div
                    class="user-head flex-center-center ml-4"
                    :style="
                      'background-color:' + tranColor(param.userInfo.photo)
                    "
                    v-html="computePhoto(param.userInfo.photo)"
                  ></div>
                  <div class="cbbm-box cbbm-box-right flex">
                    <span v-if="item.type === 0">{{ item.message }}</span>
                    <n-image v-else class="chat-image" :src="item.message" />
                  </div>
                  <!-- 消息状态 -->
                  <div class="flex-end mr-6">
                    <!-- 失败 -->
                    <Svg
                      class="hover-pointer"
                      name="message-fail"
                      fill="red"
                      :width="24"
                      :height="24"
                      v-if="item.state === 2"
                      @click="reissue(item.message, item.type)"
                    ></Svg>
                    <!-- 加载中 -->
                    <Svg
                      class="message-loading"
                      name="message-loading"
                      fill="#d2d2d2"
                      :width="24"
                      :height="24"
                      v-if="item.state === 0"
                    ></Svg>
                  </div>
                </div>
              </div>
            </template>
          </template>
        </div>
      </div>
      <div class="cb-input">
        <template v-if="moreCheckState">
          <div class="flex-center-center h-100">
            <div class="flex-down-center" @click="delMoreChecked">
              <Svg
                :width="24"
                :height="24"
                fill="#ff6700"
                class="hover-pointer mb-2"
                name="del"
              ></Svg>
              <n-button text color="#ff6700" size="large">删除</n-button>
            </div>
            <div class="flex-down-center ml-18" @click="cancelMoreChecked">
              <Svg
                :width="24"
                :height="24"
                fill="#ff670099"
                class="hover-pointer mb-2"
                name="cancel"
              ></Svg>
              <n-button text color="#ff670099" size="large">取消</n-button>
            </div>
          </div>
        </template>
        <template v-else>
          <!-- 表情包图片等等 -->
          <div class="cb-input-controls flex-center">
            <n-popover trigger="click" raw @update:show="handleUpdateShow">
              <template #trigger>
                <Svg
                  :width="24"
                  :height="24"
                  class="hover-pointer ml-12"
                  name="emoji"
                ></Svg>
              </template>
              <EmoJi @choose="chooseEmoji" />
            </n-popover>
            <n-upload
              :show-file-list="false"
              @before-upload="beforeUpload"
              accept=".png,.jpeg,.jpg"
            >
              <Svg
                :width="24"
                :height="24"
                class="hover-pointer ml-12"
                name="up-image"
              ></Svg>
            </n-upload>
          </div>
          <!-- 输入框 -->
          <div class="cb-input-main">
            <n-input
              ref="inputInstRef"
              style="
                --n-border: unset;
                --n-border-hover: unset;
                --n-border-focus: unset;
                --n-box-shadow-focus: unset;
              "
              v-model:value="sendVal"
              type="textarea"
              @keydown.enter.native="handleKeyUp"
              @input="valChange"
              placeholder="说点啥..."
            />
          </div>
          <div class="cb-input-go flex-center-zy pd-zy-6 ft-color-tips">
            <div>{{ sendVal.length }} / {{ inputMaxNumber }}</div>
            <div class="hover-pointer" @click="sendInfo">按Enter键发送</div>
          </div>
        </template>
      </div>
    </template>
    <!-- 右键封装 -->
    <RightButton
      v-if="showRightBtnMessage"
      :left="rightBtnLeft"
      :top="rightBtnTop"
      :list="czList"
      @choose="chooseRight"
    ></RightButton>
  </div>
</template>

<script setup lang="ts">
import { createDiscreteApi } from "naive-ui";
const { notification } = createDiscreteApi(["notification"]);
import type { UploadFileInfo } from "naive-ui";
import { countTimeDiff, cutChatTime, getTimeFormat } from "@/utils/TimeUtil";
import { throttle } from "@/utils/domUtils";
import {
  tranColor,
  truncate,
  tips,
  computePhoto,
  randomNumber,
  copyImage,
  copyText,
} from "@/utils/OtherUtils";
import {
  eqChat,
  sendMessage,
  sendMessageImage,
  revocationMessage,
  delMessage,
} from "@/api/index";
import WebSocketService from "@/utils/WebSocketService";
import RightButton from "../of/RightButton.vue";
import Svg from "../of/Svg.vue";
import EmoJi from "../of/EmoJi.vue";
import Loader from "../of/Loader.vue";

const webSocketService = inject<WebSocketService>(
  "webSocketService"
) as WebSocketService;
const isSmallWin = inject<Ref<boolean>>("isSmallWin") || ref(false);

const param = inject<Ref<any>>("param") as any;

const props = defineProps({
  user: {
    type: Object,
    default: {},
  },
  isPhoneUnfold: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits(["sendCallBack", "closeChat"]);

// ===================================其他功能===================================//
// 关闭聊天
const closeChat = () => {
  if (nowChatUid) nowChatUid(-99);
  emit("closeChat");
};

// 消息与上一条消息时间标
const chatTabOne = (data: message) => {
  if (chatData.value.length === 0) data.tab = true;
  else
    data.tab =
      countTimeDiff(
        chatData.value[chatData.value.length - 1].date,
        data.date,
        60
      ) >= 10;
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

// 展开关闭表情
const handleUpdateShow = (state: boolean) => {
  if (state) inputInstRef.value?.focus();
};

// 表情回调
const inputInstRef = ref<any>("");
const chooseEmoji = (val: string) => {
  const inputElement = inputInstRef.value?.$el.querySelector("textarea");
  if (inputElement) {
    const nowPonit = inputElement.selectionStart + val.length;
    sendVal.value =
      sendVal.value.slice(0, inputElement.selectionStart) +
      val +
      sendVal.value.slice(inputElement.selectionStart);
    setTimeout(() => {
      inputElement.focus();
      inputElement.setSelectionRange(nowPonit, nowPonit);
    });
  } else sendVal.value += val;
};

// 滚动条居底
const scrollToButtom = () => {
  setTimeout(() => {
    let scrollDom = document.getElementsByClassName("cb-body")[0];
    scrollDom.scrollTop = scrollDom.scrollHeight;
  }, 100);
};

// 监听滚动条
const listenerScrollToTop = (state: boolean) => {
  let scrollDom = document.getElementsByClassName("cb-body")[0];
  if (!scrollDom) return;
  const throttledScrollToTop = throttle(scrollToTop, 50);
  if (state) scrollDom.addEventListener("scroll", throttledScrollToTop);
  else scrollDom.removeEventListener("scroll", throttledScrollToTop);
};

// ===================================发送消息===================================//
// 回车处理
const sendVal = ref("");
const inputMaxNumber = ref(2000);
const handleKeyUp = (e: KeyboardEvent) => {
  if (e.key !== "Enter") return;
  if (e.shiftKey) return;
  if (e.altKey) return (sendVal.value += "\n");
  e.preventDefault();
  sendInfo();
};

// 输入框字数限制
const valChange = () => {
  if (sendVal.value.length > inputMaxNumber.value)
    return (sendVal.value = sendVal.value.slice(0, inputMaxNumber.value));
};

// 发送消息
const sendInfo = () => {
  if (!sendInfoPre()) return;
  const pointer = pushDataOneCom(
    -88,
    props.user.uid,
    props.user.relationUid,
    0,
    sendVal.value,
    0
  );
  emit("sendCallBack", { val: truncate(sendVal.value), type: 0 });
  let data = sendVal.value;
  sendVal.value = "";
  scrollToButtom();
  sendMessage({
    receiveUid: props.user.relationUid,
    message: data,
    type: 0,
  }).then((res: Result) => {
    if (res.code !== 200) res.data = randomNumber();
    chatData.value[pointer].id = res.data;
    chatData.value[pointer].state = res.code === 200 ? 1 : 2;
  });
};

// 推送单条消息
const pushDataOneCom = (
  id: number,
  sendUid: number,
  receiveUid: number,
  type: number,
  message: string,
  state: number,
  sendState: number = 1
): number => {
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
    })
  );
  return chatData.value.length - 1;
};

// 发送消息前置处理
const sendInfoPre = (): boolean => {
  if (sendVal.value.length === 0) return false;
  if (sendVal.value.split("\n").length > 20) {
    notification["error"]({
      content: "超出长度20行的限制",
      duration: 2500,
      keepAliveOnHover: true,
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
    const file = new File([blob], "chat-image", { type: blob.type });
    beforeUpload({ file: { file: file } as UploadFileInfo, fileList: [] });
    return;
  }
  sendVal.value = message;
  sendInfo();
};

// 发送图片前置校验
const upLoadCheck = (file: File | null | undefined) => {
  if (!file) {
    tips("error", "文件异常👾");
    return false;
  }
  if ((file?.size ?? 0) / 1024 / 1024 > 4) {
    tips("error", "图片太大了吧，大小不能超过4M🤯");
    return false;
  }
  if (file?.type !== "image/png" && file?.type !== "image/jpeg") {
    tips("error", "只能发送png或jpeg格式的图片文件");
    return false;
  }
  return true;
};

// 发送图片
const beforeUpload = async (data: {
  file: UploadFileInfo;
  fileList: UploadFileInfo[];
}): Promise<boolean> => {
  const file = data.file.file as File;
  const result = upLoadCheck(file);
  if (!result) return false;
  const reader = new FileReader();
  reader.onload = function (e: any) {
    const fd = new FormData();
    fd.append("file", file);
    const pointer = pushDataOneCom(
      -88,
      props.user.uid,
      props.user.receiveUid,
      1,
      e.target.result,
      0
    );
    emit("sendCallBack", { val: truncate("[图片]"), type: 1 });
    scrollToButtom();
    sendMessageImage(fd, props.user.relationUid).then((res) => {
      if (res.code !== 200) res.data = randomNumber();
      chatData.value[pointer].id = res.data;
      chatData.value[pointer].state = res.code === 200 ? 1 : 2;
    });
  };
  reader.readAsDataURL(file as any);
  return true;
};

// ===================================获取聊天数据===================================//
// 获取聊天数据
const chatData = ref([] as any);
const page = ref(1);
const next = ref(false);
const eqChatData = () => {
  eqChatCom();
};

// 数据公共处理
const eqChatCom = (needBootom: boolean = true) => {
  lodingMessage.value = true;
  eqChat(props.user.relationUid, page.value)
    .then((res: Result) => {
      let data = [] as message[];
      if (res.code !== 200) return tips("error", res.msg);
      data = res.data.data;
      next.value = res.data.next;
      if (next.value) page.value++;
      if (needBootom) chatData.value = chatTab(data);
      else {
        data = chatTab([...data, chatData.value[0]]);
        const oldHeight =
          document.getElementsByClassName("cb-body")[0].scrollHeight || 0;
        chatData.value.shift();
        chatData.value = [...chatTab(data), ...chatData.value];
        setTimeout(() => {
          let newDom = document.getElementsByClassName("cb-body")[0];
          newDom.scrollTop = newDom.scrollHeight - oldHeight;
        }, 10);
      }
    })
    .finally(() => {
      setTimeout(() => {
        lodingMessage.value = false;
        addListener();
        onelyOne.value = true;
        if (!needBootom) return;
        scrollToButtom();
        listenerScrollToTop(true);
      }, 150);
    });
};

// 上拉拉取消息
const lodingMessage = ref(false);
const scrollToTop = () => {
  let scrollDom = document.getElementsByClassName("cb-body")[0];
  if (scrollDom.scrollTop < 30 && next.value && !lodingMessage.value) {
    eqChatCom(false);
  }
};

// ===================================右键操作===================================//
const tapAndHold = ref(false);
const onelyOne = ref(false);
const addListener = () => {
  if (onelyOne.value) return;
  tapAndHold.value = false;
  const parentDiv = document.querySelector(".cb-body") as HTMLElement;
  parentDiv.addEventListener("contextmenu", listenerMessage);
  let longPressTimer: NodeJS.Timeout;
  parentDiv.addEventListener("mousedown", (e: MouseEvent) => {
    longPressTimer = setTimeout(() => {
      listenerMessage(e);
      tapAndHold.value = true;
      setTimeout(() => {
        tapAndHold.value = false;
      }, 500);
    }, 500);
  });
  parentDiv.addEventListener("mouseup", () => {
    clearTimeout(longPressTimer);
  });
  parentDiv.addEventListener("mouseleave", () => {
    clearTimeout(longPressTimer);
  });
};
// 右键事件
const rightBtnLeft = ref(0);
const rightBtnTop = ref(0);
const czList = ref();
const showRightBtnMessage = ref(false);
const nowCheckData = ref({} as message);
const listenerMessage = (e: MouseEvent) => {
  e.preventDefault();
  const parentDiv = document.querySelector(".cb-body");
  if (!parentDiv) return;
  if (moreCheckState.value) return;
  let target = e.target as HTMLElement;
  let index = -1;
  if (target.classList.contains("cbbm-box")) {
    const allSonBoxes = Array.from(parentDiv.children);
    index = allSonBoxes.indexOf(eqFather(target));
  } else {
    while (target !== parentDiv) {
      if (target.classList.contains("cbbm-box")) {
        const allSonBoxes = Array.from(parentDiv.children);
        index = allSonBoxes.indexOf(eqFather(target));
      }
      target = target.parentElement as HTMLElement;
    }
  }
  if (index === -1) return;
  nowCheckData.value = chatData.value[index];
  if (nowCheckData.value.state === 0) return;
  showRightBtnMessage.value = true;
  rightBtnLeft.value = e.x;
  rightBtnTop.value = e.y;
  czList.value = [
    { id: 0, name: "复制内容", incident: () => copyMessage() },
    {
      id: 1,
      name: "删除消息",
      incident: () => delMessageGo([nowCheckData.value.id]),
    },
  ];
  if (
    nowCheckData.value.sendUid !== props.user.relationUid &&
    countTimeDiff(nowCheckData.value.date, getTimeFormat(new Date()), 60) < 3
  ) {
    czList.value.push({
      id: 2,
      name: "撤回消息",
      incident: () => revocationMessageGo(),
    });
  }
  czList.value.push({ id: 3, name: "多选", incident: () => moreChecked() });
};

// 获取父级dom
const eqFather = (target: HTMLElement) => {
  while (!target.classList.contains("cbb-box")) {
    target = target.parentElement as HTMLElement;
  }
  return target;
};

// 选择右键内容回调
const chooseRight = (item: any) => {
  item.incident();
  showRightBtnMessage.value = false;
};

// 撤回消息
const reMessage = ref("");
const reMessageId = ref(-1);
const revocationMessageGo = () => {
  showRightBtnMessage.value = false;
  if (nowCheckData.value.type === 0) {
    reMessage.value = nowCheckData.value.message;
    reMessageId.value = nowCheckData.value.id;
  }
  revocationMessage(nowCheckData.value.id).then((res: Result) => {
    if (res.code !== 200) tips("error", res.msg);
    nowCheckData.value.message = "你撤回了一条消息";
    nowCheckData.value.sendState = 2;
    emit("sendCallBack", { val: truncate("你撤回了一条消息"), type: 0 });
  });
};
// 重新编辑
const reEdit = () => {
  sendVal.value = reMessage.value;
};

// 删除聊天
const delMessageGo = (ids: number[]) => {
  showRightBtnMessage.value = false;
  delMessage(ids).then((res: Result) => {
    if (res.code !== 200) tips("error", res.msg);
    chatData.value = chatData.value.filter(
      (item: message) => !ids.includes(item.id)
    );
    if (chatData.value.length === 0)
      emit("sendCallBack", { val: truncate("消息被删除"), type: 0 });
    else {
      const lastData = chatData.value[chatData.value.length - 1];
      emit("sendCallBack", {
        val: truncate(lastData.type === 1 ? "[图片]" : lastData.message),
        type: lastData.type,
      });
    }
  });
};

// 复制聊天内容
const copyMessage = () => {
  if (nowCheckData.value.type === 1) {
    copyImage(nowCheckData.value.message).then((res) => {
      res ? tips("success", "图片已复制到粘贴板") : tips("error", "复制失败");
    });
    return;
  }
  const result = copyText(nowCheckData.value.message);
  result ? tips("success", "内容已复制到粘贴板") : tips("error", "复制失败");
};

// 多选
const moreCheckState = ref(false);
const moreChecked = () => {
  moreCheckState.value = true;
  moreCheckedCallBack(nowCheckData.value);
};
// 删除多选内容
const delMoreChecked = () => {
  moreCheckState.value = false;
  delMessageGo(saveChecked.value);
};
// 取消多选
const cancelMoreChecked = () => {
  moreCheckState.value = false;
  saveChecked.value = [];
  chatData.value.forEach((item: message) => {
    item.check = false;
  });
};
// 多选操作
const saveChecked = ref([] as number[]);
const moreCheckedCallBack = (data: message) => {
  if (!data.check) saveChecked.value.push(data.id);
  else saveChecked.value.splice(saveChecked.value.indexOf(data.id), 1);
  data.check = !data.check;
};

// 销毁监听
const clearListener = () => {
  const parentDiv = document.querySelector(".cb-body") as HTMLElement;
  if (parentDiv) parentDiv.removeEventListener("contextmenu", listenerMessage);
};

// 关闭右键
const closeRightBtn = () => {
  if (!tapAndHold.value) showRightBtnMessage.value = false;
};

// 关闭右键公共方法
const closeRightBtnCom = (state: boolean) => {
  const dom = document.getElementsByClassName("chat-main")[0];
  state
    ? dom.addEventListener("click", closeRightBtn)
    : dom.removeEventListener("click", closeRightBtn);
};

// ===================================组件初始化操作===================================//
// 初始化数据
const initData = () => {
  chatData.value = [];
  lodingMessage.value = false;
  page.value = 1;
  sendVal.value = "";
  moreCheckState.value = false;
  saveChecked.value = [];
};

// 选择用户做出改变
const nowChatUid = inject<(uid: number) => void>("getNowChatUid");
watch(
  () => props.user,
  () => {
    if (Object.keys(props.user).length === 0) return;
    initData();
    eqChatData();
    if (nowChatUid) nowChatUid(props.user.relationUid);
  }
);

const ws = ref(webSocketService);
watch(
  () => ws.value?.pushCount,
  () => {
    const data = ws.value.newMessage;
    if (
      Object.keys(props.user).length === 0 ||
      props.user.relationUid !== data.receiveUid
    )
      return;
    emit("sendCallBack", { val: truncate(data.message), type: data.type });
    if (data.state === 2) {
      let newData = chatData.value.filter(
        (item: message) => item.id === data.mid
      )[0];
      newData.sendState = 2;
      newData.message = "对方撤回了一条消息";
    } else {
      pushDataOneCom(
        data.mid,
        data.receiveUid,
        props.user.uid,
        data.type,
        data.message,
        1,
        data.state
      );
      scrollToButtom();
    }
  }
);

// 组件初开始和结束
onMounted(() => {
  closeRightBtnCom(true);
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
  listenerScrollToTop(false);
  if (nowChatUid) nowChatUid(-99);
});
</script>

<style lang="scss" scoped>
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
  content: "";
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
  content: "";
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
      color: white;
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
