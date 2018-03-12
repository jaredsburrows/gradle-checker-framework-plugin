package com.jaredsburrows.checkerframework

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

final class CheckerPluginSpec extends Specification {
  def project

  def "setup"() {
    project = ProjectBuilder.builder().build()
  }

  def "unsupported project project"() {
    when:
    new CheckerPlugin().apply(project)

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
    projectPlugin << CheckerPlugin.JVM_PLUGINS + CheckerPlugin.ANDROID_PLUGINS
  }
}
