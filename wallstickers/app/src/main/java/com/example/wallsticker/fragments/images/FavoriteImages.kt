package com.example.wallsticker.fragments.images

import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallsticker.Adapters.ImagesAdapter
import com.example.wallsticker.Interfaces.ImageClickListener
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import com.example.wallsticker.Model.Quote
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.FeedReaderContract
import com.example.wallsticker.ViewModel.ImagesViewModel


class FavoriteImages : Fragment(), ImageClickListener {


    private var itemIds: ArrayList<Quote>? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var nofav: LinearLayout
    private lateinit var imagesViewMode: ImagesViewModel

    val projection = arrayOf(BaseColumns._ID, FeedReaderContract.FeedEntryImage.COLUMN_NAME_IMAGE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_images_fav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        nofav = view.findViewById(R.id.nofav)
        recyclerView = view.findViewById<RecyclerView>(R.id.fav_images_recycler_view)
        viewManager = GridLayoutManager(activity, 3)
        viewAdapter = ImagesAdapter(this, context, Const.ImageTempFav)
        recyclerView.adapter = viewAdapter
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
        imagesViewMode = ViewModelProvider(requireActivity()).get(ImagesViewModel::class.java)
        imagesViewMode.readFavorite.observe(viewLifecycleOwner, { favorites ->
            Const.ImageTempFav.clear()
            if (favorites.isEmpty()) {
                nofav.visibility = View.VISIBLE
            } else {
                for (fav in favorites) {
                    fav.image.isfav = 1
                    Const.ImageTempFav.add(fav.image)
                }
            }
            viewAdapter.notifyDataSetChanged()
        })
    }


    override fun onImageClicked(view: View, Image: Image, pos: Int) {
        Const.arrayOf = "fav"
        val action2 =
            ImagesFragmentDirections.actionImagesFragmentToImgSlider(
                pos
            )
        findNavController().navigate(action2)
    }

    override fun onCatClicked(view: View, category: Category, pos: Int) {
        //don't override this it's for category adapter
    }


//    override fun onFavClicked(image: image, pos: Int) {
//        Const.isFavChanged = true
//        val dbHelper = context?.let { helper(it) }
//        val db = dbHelper?.writableDatabase
//        if (image.isfav == 1) {
//            val selection = "${BaseColumns._ID} like ?"
//            val selectionArgs = arrayOf(image.image_id.toString())
//            val deletedRows =
//                db?.delete(FeedReaderContract.FeedEntryImage.TABLE_NAME, selection, selectionArgs)
//            image.isfav = 0
//            Const.ImageTempFav.remove(image)
//            viewAdapter.notifyItemRemoved(pos)
//            Toast.makeText(context, deletedRows.toString(), Toast.LENGTH_LONG).show()
//        }
//
//    }
}