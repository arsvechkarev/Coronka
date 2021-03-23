-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

-keepclassmembers class androidx.lifecycle.Lifecycle$Event { *; }

-keepclassmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}

-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * extends base.RxViewModel {
    <init>();
}

-keep class com.google.android.gms.maps.MapView