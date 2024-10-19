<template>
  <div class="pd-12 flex-zy"><ContentDoc :path="'/' + url" class="md" /></div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';

const emit = defineEmits(['anchor']);
const url = ref('structure');
onMounted(async () => {
  const articles = await queryContent(url.value).find();
  emit(
    'anchor',
    articles[0].body?.children.filter((item) => item.props?.id).map((item) => item.props?.id)
  );
});
</script>

<style lang="scss" scoped>
:deep(.md) {
  a {
    text-decoration: none;
  }
  h2 a {
    color: $ft-color;
  }
  p a,
  li a {
    color: $ft-color-2;
  }
}
</style>
