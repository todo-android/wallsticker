package com.example.wallsticker.ViewModel


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.wallsticker.Model.Categories
import com.example.wallsticker.Model.Quotes
import com.example.wallsticker.Repository.QuotesRepo
import com.example.wallsticker.Utilities.NetworkResults
import com.example.wallsticker.data.databsae.entities.FavoritesEntity
import com.example.wallsticker.data.databsae.entities.QuoteEntity
import com.example.wallsticker.data.databsae.entities.QuotesCategoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class QuotesViewModel @ViewModelInject constructor(
    private val quotesRepo: QuotesRepo,
    application: Application
) : AndroidViewModel(application) {

    /** RETROFIT **/
    var categories: MutableLiveData<NetworkResults<Categories>> = MutableLiveData()
    var quotesNetworkResults: MutableLiveData<NetworkResults<Quotes>> = MutableLiveData()
    var categoriesNetworkResults: MutableLiveData<NetworkResults<Categories>> = MutableLiveData()


    /**ROOM DATABASE**/
    var readQuotes: LiveData<List<QuoteEntity>> = quotesRepo.local.readQuotes().asLiveData()
    var readFavorite: LiveData<List<FavoritesEntity>> = quotesRepo.local.readFavorite().asLiveData()
    var readCategories: LiveData<List<QuotesCategoryEntity>> =
        quotesRepo.local.readCategoriesQuotes().asLiveData()


    private fun insert(quoteEntity: QuoteEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            quotesRepo.local.insertQuotes(quoteEntity)
        }

    suspend fun readQuotes() {
        getQuotesSafeCall()
    }


    private suspend fun getQuotesSafeCall() {

        if (readQuotes.value.isNullOrEmpty()) {
            if (hasInternetConnection()) {
                try {
                    val quotesResponse = quotesRepo.remot.getQuotes()
                    quotesNetworkResults.value = handleQuotes(quotesResponse)

                    //Log.d("Tag_quote","handle quotes")
                } catch (ex: Exception) {
                    quotesNetworkResults.value = NetworkResults.Error(ex.message)

                }
            }
        } else {
            quotesNetworkResults.value = NetworkResults.Cached()
        }
    }

    private fun handleQuotes(quotesResponse: Response<Quotes>): NetworkResults<Quotes> {
        when {
            quotesResponse.message().toString()
                .contains("Timeout") -> return NetworkResults.Error("Timeout")
            quotesResponse.code() == 402 -> return NetworkResults.Error("Api Key Limited.")
            quotesResponse.body()!!.results.isNullOrEmpty() -> return NetworkResults.Error("No Data Found")
            quotesResponse.isSuccessful -> {
                val quotes = quotesResponse.body()
                CacheQuotes(quotes!!)
                return NetworkResults.Success(quotes!!)
            }
            else -> return NetworkResults.Error(quotesResponse.message())
        }
    }

    private fun CacheQuotes(quotes: Quotes) {
        val quoteEntity = QuoteEntity(quotes)
        insert(quoteEntity)
    }


    /**get categories from api and cache it**/
    suspend fun getCategories() {
        if (readCategories.value.isNullOrEmpty()|| readCategories.value!!.size<=0) {
            getcategoriesSafeCall()
        }
    }

    private suspend fun getcategoriesSafeCall() {
        if (hasInternetConnection()) {
            try {
                val categoriesResopse = quotesRepo.remot.getCategoriesQuotes()
                categoriesNetworkResults.value = handleCategoriesResults(categoriesResopse)
                Log.d("Tag_quote", "abdou: cats" + quotesNetworkResults.value.toString())
            } catch (ex: Exception) {
                Log.d("Tag_quote", "abdou: ex" + ex.toString())
            }
        }
    }

    private suspend fun handleCategoriesResults(categoriesResopse: Response<Categories>): NetworkResults<Categories>? {
        when {
            categoriesResopse.message().toString()
                .contains("Timeout") -> {
                Log.d("Tag_quote", "abdou: Timeout")
                return NetworkResults.Error("Timeout")
            }
            categoriesResopse.code() == 402 -> {
                Log.d("Tag_quote", "abdou: Api Key Limited.")
                return NetworkResults.Error("Api Key Limited.")
            }
            categoriesResopse.body()!!.results.isNullOrEmpty() -> {
                Log.d("Tag_quote", "abdou: No Data Found -Categories-")
                return NetworkResults.Error("No Data Found -Categories-")
            }
            categoriesResopse.isSuccessful -> {
                Log.d("Tag_quote", "abdou: cat" + categories!!)
                val categories = categoriesResopse.body()
                CacheCategories(categories!!)

                return NetworkResults.Success(categories!!)
            }
            else -> return NetworkResults.Error(categoriesResopse.message())
        }
    }

    private suspend fun CacheCategories(categories: Categories) {
        Log.d("Tag_quote", "abdou: cache quotes")
        val categoryEntity = QuotesCategoryEntity(0, categories)
        quotesRepo.local.insertCategoriesQuotes(categoryEntity)
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