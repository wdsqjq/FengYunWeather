package me.wsj.fengyun.ui.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.wsj.fengyun.R
import me.wsj.fengyun.adapter.Forecast15dAdapter
import me.wsj.fengyun.adapter.Forecast3dAdapter
import me.wsj.fengyun.bean.*
import me.wsj.fengyun.databinding.*
import me.wsj.fengyun.dialog.AlarmDialog
import me.wsj.fengyun.utils.Lunar
import me.wsj.lib.utils.WeatherUtil
import me.wsj.fengyun.ui.activity.vm.MainViewModel
import me.wsj.fengyun.ui.base.BaseVmFragment
import me.wsj.fengyun.ui.fragment.vm.WeatherViewModel
import me.wsj.fengyun.utils.ContentUtil
import me.wsj.lib.extension.notEmpty
import me.wsj.lib.net.LoadState
import me.wsj.lib.utils.DateUtil
import per.wsj.commonlib.utils.LogUtil
import per.wsj.commonlib.utils.Typefaces
import java.util.*

private const val PARAM_CITY_ID = "param_city_id"


class WeatherFragment : BaseVmFragment<FragmentWeatherBinding, WeatherViewModel>() {

    private lateinit var mCityId: String

    private var todayWeather: Daily? = null

    private var hasAni = false

    private var nowTmp: String? = null

    private var condCode: String? = null

    private var mForecastAdapter3d: Forecast3dAdapter? = null

    private var mForecastAdapter15d: Forecast15dAdapter? = null

    private val mForecastList by lazy { ArrayList<Daily>() }

    private lateinit var todayBriefInfoBinding: LayoutTodayBriefInfoBinding

    private lateinit var forecastHourlyBinding: LayoutForecastHourlyBinding

    private lateinit var forecast15dBinding: LayoutForecast15dBinding

    private lateinit var airQualityBinding: LayoutAirQualityBinding

