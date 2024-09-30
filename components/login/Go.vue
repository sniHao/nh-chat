<template>
  <div class="lb-body">
    <form>
      <div class="lb-tp-te">
        <input type="text" placeholder="请输入邮箱" v-model="loginfo.uid" @focus="onFocus('lb-tp-te')" @blur="onBluer('lb-tp-te')" required />
      </div>
      <div class="lb-tp-tepwd mt-18">
        <input type="password" placeholder="请输入密码" v-model="loginfo.upwd" @focus="onFocus('lb-tp-tepwd')" @blur="onBluer('lb-tp-tepwd')" required />
      </div>
      <span v-show="loginfo.msginfo" style="color: red" class="ft-13">{{ loginfo.msginfo }}</span>
      <!-- 登录按钮 -->
      <template v-if="loding === 0">
        <div class="lb-tp-go mt-30">
          <button @click.prevent="gologin" class="hover-pointer">登录</button>
        </div>
      </template>
      <template v-else>
        <div class="lb-tp-go">
          <button disabled>
            <svg class="icon icon-loding" aria-hidden="true">
              <use xlink:href="#icon-loding"></use>
            </svg>
            <span>登录中...</span>
          </button>
        </div>
      </template>
    </form>
    <!-- 忘记密码 -->
    <div class="flex-right flex-center mt-12">
      <n-button quaternary ghost @click="cuts()">立即注册</n-button>
      <div class="mb-2">|</div>
      <n-button quaternary ghost @click="forgot">忘记密码</n-button>
    </div>
  </div>
</template>

<script setup>
// import { reactive, toRefs, ref, defineEmits } from 'vue';
// import { onBluers, onFocu, closeLogin } from '@/hooks/UsLogin';
// import { useStore } from 'vuex';
// import { useRouter, useRoute } from 'vue-router';
// import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();
const store = useStore();
let loginfo = reactive({
  uid: '',
  upwd: '',
  msginfo: '',
  token: ''
});

let loding = ref(0);

let cut = ref(1);

// 传值给父组件(显示注册)
const emit = defineEmits(['cuts']);
const cuts = () => {
  cut.value = 3;
  //传递给父组件
  emit('cuts', cut);
};

//登录事件
async function gologin() {
  if (loginfo.uid && loginfo.upwd) {
    loding.value = 1;
    const res = store.dispatch('gologin', loginfo);
    //得到验证后的结果---登录成功
    res
      .then((result) => {
        if (result === 'ok') {
          store.dispatch('getUserInfo'); //获取用户数据
          //跳转到当前登录页面
          router.replace({ path: '/gojump', query: { go: route.fullPath } });
          location.reload(); //强刷
          closeLogin(); //关闭登录
          //消息框提示
          ElMessage({
            message: '登陆成功',
            type: 'success',
            duration: 1600
          });
        }
      })
      .finally(() => {
        loding.value = 0;
      });
  } else {
    loding.value = 0;
    loginfo.msginfo = '账号或密码不能为空！';
  }
}

//输入框获得焦点时
function onFocus(msg) {
  onFocu(msg);
}
//输入框失去焦点时
function onBluer(msg) {
  onBluers(msg);
}

//忘记
function forgot() {
  ElMessage.warning('鉴于安全,暂无法修改密码,建议使用验证码登录');
}
</script>

<style scoped lang="scss">
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
