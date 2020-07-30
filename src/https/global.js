// 'use strict';
// module.exports = {
//   /*erp接口前缀*/
//   // erp_prefix: 'http://192.168.86.44:9050',
//   // erp_prefix: 'http://192.168.86.20:9050', //测试/
//   erp_prefix: ''
// };
import moment from 'moment'

let commonFun = {};

commonFun.install = function (Vue) {
  // 未登录跳转
  Vue.prototype.$fun = function (change) {
    console.log('这是我的'+change);
  };
  Vue.prototype.$headerCellStyle = function(){
    return 'background:rgba(247,247,247,1);padding: 9px 5px;line-height: 23px;text-align:center;'
  };

  Vue.prototype.$dateFormat= function(ts) {
    return moment(ts).format('YYYY-MM-DD')
  };

  Vue.prototype.$renderHeader = function (h,{column}) {
    return h(
      'div',
      [
        h('span', {
          class:'must-input-item'
        },['*']),
        h('span', column.label)
      ],
    );
  };
  Vue.prototype.$str='hello world'
};

export default commonFun;
