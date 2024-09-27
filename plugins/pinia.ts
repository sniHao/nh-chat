import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
export default defineNuxtPlugin((nuxtApp: any) => {
  nuxtApp.$pinia.use(piniaPluginPersistedstate)
})
