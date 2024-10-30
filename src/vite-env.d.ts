import { AxiosRequestConfig } from "axios";

/* eslint-disable */
declare module "*.vue" {
  import type { DefineComponent } from "vue";
  const component: DefineComponent<{}, {}, any>;
  export default component;
}

declare module "*.vue" {
  import Vue from "vue";
  export default Vue;
}

declare module "axios" {
  interface AxiosInstance {
    (config: AxiosRequestConfig): Promise<any>;
  }
}

export {};
declare global {
  interface userInfo {
    uid: number;
    name: string;
    photo: string;
  }
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