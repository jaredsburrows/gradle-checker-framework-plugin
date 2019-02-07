package com.jaredsburrows.checkerframework

class CheckerExtension {
  List<String> checkers = ["org.checkerframework.checker.nullness.NullnessChecker"]

  // A list of extra options to pass directly to javac when running typecheckers
  List<String> extraJavacArgs = []
}
