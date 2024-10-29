<template>
    <ContentDoc :path="'/' + url" class="md" />
  </template>
  
  <script setup lang="ts">
  const emit = defineEmits(['anchor']);
  const url = ref('page-config');
  onMounted(async () => {
    const articles = await queryContent(url.value).find();
    emit(
      'anchor',
      articles[0].body?.children.filter((item) => item.props?.id).map((item) => item.props?.id)
    );
  });
  </script>
  
  <style lang="scss" scoped></style>
  