<script>
export default {
  name: 'TagEllipsis',
  components: {},
  props: {
    list: {
      type: Array,
      default: () => [],
    },
    limit: {
      type: [String, Number],
      default: 2,
    },
    defaultProps: {
      type: String,
      default: 'name',
    },
  },
  data() {
    return {}
  },
  computed: {
    _list() {
      return this.list.filter((e) => (typeof e === 'string' ? e : e[this.defaultProps]))
    },
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <div class="tag-ellipsis">
    <div v-if="!_list.length">无</div>

    <template v-else-if="_list.length <= +limit">
      <el-tag v-for="(item, unique) in _list" :key="unique">
        {{ item[defaultProps] || item }}
      </el-tag>
    </template>

    <el-popover v-else placement="bottom" trigger="hover">
      <div class="tag-all">
        <el-tag v-for="(item, unique) in _list" :key="unique">
          {{ item[defaultProps] || item }}
        </el-tag>
      </div>
      <div slot="reference">
        <el-tag v-for="(item, unique) in _list.slice(0, +limit)" :key="unique">
          {{ item[defaultProps] || item }}
        </el-tag>
        <el-tag key="a" v-if="_list.length > +limit">...</el-tag>
      </div>
    </el-popover>
  </div>
</template>

<style lang="scss" scoped>
.tag-ellipsis {
  display: inline-block;
  line-height: 34px;
  .el-tag {
    vertical-align: middle;
  }
  .tag-all {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    .el-tag {
      margin: 0;
    }
  }
}
</style>
