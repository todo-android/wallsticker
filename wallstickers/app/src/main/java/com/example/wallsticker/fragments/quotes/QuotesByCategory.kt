package com.example.wallsticker.fragments.quotes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wallsticker.Adapters.QuotesAdapter
import com.example.wallsticker.Interfaces.IncrementServiceQuote
import com.example.wallsticker.Interfaces.QuoteClickListener
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.AdItem_Fb
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.interstitial
import com.example.wallsticker.ViewModel.QuotesViewModel
import com.example.wallsticker.data.databsae.entities.QuoteFavoritesEntity
import com.facebook.ads.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuotesByCategory : Fragment(), QuoteClickListener {

    val args: QuotesByCategoryArgs by navArgs()


    private lateinit var clipboardManager: ClipboardManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val mNativeAds_Fb: ArrayList<AdItem_Fb> = ArrayList()
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var interstitialad: interstitial
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var quotesViewmodel: QuotesViewModel
    private var offset = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes_by_category, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)


        quotesViewmodel.readQuotes.observe(viewLifecycleOwner, { quotes ->
            if (quotes.isNullOrEmpty()) {
                Toast.makeText(context, "Null or empty quotes", Toast.LENGTH_LONG).show()
            } else {
                Const.QuotesByCat.clear()
                Const.QuotesByCat.addAll(quotes[0].quotes.results.filter { q -> q.cid == args.catId })
                viewAdapter.notifyDataSetChanged()
            }
        })

        AdSettings.addTestDevice(resources.getString(R.string.addTestDevice))
        interstitialad = context?.let { interstitial(it) }!!
        interstitialad.loadInter()

    }


    private fun initview(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.bycat_quote_recycler_view)
        refresh = view.findViewById(R.id.refreshLayout)
        progressBar = view.findViewById(R.id.progress)
        clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        viewManager = GridLayoutManager(activity, 1)
        layoutManager = LinearLayoutManager(context)
        quotesViewmodel = ViewModelProvider(requireActivity()).get(QuotesViewModel::class.java)
        viewAdapter = QuotesAdapter(this, Const.QuotesByCat, context)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
    }

    override fun onQuoteClicked(view: View, quote: Quote, pos: Int) {
        Const.INCREMENT_COUNTER++
        if (Const.INCREMENT_COUNTER % Const.COUNTER_AD_SHOW == 0)
            interstitialad.showInter()
        else {
            Const.quotesarrayof = "byCat"
            val GoToSlider = QuotesByCategoryDirections.actionQuotesByCategoryToQuotesSlider(pos)
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
        lifecycleScope.launch {
            if (quote.isfav == 0 || quote.isfav == null) {
                quote.isfav = 1
                quotesViewmodel.insertFavorite(QuoteFavoritesEntity(quote.id!!, quote))

            } else {
                quotesViewmodel.deleteFavorite(QuoteFavoritesEntity(quote.id!!, quote))
                quote.isfav = 0
            }
        }
        viewAdapter.notifyItemChanged(pos)
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
                //Toast.makeText(context, "Error: " + adError.errorCode.toString(), Toast.LENGTH_LONG).show()
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
                //Toast.makeText(context, "Error: " + err?.errorMessage.toString(), Toast.LENGTH_LONG).show()
            }
        })


    }

    private fun insertAdsInMenuItems() {
        if (mNativeAds_Fb.size <= 0) {
            Log.d("ADNative", "insertAdsInMenuItems: Empty Facebook Native ad")
            return
        }
        var indexFB = 3
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
            indexFB += 4
        }
    }


}