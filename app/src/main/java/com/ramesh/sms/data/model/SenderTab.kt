package com.ramesh.sms.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class SenderTab(
    val id: Long,
    val name: String,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val senderAddresses: Set<String> = emptySet(),
    /** Bit mask built from [MASK_SPAM_ONLY] and friends; 0 means "no extra filter". */
    val filterMask: Int = 0
) {
    val spamOnly: Boolean get() = filterMask and MASK_SPAM_ONLY != 0
    val nonSpamOnly: Boolean get() = filterMask and MASK_NON_SPAM_ONLY != 0
    val contactsOnly: Boolean get() = filterMask and MASK_CONTACTS_ONLY != 0
    val nonContactsOnly: Boolean get() = filterMask and MASK_NON_CONTACTS_ONLY != 0

    /** True when the tab narrows the inbox by spam state or contact state, not just senders. */
    val hasContentFilter: Boolean get() = filterMask != 0

    fun withFlag(mask: Int, enabled: Boolean): SenderTab =
        copy(filterMask = if (enabled) filterMask or mask else filterMask and mask.inv())

    companion object {
        const val MASK_SPAM_ONLY = 1
        const val MASK_NON_SPAM_ONLY = 2
        const val MASK_CONTACTS_ONLY = 4
        const val MASK_NON_CONTACTS_ONLY = 8
    }
}
