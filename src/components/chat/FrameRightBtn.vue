<template>
  <RightButton v-if='showRightBtnMessage' :left='rightBtnLeft' :top='rightBtnTop' :list='czList'
               :style='`color:${param.style.fontColor}`'
               :bgColor='computedStyle.mainColorOpt'
               @choose='chooseRight'></RightButton>
  <!-- 弹框 -->
  <n-modal v-model:show='showTransmit'>
    <n-card style='width: 400px; max-height: 480px;--n-border-radius:10px' :bordered='false' size='huge' role='dialog'
            aria-modal='true'
            class='over-auto'>
      <template v-if='listUser.length === 0'>
        <div class='flex-down-center'>
          <Svg :width='64' :height='64' name='no-user' viewBox='0 0 1139 1024'></Svg>
          <span class='ft-16 ft-color-tips mt-12'>通讯录空空的</span>
        </div>
      </template>
      <div class='flex-center-zy mb-12' v-else v-for='(item, index) in listUser' :key='index'
           :class="item?.ok?'choose-transmit':''">
        <div class="flex-center w-70">
          <div class='user-head flex-center-center' :style="'background-color:' + tranColor(item.photo)"
               v-html='computePhoto(item.photo)'></div>
          <div class='w-70 ml-12 ft-over' :title=" item.name ">{{ item.name }}</div>
        </div>
        <n-button v-if="!item?.ok" round strong type='primary' :color='param.style.leftChatBgColor'
                  @click='transmitMessage(item)'>选 择
        </n-button>
        <n-button v-else round strong type='primary' :color='computedStyle.leftChatColorOpt'
                  @click="closeTransmit(item)">
          取 消
        </n-button>
      </div>
      <div class="flex-center mt-30">
        <n-input round placeholder="顺带捎句话" class="mr-30" type="textarea" v-model:value='withVal' :autosize="{
        minRows: 1,
        maxRows: 3,
      }"/>
        <n-button round strong type='primary' :disabled="checkedUser.length === 0"
                  :color='param.style.rightChatBgColor' @click="sendMessageTransmit">发 送
        </n-button>
      </div>
    </n-card>
  </n-modal>
</template>

<script setup lang='ts'>
import {countTimeDiff, getTimeFormat} from '@/utils/TimeUtil';
import {tips, copyImage, copyText, tranColor, computePhoto} from '@/utils/OtherUtils';
import RightButton from '../of/RightButton.vue';

const param = inject<Ref<chatProps>>('param') as chatProps | any;
const computedStyle = inject<Ref<any>>('computedStyle') as any;

const props = defineProps({
  needListener: {
    type: Boolean,
    default: true
  },
  chatData: {
    type: Array<message>,
    default: () => []
  },
  user: {
    type: Object,
    default: {}
  },
  moreCheckState: {
    type: Boolean,
    default: false
  },
  userList: {
    type: Array,
    default: [] as Relation[]
  }
});
const emit = defineEmits(['isActionEmit', 'isQuoteEmit', 'delMessageEmit', 'moreCheckStateEmit',
  'nowCheckDataEmit', 'revocationMessageEmit', 'sendMessageTransmitEmit']);

watch(() => props.needListener, () => {
  if (props.needListener) addListener();
});

const listUser = ref()
watch(() => props.userList, () => {
  listUser.value = props.userList;
}, {deep: true});
// ===================================右键操作===================================//
const tapAndHold = ref(false);
const addListener = () => {
  tapAndHold.value = false;
  const parentDiv = document.querySelector('.cb-body') as HTMLElement;
  parentDiv.addEventListener('contextmenu', listenerMessage);
  let longPressTimer: NodeJS.Timeout;
  parentDiv.addEventListener('mousedown', (e: MouseEvent) => {
    longPressTimer = setTimeout(() => {
      listenerMessage(e);
      tapAndHold.value = true;
      setTimeout(() => {
        tapAndHold.value = false;
      }, 500);
    }, 500);
  });
  parentDiv.addEventListener('mouseup', () => {
    clearTimeout(longPressTimer);
  });
  parentDiv.addEventListener('mouseleave', () => {
    clearTimeout(longPressTimer);
  });
};
// 右键事件
const rightBtnLeft = ref(0);
const rightBtnTop = ref(0);
const czList = ref();
const showRightBtnMessage = ref(false);
const nowCheckData = ref({} as message);
const listenerMessage = (e: MouseEvent) => {
  e.preventDefault();
  const parentDiv = document.querySelector('.cb-body-sc');
  if (!parentDiv) return;
  if (props.moreCheckState) return;
  let target = e.target as HTMLElement;
  let index = -1;
  if (target.classList.contains('cbbm-box')) {
    const allSonBoxes = Array.from(parentDiv.children);
    index = allSonBoxes.indexOf(eqFather(target));
  } else {
    while (target !== parentDiv) {
      if (target.classList.contains('cbbm-box')) {
        const allSonBoxes = Array.from(parentDiv.children);
        index = allSonBoxes.indexOf(eqFather(target));
      }
      target = target.parentElement as HTMLElement;
    }
  }
  if (index === -1) return;
  nowCheckData.value = props.chatData[index];
  if (nowCheckData.value.state === 0) return;
  showRightBtnMessage.value = true;
  rightBtnLeft.value = e.x;
  rightBtnTop.value = e.y;
  czList.value = [
    {id: 0, name: '复制内容', incident: () => copyMessage()},
    {id: 4, name: '引用', incident: () => quoteMessage(nowCheckData.value)},
    {id: 5, name: '转发', incident: () => transmit(nowCheckData.value)},
    {
      id: 1,
      name: '删除消息',
      incident: () => delMessageGo([nowCheckData.value.id])
    }
  ];
  if (nowCheckData.value.sendUid !== props.user.relationUid && countTimeDiff(nowCheckData.value.date, getTimeFormat(new Date()), 60) < 3) {
    czList.value.push({
      id: 2,
      name: '撤回消息',
      incident: () => revocationMessageGo()
    });
  }
  czList.value.push({id: 3, name: '多选', incident: () => moreChecked()});
};

