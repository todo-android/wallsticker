package com.example.wallsticker.Interfaces

import android.view.View
import com.example.wallsticker.Model.Category
import com.example.wallsticker.Model.Image

interface ImageClickListener {

    fun onImageClicked(view: View, Image: Image, pos: Int)
    fun onCatClicked(view: View, category: Category, pos: Int)

}