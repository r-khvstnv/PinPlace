package com.rkhvstnv.pinplace.ui.allplaces

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.databinding.ItemPlaceBinding
import com.rkhvstnv.pinplace.utils.ItemPlaceCallback
import com.rkhvstnv.pinplace.utils.loadImage

class AllPlacesAdapter(
    private val context: Context,
    private val callback: ItemPlaceCallback
): RecyclerView.Adapter<AllPlacesAdapter.ViewHolder>() {
    private var placesList: List<PlaceEntity> = listOf()

    class ViewHolder(val binding: ItemPlaceBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPlaceBinding =
            ItemPlaceBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding = binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placesList[position]

        with(holder.binding){
            ivPlaceImageItem.loadImage(place.imageSource)
            tvPlaceTitleItem.text = place.title
            tvPlaceLocationItem.text = place.locationName
        }

        holder.itemView.setOnClickListener {
            callback.onClick(place.id)
        }

    }

    override fun getItemCount(): Int {
        return placesList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<PlaceEntity>){
        placesList = list
        notifyDataSetChanged()
    }

    fun performEditSwipe(){

    }

    fun preformDeleteSwipe(){
    }

}