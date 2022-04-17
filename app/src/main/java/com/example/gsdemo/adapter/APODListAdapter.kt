package com.example.gsdemo.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gsdemo.BR
import com.example.gsdemo.R
import com.example.gsdemo.databinding.ItemImageRangeBinding
import com.example.gsdemo.model.APODDetail
import com.example.gsdemo.viewmodel.MainViewModel

/**
 * The APODListAdapter class,to show APOD details based on selected date range
 * @property MainViewModel
 */
class APODListAdapter(private val mainViewModel: MainViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var apodList: List<APODDetail> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemImageRangeBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_image_range, parent, false)

        val width: Int = Resources.getSystem().displayMetrics.widthPixels
        val params: ViewGroup.LayoutParams = binding.clMain.layoutParams
        params.width = (width * 0.9).toInt()
        binding.clMain.layoutParams = params

        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(mainViewModel, apodList[position])
        }
    }

    override fun getItemCount(): Int {
        return apodList.size
    }

    class ItemViewHolder(private val binding: ItemImageRangeBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(
            mainViewModel: MainViewModel,
            apodDetail: APODDetail
        ) {

            binding.setVariable(BR.mainViewModel, mainViewModel)
            binding.setVariable(BR.apoDetail, apodDetail)
            binding.executePendingBindings()
            binding.ivFav.setOnClickListener {
                if (apodDetail.isFav) {
                    apodDetail.isFav = false
                    binding.ivFav.post { binding.ivFav.setBackgroundResource(R.drawable.ic_unfav) }
                } else {
                    apodDetail.isFav = true
                    binding.ivFav.post { binding.ivFav.setBackgroundResource(R.drawable.ic_fav) }
                }
            }
        }
    }


    /**
     *The setAPODList method, to set apod list
     */
    @JvmName("setAPODList")
    fun setAPODList(items: List<APODDetail>) {
        apodList = items
    }

    /**
     *The getAPODList method, to get apod list
     */
    @JvmName("getAPODList")
    fun getAPODList(): List<APODDetail> {
        return apodList
    }
}