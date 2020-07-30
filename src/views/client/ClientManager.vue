/**
* create by shiju.wang on 2019/9/9
*/

<template>
  <div>
    <el-form :inline="true" :model="formInline" style="padding: 22px 35px 0 36px;">
      <el-form-item label="姓名：">
        <el-input size="small" class="client-input" v-model="formInline.name" placeholder="请输入"></el-input>
      </el-form-item>

      <el-form-item label="序列号：">
        <el-input size="small" class="client-input" v-model="formInline.sn" placeholder="请输入"></el-input>
      </el-form-item>

      <el-form-item label="客户端归属：">
        <el-select size="small" class="client-input" clearable v-model="formInline.userId" placeholder="请选择">
          <el-option v-for="item in userList"
                     :label="item.name"
                     :value="item.id"
                     :key="item.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="灌装状态：">
        <el-select size="small" class="client-input" v-model="formInline.status" placeholder="请选择">
          <el-option label="全部" value=""></el-option>
          <el-option label="开" value="1"></el-option>
          <el-option label="关" value="2"></el-option>
        </el-select>
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

    <div class="manager-div">
      <div>
        <div class="common-fill-btn" style="padding-left: 24px;padding-right: 24px;" @click="onAdd">
          <img class="common-btn-img" src="../../assets/img/add.svg">
          <span>添加</span>
        </div>

        <div class="common-btn" style="margin-left: 30px;" @click="onBatchDelete">
          <img class="common-btn-img" src="../../assets/img/delete.svg">
          <span>批量删除</span>
        </div>

        <!--<el-radio style="margin-left: 30px;" v-model="batchSwitch" label="1">批量开</el-radio>-->
        <!--<el-radio v-model="batchSwitch" label="2">批量关</el-radio>-->
      </div>

      <el-table
        ref="userTable"
        :data="tableData"
        :header-cell-style="$headerCellStyle"
        style="width: 100%;margin-top: 25px;" v-loading="loading"
        @selection-change="handleSelectionChange">

        <el-table-column
          align="center" type="selection" width="60">
        </el-table-column>

        <el-table-column prop="name" align="center"
                         label="名称" min-width="180">
        </el-table-column>

        <el-table-column prop="sn" align="center"
                         label="设备序列号" min-width="180">
        </el-table-column>

        <el-table-column prop="userName" align="center"
                         label="客户端归属" min-width="180">
          <template slot-scope="scope">
            {{scope.row.userName ? scope.row.userName : "--"}}
          </template>
        </el-table-column>

        <el-table-column prop="subCount" align="center"
                         label="灌装开关" min-width="180">
          <template slot-scope="scope">
            <el-switch
              @change="onSwitchChange(scope.row)"
              v-model="scope.row.status"
              active-color="#5D8ADE"
              inactive-color="#BBBBBB"
              active-value="1"
              inactive-value="2">
            </el-switch>
          </template>
        </el-table-column>

        <el-table-column prop="operation" align="center"
                         label="查看" min-width="180">
          <template slot-scope="scope">
            <img style="width: 25px;cursor: pointer;" src="../../assets/img/search-gray.svg"
                 @click="itemOper(scope.row,2)">
          </template>
        </el-table-column>

        <el-table-column prop="operation" align="center"
                         label="操作" min-width="180">

          <template slot-scope="scope">
            <el-button type="text" @click="itemOper(scope.row,1)" style="margin: 0px 5px 0px 5px"
                       size="small">编辑
            </el-button>
            <span>|</span>
            <el-button type="text" @click="itemOper(scope.row,3)"
                       style="margin: 0px 5px 0px 5px;color: #ff6868"
                       size="small">删除
            </el-button>
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

      <!--删除操作-->
      <el-dialog
        title="删除设备"
        :visible.sync="isShowDelete"
        width="500px" left>
        <div style="font-size: 15px;color: #333333;margin-left: 15px">
          您正在删除设备 {{currentRow.name}}，是否确认?
        </div>
        <div slot="footer" class="dialog-footer">
          <el-button class="dialog-btn" type="primary" @click="confirmDel">确 定</el-button>
          <el-button class="dialog-btn" style="margin-left: 80px;" @click="isShowDelete = false">取 消</el-button>
        </div>
      </el-dialog>

    </div>
  </div>
</template>

<script>
  export default {
    name: "ClientManager",
    data() {
      return {
        selectedItems: [],
        tableData: [],
        pageIndex: 1,
        pageSize: 20,
        recordCount: 0,
        loading: false,

        formInline: {
          name: '',
          sn: '',
          userId: '',
          status: '',
        },

        userList: [],

        currentRow: '',
        isShowDelete: false,
        // batchSwitch: '1',
      }
    },
    created() {
      this.getData();
      this.getUserList();
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
        this.selectedItems = val;
      },

      onSwitchChange(row) {
        let request = {};
        request.id = row.id;
        request.sn = row.sn;
        request.name = row.name;
        request.status = row.status;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/device/onoff', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success("操作成功");
          } else {
            this.$message.error(res.msg);
          }
        })

      },

      getData() {
        this.loading = true;
        let request = {};
        request.currPage = this.pageIndex;
        request.pageSize = this.pageSize;
        request.name = this.formInline.name;
        request.sn = this.formInline.sn;
        request.status = this.formInline.status;
        request.userId = this.formInline.userId;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/device/queryList', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.recordCount = res.result.totalCount;
            this.tableData = res.result.list;
            if (!res.result.list) {
              this.$message.error("数据为空");
            } else {
              this.tableData.forEach(it => {
                it.status = it.status.toString();
              });
            }
          } else {
            this.$message.error(res.msg)
          }
          this.loading = false;
        })
      },

      // 获取设备归属列表
      getUserList() {
        let request = {};

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/getUsers', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.userList = res.result;
          } else {
            this.$message.error(res.msg)
          }
        })
      },

      onSearch() {
        this.getData();
      },

      onReset() {
        this.formInline.status = "";
        this.formInline.sn = "";
        this.formInline.name = "";
        this.formInline.userId = "";
      },

      itemOper(row, index) {
        this.currentRow = row;
        if (index === 1) {        // 编辑
          this.$router.push({name: '设备管理', params: {type: 2, deviceId: row.id}});
        } else if (index === 2) { // 查看
          this.$router.push({name: '设备日志', params: {deviceId: row.id}});
        } else {                // 删除
          this.isShowDelete = true;
        }
      },

      onAdd() {
        this.$router.push({name: '设备管理', params: {type: 1, deviceId: -1}});
      },

      onBatchDelete() {
        if (!this.selectedItems || this.selectedItems.length === 0) {
          this.$message.error("请选择要删除的设备");
          return;
        }
        let request = [];

        this.selectedItems.forEach(it => {
          request.push({'id': it.id, 'name': it.name, 'sn': it.sn, 'status': it.status})
        });

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/device/batchDelete', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success(res.msg);
            this.getData();
          } else {
            this.$message.error(res.msg);
          }
        })
      },

      // 删除设备
      confirmDel() {
        this.loading = true;
        let request = {};
        request.id = this.currentRow.id;
        request.name = this.currentRow.name;
        request.sn = this.currentRow.sn;
        request.status = 0;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/device/delete', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success("操作成功");
            this.getData();
          } else {
            this.$message.error(res.msg)
          }
          this.loading = false;
          this.isShowDelete = false;
        });

      },

      onSubmit() {
        console.log('submit!');
      }
    }
  }
</script>

<style scoped>
  .client-input {
    width: 155px;
  }
</style>
