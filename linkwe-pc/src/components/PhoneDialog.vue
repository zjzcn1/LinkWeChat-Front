<script>
export default {
  components: {},
  props: {
    message: {
      type: String,
      default: '',
    },
    isOther: {
      type: Boolean,
      default: false,
    },
    imageList: {
      type: Array,
      default: () => [],
    },
    messageList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {}
  },
  watch: {},
  computed: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <PhoneTemplate>
    <div class="time">13:14</div>
    <ul class="msg-ul">
      <li class="flex msg-li">
        <el-avatar shape="square" size="small" :src="require('@/assets/image/profile.jpg')"></el-avatar>
        <div class="msg">{{ message }}</div>
      </li>
      <li class="flex msg-li" v-if="isOther">
        <el-avatar shape="square" size="small" :src="require('@/assets/image/profile.jpg')"></el-avatar>
        <div class="msg" style="line-height: 0; padding: 5px">
          <slot></slot>
        </div>
      </li>
      <li class="flex msg-li" v-for="message in messageList" :key="message">
        <el-avatar shape="square" size="small" :src="require('@/assets/image/profile.jpg')"></el-avatar>
        <div class="msg">
          <slot name="text" v-bind:text="message"></slot>
        </div>
      </li>
      <li class="flex msg-li" v-for="image in imageList" :key="image">
        <el-avatar shape="square" size="small" :src="require('@/assets/image/profile.jpg')"></el-avatar>
        <div class="msg" style="line-height: 0; padding: 5px">
          <slot name="image" v-bind:image="image"></slot>
        </div>
      </li>
    </ul>
  </PhoneTemplate>
</template>

<style lang="scss" scoped>
.time {
  font-size: 12px;
  color: #aaa;
  padding: 10px 0 5px;
  text-align: center;
}
.msg-ul {
  margin: 0;
  padding: 0;
  max-height: 80%;
  overflow: auto;
  padding: 0 10px;
}
.msg-li {
  margin-bottom: 10px;
}
.msg {
  min-height: 28px;
  background-color: #fff;
  border: 1px solid #e4e6e9;
  display: inline-block;
  position: relative;
  text-align: left;
  font-size: 14px;
  line-height: 22px;
  padding: 3px 6px;
  border-radius: 4px;
  max-width: 90%;
  min-width: 24px;
  box-sizing: border-box;
  word-break: break-all;
  white-space: pre-line;
  margin-left: 10px;
  &:before {
    content: ' ';
    display: block;
    position: absolute;
    left: -6px;
    border-width: 4px 6px;
    border-left: 0;
    width: 0;
    border-style: solid;
    border-color: transparent;
    border-right-color: #fff;
    top: 10px;
    z-index: 888;
  }
}
.el-avatar {
  flex: none;
}
</style>
