#ImmersionBar
 -keep class com.gyf.immersionbar.* {*;}
 -dontwarn com.gyf.immersionbar.**

#LitePal
-keep class org.litepal.** {*;}
-keep class * extends org.litepal.crud.DataSupport {*;}
-keep class * extends org.litepal.crud.LitePalSupport {*;}

#OkHttp
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** {*;}
-dontwarn okio.**
-keep class org.conscrypt.** {*;}

#Gson
-keep class com.google.gson.** {*;}
-keep class com.google.** {*;}
-keep class sun.misc.Unsafe {*;}
-keep class com.google.gson.stream.** {*;}
-keep class top.myhdg.bijou.gson.** {*;}
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable {*;}

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#SwitchButton
-keep class com.kyleduo.** {*;}

#FloatingActionButton
-keep class com.clans.** {*;}
-keep class com.github.clans.** {*;}

#ShadowLayout
-keep class com.lihang.** {*;}
-keep class com.github.lihangleo.** {*;}
-keep class com.github.lihangleo2.** {*;}