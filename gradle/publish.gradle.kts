apply(plugin = "maven-publish")
//apply(plugin = "signing")

fun Project.publishing(action: PublishingExtension.() -> Unit) =
    configure(action)

//fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
//    configure(configure)

val publications: PublicationContainer =
    (extensions.getByName("publishing") as PublishingExtension).publications

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

// read values from gradle.properties
val mavenGroup: String by project
val projectName: String by project
val pomDescription: String by project
val siteUrl: String by project
val pomLicenseName: String by project
val pomLicenseUrl: String by project
val pomLicenseDist: String by project
val pomDeveloperId: String by project
val pomDeveloperName: String by project
val pomOrganizationName: String by project
val pomOrganizationUrl: String by project

val sonatypeUser: String? by project
val sonatypePassword: String? by project
val sonatypePasswordEnv: String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv: String? = System.getenv()["SONATYPE_USERNAME"]

publishing {
    publications.all {
        group = OFM.group
        version = OFM.version
    }

    publications.withType<MavenPublication>().all {
        artifact(javadocJar.get())

        pom {
            name.set(projectName)
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

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/orto-app/orto-flavoured-markdown")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GH_USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GH_TOKEN")
            }
        }
    }
}

//signing {
//    sign(publications)
//}
