import java.lang.module.ModuleFinder.compose

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.braguia2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.braguia2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.play.services.maps)
    val fragment_version = "1.6.2"

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.google.android.material:material:1.4.0")

    implementation(libs.androidx.gridlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    implementation("androidx.fragment:fragment:$fragment_version")


    //retrofit
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.navigation:navigation-fragment:2.3.2")
    implementation("androidx.navigation:navigation-ui:2.3.2")

    //picasso
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.jakewharton.picasso:picasso2-okhttp3-downloader:1.1.0")

    // Room components
    implementation("androidx.room:room-runtime:2.2.0")

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    annotationProcessor("androidx.room:room-compiler:2.2.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    // Outros

    implementation("androidx.constraintlayout:constraintlayout:2.1.0")

    //notificaçoes
    implementation("com.google.android.gms:play-services-location:18.0.0")

    implementation("androidx.appcompat:appcompat:1.3.0")

    //videos
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
    implementation ("pub.devrel:easypermissions:3.0.0")


}