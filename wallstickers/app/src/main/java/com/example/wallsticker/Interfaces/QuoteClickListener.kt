package com.example.wallsticker.Interfaces

import android.view.View
import com.example.wallsticker.Model.Quote

interface QuoteClickListener {
    fun onQuoteClicked(view: View, quote: Quote, pos: Int)
    fun onShareClicked(quote: Quote)
    fun onCopyClicked(view: View, quote: Quote)
    fun onFavClicked(quote: Quote, pos: Int)
}