fun properties(key: String) = providers.gradleProperty(key)
fun environment(key: String) = providers.environmentVariable(key)

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("native.cocoapods")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

kotlin {
    jvmToolchain(8)
    android {
        publishAllLibraryVariants()
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    jvm()
    js(IR) {
        jvmToolchain(8)
    }

    // darwin
    if (ideaActive.not()) {
        listOf(
            // intel
            macosX64(),
            iosX64(),

            // apple silicon
            macosArm64(),
            iosSimulatorArm64()
        ).forEach {
            it.binaries.framework {
                baseName = "ofm"
            }
        }
    } else {
        if (isAppleSilicon) {
            // apple silicon
            macosArm64()
            iosSimulatorArm64()
        } else {
            // intel
            macosX64()
            iosX64()
        }
    }

    cocoapods {
        summary = "This project implements an extension to Github Flavoured Markdown to add #tags."
        homepage = "https://codeberg.org/uwutech/orto-flavoured-markdown"
        version = OFM.version
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Dependencies are managed with Gradle version catalog - read more: https://docs.gradle.org/current/userguide/platforms.html#sub:version-catalog
                implementation(Deps.Kotlin.common)
                implementation(Deps.Jetbrains.markdown)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Test.common)
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        val jvmMain by getting {
            dependencies {
                implementation(Deps.Kotlin.jvm)
            }
            dependsOn(commonMain)
        }
        val jvmTest by getting {
            dependencies {
                implementation(Deps.Test.jvm)
            }
            dependsOn(commonTest)
        }
        val androidMain by getting {
            dependencies {
            }
            dependsOn(commonMain)
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(Deps.Test.jvm)
            }
            dependsOn(commonTest)
        }
        val jsMain by getting {
            dependencies {
                implementation(Deps.Kotlin.js)
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(Deps.Test.js)
            }
        }

        // darwin
        if (ideaActive.not()) {
            // intel
            val macosX64Main by getting {
                dependsOn(nativeMain)
            }
            val macosX64Test by getting {
                dependsOn(nativeTest)
            }
            val iosMain by getting {
                dependsOn(nativeMain)
            }
            val iosTest by getting {
                dependsOn(nativeTest)
            }
            // apple silicon
            val macosArm64Main by getting {
                dependsOn(nativeMain)
            }
            val macosArm64Test by getting {
                dependsOn(nativeTest)
            }
            val iosSimulatorArm64Main by getting {
                dependsOn(nativeMain)
            }
            val iosSimulatorArm64Test by getting {
                dependsOn(nativeTest)
            }
        } else {
            if (isAppleSilicon) {
                // apple silicon
                val macosArm64Main by getting {
                    dependsOn(nativeMain)
                }
                val macosArm64Test by getting {
                    dependsOn(nativeTest)
                }
                val iosSimulatorArm64Main by getting {
                    dependsOn(nativeMain)
                }
                val iosSimulatorArm64Test by getting {
                    dependsOn(nativeTest)
                }
            } else {
                // intel
                val macosX64Main by getting {
                    dependsOn(nativeMain)
                }
                val macosX64Test by getting {
                    dependsOn(nativeTest)
                }
                val iosX64Main by getting {
                    dependsOn(nativeMain)
                }
                val iosX64Test by getting {
                    dependsOn(nativeTest)
                }
            }
        }
    }
}

android {
    namespace = "garden.orto.ofm"
    compileSdk = Versions.compileSdk
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


// Will be fixed in Kotlin 1.9
// See https://youtrack.jetbrains.com/issue/KT-55751
val myAttribute = Attribute.of("myOwnAttribute", String::class.java)

// replace releaseFrameworkIosFat by the name of the first configuration that conflicts
configurations.named("podDebugFrameworkIosFat").configure {
    attributes {
        // put a unique attribute
        attribute(myAttribute, "pod-debug-all")
    }
}

// replace releaseFrameworkIosFat by the name of the first configuration that conflicts
configurations.named("podDebugFrameworkIosX64").configure {
    attributes {
        // put a unique attribute
        attribute(myAttribute, "x64-pod-debug-all")
    }
}

// replace debugFrameworkIosFat by the name of the second configuration that conflicts
configurations.named("podReleaseFrameworkIosFat").configure {
    attributes {
        attribute(myAttribute, "ios-pod-release-all")
    }
}

// replace debugFrameworkIosFat by the name of the second configuration that conflicts
configurations.named("podReleaseFrameworkIosX64").configure {
    attributes {
        attribute(myAttribute, "x64-pod-release-all")
    }
}

// replace podDebugFrameworkOsxFat by the name of the second configuration that conflicts
configurations.named("podDebugFrameworkOsxFat").configure {
    attributes {
        attribute(myAttribute, "x64-pod-debug-fat")
    }
}

// replace podDebugFrameworkOsxFat by the name of the second configuration that conflicts
configurations.named("podDebugFrameworkMacosX64").configure {
    attributes {
        attribute(myAttribute, "x64-pod-debug-macos")
    }
}

// replace podReleaseFrameworkOsxFat by the name of the second configuration that conflicts
configurations.named("podReleaseFrameworkOsxFat").configure {
    attributes {
        attribute(myAttribute, "x64-pod-release-fat")
    }
}

// replace podReleaseFrameworkMacosX64 by the name of the second configuration that conflicts
configurations.named("podReleaseFrameworkMacosX64").configure {
    attributes {
        attribute(myAttribute, "x64-pod-release-macos")
    }
}