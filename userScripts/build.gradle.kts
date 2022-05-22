
plugins {
    kotlin("multiplatform") version "1.5.20"
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.20")
    }
}

subprojects { // SubProjects(:userScripts:*)
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    repositories {
        mavenCentral()
    }

    val deployDir = projectDir.resolve("screeps-arena-deploy")

    kotlin {
        wasm32("wasm") {
            binaries.executable()
        }

        sourceSets {
            val wasmMain by getting {
                dependencies {
                    implementation(kotlin("stdlib"))
                    implementation(kotlin("stdlib-common"))
                    implementation(kotlin("stdlib-js"))
                    implementation(project(":lib"))
                }
            }
        }
    }

    tasks {
        // Deploy
        val linkReleaseExecutableWasm by getting
        val copyBin by creating(Copy::class) {
            from(linkReleaseExecutableWasm.outputs)
            into(deployDir)
            eachFile { if (file.extension != "wasm") exclude() }
            rename { "wasm.bin" }
        }
        val copyJs by creating(Copy::class) {
            from(project(":lib").projectDir.resolve("src/wasmMain/resources"))
            into(deployDir)
            eachFile { if (file.extension !in listOf("js", "mjs")) exclude() }
        }
        val deploy by creating {
            group = "screeps"
            dependsOn(copyBin)
            dependsOn(copyJs)
        }
        val build by getting {
            dependsOn(deploy)
        }

        val clean by getting {
            doLast {
                delete(deployDir)
            }
        }
    }
}

// Dummy settings for :userScripts
kotlin {
    wasm32("wasm") {
        binaries.executable() // dummy
    }
}