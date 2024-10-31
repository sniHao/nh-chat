import { defineStore } from "pinia";

interface State {
  token: string;
  not_tips: string;
  go_chat_u: number;
  set_pw: string;
  base_url: string;
}
export const useStore = defineStore("nh", {
  state: (): State => ({
    token: "",
    not_tips: "false",
    go_chat_u: -99,
    set_pw: "false",
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
    },
  },
  persist: true,
});
