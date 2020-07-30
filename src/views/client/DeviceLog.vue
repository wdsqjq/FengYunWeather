/**
* create by shiju.wang on 2019/9/16
*/

<template>
  <div style="padding: 26px 36px 0 36px;">
    <p v-if="false" style="font-size: 14px;cursor: pointer" @click="back2List">< 返回客户列表</p>

    <el-table
      ref="userTable"
      :data="tableData"
      :header-cell-style="$headerCellStyle"
      style="width: 100%;margin-top: 25px;" v-loading="loading"
      @selection-change="handleSelectionChange">

      <el-table-column prop="deviceName" align="center"
                       label="名称" min-width="160">
      </el-table-column>

      <el-table-column prop="deviceSn" align="center"
                       label="设备序列号" min-width="180">
      </el-table-column>

      <el-table-column prop="userName" align="center"
                       label="请求时间" min-width="180">
        <template slot-scope="scope">
          {{scope.row.reqTimeLong ? scope.row.reqTimeLong : "--"}}
        </template>
      </el-table-column>

      <el-table-column prop="userName" align="center"
                       label="响应时间" min-width="180">
        <template slot-scope="scope">
          {{scope.row.respTimeLong ? scope.row.respTimeLong : "--"}}
        </template>
      </el-table-column>

      <el-table-column prop="city" align="center"
                       label="灌装地点" min-width="180">
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
</template>

<script>
  export default {
    name: "DeviceLog",
    data() {
      return {
        tableData: [],
        pageIndex: 1,
        pageSize: 20,
        recordCount: 0,
        loading: false,

        deviceId:'',
      }
    },

    created() {
      this.deviceId = this.$router.currentRoute.params.deviceId * 1;

      this.getData();
    },

    methods: {
      handleSizeChange(val) {
        this.pageSize = val;
        this.pageIndex = 1;
        this.getData();
      },

      handleCurrentChange(val) {
        this.pageIndex = val;
        this.getData();
      },

      handleSelectionChange(val) {
      },

      back2List(){
        this.$router.push({path:'/client'});
      },

      getData() {
        this.loading = true;
        let request = {};
        request.currPage = this.pageIndex;
        request.pageSize = this.pageSize;
        request.deviceId = this.deviceId;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/deviceRecord/queryList', request, {
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
