import { defineStore } from "pinia";

export const useStore = defineStore("nh", {
  state: () => ({
    token: "" as string,
    not_tips: "false" as string,
    go_chat_u: -99,
    set_pw: "false" as string,
    base_url: "",
  }),
  actions: {
    clearToken(): void {
      this.token = "";
    },
    saveToken(token: string): void {
      this.token = token;
    },
    stopTips(): void {
      this.not_tips = "true";
    },
    setGoChat(uid: number) {
      this.go_chat_u = uid;
    },
    initGoChat() {
      this.go_chat_u = -99;
    },
    setPwd() {
      this.set_pw = "true";
    },
    initPwd() {
      this.set_pw = "false";
    },
    initBaseUrl(url: string) {
      this.base_url = url;
      console.log(this.base_url, "--------");
    },
  },
  persist: true,
});
