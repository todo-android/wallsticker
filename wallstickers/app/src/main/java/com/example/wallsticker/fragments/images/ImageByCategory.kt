package com.example.wallsticker.fragments.images

import android.os.Bundle
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
import com.example.wallsticker.Adapters.ImagesAdapter
import com.example.wallsticker.Interfaces.ImageClickListener
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.ViewModel.ImageViewModelGetImageByCat
import com.example.wallsticker.ViewModel.ImagesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageByCategory : Fragment(), ImageClickListener {

    val args: ImageByCategoryArgs by navArgs()

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var progressBar: ProgressBar
    private lateinit var refresh: SwipeRefreshLayout
    lateinit var layoutManager: LinearLayoutManager
    var offset: Int = 0
    private lateinit var imagesViewMode: ImagesViewModel
    private var Images = arrayListOf<Image>()
    private var isDataLoaded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_img_latest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        iniView(view)
        val viewManager = GridLayoutManager(activity, 3)
        layoutManager = LinearLayoutManager(activity)
        refresh = view.findViewById(R.id.refreshLayout)


        readDatabase()

        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
        //addScrollerListener()
        if (Const.ImagesByCatTemp.size <= 0) {
            refresh.isRefreshing = true
            //fetchImages()
        }

    }

    private fun iniView(view: View) {


        //imagesViewMode= ViewModelProvider(this,imageviewModelFactory).get(ImagesViewModel::class.java)

        viewManager = GridLayoutManager(activity, 3)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progress)
        refresh = view.findViewById(R.id.refreshLayout)
        //viewAdapter = ImagesAdapter(this, Const.ImagesByCatTemp, context)

        //viewAdapter = ImagesAdapter(this, Const.ImagesTemp, context)
        recyclerView = view.findViewById(R.id.images_recycler_view)
        Images = ArrayList()
        viewAdapter = ImagesAdapter(this, requireContext(), Const.ImagesByCatTemp)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        imagesViewMode = ViewModelProvider(requireActivity()).get(ImagesViewModel::class.java)

        recyclerView.setHasFixedSize(true)

    }


    override fun onImageClicked(view: View, Image: Image, pos: Int) {
        Const.arrayOf = "byCat"
        val action2 =
            ImageByCategoryDirections.actionImageByCategoryToImgSlider(
                pos
            )
        findNavController().navigate(action2)
    }



    private fun readDatabase() {


        Toast.makeText(context, args.CatId.toString(), Toast.LENGTH_LONG).show()
            imagesViewMode.readImages.observe(viewLifecycleOwner, { database ->
                if (database.isNullOrEmpty()) {
                    imagesViewMode.getImages()
                    Toast.makeText(context, "null or empty", Toast.LENGTH_LONG).show()
                    return@observe
                } else if (Const.ImagesByCatTemp.isNullOrEmpty()) {
                    Const.ImagesByCatTemp.clear()
                    Const.ImagesByCatTemp.addAll(database[0].images.results.shuffled().filter { img->img.cat_id==6 })
                    Toast.makeText(context,Const.ImagesTemp.size.toString(),Toast.LENGTH_LONG).show()
                    //
                    refresh.isRefreshing = false
                    viewAdapter.notifyDataSetChanged()
                    //viewAdapter.setData(database[0].images)
                }
            })

    }


    override fun onCatClicked(view: View, category: Category, pos: Int) {
        //this lister for categoty clicked
    }

}