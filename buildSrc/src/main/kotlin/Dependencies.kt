object Dependencies {
    // Kotlin
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"

    // AndroidX Core
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle}"
    const val lifecycleCompose = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle}"
    const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"

    // Compose
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigationCompose}"

    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigation}"
    const val hiltWork = "androidx.hilt:hilt-work:${Versions.hiltNavigation}"
    const val hiltCompilerX = "androidx.hilt:hilt-compiler:${Versions.hiltNavigation}"

    // Network
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLogging = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"

    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomPaging = "androidx.room:room-paging:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"

    // Paging
    const val pagingRuntime = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
    const val pagingCompose = "androidx.paging:paging-compose:${Versions.paging}"

    // DataStore
    const val dataStorePrefs = "androidx.datastore:datastore-preferences:${Versions.dataStore}"

    // WorkManager
    const val workManager = "androidx.work:work-runtime-ktx:${Versions.workManager}"

    // Security
    const val securityCrypto = "androidx.security:security-crypto:${Versions.securityCrypto}"

    // Location & Maps
    const val playServicesLocation = "com.google.android.gms:play-services-location:${Versions.playServicesLocation}"
    const val playServicesMaps = "com.google.android.gms:play-services-maps:${Versions.playServicesMaps}"
    const val mapsCompose = "com.google.maps.android:maps-compose:${Versions.mapsCompose}"

    // Firebase
    const val firebaseBom = "com.google.firebase:firebase-bom:${Versions.firebaseBom}"

    // Image Loading
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"

    // Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val mockitoKotlin = "org.mockito.kotlin:mockito-kotlin:${Versions.mockitoKotlin}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    const val truth = "com.google.truth:truth:${Versions.truth}"
    const val androidxTestJunit = "androidx.test.ext:junit:${Versions.androidxTestJunit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val hiltTesting = "com.google.dagger:hilt-android-testing:${Versions.hilt}"

    // Debug
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
}
