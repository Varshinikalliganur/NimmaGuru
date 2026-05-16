package com.nimmaguru.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "nimma_guru_prefs")

@Singleton
class LocalePreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val key = stringPreferencesKey("app_locale")

    val localeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[key] ?: "en"
    }

    suspend fun setLocale(code: String) {
        context.dataStore.edit { it[key] = code }
    }
}
