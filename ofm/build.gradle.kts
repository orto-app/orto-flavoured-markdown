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

// read values from gradle.properties
val artifactId: String by project
val pomDescription: String by project
val siteUrl: String by project
val pomLicenseName: String by project
val pomLicenseUrl: String by project
val pomLicenseDist: String by project
val pomDeveloperId: String by project
val pomDeveloperName: String by project
val pomOrganizationName: String by project
val pomOrganizationUrl: String by project

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

group = OFM.group
version = OFM.version

kotlin {
    jvmToolchain(11)
    android {
        publishAllLibraryVariants()
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    jvm()
    linuxX64()
    mingwX64()
    js(IR) {
        jvmToolchain(11)
    }

    // darwin
    if (ideaActive.not()) {
        listOf(
            // intel
            macosX64(),
            iosX64(),

            // apple silicon
            macosArm64(),
            iosArm64(),
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
        val jsMain by getting {
            dependencies {
                implementation(Deps.Kotlin.js)
            }
            dependsOn(commonMain)
        }
        val jsTest by getting {
            dependencies {
                implementation(Deps.Test.js)
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
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val linuxX64Test by getting {
            dependsOn(nativeTest)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val mingwX64Test by getting {
            dependsOn(nativeTest)
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
            val iosX64Main by getting {
                dependsOn(nativeMain)
            }
            val iosX64Test by getting {
                dependsOn(nativeTest)
            }
            // apple silicon
            val macosArm64Main by getting {
                dependsOn(nativeMain)
            }
            val macosArm64Test by getting {
                dependsOn(nativeTest)
            }
            val iosArm64Main by getting {
                dependsOn(nativeMain)
            }
            val iosArm64Test by getting {
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
                val iosArm64Main by getting {
                    dependsOn(nativeMain)
                }
                val iosArm64Test by getting {
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


publishing {
    repositories {
        maven {
            url = uri(
                if (!OFM.snapshot) {
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                } else {
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                }
            )
            credentials {
                username = properties["sonatypeUsername"].toString()
                password = properties["sonatypePassword"].toString()
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(emptyJavadocJar.get())

        pom {
            name.set(artifactId)
            description.set(pomDescription)
            url.set(siteUrl)
            licenses {
                license {
                    name.set(pomLicenseName)
                    url.set(pomLicenseUrl)
                    distribution.set(pomLicenseDist)
                }
            }
            developers {
                developer {
                    id.set(pomDeveloperId)
                    name.set(pomDeveloperName)
                    organization.set(pomOrganizationName)
                    organizationUrl.set(pomOrganizationUrl)
                }
            }
            scm {
                url.set(siteUrl)
            }
        }
    }
}

signing {
    val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
    val password = properties["signingPassword"]?.toString()

    useInMemoryPgpKeys(key, password)
    sign(publishing.publications)
}

afterEvaluate {
    tasks.withType<Sign>().configureEach {
        onlyIf { !OFM.snapshot }
    }
    // Will be fixed in Kotlin 1.9
    // See https://youtrack.jetbrains.com/issue/KT-46466

    // Workaround from https://github.com/copper-leaf/gradle-convention-plugins/blob/main/src/main/kotlin/copper-leaf-publish.gradle.kts
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishKotlinMultiplatformPublicationToMavenRepository") { dependsOn("signMingwX64Publication") }


    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }
    tasks.getByName("publishAndroidDebugPublicationToMavenRepository") { dependsOn("signMingwX64Publication") }


    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }
    tasks.getByName("publishAndroidReleasePublicationToMavenRepository") { dependsOn("signMingwX64Publication") }


    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }
    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishJsPublicationToMavenRepository") { dependsOn("signMingwX64Publication") }


    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }
    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishJvmPublicationToMavenRepository") { dependsOn("signMingwX64Publication") }


    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishLinuxX64PublicationToMavenRepository") { dependsOn("signMingwX64Publication") }

    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signJsPublication") }
    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signJvmPublication") }
    tasks.getByName("publishMingwX64PublicationToMavenRepository") { dependsOn("signLinuxX64Publication") }


    if (os.isMacOsX) {
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signIosSimulatorArm64Publication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signIosX64Publication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signJvmPublication") }
        tasks.getByName("publishIosArm64PublicationToMavenRepository") { dependsOn("signJsPublication") }

        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signIosArm64Publication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signIosX64Publication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signJsPublication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signJvmPublication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
        tasks.getByName("publishIosSimulatorArm64PublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }

        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signIosSimulatorArm64Publication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signIosArm64Publication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signKotlinMultiplatformPublication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signAndroidReleasePublication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signAndroidDebugPublication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signJvmPublication") }
        tasks.getByName("publishIosX64PublicationToMavenRepository") { dependsOn("signJsPublication") }
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

if (ideaActive.not()) {
    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("debugFrameworkIosX64").configure {
        attributes {
            attribute(myAttribute, "x64-debug-all")
        }
    }
    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("releaseFrameworkIosX64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "x64-release-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("podReleaseFrameworkIosX64").configure {
        attributes {
            attribute(myAttribute, "x64-pod-release-all")
        }
    }
    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("podDebugFrameworkIosX64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "x64-pod-debug-all")
        }
    }


    // apple silicon
    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("releaseFrameworkIosArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "arm64-release-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("podReleaseFrameworkIosArm64").configure {
        attributes {
            attribute(myAttribute, "arm64-pod-release-all")
        }
    }

    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("podDebugFrameworkIosArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "arm64-pod-debug-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("debugFrameworkIosArm64").configure {
        attributes {
            attribute(myAttribute, "arm64-debug-all")
        }
    }

    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("releaseFrameworkIosSimulatorArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "simulator-release-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("podReleaseFrameworkIosSimulatorArm64").configure {
        attributes {
            attribute(myAttribute, "simulator-pod-release-all")
        }
    }

    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("podDebugFrameworkIosSimulatorArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "simulator-pod-debug-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("debugFrameworkIosSimulatorArm64").configure {
        attributes {
            attribute(myAttribute, "simulator-debug-all")
        }
    }

    // replace releaseFrameworkIosFat by the name of the first configuration that conflicts
    configurations.named("podDebugFrameworkMacosArm64").configure {
        attributes {
            // put a unique attribute
            attribute(myAttribute, "macos-pod-debug-all")
        }
    }

    // replace debugFrameworkIosFat by the name of the second configuration that conflicts
    configurations.named("debugFrameworkMacosArm64").configure {
        attributes {
            attribute(myAttribute, "macos-debug-all")
        }
    }

    // replace releaseFrameworkMacosArm64 by the name of the second configuration that conflicts
    configurations.named("releaseFrameworkMacosArm64").configure {
        attributes {
            attribute(myAttribute, "macos-release-all")
        }
    }

    // replace podReleaseFrameworkMacosArm64 by the name of the second configuration that conflicts
    configurations.named("podReleaseFrameworkMacosArm64").configure {
        attributes {
            attribute(myAttribute, "macos-pod-release-all")
        }
    }

} else {
    if (isAppleSilicon) {
        // apple silicon

        // TODO: Fill in as above
    } else {
        // intel

        // TODO: Fill in as above
    }
}
