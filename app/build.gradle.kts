plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.sql"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sql"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "BASE_URL", "\"http://192.168.1.2/fitnessmedical/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.2/fitnessmedical/\"")
        }
        debug {
            buildConfigField("String", "BASE_URL", "\"http://192.168.1.2/fitnessmedical/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        dataBinding =true
    }
    packaging {
        resources {
            excludes += listOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
            excludes += ("res/raw/zxing_beep.ogg")
            // or pickFirsts += listOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
        }
    }


}
// strip out that ancient DataBinding AAR so AGP's built-in version wins
configurations.all {
    exclude(group = "androidx.databinding", module = "baseLibrary")
}


dependencies {
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("de.hdodenhof:circleimageview:3.1.0")

    // ML Kit Text Recognition
    implementation ("com.google.mlkit:text-recognition:16.0.1")
    // ML Kit Object Detection & Tracking
    implementation("com.google.mlkit:object-detection:17.0.2")


//retro
    // MPAndroidChart (line, bar, pie, radar)
    implementation ("com.github.PhilJay:MPAndroidChart:3.1.0")

// Circular progress gauges
    implementation ("com.github.lzyzsd:circleprogress:1.2.1")


    implementation ("com.google.guava:guava:31.1-android")

    // Konfetti “party popper” for view-based (XML) projects
    implementation ("nl.dionsegijn:konfetti-xml:2.0.5")
    implementation ("nl.dionsegijn:konfetti-core:2.0.5")
// remove: implementation "nl.dionsegijn:konfetti:1.2.6"


    implementation ("com.google.android.material:material:1.10.0")
    implementation( "androidx.viewpager2:viewpager2:1.0.0")

    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.baselibrary)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.play.services.maps)
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    // ZXing QR code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // or latest version
    implementation ("com.google.android.material:material:1.8.0")
    // or latest version
    implementation ("com.hbb20:ccp:2.5.4")  // use latest version
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.activity:activity:1.6.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation ("com.google.android.gms:play-services-safetynet:18.0.1")
    implementation ("com.google.android.gms:play-services-base:18.3.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.volley)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.annotation.jvm)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation ("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("androidx.fragment:fragment:1.4.1")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")


}