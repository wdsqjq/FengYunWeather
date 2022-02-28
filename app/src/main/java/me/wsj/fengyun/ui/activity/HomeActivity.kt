package me.wsj.fengyun.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import coil.imageLoader
import coil.load
import coil.transform.CircleCropTransformation
import me.wsj.fengyun.R
import me.wsj.fengyun.adapter.ViewPagerAdapter
import me.wsj.fengyun.bean.UserInfoBean
import me.wsj.fengyun.databinding.ActivityMainBinding
import me.wsj.fengyun.databinding.NavHeaderMainBinding
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.dialog.UpgradeDialog
import me.wsj.fengyun.ui.activity.vm.LoginViewModel
import me.wsj.fengyun.ui.activity.vm.MainViewModel
import me.wsj.fengyun.ui.base.BaseVmActivity
import me.wsj.fengyun.ui.fragment.WeatherFragment
import me.wsj.fengyun.utils.ContentUtil
import me.wsj.fengyun.utils.TencentUtil
import me.wsj.lib.EffectUtil
import me.wsj.lib.extension.expand
import me.wsj.lib.extension.startActivity
import me.wsj.lib.extension.toast
import me.wsj.lib.utils.IconUtils
import me.wsj.lib.utils.SpUtil
import per.wsj.commonlib.utils.DisplayUtil
import java.util.*

//@AndroidEntryPoint
class HomeActivity : BaseVmActivity<ActivityMainBinding, MainViewModel>() {

