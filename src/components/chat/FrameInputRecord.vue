<template>
  <n-modal v-model:show='show' :on-after-leave='closeModal'>
    <n-card style='width: 600px; height: 550px;' class='border-10' :bordered='false' size='huge' role='dialog'
            aria-modal='true'>
      <div class='chat-history'>
        <span class='flex-center-center'>{{ user.name }}</span>
        <!-- 搜索和筛选区域 -->
        <div class='chat-history-header pd-16'>
          <n-input-group>
            <n-input v-model:value='record.search' placeholder='搜索聊天内容' clearable :on-clear='clearSearch'/>
            <n-button type='primary' ghost @click='goSearch'>搜 索</n-button>
          </n-input-group>

          <div class='mt-12 flex-center-zy'>
            <n-radio-group v-model:value='record.type' size='small' class='mr-16' :on-update:value='upType'>
              <n-radio-button :value='0'>全部</n-radio-button>
              <n-radio-button :value='1'>文本</n-radio-button>
              <n-radio-button :value='2'>图片</n-radio-button>
            </n-radio-group>

            <n-config-provider :locale='zhCN' :date-locale='dateZhCN'>
              <n-date-picker v-model:value='record.date' type='date' size='small' :on-update:value='upDate' clearable/>
            </n-config-provider>
          </div>
        </div>

        <!-- 聊天记录列表 -->
        <div class='chat-history-content pd-16 pos-r'>
          <div class='flex-center-center h-100 pos-a chat-history-loading' v-if='loadingMessage'>
            <div class='flex-down flex-center'>
              <n-space>
                <n-spin size='large'/>
              </n-space>
              <div class='mt-12 ft-16'>数据加载中...</div>
            </div>
          </div>
          <div class='over-auto chat-history-box'>
            <div v-if='historyData.length === 0' class='flex-center-center h-100'>
              <n-empty description='暂无聊天记录'/>
            </div>
            <div v-else class='flex-down'>
              <div class='history-message pd-12 flex' v-for='(item,index) in historyData' :key='index'>
                <div
                    class='user-head flex-center-center'
                    :style="'background-color:' + tranColor(item.sendUid !== user.uid?user.photo:param.userInfo.photo)"
                    v-html='computePhoto(item.sendUid !== user.uid?user.photo:param.userInfo.photo)'></div>
                <div class='flex-down ml-18 w-auto'>
                  <div class='flex-center-zy'>
                    <div class='ft-16'>{{ item.sendUid !== user.uid ? user.name : param.userInfo.name }}</div>
                    <div class='ft-color-tips'>{{ cutChatTime(item.date) }}</div>
                  </div>
                  <div>
                    <span v-if='item.type === 0' class='history-message-span'>{{ item.message }}</span>
                    <n-image v-else class='chat-image' :src='item.message'/>
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
import {createDiscreteApi} from 'naive-ui';
import {cutChatTime} from '@/utils/TimeUtil';
import {tranColor, computePhoto, tips} from '@/utils/OtherUtils';
import {filterExperienceData} from '@/utils/staticUtils';
import {throttle} from '@/utils/domUtils';
import {eqChatRecord} from '@/api/index';

const param = inject<Ref<chatProps>>('param') as chatProps | any;

const {notification} = createDiscreteApi(['notification']);

import {NConfigProvider, zhCN, dateZhCN} from 'naive-ui';

const props = defineProps({
  user: {
    type: Object,
    default: {}
  },
  showChatHistory: {
    type: Boolean,
    default: false
  },
  chatData: {
    type: Array,
    default: []
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
    searchHistory({isOnePage: false, needBottom: false});
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
const historyData = ref([] as chatRecord[]);
const page = ref(1);

// 定义默认值
const definedVal = (obj: { isOnePage?: boolean, needBottom?: boolean, newData?: boolean }) => {
  if (obj?.isOnePage === undefined) obj['isOnePage'] = true;
  if (obj?.needBottom === undefined) obj['needBottom'] = true;
  if (obj?.newData === undefined) obj['newData'] = false;
  return obj;
};

// 填充数据
const fillData = (newData: boolean = true, data: chatRecord[]) => {
  if (newData) historyData.value = data;
  else historyData.value.unshift(...data);
};

/**
 * 搜索历史记录
 * @param obj
 */
const searchHistory = (obj: { isOnePage?: boolean, needBottom?: boolean, newData?: boolean }) => {
  obj = definedVal(obj ?? {});
  loadingMessage.value = true;
  eqChatRecord(setEqData(obj.isOnePage)).then((res: Result) => {
    if (res.code !== 200) {
      if (!param.experienceMode) return tips('error', res.msg);
      historyData.value = filterExperienceData(props.chatData, record);
      return;
    }
    next.value = res.data.next;
    if (next.value) page.value++;
    if (!obj.needBottom) {
      const oldHeight = document.getElementsByClassName('chat-history-box')[0].scrollHeight || 0;
      fillData(obj.newData, res.data.data);
      setTimeout(() => {
        let newDom = document.getElementsByClassName('chat-history-box')[0];
        newDom.scrollTop = newDom.scrollHeight - oldHeight;
      }, 50);
    } else {
      fillData(obj.newData, res.data.data);
    }
  }).finally(() => {
    if (obj.needBottom) scrollToBottom();
    setTimeout(() => {
      loadingMessage.value = false;
      listenerScrollToTop(true);
    }, 150);
  });
};

// 设置查询参数
const setEqData = (isOnePage: boolean | undefined) => {
  if (isOnePage) page.value = 1;
  let newData = JSON.parse(JSON.stringify(record));
  newData.receiveUid = props.user.relationUid;
  newData.page = page.value;
  newData.date = newData.date ?? 0;
  return newData;
};

// 清除搜索查询
const clearSearch = () => {
  record.search = '';
  searchHistory({newData: true});
};

// 搜索查询
const goSearch = () => {
  if (!record.search.trim()) return;
  searchHistory({newData: true});
};

// 日期查询
const upDate = (val: any) => {
  record.date = val;
  searchHistory({newData: true});
};

// 类型查询
const upType = (val: any) => {
  record.type = val;
  searchHistory({newData: true});
};

// 筛选
const record = reactive({
  search: '',
  type: 0,
  date: null
});

const initData = () => {
  historyData.value = [];
  record.type = 0;
  record.date = null;
  record.search = '';
};

const show = ref(false);
watch(() => props.showChatHistory, (newVal) => {
  if (!newVal) return show.value = false;
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

  &-loading {
    left: 0;
    right: 0;
    background-color: white;
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
