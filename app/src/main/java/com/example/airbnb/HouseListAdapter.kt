package com.example.airbnb

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.airbnb.model.HouseModel
import org.w3c.dom.Text

class HouseListAdapter : ListAdapter<HouseModel, HouseListAdapter.ItemViewHolder>(differ) {

    inner class ItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bind(houseModel: HouseModel) {
            val imageView = itemView.findViewById<ImageView>(R.id.thumbnailImageView)
            val titleTextView = itemView.findViewById<TextView>(R.id.titleTextView)
            val priceTextView = itemView.findViewById<TextView>(R.id.priceTextView)

            titleTextView.text = houseModel.title
            priceTextView.text = houseModel.price

            Glide
                .with(imageView.context)
                .load(houseModel.imageUrl)
                .transform(CenterCrop(), RoundedCorners(dpToPx(imageView.context, 12)))
                .into(imageView)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_house, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(currentList.get(position))
    }

    private fun dpToPx(context : Context, dp : Int) : Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
    }

    companion object {
        val differ = object : DiffUtil.ItemCallback<HouseModel>() {
            override fun areItemsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: HouseModel, newItem: HouseModel): Boolean {
                return oldItem == newItem
            }

        }
    }

}













































