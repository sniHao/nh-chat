import { reactive, ref } from 'vue';
export default class WebSocketService {
  private socket: WebSocket | null = null;
  private reconnectInterval = 1000; // 初始重连间隔时间（毫秒）
  private state = 0;
  public newMessage = reactive({
    mid: -1,
    receiveUid: -1,
    message: '',
    state: 0,
    type: 0
  });
  public pushCount = ref(0);
  constructor(private params: string) {}

  connect(): void {
    this.socket = new WebSocket('wss://www.notes-something.fun/wss/socket.chat/' + this.params);
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
  }
}
