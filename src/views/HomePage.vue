/**
* create by shiju.wang on 2019/9/6
*/

<template>
  <div class="main">
    <div class="fl left" @click="hideMenu">
      <h1 class="kms">KMS</h1>

      <router-link :to="'/user'">
        <div class="item-div" style="margin-top: 50px;"
             :class="{itemActive:index===1}">
          <img v-bind:style="{opacity:index===1?1:0.6}" src="../assets/img/management_user.svg" class="item-img">
          <span v-bind:style="{opacity:index===1?1:0.6}" class="item-text">用户管理</span>
        </div>
      </router-link>

      <router-link :to="'/client'">
        <div class="item-div" style="margin-top: 28px;"
             :class="{itemActive:index===2}">
          <img v-bind:style="{opacity:index===2?1:0.6}" src="../assets/img/management_client.svg" class="item-img">
          <span v-bind:style="{opacity:index===2?1:0.6}" class="item-text">客户端管理</span>
        </div>
      </router-link>

      <router-link :to="'/sheet'">
        <div class="item-div" style="margin-top: 28px;"
             :class="{itemActive:index===3}">
          <img v-bind:style="{opacity:index===3?1:0.6}" src="../assets/img/management_sheet.svg" class="item-img">
          <span v-bind:style="{opacity:index===3?1:0.6}" class="item-text">报表</span>
        </div>
      </router-link>

      <router-link :to="'/diary'">
        <div class="item-div" style="margin-top: 28px;"
             :class="{itemActive:index===4}">
          <img v-bind:style="{opacity:index===4?1:0.6}" src="../assets/img/management_diary.svg" class="item-img">
          <span v-bind:style="{opacity:index===4?1:0.6}" class="item-text">日志管理</span>
        </div>
      </router-link>
    </div>

    <div class="fl right">

      <div class="title">
        <div>
          <span style="color: #5D8ADE;cursor: pointer;" @click="onTitleClick">{{title}}</span>
          <i class="el-breadcrumb__separator el-icon-arrow-right" v-if="subTitle"></i>
          <span style="color: #333;">{{subTitle}}</span>
        </div>
        <div class="user-icon" @click="showMenu">
          <el-avatar icon="el-icon-user-solid"></el-avatar>
          <span style="margin-left: 18px;user-select: none;-webkit-user-select: none;font-size: 14px;">{{userName}}</span>
          <i style="margin-left: 12px;" class="el-icon-arrow-down"></i>
        </div>
      </div>

      <div v-if="isShowMenu" class="menu">

        <div class="menu-item" @click="changeUserInfo">
          <img src="../assets/img/modify_info.svg" class="menu-item-icon">
          <span class="menu-item-text">修改个人信息</span>
        </div>

        <div class="menu-item" @click="changePwd">
          <img src="../assets/img/modify_pwd.svg" class="menu-item-icon">
          <span class="menu-item-text">修改密码</span>
        </div>

        <div style="height: 1px;background-color: #EEEEEE"></div>

        <div class="menu-item" @click="logOut">
          <img src="../assets/img/logout.svg" class="menu-item-icon">
          <span class="menu-item-text">退出账号</span>
        </div>

      </div>

      <div ref="contentDiv" class="content" @click="hideMenu"
           :style="{height:contentHeight+'px'}">
        <transition name="move" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
  import {mapActions} from 'vuex'

  export default {
    name: "HomePage",
    data() {
      return {
        contentHeight: 0,
        title: '用户管理',
        subTitle:'',
        path:'',

        index: 1,

        userName:'',

        isShowMenu: false,
      }
    },
    created() {
      window.addEventListener("resize",this.getHeight);
      this.getHeight();

      this.userName=this.$store.getters.userInfo.username;
    },

    // 页面刷新时会执行
    mounted(){
        this.getIndex();
    },

    methods: {
      ...mapActions(['doLogout']),

      // 计算内容的高度
      getHeight(){
        this.contentHeight = document.documentElement.clientHeight - 90;
      },
      showMenu() {
        this.isShowMenu = !this.isShowMenu;
      },
      hideMenu() {
        if (this.isShowMenu) {
          this.isShowMenu = false;
        }
      },
      changeUserInfo() {
        this.isShowMenu = false;
        let userId = this.$store.getters.userInfo.id;
        this.$router.push({name:'用户编辑',params:{type:2,userId:userId}});
      },
      changePwd() {
        this.isShowMenu = false;
        this.$router.push({path:'/user/resetPwd'});
      },
      logOut() {
        this.isShowMenu = false;
        this.$message.success('成功退出');
        this.doLogout();
        location.href = '';
      },

      getIndex(){
        // console.log(this.$route.path);e
        if (this.$route.path.indexOf('/user') > -1) {
          this.title = "用户管理";
          this.index = 1;
          this.path = '/user';
        } else if (this.$route.path.indexOf('/client') > -1) {
          this.title = "客户端管理";
          this.index = 2;
          this.path = '/client';
        } else if (this.$route.path.indexOf('/sheet') > -1) {
          this.title = "报表";
          this.index = 3;
          this.path = '/sheet';
        }else if (this.$route.path.indexOf('/diary') > -1) {
          this.title = "日志管理";
          this.index = 4;
          this.path = '/diary';
        }
        if(this.$route.meta.showSubTitle){
          this.subTitle = this.$route.name;
        }else{
          this.subTitle = "";
        }
      },

      onTitleClick(){
        this.$router.push({path:this.path});
      },
    },
    watch: {
      $route() {
        console.log(this.$route);
        this.getIndex();
      }
    }
  }
</script>

<style scoped>

  .main {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    padding-left: 240px;
  }

  .left {
    height: 100%;
    background-color: #37404F;
    width: 240px;
    margin-left: -240px;
  }

  .right {
    width: 100%;
    height: 100%;
    background-color: #F6F6F6;
    box-sizing: border-box;
    min-width: 1250px;
  }

  .kms {
    color: white;
    text-align: center;
    margin-top: 26px;
    font-size: 30px;
    font-family: Source Han Sans CN;
    font-weight: bold;
  }

  .item-img {
    width: 16px;
    margin-left: 32px;
  }

  .item-text {
    color: #FFFFFF;
    padding: 0;
    margin-left: 16px;
    font-size: 15px
  }

  .item-div {
    height: 50px;
    display: flex;
    align-items: center;
  }

  .menu-item {
    height: 40px;
    display: flex;
    align-items: center;
    margin-left: 18px;
    cursor: pointer;
    user-select: none;
    -webkit-user-select: none;
  }

  .menu-item-icon {
    width: 16px;
    color: #666;
  }

  .menu-item-text {
    color: #666666;
    margin-left: 12px;
    font-size: 15px;
  }

  .itemActive {
    background-color: #6791DF;
  }

  .title {
    background-color: white;
    height: 70px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 50px;
  }

  .menu {
    position: absolute;
    top: 71px;
    right: 20px;
    width: 156px;
    height: 122px;
    background-color: white;
    z-index: 999;
    border-radius: 9px;
    box-shadow: 0px 1px 3px 0px rgba(45, 45, 46, 0.18);
  }

  .content {
    margin: 10px;
    background-color: white;
    height: 500px;
    border-radius: 8px;
    box-sizing: border-box;
  }

  .user-icon {
    display: flex;
    align-items: center;
    cursor: pointer;
  }
</style>
