/**
* create by shiju.wang on 2019/9/6
*/

<template>
  <div :style="bg" style="height: 100%;box-sizing: border-box;display: flex;align-items: center;">
    <div class="login-content">
      <p style="color: white;font-size: 65px;font-weight: bold;font-family:Source Han Sans CN;">KMS</p>
      <p style="color: white;font-size: 18px;">致力于提供最好的管理系统</p>
      <div style="margin: 74px 44px 0 44px;">

        <div class="login-input" v-bind:class="{'input-focus':accountFocus}">
          <img src="../../assets/img/user_name.svg" style="width: 18px;">
          <el-input style="border: none" v-model="userName" @focus="onAccountFocus(true)" @blur="onAccountFocus(false)" placeholder="请输入账号">
          </el-input>
        </div>

        <div class="login-input" style="margin-top: 30px;" v-bind:class="{'input-focus':pwdFocus}">
          <img src="../../assets/img/psw.svg" style="width: 18px;">
          <el-input v-model="passWord" placeholder="请输入密码" @focus="onPwdFocus(true)" @blur="onPwdFocus(false)" onfocus="this.type='password'"></el-input>
        </div>

        <div style="margin-top: 22px;color: white;font-size: 14px;">
          <el-checkbox class="fl" v-if="false">自动登录</el-checkbox>
          <span class="fr" @click="forgetPwd">忘记密码</span>
        </div>

        <el-button type="primary" style="width: 100%;margin-top: 52px;background-color: #4586FF" @click="login">登录
        </el-button>
      </div>
    </div>

    <div class="login-footer">
      <span>copyright © 2019 麦穗科技技术部出品</span>
    </div>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'

  export default {
    name: "Login",
    data() {
      return {
        loading: false,

        passWord: '',
        userName: '',

        accountFocus:false,
        pwdFocus:false,

        bg:{
          backgroundImage: "url("+require("../../assets/img/login_bg.png")+")",
          backgroundRepeat: "no-repeat",
        }
      }
    },
    created() {
    },

    methods: {
      ...mapActions(['setUserInfo']),

      login() {
        this.loading = true;
        let formData = new FormData();
        formData.append('passWord', this.passWord);
        formData.append('userName', this.userName);
        // let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/user/login', formData).then(res => {
          if (res.code === '0000') {
            // this.tableData = res.result || [];
            let userInfo = {
              'id': res.result.id,
              'account': res.result.account,
              'username': res.result.name,
              'corpId': res.result.corpId,
              'token': res.result.token,
            };
            this.setUserInfo(userInfo);

            this.$router.push({path: '/user'});
          } else {
            this.$message.error(res.msg)
          }
          this.loading = false;
        })
      },
      forgetPwd() {
        this.$router.push({path: '/reset-pwd'});
        console.log(this.$store.getters.userInfo.username);
      },
      onAccountFocus(flag){
        this.accountFocus = flag;
      },
      onPwdFocus(flag){
        this.pwdFocus = flag;
      }
    }
  }
</script>

<style>

  .login-content {
    box-sizing: border-box;
    width: 472px;
    height: 574px;
    text-align: center;
    padding-top: 62px;
    background-color: #5673A8;
    margin-left: 55%;
    border-radius: 8px;
  }

  .login-footer {
    position: absolute;
    bottom: 0;
    background-color: #1C3A6C;
    width: 100%;
    text-align: center;
    color: white;
    font-size: 14px;
    height: 44px;
    line-height: 44px;
  }

  .login-input {
    box-sizing: border-box;
    height: 46px;
    display: flex;
    align-items: center;
    background-color: white;
    border-radius: 4px;
    padding-left: 25px;
  }

  .input-focus{
    border: 1px solid #4586FF;
  }

  .login-content .el-input__inner{
    border: none;
  }
</style>
