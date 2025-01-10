import { App, ref, watch, h, reactive } from 'vue';
import { setUser } from './utils/OtherUtils';
import { createDiscreteApi } from 'naive-ui';

const { notification } = createDiscreteApi(['notification']);

const ws = ref<WebSocketService | null>(null);
const eqUserInfo = ref('');
const nowUid = ref(-1);
const saveInfo = ref([] as any);
const goLink = ref('');

const useWebSocketService = (data: { url: string, eqUserInfoUrl: string, goLinkUrl: string }) => {
  ws.value = new WebSocketService(data.url);
  eqUserInfo.value = data.eqUserInfoUrl ?? '';
  goLink.value = data.goLinkUrl ?? '';
  vue.value.provide('eqUserInfoUrl', eqUserInfo.value);
  vue.value.provide('webSocketService', ws.value);
  watch(
    () => ws.value!.pushCount,
    () => {
      if (ws.value && nowUid.value !== ws.value.newMessage.receiveUid) {
        ofNewMessage(ws.value.newMessage);
      }
    },
    {
      deep: true
    }
  );
};

const useCloseWebSocketService = () => {
  if (ws.value) ws.value.close();
};

// 获取当前聊天的uid
const getNowChatUid = (uid: number) => {
  nowUid.value = uid;
};

// 查看消息详情
const goChat = (id: number) => {
  if (!id.toString()) return;
  sessionStorage.setItem('go_chat_uid', id.toString());
  window.location.href = goLink.value;
};

const modifyKey = (obj: { [key: string]: any }, oldKey: string, newKey: string) => {
  const { [oldKey]: value, ...rest } = obj;
  return { [newKey]: value, ...rest };
};

// 设置标签标题闪烁效果
let originalTitle:string;
let blinkInterval: number;
let stopBlinkTimeout: number;

const startBlinking = () => {
  if (typeof document !== 'undefined') {
    originalTitle = document.title;
    let isOriginalTitle = true;
    blinkInterval = window.setInterval(() => {
      document.title = isOriginalTitle ? "新消息来啦" : originalTitle;
      isOriginalTitle = !isOriginalTitle;
    }, 500);

    // 2秒后停止闪烁
    stopBlinkTimeout = window.setTimeout(() => {
      stopBlinking();
    }, 2000);
  }
};

const stopBlinking = () => {
  if (typeof document !== 'undefined') {
    clearInterval(blinkInterval);
    clearTimeout(stopBlinkTimeout);
    document.title = originalTitle;
  }
};

// 假设这是你的初始化代码
if (typeof window !== 'undefined') {
  window.addEventListener('beforeunload', stopBlinking);
}

// 新消息处理
const ofNewMessage = (msg: any) => {
  if (msg.mid === -1) return;
  msg = modifyKey(msg, 'receiveUid', 'relationUid');
  if (msg.message.length > 30) msg.message = msg.message.substring(0, 30) + '...';
  if (msg.type === 1) msg.message = '[图片]';
  startBlinking();
  setUser([msg], eqUserInfo.value).then((res: any) => {
    let name = res[0].name;
    if (saveInfo.value.length > 3) {
      saveInfo.value.shift();
      saveInfo.value[0].destroy();
    }
    const n = notification['info']({
      content: '你收到了一条来自 ' + name + ' 的消息',
      meta: () =>
        h('div', null, [
          msg.message,
          h(
            'span',
            {
              onClick: (event) => {
                event.stopPropagation();
                n.destroy();
                goChat(msg.relationUid);
              },
              class: 'tips-link'
            },
            '查看详情'
          )
        ]),
      duration: 2500,
      keepAliveOnHover: true
    });
    saveInfo.value.push(n);
  });
};

const vue = ref();
const install = (app: App) => {
  vue.value = app;
  app.provide('ws', useWebSocketService);
  app.provide('closeWebSocketService', useCloseWebSocketService);
  app.provide('getNowChatUid', getNowChatUid);
  app.provide('ofNewMessage', ofNewMessage);
};

class WebSocketService {
  private socket: WebSocket | null = null;
  private reconnectInterval = 1000; // 初始重连间隔时间（毫秒）
  private state = 0;
  public newMessage = reactive({
    mid: -1,
    receiveUid: -1,
    message: '',
    state: 0,
    type: 0,
    pushUserState: {
      uid: -1,
      state: 0
    }
  });
  public pushCount = ref(0);

  constructor(private params: string) {
    this.connect();
  }

  connect(): void {
    this.socket = new WebSocket(this.params);
    this.socket.onopen = () => {
      this.state = this.socket?.readyState ?? 0;
    };
    this.socket.onmessage = (event: MessageEvent) => {
      this.newMessage = JSON.parse(event.data);
      this.pushCount.value++;
    };
    this.socket.onclose = () => {
      this.state = this.socket?.readyState ?? 0;
      this.reconnect();
    };
  }

  send(id: string) {
    if (this.socket) this.socket.send(id);
  }

  reconnect() {
    if (this.reconnectInterval === 30000 || this.state === 1) return;
    setTimeout(() => {
      this.connect();
      this.reconnectInterval *= 2;
      if (this.reconnectInterval > 30000) this.reconnectInterval = 30000;
    }, this.reconnectInterval);
  }

  // 关闭连接
  close(): void {
    if (this.socket) this.socket.close();
  };
}

export { WebSocketService, install };