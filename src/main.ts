import { createApp } from "vue";
import "@/assets/css/nh.scss";
import "@/assets/css/index.scss";
import App from "./App.vue";
//引入router
import router from "@/router/index";

import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App);
app.use(pinia);

// 使用路由
app.use(router);

// 挂载app
app.mount("#app");
