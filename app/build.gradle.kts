plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rahul.udpplayerapp"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    sourceSets {

    }

//    sourceSets {
//        main {
//            jni.srcDirs = []
//            // Prevent gradle from building native code with ndk; we have our own Makefile for it.
//            jniLibs.srcDir 'jni/libs' // Where generated .so files are placed
//            assets.srcDirs = ['src/main/assets', '../assets/']
//        }
//    }


    packaging {
        pickFirst("**/libc++_shared.so")
    }

    defaultConfig {
        applicationId = "com.rahul.udpplayerapp"
        minSdk = 24
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

//    implementation("androidx.media3:media3-exoplayer:1.2.1")
//    implementation("androidx.media3:media3-exoplayer-dash:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
//    implementation("androidx.media3:media3-exoplayer-smoothstreaming:1.2.1")

//    implementation("de.mrmaffen:vlc-android-sdk:3.5.4")
//    implementation("de.mrmaffen:libvlc-android:3.5.4")
//    implementation("de.mrmaffen:libvlc-android:2.1.12@aar")
//    implementation("org.opencv:OpenCV-Android:3.1.0")
    implementation("org.videolan.android:libvlc-all:4.0.0-eap15")
    implementation("com.squareup.okhttp3:okhttp:3.9.0")
//    implementation("com.arthenica:ffmpeg-kit-full:6.0-2")
    implementation("com.writingminds:FFmpegAndroid:0.3.2")
}