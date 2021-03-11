package com.example.wallsticker.Utilities

import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image
import com.example.wallsticker.Model.Quote

class Const {

    companion object {

        var apiurl = "https://usaapi.dev3pro.co/api/"
        var DELAY_SET_WALLPAPER: Long = 2000
        var COUNTER_AD_SHOW = 3
        var INCREMENT_COUNTER = 0
        val directoryUpload: String = com.example.wallsticker.Config.BASE_URL + "upload/images/"
        val directoryUploadCat: String =
            com.example.wallsticker.Config.BASE_URL + "upload/category/"


        //for images
        var ImagesTemp = arrayListOf<Image>()
        var ImagesByCatTemp = arrayListOf<Image>()
        var ImageTempFav = arrayListOf<Image>()

        var CatImages = arrayListOf<Category>()


        var isFavChanged = true
        var arrayOf: String = "latest"

        const val enable_share_with_package: Boolean = true


        //for quotes
        var quotesarrayof: String = "latest"
        var quotes = ArrayList<Quote>()
        var QuotesTemp = arrayListOf<Any>()
        var QuotesTempFav = arrayListOf<Any>()
        var QuotesByCat = arrayListOf<Any>()
        var QuotesCategories = arrayListOf<Category>()

        //Room database
        const val DATABASE_NAME = "MY_DB"
        const val TABLE_IMAGE = "tbl_images"
        const val TABLE_FAVORITE = "favorite_tbl"
        const val TABLE_CATEGORY = "tbl_category"

        const val TABLE_QUOTE = "tbl_quote"
        const val TABLE_CATEGOY_QUOTE="tbl_category_quote"
    }


}