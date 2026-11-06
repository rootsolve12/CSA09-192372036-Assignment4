#!/bin/sh
set -eu
cd "$(dirname "$0")/.."
java -m jdk.compiler/com.sun.tools.javac.Main -d build/common tools/TerminalEvidence.java
"${ASSIGNMENT_GUI_JAVA:-java}" -cp build/common TerminalEvidence
