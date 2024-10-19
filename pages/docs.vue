<template>
  <div class="w-100 docs-main">
    <n-space vertical>
      <n-layout has-sider>
        <n-layout-sider
          bordered
          collapse-mode="width"
          :collapsed-width="0"
          :width="240"
          :collapsed="collapsed"
          show-trigger
          @collapse="collapsed = true"
          @expand="collapsed = false">
          <n-menu :collapsed="collapsed" :collapsed-width="0" :options="menuOptions" :value="check" :on-update:value="upCheck" />
        </n-layout-sider>
        <n-layout>
          <div class="pd-12 flex-zy">
            <NuxtPage @anchor="anchorCallBack" />
            <n-anchor :show-rail="false" :show-background="true" class="anchor-style">
              <n-anchor-link :title="item" :href="`#${item}`" v-for="(item, index) in anchor" :key="index" />
            </n-anchor>
          </div>
        </n-layout>
      </n-layout>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import type { MenuOption } from 'naive-ui';
const router = useRouter();
const route = useRoute();

const anchor = ref([] as string[]);
const anchorCallBack = (val: string[]) => {
  anchor.value = val;
};

const check = ref('');
const collapsed = ref(false);
const upCheck = (key: string, item: MenuOption): void => {
  if (key === check.value) return;
  check.value = key;
  anchor.value = [];
  router.push('/docs/' + item.key);
};
const menuOptions: MenuOption[] = [
  {
    type: 'group',
    label: '介绍',
    children: [
      {
        label: 'nh-chat',
        key: 'nh-chat'
      }
    ]
  },
  {
    type: 'group',
    label: '使用指南',
    children: [
      {
        label: '下载',
        key: 'down'
      },
      {
        label: '项目结构',
        key: 'structure'
      }
    ]
  }
];

onMounted(() => {
  check.value = route.path.replace('/docs/', '') || 'nh-chat';
});
</script>

<style lang="scss" scoped>
:deep(.n-anchor-link--active) {
  background-color: $ft-color;
  border-radius: $px-5;
}
.docs-bd {
  width: $px-800;
  flex-grow: 1; /* 右边盒子自适应宽度 */
}
.anchor-style {
  min-width: $px-160;
  position: fixed;
  right: $px-20;
}
:deep(.n-anchor-link) {
  margin-top: unset !important;
}
:deep(.n-anchor-link__title) {
  color: $ft-color-tips !important;
  padding: $px-6;
  box-sizing: border-box;
}

:deep(.n-layout-scroll-container::-webkit-scrollbar) {
  width: 0.4rem;
  height: 0.25rem;
  position: fixed;
}

/* 滚动条导轨 */
::-webkit-scrollbar-track {
  border-radius: 0.25rem;
  background: rgba(199, 97, 97, 0.06);
  -webkit-box-shadow: inset 0 0 0.3125rem rgba(207, 48, 48, 0.08);
}

/* 滚动条滑块 */
::-webkit-scrollbar-thumb {
  border-radius: 0.25rem;
  background: rgba(23, 164, 33, 0.15);
  -webkit-box-shadow: inset 0 0 0.625rem rgba(255, 103, 0, 0.5);
}

:deep(.n-menu .n-menu-item-content.n-menu-item-content--selected::before) {
  background-color: unset;
}
:deep(.n-menu-item-content--selected) {
  background-color: $ft-color;
}
.n-layout {
  height: calc(100vh - $px-64);
}
:deep(.n-menu .n-menu-item-content:not(.n-menu-item-content--disabled):hover::before) {
  background-color: $ft-color-op-6;
}
:deep(.n-menu-item-group-title) {
  color: $ft-color-tips !important;
}
:deep(.n-menu-item-content-header) {
  color: white !important;
}
.n-layout,
.n-layout-sider {
  background-color: unset;
  color: white;
}
.docs-main {
  margin-top: $px-64;
}
</style>
