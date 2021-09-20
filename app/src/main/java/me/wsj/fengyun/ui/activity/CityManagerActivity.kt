package me.wsj.fengyun.ui.activity

import android.content.Intent
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import dagger.hilt.android.AndroidEntryPoint
import me.wsj.fengyun.R
import me.wsj.fengyun.adapter.CityManagerAdapter
import me.wsj.fengyun.adapter.MyItemTouchCallback
import me.wsj.fengyun.databinding.ActivityCityManagerBinding
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.activity.vm.CityManagerViewModel
import me.wsj.fengyun.ui.base.BaseVmActivity
import me.wsj.fengyun.utils.ContentUtil
import javax.inject.Inject

//@AndroidEntryPoint
class CityManagerActivity : BaseVmActivity<ActivityCityManagerBinding, CityManagerViewModel>() {

    private val datas by lazy { ArrayList<CityEntity>() }

    private var adapter: CityManagerAdapter? = null

    //    @Inject
    lateinit var itemTouchCallback: MyItemTouchCallback

    override fun bindView() = ActivityCityManagerBinding.inflate(layoutInflater)

    override fun prepareData(intent: Intent?) {
    }

    override fun initView() {
        setTitle(getString(R.string.control_city))

        itemTouchCallback = MyItemTouchCallback(this)

        adapter = CityManagerAdapter(datas) {
            viewModel.updateCities(it)
            ContentUtil.CITY_CHANGE = true
        }

        mBinding.recycleView.adapter = adapter

        mBinding.recycleView.setStateCallback {
            itemTouchCallback.dragEnable = it
        }

        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(mBinding.recycleView)
    }

    override fun initEvent() {
        adapter?.listener = object : CityManagerAdapter.OnCityRemoveListener {
            override fun onCityRemove(pos: Int) {
                viewModel.removeCity(datas[pos].cityId)
                datas.removeAt(pos)
                adapter?.notifyItemRemoved(pos)
                ContentUtil.CITY_CHANGE = true

            }
        }

        viewModel.cities.observe(this) {
            datas.clear()
            datas.addAll(it)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun initData() {
        viewModel.getCities()
    }
}