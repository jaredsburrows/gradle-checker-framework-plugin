# Gradle Checker Framework Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin.svg?branch=master)](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin)
[![Coverage Status](https://coveralls.io/repos/github/jaredsburrows/gradle-checker-framework-plugin/badge.svg?branch=master)](https://coveralls.io/github/jaredsburrows/gradle-checker-framework-plugin?branch=master)
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
    classpath "com.jaredsburrows:gradle-checker-framework-plugin:0.1.0"
  }
}

apply plugin: "com.jaredsburrows.checkerframework"
```

Snapshot versions are available in the JFrog Artifactory repository: https://oss.jfrog.org/webapp/#/builds/gradle-checker-framework-plugin
