package com.example.wallsticker.fragments.quotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wallsticker.Adapters.CategoryAdapter
import com.example.wallsticker.Interfaces.ImageClickListener
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const.Companion.QuotesCategories
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.ViewModel.QuotesViewModel
import kotlinx.coroutines.launch


class QuotesCategory : Fragment(), ImageClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var quotesViewModel: QuotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)


        getCategories()
        quotesViewModel.readCategories.observe(viewLifecycleOwner, { categories ->
            if (!categories.isNullOrEmpty()) {
                QuotesCategories.clear()
                QuotesCategories.addAll(categories[0].Categories.results)

                viewAdapter.notifyDataSetChanged()

                Toast.makeText(context, QuotesCategories.size.toString(), Toast.LENGTH_LONG).show()
            } else if (categories.isEmpty()) {
                Toast.makeText(context, "Empty", Toast.LENGTH_LONG).show()
            }

        })

        quotesViewModel.categoriesNetworkResults.observe(viewLifecycleOwner, { data ->
            Toast.makeText(context, "observer", Toast.LENGTH_LONG).show()
            when (data) {
                is NetworkResults.Success -> {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                }
                is NetworkResults.Error -> {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }

            }
        })


    }

    override fun onImageClicked(view: View, Image: Image, pos: Int) {
    }

    override fun onCatClicked(view: View, category: Category, pos: Int) {
        val GoToQuotesByCategory =
            HomeQuotesDirections.actionHomeQuotesToQuotesByCategory(category.id)
        findNavController().navigate(GoToQuotesByCategory)
    }

    private fun initView(view: View) {
        refresh = view.findViewById(R.id.refreshLayout)
        recyclerView = view.findViewById<RecyclerView>(R.id.cat_quotes_recycler_view)
        viewAdapter = CategoryAdapter(this)
        viewManager = GridLayoutManager(activity, 1)
        quotesViewModel = ViewModelProvider(requireActivity()).get(QuotesViewModel::class.java)
        //RecycleView
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)

    }

    private fun getCategories() {
        lifecycleScope.launch {
            quotesViewModel.getCategories()
        }
    }
}