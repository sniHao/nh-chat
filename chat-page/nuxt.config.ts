// https://nuxt.com/docs/api/configuration/nuxt-config
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { NaiveUiResolver } from "unplugin-vue-components/resolvers";

export default defineNuxtConfig({
  modules: ["nuxtjs-naive-ui", "@pinia/nuxt"],
  compatibilityDate: "2024-04-03",
  $production: {
    routeRules: {
      "/**": { isr: true },
    },
  },
  devtools: { enabled: true },
  css: ["~/assets/css/nh.scss", "~/assets/css/index.scss"],
  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@use "@/assets/css/constant.scss" as *;',
        },
      },
    },
    plugins: [
      AutoImport({
        imports: [
          {
            "naive-ui": [
              "useDialog",
              "useMessage",
              "useNotification",
              "useLoadingBar",
            ],
          },
          "vue",
          "vue-router",
        ],
      }),
      Components({
        resolvers: [NaiveUiResolver()],
      }),
    ],
  },
});