// 获取父级dom
const eqFather = (target: HTMLElement) => {
  while (!target.classList.contains('cbb-box')) {
    target = target.parentElement as HTMLElement;
  }
  return target;
};

// 选择右键内容回调
const chooseRight = (item: any) => {
  item.incident();
  showRightBtnMessage.value = false;
};

// 引用消息
const quoteMessage = (item: message) => {
  clearQuote();
  emit('isActionEmit', 2);
  emit('isQuoteEmit', {
    id: item.id,
    type: item.type,
    message: (item.sendUid !== props.user.uid ? '对方：' : '我：') + item.message
  });
};

const clearQuote = () => {
  emit('isActionEmit', 0);
  emit('isQuoteEmit', {id: 0, type: 0, message: ''});
};

// 消息转发
const showTransmit = ref(false);
const transmitData = ref()
const transmit = (data: message) => {
  transmitData.value = data;
  showTransmit.value = true;
};

watchEffect(() => {
  if (!showTransmit.value && listUser.value && listUser.value.length !== 0) {
    listUser.value.filter((item: any) => {
      item['ok'] = false
      return item
    })
  }
})

// 选中用户-转发
const checkedUser = ref([] as any[])
const transmitMessage = (data: any) => {
  data['ok'] = true
  checkedUser.value.push(data)
}
// 取消选中用户-转发
const closeTransmit = (data: any) => {
  data['ok'] = false
  checkedUser.value = checkedUser.value.filter((item: any) => item.id !== data.id)
};

// 转发
const withVal = ref('')
const sendMessageTransmit = () => {
  showTransmit.value = false;
  let names = ""
  checkedUser.value.forEach((item, index) => {
    names += item.name + (index !== checkedUser.value.length - 1 ? "、" : "。")
    emit('sendMessageTransmitEmit', {
      type: transmitData.value.type,
      message: transmitData.value.message,
      uid: item.relationUid
    })
    if (withVal.value !== '') {
      emit('sendMessageTransmitEmit', {
        type: 0,
        message: withVal.value,
        uid: item.relationUid
      })
    }
  })
  tips('success', "消息已转发至：" + names);
  checkedUser.value = []
  withVal.value = ''
};

// 撤回消息
const revocationMessageGo = () => {
  emit('revocationMessageEmit', nowCheckData.value);
};

// 删除聊天
const delMessageGo = (ids: number[]) => {
  emit('delMessageEmit', ids);
};

// 复制聊天内容
const copyMessage = () => {
  if (nowCheckData.value.type === 1) {
    copyImage(nowCheckData.value.message).then((res) => {
      res ? tips('success', '图片已复制到粘贴板') : tips('error', '复制失败');
    });
    return;
  }
  const result = copyText(nowCheckData.value.message);
  result ? tips('success', '内容已复制到粘贴板') : tips('error', '复制失败');
};

// 多选
const moreChecked = () => {
  emit('moreCheckStateEmit', true);
  emit('nowCheckDataEmit', nowCheckData.value);
};

// 销毁监听
const clearListener = () => {
  const parentDiv = document.querySelector('.cb-body') as HTMLElement;
  if (parentDiv) parentDiv.removeEventListener('contextmenu', listenerMessage);
};

// 关闭右键
const closeRightBtn = () => {
  if (!tapAndHold.value) showRightBtnMessage.value = false;
};

// 关闭右键公共方法
const closeRightBtnCom = (state: boolean) => {
  const dom = document.getElementsByClassName('chat-main')[0];
  state ? dom.addEventListener('click', closeRightBtn) : dom.removeEventListener('click', closeRightBtn);
};

// 组件初开始和结束
onMounted(() => {
  closeRightBtnCom(true);
});
onBeforeUnmount(() => {
  clearListener();
  closeRightBtnCom(false);
});
</script>

<style lang='scss' scoped>
.choose-transmit {
  background-color: $bg-color-hover;
}
</style>
