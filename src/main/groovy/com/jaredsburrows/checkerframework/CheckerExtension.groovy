package com.jaredsburrows.checkerframework

class CheckerExtension { // extensions can't be final
  List<String> checkers = ["org.checkerframework.checker.nullness.NullnessChecker"]

  // A list of extra options to pass directly to javac when running typecheckers
  List<String> extraJavacArgs = []
}
