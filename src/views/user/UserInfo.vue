/**
* create by shiju.wang on 2019/9/11
*/

<template>
    <div style="width: 480px;padding-left: 200px;padding-top: 50px;">

      <el-form ref="form" :model="form" :rules="rules" label-width="98px">
        <el-form-item label="账号：" prop="account">
          <el-input size="medium" v-model="form.account"></el-input>
        </el-form-item>

        <el-form-item label="姓名：" prop="name">
          <el-input size="medium" v-model="form.name"></el-input>
        </el-form-item>

        <el-form-item label="手机号：">
          <el-input size="medium" v-model="form.mobile"></el-input>
        </el-form-item>

        <el-form-item label="邮箱：">
          <el-input size="medium" v-model="form.email"></el-input>
        </el-form-item>

        <el-form-item label="管理子用户：">
          <el-select multiple style="width: 100%" size="medium" v-model="form.childUsers" placeholder="请选择子用户">
            <el-option v-for="item in userList"
                       :label="item.name"
                       :value="item.id"
                       :key="item.id"></el-option>
          </el-select>
          </el-select>
        </el-form-item>

        <el-form-item style="padding-top: 24px;">
          <el-button size="medium" style="width: 100px;" type="primary" @click="onSubmit">确  定</el-button>
          <el-button style="margin-left: 40px;width: 100px;" size="medium" @click="onCancel">取  消</el-button>
        </el-form-item>
      </el-form>
    </div>
</template>

<script>
    export default {
        name: "Userinfo",

      data() {
        return {
          type: "",
          userId:0,
          form: {
            account: '',
            name: '',
            mobile:'',
            email:'',

            childUsers: [],
          },
          rules: {
            account: [
              { required: true, message: '请输入账号', trigger: 'blur' },
            ],
            name: [
              { required: true, message: '请输入用户名', trigger: 'blur' },
            ],
          },
          userList:[],
        }
      },

      created() {
          // console.log(this.$router.currentRoute.params);
          this.type=this.$router.currentRoute.params.type;
          this.userId=this.$router.currentRoute.params.userId*1;

          this.getUserList();

          if(this.type===2){// 编辑用户
              this.getUserInfo();
          }
      },

      methods: {
        onSubmit() {
          this.$refs.form.validate((valid) => {
            if (valid) {
              this.saveData();
            }
          });
        },
        saveData(){
          let request = {};
          request.account = this.form.account;
          request.name = this.form.name;
          request.mobile = this.form.mobile;
          request.email = this.form.email;
          request.sub = this.form.childUsers.toString();
          // request.name = this.form.account;
          let url = "";
          if(this.type===1){
            url = "/KmsPlant-web/api/console/user/add";
          }else{
            url = "/KmsPlant-web/api/console/user/update";
            request.id = this.userId;
          }

          let token = this.$store.getters.userInfo.token;
          this.$http.post(url, request, {
            headers: {
              'userToken': token,
            }
          }).then(res => {
            if (res.code === '0000') {
              this.$message.success("成功");
              this.$router.push({path:'/user'});
            } else {
              this.$message.error(res.msg)
            }
          });
        },
        onCancel(){
          this.$router.push({path:'/user'});
        },

        getUserInfo(){
          let request = {};
          request.id = this.userId;
          let token = this.$store.getters.userInfo.token;
          this.$http.post('/KmsPlant-web/api/console/user/getUserById', request, {
            headers: {
              'userToken': token,
            }
          }).then(res => {
            if (res.code === '0000') {
              this.form.account = res.result.account;
              this.form.name = res.result.name;
              this.form.mobile = res.result.mobile;
              this.form.email = res.result.email;
              this.form.childUsers = res.result.sub.split(",");

            } else {
              this.$message.error(res.msg)
            }
          });
        },

        // 获取用户列表
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
      },
    }
</script>

<style scoped>

</style>
