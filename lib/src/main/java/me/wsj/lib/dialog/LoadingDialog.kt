package me.wsj.lib.dialog

import android.content.Context
import android.graphics.drawable.Animatable
import me.wsj.lib.R
import me.wsj.lib.databinding.DialogLoadingBinding
import me.wsj.lib.view.LoadingDrawable

/**
 * 自定义加载进度对话框
 */
class LoadingDialog(context: Context) : BaseDialog<DialogLoadingBinding?>(context, 0.38f, 0f) {
    var loadingDrawable: LoadingDrawable? = null

    override fun bindView(): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadingDrawable = LoadingDrawable(
            context.resources.getDrawable(R.drawable.ic_loading_sun),
            context.resources.getDrawable(R.drawable.ic_loading_cloud)
        )
        mBinding?.ivLoading?.setImageDrawable(loadingDrawable)
    }

    override fun initEvent() {}

    fun setTip(tip: String?) {
        if (!tip.isNullOrEmpty()) {
            mBinding?.tvLoadingTip?.text = tip
        }
    }

    override fun show() {
        super.show()
        if (loadingDrawable != null) {
            (loadingDrawable as Animatable).start()
        }
    }

    override fun dismiss() {
        super.dismiss()
        if (loadingDrawable != null) {
            (loadingDrawable as Animatable).stop()
        }
    }
}