package com.example.wallsticker.fragments.images

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.wallsticker.Adapters.CategoryAdapter
import com.example.wallsticker.Interfaces.ImageClickListener
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.ViewModel.ImagesViewModel

class ImgCategoryFragment : Fragment(), ImageClickListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: CategoryAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var imagesViewMode: ImagesViewModel
    private var DataIsCached: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_img_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView(view)

        refresh.isRefreshing = false
        refresh.setOnRefreshListener {
            //refresh.isRefreshing = true
            //Const.ImagesTemp.clear()
            //imagesViewMode.getImagesCategories()
        }

        imagesViewMode.getImagesCategories()
        imagesViewMode.categories.observe(viewLifecycleOwner, { categories ->
            when (categories) {
                is NetworkResults.Success -> {
                    Toast.makeText(
                        context,
                        categories.message.toString() + "Categories",
                        Toast.LENGTH_LONG
                    ).show()
                    //viewAdapter.notifyDataSetChanged()
                    //refresh.isRefreshing = false
                }
                is NetworkResults.Error -> {

                    refresh.isRefreshing = false

                }
                is NetworkResults.Loading -> {
                    //refresh.isRefreshing = true
                }
            }
        })

        readDatabase()


    }


    private fun initView(view: View) {
        imagesViewMode = ViewModelProvider(requireActivity()).get(ImagesViewModel::class.java)
        viewManager = GridLayoutManager(activity, 1)
        refresh = view.findViewById(R.id.refreshLayout)
        viewAdapter = CategoryAdapter(this)
        recyclerView = view.findViewById(R.id.CatImg_recycler_view)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
    }


    override fun onCatClicked(view: View, category: Category, pos: Int) {
        Const.ImagesByCatTemp.clear()
        Const.arrayOf = "byCat"
        val actionToImageByCat =
            ImagesFragmentDirections.actionImagesFragmentToImageByCategory(
                category.id
            )
        findNavController().navigate(actionToImageByCat)
    }

    override fun onImageClicked(view: View, Image: Image, pos: Int) {
        //this for image clicked
    }

    fun readDatabase() {

        imagesViewMode.readCategories.observe(viewLifecycleOwner, { categories ->
            if (!categories.isNullOrEmpty()) {
                viewAdapter.setData(categories[0].Categories)
                refresh.isRefreshing = false
            } else
                Toast.makeText(context, "Empty Categories", Toast.LENGTH_LONG).show()
        })
    }

}