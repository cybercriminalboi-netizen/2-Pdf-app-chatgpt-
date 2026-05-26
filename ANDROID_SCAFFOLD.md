# OfflinePdfReader Android Scaffold

This canvas collects the Android project files already added so far.

## Project tree

```text
Pdf-viewer-app-chatgpt/
├── README.md
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── .github/
│   └── workflows/
│       └── android.yml
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/
            │       └── example/
            │           └── offlinepdfreader/
            │               └── MainActivity.kt
            └── res/
                ├── values/
                │   ├── strings.xml
                │   └── themes.xml
                └── xml/
                    ├── backup_rules.xml
                    └── data_extraction_rules.xml
```

## `README.md`

```md
# Offline PDF Reader

An offline Android PDF reader built with Kotlin.

Features planned for this repository:
- fast PDF rendering with page caching
- bookmarks
- notes
- last-page resume
- document search
- GitHub Actions workflow that builds an APK artifact
```

## `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OfflinePdfReader"
include(":app")
```

## `gradle.properties`

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

## `gradlew`

```sh
#!/bin/sh
echo "Gradle wrapper placeholder"
```

## `gradlew.bat`

```bat
@echo off
echo Gradle wrapper placeholder
```

## `.github/workflows/android.yml`

```yaml
name: Android APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Build debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/*.apk
```

## `app/build.gradle.kts`

```kotlin
plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.example.offlinepdfreader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.offlinepdfreader"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
}
```

## `app/proguard-rules.pro`

```pro
# Project specific ProGuard rules.
```

## `app/src/main/AndroidManifest.xml`

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:label="OfflinePdfReader" />
</manifest>
```

## `app/src/main/java/com/example/offlinepdfreader/MainActivity.kt`

```kotlin
package com.example.offlinepdfreader

class MainActivity
```

## `app/src/main/res/values/strings.xml`

```xml
<resources>
    <string name="app_name">OfflinePdfReader</string>
</resources>
```

## `app/src/main/res/values/themes.xml`

```xml
<resources>
    <style name="Theme.OfflinePdfReader" parent="Theme.Material3.DayNight.NoActionBar" />
</resources>
```

## `app/src/main/res/xml/backup_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<full-backup-content>
</full-backup-content>
```

## `app/src/main/res/xml/data_extraction_rules.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<data-extraction-rules>
</data-extraction-rules>
```

## Next files to add

* Android Gradle wrapper files
* root `build.gradle.kts`
* `local.properties` template for local use
* real `MainActivity` with Compose entry point
* theme/color resources
* PDF renderer classes
* page cache / fast rendering logic
* bookmarks/search storage
* proper manifest setup for launch activity
* any missing resources needed for a buildable APK
