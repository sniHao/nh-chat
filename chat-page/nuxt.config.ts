// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-04-03",
  $production: {
    routeRules: {
      '/**': { isr: true }
    }
  },
  devtools: { enabled: true },
  css: ['~/assets/css/nh.scss','~/assets/css/index.scss'],
  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@use "@/assets/css/constant.scss" as *;',
        },
      },
    },
  },
});
