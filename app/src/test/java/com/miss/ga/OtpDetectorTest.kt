package com.miss.ga

import com.miss.ga.engine.OtpDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Detection rules for one-time codes inside SMS bodies (bank dynamic passwords, etc). */
class OtpDetectorTest {

    @Test
    fun dynamicBankPasswordWithColonIsDetected() {
        val body = "بانک ملت\nرمز پویا: 482913\nمهلت استفاده 5 دقیقه"
        assertEquals("482913", OtpDetector.findCode(body))
    }

    @Test
    fun verificationCodeInEnglishIsDetected() {
        val body = "Your verification code is 123456. Do not share it with anyone."
        assertEquals("123456", OtpDetector.findCode(body))
    }

    @Test
    fun persianDigitsAreNormalizedToLatin() {
        val body = "رمز پویا:۴۸۲۹۱۳ جهت ورود"
        assertEquals("482913", OtpDetector.findCode(body))
    }

    @Test
    fun arabicYehVariantOfRamzPuyaIsDetected() {
        // "رمز پويا" written with Arabic yeh (U+064A) by many bank senders.
        val body = "رمز پويا 68532 جهت ورود به سامانه"
        assertEquals("68532", OtpDetector.findCode(body))
    }

    @Test
    fun hyphenSplitNumberWithoutKeywordReturnsNull() {
        val body = "55-4432 expired...\n" // noisy: no code-looking keyword
        assertNull(OtpDetector.findCode(body))
    }

    @Test
    fun otpKeywordIsDetected() {
        val body = "Your OTP is 778899 for MISGA login."
        assertEquals("778899", OtpDetector.findCode(body))
    }

    @Test
    fun sloganMessageWithoutKeywordReturnsNull() {
        val body = "تخفیف ویژه آخر هفته، تا 40 درصد زیرقیمت خرید کنید"
        assertNull(OtpDetector.findCode(body))
    }

    @Test
    fun phoneNumbersAreNotFlaggedAsCodes() {
        val body = "پیام از 09121234567 ارسال شد"
        assertNull(OtpDetector.findCode(body))
    }

    @Test
    fun datesAndAmountsAreNotFlaggedAsCodes() {
        val body = "صورتحساب دوره 1404/09 مبلغ 2500000 تومان please review"
        assertNull(OtpDetector.findCode(body))
    }

    @Test
    fun moneyAmountNearKeywordIsRejected() {
        val body = "کد سفارش ثبت نشد، مبلغ 1200 تومان کسر شد"
        assertNull(OtpDetector.findCode(body))
    }

    @Test
    fun noDigitsReturnsNull() {
        assertNull(OtpDetector.findCode("سلام خوبی؟"))
    }

    @Test
    fun emptyBodyReturnsNull() {
        assertNull(OtpDetector.findCode(""))
    }
}
