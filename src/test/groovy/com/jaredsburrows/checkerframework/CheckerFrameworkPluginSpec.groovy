package com.jaredsburrows.checkerframework

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
final class CheckerFrameworkPluginSpec extends Specification {
  def project

  def "setup"() {
    project = ProjectBuilder.builder().build()
  }

  def "unsupported project project"() {
    when:
    new CheckerFrameworkPlugin().apply(project) // project.apply plugin: "com.jaredsburrows.checkerframework"

    then:
    def e = thrown(IllegalStateException)
    e.message == "Checker plugin can only be applied to android or java projects."
  }

  @Unroll "#projectPlugin project"() {
    given:
    project.apply plugin: projectPlugin

    when:
    project.apply plugin: "com.jaredsburrows.checkerframework"

    then:
    noExceptionThrown()

    where:
    projectPlugin << CheckerFrameworkPlugin.JVM_PLUGINS + CheckerFrameworkPlugin.ANDROID_PLUGINS
  }
}
