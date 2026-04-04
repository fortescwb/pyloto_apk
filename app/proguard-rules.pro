# ──────────────────────────────────────────────
# Pyloto Parceiro — ProGuard / R8 rules
# ──────────────────────────────────────────────

# Manter DTOs que transitam por Gson (serialização/deserialização)
-keepclassmembers class com.pyloto.entregador.data.**.dto.** { *; }
-keepclassmembers class com.pyloto.entregador.domain.model.** { *; }
-keepclassmembers class com.pyloto.entregador.core.network.model.** { *; }

# Gson: manter anotações @SerializedName
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Hilt / Dagger
-dontwarn dagger.**
-keep class dagger.** { *; }

# Compose — já tratado pelo AGP, mas garantir navigation args
-keep class * extends androidx.navigation.NavArgs { *; }

# Google Maps
-keep class com.google.android.gms.maps.** { *; }

# Crashlytics
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
