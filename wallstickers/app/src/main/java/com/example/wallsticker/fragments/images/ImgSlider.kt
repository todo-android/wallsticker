package com.example.wallsticker.fragments.images

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.example.wallsticker.Adapters.ImagesSliderAdapter
import com.example.wallsticker.Model.Image
import com.example.wallsticker.R
import com.example.wallsticker.Utilities.Const
import com.example.wallsticker.Utilities.ShareTask
import com.example.wallsticker.ViewModel.ImagesViewModel
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import java.io.File


class ImgSlider : Fragment(R.layout.fragment_img_slider) {

    val args: ImgSliderArgs by navArgs()
    private lateinit var btnShare: ImageView
    private lateinit var btnShareinsta: ImageView
    private lateinit var btnSharewtsp: ImageView
    private lateinit var btnFav: ImageView
    private lateinit var btndownload: ImageView
    private lateinit var image: Image
    private var imagePosition: Int? = null
    private lateinit var viewpager: ViewPager
    private lateinit var imagesViewMode: ImagesViewModel

    //private val mainViewModel: ImagesViewModel by  viewModels()
    var arrayOf = Const.arrayOf
    var msg: String? = ""
    var lastMsg = ""

    var Images: ArrayList<Image> =
        if (arrayOf == "latest") Const.ImagesTemp
        else if (arrayOf == "byCat") Const.ImagesByCatTemp
        else Const.ImageTempFav

    var savedImageId = 0

    @Override
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imagesViewMode = ViewModelProvider(requireActivity()).get(ImagesViewModel::class.java)
        image = Images[args.position]
        savedImageId = image.image_id
        imagePosition = args.position
        initView(view)

        val adapter = this.context?.let { ImagesSliderAdapter(it) }
        viewpager.adapter = adapter
        viewpager.currentItem = args.position



        if (image.isfav == 1)
            btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_is_fav))
        else btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_favorite_border_24))

        Toast.makeText(context, image.cat_id.toString(), Toast.LENGTH_LONG).show()


        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                image = Images[position]
                savedImageId = image.image_id
                imagePosition = position
                if (image.isfav == 1)
                    btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_is_fav))
                else btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                //Toast.makeText(context, image.image_id.toString(), Toast.LENGTH_LONG).show()

            }
        })

        btndownload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissions()
            }
            downloadImage(Const.directoryUpload + image.image_upload)
        }
        btnShare.setOnClickListener {
            ShareTask(context, null).execute(Const.directoryUpload + image.image_upload)
        }

        btnFav.setOnClickListener {

            if (image.isfav == 0 || image.isfav == null) {
                image.isfav = 1
                btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_is_fav))
                val fav = FavoritesEntity(savedImageId, image)
                imagesViewMode.insertFavorite(fav)
                Toast.makeText(context, "added", Toast.LENGTH_LONG).show()
            } else {
                image.isfav = 1
                val fav = FavoritesEntity(savedImageId, image)
                imagesViewMode.deleteFavorite(fav)
                btnFav.setImageDrawable(context?.getDrawable(R.drawable.ic_baseline_favorite_border_24))
                Toast.makeText(context, "Removed", Toast.LENGTH_LONG).show()
            }


        }
        btnSharewtsp.setOnClickListener {
            ShareTask(context, "com.whatsapp").execute(Const.directoryUpload + image.image_upload)

        }
        btnShareinsta.setOnClickListener {
            ShareTask(
                context,
                "com.instagram.android"
            ).execute(Const.directoryUpload + image.image_upload)

        }


    }

    private fun initView(view: View) {

        btnShare = view.findViewById(R.id.fabShare)
        btnShareinsta = view.findViewById(R.id.share_insta)
        btnSharewtsp = view.findViewById(R.id.share_wtsp)
        btnFav = view.findViewById(R.id.btn_fav)
        btndownload = view.findViewById(R.id.fabDownload)
        viewpager = view.findViewById(R.id.viewpagers)

    }

    private fun askPermissions() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            } else {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
                )
            }
        }
    }


    fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }


        val downloadManager =
            context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false

                    //context.let {  Toast.makeText(it, "msg", Toast.LENGTH_SHORT).show() }

                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    Activity().runOnUiThread {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
        return msg
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    if ((ContextCompat.checkSelfPermission(
                            Activity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) ==
                                PackageManager.PERMISSION_GRANTED)
                    ) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}


