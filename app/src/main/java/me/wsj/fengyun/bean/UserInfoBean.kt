package me.wsj.fengyun.bean

import java.io.Serializable

/**
 *
{
"ret": 0,
"msg": "",
"is_lost": 0,
"nickname": "Jasmine",
"gender": "男",
"gender_type": 2,
"province": "上海",
"city": "浦东新区",
"year": "1992",
"constellation": "",
"figureurl": "http://qzapp.qlogo.cn/qzapp/101991873/B7F12EFC40858C19E94115C92633CED9/30",
"figureurl_1": "http://qzapp.qlogo.cn/qzapp/101991873/B7F12EFC40858C19E94115C92633CED9/50",
"figureurl_2": "http://qzapp.qlogo.cn/qzapp/101991873/B7F12EFC40858C19E94115C92633CED9/100",
"figureurl_qq_1": "http://thirdqq.qlogo.cn/g?b=oidb&k=b5E2pXibamwt0ojavcAZ9Hw&s=40&t=1497922679",
"figureurl_qq_2": "http://thirdqq.qlogo.cn/g?b=oidb&k=b5E2pXibamwt0ojavcAZ9Hw&s=100&t=1497922679",
"figureurl_qq": "http://thirdqq.qlogo.cn/g?b=oidb&k=b5E2pXibamwt0ojavcAZ9Hw&s=100&t=1497922679",
"figureurl_type": "0",
"is_yellow_vip": "0",
"vip": "0",
"yellow_vip_level": "0",
"level": "0",
"is_yellow_year_vip": "0"
}
 */
data class UserInfoBean(
    val nickname: String,
    val gender_type: Int,
    val province: String,
    val city: String,
    val year: String,
    val figureurl_qq: String,
    var open_id: String,
    var token: String
) : Serializable
