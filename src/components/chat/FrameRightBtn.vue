<template>
  <RightButton v-if='showRightBtnMessage' :left='rightBtnLeft' :top='rightBtnTop' :list='czList'
               :style='`color:${param.style.fontColor}`'
               :bgColor='computedStyle.mainColorOpt'
               @choose='chooseRight'></RightButton>
</template>

<script setup lang='ts'>
import { countTimeDiff, getTimeFormat } from '@/utils/TimeUtil';
import { tips, copyImage, copyText } from '@/utils/OtherUtils';
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
  }
});
const emit = defineEmits(['isActionEmit', 'isQuoteEmit', 'delMessageEmit', 'moreCheckStateEmit', 'nowCheckDataEmit', 'revocationMessageEmit']);

watch(() => props.needListener, () => {
  if (props.needListener) addListener();
});
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
    { id: 0, name: '复制内容', incident: () => copyMessage() },
    { id: 4, name: '引用', incident: () => quoteMessage(nowCheckData.value) },
    { id: 5, name: '转发', incident: () => transmit() },
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
  czList.value.push({ id: 3, name: '多选', incident: () => moreChecked() });
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
  emit('isQuoteEmit', { id: 0, type: 0, message: '' });
};

// 消息转发
const transmit = () => {
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
</style>
