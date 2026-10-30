package com.ramesh.sms.data.util

/**
 * Controls how much of an incoming message may be shown while the device is locked.
 * The full notification is always available once the device is unlocked.
 */
enum class LockScreenPrivacy(val label: String) {
    FULL("Sender and text"),
    SENDER_ONLY("Sender only"),
    HIDDEN("Nothing");

    companion object {
        fun fromName(raw: String?): LockScreenPrivacy =
            entries.firstOrNull { it.name == raw } ?: FULL
    }
}

/** UI language. [SYSTEM] follows the device locale. */
enum class AppLanguage(val label: String, val tag: String?) {
    SYSTEM("System default", null),
    ENGLISH("English", "en"),
    PERSIAN("فارسی", "fa");

    companion object {
        fun fromName(raw: String?): AppLanguage =
            entries.firstOrNull { it.name == raw } ?: SYSTEM
    }
}
