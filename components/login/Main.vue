<template>
  <div class="lo">
    <!-- 登录框 -->
    <div class="login" id="loginx">
      <div class="login-main">
        <div class="login-close">
          <n-button @click="closeLo" quaternary ghost>
            <span>×</span>
          </n-button>
        </div>
        <!-- 内容(注册) -->
        <template v-if="cut == 3">
          <LogonGo @cuts="clickEven" />
        </template>
        <!-- 内容(登录) -->
        <template v-else>
          <div class="login-boby">
            <!-- 选择区 -->
            <div class="lb-title">
              <span @click="cut = 1" :class="{ tab_act: cut == 1 }">密码登录</span>
              <span @click="cut = 2" :class="{ tab_act: cut == 2 }">验证码登录</span>
            </div>
            <!-- 选择区 -->
            <template v-if="cut == 1">
              <LoginGo @cuts="clickEven" />
            </template>
            <template v-if="cut == 2">
              <LoginGoYzm />
            </template>
          </div>

          <!-- other登录方式 -->
          <!-- <LoginOther></LoginOther> -->
        </template>

        <div class="mt-30 flex-center-center flex-down">
          <!-- <div>注册人数</div> -->
          <n-divider><span>注册人数</span></n-divider>
          <div class="ft-b">0</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// import { watchWindowSize, closeLogin } from '@/hooks/UsLogin';
// import { onMounted, ref } from 'vue';
// import LoginGo from './LoginGo';
// import LoginGoYzm from './LoginGoYzm';
// import LoginOther from './LoginOther';
// import LogonGo from './LogonGo';

let cut = ref(1);

// 关闭登录
function closeLo() {
  cut.value = 1;
  closeLogin();
}

function cutValue() {}
// 接受操作传的值(控制回复的展示)
const clickEven = (val) => {
  cut.value = val.value;
};

onMounted(() => {
  // 监听登录窗口大小
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

/* 关闭设置 */
.login-close {
  position: absolute;
  top: -$px-42;
  right: $px-6;
}
.login-close span {
  font-size: $px-32;
  color: rgb(255, 255, 255);
}

/* 内容框 */
.login-main {
  position: relative;
  margin-top: $px-48;
  min-height: calc($px-480 - $px-48);
  background-color: rgb(255, 255, 255);
  border-bottom-left-radius: 1.5%;
  border-bottom-right-radius: 1.5%;
}
/* 登录体title */
.login-boby {
  height: 320px;
  padding: 18px 24px;
  box-sizing: border-box;
}
/*选择区*/
.lb-title {
  display: flex;
  align-items: center;
  justify-content: space-around;
  font-size: 20px;
  font-weight: bold;
  color: rgb(152, 154, 150);
  margin-bottom: 20px;
  height: 50px;
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