    private lateinit var sunMoonBinding: LayoutSunMoonBinding

    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mCityId = it.getString(PARAM_CITY_ID).toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            WeatherFragment().apply {
                arguments = Bundle().apply {
                    putString(PARAM_CITY_ID, param1)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCache(mCityId)

        Calendar.getInstance().apply {
            val day = get(Calendar.DAY_OF_MONTH)
            mBinding.tvDate.text =
                (get(Calendar.MONTH) + 1).toString() + "月" + day + "日 农历" +
                        Lunar(this).toString()
        }
    }

    override fun onResume() {
        super.onResume()
        condCode?.let {
            LogUtil.e("onResume() set cond code: $condCode")
            mainViewModel.setCondCode(it)
        }

        setViewTime()
    }

    override fun bindView() = FragmentWeatherBinding.inflate(layoutInflater)

    override fun initView(view: View?) {
        // must use activity
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        todayBriefInfoBinding = LayoutTodayBriefInfoBinding.bind(mBinding.root)

        forecastHourlyBinding = LayoutForecastHourlyBinding.bind(mBinding.root)

        forecast15dBinding = LayoutForecast15dBinding.bind(mBinding.root)

        sunMoonBinding = LayoutSunMoonBinding.bind(mBinding.root)

        airQualityBinding = LayoutAirQualityBinding.bind(mBinding.root)

        // 设置字体
        mBinding.tvTodayTmp.typeface = Typefaces.get(requireContext(), "widget_clock.ttf")

        for (i in 0 until 3) {
            mForecastList.add(Daily(iconDay = "100", textDay = "晴", tempMin = "20", tempMax = "25"))
        }
        mForecastAdapter3d = Forecast3dAdapter(requireContext(), mForecastList)
        mBinding.rvForecast3.adapter = mForecastAdapter3d
        val forecastManager = GridLayoutManager(requireContext(), 3)
        mBinding.rvForecast3.layoutManager = forecastManager

        mForecastAdapter15d = Forecast15dAdapter(requireContext(), mForecastList)
        forecast15dBinding.rvForecast15.adapter = mForecastAdapter15d
        forecast15dBinding.rvForecast15.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    override fun initEvent() {
        mBinding.swipeLayout.setOnRefreshListener { loadData() }

        viewModel.weatherNow.observe(this) {
            showWeatherNow(it)
        }

        viewModel.warnings.observe(this) {
            showWarnings(it)
        }

        viewModel.airNow.observe(this) {
            showAirNow(it)
        }

        viewModel.forecast.observe(this) {
            showForecast(it)
        }
        viewModel.hourly.observe(this) {
            showHourly(it)
        }

        viewModel.loadState.observe(this) {
            when (it) {
                is LoadState.Start -> {
                    mBinding.swipeLayout.isRefreshing = true
                }
                is LoadState.Error -> {

                }
                is LoadState.Finish -> {
                    if (viewModel.isStopped()) {
                        mBinding.swipeLayout.isRefreshing = false
                    }
                }
            }
        }
    }

    override fun loadData() {
        viewModel.loadData(mCityId)
    }

    @SuppressLint("SetTextI18n")
    fun showWeatherNow(now: Now) {
        condCode = now.icon
        nowTmp = now.temp
        lifecycleScope.launchWhenResumed {
//            LogUtil.d("showWeatherNow() set cond code: $condCode")
            mainViewModel.setCondCode(now.icon)
        }
        mBinding.tvTodayCond.text = now.text
        mBinding.tvTodayTmp.text = now.temp
        mBinding.tvUnit.visibility = View.VISIBLE

        if (ContentUtil.APP_SETTING_UNIT == "hua") {
            mBinding.tvTodayTmp.text = WeatherUtil.getF(now.temp).toString() + "°F"
        }

        todayBriefInfoBinding.tvFeelTemp.text = now.temp + "°C"
        todayBriefInfoBinding.tvHumidity.text = now.humidity + "%"
        todayBriefInfoBinding.tvWindScale.text = now.windDir + now.windScale + "级"
        todayBriefInfoBinding.tvPressure.text = now.pressure + "hpa"

    }

    /**
     * 三天预报
     */
    private fun showForecast(dailyForecast: List<Daily>) {
        val currentTime = DateUtil.getNowTime()
        val forecastBase = dailyForecast[0]
        todayWeather = forecastBase

        sunMoonBinding.sunView.setTimes(todayWeather?.sunrise, todayWeather?.sunset, currentTime)
        sunMoonBinding.moonView.setTimes(todayWeather?.moonrise, todayWeather?.moonset, currentTime)

        mForecastList.clear()
        mForecastList.addAll(dailyForecast)

        mForecastAdapter3d?.notifyDataSetChanged()
        var min = forecastBase.tempMin.toInt()
        var max = forecastBase.tempMax.toInt()
        mForecastList.forEach {
            min = Math.min(it.tempMin.toInt(), min)
            max = Math.max(it.tempMax.toInt(), max)
        }

        mForecastAdapter15d?.setRange(min, max)
    }

    /**
     * 空气质量
     */
    private fun showAirNow(airNow: Air) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBinding.tvAirCondition.text =
                getString(R.string.air_condition, airNow.aqi, airNow.category)

            TextViewCompat.setCompoundDrawableTintList(
                mBinding.tvAirCondition, ColorStateList.valueOf(
                    WeatherUtil.getAirColor(
                        requireContext(),
                        airNow.aqi
                    )
                )
            )
            mBinding.tvAirCondition.visibility = View.VISIBLE
        } else {
            mBinding.tvAirCondition.visibility = View.GONE
        }
        airQualityBinding.airConditionView.setValue(airNow.aqi.toInt(), airNow.category)

        airQualityBinding.tvTodayPm25.text = airNow.pm2p5
        airQualityBinding.tvTodaySo2.text = airNow.so2
        airQualityBinding.tvTodayCo.text = airNow.co
        airQualityBinding.tvTodayPm10.text = airNow.pm10
        airQualityBinding.tvTodayNo2.text = airNow.no2
        airQualityBinding.tvTodayO3.text = airNow.o3
    }

    /**
     * 预警
     */
    private fun showWarnings(warnings: List<Warning>) {
        mBinding.alarmFlipper.visibility = View.VISIBLE
        mBinding.alarmFlipper.setInAnimation(requireContext(), R.anim.bottom_in)
        mBinding.alarmFlipper.setOutAnimation(requireContext(), R.anim.top_out)
        mBinding.alarmFlipper.flipInterval = 4000
        for (warning in warnings) {
            val level: String = warning.level
            val tip = warning.typeName + level + "预警"
            val warningRes = WeatherUtil.getWarningRes(requireContext(), level)
            val textView: TextView = layoutInflater.inflate(R.layout.item_warning, null) as TextView
            textView.background = warningRes.first
            textView.text = tip
            textView.setOnClickListener {
//                toastCenter(warning.text)
                AlarmDialog(requireContext()).apply {
                    setContent(tip, warning.text)
                    show()
                }
            }
            textView.setTextColor(warningRes.second)
            mBinding.alarmFlipper.addView(textView)
        }
        if (warnings.size > 1) {
            mBinding.alarmFlipper.startFlipping()
        }
    }

