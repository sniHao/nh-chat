import NhChat from "./chat/NhChat.vue";

import { App } from "vue";
const comps = [NhChat];
const install = (Vue: App) => {
  comps.map((component: any) => {
    Vue.component(component.__name as string, component);
  });
};
const windowObj = window as any;

if (typeof windowObj !== "undefined" && windowObj.Vue) {
  install(windowObj.Vue);
}

export default install;
