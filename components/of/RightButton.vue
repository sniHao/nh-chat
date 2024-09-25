<template>
  <div class="right-cz pd-6" :style="'left:' + left + 'px;top:' + top + 'px'">
    <div>{{ left }} --- {{ top }}</div>
    <div>{{ left }} --- {{ top }}</div>
    <div>{{ left }} --- {{ top }}</div>
    <div>{{ left }} --- {{ top }}</div>
  </div>
</template>

<script setup lang="ts">
const emit = defineEmits(['event']);
const props = defineProps({
  left: {
    default: 0,
    type: Number
  },
  top: {
    default: 0,
    type: Number
  }
});
</script>

<style lang="scss" scoped>
.right-cz {
  position: fixed;
  width: 110px;
  height: 150px;
  border-radius: $px-4;
  box-shadow: 0 0 $px-2 $px-1 $ft-color-tips-2;
  background-color: rgb(142 91 180 / 80%);
  backdrop-filter: blur(0.25rem);
}
</style>
