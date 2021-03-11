package com.example.wallsticker.ViewModel

import android.app.Application
import androidx.datastore.preferences.core.preferencesKey
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.wallsticker.Repository.DataStoreRepository
import com.example.wallsticker.Repository.ImagesRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    application: Application,
    private val repository: ImagesRepo
) : AndroidViewModel(application) {
    private object PreferenceKeys {
        val name = preferencesKey<String>("mode")
    }

    private val repositoryDataStore = DataStoreRepository(application)
    val readFromDataStore = repositoryDataStore.readFromDataStore.asLiveData()

    fun saveToDataStore(myName: String) = viewModelScope.launch(Dispatchers.IO) {
        repositoryDataStore.saveToDataStore(myName)
    }


}