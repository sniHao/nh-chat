import { createApp } from "vue";
import "@/assets/css/nh.scss";
import "@/assets/css/index.scss";
import App from "./App.vue";
//引入router
import router from "@/router/index";

const app = createApp(App);

// 使用路由
app.use(router);

// 挂载app
app.mount("#app");
