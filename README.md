# CSA09-192372036-Assignment4

**B Hitesh Roy — Register 192372036**  
Programming in Java — Assignment 4 CO4 — Individual — 50 marks

Six AWT/Swing GUI tasks: source code, SQL schema, reasoned designs, pseudocode, alternative/complexity comparisons, compiler logs, real GUI checks and requirement-linked screenshots. Word/PDF preparation is deferred until the GitHub stage is complete.

## Environment and requirements

Compilation: OpenJDK 21.0.13 using its jdk.compiler module. GUI execution: full OpenJDK 21.0.12.1 desktop runtime. JDBC: MySQL Connector/J 9.5.0, MySQL Community Server 8.4.13. GUI checks require a real desktop (not java.awt.headless=true). Java 17+ source features are used. No browser applet is required. Dependency binaries, database files and credentials are excluded from GitHub.

## Setup and run

Install a **full JDK** (including desktop/AWT support), Python 3, MySQL and MySQL Connector/J. Place the driver at deps/mysql-connector-j.jar (create deps first). Configure a dedicated database using Q6/schema.sql. A database administrator creates the database/user and grants SELECT and INSERT; do not put passwords in source or git.

Set ASSIGNMENT_DB_URL to jdbc:mysql://127.0.0.1:3306/assignment4, ASSIGNMENT_DB_USER and ASSIGNMENT_DB_PASSWORD in your local environment. Db.java defaults to port 3307 for the workspace demonstration; override the URL for your server. For a remote database, configure certificate-verified TLS appropriate to your deployment.

From the repository root:
```sh
python tools/compile.py
# Unix classpath separator is : (use ; on Windows).
java -cp build/common:build/Q1:deps/mysql-connector-j.jar FeeWindow
java -cp build/common:build/Q2 AppointmentDesk
java -cp build/common:build/Q3 LibrarySearch
java -cp build/common:build/Q4 DrawPad
java -cp build/common:build/Q4 ImmediatePad
java -cp build/common:build/Q5/before FrameConverter
java -cp build/common:build/Q5/after FrameConverter
java -cp build/common:build/Q5/after NamedConverter
java -cp build/common:build/Q5/after AnonymousConverter
java -cp build/common:build/Q5/after LambdaConverter
java -cp build/common:build/Q5/supplied Converter
java -cp build/common:build/Q6:deps/mysql-connector-j.jar CampusEvents
```
Run each Q5 before counterpart by changing after to before. Keep variants in separate build directories; they intentionally have the same class names. Q4's misspelled-with-Override variant is an expected compiler failure; the annotation-free misspelling compiles but does not handle clicks.

## Task map

| Folder | Result and evidence |
|---|---|
| [Q1](Q1/README.md) | FeeWindow/FeeService, validation, saved payment and receipt design |
| [Q2](Q2/README.md) | Event table, containment diagram, handler pseudocode and window trace |
| [Q3](Q3/README.md) | Search/pagination/reservation and three-size measured layout evidence |
| [Q4](Q4/README.md) | Stored-point repair, immediate-mode failure and both compiler experiments |
| [Q5](Q5/README.md) | Four before/after implementations, supplied TODO completion and exact change diffs |
| [Q6](Q6/README.md) | Swing/JDBC layers, unique-key race protection, filtering/export and 100000-row load comparison |

Evidence dates describe actual workspace execution; progressive task commits are created as tasks are completed in this session and are not represented as work on different days. Personally execute every task as required by the assignment. The exact GCR report filename and deadline are not supplied in the assignment PDF.

## Automated reproduction

On a Unix graphical desktop, run `ASSIGNMENT_TEST_DELAY_MS=1200 sh tools/run_gui.sh` after setting the database environment variables. A full desktop JDK is required. Tests create small fee and registration records in the dedicated assignment database, move/minimize windows and take actual screenshots. Set ASSIGNMENT_GUI_JAVA to a full desktop Java executable if the default java installation is headless. Compiler-only checks do not require a display. `sh tools/capture_compilers.sh` captures a maximized terminal while actually compiling each task. The GUI test output and compiler logs are separate evidence.

For the optional 100000-row benchmark, an administrator must set ASSIGNMENT_ADMIN_USER and ASSIGNMENT_ADMIN_PASSWORD to credentials for the dedicated assignment database. BenchmarkJdbc creates/repopulates **only benchmark_registrations** when it does not already contain exactly 100000 rows. Compile and run it separately:
```sh
java -m jdk.compiler/com.sun.tools.javac.Main -cp build/common:build/Q6 -d build/common tools/BenchmarkJdbc.java
java -cp build/common:build/Q6:deps/mysql-connector-j.jar BenchmarkJdbc
```
The app user needs SELECT on that benchmark table as well. Do not run these experiments against a production database. App login/committee authorization and a real banking gateway are outside this assignment demonstration.
