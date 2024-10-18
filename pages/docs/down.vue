<template>
    <div class="pd-12 flex-zy">
      <ContentDoc path="/down" />
    </div>
  </template>
  
  <script setup lang="ts">
  import { onMounted } from 'vue';
  
  const emit = defineEmits(['anchor']);
  onMounted(async () => {
    const articles = await queryContent('down').find();
    emit(
      'anchor',
      articles[0].body?.children.filter((item) => item.props?.id).map((item) => item.props?.id)
    );
  });
  </script>
  
  <style lang="scss" scoped></style>
  