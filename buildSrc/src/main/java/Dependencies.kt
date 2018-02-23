object Versions {
    const val kotlin = "1.2.21"
    const val build_tools = "3.2.0-alpha03"
    const val android_junit5 = "1.0.31-SNAPSHOT"
    const val junit_platform = "1.0.3"
    const val jacoco = "0.1.2"
    const val bintray = "1.7.3"

    const val kotlin_coroutines = "0.20"
    const val support_lib = "27.0.2"
    const val constraint_layout = "1.1.0-beta5"
    const val arch = "1.1.0"
    const val junit = "4.12"
    const val kluent = "1.33"
    const val robolectric = "3.6.1"
    const val mockito = "2.13.0"
    const val detekt = "1.0.0.RC6-3"
    const val dokka = "0.9.16-eap-3"

    const val compileSdkVersion = 27
    const val minSdkVersion = 16
    const val targetSdkVersion = 27

    private const val major = 1
    private const val minor = 0
    private const val patch = 0

    val versionCode: Int = (major * 10000) + (minor * 100) + patch

    val versionString: String = "$major.$minor.$patch"
}

object Libs {
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"
    const val kotlin_coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlin_coroutines}"
    const val kotlin_coroutines_android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlin_coroutines}"
    const val appcompat = "com.android.support:appcompat-v7:${Versions.support_lib}"
    const val constraint_layout = "com.android.support.constraint:constraint-layout:${Versions.constraint_layout}"
    const val lifecycle_runtime = "android.arch.lifecycle:runtime:${Versions.arch}"
    const val lifecycle_compiler = "android.arch.lifecycle:compiler:${Versions.arch}"
    const val lifecycle_extensions = "android.arch.lifecycle:extensions:${Versions.arch}"
    const val arch_core_testing = "android.arch.core:core-testing:${Versions.arch}"
    const val junit = "junit:junit:${Versions.junit}"
    const val kluent = "org.amshove.kluent:kluent-android:${Versions.kluent}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
}


object Publishing {
    const val bintrayDryRun = false
}
