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

#QRCode
-keep class com.XuDaojie.** {*;}
-keep class com.github.XuDaojie.** {*;}
-keep class com.google.zxing.** {*;}

#PopWindow
-keep class com.example.zhouwei.library.** {*;}
-keep class com.github.pinguo-zhouwei.** {*;}

#PhotoView
-keep class com.bm.photoview.** {*;}
-keep class com.bm.library.** {*;}

#CircleImageView
-keep class de.hdodenhof.circleimageview.** {*;}

#Aria
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keepclasseswithmembernames class * {
    @Download.* <methods>;
    @Upload.* <methods>;
    @DownloadGroup.* <methods>;
}