    private val fragments: MutableList<Fragment> by lazy { ArrayList() }
    private val cityList = ArrayList<CityEntity>()
    private var mCurIndex = 0
    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var navHeaderBinding: NavHeaderMainBinding

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //此处是跳转的result回调方法
            if (it.data != null && it.resultCode == Activity.RESULT_OK) {
                val isLogin = it.data!!.getBooleanExtra("login", false)
                if (isLogin) {
                    loginViewModel.register(it.data!!.getSerializableExtra("user_info") as UserInfoBean)
                    toast("登录成功")
                } else {
                    toast("已退出登录")
                }
                initUserInfo()
            }
        }

    /**
     * 当前的天气code
     */
    var currentCode = ""

    override fun bindView() = ActivityMainBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {

    }

    override fun initView() {
        hideTitleBar()
        // 沉浸式态栏
        immersionStatusBar()

        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        mBinding.viewPager.offscreenPageLimit = 5

        mBinding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(i: Int) {
                mBinding.ivLoc.visibility =
                    if (cityList[i].isLocal) View.VISIBLE else View.INVISIBLE

                mBinding.llRound.getChildAt(mCurIndex).isEnabled = false
                mBinding.llRound.getChildAt(i).isEnabled = true
                mCurIndex = i
                mBinding.tvLocation.text = cityList[i].cityName
            }
        })

        mBinding.ivAddCity.expand(10, 10)

        mBinding.ivBg.setImageResource(IconUtils.defaultBg)

        navHeaderBinding = NavHeaderMainBinding.bind(mBinding.navView.getHeaderView(0))
        // 侧边栏顶部下移状态栏高度
        ViewCompat.setOnApplyWindowInsetsListener(
            navHeaderBinding.llUserHeader
        ) { view, insets ->
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            insets
        }

        // 设置默认单位
        val unitConfig =
            PreferenceManager.getDefaultSharedPreferences(context).getString("unit", "she")
        val menu = mBinding.navView.menu
        if (unitConfig == "she") {
            menu.findItem(R.id.navShe).isChecked = true
        } else {
            menu.findItem(R.id.navHua).isChecked = true
        }
        menu.findItem(R.id.itemUnit).subMenu.setGroupCheckable(R.id.navUnitGroup, true, true)

        // 用户信息
        initUserInfo()
    }

    private fun initUserInfo() {
        val account = SpUtil.getAccount(this)
        if (account.isNotEmpty()) {
            navHeaderBinding.tvAccount.text = account
            navHeaderBinding.ivAvatar.load(
                SpUtil.getAvatar(this), imageLoader = context.imageLoader
            ) {
                placeholder(R.drawable.ic_avatar_default)
                transformations(CircleCropTransformation())
            }
        } else {
            navHeaderBinding.tvAccount.text = getString(R.string.login_plz)
            navHeaderBinding.ivAvatar.load(R.drawable.ic_avatar_default)
        }
    }

    override fun initEvent() {
        mBinding.ivSetting.setOnClickListener {
            if (!mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                mBinding.drawerLayout.openDrawer(GravityCompat.END)
            }
        }
        mBinding.ivAddCity.setOnClickListener {
            startActivity<AddCityActivity>()
        }

        mBinding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navCity -> {
                    startActivity<CityManagerActivity>()
                }
                R.id.navTheme -> {
                    startActivity<ThemeActivity>()
                }
                R.id.navShe -> {
                    viewModel.changeUnit("she")
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.navHua -> {
                    viewModel.changeUnit("hua")
                    mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                }
                R.id.navFeedback -> {
                    startActivity<FeedBackActivity>()
                }
                R.id.navAbout -> {
                    startActivity<AboutActivity>()
                }
            }
            true
        }

        navHeaderBinding.llUserHeader.setOnClickListener {
            if (SpUtil.getAccount(this).isEmpty()) {
                launcher.launch(Intent(this, LoginActivity::class.java))
            } else {
                launcher.launch(Intent(this, UserInfoActivity::class.java))
            }
        }

        // 检查登录状态
        loginViewModel.checkLogin().observe(this) {
            if (!it && SpUtil.getAccount(this).isNotEmpty()) {
                TencentUtil.sTencent.logout(this)
                SpUtil.logout(this)
            }
        }

        viewModel.mCities.observe(this) {
            if (it.isEmpty()) {
                startActivity<AddCityActivity>()
            } else {
                cityList.clear()
                cityList.addAll(it)
                showCity()
            }
        }

        viewModel.mCurCondCode.observe(this, ::changeBg)

        viewModel.newVersion.observe(this) {
            UpgradeDialog(it).show(supportFragmentManager, "upgrade_dialog")
        }
    }

    override fun initData() {
        viewModel.getCities()
        viewModel.checkVersion()
    }

    /**
     * 显示城市
     */
    private fun showCity() {
        if (mCurIndex > cityList.size - 1) {
            mCurIndex = cityList.size - 1
        }

        mBinding.ivLoc.visibility =
            if (cityList[mCurIndex].isLocal) View.VISIBLE else View.INVISIBLE
        mBinding.tvLocation.text = cityList[mCurIndex].cityName

        mBinding.llRound.removeAllViews()

        // 宽高参数
        val size = DisplayUtil.dp2px(4f)
        val layoutParams = LinearLayout.LayoutParams(size, size)
        // 设置间隔
        layoutParams.rightMargin = 12

        for (i in cityList.indices) {
            // 创建底部指示器(小圆点)
            val view = View(this@HomeActivity)
            view.setBackgroundResource(R.drawable.background)
            view.isEnabled = false

            // 添加到LinearLayout
            mBinding.llRound.addView(view, layoutParams)
        }
        // 小白点
        mBinding.llRound.getChildAt(mCurIndex).isEnabled = true
        mBinding.llRound.visibility = if (cityList.size <= 1) View.GONE else View.VISIBLE

        fragments.clear()
        for (city in cityList) {
            val cityId = city.cityId
//            LogUtil.i("cityId: " + cityId)
            val weatherFragment = WeatherFragment.newInstance(cityId)
            fragments.add(weatherFragment)
        }

//        mBinding.viewPager.adapter?.notifyDataSetChanged()
        mBinding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        mBinding.viewPager.currentItem = mCurIndex
    }


    override fun onResume() {
        super.onResume()
        if (ContentUtil.CITY_CHANGE) {
            viewModel.getCities()
            ContentUtil.CITY_CHANGE = false
        }
        mBinding.ivEffect.drawable?.let {
            (it as Animatable).start()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        }
    }

    private fun changeBg(condCode: String) {
        if (currentCode == condCode) {
            return
        }
        currentCode = condCode
        // 获取背景
        val bgDrawable = IconUtils.getBg(this@HomeActivity, condCode.toInt())

        val originDrawable = mBinding.ivBg.drawable
        val targetDrawable = resources.getDrawable(bgDrawable)
        val transitionDrawable = TransitionDrawable(
            arrayOf<Drawable>(
                originDrawable,
                targetDrawable
            )
        )

        mBinding.ivBg.setImageDrawable(transitionDrawable)
        transitionDrawable.isCrossFadeEnabled = true
        transitionDrawable.startTransition(1000)

        // 获取特效
        val effectDrawable = EffectUtil.getEffect(context, condCode.toInt())
        mBinding.ivEffect.setImageDrawable(effectDrawable)
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.ivEffect.drawable?.let {
            (it as Animatable).stop()
        }
    }
}