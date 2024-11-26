<template>
  <div class='h-100 flex-center-center flex-down'>
    <div class='loader'></div>
    <div class='mt-10 ft-16' :style='`color:${computedStyle.fontColorOpt}`'>{{ width }}</div>
  </div>
</template>

<script setup lang='ts'>
const emit = defineEmits(['event']);
const computedStyle = inject<Ref<any>>('computedStyle') as any;
const props = defineProps({
  width: {
    default: '数据加载中......☕',
    type: String
  }
});
</script>

<style lang='scss' scoped>
.loader {
  display: inline-grid;
  width: 90px;
  aspect-ratio: 1;
  transform: rotate(90deg);
  animation: l4 1s linear infinite;
}

.loader:before,
.loader:after {
  content: '';
  grid-area: 1/1;
}

.loader:before {
  clip-path: polygon(
      100% 50%,
      90.45% 79.39%,
      65.45% 97.55%,
      34.55% 97.55%,
      9.55% 79.39%,
      0% 50%,
      9.55% 20.61%,
      34.55% 2.45%,
      65.45% 2.45%,
      90.45% 20.61%,
      100% 50%,
      85.6% 24.14%,
      63.6% 8.15%,
      36.4% 8.15%,
      14.4% 24.14%,
      6% 50%,
      14.4% 75.86%,
      36.4% 91.85%,
      63.6% 91.85%,
      85.6% 75.86%,
      94% 50%,
      85.6% 24.14%
  );
  background: $ft-color-2-op-8;
}

.loader:after {
  background: $ft-color-op-8;
  clip-path: polygon(100% 50%, 65.45% 97.55%, 9.55% 79.39%, 9.55% 20.61%, 65.45% 2.45%);
  margin: 27%;
  translate: 46% 0;
  transform-origin: right;
  animation: inherit;
}

@keyframes l4 {
  0% {
    rotate: 18deg;
  }
  to {
    rotate: -18deg;
  }
}
</style>
