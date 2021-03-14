package com.example.wallsticker.fragments.quotes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallsticker.Adapters.QuotesAdapter
import com.example.wallsticker.Interfaces.QuoteClickListener
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.FeedReaderContract
import com.example.wallsticker.ViewModel.QuotesViewModel
import com.example.wallsticker.data.databsae.entities.QuoteFavoritesEntity
import kotlinx.coroutines.launch

private lateinit var quotesViewModel: QuotesViewModel


class FavoriteQuotes : Fragment(), QuoteClickListener {


    private var itemIds: ArrayList<Quote>? = null
    private lateinit var clipboardManager: ClipboardManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var nofav: LinearLayout
    val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntry.COLUMN_NAME_QUOTE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes_fav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView(view)
        quotesViewModel.readFavorite.observe(viewLifecycleOwner, { favorites ->
            if (favorites.isNullOrEmpty()) {
                nofav.visibility = View.VISIBLE
                Const.QuotesTempFav.clear()
            } else {
                Const.QuotesTempFav.clear()
                for (fav in favorites) {
                    fav.quote.isfav = 1
                    Const.QuotesTempFav.add(fav.quote)
                }
                nofav.visibility = View.GONE
            }
            viewAdapter.notifyDataSetChanged()
        })


    }


    override fun onQuoteClicked(view: View, quote: Quote, pos: Int) {
        Const.quotesarrayof = "favs"
        val GoToSlider = HomeQuotesDirections.actionHomeQuotesToQuotesSlider(pos)
        findNavController().navigate(GoToSlider)
    }

    private fun initView(view: View) {
        clipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        nofav = view.findViewById(R.id.nofav)
        recyclerView = view.findViewById<RecyclerView>(R.id.fav_quote_recycler_view)
        quotesViewModel = ViewModelProvider(requireActivity()).get(QuotesViewModel::class.java)
        viewManager = GridLayoutManager(activity, 1)
        viewAdapter = QuotesAdapter(this, Const.QuotesTempFav, context)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
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
                quotesViewModel.insertFavorite(QuoteFavoritesEntity(quote.id!!, quote))

            } else {
                quotesViewModel.deleteFavorite(QuoteFavoritesEntity(quote.id!!, quote))
                quote.isfav = 0

            }

        }
        viewAdapter.notifyItemChanged(pos)

    }
}