package com.jaredsburrows.checkerframework

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Unroll
import test.BaseSpecification

final class CheckerPluginSpec extends BaseSpecification {
  @Unroll def "java project running licenseReport using with gradle #gradleVersion"() {
    given:
    buildFile <<
      """
        plugins {
          id "java"
          id "com.jaredsburrows.checkerframework"
        }

        repositories {
          maven {
            url "${getClass().getResource("/maven/").toURI()}"
          }
        }
      """.stripIndent().trim()

    when:
    GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .build()

    then:
    noExceptionThrown()

    where:
    gradleVersion << [
      "3.4",
      "3.5",
      "4.0",
      "4.1",
      "4.2",
      "4.3",
      "4.4",
      "4.5",
      "4.6",
    ]
  }
}
