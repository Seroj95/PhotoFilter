package com.example.photofilter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.photofilter.R
import com.example.photofilter.interfacee.FilterListFragmentListener
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.thumbnail_list_item.view.*

class ThumbnailAdapter(
    private val context: Context, private val thumbnailItemList: List<ThumbnailItem>,
    private val listener: FilterListFragmentListener
) : RecyclerView.Adapter<ThumbnailAdapter.MyViewHolder>() {
    private var selectedIndex=0;
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var thumbNail: ImageView
        var filterName: TextView

        init {
            thumbNail = itemView.thumbnail
            filterName = itemView.filter_name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
val itemView=LayoutInflater.from(context).inflate(R.layout.thumbnail_list_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return thumbnailItemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
      val thummNailItem=thumbnailItemList[position]
        holder.thumbNail.setImageBitmap(thummNailItem.image)
        holder.thumbNail.setOnClickListener {
            listener.onFilterSelected(thummNailItem.filter)
selectedIndex=position
            notifyDataSetChanged()
        }

    }
}




