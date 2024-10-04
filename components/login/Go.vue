<template>
  <div class="lb-body">
    <div class="lb-tp-te">
      <input type="text" placeholder="请输入邮箱" v-model="data.mail" required />
    </div>
    <div class="lb-tp-tepwd mt-18" v-if="cutUp == 1">
      <input type="password" placeholder="请输入密码" v-model="data.password" required />
    </div>
    <!-- 验证码 -->
    <div class="lb-tp-tepwd flex-center mt-18" v-if="cutUp == 2">
      <input type="number" placeholder="验证码" v-model="data.code" required style="width: 62%" />
      <n-button class="btn-w" v-if="codeState == 1" type="text" @click="getCode">获取验证码</n-button>
      <n-button class="btn-w" v-if="codeState == 2" type="text" disabled>{{ countDown }}秒</n-button>
    </div>
    <template v-if="loding === 0">
      <div class="lb-tp-go mt-30">
        <button @click.prevent="gologin" class="hover-pointer">登录</button>
      </div>
    </template>
    <template v-else>
      <div class="lb-tp-go mt-30">
        <button disabled>
          <span>登录中...</span>
        </button>
      </div>
    </template>
    <div class="flex-right flex-center mt-12">
      <n-button quaternary ghost @click="forgot">忘记密码</n-button>
    </div>
  </div>
</template>

<script setup>
import { login, welcome, welcomeCode } from '@/api/index';
const store = useStore();

const props = defineProps({
  cutUp: {
    type: Number,
    default: 1
  }
});

const data = reactive({
  mail: '',
  password: '',
  code: ''
});
const codeState = ref(1);

const countDown = ref(0);
const getCode = () => {
  if (!isEmail(data.mail)) return tips('error', '请输入正确的邮箱格式📫');
  welcomeCode(data.mail).then((res) => {
    if (res.code !== 200) return tips('error', res.msg);
    tips('success', res.msg);
    codeState.value = 2;
    countDown.value = 60;
    let times = setInterval(() => {
      countDown.value--;
      if (countDown.value <= 0) {
        codeState.value = 1;
        clearInterval(times);
      }
    }, 1000);
  });
};

let cut = ref(1);
const emit = defineEmits(['cuts', 'showLogin']);
const cuts = () => {
  cut.value = 3;
  emit('cuts', cut);
};

//登录事件
let loding = ref(0);
const gologin = () => {
  if (!isEmail(data.mail)) return tips('error', '请输入正确的邮箱格式📫');
  if (props.cutUp === 1) {
    if (data.password.trim().length === 0) return;
    if (data.password.length < 6) return tips('error', '密码不太对哦👾');
    loginPwd();
    return;
  }
  if (data.code.toString().trim().length === 0) return;
  loginCode();
};

const loginPwd = () => {
  loding.value = 1;
  login(data).then((res) => {
    loginResultCom(res);
  });
};
const loginCode = () => {
  welcome(data).then((res) => {
    loginResultCom(res);
  });
};
const loginResultCom = (res) => {
  loding.value = 0;
  if (res.code !== 200) return tips('error', res.msg);
  store.saveToken(res.data);
  emit('showLogin', false);
};

const forgot = () => {
  tips('warning', '暂无法修改密码，建议使用验证码登录');
};
</script>

<style scoped lang="scss">
.btn-w {
  width: $px-96;
}
/* 登录体title */
.login-boby {
  height: $px-320;
  padding: $px-18 $px-24;
  box-sizing: border-box;
}

/* 登录体 */
.lb-body {
  height: $px-240;
  color: #898989;
}
/* 账号 */
.lb-tp-te,
.lb-tp-tepwd {
  height: $px-40;
  background-color: rgb(237, 237, 237);
  border-radius: $px-8;
}
.lb-tp-te input,
.lb-tp-tepwd input {
  display: block;
  width: 90%;
  height: $px-38;
  background-color: rgb(237, 237, 237);
  outline: none;
  border: none;
  margin: 0 auto;
}

/* 登录按钮 */
.lb-tp-go {
  height: $px-38;
  background-color: $ft-color-hui-1;
  border-radius: $px-8;
}
.lb-tp-go button {
  display: block;
  width: 100%;
  height: 100%;
  background-color: $ft-color-hui-1;
  border-radius: $px-8;
  border-color: $ft-color;
  color: white;
}

.icon-loding {
  fill: white;
  width: $px-18;
  height: $px-18;
  vertical-align: bottom;
  animation: loding 0.8s infinite;
}

@keyframes loding {
  0% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(90deg);
  }
  50% {
    transform: rotate(180deg);
  }
  75% {
    transform: rotate(270deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
