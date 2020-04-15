plugins {
  kotlin("jvm")
  kotlin("plugin.spring")
  id("org.ajoberstar.reckon")
  id("org.springframework.boot")
  id("com.github.node-gradle.node")
  id("io.spring.dependency-management")
}

val projectNpmVersion: String by project
val projectNodeVersion: String by project
val springBootAdminVersion: String by project
val javaVersion = JavaVersion.VERSION_11
java.sourceCompatibility = javaVersion

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
  maven(uri("https://repo.spring.io/milestone"))
}

extra["springBootAdminVersion"] = springBootAdminVersion

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-rsocket")
  implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("de.codecentric:spring-boot-admin-starter-client")
  implementation("de.codecentric:spring-boot-admin-starter-server")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  compileOnly("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")
  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
  imports {
    mavenBom("de.codecentric:spring-boot-admin-dependencies:${property("springBootAdminVersion")}")
  }
}

tasks {
  withType<Test> {
    useJUnitPlatform()
    testLogging {
      showCauses = true
      showExceptions = true
      showStackTraces = true
      showStandardStreams = true
    }
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs = listOf("-Xjsr305=strict")
      jvmTarget = javaVersion.toString()
    }
  }
}

reckon {
  scopeFromProp()
  // stageFromProp()
  snapshotFromProp()
}

tasks {
  register("version") {
    println(project.version.toString())
  }
  register("status") {
    doLast {
      val status = grgit.status()
      status?.let { s ->
        println("workspace is clean: ${s.isClean}")
        if (!s.isClean) {
          if (s.unstaged.allChanges.isNotEmpty()) {
            println("""all unstaged changes: ${s.unstaged.allChanges.joinToString(separator = "") { i -> "\n - $i" }}""")
          }
        }
      }
    }
  }
}

node {
  download = true
  version = projectNodeVersion
  npmVersion = projectNpmVersion
  workDir = file("$buildDir/.gradle/nodejs")
  npmWorkDir = file("$buildDir/.gradle/npm")
  nodeModulesDir = file("$projectDir/src/main/js")
}

tasks {
  clean {
    delete("$projectDir/src/main/js/.cache")
    delete("$projectDir/src/main/resources/static")
  }
  processResources {
    shouldRunAfter("clean", "npm_i", "npm_run_build")
    dependsOn("npm_i", "npm_run_build")
  }
}

defaultTasks("clean", "npm_i", "npm_run_build", "build")
