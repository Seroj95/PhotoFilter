package com.example.photofilter

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photofilter.Utils.BitmapUtils
import com.example.photofilter.Utils.SpaceItemDecoration
import com.example.photofilter.adapter.ThumbnailAdapter
import com.example.photofilter.interfacee.FilterListFragmentListener
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_filter_list.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FilterListFragment : Fragment(),FilterListFragmentListener {
  internal var listener:FilterListFragmentListener?=null
    internal  lateinit  var adapter:ThumbnailAdapter
    internal lateinit var thumbnailItemList:MutableList<ThumbnailItem>
    fun setListener(listFragmentListener: FilterListFragmentListener){
this.listener=listFragmentListener
    }
    override fun onFilterSelected(filter: Filter) {
       if (listener!=null){
           listener!!.onFilterSelected(filter)
       }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_filter_list, container, false)
        thumbnailItemList=ArrayList()
        adapter= ThumbnailAdapter(activity!!,thumbnailItemList,this)
        recycler_view.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        recycler_view.itemAnimator=DefaultItemAnimator()
        val space=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,resources.displayMetrics).toInt()
        recycler_view.addItemDecoration(SpaceItemDecoration(space))
        recycler_view.adapter=adapter
        displayImage(null)
        return  view
    }

    private fun displayImage(bitmap: Bitmap?) {
        val r = Runnable {
            val thumbImage:Bitmap?
            if (bitmap==null)
                thumbImage=BitmapUtils.getBitmapFromAssets(activity!!,MainActivity.Main.IMAGE_NAME,100,100)
            else
                thumbImage=Bitmap.createScaledBitmap(bitmap,100,100,false)
            if (thumbImage==null)
                return@Runnable
            ThumbnailsManager.clearThumbs()
            thumbnailItemList.clear()
            //add normal bitmap first
            val thumbnailItem=ThumbnailItem()
            thumbnailItem.image=thumbImage
            thumbnailItem.filterName="Normal"
            ThumbnailsManager.addThumb(thumbnailItem)
            //Add filter pack
            val filters=FilterPack.getFilterPack(activity!!)
            for (filter in filters){
                val item=ThumbnailItem()
                item.image=thumbImage
                item.filter=filter
                item.filterName=filter.name
                ThumbnailsManager.addThumb(item)
            }
            thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))
            activity!!.runOnUiThread{
                adapter.notifyDataSetChanged()
            }

        }
        Thread(r).start()

    }


}
