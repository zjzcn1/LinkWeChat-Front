<script>
import * as api from '@/api/customer/group'
// import * as api from '@/api/customer/tag'
import SelectTag from '@/components/SelectTag'

export default {
  name: 'Group',
  components: { SelectTag },
  data() {
    return {
      // 查询参数
      query: {
        pageNum: 1,
        pageSize: 10,
        groupName: undefined,
        groupLeaderName: undefined,
        tagIds: undefined,
        beginTime: undefined,
        endTime: undefined,
      },
      // 日期范围
      dateRange: [],
      // 遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      lastSyncTime: '',
      // 总条数
      total: 0,
      // 表格数据
      list: [],
      queryTag: [], // 查询的群标签
      dialogVisible: false, // 选择标签弹窗显隐
      selectedTag: [], // 选择的标签
      tagDialogType: {
        title: '选择标签', // 选择标签弹窗标题
        type: '', // 弹窗类型
      },
      makeLabelCustomerList: [],
    }
  },
  computed: {},
  created() {
    this.getList()
  },
  methods: {
    /** 查询 */
    getList(page) {
      if (this.dateRange) {
        this.query.beginTime = this.dateRange[0]
        this.query.endTime = this.dateRange[1]
      } else {
        this.query.beginTime = ''
        this.query.endTime = ''
      }
      page && (this.query.pageNum = page)
      this.query.tagIds = this.queryTag.map((d) => d.tagId) + ''
      this.loading = true
      api
        .getList(this.query)
        .then(({ rows, lastSyncTime, total }) => {
          this.list = rows.map((i) => {
            // 处理标签
            if (i.tags && typeof i.tags == 'string') {
              i.tags = i.tags.split(',')
            }
            if (i.tagIds && typeof i.tagIds == 'string') {
              i.tagIds = i.tagIds.split(',')
            }
            return i
          })
          this.lastSyncTime = lastSyncTime
          this.total = +total
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    showTagDialog() {
      this.selectedTag = this.queryTag
      this.tagDialogType = {
        title: '选择标签',
        type: 'query',
      }
      this.dialogVisible = true
      // this.$refs.selectTag.$forceUpdate()
    },
    makeTag(data) {
      this.makeLabelCustomerList = data || this.ids
      this.tagDialogType.type = ''
      let curTags = []
      if (data.tags && data.tagIds) {
        curTags = data.tags.map((i, idx) => ({
          name: i,
          tagName: i,
          tagId: data.tagIds[idx],
        }))
      }
      this.selectedTag = curTags
      this.dialogVisible = true
    },
    submitSelectTag(selected) {
      if (this.tagDialogType.type === 'query') {
        this.queryTag = selected
        this.dialogVisible = false
      } else {
        this.loading = true
        let data = {
          chatId: this.makeLabelCustomerList.chatId,
          tagIds: selected.map((row) => {
            return row.tagId
          }),
        }
        api
          .makeGroupTag(data)
          .then((res) => {
            if (res.code == 200) {
              this.msgSuccess('操作成功')
              this.getList()
              this.loading = false
            }
          })
          .finally(() => {
            this.loading = false
            this.dialogVisible = false
          })
      }
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = []
      this.queryTag = []
      this.queryUser = []
      this.$refs['queryForm'].resetFields()
      this.getList(1)
    },
    sync() {
      // const loading = this.$loading({
      //   lock: true,
      //   text: 'Loading',
      //   spinner: 'el-icon-loading',
      //   background: 'rgba(0, 0, 0, 0.7)'
      // })
      api.sync().then((r) => {
        // loading.close()
        this.getList(1)
        this.msgSuccess('后台开始同步数据，请稍后关注进度')
      })
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.multiple = !selection.length
    },
    // /** 导出按钮操作 */
    // handleExport() {
    //   const query = this.query;
    //   this.$confirm("是否确认导出所有数据项?", "警告", {
    //     confirmButtonText: "确定",
    //     cancelButtonText: "取消",
    //     type: "warning",
    //   })
    //     .then(function () {
    //       return exportOperlog(query);
    //     })
    //     .then((response) => {
    //       this.download(response.msg);
    //     })
    //     .catch(function () {});
    // }
  },
}
</script>

<template>
  <div class="">
    <el-form :model="query" ref="queryForm" :inline="true" class="top-search" label-width="80px">
      <el-form-item label="群名" prop="groupName">
        <el-input clearable v-model="query.groupName" placeholder="请输入群名" clearable />
      </el-form-item>
      <el-form-item label="群主" prop="groupLeaderName">
        <el-input clearable v-model="query.groupLeaderName" placeholder="请输入群主" clearable />
      </el-form-item>
      <el-form-item label="群标签">
        <div class="tag-input" @click="showTagDialog">
          <span class="tag-place" v-if="!queryTag.length">请选择</span>
          <template v-else>
            <el-tag v-for="(unit, unique) in queryTag" :key="unique">{{ unit.name }}</el-tag>
          </template>
        </div>
      </el-form-item>
      <el-form-item label="创建日期">
        <el-date-picker
          clearable
          v-model="dateRange"
          value-format="yyyy-MM-dd"
          type="daterange"
          :picker-options="pickerOptions"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="">
        <!-- v-hasPermi="['customerManage:group:query']" -->
        <el-button type="primary" @click="getList(1)">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
        <!-- v-hasPermi="['customerManage:group:query']" -->
        <!-- <el-button v-hasPermi="['customerManage:group:export']" type="success" @click="handleExport"
          >导出列表</el-button
        > -->
      </el-form-item>
    </el-form>

    <div class="g-card">
      <div class="g-card-title mid-action">
        <div class="">
          共
          <span class="g-color" style="color: var(--color)">{{ total }}</span>
          个客户群
        </div>
        <div>
          <span class="desc">最近同步：{{ lastSyncTime }}</span>
          <ButtonSync :lastSyncTime="lastSyncTime" @click="sync">同步客户群</ButtonSync>
          <!-- <el-button v-hasPermi="['customerManage:group:sync']" type="primary" plain @click="sync"
          >批量标签设置</el-button
        > -->
        </div>
      </div>

      <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="群名" align="center" prop="groupName" />
        <el-table-column label="群主" align="center" prop="groupLeaderName" />
        <el-table-column label="群人数" align="center" prop="memberNum" />
        <el-table-column prop="tags" label="群标签" align="center">
          <div v-if="row.tags" slot-scope="{ row }">
            <el-popover placement="bottom" trigger="hover">
              <div>
                <el-tag v-for="(unit, unique) in row.tags" :key="unique">
                  {{ unit }}
                </el-tag>
              </div>
              <div slot="reference">
                <el-tag v-for="(unit, unique) in row.tags.slice(0, 2)" :key="unique">
                  {{ unit }}
                </el-tag>
                <el-tag key="a" v-if="row.tags.length > 2">...</el-tag>
              </div>
            </el-popover>
          </div>
          <span v-else>无标签</span>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180"></el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              type="text"
              @click="
                $router.push({
                  path: './groupDetail',
                  query: { chatId: scope.row.chatId },
                })
              ">
              查看详情
            </el-button>
            <!-- v-hasPermi="['customerManage:group:view']" -->
            <el-button type="text" @click="makeTag(scope.row)">标签管理</el-button>
            <!-- v-hasPermi="['customerManage/customer:makeTag']" -->
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="query.pageNum"
        :limit.sync="query.pageSize"
        @pagination="getList()" />
    </div>

    <!-- 选择标签弹窗 -->
    <SelectTag
      ref="selectTag"
      :visible.sync="dialogVisible"
      type="2"
      :title="tagDialogType.title"
      :defaultValues="selectedTag"
      @success="submitSelectTag"></SelectTag>
  </div>
</template>

<style lang="scss" scoped></style>
