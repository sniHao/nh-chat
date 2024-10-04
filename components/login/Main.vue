<template>
  <div class="lo">
    <div class="login" id="loginx">
      <div class="login-main">
        <div class="login-logo">nh-chat</div>
        <div class="login-close">
          <n-button @click="closeLo" quaternary ghost>
            <span>×</span>
          </n-button>
        </div>
        <div class="login-boby">
          <div class="lb-title">
            <span @click="cut = 1" :class="{ tab_act: cut == 1 }">密码登录</span>
            <span @click="cut = 2" :class="{ tab_act: cut == 2 }">验证码登录</span>
          </div>
          <LoginGo @cuts="clickEven" @showLogin="showLoginEvent" :cutUp="cut" />
        </div>
        <div class="mt-30 flex-center-center flex-down">
          <n-divider><span>体验人数</span></n-divider>
          <div class="ft-b">{{ count }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { eqUserCount } from '@/api/index';
const emit = defineEmits(['showLogin']);

const cut = ref(1);

// 关闭登录
const closeLo = () => {
  cut.value = 1;
  emit('showLogin', false);
};

// 接受操作传的值(控制回复的展示)
const clickEven = (val) => {
  cut.value = val.value;
};

// 登录框状态
const showLoginEvent = (show) => {
  emit('showLogin', show);
};

const count = ref(0);
onMounted(() => {
  eqUserCount().then((res) => {
    if (res.code === 200) count.value = res.data;
  });
});
</script>

<style scoped lang="scss">
:deep(.n-divider:not(.n-divider--vertical)) {
  margin-top: $px-12;
  margin-bottom: $px-4;
  color: #898989;
}
/* 遮罩层 */
.lo {
  z-index: 999;
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background-color: rgb(0, 0, 0, 0.5);
  /* display: none; */
}

/* 登录框 */
.login {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  min-height: $px-480;
  width: $px-400;
  background-color: $ft-color-hui-1;
  color: #898989;
  border-radius: 1.5%;
  animation: nih 1.3s;
}

@keyframes nih {
  from {
    left: 0;
    opacity: 0;
  }

  to {
    left: 50%;
    opacity: 1;
  }
}

.login-logo {
  position: absolute;
  top: -$px-38;
  font-size: $px-18;
  color: white;
  left: $px-20;
}

.login-close {
  position: absolute;
  top: -$px-42;
  right: $px-6;
}
.login-close span {
  font-size: $px-32;
  color: white;
}

/* 内容框 */
.login-main {
  position: relative;
  margin-top: $px-48;
  min-height: calc($px-480 - $px-48);
  background-color: white;
  border-bottom-left-radius: 1.5%;
  border-bottom-right-radius: 1.5%;
}
/* 登录体title */
.login-boby {
  height: $px-320;
  padding: $px-18 $px-24;
  box-sizing: border-box;
}
/*选择区*/
.lb-title {
  display: flex;
  align-items: center;
  justify-content: space-around;
  font-size: $px-20;
  font-weight: bold;
  color: rgb(152, 154, 150);
  margin-bottom: $px-20;
  height: $px-48;
}
/* 悬浮变鼠标样式 */
.lb-title span:hover {
  cursor: pointer;
}
/* 用于选择切换字体颜色 */
.tab_act {
  color: black;
}
</style>
