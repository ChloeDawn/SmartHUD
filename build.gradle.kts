import net.minecraftforge.gradle.common.tasks.SignJar
import java.time.Instant

plugins {
  id("net.minecraftforge.gradle") version "5.1.26"
  id("net.nemerosa.versioning") version "2.15.1"
  id("org.gradle.signing")
}

group = "dev.sapphic"
version = "2.1.0"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = sourceCompatibility

  withSourcesJar()
}

minecraft {
  mappings("stable", "39-1.12")
  runs {
    create("client") {
      mods.create("smarthud").source(sourceSets["main"])
      property("forge.logging.console.level", "debug")
      property("forge.logging.markers", "SCAN")
    }
  }
}

repositories {
  maven("https://cursemaven.com") {
    content {
      includeGroup("curse.maven")
    }
  }
}

dependencies {
  minecraft("net.minecraftforge:forge:1.12.2-14.23.5.2860")
  implementation(fg.deobf("curse.maven:baubles-227083:2518667")) // 1.12-1.5.2
  implementation(fg.deobf("curse.maven:quark-243121:2924091")) // r1.6-179
  implementation(fg.deobf("curse.maven:autoreglib-250363:2746011")) // 1.3-32
  runtimeOnly(fg.deobf("curse.maven:quark-oddities-301051:2604798")) // 1.12.2
  implementation("org.checkerframework:checker-qual:3.21.0")
}

tasks {
  compileJava {
    with(options) {
      isDeprecation = true
      encoding = "UTF-8"
      isFork = true
      compilerArgs.addAll(
        listOf(
          "-Xlint:all",
          "-Xlint:-processing",
          "-parameters" // JEP 118
        )
      )
    }
  }

  processResources {
    filesMatching("/mcmod.info") {
      expand("version" to project.version)
    }
  }

  withType<Jar> {
    from("/LICENSE")

    manifest.attributes(
      "Build-Timestamp" to Instant.now(),
      "Build-Revision" to versioning.info.commit,
      "Build-Jvm" to "${
        System.getProperty("java.version")
      } (${
        System.getProperty("java.vendor")
      } ${
        System.getProperty("java.vm.version")
      })",
      "Built-By" to GradleVersion.current(),
      "Implementation-Title" to project.name,
      "Implementation-Version" to project.version,
      "Implementation-Vendor" to project.group,
      "Specification-Title" to "ForgeMod",
      "Specification-Version" to "1.1.0",
      "Specification-Vendor" to project.group,
      "Sealed" to true
    )
  }

  val sourcesJar by getting(Jar::class)

  if (project.hasProperty("signing.mods.keyalias")) {
    val keyalias = project.property("signing.mods.keyalias") as String
    val keystore = project.property("signing.mods.keystore") as String
    val password = project.property("signing.mods.password") as String

    val signJar by creating(SignJar::class) {
      dependsOn(reobf)

      alias.set(keyalias)
      keyStore.set(keystore)
      keyPass.set(password)
      storePass.set(password)
      inputFile.set(jar.get().archiveFile)
      outputFile.set(inputFile)

      doLast {
        signing.sign(outputFile.get().asFile)
      }
    }

    val signSourcesJar by creating(SignJar::class) {
      dependsOn(sourcesJar)

      alias.set(keyalias)
      keyStore.set(keystore)
      keyPass.set(password)
      storePass.set(password)
      inputFile.set(sourcesJar.archiveFile)
      outputFile.set(inputFile)

      doLast {
        signing.sign(outputFile.get().asFile)
      }
    }

    assemble {
      dependsOn(signJar, signSourcesJar)
    }
  }
}
