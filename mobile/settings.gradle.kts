pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OTPstudent"
include(":app")
include(":core")

// --- Auto-module inclusion and dependency injection ---
val mobileDir = file(".")
val ignore = setOf("app", "core", "build", "gradle", ".gradle", ".idea")
val featureModules = mutableListOf<String>()

mobileDir.listFiles()
    ?.filter { it.isDirectory && it.name !in ignore }
    ?.forEach { dir ->
        val kts = File(dir, "build.gradle.kts")
        val groovy = File(dir, "build.gradle")
        if (kts.exists() || groovy.exists()) {
            val name = dir.name
            val path = ":$name"
            include(path)
            featureModules += path
        }
    }

// Expose discovered feature modules to be used in build.gradle files
extra["featureModules"] = featureModules
