<template>
  <div class='right-cz over-auto' :style="'left:' + left + 'px;top:' + top + 'px;background-color:' + bgColor">
    <div class='check hover-pointer pd-6 flex-center-center ft-over' v-for='(item, index) in list' :key='index'
         @click='choose(item)'>{{ item.name }}
    </div>
  </div>
</template>

<script setup lang='ts'>
const emit = defineEmits(['choose']);
const props = defineProps({
  left: {
    default: 0,
    type: Number
  },
  top: {
    default: 0,
    type: Number
  },
  list: {
    default: [],
    type: Array<any>
  },
  bgColor: {
    default: '',
    type: String
  }
});
const choose = (item: any) => {
  emit('choose', item);
};
</script>

<style lang='scss' scoped>
.right-cz {
  position: fixed;
  width: $px-128;
  min-height: $px-160;
  max-height: $px-240;
  border-radius: $px-4;
  box-shadow: 0 0 $px-2 $px-1 $ft-color-tips-2;
  backdrop-filter: blur(0.25rem);
  z-index: 9;
}

.check:hover {
  background-color: $bg-color-hover;
}
</style>
