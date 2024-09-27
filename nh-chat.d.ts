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
}

import { AxiosRequestConfig } from 'axios';
declare module 'axios' {
  interface AxiosInstance {
    (config: AxiosRequestConfig): Promise<any>;
  }
}
