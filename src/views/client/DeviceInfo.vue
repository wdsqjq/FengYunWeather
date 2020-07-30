/**
* create by shiju.wang on 2019/9/12
*/

<template>
  <div style="width: 480px;padding-left: 200px;padding-top: 50px;">

    <el-form ref="form" :model="form" :rules="rules" label-width="98px">
      <el-form-item label="名称：" prop="name">
        <el-input size="medium" v-model="form.name" placeholder="请输入名称"></el-input>
      </el-form-item>

      <el-form-item label="序列号：" prop="sn">
        <el-input size="medium" v-model="form.sn" placeholder="请输入序列号"></el-input>
      </el-form-item>

      <el-form-item style="padding-top: 24px;">
        <el-button size="medium" style="width: 100px;" type="primary" @click="onSubmit">确 定</el-button>
        <el-button style="margin-left: 40px;width: 100px;" size="medium" @click="onCancel">取 消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  export default {
    name: "DeviceInfo",
    data() {
      return {
        type: "",
        deviceId: 0,
        form: {
          name: '',
          sn: '',
        },
        rules: {
          name: [
            {required: true, message: '请输入名称', trigger: 'blur'},
          ],
          sn: [
            {required: true, message: '请输入序列号', trigger: 'blur'},
          ],
        },
        userList: [],
      }
    },
    created() {
      this.type = this.$router.currentRoute.params.type;
      this.deviceId = this.$router.currentRoute.params.deviceId * 1;

      if(this.type===2){
        this.getDeviceInfo();
      }
    },

    methods: {
      getDeviceInfo(){
        let request = {};
        request.id = this.deviceId;

        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/device/getDeviceById', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.form.name = res.result.name;
            this.form.sn = res.result.sn;
          } else {
            this.$message.error(res.msg)
          }
        });
      },
      onSubmit() {
        this.$refs.form.validate((valid) => {
          if (valid) {
            this.saveData();
          }
        });
      },
      saveData(){
        let request = {};
        request.name = this.form.name;
        request.sn = this.form.sn;
        request.userId = this.$store.getters.userInfo.id*1;

        let url = "";
        if(this.type===1){
          url = "/KmsPlant-web/api/console/device/add";
        }else{
          url = "/KmsPlant-web/api/console/device/update";
          request.id = this.deviceId;
        }

        let token = this.$store.getters.userInfo.token;
        this.$http.post(url, request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.$message.success("成功");
            this.$router.push({path:'/client'});
          } else {
            this.$message.error(res.msg)
          }
        });
      },
      onCancel() {
        this.$router.push({path: '/client'});
      },
    }
  }
</script>

<style scoped>

</style>
