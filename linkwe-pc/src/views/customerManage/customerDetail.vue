<script>
import { updateBirthday, getDetail, getSummary, getFollowUpRecord, getCustomerInfoByUserId } from '@/api/customer'
import { getList } from '@/api/salesCenter/businessConver.js'

import { dictAddType, dictJoinGroupType, dictTrackState } from '@/utils/dictionary'
import InfoTab from './customer/infoTab.vue'

export default {
  name: 'CustomerDetail',
  components: { InfoTab },
  data() {
    return {
      datePickerVisible: false,
      customer: {
        // weFlowerCustomerRels: [{}]
      },
      birthday: '',
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
      },
      dictAddType,
      dictJoinGroupType,
      dictTrackState,

      active: 0,
      openedTabs: ['0'],
    }
  },
  created() {
    this.getDetail()
    this.getStage()
  },
  methods: {
    getStage() {
      getList().then((res) => {
        this.stageList = res.data
      })
    },
    setValue(data) {
      let str = ''
      this.stageList.forEach((dd) => {
        if (dd.stageVal == data) {
          str = dd.stageKey
        }
      })
      return str
    },
    // updateBirthday() {
    //   if (!this.birthday || this.birthday == this.customer.birthday) {
    //     return
    //   }
    //   let data = {
    //     externalUserid: this.customer.externalUserid,
    //     birthday: this.birthday,
    //     firstUserId: this.customer.firstUserId
    //   }
    //   updateBirthday(data).then((response) => {
    //     this.msgSuccess('操作成功')
    //     // this.getDetail()
    //     this.$set(this.customer, 'birthday', this.birthday)
    //     // this.datePickerVisible = false
    //   })
    // },
    /**
     *客户详情基础(基础信息+社交关系)
     * @param {*}
     * externalUserid	是	当前客户id
     * userId	是	当前跟进人id
     */
    getDetail() {
      getDetail({ ...this.$route.query, delFlag: this.$route.query.isDelete ? 1 : 0 }).then(({ data }) => {
        data.companyTags && (data.companyTags = data.companyTags.split(','))
        this.customer = data

        if (data.trackUsers && data.trackUsers.length === 1) {
          this.openedTabs = [data.trackUsers[0].trackUserId]
        }
        this.birthday = data.birthday
      })
    },
    changeTab(tab) {
      this.openedTabs.includes(tab.$attrs.id) || this.openedTabs.push(tab.$attrs.id)
    },

    // remark(item) {
    //   return (
    //     item.remark ||
    //     this.customer.customerName + (item.remarkCorpName ? '-' + item.remarkCorpName : '')
    //   )
    // }
  },
}
</script>

<template>
  <div>
    <!-- <el-button slot="append" circle icon="el-icon-back" @click="$router.back()"></el-button>返回 -->
    <div class="g-card flex aic mb20" @click="goRoute(customer)">
      <el-image class="avatar" :src="customer.avatar" fit="fit"></el-image>
      <div class="ml10">
        <div>
          {{ customer.customerName + (customer.corpName ? '-' + customer.corpName : '') }}

          <i :class="['el-icon-s-custom', { 1: 'man', 2: 'woman' }[customer.gender]]"></i>
        </div>
        <div class="mt10">
          <span :style="{ color: customer.customerType === 1 ? '#4bde03' : '#f9a90b' }">
            {{ { 1: '@微信', 2: '@企业微信' }[customer.customerType] }}
          </span>
        </div>
        <div class="mt10 gap10">
          <template v-if="customer.tagNames">
            <el-tag v-for="(unit, unique) in customer.tagNames.split(',')" :key="unique">
              {{ unit }}
            </el-tag>
          </template>
          <div v-else>暂无标签</div>
        </div>
      </div>
    </div>

    <div class="g-card">
      <div class="g-card-title">社交关系</div>
      <el-tabs value="1">
        <el-tab-pane :label="`跟进员工(${customer.trackUsers ? customer.trackUsers.length : 0})`" name="1">
          <el-table :data="customer.trackUsers">
            <el-table-column label="员工" align="center" prop="userName" />
            <el-table-column prop="addMethod" label="添加方式" align="center">
              <template slot-scope="{ row }">{{ dictAddType[row.addMethod + ''] }}</template>
            </el-table-column>
            <el-table-column label="添加时间" align="center" prop="firstAddTime" />
            <el-table-column prop="trackState" label="商机阶段" align="center">
              <template slot-scope="{ row }">
                <!-- <el-tag v-if="row.trackState" :type="dictTrackState[~~row.trackState + ''].color">
                  {{ dictTrackState[~~row.trackState + ''].name }}
                </el-tag> -->
                <el-tag v-if="row.trackState">
                  {{ setValue(row.trackState) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane :label="`所在客群(${customer.groups ? customer.groups.length : 0})`" name="2">
          <el-table :data="customer.groups">
            <el-table-column label="群名" align="center" prop="groupName" />
            <el-table-column label="群主" align="center" prop="leaderName" />
            <el-table-column label="入群时间" align="center" prop="joinTime" />
            <el-table-column prop="joinScene" label="入群方式" align="center">
              <template slot-scope="{ row }">{{ dictJoinGroupType[row.joinScene + ''] }}</template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="g-card">
      <div class="g-card-title">客户画像</div>
      <el-tabs value="0" @tab-click="changeTab">
        <el-tab-pane v-if="customer.trackUsers && customer.trackUsers.length > 1" label="客户画像汇总" id="0">
          <info-tab :stageList="stageList" :trackUsers="customer.trackUsers"></info-tab>
        </el-tab-pane>
        <el-tab-pane
          v-for="(item, index) of customer.trackUsers"
          :key="index"
          :label="item.userName"
          :id="item.trackUserId">
          <info-tab
            v-if="openedTabs.includes(item.trackUserId)"
            :stageList="stageList"
            :userId="item.trackUserId"></info-tab>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.avatar {
  width: 80px;
  height: 80px;
  border-radius: var(--border-radius-big);
  flex: none;
}
.info-wrap {
  margin-left: 20px;
  .info {
    color: #aaa;
    line-height: 32px;
  }
}

.el-icon-s-custom {
  font-size: 16px;
  margin-left: 4px;
  color: #999;
  &.man {
    color: #13a2e8;
  }
  &.woman {
    color: #f753b2;
  }
}
.g-card-title {
  margin-bottom: 5px;
}
</style>
