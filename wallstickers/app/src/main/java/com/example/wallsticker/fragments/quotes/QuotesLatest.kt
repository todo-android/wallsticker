package com.example.wallsticker.fragments.quotes

import android.content.*
import android.os.Bundle
import android.provider.BaseColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wallsticker.Adapters.QuotesAdapter
import com.example.wallsticker.Interfaces.IncrementServiceQuote
import com.example.wallsticker.Interfaces.QuoteClickListener
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.*
import com.example.wallsticker.ViewModel.QuotesViewModel
import com.facebook.ads.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@AndroidEntryPoint
class QuotesLatest : Fragment(), QuoteClickListener {

    private lateinit var clipboardManager: ClipboardManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var refresh: SwipeRefreshLayout
    private val mNativeAds_Fb: ArrayList<AdItem_Fb> = ArrayList()
    private lateinit var interstitialad: interstitial
    private lateinit var progressBar: ProgressBar
    private var offset = 0

    private lateinit var quotesViewmodel: QuotesViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return inflater.inflate(R.layout.fragment_quotes_latest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView(view)
        lifecycleScope.launch {
            quotesViewmodel.readQuotes()
        }

        quotesViewmodel.readQuotes.observe(viewLifecycleOwner, { quotes ->
            if (quotes.isNullOrEmpty()) {
                //Toast.makeText(context, "Null or empty quotes", Toast.LENGTH_LONG).show()
            } else {
                Const.QuotesTemp.clear()
                Const.QuotesTemp.addAll(quotes[0].quotes.results)
                viewAdapter.notifyDataSetChanged()
                Toast.makeText(context, Const.QuotesTemp.size.toString(), Toast.LENGTH_LONG).show()
            }
        })
        quotesViewmodel.quotesNetworkResults.observe(viewLifecycleOwner,{results->

            //Toast.makeText(context,results.message,Toast.LENGTH_LONG).show()
        })


        AdSettings.addTestDevice(resources.getString(R.string.addTestDevice))
        interstitialad = context?.let { interstitial(it) }!!
        interstitialad.loadInter()
        //Toast.makeText(context, interstitialad.hashCode().toString(), Toast.LENGTH_LONG).show()


    }

    private fun initView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.latest_quote_recycler_view)
        refresh = view.findViewById(R.id.refreshLayout)
        progressBar = view.findViewById(R.id.progress)

        quotesViewmodel = ViewModelProvider(requireActivity()).get(QuotesViewModel::class.java)
        viewManager = GridLayoutManager(activity, 1)
        viewAdapter = QuotesAdapter(this, Const.QuotesTemp, context)

        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
        addScrollerListener()
    }


    override fun onQuoteClicked(view: View, quote: Quote, pos: Int) {
        Const.INCREMENT_COUNTER++
        if (Const.INCREMENT_COUNTER % Const.COUNTER_AD_SHOW == 0)
            interstitialad.showInter()
        else {
            Const.quotesarrayof = "latest"
            val GoToSlider = HomeQuotesDirections.actionHomeQuotesToQuotesSlider(pos)
            findNavController().navigate(GoToSlider)
        }

    }

    override fun onShareClicked(quote: Quote) {

        var packageTxt: String? = ""
        if (Const.enable_share_with_package)
            packageTxt =
                "\n" + resources.getString(R.string.share_text) + "\n${resources.getString(R.string.store_prefix) + context?.packageName}"

        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, quote.quote + packageTxt)
        startActivity(Intent.createChooser(shareIntent, "Share To"))
        var incrementShare = quote.count_shared?.plus(1)
        IncrementServiceQuote().incrementShare(quote.id, incrementShare)
            .enqueue(object : Callback<Any> {
                override fun onFailure(call: Call<Any>, t: Throwable) {
                    //Toast.makeText(context,t.message,Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<Any>, response: Response<Any>) {
                    //Toast.makeText(context,"Response :${response}",Toast.LENGTH_LONG).show()
                }

            })
    }

    override fun onCopyClicked(view: View, quote: Quote) {
        val textToCopy = quote.quote
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    override fun onFavClicked(quote: Quote, pos: Int) {
        Const.isFavChanged = true
        val dbHelper = context?.let { helper(it) }
        val db = dbHelper?.writableDatabase
        if (quote.isfav == 0) {
            val values = ContentValues().apply {
                put(BaseColumns._ID, quote.id)
                put(FeedReaderContract.FeedEntry.COLUMN_NAME_QUOTE, quote.quote)
            }
            val newRowId = db!!.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values)
            quote.isfav = 1
            viewAdapter.notifyItemChanged(pos)
            //Toast.makeText(context, newRowId.toString(), Toast.LENGTH_LONG).show()
        } else if (quote.isfav == 1) {
            val selection = "${BaseColumns._ID} like ?"
            val selectionArgs = arrayOf(quote.id.toString())
            val deletedRows =
                db?.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs)
            quote.isfav = 0
            viewAdapter.notifyItemChanged(pos)
            //Toast.makeText(context, deletedRows.toString(), Toast.LENGTH_LONG).show()
        }
    }

    // ad scrolling lister to recycleview
    private fun addScrollerListener() {
        //attaches scrollListener with RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {


            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        offset += 30
                        progressBar.visibility = View.VISIBLE

                    }
                }


            }


        })
    }

    //load Native Ad
    private fun LoadNativeAd() {
        var mNativeAdsManager = NativeAdsManager(
            context,
            resources.getString(R.string.native_facebook),
            5
        )
        AdSettings.addTestDevice(resources.getString(R.string.addTestDevice))
        mNativeAdsManager.loadAds()
        mNativeAdsManager.setListener(object : AdListener, NativeAdsManager.Listener {
            override fun onError(ad: Ad, adError: AdError) {
                // Ad error callback
                //Toast.makeText(context, "Error :( ", Toast.LENGTH_LONG).show()
            }

            override fun onAdLoaded(ad: Ad) {
            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
            }

            override fun onAdsLoaded() {

                val count = mNativeAdsManager.uniqueNativeAdCount
                for (i in 0 until count) {

                    val ad = mNativeAdsManager.nextNativeAd()
                    val adItem = AdItem_Fb(ad)
                    if (!ad.isAdInvalidated) {
                        mNativeAds_Fb.add(adItem)
                    } else {
                    }
                }

                insertAdsInMenuItems()
            }

            override fun onAdError(err: AdError?) {
                // Ad error callback
            }
        })


    }

    private fun insertAdsInMenuItems() {
        if (mNativeAds_Fb.size <= 0) {
            Log.d("ADNative", "insertAdsInMenuItems: Empty Facebook Native ad")
            return
        }
        var indexFB = Const.COUNTER_AD_SHOW
        for (ad in mNativeAds_Fb) {
            //Comment this to close the native Ads
            if (indexFB > Const.QuotesTemp.size) {
                break
            }
            if (Const.QuotesTemp[indexFB] !is AdItem_Fb) {
                Const.QuotesTemp.add(indexFB, ad)
                viewAdapter.notifyItemInserted(indexFB)
                Log.d("ADNative", "insertAdsInMenuItems Facebook|index is :$indexFB")
            }
            indexFB += Const.COUNTER_AD_SHOW + 1
        }
    }


}