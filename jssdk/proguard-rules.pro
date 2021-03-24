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

-printconfiguration configuration.txt
-printusage usage.txt
-printseeds seeds.txt

-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
#-allowaccessmodification

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Preserve some attributes that may be required for reflection.
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.google.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.google.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick.
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# Preserve annotated Javascript interface methods.
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# The support libraries contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version. We know about them, and they are safe.
-dontnote android.support.**
-dontnote androidx.**
-dontwarn android.support.**
-dontwarn androidx.**

# This class is deprecated, but remains for backward compatibility.
-dontwarn android.util.FloatMath

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep
-keep class androidx.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}
-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}

# These classes are duplicated between android.jar and org.apache.http.legacy.jar.
-dontnote org.apache.http.**
-dontnote android.net.http.**

# These classes are duplicated between android.jar and core-lambda-stubs.jar.
-dontnote java.lang.invoke.**


#-----------------------------------
#          Tencent X5-WebView
#-----------------------------------
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**
-keep class com.tencent.smtt.** {*;}
-keep class com.tencent.tbs.** {*;}

#-----------------------------------
#          JSSDK-X5
#-----------------------------------
-keep class com.longrise.android.jssdk.Request{
    public <methods>;
}
-keepclassmembers class com.longrise.android.jssdk.sender.base.SenderAgent{
    public <methods>;
}

-keep class com.longrise.android.jssdk.Response{
    public static <methods>;
    public static final int RESULT_OK;
}

-keep class com.longrise.android.jssdk.sender.INativeListener{
    *;
}

-keep class com.longrise.android.jssdk.sender.IScriptListener{
    *;
}

-keep class com.longrise.android.jssdk.sender.IMethodListener{
    *;
}

-keep class com.longrise.android.jssdk.sender.IEventListener{
    *;
}

-keep class com.longrise.android.jssdk.sender.ResultCallback{
    abstract <methods>;
}

-keepclassmembers class com.longrise.android.jssdk.core.protocol.Result{
    public <methods>;
}

-keep class com.longrise.android.jssdk.receiver.base.EventName

-keep class com.longrise.android.jssdk.receiver.IReceiver{
    void onEvent();
}

-keep class com.longrise.android.jssdk.receiver.IParamsReceiver{
    void onEvent(**);
}

-keep class com.longrise.android.jssdk.receiver.IReturnReceiver{
    ** onEvent();
}

-keep class com.longrise.android.jssdk.receiver.IParamsReturnReceiver{
    ** onEvent(**);
}

-keepclassmembers class com.longrise.android.jssdk.receiver.base.ICallbackReceiver{
    protected final <methods>;
}

-keepclassmembers class com.longrise.android.jssdk.receiver.base.BaseReceiver{
    public final com.longrise.android.jssdk.receiver.base.ReceiverAgent alive();
}

-keepclassmembers class com.longrise.android.jssdk.receiver.base.ReceiverAgent{
    lifecycle(**);
}

-keep class com.longrise.android.jssdk.core.bridge.BaseBridge{
    getWebView();
    bindTarget(**);
    public final ** bridgeName();
}

-keepclassmembers class com.longrise.android.jssdk.core.bridge.BridgeLifecyle{
    onDestroy();
    getTarget();
    isFinished();
}

-keepclassmembers class com.longrise.android.jssdk.core.protocol.base.AbsDataProtocol{
    public <methods>;
}

-keep class com.longrise.android.jssdk.gson.JsonHelper{
    public <methods>;
}

#-----------------------------------
#          Google Gson
# https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
#-----------------------------------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
