pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://maven.minecraftforge.net") {
      content {
        includeGroupByRegex("net\\.minecraftforge.*")
      }
    }
  }

  resolutionStrategy {
    eachPlugin {
      if ("net.minecraftforge.gradle" == requested.id.id) {
        useModule("net.minecraftforge.gradle:ForgeGradle:${requested.version}")
      }
    }
  }
}

rootProject.name = "smarthud"
