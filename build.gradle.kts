plugins {
    //trick: for the same plugin versions in all submodules
    id("binary-compatibility-validator").version(Versions.binaryCompatibilityValidator).apply(false)
    id("org.jetbrains.dokka").version(Versions.dokka).apply(false)
    id("com.android.library").version(Versions.android).apply(false)
    kotlin("android").version(Versions.kotlin).apply(false)
    kotlin("multiplatform").version(Versions.kotlin).apply(false)
}

buildscript {
    dependencies {
        classpath(Deps.Gradle.kotlin)
        classpath(Deps.Gradle.android)
    }
}
