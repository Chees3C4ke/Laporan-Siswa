plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.pplgsmkn4.laporanmasyarakat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pplgsmkn4.laporanmasyarakat"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "keepRules/rules.keep"
            )
        }
    }

    buildFeatures {
        // Dibutuhkan karena kode memakai BuildConfig.APPLICATION_ID (FileProvider authority).
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Lifecycle components (ViewModel + LiveData)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.rxjava3)
    annotationProcessor(libs.androidx.room.compiler)

    // Glide (menampilkan gambar dari Base64/Bitmap)
    implementation(libs.glide)
    annotationProcessor(libs.glide.compiler)

    // RxJava 3 (eksekusi query Room di background thread)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)

    // Lokasi (FusedLocationProviderClient)
    implementation(libs.play.services.location)
}
