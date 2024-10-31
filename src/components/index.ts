import { App } from "vue";
import NhChat from "./chat/NhChat.vue";

export default {
  install(app: App) {
    app.component("NhChat", NhChat);
    // 如果还有更多的组件需要注册，可以在这里继续添加
  },
};

/**
 * 这里将组件导出，是为了在单独使用组件时，可以按需引入
 * 为了将来支持类似这样按需使用 import {Vv3Test} from "vite-vue3-ts-test-fjc";
 * 有多少个组件就添加多少个组件
 */
export { NhChat };

/**
 * 这里是重点，需要将这些组件在ts中声明为全局组件；
 */
declare module "vue" {
  export interface GlobalComponents {
    NhChat: typeof NhChat;
  }
}
