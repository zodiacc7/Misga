package com.ramesh.sms.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.ramesh.sms.theme.Appearance
import com.ramesh.sms.theme.backdropColorOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Paints the chat wallpaper: a Textra-style solid/gradient tone, an optional
 * photo picked from the gallery, plus a scrim so bubbles stay readable.
 */
@Composable
fun ChatBackdropBackground(appearance: Appearance, modifier: Modifier = Modifier) {
    val baseColor = appearance.backdrop.backdropColorOrNull() ?: MaterialTheme.colorScheme.background
    val wallpaper = rememberWallpaperImage(appearance.wallpaperUri)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        if (wallpaper != null) {
            Image(
                bitmap = wallpaper,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.35f))
            )
        }
    }
}

@Composable
private fun rememberWallpaperImage(uri: String?): ImageBitmap? {
    if (uri.isNullOrBlank()) return null
    val context = LocalContext.current
    val image by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                decodeSampledBitmap(context, Uri.parse(uri))?.asImageBitmap()
            }.getOrNull()
        }
    }
    return image
}

/** Decodes a downsampled copy so full-resolution photos cannot exhaust memory. */
private fun decodeSampledBitmap(context: Context, uri: Uri, maxWidth: Int = 1440): Bitmap? {
    return try {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, bounds)
        }
        var sampleSize = 1
        val width = bounds.outWidth
        if (width > 0) {
            while (width / (sampleSize * 2) >= maxWidth) {
                sampleSize *= 2
            }
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    } catch (e: Exception) {
        null
    }
}