    /**
     * 逐小时天气
     */
    private fun showHourly(hourlyWeatherList: List<Hourly>) {
        val data: MutableList<Hourly> = ArrayList()
        if (hourlyWeatherList.size > 23) {
            for (i in 0..23) {
                data.add(hourlyWeatherList[i])
                val condCode = data[i].icon
                var time = data[i].fxTime
                time = time.substring(time.length - 11, time.length - 9)
                val hourNow = time.toInt()
                if (hourNow in 6..19) {
                    data[i].icon = condCode + "d"
                } else {
                    data[i].icon = condCode + "n"
                }
            }
        } else {
            for (i in hourlyWeatherList.indices) {
                data.add(hourlyWeatherList[i])
                val condCode = data[i].icon
                var time = data[i].fxTime
                time = time.substring(time.length - 11, time.length - 9)
                val hourNow = time.toInt()
                if (hourNow in 6..19) {
                    data[i].icon = condCode + "d"
                } else {
                    data[i].icon = condCode + "n"
                }
            }
        }
        var minTmp = data[0].temp.toInt()
        var maxTmp = minTmp
        for (i in data.indices) {
            val tmp = data[i].temp.toInt()
            minTmp = Math.min(tmp, minTmp)
            maxTmp = Math.max(tmp, maxTmp)
        }
        //设置当天的最高最低温度
        forecastHourlyBinding.hourly.setHighestTemp(maxTmp)
        forecastHourlyBinding.hourly.setLowestTemp(minTmp)
        if (maxTmp == minTmp) {
            forecastHourlyBinding.hourly.setLowestTemp(minTmp - 1)
        }
        forecastHourlyBinding.hourly.initData(data)
        forecastHourlyBinding.tvLineMaxTmp.text = "$maxTmp°"
        forecastHourlyBinding.tvLineMinTmp.text = "$minTmp°"
        if (ContentUtil.APP_SETTING_UNIT == "hua") {
            forecastHourlyBinding.tvLineMaxTmp.text =
                WeatherUtil.getF(maxTmp.toString()).toString() + "°"
            forecastHourlyBinding.tvLineMinTmp.text =
                WeatherUtil.getF(minTmp.toString()).toString() + "°"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBinding.root.parent != null) {
            (mBinding.root.parent as ViewGroup).removeView(mBinding.root)
        }
    }

    /**
     * 设置view的时间
     */
    private fun setViewTime() {
        if (!hasAni && todayWeather?.sunrise.notEmpty() && todayWeather?.sunset.notEmpty() && todayWeather?.moonrise.notEmpty() && todayWeather?.moonset.notEmpty()) {
            val currentTime = DateUtil.getNowTime()
            sunMoonBinding.sunView.setTimes(
                todayWeather?.sunrise,
                todayWeather?.sunset,
                currentTime
            )
            sunMoonBinding.moonView.setTimes(
                todayWeather?.moonrise,
                todayWeather?.moonset,
                currentTime
            )
            hasAni = true
        }
    }

    fun changeUnit() {
        if (ContentUtil.APP_SETTING_UNIT == "hua") {
            LogUtil.e("当前城市1：$condCode")
//            todayDetailBinding.tvMaxTmp.text =
//                WeatherUtil.getF(todayMaxTmp!!).toString() + "°F"
//            todayDetailBinding.tvMinTmp.text =
//                WeatherUtil.getF(todayMinTmp!!).toString() + "°F"
            mBinding.tvTodayTmp.text = WeatherUtil.getF(nowTmp!!).toString() + "°F"
        } else {
            LogUtil.e("当前城市2：$condCode")
//            todayDetailBinding.tvMaxTmp.text = "$todayMaxTmp°C"
//            todayDetailBinding.tvMinTmp.text = "$todayMinTmp°C"
            mBinding.tvTodayTmp.text = "$nowTmp°C"
        }
//        getWeatherHourly(weatherHourlyBean)
//        getWeatherForecast(weatherForecastBean)
    }
}