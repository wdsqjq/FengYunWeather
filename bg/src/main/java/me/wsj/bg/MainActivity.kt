package me.wsj.bg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import me.wsj.bg.adapter.AllAdapter
import me.wsj.bg.bean.WeatherBean
import me.wsj.bg.databinding.ActivityMainBinding
import me.wsj.lib.extension.startActivity

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding
    val mData by lazy { ArrayList<WeatherBean>() }

    lateinit var adapter: AllAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initData()


        adapter = AllAdapter(this, mData, { startActivity<ShowActivity>("code" to it.code) }) {
            mData.removeAt(it)
            adapter.notifyItemRemoved(it)
            mBinding.rvAll.closeMenu()
        }
        mBinding.rvAll.layoutManager = GridLayoutManager(this, 2)
        mBinding.rvAll.adapter = adapter
    }

    private fun initData() {
        mData.add(WeatherBean(100, "晴"))
        mData.add(WeatherBean(101, "多云"))
        mData.add(WeatherBean(102, "少云"))
        mData.add(WeatherBean(103, "晴间多云"))
        mData.add(WeatherBean(104, "阴"))
        mData.add(WeatherBean(150, "晴(夜)"))
        mData.add(WeatherBean(153, "晴间多云(夜)"))
        mData.add(WeatherBean(154, "阴(夜)"))
        mData.add(WeatherBean(300, "阵雨"))
        mData.add(WeatherBean(301, "强阵雨"))
        mData.add(WeatherBean(302, "雷阵雨"))
        mData.add(WeatherBean(303, "强雷阵雨"))
        mData.add(WeatherBean(305, "小雨"))
        mData.add(WeatherBean(306, "中雨"))
        mData.add(WeatherBean(307, "大雨"))
        mData.add(WeatherBean(308, "极端降雨"))
        mData.add(WeatherBean(309, "毛毛雨/细雨"))
        mData.add(WeatherBean(310, "暴雨"))
        mData.add(WeatherBean(310, "暴雨"))
        mData.add(WeatherBean(311, "大暴雨"))
        mData.add(WeatherBean(312, "特大暴雨"))
        mData.add(WeatherBean(313, "冻雨"))
        mData.add(WeatherBean(314, "小到中雨"))
        mData.add(WeatherBean(315, "中到大雨"))
        mData.add(WeatherBean(316, "大到暴雨"))
        mData.add(WeatherBean(317, "暴雨到大暴雨"))
        mData.add(WeatherBean(318, "大暴雨到特大暴雨"))
        mData.add(WeatherBean(399, "雨"))
        mData.add(WeatherBean(350, "阵雨"))
        mData.add(WeatherBean(351, "强阵雨"))
        mData.add(WeatherBean(400, "小雪"))
        mData.add(WeatherBean(401, "中雪"))
        mData.add(WeatherBean(402, "大雪"))
        mData.add(WeatherBean(403, "暴雪"))
        mData.add(WeatherBean(404, "雨夹雪"))
        mData.add(WeatherBean(405, "雨雪天气"))
        mData.add(WeatherBean(406, "阵雨夹雪"))
        mData.add(WeatherBean(407, "阵雪"))
        mData.add(WeatherBean(408, "小到中雪"))
        mData.add(WeatherBean(409, "中到大雪"))
        mData.add(WeatherBean(410, "大到暴雪"))
        mData.add(WeatherBean(456, "阵雨夹雪"))
        mData.add(WeatherBean(457, "阵雪"))
        mData.add(WeatherBean(499, "雪"))
        mData.add(WeatherBean(500, "薄雾"))
        mData.add(WeatherBean(501, "雾"))
        mData.add(WeatherBean(502, "霾"))
        mData.add(WeatherBean(503, "扬沙"))
        mData.add(WeatherBean(504, "浮尘"))
        mData.add(WeatherBean(507, "沙尘暴"))
        mData.add(WeatherBean(508, "强沙尘暴"))
        mData.add(WeatherBean(509, "浓雾"))
        mData.add(WeatherBean(510, "强浓雾"))
        mData.add(WeatherBean(511, "中度霾"))
        mData.add(WeatherBean(512, "重度霾"))
        mData.add(WeatherBean(513, "严重霾"))
        mData.add(WeatherBean(514, "大雾"))
        mData.add(WeatherBean(515, "特强浓雾"))
        mData.add(WeatherBean(900, "热"))
        mData.add(WeatherBean(901, "冷"))
        mData.add(WeatherBean(999, "未知"))
    }
}