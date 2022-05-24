# Gradle Checker Framework Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/jaredsburrows/gradle-checker-framework-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/jaredsburrows/gradle-checker-framework-plugin/actions/workflows/build.yml)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

This plugin configures `JavaCompile` tasks to use the [Checker Framework](https://checkerframework.org).

## Version Compatibility
| Plugin Version | Checker Framework | Android Gradle Plugin |
| --- | --- | --- |
| 0.2.2 | 2.4.0 | 3.2.1 |

## Download

**Release:**
```groovy
buildscript {
  repositories {
    mavenCentral()
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-checker-framework-plugin:0.2.2'
  }
}

apply plugin: 'com.jaredsburrows.checkerframework'
```
Release versions are available in the [Sonatype's release repository](https://repo1.maven.org/maven2/com/jaredsburrows/gradle-checker-framework-plugin/).

**Snapshot:**
```groovy
buildscript {
  repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-checker-framework-plugin:0.2.3-SNAPSHOT'
  }
}

apply plugin: 'com.jaredsburrows.checkerframework'
```
Snapshot versions are available in the [Sonatype's snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/com/jaredsburrows/gradle-checker-framework-plugin/).

## Configuration

It is possible to configure the checkers you want to enable using the `checkerFramework.checkers` property.

For example:

```groovy
checkerFramework {
  checkers = [
    'org.checkerframework.checker.units.UnitsChecker',
    'org.checkerframework.checker.nullness.NullnessChecker'
  ]
}
```

By default, only the `NullnessChecker` is enabled.

You can find out what checkers are available in the [Checker Framework Manual](https://checkerframework.org/manual/#introduction).

## License
```
Copyright (C) 2017 Jared Burrows

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
