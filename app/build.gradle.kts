plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.delex.ffsensiboost"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.delex.ffsensiboost"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
}
