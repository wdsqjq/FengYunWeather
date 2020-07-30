/**
* create by shiju.wang on 2019/9/9
*/

<template>
  <div>
    <el-form :inline="true" :model="paramForm" style="padding: 22px 35px 0 36px;">
      <el-form-item label="模块：">
        <el-select size="small" class="custom-input-width" v-model="paramForm.moduleName" placeholder="请选择">
          <el-option label="全部" value=""></el-option>
          <el-option label="开" value="1"></el-option>
          <el-option label="关" value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="类型：">
        <el-select size="small" class="custom-input-width" v-model="paramForm.type" placeholder="请选择">
          <el-option label="全部" value=""></el-option>
          <el-option label="开" value="1"></el-option>
          <el-option label="关" value="2"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="选择日期：" :label-width="85+'px'">
        <el-date-picker size="small"
                        v-model="paramForm.date"
                        type="daterange" value-format="timestamp"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>

      <div style="display: inline-block;padding-top: 6px;">
        <div class="common-fill-btn" style="margin-left: 10px;padding-left: 24px;padding-right: 24px;"
             @click="onSearch">
          <span>查询</span>
        </div>
        <div class="common-btn" style="margin-left: 10px;padding-left: 24px;padding-right: 24px;" @click="onReset">
          <span>重置</span>
        </div>
      </div>
    </el-form>

    <div style="background-color: #F6F6F6;height: 10px;padding: 0 -30px;">
    </div>

    <div style="padding: 0 36px;">
      <el-table
        ref="userTable"
        :data="tableData" :height="tableHeight"
        :header-cell-style="$headerCellStyle"
        style="width: 100%;margin-top: 25px;" v-loading="loading"
        @selection-change="handleSelectionChange">

        <el-table-column prop="moduleValue" align="center"
                         label="模块" min-width="160">
        </el-table-column>

        <el-table-column prop="operationType" align="center"
                         label="类型" min-width="180">
        </el-table-column>

        <el-table-column prop="moduleValue=" align="center"
                         label="内容" min-width="180">
          <template slot-scope="scope">
            {{scope.row.moduleValue ? scope.row.moduleValue : "--"}}
          </template>
        </el-table-column>

        <el-table-column prop="date" align="center"
                         label="时间" min-width="180">
          <template slot-scope="scope">
            {{scope.row.createDateLong ? $dateFormat(scope.row.createDateLong) : "--"}}
          </template>
        </el-table-column>

      </el-table>
      <!--分页加载-->
      <div class="pagination-div">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageIndex"
          :page-sizes="[10, 20, 30, 50,100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="recordCount">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "DiaryManager",
    data() {
      return {
        tableHeight: 359,

        paramForm: {
          moduleName: '',
          type: '',
          date: '',
        },


        tableData: [],
        pageIndex: 1,
        pageSize: 20,
        recordCount: 0,
        loading: false,

      }
    },
    created() {
      this.getData();
      window.addEventListener("resize",this.getHeight);
      this.getHeight();
    },

    methods: {
      getHeight(){
        this.tableHeight = document.documentElement.clientHeight - 260;
      },
      handleSizeChange(val) {
        this.pageSize = val;
        this.pageIndex = 1;
        this.getData();
      },

      handleCurrentChange(val) {
        this.pageIndex = val;
        this.getData();
      },

      onSearch() {
        this.getData();
      },

      onReset() {
        this.paramForm.moduleName = "";
        this.paramForm.type = "";
        this.paramForm.date = "";
      },

      getData() {
        this.loading = true;
        let request = {};
        request.currPage = this.pageIndex;
        request.pageSize = this.pageSize;
        // request.deviceId = this.deviceId;
        if (this.paramForm.date) {
          request.startTimeLong = this.paramForm.date[0];
          request.endTimeLong = this.paramForm.date[1];
        }

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/log/queryList', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.recordCount = res.result.totalCount;
            this.tableData = res.result.list;
            if (!res.result.list) {
              this.$message.error("数据为空");
            }
          } else {
            this.$message.error(res.msg)
          }
          this.loading = false;
        })
      },
    }
  }
</script>

<style scoped>

</style>
