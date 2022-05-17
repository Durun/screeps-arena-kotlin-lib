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
        workingDir("./")
        executable("${project.properties["konanHome"]}/bin/kotlinc-native")

        val jsStubFile = projectDir.resolve("src/jsInterop/js/stub.js")
        val ktFile = projectDir.resolve("src/jsInterop/kotlin/com/github/durun/screeps/arena/jsinterop/lib.kt")

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

    // Deploy
    val wasmMainBinaries by getting
    val copyBin by creating(Copy::class) {
        dependsOn(wasmMainBinaries)
        from(buildDir.resolve("bin/wasm"))
        into(buildDir.resolve("bin/wasm"))
        eachFile {
            if (file.extension != "wasm") exclude()
        }
        rename { "${it}.bin" }
    }
    val copyBinToDeployDir by creating(Copy::class) {
        dependsOn(copyBin)
        from(buildDir.resolve("bin/wasm/releaseExecutable/${project.name.replace('-', '_')}.wasm.bin"))
        into(projectDir.resolve("screeps-arena-deploy"))
        rename { "wasm.bin" }
    }
    val deploy by creating {
        group = "screeps"
        dependsOn(copyBinToDeployDir)
    }
    val build by getting {
        dependsOn(deploy)
    }
}