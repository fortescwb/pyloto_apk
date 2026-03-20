object Versions {
    // Kotlin
    const val kotlin = "2.2.21"
    const val coroutines = "1.9.0"
    const val serialization = "1.7.3"

    // Android
    const val compileSdk = 35
    const val minSdk = 26
    const val targetSdk = 35

    // Compose
    // composeCompiler removed: bundled via kotlin.plugin.compose since Kotlin 2.x
    const val composeBom = "2024.12.01"
    const val activityCompose = "1.9.3"
    const val navigationCompose = "2.8.7"

    // AndroidX
    const val coreKtx = "1.15.0"
    const val lifecycle = "2.8.7"
    const val splashScreen = "1.0.1"
    const val dataStore = "1.1.1"
    const val paging = "3.3.2"
    const val workManager = "2.10.0"
    const val securityCrypto = "1.1.0-alpha06"

    // Hilt
    const val hilt = "2.57.2"
    const val hiltNavigation = "1.2.0"

    // Network
    const val retrofit = "2.11.0"
    const val okhttp = "4.12.0"

    // Room
    const val room = "2.7.0"

    // Google Services
    const val playServicesLocation = "21.3.0"
    const val playServicesMaps = "18.2.0"
    const val mapsCompose = "4.3.0"

    // Firebase
    const val firebaseBom = "33.7.0"

    // Image Loading
    const val coil = "2.7.0"

    // Testing
    const val junit = "4.13.2"
    const val mockitoKotlin = "5.4.0"
    const val turbine = "1.2.0"
    const val truth = "1.4.2"
    const val androidxTestJunit = "1.2.1"
    const val espresso = "3.6.1"

    // Debug
    const val leakCanary = "2.14"

    // AGP
    const val agp = "8.13.2"
    // KSP must match Kotlin exactly — verify at: https://github.com/google/ksp/releases
    const val ksp = "2.2.21-2.0.5"
    const val googleServices = "4.4.2"
    const val crashlytics = "3.0.2"
}
