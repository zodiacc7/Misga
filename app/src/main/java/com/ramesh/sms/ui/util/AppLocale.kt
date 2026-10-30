package com.ramesh.sms.ui.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.ramesh.sms.data.util.AppLanguage
import java.util.Locale

/**
 * Overrides the resources used by [androidx.compose.ui.res.stringResource] so the UI can be
 * English or Persian independently of the device locale. [AppLanguage.SYSTEM] simply delegates
 * to the platform configuration, which already follows the system language.
 */
@Composable
fun ProvideAppLanguage(language: AppLanguage, content: @Composable () -> Unit) {
    val baseContext = LocalContext.current
    val tag = language.tag
    if (tag == null) {
        content()
        return
    }

    val configuration = remember(tag, baseContext) {
        Configuration(baseContext.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(tag))
            setLayoutDirection(Locale.forLanguageTag(tag))
        }
    }
    val localizedContext = remember(tag, baseContext) {
        baseContext.createConfigurationContext(configuration)
    }
    val direction = if (configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides configuration,
        LocalLayoutDirection provides direction,
        content = content
    )
}
