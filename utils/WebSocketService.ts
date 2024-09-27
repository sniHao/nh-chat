import { reactive } from 'vue';
export default class WebSocketService {
  private socket: WebSocket | null = null;

  public newMessage = reactive({
    uid: -1,
    value: ''
  });
  constructor(private url: string) {}

  connect(): void {
    this.socket = new WebSocket(this.url);

    this.socket.onopen = () => {
      console.log('建立连接');
    };

    this.socket.onmessage = (event: MessageEvent) => {
      console.log('接收消息:', event.data);
      this.newMessage.uid = 0;
      this.newMessage.value = 'xxx';
    };

    this.socket.onclose = () => {
      console.log('断开连接');
    };
  }

  // 关闭连接
  close(): void {
    if (this.socket) {
      this.socket.close();
    }
  }
}
