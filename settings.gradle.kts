pluginManagement {
  val kotlinVersion: String by extra
  val reckonVersion: String by extra
  val springBootVersion: String by extra
  val nodeGradleVersion: String by extra
  val dependencyManagementVersion: String by extra
  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.ajoberstar.reckon") version reckonVersion
    id("org.springframework.boot") version springBootVersion
    id("com.github.node-gradle.node") version nodeGradleVersion
    id("io.spring.dependency-management") version dependencyManagementVersion
  }
  repositories {
    maven(uri("https://repo.spring.io/milestone"))
    gradlePluginPortal()
  }
  /*resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "org.springframework.boot") {
        useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
      }
    }
  }*/
}

val name: String by extra
rootProject.name = name
