pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "kotlin2js") {
                useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}
rootProject.name = "screeps-arena-kotlin"
include("lib", "userScripts")
// include user scripts
rootProject.projectDir.resolve("userScripts")
    .listFiles { it: File -> it.isDirectory && it.name != "build" }
    .forEach { include("userScripts:${it.name}") }