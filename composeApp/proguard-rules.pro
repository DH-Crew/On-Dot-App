##############################################################
# Kakao SDK 관련 리플렉션 보호
##############################################################
-keep class com.kakao.sdk.**.model.* { <fields>; }
-dontwarn com.kakao.sdk.**
-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.**

##############################################################
# Jetpack Compose, KMP, Coroutine 관련 기본 설정
##############################################################
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**

##############################################################
# Ktor / Retrofit / OkHttp 관련
##############################################################
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.common.** { *; }
-dontwarn okio.**
