import { reactive } from 'vue';
export default class WebSocketService {
  private socket: WebSocket | null = null;
  private reconnectInterval = 1000; // 初始重连间隔时间（毫秒）
  private state = 0;
  public newMessage = reactive({
    uid: -1,
    value: ''
  });
  constructor(private params: string) {}

  connect(): void {
    this.socket = new WebSocket('ws://127.0.0.1:8087/socket.chat/' + this.params);
    this.socket.onopen = () => {
      this.state = this.socket?.readyState ?? 0;
      console.log('建立连接');
    };

    this.socket.onmessage = (event: MessageEvent) => {
      console.log('接收消息:', event.data);
      this.newMessage.uid = 0;
      this.newMessage.value = 'xxx';
    };

    this.socket.onclose = () => {
      this.state = this.socket?.readyState ?? 0;
      console.log('断开连接');
      this.reconnect();
    };
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
    if (this.socket) {
      this.socket.close();
    }
  }
}
