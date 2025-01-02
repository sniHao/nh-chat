<template>
  <n-modal v-model:show='show' :on-after-leave='closeModal'>
    <n-card style='width: 600px; height: 480px;' class='border-10' :bordered='false' size='huge' role='dialog'
            aria-modal='true'>
      <div class='chat-history'>
        <span class='flex-center-center'>{{ user.name }}</span>
        <!-- 搜索和筛选区域 -->
        <div class='chat-history-header pd-16'>
          <n-input-group>
            <n-input v-model:value='record.search' placeholder='搜索聊天内容' clearable />
            <n-button type='primary' ghost @click='searchHistory'>搜 索</n-button>
          </n-input-group>

          <div class='mt-12 flex-center-zy'>
            <n-radio-group v-model:value='record.type' size='small' class='mr-16'>
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
              <div v-for='(item, index) in historyData'
                   :key='index'
                   class='flex-down mb-16'
                   :class="item.sendUid === user.uid ? 'align-self-end' : 'align-self-start'"
                   style='max-width: 80%'>
                <div class='ft-13' style='color: rgba(0, 0, 0, 0.45); margin-bottom: 4px'
                     :class="item.sendUid === user.uid ? 'text-right' : ''">
                  {{ getTimeFormat(item.date) }}
                </div>
                <div class='history-item-content'
                     :style='`background-color: ${item.sendUid === user.uid ? param.style.rightChatBgColor : param.style.leftChatBgColor}`'>
                  <template v-if='item.type === 0'>
                    {{ item.message }}
                  </template>
                  <n-image
                    v-else-if='item.type === 1'
                    :src='item.message'
                    :preview-src='item.message'
                    class='history-image'
                  />
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

const { notification } = createDiscreteApi(['notification']);

import { NConfigProvider, zhCN, dateZhCN } from 'naive-ui';

const param = inject<Ref<chatProps>>('param') as chatProps | any;
const computedStyle = inject<Ref<any>>('computedStyle') as any;

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

// 搜索历史记录
const historyData = ref<any[]>([]);
const searchHistory = () => {
  console.log(record, 'record');
};
const upDate = (val: any) => {
  record.date = val;
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
watch(() => props.showChatHistory, (newVal) => {
  if (!newVal) return;
  initData();
  show.value = true;
  searchHistory();
});

</script>

<style lang='scss' scoped>
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
    height: $px-320;
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
