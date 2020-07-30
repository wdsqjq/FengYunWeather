/**
* create by shiju.wang on 2019/9/16
*/

<template>
  <div>
    <el-form :inline="true" :model="formInline" style="padding: 22px 35px 0 36px;">
      <el-form-item label="设备名：">
        <el-input size="small" clearable class="custom-input-width" v-model="formInline.name" placeholder="请输入"></el-input>
      </el-form-item>

      <el-form-item label="序列号：">
        <el-input size="small" clearable class="custom-input-width" v-model="formInline.sn" placeholder="请输入"></el-input>
      </el-form-item>

      <el-form-item label="客户端归属：">
        <el-select size="small" class="custom-input-width" clearable v-model="formInline.userId" placeholder="请选择">
          <el-option v-for="item in userList"
                     :label="item.name"
                     :value="item.id"
                     :key="item.id"></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="灌装状态：">
        <el-select size="small" class="custom-input-width" v-model="formInline.status" placeholder="请选择">
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
      <div style="border: 1px solid #DDDDDD;padding: 35px 0;">
        <div style="display: flex;margin-left: 5%;color: #666666;">
          <div>
            <p class="chart-title">今日灌装次数</p>
            <p class="chart-count">{{data1}}<span>次</span></p>
          </div>
          <div style="margin-left: 160px;">
            <p class="chart-title">近7日灌装次数</p>
            <p class="chart-count">{{data2}}<span>次</span></p>
          </div>
          <div style="margin-left: 160px;">
            <p class="chart-title">近30日灌装次数</p>
            <p class="chart-count">{{data3}}<span>次</span></p>
          </div>

        </div>
        <div id="chart-part" style="width: 100%;height: 500px;"></div>

        <div style="display: flex;justify-content: center;margin-top: 36px;">
          <el-radio v-model="dateFlag" label="7">近一周</el-radio>
          <el-radio v-model="dateFlag" label="30">近一个月</el-radio>
          <el-radio v-model="dateFlag" label="180">近三个月</el-radio>
        </div>
      </div>

    </div>
  </div>
</template>

<script>

  export default {
    name: "SheetManager",
    data() {
      return {
        formInline: {
          name: '',
          sn: '',
          userId: '',
          status: '',
        },
        userList: [],
        dateFlag: '7',

        data1:0,
        data2:0,
        data3:0,

        chartData: [],
        dateData:[],

        deviceId:'',
      }
    },
    created() {
      this.getUserList();
      this.getData();
    },
    mounted() {
      this.drawChart()
    },
    methods: {
      drawChart() {
        const myChart = this.$echarts.init(document.getElementById('chart-part'));
        const option = {
          //设置饼图的颜色
          color:['#5D8ADE'],
          tooltip: {
            trigger: 'axis'
          },
          grid: {
            left: '5%',
            right: '8%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              name:'（时间）',
              type: 'category',
              boundaryGap: false,
              data: this.dateData,
            }
          ],
          yAxis: [{
            name:'（请求次数）',
            type: 'value'
          }],
          series: [
            {
              name:'请求次数',
              type: 'line',
              stack: '总量',
              areaStyle: {normal: {}},
              data: this.chartData
            }
          ]
        };
        myChart.setOption(option);
        window.addEventListener('resize', function () {
          myChart.resize();
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

      getData() {
        let request = {};
        request.daysCount = this.dateFlag;
        request.deviceName = this.formInline.name;
        request.deviceSn = this.formInline.sn;
        request.deviceStatus = this.formInline.status;


        let token = this.$store.getters.userInfo.token;
        this.$http.post('/KmsPlant-web/api/console/report/queryDeviceRecordChart', request, {
          headers: {
            'userToken': token,
          }
        }).then(res => {
          if (res.code === '0000') {
            this.data1 = res.result.todayCount;
            this.data2 = res.result.sevenDayCount;
            this.data3 = res.result.thirtyDayCount;

            this.chartData.splice(0,this.chartData.length);
            this.dateData.splice(0,this.dateData.length);
            res.result.chartList.forEach(it=>{
              this.chartData.push(it.dateCount?it.dateCount:0);
              this.dateData.push(it.dateString);
            });

            this.drawChart();
          } else {
            this.$message.error(res.msg)
          }
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
    },
    watch:{
      dateFlag:function () {
        this.getData();
      }
    },
  }
</script>

<style scoped>
  .chart-title{
    font-size: 15px;
  }

  .chart-count{
    font-size: 36px;
    margin-top: 4px;
  }

  .chart-count span{
    font-size: 15px;
  }

</style>
