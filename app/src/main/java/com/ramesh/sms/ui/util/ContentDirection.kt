package com.ramesh.sms.ui.util

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection

/**
 * Kept for call-site compatibility while the UI learns about RTL layouts.
 *
 * Text used to resolve its paragraph direction from the *content* itself. With a Persian (RTL)
 * interface that made every LTR string — phone numbers, times, dates, SIM labels — start at the
 * left edge while Persian text started at the right, so a conversation row showed the name, the
 * SIM badge and the date jumbled on one side. Paragraph direction now follows the layout
 * direction, which is what every platform text view does; the bidi algorithm still reorders
 * Persian and Latin runs correctly inside the line.
 */
@Suppress("UNUSED_PARAMETER")
fun TextStyle.contentAware(): TextStyle = this

/**
 * Message bodies are the one place where the text itself decides the alignment: a Persian
 * message must hug the right edge of the bubble even when the interface is in English, and an
 * English message must hug the left edge even in a Persian interface. Only the paragraph of the
 * body is affected — never the layout of the surrounding row.
 */
fun TextStyle.forMessageBody(text: String): TextStyle {
    return if (isRtlParagraph(text)) {
        copy(textAlign = TextAlign.Right, textDirection = TextDirection.Rtl)
    } else if (text.isBlank()) {
        this
    } else {
        copy(textAlign = TextAlign.Left, textDirection = TextDirection.Ltr)
    }
}

/** True when the first strong character of the string belongs to an RTL script. */
fun isRtlParagraph(text: String): Boolean {
    for (ch in text) {
        when {
            ch in '\u0590'..'\u08FF' || ch in '\uFB1D'..'\uFDFF' || ch in '\uFE70'..'\uFEFF' -> {
                return true
            }
            ch in 'A'..'Z' || ch in 'a'..'z' || ch in '\u00C0'..'\u024F' -> return false
        }
    }
    return false
}
