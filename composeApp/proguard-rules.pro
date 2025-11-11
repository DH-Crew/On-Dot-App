##############################################################
# Kakao SDK 관련 리플렉션 보호
##############################################################
-keep class com.kakao.sdk.** { *; }
-dontwarn com.kakao.sdk.**

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
# Ktor / Retrofit / OkHttp 관련 (KMP 프로젝트에서 자주 필요)
##############################################################
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**
