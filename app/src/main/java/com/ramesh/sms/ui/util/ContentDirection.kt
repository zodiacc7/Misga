package com.ramesh.sms.ui.util

import androidx.compose.ui.text.TextStyle

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
