<template>
  <NhChat
    inputTips="输入Ta的账号-发起聊天"
    :searchUserResult="searchUserResult"
    eqUserInfo="https://www.notes-something.fun/nh/user/eqUserBasics"
    baseUrl="https://www.notes-something.fun/nh"
    socketUrl="ws://localhost:8087/socket.chat/"
    token=""
    :messageFlicker="false"
    @searchUser="searchUser"
  ></NhChat>
</template>
<script setup lang="ts">

const searchUserResult = ref([]);
const searchUser = (val: string) => {
  searchUserResult.value = [];
};

const userInfos = ref({} as userInfo);
</script>
<style lang="scss" scoped></style>
