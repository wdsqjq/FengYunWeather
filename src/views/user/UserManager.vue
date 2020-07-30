/**
* create by shiju.wang on 2019/9/9
*/

<template>
  <div class="manager-div">
    <div>
      <div class="common-fill-btn" @click="addUser">
        <img class="common-btn-img" src="../../assets/img/add.svg">
        <span>添加</span>
      </div>

      <div class="common-btn" style="margin-left: 30px;" @click="batchImport">
          <img class="common-btn-img" src="../../assets/img/import.svg">
          <span>批量导入</span>
      </div>
      <div class="common-btn" style="margin-left: 30px;" @click="batchDelete">
        <img class="common-btn-img" src="../../assets/img/delete.svg">
        <span>批量删除</span>
      </div>

      <div class="fr search-div" @click="getData">
        <img style="width: 22px;" src="../../assets/img/search.svg">
      </div>
      <el-input class="fr" style="width: 300px;" v-model="keyword" size="small" placeholder="请输入内容"></el-input>

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

      <el-table-column prop="account" align="center"
                       label="账号" min-width="180">
      </el-table-column>

      <el-table-column prop="name" align="center"
                       label="姓名" min-width="180">
      </el-table-column>

      <el-table-column prop="subCount" align="center" sortable
                       label="子用户数量" min-width="180">
        <template slot-scope="scope">
          {{scope.row.sub ? scope.row.sub.split(",").length : "--"}}
        </template>
      </el-table-column>

      <el-table-column prop="operation" align="center"
                       label="操作" min-width="180">

        <template slot-scope="scope">
          <el-button type="text" @click="itemOper(scope.row,1)" style="margin: 0px 5px 0px 5px"
                     size="small">编辑
          </el-button>
          <span>|</span>
          <el-button type="text" @click="itemOper(scope.row,2)" style="margin: 0px 5px 0px 5px"
                     size="small">重置密码
          </el-button>
          <span>|</span>
          <el-button type="text" @click="itemOper(scope.row,3)"
                     style="margin: 0px 5px 0px 5px;color: #ff6868"
                     :disabled="!editAble"
                     v-bind:style="{color:!editAble ? '#cccccc' : '#ff6868'}"
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

    <!--重置密码操作-->
    <el-dialog
      title="重置密码"
      :visible.sync="isShowResetPwd"
      width="500px" left>
      <div style="padding:8px 50px 0 50px">
        <el-form ref="newPwd" :model="newPwd" label-width="80px">
          <el-form-item label="新密码">
            <el-input size="small" v-model="newPwd.pwd1" onfocus="this.type='password'"></el-input>
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input size="small"  v-model="newPwd.pwd2" onfocus="this.type='password'"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button class="dialog-btn" type="primary" @click="confirmReset">确 定</el-button>
        <el-button class="dialog-btn" style="margin-left: 80px;" @click="isShowResetPwd = false">取 消</el-button>
      </div>
    </el-dialog>

    <!--删除操作-->
    <el-dialog
      title="删除用户"
      :visible.sync="isShowDelete"
      width="500px" left>
      <div style="font-size: 15px;color: #333333;margin-left: 15px">
        您正在删除用户 {{currentRow.name}}，是否确认?
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button class="dialog-btn" type="primary" @click="confirmDel">确 定</el-button>
        <el-button class="dialog-btn" style="margin-left: 80px;" @click="isShowDelete = false">取 消</el-button>
      </div>
    </el-dialog>

    <!--批量导入-->
    <el-dialog
      title="批量导入"
      :visible.sync="isShowImport"
      width="500px" left>
      <div style="padding:8px 50px 0 50px">
        <h3>（1） 准备模板信息</h3>
        <h4 style="margin-left: 38px">请下载模板，按规定录入信息。</h4>
        <div style="margin-left: 38px;color: #5D8ADE;cursor: pointer" @click="downloadTemplate">
          <img src="../../assets/img/download.svg" style="width: 12px;"> <span>点击下载模板</span>
        </div>
        <h3 style="margin-top: 18px;">（2） 上传文件</h3>
        <div style="width: 100%;height: 35px;margin: 10px 0 0 35px">
          <div style="font-size: 14px;color: #ABABAB;width: 80px;line-height: 35px;text-align: center;float: left;">
            选择文件：
          </div>
          <div class="fileinput-button">
            <input class="fileinput-path"
                   type="text"
                   readonly="readonly"
                   :value="fileUpload"/>
            <input class="fileinput" type="file" ref="file"
                   name="file" @change="getFile"/>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button class="dialog-btn" type="primary" @click="confirmImport">确 定</el-button>
        <el-button class="dialog-btn" style="margin-left: 80px;" @click="isShowImport = false">取 消</el-button>
      </div>
    </el-dialog>

  </div>

