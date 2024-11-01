<template>
  <div class="body w-100 h-100 flex-center-onely">
    <client-only>
      <NhChat
        inputTips="输入Ta的邮箱-发起聊天"
        :searchUserResult="searchUserResult"
        baseUrl="https://www.notes-something.fun/nh"
        :userInfo="userInfo.uInfo"
        eqUserInfo="https://www.notes-something.fun/nh/user/eqUserBasics"
        socketUrl="wss://www.notes-something.fun/wss/socket.chat/"
        :token="token"
        chatRoute="/try"
        :messageFlicker="true"
        :experienceMode="true"
        @searchUser="searchUser"></NhChat>
    </client-only>
  </div>
</template>

<script setup lang="ts">
import { NhChat } from 'nh-chat';
import 'nh-chat/dist/assets/nh-style.css';
import { notes } from '~/api/index';
import { eqUserMail } from '~/api/index';
const userInfo = inject<Ref<any>>('userInfo');
const store = useStore();
const token = ref(store.token);
onMounted(() => {
  notes();
});

const searchUserResult = ref([] as any);
const searchUser = (val: string) => {
  searchUserResult.value = [];
  if (!isEmail(val)) return tips('error', '请输入正确的邮箱格式📫');
  eqUserMail(val).then((res) => {
    if (res.code !== 200) return tips('error', res.msg);
    searchUserResult.value = [res.data];
  });
};
</script>

<style lang="scss" scoped>
.body {
  padding: $px-96 $px-32;
  box-sizing: border-box;
}
</style>
