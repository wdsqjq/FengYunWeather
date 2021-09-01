package me.wsj.fengyun.ui.activity

import android.content.Intent
import me.wsj.fengyun.R
import me.wsj.fengyun.databinding.ActivitySettingsBinding
import me.wsj.fengyun.ui.base.BaseActivity
import me.wsj.fengyun.ui.fragment.SettingsFragment

class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override fun bindView() = ActivitySettingsBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {

    }

    override fun initView() {
        setTitle(getString(R.string.setting))

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun initEvent() {

    }

    override fun initData() {

    }
}