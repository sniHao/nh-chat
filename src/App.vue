<template>
  <NhChat
    inputTips="输入Ta的账号-发起聊天"
    :searchUserResult="searchUserResult"
    :userInfo="userInfos"
    eqUserInfo="https://www.notes-something.fun/nh/user/eqUserBasics"
    baseUrl="https://www.notes-something.fun/nh"
    @searchUser="searchUser"
  ></NhChat>
</template>
<script setup lang="ts">
import { eqUserMail, userInfo } from "@/api/index";
import { tips } from "@/utils/OtherUtils";
import { onMounted } from "vue";

const searchUserResult = ref([]);
const searchUser = (val: string) => {
  searchUserResult.value = [];
  eqUserMail(val).then((res) => {
    if (res.code !== 200) return tips("error", res.msg);
    searchUserResult.value = [res.data];
  });
};

const userInfos = ref({} as userInfo);
const eqUserInfos = () => {
  userInfo().then((res) => {
    userInfos.value = res.data;
  });
};

onMounted(() => {
  eqUserInfos();
});
</script>
<style lang="scss" scoped></style>
