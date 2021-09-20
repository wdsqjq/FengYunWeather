package me.wsj.fengyun.ui.activity

import android.content.Intent
import me.wsj.fengyun.R
import me.wsj.fengyun.databinding.ActivityThemeBinding
import me.wsj.fengyun.ui.activity.vm.ThemeViewModel
import me.wsj.fengyun.ui.base.BaseActivity
import me.wsj.fengyun.ui.base.BaseVmActivity
import me.wsj.lib.extension.toast
import me.wsj.lib.net.LoadState
import me.wsj.lib.net.OkHttpUtils
import me.wsj.lib.utils.SpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import per.wsj.commonlib.utils.FileUtil
import per.wsj.commonlib.utils.LogUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class ThemeActivity : BaseVmActivity<ActivityThemeBinding, ThemeViewModel>() {

    var curFlag = 0

    override fun bindView() = ActivityThemeBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {

    }

    override fun initView() {
        setTitle("主题设置")
        curFlag = SpUtil.getThemeFlag(this)
        setSelected(curFlag)
    }

    override fun initEvent() {
        mBinding.llDefault.setOnClickListener {
            changeSelect(0)
        }
        mBinding.llColorful.setOnClickListener {
            changeSelect(1)
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Error -> {
                    toast("下载失败，请重试")
                    changeSelect(0)
                }
                LoadState.Finish -> {
                    showLoading(false)
                }
                is LoadState.Start -> {
                    showLoading(true, "正在下载...")
                }
            }
        }

        viewModel.downloadStatus.observe(this) {
            toast("设置成功")
        }
    }

    private fun changeSelect(index: Int) {
        if (curFlag == index) {
            return
        }
        curFlag = index
        setSelected(index)
        SpUtil.setThemeFlag(this, index)
        if (index == 1) {
            val path = SpUtil.getPluginPath(this)
            if (path.isEmpty() || !File(path).exists()) {
                viewModel.downPlugin()
            }
        }
    }

    private fun setSelected(index: Int) {
        if (index == 1) {
            mBinding.llDefault.background = resources.getDrawable(R.drawable.shape_theme_bg)
            mBinding.llColorful.background =
                resources.getDrawable(R.drawable.shape_theme_bg_selected)
        } else {
            mBinding.llDefault.background =
                resources.getDrawable(R.drawable.shape_theme_bg_selected)
            mBinding.llColorful.background = resources.getDrawable(R.drawable.shape_theme_bg)
        }
    }

    override fun initData() {

    }

}