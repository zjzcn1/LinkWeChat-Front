<template>
  <div>
    <el-form ref="queryForm" :model="query" inline class="top-search" label-position="left" label-width="">
      <el-form-item label="" prop="fassionName">
        <el-input clearable v-model="query.fassionName" placeholder="请输入任务名称"></el-input>
      </el-form-item>
      <el-form-item label="" prop="fassionState">
        <el-select :popper-append-to-body="false" v-model="query.fassionState" placeholder="请选择任务状态">
          <el-option label="全部任务状态" value=""></el-option>
          <el-option v-for="(item, index) in statusType" :key="index" :label="item.name" :value="item.key"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="">
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <div class="g-card">
      <div class="mid-action">
        <el-button type="primary" @click="$router.push('./add')">新建任务</el-button>
        <div>
          <el-button type="primary" plain :disabled="selectList.length == 0" @click="deleteMult">
            批量删除
          </el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" @selection-change="handleSelectionChange">
        <el-table-column
          type="selection"
          width="55"
          align="center"
          fixed="left"
          :selectable="selectable"
        ></el-table-column>
        <el-table-column prop="fassionName" label="任务名称" align="center" width="120" fixed="left"></el-table-column>
        <el-table-column prop="statusType" label="任务状态" align="center" width="100">
          <template #default="{ row }">
            <el-tag :type="(row.fassionState == 1 ? 'info' : (row.fassionState == 3 ? 'danger' : ''))">{{
              setStatus(row)
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fassionStartTime" label="任务开始时间" align="center" width="180">
          <template #default="{ row }">
            {{ row.fassionStartTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="fassionEndTime" label="任务结束时间" align="center" width="180">
          <template #default="{ row }">
            {{ row.fassionEndTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="inviterOldCustomerNum" label="员工触达客群总数" align="center" width="150">
          <template slot="header">
            <el-popover placement="top" trigger="hover">
              <div slot="reference">
                员工触达客群总数
                <i class="el-icon-question"></i>
              </div>
              <div>在当前任务中员工已送达的客群总数</div>
            </el-popover>
          </template>
          <template #default="{ row }">
            {{ row.inviterOldCustomerNum == null ? '-' : row.inviterOldCustomerNum }}
          </template>
        </el-table-column>
        <el-table-column prop="completeTaskOldCustomerNum" label="完成任务客户总数" align="center" width="150">
          <template slot="header">
            <el-popover placement="top" trigger="hover">
              <div slot="reference">
                完成任务客户总数
                <i class="el-icon-question"></i>
              </div>
              <div>在当前任务中完成裂变任务的客户总数</div>
            </el-popover>
          </template>
          <template #default="{ row }">
            {{ row.completeTaskOldCustomerNum == null ? '-' : row.completeTaskOldCustomerNum }}
          </template>
        </el-table-column>
        <el-table-column prop="fissionCustomerNum" label="裂变新客总数" align="center" width="140">
          <template slot="header">
            <el-popover placement="top" trigger="hover">
              <div slot="reference">
                裂变新客总数
                <i class="el-icon-question"></i>
              </div>
              <div>在当前任务中成功添加客群的新客总数</div>
            </el-popover>
          </template>
          <template #default="{ row }">
            {{ row.fissionCustomerNum == null ? '-' : row.fissionCustomerNum }}
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" align="center"></el-table-column>
        <el-table-column prop="updateTime" label="最近操作时间" align="center" width="180"></el-table-column>
        <el-table-column label="操作" align="center" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="mini" @click="$router.push({ path: './detail', query: { id: row.id } })">
              详情|统计</el-button
            >
            <el-button
              type="text"
              size="mini"
              v-if="row.fassionState == 1"
              @click="$router.push({ path: './add', query: { id: row.id } })"
              >编辑</el-button
            >
            <el-button type="text" v-if="row.fassionState !== 2" size="mini" @click="deleteFn(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="query.pageNum"
        :limit.sync="query.pageSize"
        @pagination="getList"
      />
    </div>
  </div>
</template>
<script>
  import { getList, deleteFassion, deleteMultFa } from '../taskGroup/api'
  export default {
    name: 'task-group-list',
    data() {
      return {
        statusType: [
          { name: '待开始', key: 1 },
          { name: '进行中', key: 2 },
          { name: '已结束', key: 3 }
        ],
        query: {
          fassionType: 2, //1 任务宝  2群裂变
          pageSize: 10,
          pageNum: 1,
          fassionName: '',
          fassionState: ''
        },
        total: 0,
        loading: false,
        list: [],
        selectList: []
      }
    },
    methods: {
      selectable(row, index) {
        if (row.fassionState !== 2) {
          return true
        }
      },
      setType(data) {
        let str = ''
        this.infoType.forEach((dd) => {
          if (dd.key == data.type) {
            str = dd.name
          }
        })
        return str
      },
      setStatus(data) {
        let str = ''
        this.statusType.forEach((dd) => {
          if (dd.key == data.fassionState) {
            str = dd.name
          }
        })
        return str
      },
      getList() {
        this.loading = true
        getList(this.query).then((res) => {
          this.total = Number(res.total)
          this.list = res.rows
          this.loading = false
        })
      },
      handleSearch() {
        this.query.pageNum = 1
        this.getList()
      },
      resetQuery() {
        this.query.pageNum = 1
        this.$refs['queryForm'].resetFields()
        this.getList()
      },
      handleSelectionChange(e) {
        this.selectList = e.map((dd) => dd.id)
      },
      deleteFn(data) {
        this.$confirm('是否确认删除当前裂变任务？删除后不可撤销，请谨慎操作。', '删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            return deleteFassion(data.id)
          })
          .then(() => {
            this.handleSearch()
            this.msgSuccess('删除成功')
          })
      },
      deleteMult() {
        if (!this.selectList.length) {
          this.msgInfo('请选择要删除项！')
          return
        }
        const ids = this.selectList.join(',')
        this.$confirm('是否确认删除当前裂变任务？删除后不可撤销，请谨慎操作。', '警告', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            return deleteMultFa(ids)
          })
          .then(() => {
            this.handleSearch()
            this.msgSuccess('删除成功')
          })
          .catch(function () {})
      }
    },
    created() {
      this.getList()
      this.$store.dispatch(
        'app/setBusininessDesc',
        `
        <div>通过任务形式，引导老客裂变新客加群，实现客群增长</div>
      `
      )
    }
  }
</script>
<style lang="scss" scoped>
  .self_a {
    color: #07c160;
    text-decoration: underline;
    &:hover {
      opacity: 0.8;
    }
  }
</style>