</template>

<script>
  export default {
    name: "UserManager",
    data() {
      return {
        selectedItems: [],
        tableData: [],
        pageIndex: 1,
        pageSize: 20,
        recordCount: 0,
        loading: false,

        keyword: '',
        currentRow:'',

        newPwd:{
          pwd1:'',
          pwd2:'',
        },

        editAble: true,
        isShowResetPwd: false,
        isShowDelete: false,
        isShowImport: false,

        fileUpload: '上传文件',
        file: null,
      }
    },
    created() {
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
        this.selectedItems = val;
        console.log(this.selectedItems);
      },

      getData() {
        this.loading = true;
        let request = {};
        request.keyword = this.keyword;
        request.currPage = this.pageIndex;
        request.pageSize = this.pageSize;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/queryList', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.recordCount = res.result.totalCount;
            this.tableData = res.result.list;
          } else {
            this.$message.error(res.msg)
          }
          this.loading = false;
        })
      },

      addUser() {
        this.$router.push({ name:'用户编辑',params:{type:1,userId:-1}});
      },

      // 批量导入
      batchImport(){
        this.isShowImport = true;
      },

      // 批量删除
      batchDelete(){
        if(!this.selectedItems || this.selectedItems.length===0){
          this.$message.error("请选择要删除的用户");
          return;
        }
        this.loading = true;

        let userList= [];
        this.selectedItems.forEach(it=>{
          let item = {'id':it.id,'name':it.name,'status':0};
          userList.push(item);
        });

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/batchDelete', userList, {
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

      itemOper(row,index){
        this.currentRow = row;
          if(index===1){        // 编辑
            this.$router.push({name:'用户编辑',params:{type:2,userId:row.id}});
          }else if(index ===2){ // 查看
            this.isShowResetPwd = true;
          }else{                // 删除
            this.isShowDelete = true;
          }
      },

      // 删除用户
      confirmDel(){
        this.loading = true;
        let request = {};
        request.id = this.currentRow.id;
        request.name = this.currentRow.name;
        request.status = 0;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/delete', request, {
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

      // 重置密码
      confirmReset(){
        if(!this.newPwd.pwd1 || !this.newPwd.pwd2){
          this.$message.error("请输入密码");
          return;
        }
        if(this.newPwd.pwd1!==this.newPwd.pwd2){
          this.$message.error("请保持两个密码一致");
          return;
        }
        this.loading = true;

        let request = {};
        request.id = this.currentRow.id;
        request.name = this.currentRow.name;
        request.password = this.newPwd.pwd1;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/resetPwd', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success("修改成功");
          } else {
            this.$message.error(res.msg);
          }
          this.loading = false;
          this.isShowResetPwd = false;
        });
      },

      confirmImport(){
        if(!this.file){
          this.$message.error("请选择要导入的文件");
        }
        let formData = new FormData();
        formData.append('file', this.file);

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/importFromUser', formData, {
          headers: {
            'userToken': token,
            'Content-Type': 'multipart/form-data',
            // 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8',
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success("修改成功");
          } else {
            this.$message.error(res.msg);
          }
          this.isShowResetPwd = false;
        });
      },

      getFile(){
        this.file = this.$refs.file.files[0];
        this.fileUpload = this.$refs.file.files[0].name;
      },

      // 下载用户模板
      downloadTemplate(){
        let url = "/KmsPlant-web/api/console/user/exportUserTemplate";
        let form = document.createElement("form");
        // let token = this.$store.getters.userInfo.corpId;
        // let corpId = document.createElement("input");
        // corpId.name = "corpId";
        // corpId.value = token;
        // form.appendChild(corpId);

        form.style.display = 'none';
        form.method = 'get';
        form.action = url;
        document.body.appendChild(form);
        form.submit();
      },

    }
  }
</script>

<style scoped>

  h3{
    font-weight: bold;
  }
  h4{
    font-weight: normal;
    margin: 12px 0;
  }

  .fileinput-button {
    position: relative;
    display: inline-block;
    overflow: hidden;
    float: left;
  }

  .fileinput-path {
    line-height: 32px;
    float: left;
    border: 1px solid #cecece;
    border-radius: 4px 4px 4px 4px;
    color: #a9a9a9;
    padding-left: 10px;
  }

  .fileinput {
    position: absolute;
    right: 0px;
    top: 0px;
    opacity: 0;
    -ms-filter: 'alpha(opacity=0)';
    font-size: 16px;
  }

</style>
