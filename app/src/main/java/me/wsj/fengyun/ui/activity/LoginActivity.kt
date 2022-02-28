package me.wsj.fengyun.ui.activity

import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.tencent.connect.common.Constants
import com.tencent.tauth.DefaultUiListener
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import me.wsj.fengyun.databinding.ActivityLoginBinding
import me.wsj.fengyun.ui.activity.vm.LoginViewModel
import me.wsj.fengyun.ui.base.BaseVmActivity
import me.wsj.fengyun.utils.TencentUtil
import me.wsj.lib.extension.toast
import me.wsj.lib.utils.SpUtil
import org.json.JSONObject
import per.wsj.commonlib.utils.LogUtil

open class LoginActivity : BaseVmActivity<ActivityLoginBinding, LoginViewModel>() {

    var uiListener: IUiListener = object : BaseUiListener() {
        override fun doComplete(values: JSONObject) {
            LogUtil.e("AuthorSwitch_SDK:" + SystemClock.elapsedRealtime())
            try {
                val token = values.getString(Constants.PARAM_ACCESS_TOKEN)
                val expires = values.getString(Constants.PARAM_EXPIRES_IN)
                val openId = values.getString(Constants.PARAM_OPEN_ID)
                if (token.isNotEmpty() && expires.isNotEmpty() && openId.isNotEmpty()) {
                    TencentUtil.sTencent.setAccessToken(token, expires)
                    TencentUtil.sTencent.openId = openId
                }
            } catch (e: Exception) {
            }
            viewModel.getUserInfo()
        }
    }

    override fun bindView() = ActivityLoginBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {
        Tencent.setIsPermissionGranted(true, Build.MODEL)
    }

    override fun initView() {
        setTitle("登录")
    }

    override fun initEvent() {
        mBinding.btnLogin.setOnClickListener {
            if (!TencentUtil.sTencent.isSessionValid) {
                // 判断会话是否有效
                when (TencentUtil.sTencent.login(this, "all", uiListener)) {
                    0 -> LogUtil.d("正常登录")
                    1 -> LogUtil.d("开始登录")
                    -1 -> toast("QQ登录异常")
                    2 -> LogUtil.d("使用H5登陆或显示下载页面")
                    else -> toast("QQ登录出错")
                }
            } else {
                viewModel.getUserInfo()
            }
        }

        viewModel.checkLogin().observe(this) {
            if (it) viewModel.getUserInfo()
        }

        viewModel.userInfo.observe(this) {
            if (it.first) {
                val intent = Intent()
                intent.putExtra("login", true)
                intent.putExtra("user_info", it.second)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                toast("获取用户信息失败")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 腾讯QQ回调，这里的iu仍然是相关的UIlistener
        Tencent.onActivityResultData(requestCode, resultCode, data, uiListener)
        if (requestCode == Constants.REQUEST_API) {
            if (resultCode == Constants.REQUEST_LOGIN) {
                Tencent.handleResultData(data, uiListener)
            }
        }
    }

    override fun initData() {
        //To do sth.
    }
}

abstract class BaseUiListener : DefaultUiListener() {
    override fun onComplete(response: Any?) {
        if (response == null) {
            LogUtil.e("返回为空,登录失败")
            return
        }
        val jsonResponse = response as JSONObject
        if (jsonResponse.length() == 0) {
            LogUtil.e("返回为空,登录失败")
            return
        }
        LogUtil.e("登录成功")
        // {"ret":0,"openid":"B7F12EFC40858C19E94115C92633CED9","access_token":"53D70606E348BDC837F16309387EA333","pay_token":"D950F05B22F57EEB13E17C054B69CCF8","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"69a1b27f66fd2af1efedf5a6fcba4c3e","msg":"","login_cost":131,"query_authority_cost":0,"authority_cost":0,"expires_time":1652958830237}
//        LogUtil.e(jsonResponse.toString())
        doComplete(response)
    }

    abstract fun doComplete(values: JSONObject)

    override fun onError(e: UiError) {
        Log.e("fund", "onError: ${e.errorDetail}")
    }

    override fun onCancel() {
        LogUtil.e("取消登录")
    }
}