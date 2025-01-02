<template>
  <n-modal v-model:show='show' :on-after-leave='closeModal'>
    <n-card style='width: 600px; height: 550px;' class='border-10' :bordered='false' size='huge' role='dialog'
            aria-modal='true'>
      <div class='chat-history'>
        <span class='flex-center-center'>{{ user.name }}</span>
        <!-- 搜索和筛选区域 -->
        <div class='chat-history-header pd-16'>
          <n-input-group>
            <n-input v-model:value='record.search' placeholder='搜索聊天内容' clearable />
            <n-button type='primary' ghost @click='searchHistory()'>搜 索</n-button>
          </n-input-group>

          <div class='mt-12 flex-center-zy'>
            <n-radio-group v-model:value='record.type' size='small' class='mr-16' :on-update:value='upType'>
              <n-radio-button :value='0'>全部</n-radio-button>
              <n-radio-button :value='1'>文本</n-radio-button>
              <n-radio-button :value='2'>图片</n-radio-button>
            </n-radio-group>

            <n-config-provider :locale='zhCN' :date-locale='dateZhCN'>
              <n-date-picker v-model:value='record.date' type='date' size='small' :on-update:value='upDate' clearable />
            </n-config-provider>
          </div>
        </div>

        <!-- 聊天记录列表 -->
        <div class='chat-history-content pd-16'>
          <div class='over-auto chat-history-box'>
            <div v-if='historyData.length === 0' class='flex-center-center h-100'>
              <n-empty description='暂无聊天记录' />
            </div>
            <div v-else class='flex-down'>
              <div class='history-message pd-12 flex' v-for='i in 20'>
                <div
                  class='user-head flex-center-center'
                  :style="'background-color:' + tranColor(param.userInfo.photo)"
                  v-html='computePhoto(param.userInfo.photo)'></div>
                <div class='flex-down ml-18 w-auto'>
                  <div class='flex-center-zy'>
                    <div class='ft-16'>昵称</div>
                    <div class='ft-color-tips'>时间</div>
                  </div>
                  <div>
                    <span class='history-message-span'>数数据数据数据据</span>
                    <n-image class='chat-image' src='' />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </n-card>
  </n-modal>
</template>

<script setup lang='ts'>
import { createDiscreteApi } from 'naive-ui';
import { getTimeFormat } from '@/utils/TimeUtil';
import { tranColor, computePhoto } from '@/utils/OtherUtils';
import { throttle } from '@/utils/domUtils';

const param = inject<Ref<chatProps>>('param') as chatProps | any;

const { notification } = createDiscreteApi(['notification']);

import { NConfigProvider, zhCN, dateZhCN } from 'naive-ui';


const props = defineProps({
  user: {
    type: Object,
    default: {}
  },
  showChatHistory: {
    type: Boolean,
    default: false
  }
});
const emit = defineEmits(['closeChatHistory']);
const closeModal = () => {
  emit('closeChatHistory', false);
};

// 上拉拉取消息
const loadingMessage = ref(false);
const scrollToTop = () => {
  let scrollDom = document.getElementsByClassName('chat-history-box')[0];
  if (scrollDom.scrollTop < 30 && next.value && !loadingMessage.value) {
    searchHistory(false);
  }
};

// 监听滚动条
const listenerScrollToTop = (state: boolean) => {
  let scrollDom = document.getElementsByClassName('chat-history-box')[0];
  if (!scrollDom) return;
  const throttledScrollToTop = throttle(scrollToTop, 50);
  if (state) scrollDom.addEventListener('scroll', throttledScrollToTop);
  else scrollDom.removeEventListener('scroll', throttledScrollToTop);
};

// 滚动条居底
const scrollToBottom = () => {
  setTimeout(() => {
    let scrollDom = document.getElementsByClassName('chat-history-box')[0];
    scrollDom.scrollTop = scrollDom.scrollHeight;
  }, 100);
};

// 搜索历史记录
const next = ref(false);
const historyData = ref<any[]>([{}, {}]);
const page = ref(1);
/**
 * 搜索历史记录
 * @param isOnePage 是否从第一页开始
 */
const searchHistory = (isOnePage: boolean = true) => {
  if (isOnePage) page.value = 1;
  else page.value++;

  scrollToBottom();
  setTimeout(() => {
    listenerScrollToTop(true);
  }, 150);
};

// 日期查询
const upDate = (val: any) => {
  record.date = val;
  searchHistory();
};

// 类型查询
const upType = (val: any) => {
  record.type = val;
  searchHistory();
};

// 筛选
const record = reactive({
  search: '',
  type: 0,
  date: null
});

const initData = () => {
  record.type = 0;
  record.date = null;
  record.search = '';
};

const show = ref(false);
watchEffect(() => {
  if (!props.showChatHistory) return;
  initData();
  show.value = true;
  searchHistory();
});

onBeforeUnmount(() => {
  listenerScrollToTop(false);
});
</script>

<style lang='scss' scoped>

:deep(.chat-image img) {
  max-width: $px-160;
  max-height: $px-160;
}

.history-message {
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);

  &:last-child {
    border-bottom: none;
  }

  &-span {
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: anywhere;
    white-space: pre-wrap;
  }
}

.chat-history {
  height: 100%;
  display: flex;
  flex-direction: column;

  &-header {
    padding: $px-6 0;
    border-bottom: $px-1 solid rgba(0, 0, 0, 0.06);
  }

  &-content {
    flex: 1;
    overflow: hidden;
  }

  &-box {
    height: $px-400;
  }
}

.history-item-content {
  padding: $px-8 $px-12;
  border-radius: $px-8;
  word-break: break-all;
}

.history-image {
  max-width: $px-200;
  max-height: $px-200;

  :deep(img) {
    object-fit: contain;
    border-radius: $px-4;
  }
}
</style>
