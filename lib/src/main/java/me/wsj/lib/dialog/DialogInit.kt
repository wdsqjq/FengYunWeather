package me.wsj.lib.dialog

import androidx.viewbinding.ViewBinding

/**
 * Created by shiju.wang on 2018/2/10.
 */
interface DialogInit <T : ViewBinding?> {

    fun bindView(): T

    /**
     * 初始化布局组件
     */
    fun initView()

    /**
     * 增加按钮点击事件
     */
    fun initEvent()
}