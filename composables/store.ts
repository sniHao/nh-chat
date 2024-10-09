import { defineStore } from 'pinia';

export const useStore = defineStore('nh', {
  state: () => {
    return {
      token: '' as string,
      not_tips: 'false' as string
    };
  },
  actions: {
    saveToken(token: string): void {
      this.token = token;
    },
    stopTips(): void {
      this.not_tips = 'true';
    }
  },
  persist: process.client && {
    storage: localStorage
  }
});
