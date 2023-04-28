plugins {
    //trick: for the same plugin versions in all submodules
    id("binary-compatibility-validator").version(Versions.binaryCompatibilityValidator).apply(false)
    id("org.jetbrains.dokka").version(Versions.dokka).apply(false)
    id("com.android.library").version("7.4.2").apply(false)
    kotlin("android").version(Versions.kotlin).apply(false)
    kotlin("multiplatform").version(Versions.kotlin).apply(false)
}

buildscript {
    dependencies {
        classpath(Deps.Gradle.kotlin)
        classpath(Deps.Gradle.android)
    }
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

allprojects {
    group = OFM.group
    version = OFM.version

    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
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

            publications.withType<MavenPublication>().configureEach {
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

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
            val password = properties["signingPassword"]?.toString()

            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
        }

        tasks.withType<Sign>().configureEach {
            onlyIf { !OFM.snapshot }
        }
    }
}
