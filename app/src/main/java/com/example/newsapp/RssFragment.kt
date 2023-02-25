package com.example.newsapp

import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.Adapter.FeedAdapter
import com.example.newsapp.Model.RssItem
import kotlinx.android.synthetic.main.fragment_rss_list.*

import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RssFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RssFragment : Fragment() {
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    var arrNotes = java.util.ArrayList<RssItem>()

    val RSS_FEED_LINK ="https://money.onliner.by/feed"//"https://www.woman.ru/forum/rss/"//"https://xoppop.ru/index.php?mod=rss"//"https://dom.sibmama.ru/rss.xml"//"https://money.onliner.by/feed"//"https://proweb63.ru/feed.xml";

    var adapter: FeedAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var url: URL? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rss_list, container, false)

        return view;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = activity?.let { FeedAdapter(rssItems, listener, it) }
        recycleview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycleview.adapter = adapter

        url = URL(RSS_FEED_LINK)
        RssFeedFetcher(this).execute(url)
        adapter!!.setOnClickListener(onClicked)

    }

    fun updateRV(rssItemsL: List<RssItem>) {
        if (!rssItemsL.isEmpty()) {
            rssItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
        }
    }

    class RssFeedFetcher(context: RssFragment) : AsyncTask<URL, Void, List<RssItem>>() {
        val reference = WeakReference(context)
        private var stream: InputStream? = null;


        override fun doInBackground(vararg params: URL?): List<RssItem>? {
            val connect = params[0]?.openConnection() as HttpsURLConnection
            connect.readTimeout = 8000
            connect.connectTimeout = 8000
            connect.requestMethod = "GET"
            connect.connect();

            val responseCode: Int = connect.responseCode;
            var rssItems: List<RssItem>? = null
            if (responseCode == 200) {
                stream = connect.inputStream;


                try {
                    val parser = RssParser()
                    rssItems = parser.parse(stream!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }
            else
            {
                Toast.makeText(RssFragment().context, "Deleted from", Toast.LENGTH_SHORT).show()
            }


            return rssItems

        }

        override fun onPostExecute(result: List<RssItem>?) {
            super.onPostExecute(result)
            if (result != null && !result.isEmpty()) {
                reference.get()?.updateRV(result)
            }

        }

    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: RssItem?)
    }


    private val onClicked = object : FeedAdapter.OnItemClickListener {
        override fun onClicked(yrl:String) {

            val fragment: Fragment
            val bundle = Bundle()
            bundle.putString("url", yrl)
            fragment = WebView.newInstance()
            fragment.arguments = bundle

            replaceFragment(fragment, false)
        }
    }
    fun replaceFragment(fragment: Fragment, istransition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }
        fragmentTransition.add(R.id.layout, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu)
        {
            RssFeedFetcher(RssFragment()).execute(url)
        }
        return true
    }
}