import java.nio.file.Path
import java.nio.file.Paths

plugins {
    kotlin("multiplatform") version "1.5.20"
}

group = "com.github.durun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val jsInteropKlibFile: Path = Paths.get(buildDir.toString(), "klib", "${group}-${project.name}-jsInterop.klib")


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
                implementation(files(jsInteropKlibFile))
            }
        }
        val wasmTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

tasks {
    // interop
    val jsInterop by creating(Exec::class) {
        group = "develop"
        workingDir("./")

        val os = System.getProperty("os.name")
        val executable = when {
            os.startsWith("Windows") -> file(project.properties["konanHome"]!!).resolve("bin/kotlinc-native.bat")
            else -> file(project.properties["konanHome"]!!).resolve("bin/kotlinc-native")
        }
        executable(executable)

        val jsStubFile = projectDir.resolve("src/jsInterop/js/stub.js")
        val ktFile = projectDir.resolve("src/jsInterop/kotlin/com/github/durun/screeps/arena/jsinterop/lib.kt")

        inputs.file(ktFile)
        outputs.file(jsInteropKlibFile)

        args(
            "-include-binary", jsStubFile,
            "-produce", "library",
            "-o", jsInteropKlibFile,
            "-target", "wasm32",
            ktFile
        )
    }
    val compileKotlinWasm by getting {
        dependsOn(jsInterop)
    }

    // npm
    val npmInstall by creating(Exec::class) {
        group = "develop"
        workingDir(projectDir)
        executable("npm")
        args("install")
    }

    val clean by getting {
        doLast {
            delete("node_modules")
        }
    }
}