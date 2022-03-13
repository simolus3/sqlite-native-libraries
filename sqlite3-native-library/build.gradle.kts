import java.util.Properties
import de.undercouch.gradle.tasks.download.Download

plugins {
    id("com.android.library") version "7.0.1"
    id("de.undercouch.download") version "4.1.2"
    id("maven-publish")
    id("signing")
}

val sqliteMinor = 38
val sqlitePatch = 1

group = "eu.simonbinder"
version = "3.$sqliteMinor.$sqlitePatch"
description = "Native sqlite3 library without JNI bindings"

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"
    ndkVersion = "20.1.5948944"

    defaultConfig {
        minSdk = 16

        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
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

    libraryVariants.forEach {
        it.generateBuildConfigProvider.configure { enabled = false }
    }
}

val androidSourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
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
        register("maven", MavenPublication::class) {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            artifact("$buildDir/outputs/aar/${project.name}-release.aar")
            artifact(androidSourcesJar)

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
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = secretProperties.getProperty("ossrhUsername")
                password = secretProperties.getProperty("ossrhPassword")
            }
        }

        maven {
            name = "here"
            url = uri("build/here/")
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.named("publish").configure {
    dependsOn("assembleRelease", androidSourcesJar)
}

val downloadSqlite by tasks.registering(Download::class) {
    val downloadCode = "3" + sqliteMinor.toString().padStart(2, '0') +
            sqlitePatch.toString().padStart(2, '0') +
            "00"

    src("https://www.sqlite.org/2022/sqlite-amalgamation-$downloadCode.zip")
    dest("cpp/sqlite.zip")
}

val installSqlite by tasks.registering(Copy::class) {
    dependsOn(downloadSqlite)

    from(zipTree(downloadSqlite.get().dest).matching {
        include("*/sqlite3.*")
        eachFile { path = name }
    })

    into("cpp/")
}

tasks.named("preBuild") {
    dependsOn(installSqlite)
}