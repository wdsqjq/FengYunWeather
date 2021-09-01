package me.wsj.fengyun.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.activity.result.contract.ActivityResultContracts
import me.wsj.lib.extension.toast
import per.wsj.commonlib.utils.LogUtil


class PermissionFragment : Fragment() {
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
        }
        openGPS()
    }

    private fun openGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        // 判断是否有合适的应用能够处理该 Intent，并且可以安全调用 startActivity()。
        if (intent.resolveActivity(requireContext().packageManager) != null) {
//            startActivityForResult(intent, 111)
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                LogUtil.e("打开gps: " + it.resultCode)
            }.launch(intent)
        } else {
            toast("该设备不支持位置服务")
        }
        val beginTransaction = parentFragmentManager.beginTransaction()
        beginTransaction.remove(this)
        beginTransaction.commitAllowingStateLoss()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PermissionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}