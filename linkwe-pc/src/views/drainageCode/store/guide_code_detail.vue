<template>
  <div>
    <div class="card-tab">
      <div class="item" :class="activeIndex === 0 ? 'active' : ''" @click="activeIndex = 0">活码信息</div>
      <div class="item" :class="activeIndex === 1 ? 'active' : ''" @click="activeIndex = 1">数据统计</div>
    </div>
    <div style="margin-top: 10px">
      <template v-if="activeIndex === 0">
        <base-detail :codeType="1"></base-detail>
      </template>
      <template v-if="activeIndex === 1">
        <guide-code-analyse></guide-code-analyse>
      </template>
    </div>
  </div>
</template>
<script>
import BaseDetail from './base_detail'
import GuideCodeAnalyse from './guide_code_analyse'
export default {
  name: 'store-detail',
  data() {
    return {
      activeIndex: 0,
    }
  },
  components: {
    BaseDetail,
    GuideCodeAnalyse,
  },
}
</script>
<style lang="scss" scoped>
.active {
  font-weight: 600;
  color: var(--color) !important;
  background-color: #fff !important;
  box-shadow: 0px 6px 30px 5px rgba(59, 59, 59, 0.05);
}
.card-tab {
  // margin-top: 10px;
  display: flex;
  div {
    width: 88px;
    height: 30px;
    background: #fafafa;
    border-radius: 32px 32px 32px 32px;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.4);
    line-height: 30px;
    text-align: center;
    margin-top: 15px;
    margin-right: 12px;
    margin-bottom: 6px;
  }
  .item:hover {
    cursor: pointer;
    opacity: 0.8;
  }
}
</style>
