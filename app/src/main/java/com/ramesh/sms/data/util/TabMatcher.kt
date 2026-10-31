package com.ramesh.sms.data.util

import com.ramesh.sms.data.model.ConversationThread
import com.ramesh.sms.data.model.SenderTab

/**
 * Single source of truth for "does this conversation belong to this tab".
 *
 * The inbox list, the per-tab badge counter and the selection actions all have to agree,
 * otherwise a tab shows rows while its badge stays at zero.
 */
fun threadMatchesTab(thread: ConversationThread, tab: SenderTab): Boolean {
    val hasSenders = tab.senderAddresses.isNotEmpty()
    if (!hasSenders && !tab.hasContentFilter) return false

    if (hasSenders) {
        val canonical = PhoneNumberKeys.canonical(thread.address)
        val inSenders = canonical in tab.senderAddresses ||
            PhoneNumberKeys.keys(thread.address).any { it in tab.senderAddresses }
        if (!inSenders) return false
    }

    // Spam state: the two flags cancel each other out (no restriction).
    val wantsSpam = tab.spamOnly && !tab.nonSpamOnly
    val wantsNonSpam = tab.nonSpamOnly && !tab.spamOnly
    val isSpamThread = thread.isLastReceivedSpam
    if (wantsSpam && !isSpamThread) return false
    if (wantsNonSpam && isSpamThread) return false

    // Contact state: same rule.
    val wantsContacts = tab.contactsOnly && !tab.nonContactsOnly
    val wantsNonContacts = tab.nonContactsOnly && !tab.contactsOnly
    if (wantsContacts && !thread.isContact) return false
    if (wantsNonContacts && thread.isContact) return false

    return true
}

/** Threads the inbox would actually show for [tab]; a null tab means the "All" pseudo tab. */
fun visibleThreadsForTab(
    threads: List<ConversationThread>,
    tab: SenderTab?
): List<ConversationThread> {
    return threads.filter { thread ->
        if (thread.messageCount <= 0) return@filter false
        if (tab == null) {
            !thread.spamOnly
        } else {
            (tab.spamOnly || !thread.spamOnly) && threadMatchesTab(thread, tab)
        }
    }
}
