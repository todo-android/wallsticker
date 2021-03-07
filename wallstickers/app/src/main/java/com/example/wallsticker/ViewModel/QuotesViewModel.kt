package com.example.wallsticker.ViewModel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*


import com.example.wallsticker.Repository.QuotesRepo
import com.example.wallsticker.data.databsae.entities.CategoryEntity
import com.example.wallsticker.data.databsae.entities.FavoritesEntity

import com.example.wallsticker.data.databsae.entities.QuoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.*
import com.example.wallsticker.Model.Images
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Quotes
import com.example.wallsticker.Repository.ImagesRepo
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.data.databsae.entities.ImageEntity
import kotlinx.coroutines.launch
import retrofit2.Response


class QuotesViewModel @ViewModelInject constructor(
    private val quotesRepo: QuotesRepo,
    application: Application
    ): AndroidViewModel(application){

    /** RETROFIT **/
    var categories: MutableLiveData<NetworkResults<Categories>> = MutableLiveData()
    var quotesNetworkResults: MutableLiveData<NetworkResults<Quotes>> = MutableLiveData()

    /**ROOM DATABASE**/
    val readQuotes: LiveData<List<QuoteEntity>> = quotesRepo.local.readQuotes().asLiveData()
    val readFavorite: LiveData<List<FavoritesEntity>> = quotesRepo.local.readFavorite().asLiveData()
    val readCategories : LiveData<List<CategoryEntity>> = quotesRepo.local.readCategories().asLiveData()




    private fun insert(quoteEntity: QuoteEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            quotesRepo.local.insertQuotes(quoteEntity)
        }

    private fun readQuotes(){}


    private suspend fun getQuotesSafeCall(){
        if  (readQuotes.value.isNullOrEmpty()){
            if (hasInternetConnection()){
                try {
                    val quotesResponse=quotesRepo.remot.getQuotes()
                    quotesNetworkResults.value = handleQuotes(quotesResponse)
                }catch (ex:Exception){

                }
            }
        }else {

        }
    }

    private fun handleQuotes(quotesResponse: Response<Quotes>): NetworkResults<Quotes> {
        when{
            quotesResponse.message().toString()
                .contains("Timeout") -> return NetworkResults.Error("Timeout")
            quotesResponse.code() == 402 -> return NetworkResults.Error("Api Key Limited.")
            quotesResponse.body()!!.results.isNullOrEmpty() -> return NetworkResults.Error("No Data Found")
            quotesResponse.isSuccessful -> {
                val quotes = quotesResponse.body()
                return NetworkResults.Success(quotes!!)
            }
            else -> return NetworkResults.Error(quotesResponse.message())
        }
    }

    /** **/





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