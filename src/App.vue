<template>
  <NhChat
    inputTips="输入Ta的账号-发起聊天"
    @searchUser="searchUser"
    :searchUserResult="searchUserResult"
    baseUrl="http://localhost:34560"
  ></NhChat>
</template>

<script setup lang="ts">
import { eqUserMail } from "@/api/index";
import { tips } from "@/utils/OtherUtils";

const searchUserResult = ref([]);
const searchUser = (val: string) => {
  searchUserResult.value = [];
  eqUserMail(val).then((res) => {
    if (res.code !== 200) return tips("error", res.msg);
    searchUserResult.value = [res.data];
  });
};
</script>
<style lang="scss" scoped></style>
