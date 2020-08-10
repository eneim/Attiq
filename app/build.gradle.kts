/*
 * Copyright (c) 2020 Nam Nguyen, nam@ene.im
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("com.android.application")
  id("androidx.navigation.safeargs")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(30)
  buildToolsVersion("30.0.1")

  defaultConfig {
    applicationId("app.attiq")
    minSdkVersion(21)
    targetSdkVersion(30)
    versionCode(1)
    versionName("1.0")

    buildConfigField("String", "BASE_URL", "\"https://qiita.com\"")

    testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility(JavaVersion.VERSION_1_8)
    targetCompatibility(JavaVersion.VERSION_1_8)
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }

  buildFeatures {
    viewBinding = true
  }

  configurations {
    implementation.get().exclude(
      group = "org.jetbrains",
      module = "annotations"
    )
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.72")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.8")

  implementation("androidx.core:core-ktx:1.3.1")
  implementation("androidx.appcompat:appcompat:1.2.0")
  implementation("com.google.android.material:material:1.2.0")
  implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
  implementation("androidx.navigation:navigation-ui-ktx:2.3.0")
  implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
  implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
  implementation("androidx.navigation:navigation-ui-ktx:2.3.0")

  implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
  implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
  implementation("androidx.constraintlayout:constraintlayout:2.0.0-rc1")

  implementation("androidx.paging:paging-common-ktx:3.0.0-alpha04")
  implementation("androidx.paging:paging-runtime-ktx:3.0.0-alpha04")

  // implementation("androidx.room:room-common:2.3.0-alpha02")
  // kapt("androidx.room:room-compiler:2.3.0-alpha02")
  // implementation("androidx.room:room-coroutines:2.3.0-alpha02")
  // implementation("androidx.room:room-runtime:2.3.0-alpha02")
  // implementation("androidx.room:room-ktx:2.3.0-alpha02")

  implementation("com.squareup.okhttp3:okhttp:4.8.1")
  implementation("com.squareup.okhttp3:logging-interceptor:4.8.1")
  implementation("com.squareup.moshi:moshi:1.9.3")
  implementation("com.squareup.moshi:moshi-adapters:1.9.3")
  kapt("com.squareup.moshi:moshi-kotlin-codegen:1.9.3")
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

  implementation("io.coil-kt:coil:0.11.0")
  implementation("com.samskivert:jmustache:1.15")
  implementation("org.jsoup:jsoup:1.13.1")

  testImplementation("junit:junit:4.13")
  androidTestImplementation("androidx.test.ext:junit:1.1.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
