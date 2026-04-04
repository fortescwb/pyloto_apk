plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
}

import java.util.Properties

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

android {
    namespace = "com.pyloto.entregador"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pyloto.entregador"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Room schema export
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        // Maps API key: prioriza gradle/local.properties, depois env var.
        val mapsApiKey = (
            project.findProperty("MAPS_API_KEY") as String?
                ?: localProperties.getProperty("MAPS_API_KEY")
                ?: System.getenv("MAPS_API_KEY")
            )
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "placeholder-replace-with-real-key"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
    }

    signingConfigs {
        create("release") {
            storeFile = file("upload-keystore.jks")
            storePassword = localProperties.getProperty("UPLOAD_STORE_PASSWORD")
                ?: System.getenv("UPLOAD_STORE_PASSWORD") ?: ""
            keyAlias = "upload"
            keyPassword = localProperties.getProperty("UPLOAD_KEY_PASSWORD")
                ?: System.getenv("UPLOAD_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            // STAGING desativado temporariamente (planejado para retorno em 2-3 meses).
            // buildConfigField("String", "API_BASE_URL", project.property("API_BASE_URL_STAGING") as String)
            buildConfigField("String", "API_BASE_URL", project.property("API_BASE_URL_PRODUCTION") as String)
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_BASE_URL", project.property("API_BASE_URL_PRODUCTION") as String)
        }
    }

    // Flavors para escalar ambientes
    flavorDimensions += "environment"
    productFlavors {
        // STAGING desativado temporariamente (planejado para retorno em 2-3 meses).
        // create("staging") {
        //     dimension = "environment"
        //     applicationIdSuffix = ".staging"
        //     versionNameSuffix = "-staging"
        // }
        create("production") {
            dimension = "environment"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Kotlin (stdlib added automatically since Kotlin 1.8)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Android Core
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Material Components (XML theme support)
    implementation("com.google.android.material:material:1.12.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.7")

    // Hilt (DI)
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Database (Room)
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    implementation("androidx.room:room-paging:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")

    // Paging
    implementation("androidx.paging:paging-runtime-ktx:3.3.2")
    implementation("androidx.paging:paging-compose:3.3.2")

    // Location & Maps
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.maps.android:maps-compose:4.3.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // LeakCanary (debug only)
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("com.google.truth:truth:1.4.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57.2")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.57.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

// STAGING desativado temporariamente (planejado para retorno em 2-3 meses).
// O bloco abaixo foi mantido apenas para referencia de reativacao futura.
// tasks.configureEach {
//     if (name.startsWith("processStaging") && name.endsWith("GoogleServices")) {
//         enabled = false
//     }
// }
