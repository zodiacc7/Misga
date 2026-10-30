# R8 rules for Ramesh (release minification enabled).

# --- kotlinx.serialization ---
# Official rules: https://github.com/Kotlin/kotlinx.serialization#android
-keepattributes *Annotation*, InnerClasses

-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.ramesh.sms.**$$serializer { *; }
-keepclassmembers class com.ramesh.sms.** {
    *** Companion;
}
-keepclasseswithmembers class com.ramesh.sms.** {
    kotlinx.serialization.KSerializer serializer(...);
}
