# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\ProgramFiles\androidsdk\android-sdk-windows/tools/proguard/proguard-android.txt
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

-dontshrink
-dontoptimize
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn android.support.**
-dontwarn com.markupartist.**
-dontwarn com.baidu.mobstat.**
-dontwarn android.support.v4.**
-dontwarn org.apache.**
-dontwarn com.qq.e.**
-dontwarn com.androidquery.**


-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep public class com.tencent.** {*;}
-keep public class [your_pkg].R$*{
    public static final int *;
}

-keep public class com.umeng.example.R$*{
    public static final int *;
}

-keep class com.qq.e.** {
   public protected *;
}

-keep class com.baidu.**{
  public protected *;
}

-keep class com.umeng.scrshot.**
##-keep class com.baidu.*.*
-keep class com.umeng.socialize.sensor.**
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class android.support.v4.** { *; }
-keep class android.support.v4.view.**{ *;}
-keep class android.support.v4.content.**{ *;}
-keep class com.baidu.mobstat.** { *;}
-keep class * extends com.baidu.mobstat.**
-keep class com.baidu.kirin.** {*; }
-keep class org.apache.** { *; }
-keep class org.apache.http.** { *; }

-keep class com.androidquery.** { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keepclasseswithmembers class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-repackageclass com.umeng.fb.example.proguard
-keepclasseswithmembernames class * {
    native <methods>;
}
