package com.example.wallsticker.ViewModel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.wallsticker.Repository.ImagesRepo
import com.example.wallsticker.data.databsae.entities.ImageEntity

class ImageViewModelGetImageByCat @ViewModelInject constructor(
    private val imageRepo: ImagesRepo,
    application: Application,
) : AndroidViewModel(application) {

    fun getImageByCat(): LiveData<List<ImageEntity>> {
        return imageRepo.local.readImageByCategory().asLiveData()
    }
}