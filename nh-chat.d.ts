export {};
declare global {
  interface Result {
    code: number;
    data: any;
    msg: string;
  }
  interface Relation {
    id: number;
    uid: number;
    relationUid: number;
    lastMessage: string;
    notRead: number;
    top: number;
    lastMessageDate: string;
    name: string;
    photo: string;
  }
  interface message {
    id: number;
    sendUid: number | undefined;
    receiveUid: number;
    type: number;
    sendState: number;
    receiveState: number;
    date: string;
    message: string;
    tab: boolean;
    state: number;
    check: boolean;
  }
}

import { AxiosRequestConfig } from 'axios';
declare module 'axios' {
  interface AxiosInstance {
    (config: AxiosRequestConfig): Promise<any>;
  }
}
