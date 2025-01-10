import { createApp } from "vue";
import App from "./App.vue";
import router from "@/router/index";
import {install} from "@/plugin";

const app = createApp(App);
app.use(install);
app.use(router);
app.mount("#app");
