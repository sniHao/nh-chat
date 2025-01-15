<template>
  <div class='flex-center-center w-100 h-100-vh'>
    <NhChat></NhChat>
  </div>
</template>
<script setup lang='ts'>
const searchUserResult = ref([]);
const searchUser = (val: string) => {
  searchUserResult.value = [];
};

const userInfos = ref({} as userInfo);
</script>
<style lang='scss' scoped></style>
