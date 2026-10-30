package com.ramesh.sms.ui.util

import android.content.Context
import android.content.res.Configuration
import com.ramesh.sms.data.util.AppPreferences
import java.util.Locale

/**
 * Applies the language chosen in Settings to a context.
 *
 * The locale is applied to the Activity's *base* context (see `MainActivity.attachBaseContext`)
 * instead of swapping `LocalContext` inside the composition. Swapping the composition context
 * handed Compose a context without the Activity theme, which made dialogs and sheets fragile
 * and crashed the app as soon as the UI switched to Persian.
 */
fun localizedContext(base: Context): Context {
    val tag = runCatching { AppPreferences(base).appLanguage.tag }.getOrNull() ?: return base
    val locale = Locale.forLanguageTag(tag)
    val configuration = Configuration(base.resources.configuration).apply {
        setLocale(locale)
        setLayoutDirection(locale)
    }
    return base.createConfigurationContext(configuration)
}
