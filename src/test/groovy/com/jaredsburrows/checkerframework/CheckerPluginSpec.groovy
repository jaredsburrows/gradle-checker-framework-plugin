package com.jaredsburrows.checkerframework

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Unroll
import test.BaseSpecification

final class CheckerPluginSpec extends BaseSpecification {
  static final List<String> TESTED_GRADLE_VERSIONS = [
    '3.4',
    '3.5',
    '4.0',
    '4.1',
    '4.2',
    '4.3',
    '4.4',
    '4.5',
    '4.6',
    '4.7',
    '4.8',
    '4.9',
    '4.10',
    '5.0',
  ]

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
    gradleVersion << TESTED_GRADLE_VERSIONS
  }

  @Unroll
  def 'without relevant plugin, compiler settings are not applied using version #gradleVersion'() {
    given:
    buildFile <<
      """
        plugins {
          id 'com.jaredsburrows.checkerframework'
        }

        repositories {
          maven {
            url "${getClass().getResource("/maven/").toURI()}"
          }
        }
      """.stripIndent().trim()

    when:
    BuildResult result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .build()

    then:
    result.output.contains('checker compiler options will not be applied')

    where:
    gradleVersion << TESTED_GRADLE_VERSIONS
  }

  @Unroll
  def 'with relevant plugin, compiler settings are applied using version #gradleVersion'() {
    given:
    buildFile <<
      """
        plugins {
          id 'java'
          id 'com.jaredsburrows.checkerframework'
        }

        repositories {
          maven {
            url "${getClass().getResource("/maven/").toURI()}"
          }
        }
      """.stripIndent().trim()

    when:
    BuildResult result = GradleRunner.create()
      .withGradleVersion(gradleVersion)
      .withProjectDir(testProjectDir.root)
      .withPluginClasspath()
      .withArguments('--info')
      .build()

    then:
    result.output.contains('applying checker compiler options')

    where:
    gradleVersion << TESTED_GRADLE_VERSIONS
  }

}
