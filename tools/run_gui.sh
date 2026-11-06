#!/bin/sh
# Run from a graphical desktop after configuring the dedicated assignment database.
set -eu
cd "$(dirname "$0")/.."
GUI_JAVA="${ASSIGNMENT_GUI_JAVA:-java}"
CP="build/common:build/Q1:build/Q2:build/Q3:build/Q4:build/Q5/after:build/Q6:deps/mysql-connector-j.jar"
python3 tools/compile.py
java -m jdk.compiler/com.sun.tools.javac.Main -cp "$CP" -d build/common tools/GuiEvidence.java
for task in Q1 Q2 Q3 Q4; do
 "$GUI_JAVA" -cp "$CP" GuiEvidence "$task" > "$task/results/execution.txt" 2>&1
done
"$GUI_JAVA" -cp "build/Q5/before:$CP" GuiEvidence Q5 before > Q5/results/before-execution.txt 2>&1
mv Q5/results/gui-tests.txt Q5/results/gui-tests-before.txt
"$GUI_JAVA" -cp "$CP" GuiEvidence Q5 after > Q5/results/after-execution.txt 2>&1
"$GUI_JAVA" -cp "$CP" GuiEvidence Q6 > Q6/results/execution.txt 2>&1
java -m jdk.compiler/com.sun.tools.javac.Main -cp "build/common:build/Q5/supplied" -d build/common tools/SuppliedConverterEvidence.java
"$GUI_JAVA" -cp "build/common:build/Q5/supplied" SuppliedConverterEvidence > Q5/results/supplied-execution.txt 2>&1
printf 'All GUI checks completed\n'
