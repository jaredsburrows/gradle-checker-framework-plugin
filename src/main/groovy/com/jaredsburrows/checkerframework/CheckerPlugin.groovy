package com.jaredsburrows.checkerframework

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.AppliedPlugin
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

  private final static Logger LOG = Logging.getLogger(CheckerPlugin)

  @Override void apply(Project project) {
    CheckerExtension userConfig = project.extensions.create("checkerFramework", CheckerExtension)
    boolean applied = false
    (ANDROID_IDS + "java").each { id ->
      project.pluginManager.withPlugin(id) {
        LOG.info('Found plugin {}, applying checker compiler options.', id)
        configureProject(project, userConfig)
        if (!applied) applied = true
      }
    }
    project.gradle.projectsEvaluated {
      if (!applied) LOG.warn('No android or java plugins found, checker compiler options will not be applied.')
    }
  }

  private static configureProject(Project project, CheckerExtension userConfig) {
    JavaVersion javaVersion =
        project.extensions.findByName('android')?.compileOptions?.sourceCompatibility ?:
        project.property('sourceCompatibility')

    // Check for Java 7 or Java 8 to make sure to get correct annotations dependency
    def jdkVersion
    if (javaVersion.java7) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK7
    } else if (javaVersion.java8) {
      jdkVersion = ANNOTATED_JDK_NAME_JDK8
    } else {
      throw new IllegalStateException("Checker plugin only supports Java 7 and Java 8 projects.")
    }

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
        compile.options.annotationProcessorPath = project.configurations.checkerFramework
        compile.options.compilerArgs = [
          "-Xbootclasspath/p:${project.configurations.checkerFrameworkAnnotatedJDK.asPath}".toString()
        ]
        if (!userConfig.checkers.empty) {
          compile.options.compilerArgs << "-processor" << userConfig.checkers.join(",")
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
