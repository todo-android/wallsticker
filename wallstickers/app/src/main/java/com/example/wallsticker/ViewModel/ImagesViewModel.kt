package com.example.wallsticker.ViewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Repository.ImagesRepo
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.data.databsae.entities.CategoryEntity
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import com.example.wallsticker.data.databsae.entities.ImageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class ImagesViewModel @ViewModelInject constructor(
    private val imageRepo: ImagesRepo,
    application: Application
) : AndroidViewModel(application) {

    /**ROOM DATABASE**/
    val readImages: LiveData<List<ImageEntity>> = imageRepo.local.readdatabase().asLiveData()
    val readFavorite: LiveData<List<FavoritesEntity>> = imageRepo.local.readFavorite().asLiveData()
    val readCategories :LiveData<List<CategoryEntity>> = imageRepo.local.readCategories().asLiveData()


    private fun insert(imageEntity: ImageEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            imageRepo.local.insertImage(imageEntity)
        }

    private fun insertCategories(categoryEntity: CategoryEntity)=
        viewModelScope.launch (Dispatchers.IO){
            imageRepo.local.insertCategories(categoryEntity)
        }

    fun insertFavorite(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            imageRepo.local.insertFavorite(favoritesEntity)
        }

    fun deleteFavorite(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            imageRepo.local.deleteFavorite(favoritesEntity)
        }

    /** RETROFIT **/
    var categories: MutableLiveData<NetworkResults<Categories>> = MutableLiveData()
    var imagesData: MutableLiveData<NetworkResults<Images>> = MutableLiveData()

    fun getImagesCategories() {
            viewModelScope.launch {
                getCategoriesSafeCall()
            }
    }

    private suspend fun getCategoriesSafeCall() {
        if  (readCategories.value.isNullOrEmpty()){
            if (hasInternetConnection()) {
                try {
                    val categoriesResponse = imageRepo.remot.getCategories()
                    categories.value = handCategoriesResponse(categoriesResponse)

                } catch (ex: Exception) {
                    imagesData.value = NetworkResults.Error(ex.message)
                }
            } else {
                imagesData.value = NetworkResults.Error("No Internet Connection")
            }
        }
        //categories.value=NetworkResults.Loading()


    }



    private fun handCategoriesResponse(categoriesResponse: Response<Categories>): NetworkResults<Categories>? {
        when {
            categoriesResponse.message().toString()
                .contains("Timeout") -> return NetworkResults.Error("Timeout")
            categoriesResponse.code() == 402 -> return NetworkResults.Error("Api Key Limited.")
            categoriesResponse.body()!!.results.isNullOrEmpty() -> return NetworkResults.Error("No Data Found")
            categoriesResponse.isSuccessful -> {
                val categories = categoriesResponse.body()
                val categoryEntity = categories?.let { CategoryEntity(0, it) }
                insertCategories(categoryEntity!!)
                return NetworkResults.Success(categories!!)
            }
            else -> return NetworkResults.Error(categoriesResponse.message())
        }
    }


    fun getImages() {
        viewModelScope.launch {
            getImagesSafeCall()
        }
    }

    private suspend fun getImagesSafeCall() {
        imagesData.value = NetworkResults.Loading()
        if (hasInternetConnection()) {
            try {
                val imagesResponse = imageRepo.remot.getImages()
                imagesData.value = handlImagesResponse(imagesResponse)

                val imageCache = imagesData.value!!.data
                if (imageCache != null) {
                    offlineCacheImages(imageCache)
                }
            } catch (ex: Exception) {
                imagesData.value = NetworkResults.Error(ex.message)
            }

        } else {
            imagesData.value = NetworkResults.Error("No Internet Connection")
        }
    }

    private fun offlineCacheImages(images: Images) {
        val imageEntity=ImageEntity(images)
        insertRecipes(imageEntity)

    }

    private fun insertRecipes(imageEntity: ImageEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            imageRepo.local.insertImage(imageEntity)
        }

    private fun handlImagesResponse(imagesResponse: Response<Images>): NetworkResults<Images>? {
        when {
            imagesResponse.message().toString()
                .contains("Timeout") -> return NetworkResults.Error("Timeout")
            imagesResponse.code() == 402 -> return NetworkResults.Error("Api KEy Limited.")
            imagesResponse.body()!!.results.isNullOrEmpty() -> return NetworkResults.Error("No Data Found")
            imagesResponse.isSuccessful -> {
                val images = imagesResponse.body()
                return NetworkResults.Success(images!!)
            }
            else -> return NetworkResults.Error(imagesResponse.message())
        }
    }


    fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilites = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilites.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}