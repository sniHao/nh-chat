<template>
  <div class='cb-input pd-1' :style='`box-shadow: 0 0 .4px .4px ${computedStyle.fontColorOpt}`'>
    <!-- 多选操作 -->
    <div v-show='moreCheckState' class='flex-center-center h-100'>
      <div class='flex-down-center' @click='delMoreChecked'>
        <Svg :width='24' :height='24' :fill='computedStyle.fontColorOpt95' class='hover-pointer mb-2'
             name='del'></Svg>
        <n-button text :color='computedStyle.fontColorOpt95' size='large'>删除</n-button>
      </div>
      <div class='flex-down-center ml-18' @click='cancelMoreChecked'>
        <Svg :width='24' :height='24' :fill='computedStyle.fontColorOpt' class='hover-pointer mb-2'
             name='cancel'></Svg>
        <n-button text :color='computedStyle.fontColorOpt' size='large'>取消</n-button>
      </div>
    </div>
    <!-- 复制图片 -->
    <div v-show='copyImage' class='flex-down-center flex-zy h-100 pd-sx-12'>
      <n-image class='copy-image' :src='copyImageUrl' :style='`border: 2px solid ${computedStyle.fontColorOpt}`' />
      <div class='flex mt-12'>
        <n-button class='mr-16' type='primary' ghost @click='closeCopyImage' :color='param.style.leftChatBgColor'>
          取 消
        </n-button>
        <n-button type='primary' @click='sendCopyImage' :color='param.style.rightChatBgColor'>
          发 送
        </n-button>
      </div>
    </div>
    <div v-show='!moreCheckState && !copyImage'>
      <!-- 表情包图片等等 -->
      <div class='cb-input-controls flex-center-zy pd-zy-12'>
        <div class='flex-center'>
          <n-popover trigger='click' raw @update:show='handleUpdateShow'>
            <template #trigger>
              <Svg :width='24' :height='24' class='hover-pointer' name='emoji'
                   :fill='computedStyle.fontColorOpt'></Svg>
            </template>
            <EmoJi @choose='chooseEmoji' />
          </n-popover>
          <n-upload :show-file-list='false' @before-upload='beforeUpload' accept='.png,.jpeg,.jpg'>
            <Svg :width='24' :height='24' class='hover-pointer ml-12' name='up-image'
                 :fill='computedStyle.fontColorOpt'></Svg>
          </n-upload>
        </div>
        <n-popover trigger='hover'>
          <template #trigger>
            <Svg :width='26' :height='26' class='hover-pointer' name='chat-history'
                 :fill='computedStyle.fontColorOpt' @click='goChatHistory(true)'></Svg>
          </template>
          <span>聊天记录</span>
        </n-popover>
      </div>
      <!-- 输入框 -->
      <div class='cb-input-main' :style='`background-color: ${param.style.mainColor};`'
           :class="isQuote.id!==0?'cb-input-main-quote':''">
        <n-input
          id='nh-chat-input'
          ref='inputInstRef'
          :style='`--n-border: unset; --n-border-hover: unset; --n-border-focus: unset; --n-box-shadow-focus: unset;
              --n-placeholder-color:${computedStyle.fontColorOpt};--n-text-color:${param.style.fontColor}`'
          v-model:value='sendVal'
          type='textarea'
          @keydown.enter.native='handleKeyUp'
          @input='valChange'
          placeholder-style='color:red'
          placeholder='说点啥...' />
      </div>
      <!-- 引用-->
      <template v-if='isQuote.id!==0'>
        <div class='pd-zy-12 quote-message flex-center-zy ft-13'
             :style='`background-color: ${computedStyle.fontColorOpt35}`'>
          <div class='w-70 flex-center' :style='`color:${computedStyle.fontColorOpt}`'>
            <template v-for='(quote, index) in getQuoteView(isQuote.message)' :key='index'>
              <span v-if='index === 0'>{{ quote }}</span>
              <span class='ft-over' v-if='isQuote.type === 0 && index === 1'
                    :title='quote'>{{ quote }}</span>
              <n-image v-else-if='index === 1' class='quote-image' :src='quote' />
            </template>
          </div>
          <div class='hover-pointer' @click='clearQuote'>取消引用</div>
        </div>
      </template>
      <div class='quote-message' v-else></div>
      <!-- 发送 -->
      <div class='cb-input-go flex-center-zy pd-zy-6' :style='`color:${computedStyle.fontColorOpt}`'>
        <div>{{ sendVal.length }} / {{ inputMaxNumber }}</div>
        <div class='hover-pointer' @click='sendInfo'>按Enter键发送</div>
      </div>
    </div>
    <!-- 弹框 -->
    <FrameInputRecord :user='user' :showChatHistory='showChatHistory'
                      @closeChatHistory='goChatHistory'></FrameInputRecord>
  </div>
</template>

<script setup lang='ts'>
import { createDiscreteApi } from 'naive-ui';

const { notification } = createDiscreteApi(['notification']);
import type { UploadFileInfo } from 'naive-ui';
import { getQuoteView } from '@/utils/OtherUtils';
import Svg from '../of/Svg.vue';
import EmoJi from '../of/EmoJi.vue';
import FrameInputRecord from './FrameInputRecord.vue';

const param = inject<Ref<chatProps>>('param') as chatProps | any;
const computedStyle = inject<Ref<any>>('computedStyle') as any;

const props = defineProps({
  isQuote: {
    type: Object,
    default: () => ({
      id: 0,
      type: 0,
      message: ''
    })
  },
  clearSendVal: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: {}
  },
  moreCheckState: {
    type: Boolean,
    default: false
  },
  saveChecked: {
    type: Array,
    default: () => []
  },
  addSendVal: {
    type: String,
    default: ''
  }
});
const emit = defineEmits(['sendMessageEmit', 'sendImageEmit', 'isActionEmit', 'isQuoteEmit', 'moreCheckStateEmit', 'delMessageEmit', 'cancelMoreCheckedEmit']);

// 聊天记录
const showChatHistory = ref(false);
const goChatHistory = (state: boolean) => {
  showChatHistory.value = state;
};

//清除引用
const clearQuote = () => {
  emit('isActionEmit', 0);
  emit('isQuoteEmit', { com: '', id: 0, data: {} });
};

// 删除多选内容
const delMoreChecked = () => {
  emit('moreCheckStateEmit', false);
  emit('delMessageEmit', props.saveChecked);
};
// 取消多选
const cancelMoreChecked = () => {
  emit('moreCheckStateEmit', false);
  emit('cancelMoreCheckedEmit');
};

// 展开关闭表情
const handleUpdateShow = (state: boolean) => {
  if (state) inputInstRef.value?.focus();
};

// 表情回调
const inputInstRef = ref<any>('');
const chooseEmoji = (val: string) => {
  const inputElement = inputInstRef.value?.$el.querySelector('textarea');
  if (inputElement) {
    const nowPoint = inputElement.selectionStart + val.length;
    sendVal.value = sendVal.value.slice(0, inputElement.selectionStart) + val + sendVal.value.slice(inputElement.selectionStart);
    setTimeout(() => {
      inputElement.focus();
      inputElement.setSelectionRange(nowPoint, nowPoint);
    });
  } else sendVal.value += val;
};

// 回车处理
const sendVal = ref('');
const inputMaxNumber = ref(2000);
const handleKeyUp = (e: KeyboardEvent) => {
  if (e.key !== 'Enter') return;
  if (e.shiftKey) return;
  if (e.altKey) return (sendVal.value += '\n');
  e.preventDefault();
  sendInfo();
};

// 输入框字数限制
const valChange = () => {
  if (sendVal.value.length > inputMaxNumber.value) return (sendVal.value = sendVal.value.slice(0, inputMaxNumber.value));
};

// 发送消息
const sendInfo = () => {
  emit('sendMessageEmit', sendVal.value);
  sendVal.value = '';
};

// 发送图片
const beforeUpload = (data: { file: UploadFileInfo; fileList: UploadFileInfo[] }) => {
  const file = data.file.file as File;
  emit('sendImageEmit', file);
};

// 监听复制粘贴消息
const copyImage = ref(false);
const copyImageFile = ref();
const copyImageUrl = ref('');

const closeCopyImage = () => {
  copyImage.value = false;
  copyImageUrl.value = '';
  copyImageFile.value = '';
};
const sendCopyImage = () => {
  emit('sendImageEmit', copyImageFile.value);
  closeCopyImage();
};

const listenerCopy = () => {
  const pasteArea = document.getElementById('nh-chat-input') as HTMLDivElement;
  pasteArea.addEventListener('paste', (event: ClipboardEvent) => {
    const items = event.clipboardData?.items;
    if (!items) return;
    for (let i = 0; i < items.length; i++) {
      if (items[i].type.startsWith('image/')) {
        const file = items[i].getAsFile();
        if (file) {
          copyImage.value = true;
          copyImageFile.value = file;
          const reader = new FileReader();
          reader.onload = function(e: any) {
            copyImageUrl.value = e.target.result;
          };
          reader.readAsDataURL(file as any);
          event.preventDefault();
        }
      }
    }
  });
};

watch(() => props.clearSendVal, () => {
  if (props.clearSendVal) sendVal.value = '';
});
watch(() => props.addSendVal, () => {
  if (props.addSendVal) sendVal.value = props.addSendVal;
});
watch(() => props.isQuote, () => {
  if (props.isQuote.id !== 0) inputInstRef.value?.focus();
});

onMounted(() => {
  listenerCopy();
});
</script>

<style lang='scss' scoped>
.copy-image {
  max-width: $px-96;
  max-height: $px-96;
  border-radius: $px-4;
}

:deep(.copy-image img) {
  max-width: 100%;
  max-height: 100%;
}

:deep(.n-upload) {
  width: unset;
}

:deep(.n-upload-trigger) {
  display: flex;
}

:deep(.n-upload-trigger + .n-upload-file-list) {
  margin-top: unset;
}

.quote-message {
  height: $px-26;
}

.quote-image {
  max-height: $px-24;
  max-width: $px-24;
}

:deep(.quote-image img) {
  width: 100% !important;
  height: 100% !important;
  object-fit: contain !important;
}

.cb-input-main-quote {
  height: calc(100% - $px-32 - $px-32 - $px-26) !important;
}

.cb-input {
  z-index: 6;
  min-height: $px-160;

  .cb-input-main {
    height: calc(100% - $px-32 - $px-32);

    .n-input {
      height: 100%;
      background-color: unset;
    }

    .n-input:not(.n-input--disabled).n-input--focus {
      background-color: unset;
    }

    :deep(.n-input-wrapper) {
      resize: unset;
    }

    :deep(.n-scrollbar-rail__scrollbar) {
      --n-scrollbar-color: rgba(255, 103, 0, 0.5);
      --n-scrollbar-color-hover: rgba(255, 103, 0);
    }
  }

  .cb-input-go,
  .cb-input-controls {
    height: $px-32;
  }
}
</style>
