# ProGuard configuration for FF Sensi Boost

# Keep all public classes
-keep public class * {
    public *;
}

# Keep Compose runtime
-keep class androidx.compose.** { *; }

# Keep Hilt classes
-keep class dagger.hilt.** { *; }
-keep class com.delex.ffsensiboost.di.** { *; }

# Keep ViewModels
-keep class com.delex.ffsensiboost.viewmodel.** { *; }

# Keep data models
-keep class com.delex.ffsensiboost.data.model.** { *; }

# Keep repository classes
-keep class com.delex.ffsensiboost.domain.repository.** { *; }

# Keep Kotlin specific
-keepclassmembers class ** {
    *** Companion;
}

-keep class kotlin.Metadata { *; }
