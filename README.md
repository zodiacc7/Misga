# رامش (Ramesh)

**SMS without the spam — and a look you actually like.**

[English](#english) · [فارسی](#farsi)

[![License: GPL-3.0](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)
[![Android 8.0+](https://img.shields.io/badge/Android-8.0%2B-3ddc84.svg)](https://developer.android.com/about/versions/oreo)

---

<a id="english"></a>

## English

Ramesh is a full-featured Android SMS app built for Iran's messaging reality, with a
Textra-style look you can customise down to the last detail.

Iranian SMS is mostly noise: ads, promotions and scams, mixed with the occasional
verification code you cannot afford to miss. Ramesh ships with ready-made regex filters
that silence the noise, while an allowlist makes sure OTPs and banking messages always
get through.

> Ramesh is a fork of [Misga](https://github.com/mirarr-app/Misga) (GPL-3.0) and keeps the
> `com.miss.ga` package id so existing installs keep their messages, filters and settings.

### Highlights

- **Allowlist-first filtering.** Allowlist rules run before the blocklist, so a promotional
  message that also carries a real login code still reaches you.
- **Built-in Iranian presets.** Spam, ads, phishing and scam patterns are included; keep them,
  disable them, edit them, or delete everything and start from your own list.
- **Filter Studio.** Add regex or plain-text rules, test a pattern against sample text, and
  export/import the whole rule set as JSON.
- **Textra-style appearance.** Auto/Light/Dark/AMOLED-Black themes, accent colors, bubble
  styles with a tail, separate sent/received bubble colors, message fonts and sizes, chat
  wallpapers and contact photos.
- **Dual SIM done properly.** Pick the SIM per message, see which SIM received a message, and
  filter the inbox by SIM.
- **Peaceful notifications.** Filtered messages arrive silently, spam produces no notification
  at all, and notifications support inline reply, mark-as-read, optional delete and a one-tap
  **Copy code** button for verification SMS.
- **No network permission.** Ramesh has no `INTERNET` permission: filtering, contacts and
  media decoding all happen on-device.
- **RTL-aware.** Persian and Arabic message bodies render with the correct direction and
  punctuation even in the English UI, and dates can be shown in the Shamsi calendar.

### Appearance & settings

Open the **⋮** menu in the inbox and choose **Settings** — or use the quick **Customize
inbox** sheet. Everything applies live.

| Section | What you can change |
| --- | --- |
| **Theme** | Auto, Light, Dark, or Black (true black for AMOLED). Accent follows the system wallpaper color on Android 12+, or pick one of nine hand-tuned accents. |
| **Bubbles** | Textra, Rounded, Pill or Compact shape; bubble tail on/off; independent colors for sent and received bubbles; four text sizes; Vazirmatn, Plus Jakarta or the system font. |
| **Chat background** | Six flat tones, or any photo from your gallery (downsampled and scrimmed so bubbles stay readable). |
| **Conversation** | Contact photos next to received messages, Today/Yesterday dividers, Enter-to-send, SMS character counter, tap a timestamp to switch Gregorian/Shamsi. |
| **Inbox** | Conversation tabs, SIM filter, contact photos, contacts-only mode. |
| **Notifications** | Delete action, and the copy-code button for one-time codes. |
| **Filtering** | “No spam for contacts”, plus a shortcut to Filter Studio. |
| **Reset** | Restore the default look without touching messages or filters. |

### How filtering works

Every incoming message is checked in this order:

1. **Allowlist** — always delivered with a normal notification. Built-in presets cover login
   codes, banking OTPs (`رمز پویا` / `رمز دوم`) and other verification messages.
2. **Blocklist** — matching messages are marked as spam: no sound, no badge, and collapsed
   into a slim pill inside the conversation (tap to reveal, or mark “not spam”).
3. **Sender settings** — inside a chat you can mute or block one specific sender, choose its
   default action and preferred SIM, and even give that sender its own rules.

Because the allowlist wins, filtering never swallows an OTP.

Ramesh must be your **default SMS app** to receive messages in real time — Android only
delivers incoming SMS to the default messaging app.

### Custom filters

Filter Studio is where rules live:

- add allowlist or blocklist rules (regex or substring)
- enable, disable, edit or delete any rule, including built-ins
- test a pattern against sample text before saving
- export and import the full list as JSON

A prompt that works well with an AI agent:

> Write a Java regex for Ramesh that matches Iranian SMS of this kind: `[paste an example]`.
> It must not match OTPs or bank verification messages.

Then add the pattern in Filter Studio as a blocklist rule.

### Install

1. Download the signed `ramesh-<version>-universal.apk` from
   [GitHub Releases](https://github.com/zodiacc7/Misga/releases).
2. Install it and open Ramesh.
3. Grant SMS, contacts and notification permissions.
4. Accept the prompt to become the default SMS app.

Android 8.0 (API 26) or newer is required. MMS is not supported.

> The F-Droid listing for `com.miss.ga` belongs to the upstream Misga project and may not
> include the features described here. Ramesh itself is distributed through GitHub Releases.

### Build from source

Requirements: **JDK 21** and the **Android SDK (API 36)**.

```bash
./gradlew assembleDebug      # app/build/outputs/apk/debug/app-debug.apk
./gradlew test               # unit tests (filter engine, OTP detector, dates, prefs)
./gradlew assembleRelease    # needs the signing environment variables below
```

Release signing is read from the environment:
`SIGNING_STORE_FILE`, `SIGNING_STORE_PASSWORD`, `SIGNING_KEY_ALIAS`, `SIGNING_KEY_PASSWORD`.

### Project structure

```
app/src/main/java/com/miss/ga/
├── data/        SQLite rules DB, SMS repository, SIM telephony, preferences
├── engine/      filter engine, incoming-SMS policy, OTP detector, notifications
├── receiver/    SMS/MMS receivers, notification actions, sent/delivery reports
├── theme/       Material 3 theme, appearance model + store, bubble shapes
└── ui/          inbox, chat, compose, Filter Studio, settings, components
```

Internal storage identifiers (`com.miss.ga`, `misga_*` preferences, `misga_filters.db`,
notification channels) are intentionally unchanged so updating from Misga keeps your data.

### Privacy

- No internet permission, no analytics, no ads, no third-party SDKs.
- Messages and rules stay on the device.
- Contact photos and wallpapers are read only when you pick them, through the system picker.

### Contributing

Issues and pull requests: <https://github.com/zodiacc7/Misga>.

### Credits & license

Ramesh is based on [Misga](https://github.com/mirarr-app/Misga) by Mirarr and is inspired by
[Textra SMS](https://textra.app). Fonts: [Vazirmatn](https://github.com/rastikerdar/vazirmatn)
and [Plus Jakarta Sans](https://github.com/tokotype/PlusJakartaSans) (both SIL OFL).
Licensed under the **GNU GPL v3** — see [LICENSE](LICENSE).

---

<a id="farsi"></a>

<div dir="rtl" lang="fa">

## فارسی

**رامش** یک برنامهٔ کامل پیامک برای اندروید است که برای واقعیت پیامک در ایران ساخته شده و
ظاهری به سبک Textra دارد که تا آخرین جزئیات قابل شخصی‌سازی است.

پیامک در ایران بیشتر سر و صداست: تبلیغ، تخفیف و کلاهبرداری، لابه‌لای چند کد تأیید که نباید
از دست بروند. رامش از ابتدا فیلترهای آمادهٔ regex دارد تا این سر و صدا را خفه کند، و یک
لیست سفید تضمین می‌کند کدهای تأیید و پیام‌های بانکی همیشه برسند.

> رامش ادامهٔ پروژهٔ [میسگا](https://github.com/mirarr-app/Misga) با پروانهٔ GPL-3.0 است و
> شناسهٔ بستهٔ `com.miss.ga` را نگه داشته تا با به‌روزرسانی، پیام‌ها و فیلترهای فعلی از دست نروند.

### ویژگی‌ها

- **فیلتر با اولویت لیست سفید.** قوانین لیست سفید پیش از لیست سیاه بررسی می‌شوند؛ پس پیام
  تبلیغاتی‌ای که یک کد ورود واقعی هم دارد، از دست نمی‌رود.
- **پیش‌فرض‌های آمادهٔ ایرانی.** الگوهای اسپم، تبلیغات، فیشینگ و کلاهبرداری از قبل هست؛
  می‌توانید نگه دارید، خاموش کنید، ویرایش کنید یا همه را پاک کنید و از صفر بسازید.
- **Filter Studio.** افزودن قانون regex یا متن ساده، آزمایش الگو روی متن نمونه، و
  خروجی/ورودی گرفتن کل قوانین با JSON.
- **ظاهر به سبک Textra.** تم خودکار/روشن/تیره/مشکی AMOLED، رنگ تأکیدی، شکل حباب با دنباله،
  رنگ جداگانه برای حباب ارسالی و دریافتی، فونت و اندازهٔ متن، پس‌زمینهٔ گفتگو و عکس مخاطب.
- **دو سیم‌کارت، درست و کامل.** انتخاب سیم‌کارت برای هر پیام، نمایش سیم‌کارت دریافت‌کننده و
  فیلتر کردن صندوق بر اساس سیم‌کارت.
- **اعلان‌های آرام.** پیام‌های فیلترشده بی‌صدا می‌رسند، اسپم‌ها هیچ اعلانی نمی‌سازند و اعلان‌ها
  پاسخ سریع، خوانده‌شدن، حذف اختیاری و دکمهٔ **کپی کد** برای پیامک‌های تأیید دارند.
- **بدون دسترسی اینترنت.** رامش اجازهٔ `INTERNET` ندارد؛ فیلتر، مخاطبین و پردازش تصویر
  کاملاً روی گوشی انجام می‌شود.
- **پشتیبانی از راست‌به‌چپ.** متن فارسی و عربی با جهت و نشانه‌گذاری درست نمایش داده می‌شود و
  تاریخ‌ها را می‌توان شمسی دید.

### ظاهر و تنظیمات

از منوی **⋮** در صندوق ورودی گزینهٔ **Settings** را بزنید، یا از پنجرهٔ سریع **Customize inbox**
استفاده کنید. همهٔ تغییرها همان لحظه اعمال می‌شوند.

| بخش | چه چیزی قابل تغییر است |
| --- | --- |
| **تم** | خودکار، روشن، تیره یا مشکی واقعی (AMOLED). رنگ تأکیدی از رنگ سیستم اندروید ۱۲ به بالا، یا یکی از نُه رنگ دست‌چین‌شده. |
| **حباب‌ها** | شکل Textra، گرد، قرصی یا جمع‌وجور؛ دنبالهٔ حباب روشن/خاموش؛ رنگ مستقل حباب ارسالی و دریافتی؛ چهار اندازهٔ متن؛ فونت Vazirmatn، Plus Jakarta یا فونت سیستم. |
| **پس‌زمینهٔ گفتگو** | شش رنگ ساده یا هر عکسی از گالری (با کاهش اندازه و لایهٔ محو برای خوانایی حباب‌ها). |
| **گفتگو** | عکس مخاطب کنار پیام‌های دریافتی، جداکنندهٔ امروز/دیروز، ارسال با Enter، شمارندهٔ کاراکتر و لمس تاریخ برای تغییر تقویم شمسی/میلادی. |
| **صندوق ورودی** | تب‌های گفتگو، فیلتر سیم‌کارت، عکس مخاطب و حالت «فقط مخاطبین». |
| **اعلان‌ها** | دکمهٔ حذف و دکمهٔ کپی کد برای کدهای یک‌بارمصرف. |
| **فیلتر** | «بدون اسپم برای مخاطبین» و میان‌بر Filter Studio. |
| **بازنشانی** | برگشت به ظاهر پیش‌فرض، بدون دست‌زدن به پیام‌ها و فیلترها. |

### فیلتر چطور کار می‌کند

هر پیام ورودی به این ترتیب بررسی می‌شود:

1. **لیست سفید** — همیشه با اعلان عادی می‌رسد. پیش‌فرض‌ها کد ورود، رمز پویا/رمز دوم بانکی و
   دیگر پیام‌های تأیید را پوشش می‌دهند.
2. **لیست سیاه** — پیام‌های منطبق، اسپم می‌شوند: بدون صدا، بدون نشان اعلان و جمع‌شده در یک
   نوار باریک داخل گفتگو (برای دیدن یا «اسپم نیست» لمس کنید).
3. **تنظیمات فرستنده** — از داخل گفتگو می‌توانید یک فرستنده را بی‌صدا یا مسدود کنید، رفتار
   پیش‌فرض و سیم‌کارت دلخواهش را تعیین کنید و حتی برایش قانون اختصاصی بسازید.

چون لیست سفید اولویت دارد، هیچ کد تأییدی قربانی فیلتر نمی‌شود.

برای دریافت پیام‌ها در لحظه، رامش باید **برنامهٔ پیامک پیش‌فرض** باشد؛ اندروید پیامک ورودی را
فقط به برنامهٔ پیش‌فرض می‌دهد.

### فیلترهای سفارشی

قوانین در Filter Studio نگهداری می‌شوند:

- افزودن قانون لیست سفید یا لیست سیاه (regex یا زیررشته)
- روشن، خاموش، ویرایش یا حذف هر قانون، حتی پیش‌فرض‌ها
- آزمایش الگو روی متن نمونه پیش از ذخیره
- خروجی و ورودی گرفتن کل لیست به‌صورت JSON

نمونهٔ درخواست از ایجنت هوش مصنوعی:

> یک regex جاوا برای رامش بنویس که این نوع پیامک ایرانی را بگیرد: `[یک نمونه بچسبانید]`.
> نباید با کد تأیید یا پیام بانکی منطبق شود.

بعد الگو را در Filter Studio به‌عنوان قانون لیست سیاه اضافه کنید.

### نصب

۱. فایل امضاشدهٔ `ramesh-<version>-universal.apk` را از
   [GitHub Releases](https://github.com/zodiacc7/Misga/releases) دانلود کنید.
۲. نصب کنید و رامش را باز کنید.
۳. دسترسی پیامک، مخاطبین و اعلان را بدهید.
۴. درخواست «برنامهٔ پیامک پیش‌فرض» را بپذیرید.

اندروید ۸٫۰ (API ۲۶) یا جدیدتر لازم است. پیام‌های چندرسانه‌ای (MMS) پشتیبانی نمی‌شوند.

> صفحهٔ F-Droid برای بستهٔ `com.miss.ga` متعلق به پروژهٔ بالادستی میسگا است و ممکن است
> ویژگی‌های این نسخه را نداشته باشد. خود رامش از طریق GitHub Releases منتشر می‌شود.

### ساخت از روی سورس

نیازمندی‌ها: **JDK 21** و **Android SDK (API 36)**.

```bash
./gradlew assembleDebug      # app/build/outputs/apk/debug/app-debug.apk
./gradlew test               # تست‌های واحد موتور فیلتر، تشخیص OTP، تاریخ و تنظیمات
./gradlew assembleRelease    # نیازمند متغیرهای امضای زیر
```

امضای نسخهٔ انتشار از محیط خوانده می‌شود:
`SIGNING_STORE_FILE`، `SIGNING_STORE_PASSWORD`، `SIGNING_KEY_ALIAS`، `SIGNING_KEY_PASSWORD`.

### ساختار پروژه

```
app/src/main/java/com/miss/ga/
├── data/        پایگاه‌دادهٔ قوانین، مخزن پیام‌ها، سیم‌کارت، تنظیمات
├── engine/      موتور فیلتر، سیاست پیام ورودی، تشخیص OTP، اعلان‌ها
├── receiver/    گیرنده‌های پیامک/MMS، اکشن‌های اعلان، گزارش ارسال و تحویل
├── theme/       تم Material 3، مدل و مخزن ظاهر، شکل حباب‌ها
└── ui/          صندوق ورودی، گفتگو، نوشتن پیام، Filter Studio، تنظیمات، کامپوننت‌ها
```

شناسه‌های داخلی ذخیره‌سازی (`com.miss.ga`، تنظیمات `misga_*`، پایگاه‌دادهٔ
`misga_filters.db` و کانال‌های اعلان) عمداً تغییر نکرده‌اند تا به‌روزرسانی از میسگا داده‌های
شما را حفظ کند.

### حریم خصوصی

- بدون دسترسی اینترنت، بدون تحلیل‌گر، بدون تبلیغات و بدون کتابخانهٔ شخص ثالث.
- پیام‌ها و قوانین فقط روی دستگاه می‌مانند.
- عکس مخاطب و پس‌زمینه فقط وقتی خودتان انتخاب کنید و از طریق انتخاب‌گر سیستم خوانده می‌شوند.

### مشارکت

گزارش خطا و درخواست تغییر: <https://github.com/zodiacc7/Misga>

### اعتبار و پروانه

رامش بر پایهٔ [میسگا](https://github.com/mirarr-app/Misga) ساختهٔ Mirarr است و از
[Textra SMS](https://textra.app) الهام گرفته. فونت‌ها:
[Vazirmatn](https://github.com/rastikerdar/vazirmatn) و
[Plus Jakarta Sans](https://github.com/tokotype/PlusJakartaSans) (هر دو با پروانهٔ SIL OFL).
پروانه: **GNU GPL v3** — فایل [LICENSE](LICENSE).

</div>
