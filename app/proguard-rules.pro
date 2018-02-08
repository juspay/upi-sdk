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

-keep class org.apache.xml.security.** {*;}
-keep interface org.apache.xml.security.** {*;}

-keep class org.npci.** {*;}
-keep interface org.npci.** {*;}

-keep class com.axis.axismerchantsdk.** {*;}
-keep interface com.axis.axismerchantsdk.** {*;}

-dontwarn com.axis.axismerchantsdk.**

-keep class in.org.npci.** {*;}
-keep interface in.org.npci.** {*;}
-dontwarn in.org.npci.**

-keep class in.juspay.mystique.** {*;}
-keep interface in.juspay.mystique.** {*;}
