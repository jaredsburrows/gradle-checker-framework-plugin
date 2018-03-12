package com.jaredsburrows.checkerframework

import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.tasks.compile.AbstractCompile

final class CheckerPlugin implements Plugin<Project> {
  // Applicable plugins
  final static ANDROID_PLUGINS = ["com.android.application", "com.android.library", "com.android.test"]
  final static JVM_PLUGINS = ["kotlin", "groovy", "java"]
  // Checker Framework configurations and dependencies
  final static LIBRARY_VERSION = "2.1.11"
  final static ANNOTATED_JDK_NAME_JDK7 = "jdk7"
  final static ANNOTATED_JDK_NAME_JDK8 = "jdk8"
  final static ANNOTATED_JDK_CONFIGURATION = "checkerFrameworkAnnotatedJDK"
  final static ANNOTATED_JDK_CONFIGURATION_DESCRIPTION = "A copy of JDK classes with Checker Framework type qualifiers inserted."
  final static JAVAC_CONFIGURATION = "checkerFrameworkJavac"
  final static JAVAC_CONFIGURATION_DESCRIPTION = "A customization of the OpenJDK javac compiler with additional support for type annotations."
  final static CONFIGURATION = "checkerFramework"
  final static CONFIGURATION_DESCRIPTION = "The Checker Framework: custom pluggable types for Java."
  final static JAVA_COMPILE_CONFIGURATION = "compile"
  final static COMPILER_DEPENDENCY = "org.checkerframework:compiler:${LIBRARY_VERSION}"
  final static CHECKER_DEPENDENCY = "org.checkerframework:checker:${LIBRARY_VERSION}"
  final static CHECKER_QUAL_DEPENDENCY = "org.checkerframework:checker-qual:${LIBRARY_VERSION}"

  @Override void apply(Project project) {
    project.evaluationDependsOnChildren()

    if (!isValidProject(project)) {
      throw new IllegalStateException(
        "Checker plugin can only be applied to android or java projects.")
    }

    // Check for Java 7 or Java 8 to make sure to get correct annotations dependency
    def jdkVersion
    if (JavaVersion.current().java7) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK7
    } else if (JavaVersion.current().java8) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK8
    } else {
      throw new IllegalStateException("Checker plugin only supports Java 7 and Java 8 projects.")
    }

    def userConfig = project.extensions.create('checkerFramework', CheckerExtension)

    // Create a map of the correct configurations with dependencies
    def dependencyMap = [
      [name: "$ANNOTATED_JDK_CONFIGURATION", descripion: "$ANNOTATED_JDK_CONFIGURATION_DESCRIPTION"]: "org.checkerframework:$jdkVersion:$LIBRARY_VERSION",
      [name: "$JAVAC_CONFIGURATION", descripion: "$JAVAC_CONFIGURATION_DESCRIPTION"]                : "$COMPILER_DEPENDENCY",
      [name: "$CONFIGURATION", descripion: "$ANNOTATED_JDK_CONFIGURATION_DESCRIPTION"]              : "$CHECKER_DEPENDENCY",
      [name: "${JAVA_COMPILE_CONFIGURATION}", descripion: "$CONFIGURATION_DESCRIPTION"]             : "${CHECKER_QUAL_DEPENDENCY}"
    ]

    // Now, apply the dependencies to project
    dependencyMap.each { configuration, dependency ->
      // User could have an existing configuration, the plugin will add to it
      if (project.configurations.find { it.name == "$configuration.name".toString() }) {
        project.configurations."$configuration.name".dependencies.add(
          project.dependencies.create(dependency))
      } else {
        // If the user does not have the configuration, the plugin will create it
        project.configurations.create(configuration.name, new Action<Configuration>() {
          @Override
          void execute(Configuration files) {
            files.description = configuration.descripion
            files.visible = false
            files.defaultDependencies(new Action<DependencySet>() {
              @Override
              void execute(DependencySet dependencies) {
                dependencies.add(project.dependencies.create(dependency))
              }
            })
          }
        })
      }
    }

    // Apply checker to project
    project.gradle.projectsEvaluated {
      project.tasks.withType(AbstractCompile).all { compile ->
        compile.options.compilerArgs = [
          "-processorpath", "${project.configurations.checkerFramework.asPath}",
          "-Xbootclasspath/p:${project.configurations.checkerFrameworkAnnotatedJDK.asPath}"
        ]
        if (!userConfig.checkers.empty) {
          compile.options.compilerArgs << "-processor" << userConfig.checkers.join(',')
        }
        if (JavaVersion.current().java7) {
          compile.options.compilerArgs += ["-source", "7", "-target", "7"]
        } else {
          compile.options.compilerArgs += ["-source", "8", "-target", "8"]
        }
        if (isAndroidProject(project)) {
          options.bootClasspath =
            System.getProperty("sun.boot.class.path") + ":" + options.bootClasspath
          options.bootClasspath =
            "${project.configurations.checkerFrameworkJavac.asPath}:" + ":" + options.bootClasspath
        }
        options.fork = true
        //        options.forkOptions.jvmArgs += ["-Xbootclasspath/p:${project.configurations.checkerFrameworkJavac.asPath}"]
      }
    }
  }

  /**
   * Check to see if we can apply plugin to existing project.
   */
  static isValidProject(project) {
    isAndroidProject(project) || isJavaProject(project)
  }

  /**
   * Check if the project has Android plugins.
   */
  static isAndroidProject(project) {
    ANDROID_PLUGINS.find { plugin -> project.plugins.hasPlugin(plugin) }
  }

  /**
   * Check if project has Java plugins.
   */
  static isJavaProject(project) {
    JVM_PLUGINS.find { plugin -> project.plugins.hasPlugin(plugin) }
  }
}
