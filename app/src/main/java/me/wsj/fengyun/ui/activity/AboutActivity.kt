package me.wsj.fengyun.ui.activity

import android.content.Intent
import me.wsj.fengyun.R
import me.wsj.fengyun.databinding.ActivityAboutBinding
import me.wsj.fengyun.ui.base.BaseActivity
import me.wsj.lib.extension.toast
import per.wsj.commonlib.utils.CommonUtil

class AboutActivity : BaseActivity<ActivityAboutBinding>() {
    override fun bindView(): ActivityAboutBinding {
        return ActivityAboutBinding.inflate(layoutInflater)
    }

    override fun prepareData(intent: Intent?) {

    }

    override fun initView() {
        setTitle(getString(R.string.about))
        mBinding.tvVersionNum.text = CommonUtil.getVersionName(this)
    }

    override fun initEvent() {
        mBinding.rlDisclaimer.setOnClickListener {
            toast("本应用仅用于交流学习，天气数据来源于和风天气(https://dev.qweather.com/)，数据准确性仅供参考，本App不承担任何法律责任")
        }
    }

    override fun initData() {

    }
}