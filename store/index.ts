import { defineStore } from "pinia";

export const useHomeStore = defineStore("home", {
  state: () => {
    return {
      token: "" as string,
    };
  },
  getters: {},
  actions: {
    saveToken(token: string): void {
      this.token = token;
      localStorage.setItem("token", token);
    },
  },
});
