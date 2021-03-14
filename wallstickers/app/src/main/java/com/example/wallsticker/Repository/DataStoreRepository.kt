package com.example.wallsticker.Repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


const val PREFERENCE: String = "My_preference"

class DataStoreRepository(context: Context) {

    private object PreferenceKeys {
        val theme = preferencesKey<String>("mode")
        val quote = preferencesKey<String>("quote")
    }

    val dataStore: DataStore<Preferences> = context.createDataStore(
        name = "settings"
    )


    suspend fun saveTheme(name: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.theme] = name
        }
    }

    suspend fun saveRandomQuote(quote: String) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.quote] = quote
        }
    }

    val readRandomQuote: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val myName = preference[PreferenceKeys.quote] ?: "none"
            myName
        }

    val readtheme: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val myName = preference[PreferenceKeys.theme] ?: "none"
            myName
        }


}