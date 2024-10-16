import { defineStore } from 'pinia';

export const useStore = defineStore('nh', {
  state: () => {
    return {
      token: '' as string,
      not_tips: 'false' as string,
      go_chat_u: -99
    };
  },
  actions: {
    saveToken(token: string): void {
      this.token = token;
    },
    stopTips(): void {
      this.not_tips = 'true';
    },
    setGoChat(uid: number) {
      this.go_chat_u = uid;
    },
    initGoChat() {
      this.go_chat_u = -99;
    }
  },
  persist: process.client && {
    storage: localStorage
  }
});
