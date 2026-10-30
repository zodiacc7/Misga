package com.ramesh.sms.receiver

import android.app.RemoteInput
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ramesh.sms.R
import com.ramesh.sms.data.repository.SmsRepository
import com.ramesh.sms.data.util.PhoneNumberKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Handles direct-reply and mark-as-read actions on message notifications.
 */
class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val threadId = intent.getLongExtra(NotificationActions.EXTRA_THREAD_ID, -1L)
        if (threadId <= 0 && intent.action != NotificationActions.ACTION_REPLY) return

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        scope.launch {
            try {
                // Copying a code must not silently mark the thread as read.
                var skipThreadRead = false
                when (intent.action) {
                    NotificationActions.ACTION_REPLY -> {
                        val address = intent.getStringExtra(NotificationActions.EXTRA_ADDRESS).orEmpty()
                        val replyBody = extractReplyText(intent)
                        if (address.isBlank() || replyBody == null) {
                            Log.w(TAG, "Direct reply ignored: missing ${PhoneNumberKeys.redact(address)} or body")
                            return@launch
                        }
                        SmsRepository(context).sendSms(address, replyBody)
                    }
                    NotificationActions.ACTION_MARK_READ -> Unit
                    NotificationActions.ACTION_COPY_OTP -> {
                        val code = intent.getStringExtra(NotificationActions.EXTRA_OTP_CODE)
                        if (code.isNullOrBlank()) {
                            Log.w(TAG, "Copy-code action missing code")
                            return@launch
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("OTP", code))
                        android.widget.Toast.makeText(
                            context.applicationContext,
                            context.getString(R.string.toast_code_copied, code),
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        skipThreadRead = true
                    }
                    NotificationActions.ACTION_DELETE -> {
                        val messageId = intent.getLongExtra(NotificationActions.EXTRA_MESSAGE_ID, -1L)
                        if (messageId > 0) {
                            SmsRepository(context).deleteMessage(messageId)
                        } else if (threadId > 0) {
                            SmsRepository(context).deleteThread(threadId)
                        }
                        if (threadId > 0) {
                            NotificationActions.cancel(context, threadId)
                        }
                        return@launch
                    }
                    else -> return@launch
                }
                if (threadId > 0) {
                    if (!skipThreadRead) {
                        SmsRepository(context).markThreadRead(threadId)
                    }
                    NotificationActions.cancel(context, threadId)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Notification action failed", e)
            } finally {
                pendingResult.finish()
                scope.cancel()
            }
        }
    }

    private fun extractReplyText(intent: Intent): String? {
        val bundle = RemoteInput.getResultsFromIntent(intent) ?: return null
        return bundle.getCharSequence(NotificationActions.KEY_REPLY_TEXT)
            ?.toString()
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    companion object {
        private const val TAG = "NotifActionReceiver"
    }
}

/** Holds action constants plus helpers shared with [com.ramesh.sms.engine.NotificationHelper]. */
object NotificationActions {
    const val ACTION_REPLY = "com.ramesh.sms.ACTION_NOTIFICATION_REPLY"
    const val ACTION_MARK_READ = "com.ramesh.sms.ACTION_NOTIFICATION_MARK_READ"
    const val ACTION_DELETE = "com.ramesh.sms.ACTION_NOTIFICATION_DELETE"
    const val ACTION_COPY_OTP = "com.ramesh.sms.ACTION_NOTIFICATION_COPY_OTP"
    const val KEY_REPLY_TEXT = "com.ramesh.sms.KEY_NOTIFICATION_REPLY_TEXT"
    const val EXTRA_THREAD_ID = "EXTRA_THREAD_ID"
    const val EXTRA_ADDRESS = "EXTRA_ADDRESS"
    const val EXTRA_MESSAGE_ID = "EXTRA_MESSAGE_ID"
    const val EXTRA_OTP_CODE = "EXTRA_OTP_CODE"

    fun cancel(context: Context, threadId: Long) {
        val notificationId = (threadId xor (threadId ushr 32)).toInt()
        context.getSystemService(android.app.NotificationManager::class.java)
            ?.cancel(notificationId)
    }
}
