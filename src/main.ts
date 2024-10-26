import { createApp } from "vue";
import "@/assets/css/nh.scss";
import "@/assets/css/index.scss";
import App from "./App.vue";
// 引入Axios
import axios from "axios";
//引入router
import router from "@/router/index";

import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// import pinia from "@/store/store";
const app = createApp(App);
app.use(pinia);

// 全局Axios
app.config.globalProperties.$axios = axios;
// 使用路由
app.use(router);

// 挂载app
app.mount("#app");
