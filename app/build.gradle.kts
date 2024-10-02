plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.adzenglish.adzenglish"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.adzenglish.adzenglish"
        minSdk = 24
        targetSdk = 34
        versionCode = 6
        versionName = "1.0.4"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.0")
    implementation("com.google.firebase:firebase-crashlytics:19.0.3")
    implementation("com.google.firebase:firebase-crashlytics-buildtools:3.0.2")
    testImplementation("junit:junit:4.13.2")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    implementation("androidx.fragment:fragment-ktx:1.8.2")
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")

    //Android Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-common:2.6.1")

    //dagger hilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")

    //json
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("androidx.legacy:legacy-support-core-ui:1.0.0")
    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // check permission
    implementation("com.karumi:dexter:6.2.3")

    implementation ("com.google.android.flexbox:flexbox:3.0.0")
    // in app
    implementation("com.android.billingclient:billing-ktx:7.0.0")
}