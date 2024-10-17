<template>
  <div class="main-nav flex-center-center">
    <div class="nav flex-center-zy pd-6">
      <div class="flex-center ft-b">
        <div class="ft-26 home-ft-color hover-pointer" @click="goHome" :class="isSmallWin ? 'ft-18' : ''">nh-chat</div>
        <div class="ml-26 ft-color-tips hover-ft" :class="isSmallWin ? 'is-small-style' : ''" @click="goGithub">演示</div>
        <div class="ml-16 ft-color-tips hover-ft" :class="isSmallWin ? 'is-small-style' : ''" @click="goDocs">说明文档</div>
      </div>
      <div class="flex-center">
        <div class="flex-center-center pd-6 hover-btn" @click="switchMode">
          <OfSvg :width="isSmallWin ? 20 : 26" :height="isSmallWin ? 20 : 26" name="system-dark" v-if="modeStyle"></OfSvg>
          <OfSvg :width="isSmallWin ? 20 : 26" :height="isSmallWin ? 20 : 26" name="system-light" v-else></OfSvg>
        </div>
        <div class="flex-center-center pd-6 hover-btn" @click="goGithub">
          <OfSvg :width="isSmallWin ? 22 : 28" :height="isSmallWin ? 22 : 28" viewBox="0 0 512 512" name="github"></OfSvg>
          <span class="ml-4 ft-color-tips ft-b">{{ githubStar }}</span>
        </div>
        <div class="flex-center-center pd-6 hover-btn" @click="goLogin" v-if="userInfo?.state">
          <span class="ft-color-tips ft-b">登录 | 注册</span>
        </div>
        <template v-else>
          <n-dropdown trigger="click" :options="options" @select="handleSelect">
            <div class="user-head flex-center-center user-head-nav ml-12 hover-pointer" :style="'background-color:' + tranColor(initData.uInfo.photo)">
              {{ initData.uInfo.photo }}
            </div>
          </n-dropdown>
        </template>
      </div>
    </div>
    <!-- 弹框 -->
    <n-modal v-model:show="showSetPwd">
      <n-card style="width: 600px" :bordered="false" size="huge" role="dialog" aria-modal="true">
        <template v-if="!canUp">
          <span>抱歉，您已设置了密码，目前暂不支持修改</span>
        </template>
        <template v-else>
          <div class="flex-down-center">
            <n-input maxlength="14" show-count clearable v-model:value="pwd" placeholder="长度为6-14位，不那么简单的密码" />
          </div>
          <div class="flex-right mt-14">
            <n-button type="tertiary" class="ml-10" @click="cancelPwd">取消</n-button>
            <n-button type="primary" @click="setPwd">保存</n-button>
          </div>
        </template>
      </n-card>
    </n-modal>
    <n-modal v-model:show="showSetBasic">
      <n-card style="width: 600px" :bordered="false" size="huge" role="dialog" aria-modal="true">
        <div class="flex-down-center">
          <div class="user-head flex-center-center mb-10" :style="'background-color:' + tranColor(newName ?? '')">{{ newName.substring(0, 1) ?? '' }}</div>
          <n-input maxlength="10" show-count clearable v-model:value="newName" placeholder="取个秀霸天的名字" />
        </div>
        <div class="flex-right mt-14">
          <n-button type="tertiary" class="ml-10" @click="cancelBasic">取消</n-button>
          <n-button type="primary" @click="setBasic">保存</n-button>
        </div>
      </n-card>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { logout, upBasic, adPassWord, hasPwd } from '~/api/index';
const router = useRouter();
const store = useStore();
const emit = defineEmits(['showLogin']);
const isSmallWin = inject('isSmallWin');
const userInfo = inject<Ref<{ state: boolean; uInfo: { photo: string; name: string } }>>('userInfo');

const options = ref([
  {
    label: '编辑资料',
    key: () => (showSetBasic.value = true)
  },
  {
    label: '设置密码',
    key: () => goUpPwd()
  },
  {
    label: '退出登录',
    key: () => goLogout()
  }
]);

const handleSelect = (key: string | number, option: any) => {
  option.key();
};

// 编辑昵称
const newName = ref('');
const showSetBasic = ref(false);
const setBasic = () => {
  let name = newName.value.trim();
  if (name.length > 10 || name.length === 0) return tips('error', '请输入正确的昵称');
  upBasic(name).then((res) => {
    if (res.code !== 200) return tips('error', res.msg);
    showSetBasic.value = false;
    initData.value.uInfo.name = name;
    initData.value.uInfo.photo = name.substring(0, 1);
    newName.value = name;
    tips('success', res.msg);
  });
};
const cancelBasic = () => {
  showSetBasic.value = false;
  newName.value = initData.value.uInfo.name;
};

// 编辑密码
const canUp = ref(false);
const goUpPwd = () => {
  hasPwd().then((res: Result) => {
    showSetPwd.value = true;
    if (res.code !== 200 || !res.data) return (canUp.value = false);
    canUp.value = true;
  });
};
const pwd = ref('');
const showSetPwd = ref(false);
const setPwd = () => {
  const regex = /^(?=\S)(?!.*\s)[A-Za-z0-9!@#$%^&*()_+]{6,14}$/;
  if (!regex.test(pwd.value)) return tips('error', '密码格式错误');
  adPassWord(pwd.value).then((res: Result) => {
    if (res.code !== 200) return tips('error', res.msg);
    showSetPwd.value = false;
    pwd.value = '';
    canUp.value = false;
    tips('success', res.msg);
  });
};
const cancelPwd = () => {
  showSetPwd.value = false;
  pwd.value = '';
};

// 退出登录
const goLogout = () => {
  logout().then((res) => {
    if (res.code !== 200) return tips('error', res.msg);
    store.clearToken();
    tips('success', res.msg);
    setTimeout(() => {
      location.reload();
    }, 500);
  });
};

// 跳首页
const goHome = () => {
  router.push('/');
};

// 演示
const goGithub = () => {
  window.open('https://github.com/sniHao/nh-chat', '_blank');
};

// 说明文档
const goDocs = () => {
  router.push('/docs');
};

// 登录
const goLogin = () => {
  emit('showLogin', { win: true, state: false });
};

// 切换主题
const modeStyle = ref(true);
const switchMode = () => {
  modeStyle.value = !modeStyle.value;
};

const githubStar = ref(0);
const getStar = () => {
  // https://api.github.com/repos/sniHao/nh-chat
};

const initData = ref(userInfo as unknown as { state: boolean; uInfo: { name: string; photo: string } });
onMounted(() => {
  getStar();
  newName.value = initData?.value.uInfo.name ?? '';
});
</script>

<style lang="scss" scoped>
.is-small-style {
  font-size: $px-13 !important;
  margin-left: $px-10 !important;
}
.main-nav {
  position: fixed;
  top: 0;
  height: $px-64;
  width: calc(100vw - 0.4rem); //减去滚动条宽度
  background-color: #0f172a99;
  backdrop-filter: blur($px-4);
  z-index: 9;
  box-shadow: 0 $px-2 $px-5 rgba(0, 0, 0, 0.2);
}
.nav {
  width: $px-1320;
}
.user-head-nav {
  width: $px-34;
  height: $px-34;
}
</style>
