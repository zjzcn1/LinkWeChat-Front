<template>
  <!-- <el-scrollbar class="page-scrollbar"> -->
  <div class="app-main">
    <div class="page-wrap">
      <transition name="fade-transform" mode="out-in" v-if="!isActiveMicroApp">
        <router-view class="page container" id="page-container" :key="key" />
        <!-- <keep-alive :include="cachedViews">
      </keep-alive> -->
      </transition>
      <div v-loading="loading" class="micro-app-wrap height100" v-show="isActiveMicroApp">
        <div id="micro-app" class="height100"></div>
      </div>
    </div>
  </div>
  <!-- </el-scrollbar> -->
</template>

<script>
import { registerMicroApps, start, initGlobalState } from 'qiankun'
export default {
  name: 'AppMain',
  data() {
    return {
      loading: false,
    }
  },
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    },
    isActiveMicroApp() {
      let microApp = window.lwConfig.MICRO_APPS
      return (
        microApp && Object.values(microApp).some((item) => this.$route.path.startsWith(item.activeRule.match('/.*')[0]))
      )
    },
  },
  mounted() {
    if (window.lwConfig.MICRO_APPS && window.lwConfig.MICRO_APPS.store) {
      window.Vue2 = window.Vue
      delete window.Vue
      window.lwConfig.MICRO_APPS.store.loader = (loading) => (this.loading = loading)
      registerMicroApps([window.lwConfig.MICRO_APPS.store])
      start({
        sandbox: { experimentalStyleIsolation: true },
      })

      // 挂在window供下面微服务使用
      window.lwConfig.$store = this.$store

      // 初始化 state
      // const actions = initGlobalState({ token: getToken() })
      // actions.onGlobalStateChange((state, prev) => {
      //   // state: 变更后的状态; prev 变更前的状态
      // });
      // actions.setGlobalState(state)
    }
    // if (window.lwConfig.MICRO_APPS) {
    //   import('qiankun').then(({ registerMicroApps, start }) => {
    //     registerMicroApps(window.lwConfig.MICRO_APPS)
    //     start({
    //       sandbox: { experimentalStyleIsolation: true },
    //     })
    //   })
    // }
  },
}
</script>

<style lang="scss" scoped>
// ::v-deep .el-scrollbar__wrap {
//   margin: 20px 0;
//   padding: 0 20px;
// }
.app-main {
  position: relative;
  flex: auto;
  // overflow: hidden;
  overflow-y: auto;
  overflow-x: hidden;
  margin-top: 20px;
}
.page-wrap {
  position: initial !important; // 用以解决加载遮罩层随内容滚动问题
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  scroll-behavior: smooth;
  padding: 0 15px 0 20px;
}
.page {
  position: initial !important; // 用以解决加载遮罩层随内容滚动问题
}
.micro-app-wrap {
  position: relative;
}
// .fixed-header + .app-main {
//   padding-top: 50px;
// }

// .hasTagsView {
//   .app-main {
//     /* 84 = navbar + tags-view = 50 + 34 */
//     height: calc(100vh - 84px);
//   }

//   .fixed-header + .app-main {
//     padding-top: 84px;
//   }
// }
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 15px;
  }
}
#micro-app > div {
  height: 100%;
}
</style>
