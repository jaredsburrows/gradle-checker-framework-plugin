#!/usr/bin/env bash

VERSION=2.4.0
MAVEN_PATH=src/test/resources/maven/

rm -rf ${MAVEN_PATH}/org/checkerframework/

# checker
mkdir -p ${MAVEN_PATH}/org/checkerframework/checker/
wget http://jcenter.bintray.com/org/checkerframework/checker/${VERSION}/checker-${VERSION}.pom -P ${MAVEN_PATH}/org/checkerframework/checker/${VERSION}/
wget http://jcenter.bintray.com/org/checkerframework/checker/${VERSION}/checker-${VERSION}.jar -P ${MAVEN_PATH}/org/checkerframework/checker/${VERSION}/

# checker-qual
mkdir -p ${MAVEN_PATH}/org/checkerframework/checker-qual/
wget http://jcenter.bintray.com/org/checkerframework/checker-qual/${VERSION}/checker-qual-${VERSION}.pom -P ${MAVEN_PATH}/org/checkerframework/checker-qual/${VERSION}/
wget http://jcenter.bintray.com/org/checkerframework/checker-qual/${VERSION}/checker-qual-${VERSION}.jar -P ${MAVEN_PATH}/org/checkerframework/checker-qual/${VERSION}/

# jdk8
mkdir -p ${MAVEN_PATH}/org/checkerframework/jdk8/
wget http://jcenter.bintray.com/org/checkerframework/jdk8/${VERSION}/jdk8-${VERSION}.pom -P ${MAVEN_PATH}/org/checkerframework/jdk8/${VERSION}/
wget http://jcenter.bintray.com/org/checkerframework/jdk8/${VERSION}/jdk8-${VERSION}.jar -P ${MAVEN_PATH}/org/checkerframework/jdk8/${VERSION}/
