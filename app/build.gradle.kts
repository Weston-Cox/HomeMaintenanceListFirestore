plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "edu.uark.westonc.homemaintenancelist"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.uark.westonc.homemaintenancelist"
        minSdk = 27
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.converter.gson)
    implementation(libs.retrofit)

    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))

    // Add the dependency for the Firebase SDK for Google Analytics
    implementation(libs.firebase.analytics)

    // For example, add the dependencies for Firebase Authentication and Cloud Firestore

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)

    // Dependencies for working with Architecture components
    // You'll probably have to update the version numbers in build.gradle (Project)


    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.play.services.auth)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.room.ktx) // Replace with the appropriate version

    // Lifecycle components
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    // Kotlin components
    implementation(libs.kotlin.stdlib.jdk7)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)




    // UI
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.junit.v115)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}