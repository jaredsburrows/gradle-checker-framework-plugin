package com.jaredsburrows.checkerframework

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile

final class CheckerPlugin implements Plugin<Project> {
  // Handles pre-3.0 and 3.0+, "com.android.base" was added in AGP 3.0
  private static final def ANDROID_IDS = [
    "com.android.application",
    "com.android.feature",
    "com.android.instantapp",
    "com.android.library",
    "com.android.test"]
  // Checker Framework configurations and dependencies
  private final static def LIBRARY_VERSION = "2.4.0"
  private final static def ANNOTATED_JDK_NAME_JDK7 = "jdk7"
  private final static def ANNOTATED_JDK_NAME_JDK8 = "jdk8"
  private final static def ANNOTATED_JDK_CONFIGURATION = "checkerFrameworkAnnotatedJDK"
  private final static def ANNOTATED_JDK_CONFIGURATION_DESCRIPTION = "A copy of JDK classes with Checker Framework type qualifiers inserted."
  private final static def JAVAC_CONFIGURATION = "checkerFrameworkJavac"
  private final static def JAVAC_CONFIGURATION_DESCRIPTION = "A customization of the OpenJDK javac compiler with additional support for type annotations."
  private final static def CONFIGURATION = "checkerFramework"
  private final static def CONFIGURATION_DESCRIPTION = "The Checker Framework: custom pluggable types for Java."
  private final static def JAVA_COMPILE_CONFIGURATION = "compile"
  private final static def COMPILER_DEPENDENCY = "org.checkerframework:compiler:${LIBRARY_VERSION}"
  private final static def CHECKER_DEPENDENCY = "org.checkerframework:checker:${LIBRARY_VERSION}"
  private final static def CHECKER_QUAL_DEPENDENCY = "org.checkerframework:checker-qual:${LIBRARY_VERSION}"

  @Override void apply(Project project) {
    (ANDROID_IDS + "java").each { id ->
      project.plugins.withId(id) { configureProject(project) }
    }
  }

  private static configureProject(def project) {
    // Check for Java 7 or Java 8 to make sure to get correct annotations dependency
    def jdkVersion
    if (JavaVersion.current().java7) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK7
    } else if (JavaVersion.current().java8) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK8
    } else {
      throw new IllegalStateException("Checker plugin only supports Java 7 and Java 8 projects.")
    }

    def userConfig = project.extensions.create("checkerFramework", CheckerExtension)

    // Create a map of the correct configurations with dependencies
    def dependencyMap = [
      [name: "${ANNOTATED_JDK_CONFIGURATION}", descripion: "${ANNOTATED_JDK_CONFIGURATION_DESCRIPTION}"]: "org.checkerframework:${jdkVersion}:${LIBRARY_VERSION}",
      [name: "${JAVAC_CONFIGURATION}", descripion: "${JAVAC_CONFIGURATION_DESCRIPTION}"]                : "${COMPILER_DEPENDENCY}",
      [name: "${CONFIGURATION}", descripion: "${ANNOTATED_JDK_CONFIGURATION_DESCRIPTION}"]              : "${CHECKER_DEPENDENCY}",
      [name: "${JAVA_COMPILE_CONFIGURATION}", descripion: "${CONFIGURATION_DESCRIPTION}"]               : "${CHECKER_QUAL_DEPENDENCY}"
    ]

    // Now, apply the dependencies to project
    dependencyMap.each { configuration, dependency ->
      // User could have an existing configuration, the plugin will add to it
      if (project.configurations.find { it.name == "$configuration.name".toString() }) {
        project.configurations."$configuration.name".dependencies.add(
          project.dependencies.create(dependency))
      } else {
        // If the user does not have the configuration, the plugin will create it
        project.configurations.create(configuration.name) { files ->
          files.description = configuration.descripion
          files.visible = false
          files.defaultDependencies { dependencies ->
            dependencies.add(project.dependencies.create(dependency))
          }
        }
      }
    }

    // Apply checker to project
    project.gradle.projectsEvaluated {
      project.tasks.withType(AbstractCompile).all { compile ->
        compile.options.compilerArgs = [
          "-processorpath", "${project.configurations.checkerFramework.asPath}".toString(),
          "-Xbootclasspath/p:${project.configurations.checkerFrameworkAnnotatedJDK.asPath}".toString()
        ]
        if (!userConfig.checkers.empty) {
          compile.options.compilerArgs << "-processor" << userConfig.checkers.join(",")
        }
        if (JavaVersion.current().java7) {
          compile.options.compilerArgs += ["-source", "7", "-target", "7"]
        } else {
          compile.options.compilerArgs += ["-source", "8", "-target", "8"]
        }

        ANDROID_IDS.each { id ->
          project.plugins.withId(id) {
            options.bootClasspath = System.getProperty("sun.boot.class.path") + ":" + options.bootClasspath
            options.bootClasspath = "${project.configurations.checkerFrameworkJavac.asPath}:".toString() + ":" + options.bootClasspath
          }
        }
        options.fork = true
        //        options.forkOptions.jvmArgs += ["-Xbootclasspath/p:${project.configurations.checkerFrameworkJavac.asPath}"]
      }
    }
  }
}
