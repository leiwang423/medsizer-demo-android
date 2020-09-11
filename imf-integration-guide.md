# Image Measure Framework Integration Guide

## Step 1 -> Download image measure aar files and load to your project, add to dependencies of your app.

[image-measure-app-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/7270367b430c69dd1f19680137e169b4a6014afc/image-measure-app-release/image-measure-app-release.aar

[circular_floating_action_menu_library-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/69bcafd3ce03351a9adb1382deefa8baa1202259/circular_floating_action_menu_library-release/circular_floating_action_menu_library-release.aar

[opencv401-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/69bcafd3ce03351a9adb1382deefa8baa1202259/opencv401-release/opencv401-release.aar

[photoview-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/69bcafd3ce03351a9adb1382deefa8baa1202259/photoview-release/photoview-release.aar

[scanlibrary-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/ee178d9b11b92fc51773f0fa771fd6e2aed6e50e/scanlibrary-release/scanlibrary-release.aar

[colorpicker-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/69bcafd3ce03351a9adb1382deefa8baa1202259/colorpicker-release/colorpicker-release.aar

[StickyHeaderPagerLibrary-release]

https://bitbucket.org/wanglei423/imf-demo-android/raw/69bcafd3ce03351a9adb1382deefa8baa1202259/StickyHeaderPagerLibrary-release/StickyHeaderPagerLibrary-release.aar


## Step 2 -> Add settings to your AndroidManifest.xml

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-feature android:name="android.hardware.camera" />
    <uses-feature android:name="android.hardware.microphone" />

    <application ...>
       <activity android:name="org.image.measure.gallery.activities.LFMainActivity" />
       <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
       </provider>
    </application>

    
## Step 3 ->  add 3rd party dependencies to your app/build.gradle

dependencies {

    implementation fileTree(dir: "libs", include: ["*.jar"])
    // IMF dependencies start
    implementation project(path: ':circular_floating_action_menu_library-release')
    implementation project(path: ':colorpicker-release')
    implementation project(path: ':opencv401-release')
    implementation project(path: ':scanlibrary-release')
    implementation project(path: ':image-measure-app-release')
    implementation project(path: ':StickyHeaderPagerLibrary-release')
    implementation project(path: ':photoview-release')
    
    //google and support
    implementation "com.android.support:appcompat-v7:28.0.0"
    implementation "com.android.support:design:28.0.0"
    implementation "com.android.support:support-vector-drawable:28.0.0"
    implementation "com.android.support:palette-v7:28.0.0"
    implementation "com.android.support:cardview-v7:28.0.0"
    implementation "com.android.support:customtabs:28.0.0"

    //metadataextractor
    implementation 'com.drewnoakes:metadata-extractor:2.9.1'
    //compressor
    implementation 'id.zelory:compressor:2.1.0'

    implementation 'com.github.bumptech.glide:glide:3.7.0'
    implementation 'com.yalantis:ucrop:1.5.0'
    implementation 'de.psdev.licensesdialog:licensesdialog:1.8.0'

    implementation 'com.koushikdutta.ion:ion:2.1.7'
    implementation 'org.jetbrains:annotations-java5:15.0'
    implementation 'com.android.support:multidex:1.0.1'
    implementation 'com.box:box-android-sdk:4.0.8'
    //leak canary
    implementation 'com.squareup.leakcanary:leakcanary-android-no-op:1.5.1'
    //icons
    implementation 'com.mikepenz:iconics-core:2.8.4@aar'
    implementation 'com.mikepenz:google-material-typeface:2.2.0.3.original@aar'
    implementation 'com.mikepenz:community-material-typeface:1.5.54.2@aar'
    implementation 'com.mikepenz:fontawesome-typeface:4.6.0.1@aar'
    implementation 'com.mikepenz:ionicons-typeface:+@aar'

    //ui
    implementation 'de.hdodenhof:circleimageview:2.0.0'
    implementation 'com.turingtechnologies.materialscrollbar:lib:10.0.3'
    implementation 'pl.droidsonroids.gif:android-gif-drawable:1.2.7'
    implementation 'com.github.shchurov:horizontalwheelview:0.9.5'
    implementation 'com.nostra13.universalimageloader:universal-image-loader:1.9.4'
}


## Step 4 -> support realm-android

### 4.1 in app/build.gradle, add a line:

    apply plugin: 'realm-android'

### 4.2 in project build.gradle, add:

allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath "io.realm:realm-gradle-plugin:3.4.0"
    }
}
 
## Step 5 -> Start image measure activity from your code

    Intent in = new Intent(this, org.image.measure.gallery.activities.LFMainActivity.class);
    startActivity(in);
    
## Step 6 -> See more reference from imf-demo-android

https://bitbucket.org/wanglei423/imf-demo-android/src/master/
