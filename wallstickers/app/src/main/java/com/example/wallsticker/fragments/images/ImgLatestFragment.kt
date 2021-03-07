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
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.Utilities.interstitial
import com.example.wallsticker.ViewModel.ImagesViewModel
import com.facebook.ads.AdSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImgLatestFragment : Fragment(), ImageClickListener {

    private lateinit var recyclerView: RecyclerView

    //private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var progressBar: ProgressBar
    lateinit var layoutManager: LinearLayoutManager
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var interstitialad: interstitial

    private lateinit var imagesViewMode: ImagesViewModel

    //val progressBar: ProgressBar = this.progressBar2
    private lateinit var viewAdapter: ImagesAdapter

    private var isDataLoaded: Boolean = false
    var offset: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_img_latest, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        iniView(view)
        refresh.setOnRefreshListener {
            refresh.isRefreshing = true
            //Const.ImagesTemp.clear()
            //imagesViewMode.getImages()

        }

        //imagesViewMode.getImages()


        readDatabase()
        imagesViewMode.imagesData.observe(viewLifecycleOwner, { images ->
            when (images) {
                is NetworkResults.Success -> {
                    images.data?.let {
                        //viewAdapter.setData(it)
                        //Const.ImagesTemp.addAll(it.results)
                        //readDatabase()
                        //Toast.makeText(context, "get it", Toast.LENGTH_LONG).show()
                        viewAdapter.notifyDataSetChanged()
                    }

                    refresh.isRefreshing = false
                }
                is NetworkResults.Error -> {
                    Toast.makeText(context, images.message.toString(), Toast.LENGTH_LONG).show()
                    refresh.isRefreshing = false
                    readDatabase()
                }
                is NetworkResults.Loading -> {
                    progressBar.visibility = View.GONE
                    refresh.isRefreshing = true
                }
            }
        })

        addScrollerListener()
        if (Const.ImagesTemp.isEmpty()) {
            refresh.isRefreshing = true
            //fetchImages()
        }

        AdSettings.addTestDevice(resources.getString(R.string.addTestDevice))
        interstitialad = context?.let { interstitial(it) }!!
        interstitialad.loadInter()
        //Toast.makeText(context, Const.ImagesTemp.size.toString(), Toast.LENGTH_LONG).show()


    }

    private fun iniView(view: View) {
        //init ViewModel
        imagesViewMode = ViewModelProvider(requireActivity()).get(ImagesViewModel::class.java)

        viewManager = GridLayoutManager(activity, 3)
        layoutManager = LinearLayoutManager(activity)
        progressBar = view.findViewById(R.id.progress)
        refresh = view.findViewById(R.id.refreshLayout)

        viewAdapter = ImagesAdapter(this, context, Const.ImagesTemp)
        recyclerView = view.findViewById<RecyclerView>(R.id.images_recycler_view)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)

    }


    override fun onImageClicked(view: View, Image: Image, pos: Int) {
        //val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()

        Const.INCREMENT_COUNTER++
        if (Const.INCREMENT_COUNTER % Const.COUNTER_AD_SHOW == 0)
            interstitialad.showInter()
        else {
            Const.arrayOf = "latest"
            val action2 =
                ImagesFragmentDirections.actionImagesFragmentToImgSlider(
                    pos
                )
            findNavController().navigate(action2)
        }
        //Toast.makeText(context,image.image_id.toString(),Toast.LENGTH_LONG).show()
    }


    private fun addScrollerListener() {
        //attaches scrollListener with RecyclerView
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && Const.ImagesTemp.isNotEmpty()) {
                        offset += 30
                        //progressBar.visibility = View.VISIBLE
                        //fetchImages()
                        imagesViewMode.getImages()
                    }
                }
            }
        })
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            imagesViewMode.readImages.observe(viewLifecycleOwner, { database ->
                if (database.isNullOrEmpty()) {
                    imagesViewMode.getImages()
                    Toast.makeText(context, "null or empty", Toast.LENGTH_LONG).show()
                } else if (!isDataLoaded) {
                    Const.ImagesTemp.clear()
                    Const.ImagesTemp.addAll(database[0].images.results.shuffled())
                    Toast.makeText(context, Const.ImagesTemp.size.toString(), Toast.LENGTH_LONG)
                        .show()
                    //Toast.makeText(context, "get database for first time", Toast.LENGTH_LONG).show()
                    refresh.isRefreshing = false
                    isDataLoaded = true
                    //viewAdapter.setData(database[0].images)
                }
            })
        }
    }


    override fun onCatClicked(view: View, category: Category, pos: Int) {
        //this lister for categoty clicked
    }


}