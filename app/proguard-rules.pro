# ========== Basic Android classes ==========
-keep class YOUR.PACKAGE.NAME.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep class * extends androidx.recyclerview.widget.RecyclerView$Adapter

# ========== Gson ==========
-keep class com.google.gson.** { *; }

-keepattributes Signature
-keepattributes *Annotation*

# ========== Retrofit + OkHttp ==========
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keepattributes Exceptions

# ========== Volley ==========
-keep class com.android.volley.** { *; }


# ========== Miscellaneous ==========
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
