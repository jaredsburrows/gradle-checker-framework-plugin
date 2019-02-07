# Gradle Checker Framework Plugin

[![License](https://img.shields.io/badge/license-apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build Status](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin.svg?branch=master)](https://travis-ci.org/jaredsburrows/gradle-checker-framework-plugin)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

This plugin configures `JavaCompile` tasks to use the [Checker Framework](https://checkerframework.org).

## Download

**Release:**
```groovy
buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-checker-framework-plugin:0.2.2'
  }
}

apply plugin: 'com.jaredsburrows.checkerframework'
```
Release versions are available in the [JFrog Bintray repository](https://jcenter.bintray.com/).

**Snapshot:**
```groovy
buildscript {
  repositories {
    maven { url 'https://oss.jfrog.org/artifactory/oss-snapshot-local/' }
  }

  dependencies {
    classpath 'com.jaredsburrows:gradle-checker-framework-plugin:0.2.3-SNAPSHOT'
  }
}

apply plugin: 'com.jaredsburrows.checkerframework'
```
Snapshot versions are available in the [JFrog Artifactory repository](https://oss.jfrog.org/artifactory/libs-snapshot/).

## Configuration

### Configuring which checkers to use

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

### Providing checker-specific options to the compiler

You can set the `checkerFramework.extraJavacArgs` property in order to pass additional options to the compiler when running
a typechecker.

For example, to use a stub file:

```groovy
checkerFramework {
  extraJavacArgs = [
    '-Astubs=/path/to/my/stub/file.astub'
  ]
}
```

### Configuring third-party checkers

To use a third-party typechecker (i.e. one that is not distributed with the Checker Framework),
add a dependency to the `checkerFramework` dependency configuration.

For example, to use the [Glacier](http://mcoblenz.github.io/Glacier/) immutability checker:

```groovy
dependencies {
  ...
  checkerFramework 'edu.cmu.cs.glacier:glacier:0.1'
}
```

## License

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
