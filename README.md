# Gradle Checker Framework Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin.svg?branch=master)](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

This plugin configures `JavaCompile` tasks to use the [Checker Framework](https://checkerframework.org).


## Download

Gradle:
```groovy
buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath "com.jaredsburrows:gradle-checker-framework-plugin:0.2.0"
  }
}

apply plugin: "com.jaredsburrows.checkerframework"
```

Snapshot versions are available in the JFrog Artifactory repository: https://oss.jfrog.org/webapp/#/builds/gradle-checker-framework-plugin

## Configuration

It is possible to configure the checkers you want to enable using the `checkerFramework.checkers` property.

For example:

```groovy
checkerFramework {
  checkers = [
    "org.checkerframework.checker.units.UnitsChecker", 
    "org.checkerframework.checker.nullness.NullnessChecker"]
}
```

By default, only the `NullnessChecker` is enabled.

You can find out what checkers are available in the [Checker Framework Manual](https://checkerframework.org/manual/#introduction).
