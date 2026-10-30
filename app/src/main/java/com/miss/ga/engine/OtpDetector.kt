package com.miss.ga.engine

/**
 * Finds one-time codes (bank dynamic passwords, verification codes) inside SMS bodies.
 *
 * Heuristic: a contiguous 4-8 digit run must be near a code keyword (Persian or English)
 * and must not look like an amount. Persian/Arabic digits are normalized to Latin so the
 * copied value always pastes as numbers.
 */
object OtpDetector {

    /** Contiguous 4-8 digits, not part of a longer digit run (excludes phones, card numbers). */
    private val CODE_RUN = Regex("(?<![0-9])[0-9]{4,8}(?![0-9])")

    private val KEYWORDS = listOf(
        "رمز پویا" to 3,
        "رمز" to 3,
        "گذرگاه" to 3,
        "otp" to 3,
        "passcode" to 2,
        "verification" to 2,
        "verify" to 2,
        "کد" to 2,
        "code" to 1
    )

    /** Words that make the preceding number an amount, not a code. */
    private val MONEY_WORDS = listOf("تومان", "ریال", "درصد")

    private const val KEYWORD_LEAD_CHARS = 40
    private const val KEYWORD_TRAIL_CHARS = 24
    private const val MONEY_LOOKAHEAD_CHARS = 12

    fun findCode(body: String): String? {
        if (body.isBlank()) return null
        val text = normalize(body)
        val textLower = text.lowercase()
        return CODE_RUN.findAll(text)
            .mapNotNull { match -> score(text, textLower, match) }
            .maxWithOrNull(compareBy({ it.weight }, { -it.distance }))
            ?.code
    }

    private data class Candidate(val index: Int, val code: String, val weight: Int, val distance: Int)

    private fun score(text: String, textLower: String, match: MatchResult): Candidate? {
        val start = match.range.first
        if (hasMoneyWordAfter(text, match.range.last + 1)) return null
        var bestWeight = -1
        var bestDistance = Int.MAX_VALUE
        for ((keyword, weight) in KEYWORDS) {
            val haystack = if (keyword.any { it.code > 127 }) text else textLower
            var found = haystack.indexOf(keyword)
            while (found >= 0) {
                val distance = start - found
                if (distance >= -KEYWORD_TRAIL_CHARS && distance <= KEYWORD_LEAD_CHARS) {
                    val normalized = if (distance >= 0) distance else -distance
                    if (weight > bestWeight || (weight == bestWeight && normalized < bestDistance)) {
                        bestWeight = weight
                        bestDistance = normalized
                    }
                }
                found = haystack.indexOf(keyword, found + 1)
            }
        }
        if (bestWeight < 0) return null
        return Candidate(start, match.value, bestWeight, bestDistance)
    }

    private fun hasMoneyWordAfter(text: String, from: Int): Boolean {
        if (from >= text.length) return false
        val lookaheadEnd = minOf(text.length, from + MONEY_LOOKAHEAD_CHARS)
        val after = text.subSequence(from, lookaheadEnd)
        return MONEY_WORDS.any { after.contains(it) }
    }

    /** 1:1 char mapping of Persian/Arabic digits to Latin and Arabic letters to Persian forms. */
    internal fun normalize(text: String): String {
        val out = StringBuilder(text.length)
        for (ch in text) {
            when {
                ch in '٠'..'٩' -> out.append(ch - '٠')
                ch in '۰'..'۹' -> out.append(ch - '۰')
                ch == 'ي' -> out.append('ی')
                ch == 'ك' -> out.append('ک')
                else -> out.append(ch)
            }
        }
        return out.toString()
    }
}
