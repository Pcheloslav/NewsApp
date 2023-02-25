package com.example.newsapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.Interface.ItemClickListener
import com.example.newsapp.Model.Item
import com.example.newsapp.Model.RssItem
import com.example.newsapp.R
import com.example.newsapp.RssFragment
import kotlinx.android.synthetic.main.row.view.*
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException

class FeedViewHolder(itenView: View):RecyclerView.ViewHolder(itenView), View.OnClickListener, View.OnLongClickListener {
    private var itemClickListener:ItemClickListener? = null

    //val featuredImg: ImageView? = itenView.findViewById(R.id.featuredImg);
    override fun onClick(v: View?) {
        itemClickListener!!.OnClick(v, adapterPosition, false)
    }

    override fun onLongClick(v: View?): Boolean {
        itemClickListener!!.OnClick(v, adapterPosition, true)
        return true
    }



}
class FeedAdapter (private val mValues: List<RssItem>,
                   private val mListener: RssFragment.OnListFragmentInteractionListener?,
                   private val context: Context):RecyclerView.Adapter<FeedViewHolder>()
{
    var arrList: ArrayList<RssItem?> = ArrayList()
    private val inflater:LayoutInflater = LayoutInflater.from(context)
    var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = inflater.inflate(R.layout.row, parent, false)
        return FeedViewHolder(itemView)
    }
    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    override fun getItemCount(): Int {
        return mValues.size
    }
    fun setData(arrNoteList: List<RssItem?>) {
        arrList.clear()
        arrList.addAll(arrNoteList)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.itemView.textTitle.text = mValues[position].title
        holder.itemView.textDate.text = mValues[position].pubDate
        val desc = mValues[position].description.replace("\n","")
        //val img = desc.indexOf("<img")
        var desc1:String = desc
        var desc2:String =""
        while (desc1.indexOf("<")!=-1)
        {
            val toreplace = desc1.substring(desc1.indexOf("<", 0), desc1.indexOf(">")+1)
            desc1 = desc1.replace(toreplace,"")

        }
        holder.itemView.textContent.text = desc1//.substring(16,desc.indexOf("<br>"))
        //val toreplace = desc.substring(desc.indexOf("<img", 0), desc.indexOf("\">")+1)
        //val toreplace2 = desc.substring(desc.indexOf("<img", 0), desc.indexOf("/>")+2)

        //val desc2 = desc1.replace(toreplace2,"")

        var link = getFeaturedImageLink(mValues[position].description)

        if(link != null) {
            context.let {
                holder.itemView.featuredImg?.let { it1 ->
                    Glide.with(it)
                        .load(link)
                        .into(it1)
                }
            }
        }



        holder.itemView.cardView.setOnClickListener {
            listener?.onClicked(mValues[position].link)
//                holder.itemView.webView.webViewClient = WebViewClient()
//
//                holder.itemView.webView.loadUrl("https://www.geeksforgeeks.org/")
//
//                // this will enable the javascript settings, it can also allow xss vulnerabilities
//                holder.itemView.webView.settings.javaScriptEnabled = true
//
//                // if you want to enable zoom feature
//                holder.itemView.webView.settings.setSupportZoom(true)


        }
    }
    interface OnItemClickListener {
        fun onClicked(yrl: String)
    }
    private fun getFeaturedImageLink(htmlText: String): String? {
        var result: String? = null

        val stringBuilder = StringBuilder()
        try {
            val doc: org.jsoup.nodes.Document? = Jsoup.parse(htmlText)
            val imgs: Elements = doc!!.select("img")

            for (img in imgs) {
                var src = img.attr("src")
                result = src
            }

        } catch (e: IOException) {

        }
        return result

    }

}