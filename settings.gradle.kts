rootProject.name = "sonarlint-intellij"
include("its", "clion", "common")
pluginManagement {
  repositories {
    maven {
      url = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    gradlePluginPortal()
  }
}
