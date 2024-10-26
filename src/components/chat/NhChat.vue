<template>
  <Main></Main>
</template>

<script setup lang="ts">
import WebSocketService from "@/utils/WebSocketService";
import { createDiscreteApi } from "naive-ui";
import { userInfo, eqUserBasics } from "@/api/index";
const { notification } = createDiscreteApi(["notification"]);
import { useRouter } from "vue-router";
import { useStore } from "@/store";
import { tips } from "@/utils/OtherUtils";
import Main from "@/components/chat/Main.vue";
const router = useRouter();
const store = useStore();

const props = defineProps({
  baseUrl: {
    type: String,
    default: "http://localhost:8087",
  },
  inputTips: {
    type: String,
    default: "输入Ta的账号-发起聊天",
  },
  style: {
    type: Object,
    default: () => ({ width: "60rem", height: "40.625rem", theme: "dark" }),
  },
  system: {
    type: Object,
    default: () => ({}),
  },
  searchUserResult: {
    type: Array, //uid name photo
    default: [],
  },
});
provide("param", props);

//==============回调=============//
// 搜索用户
// const searchUser = (val: string) => {};

// ws状态全局
const ws = new WebSocketService(store.token);
provide("webSocketService", ws);

// 获取当前聊天的uid
const nowUid = ref(-1);
const getNowChatUid = (uid: number) => {
  nowUid.value = uid;
};
provide("getNowChatUid", getNowChatUid);

watch(
  () => ws.pushCount,
  () => {
    if (nowUid.value !== ws.newMessage.receiveUid) ofNewMessage(ws.newMessage);
  },
  {
    deep: true,
  }
);
// 新消息处理
let saveInfo = ref([] as any);
const ofNewMessage = (msg: any) => {
  if (msg.message.length > 30)
    msg.message = msg.message.substring(0, 30) + "...";
  if (msg.type === 1) msg.message = "[图片]";
  eqUserBasics(msg.receiveUid).then((res: Result) => {
    let name = res.code === 200 ? res.data.name : "未知用户";
    if (saveInfo.value.length > 3) {
      saveInfo.value.shift();
      saveInfo.value[0].destroy();
    }
    const n = notification["info"]({
      content: "你收到了一条来自 " + name + " 的消息",
      meta: () =>
        h("div", null, [
          msg.message,
          h(
            "span",
            {
              onClick: (event) => {
                event.stopPropagation();
                n.destroy();
                goChat(msg.receiveUid);
              },
              class: "tips-link",
            },
            "查看详情"
          ),
        ]),
      duration: 3500,
      keepAliveOnHover: true,
    });
    saveInfo.value.push(n);
  });
};
provide("ofNewMessage", ofNewMessage);

// 查看消息详情
const goChat = (id: number) => {
  store.setGoChat(id);
  router.push("/try");
};

// 窗口大小变化
let isSmallWin = ref(false);
const resize = () => {
  isSmallWin.value = window.screen.width < 510 || window.innerWidth < 510;
};
provide("isSmallWin", isSmallWin);

// 用户数据
const uInfo = ref({} as any);
const eqUser = () => {
  userInfo().then((res) => {
    if (res.code !== 200) return tips("error", res.msg);
    uInfo.value = res.data;
  });
};
provide("userInfo", uInfo);

// 初始化组件
const initModule = () => {
  store.initBaseUrl(props.baseUrl);
  store.base_url = props.baseUrl;
};

onMounted(() => {
  initModule();
  ws.connect();
  resize();
  eqUser();
  window.addEventListener("resize", resize);
});
onUnmounted(() => {
  ws.close();
  window.removeEventListener("resize", resize);
});
</script>
<style lang="scss" scoped>
.tips-link {
  margin-left: $px-6;
  color: #9300ff;
  font-weight: 600;
  cursor: pointer;
}
.main-body {
  height: 100vh;
}
</style>
