package com.ramesh.sms.data.util

/**
 * Where the Persian (Jalali) calendar is used.
 *
 * [CHAT_ONLY] keeps the conversation list on the Gregorian calendar and only the chat screen
 * follows the user's Jalali preference; [EVERYWHERE] uses Jalali dates in the whole app.
 */
enum class JalaliScope {
    CHAT_ONLY,
    EVERYWHERE;

    companion object {
        fun fromName(name: String?): JalaliScope =
            entries.firstOrNull { it.name == name } ?: CHAT_ONLY
    }
}
