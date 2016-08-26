# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

### for butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

### for jpush
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

### for okhttp
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okio.** { *; }
-keep interface okio.** { *; }

## retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep interface retrofit.** { *; }

# umeng
-dontwarn com.umeng.**
-keep class com.umeng.** { *; }
-keep interface com.umeng.** { *; }

# joda
-dontwarn org.joda.**
-keep class org.jodag.** { *; }
-keep interface org.joda.** { *; }

# material
-dontwarn com.rey.**
-dontwarn u.**
-dontwarn uk.**
-keep class com.rey.** { *; }
-keep class u.** { *; }
-keep class uk.** { *; }
-keep interface com.rey.** { *; }
-keep interface u.** { *; }
-keep interface uk.** { *; }

# xiaomi
-keep class com.xiaomi.** { *; }
-dontwarn com.xiaomi.**

-keep class com.google.gson.** {*;}

-keep interface net.wendal.nutzbook.** { *; }