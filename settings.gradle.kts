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
include(
    "lib",
    "userscripts",
    "userScripts:tutorial-final-test"
)
