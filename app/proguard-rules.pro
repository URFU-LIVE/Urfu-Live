# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Сохранить ViewModel классы
-keep class com.example.urfulive.ui.main.PostViewModel { *; }
-keep class com.example.urfulive.ui.search.SearchViewModel { *; }

# Сохранить методы лайков
-keepclassmembers class * {
    public void likeAndDislike(*);
    public void updatePostEverywhere(*);
}

# Сохранить корутины
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Сохранить Flow и StateFlow
-keep class kotlinx.coroutines.flow.** { *; }