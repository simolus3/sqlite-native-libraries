import com.github.mizosoft.methanol.MediaType
import com.github.mizosoft.methanol.MultipartBodyPublisher
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.util.Base64
import java.util.Properties

plugins {
    id("com.android.library") version "8.7.3"
    id("maven-publish")
    id("signing")
}

buildscript {
    dependencies {
        classpath("com.github.mizosoft.methanol:methanol:1.8.2")
    }
}

group = "eu.simonbinder"
version = "3.49.2"
description = "Native sqlite3 library without JNI bindings"

repositories {
    mavenCentral()
    google()
}

val localRepo = uri("build/here/")

android {
    compileSdk = 35
    ndkVersion = "27.2.12479018"

    namespace = "eu.simonbinder.sqlite3_native_library"

    defaultConfig {
        minSdk = 19

        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }

        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("cpp/CMakeLists.txt")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val secretsFile = rootProject.file("local.properties")
val secretProperties = Properties()

if (secretsFile.exists()) {
    secretsFile.reader().use { secretProperties.load(it) }

    secretProperties.forEach { key, value ->
        if (key is String && key.startsWith("signing")) {
            ext[key] = value
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/simolus3/sqlite-native-libraries")

                developers {
                    developer {
                        id.set("simonbinder")
                        name.set("Simon Binder")
                        email.set("oss@simonbinder.eu")
                    }
                }

                licenses {
                    license {
                        name.set("Public Domain")
                        url.set("https://www.sqlite.org/copyright.html")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/simolus3/sqlite-native-libraries.git")
                    developerConnection.set("scm:git:ssh://github.com/simolus3/sqlite-native-libraries.git")
                    url.set("https://github.com/simolus3/sqlite-native-libraries/tree/master")
                }
            }
        }
    }

    repositories {
        maven {
            name = "here"
            url = localRepo
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}


abstract class PublishToCentral: DefaultTask() {
    @get:Input
    abstract val username: Property<String>

    @get:Input
    abstract val password: Property<String>

    @get:InputFile
    abstract val file: RegularFileProperty

    @TaskAction
    fun execute() {
        // Stolen from https://github.com/yananhub/flying-gradle-plugin/blob/main/maven-central-publish/src/main/java/tech/yanand/gradle/mavenpublish/CentralPortalService.java,
        // the plugin unfortunately doesn't work with the configuration cache.
        val client = HttpClient.newHttpClient()
        val body = MultipartBodyPublisher.newBuilder()
            .filePart("bundle", file.get().asFile.toPath(), MediaType.APPLICATION_OCTET_STREAM)
            .build()

        val token = Base64.getEncoder().encodeToString(buildString {
            append(username.get())
            append(':')
            append(password.get())
        }.encodeToByteArray())
        val request = HttpRequest.newBuilder(URI.create(PUBLISHING_URL))
            .header("Authorization", "Bearer $token")
            .header("Content-Type", "multipart/form-data; boundary=${body.boundary()}")
            .POST(body)
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        if (response.statusCode() != 201) {
            error("Expected 201 status code, got ${response.statusCode()}: ${response.body()}")
        }

        client.close()
    }

    private companion object {
        const val PUBLISHING_URL = "https://central.sonatype.com/api/v1/publisher/upload?publishingType=USER_MANAGED"
    }
}

val zipPublication by tasks.registering(Zip::class) {
    dependsOn(tasks.named("publishAllPublicationsToHereRepository"))
    from(localRepo)
}

val publishToMavenCentral by tasks.registering(PublishToCentral::class) {
    username.set(secretProperties.getProperty("sonatypeUser"))
    password.set(secretProperties.getProperty("sonatypePassword"))

    file.set(zipPublication.flatMap { it.archiveFile })
}

tasks.withType<AbstractPublishToMaven> {
    dependsOn("assembleRelease")
}
