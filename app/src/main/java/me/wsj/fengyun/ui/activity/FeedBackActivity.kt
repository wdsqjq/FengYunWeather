package me.wsj.fengyun.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import me.wsj.fengyun.R
import me.wsj.fengyun.databinding.ActivityFeedBackBinding
import me.wsj.fengyun.ui.activity.vm.FeedBackViewModel
import me.wsj.fengyun.ui.base.BaseActivity
import me.wsj.lib.extension.toast
import me.wsj.lib.net.LoadState
import per.wsj.commonlib.permission.PermissionUtil
import per.wsj.commonlib.utils.DeviceUtil
import per.wsj.commonlib.utils.LogUtil

class FeedBackActivity : BaseActivity<ActivityFeedBackBinding>() {

    private lateinit var textWatcher: TextWatcher

    private val viewModel: FeedBackViewModel by viewModels()

    override fun bindView() = ActivityFeedBackBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {

    }

    override fun initView() {
        setTitle(getString(R.string.feed_back))
    }

    override fun initEvent() {
        textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mBinding.tvInputCount.text = getString(R.string.feed_back_size, s?.length)
//                LogUtil.LOGD("onTextChanged" + s.toString())
            }
        }

        mBinding.etFeedBack.addTextChangedListener(textWatcher)

        mBinding.btnCommit.setOnClickListener {
            if (mBinding.etFeedBack.text.toString().isBlank()) {
                toast("请输入您的意见")
                return@setOnClickListener
            }

            viewModel.sendFeedBack(mBinding.etFeedBack.text.toString())
        }

        viewModel.feedBackResult.observe(this) {
            toast(it)
            finish()
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Start -> {
                    showLoading(true)
                }
                is LoadState.Error -> {
                    toast(it.msg)
                }
                is LoadState.Finish -> {
                    showLoading(false)
                }
            }
        }
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.etFeedBack.removeTextChangedListener(textWatcher)
    }

    fun getEmei() {
        PermissionUtil.with(this).permission(Manifest.permission.READ_PHONE_STATE)
            .onGranted {
                LogUtil.e(DeviceUtil.getCPUSerial(context))
            }.onDenied {
                toast("没有权限")
            }.start()
    }

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, FeedBackActivity::class.java)
            context.startActivity(intent)
        }
    }
}
