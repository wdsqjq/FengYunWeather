import Vue from 'vue'
import axios from 'axios'
import qs from 'qs'
// import store from '@/store'
import {Message} from 'element-ui'
import Router from 'vue-router'

Vue.use(Router);

const instance = axios.create({
  baseURL: '',
  timeout: 120000,
  headers: {
    // 'Content-Type':'application/json;charset=UTF-8',
    // 'Accept': 'application/json',
    //     'Content-Type': 'application/json',
    // 'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': 'application/json',
    // 'Content-Type':'application/x-www-form-urlencoded',
    // 'Access-Control-Allow-Origin': '*'
  },
});

instance.interceptors.response.use(res => {
    if(parseInt(res.data.code) === 6666){
      // store.dispatch('doLogout');
      // window.location.href = window.location.origin+'/#/login';
    }
    return Promise.resolve(res.data);
}, err => {
    Message.error('网络故障，请在网络恢复后继续操作');
    // console.log('network error');
    // console.log(err);
    return Promise.reject(err);
});
// instance.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';

Vue.prototype.$http = instance;

//post请求方法封装 返回prosmise用于回调
Vue.prototype.$fatch = HttpGet;

const HttpGet = (url, qes) => {
  // 创建一个promise对象
  //url 路径为相对路径
  let promise = new Promise((resolve, reject) => {

    instance.post(url, qs.stringify(qes))
      .then((data) => {
        // console.log("这里是拦截的地方");
        resolve(data);
      })
      .catch((data) => {
        reject(data);
      })
  });
  return promise;
};

