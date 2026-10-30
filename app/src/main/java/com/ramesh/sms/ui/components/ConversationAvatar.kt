package com.ramesh.sms.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ramesh.sms.theme.NonContactAvatarBrush
import com.ramesh.sms.theme.getAvatarGradient
import com.ramesh.sms.ui.util.senderDisplayName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Small in-memory cache so scrolling the inbox (and showing chat avatars) does
 * not re-decode the same contact photo over and over.
 */
private val avatarBitmapCache = LruCache<String, ImageBitmap>(24)

@Composable
fun ConversationAvatar(
    address: String,
    contactName: String?,
    size: Dp,
    modifier: Modifier = Modifier,
    isContact: Boolean = !contactName.isNullOrBlank(),
    photoUri: String? = null
) {
    val displayName = senderDisplayName(contactName, address)
    val initial = displayName.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
    val avatarBrush = if (isContact) getAvatarGradient(address) else NonContactAvatarBrush
    val showLetter = isContact || initial.any { it.isLetter() }

    val context = LocalContext.current
    var avatarBitmap by remember(photoUri) {
        mutableStateOf(photoUri?.let { avatarBitmapCache.get(it) })
    }

    LaunchedEffect(photoUri) {
        if (photoUri.isNullOrBlank()) {
            avatarBitmap = null
            return@LaunchedEffect
        }
        val cached = avatarBitmapCache.get(photoUri)
        if (cached != null) {
            avatarBitmap = cached
            return@LaunchedEffect
        }
        val decoded: ImageBitmap? = withContext(Dispatchers.IO) {
            try {
                decodeSampledAvatar(context, Uri.parse(photoUri))?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }
        if (decoded != null) {
            avatarBitmapCache.put(photoUri, decoded)
        }
        avatarBitmap = decoded
    }

    val currentBitmap = avatarBitmap
    if (currentBitmap != null) {
        Image(
            bitmap = currentBitmap,
            contentDescription = displayName,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(avatarBrush),
            contentAlignment = Alignment.Center
        ) {
            if (showLetter) {
                Text(
                    text = initial,
                    style = if (size >= 46.dp) {
                        MaterialTheme.typography.titleLarge
                    } else {
                        MaterialTheme.typography.titleMedium
                    },
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(size * 0.5f)
                )
            }
        }
    }
}

/** Decodes a contact photo down to avatar size instead of the full camera image. */
private fun decodeSampledAvatar(context: Context, uri: Uri, targetPx: Int = 192): android.graphics.Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, bounds)
    }
    var sampleSize = 1
    val width = bounds.outWidth
    if (width > 0) {
        while (width / (sampleSize * 2) >= targetPx) {
            sampleSize *= 2
        }
    }
    val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
    return context.contentResolver.openInputStream(uri)?.use { stream ->
        BitmapFactory.decodeStream(stream, null, options)
    }
}
