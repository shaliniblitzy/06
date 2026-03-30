# Technical Specification

# 0. Agent Action Plan

## 0.1 Intent Clarification


### 0.1.1 Core Testing Objective

Based on the provided requirements, the Blitzy platform understands that the testing objective is to **create a comprehensive suite of new unit tests** for a greenfield Java Age Calculator application. The application computes a user's exact age in years, months, and days from a Date of Birth (DOB) entered in `DD/MM/YYYY` format, leveraging the `java.time` API (`LocalDate`, `Period`, `DateTimeFormatter`).

**Request Category:** Add new tests (greenfield project — no existing test infrastructure)

The testing requirements, enhanced with implicit technical needs, are:

- **Normal DOB calculation** — Verify correct age computation for standard dates (e.g., 15/08/1998), including age components in years, months, and days
- **Leap year DOB handling** — Validate accurate age calculation for DOB on February 29 (e.g., 29/02/2000), including behavior on non-leap-year current dates
- **Invalid date rejection** — Confirm proper error handling for impossible calendar dates (e.g., 31/02/2020, 32/01/2020, 00/13/2000)
- **Future date rejection** — Ensure the system rejects DOB values that fall after the current system date with a meaningful error message
- **Malformed format rejection** — Verify graceful handling of inputs that do not match `DD/MM/YYYY` (e.g., `1998-08-15`, `15-08-1998`, alphabetic input, empty strings, null values)
- **Boundary conditions** (implicit) — Test edge cases such as DOB equal to today, DOB exactly one year ago, century boundaries, and very old dates
- **Output format verification** (implicit) — Confirm the result displays as `Your age is X years, Y months, and Z days.`
- **Exception handling paths** (implicit) — Validate that `try-catch` blocks correctly intercept `DateTimeParseException` and custom validation failures

### 0.1.2 Special Instructions and Constraints

- The application is a **new Java project** — the repository currently contains only a `README.md` with heading `# 06`
- No existing test infrastructure, test patterns, or conventions exist to follow
- The user explicitly specified five test case categories: normal DOB, leap year DOB, invalid date, future date, and wrong format input
- OOP principles must guide the application structure, which in turn guides how tests are structured (testing classes and methods, not monolithic scripts)
- `java.time.LocalDate`, `java.time.Period`, and `java.time.format.DateTimeFormatter` are the mandated APIs — tests must exercise these specifically
- Proper exception handling using `try-catch` is required — tests must verify exception behavior

**User Example (preserved exactly):**

User Example — Input:
```
Enter your Date of Birth (DD/MM/YYYY): 15/08/1998
```

User Example — Output:
```
Your age is 27 years, 6 months, and 15 days.
```

### 0.1.3 Technical Interpretation

These testing requirements translate to the following technical test implementation strategy:

- To **test normal age calculation**, we will create `AgeCalculatorTest.java` with parameterized test methods exercising `calculateAge()` with known DOB-to-expected-age mappings
- To **test leap year scenarios**, we will create dedicated test methods in `AgeCalculatorTest.java` that fix the reference date and validate February 29 edge cases
- To **test invalid date rejection**, we will create `DateValidatorTest.java` verifying that `DateTimeParseException` or custom validation exceptions are raised for impossible calendar dates
- To **test future date rejection**, we will create test methods in `AgeCalculatorTest.java` and `DateValidatorTest.java` confirming `IllegalArgumentException` is thrown for future DOBs
- To **test wrong format input**, we will create test methods in `DateValidatorTest.java` exercising various malformed input strings against the `DD/MM/YYYY` `DateTimeFormatter`
- To **test the result model**, we will create `AgeResultTest.java` validating the OOP data model that encapsulates years, months, and days
- To **test output formatting**, we will create test methods verifying the `toString()` or formatting method produces the exact expected output string

### 0.1.4 Coverage Requirements Interpretation

- **Explicit coverage targets:** None specified by the user
- **Implicit coverage expectations based on project context:**
  - Java/JUnit 5 industry standard for new projects: ≥ 80% line coverage
  - All five user-specified test categories must have dedicated test methods
  - All public methods in the application must have at least one corresponding test
  - Error handling paths (exception branches) must be covered
- To achieve comprehensive testing, coverage should include:
  - 100% of public API methods across `AgeCalculator`, `DateValidator`, and `AgeResult`
  - All branching logic in validation (future date, invalid date, wrong format)
  - Both happy-path and failure-path execution for every method
  - Boundary values at date edges (month start/end, year start/end, leap day)


## 0.2 Test Discovery and Analysis


### 0.2.1 Existing Test Infrastructure Assessment

Repository analysis reveals a **completely empty project** with no testing infrastructure, no source code, and no build configuration. The repository root contains exactly one file:

| File | Content | Relevance |
|------|---------|-----------|
| `README.md` | Single heading `# 06` | No test-related content |

**Search patterns executed and results:**

- `*test*`, `*spec*`, `test_*`, `*_test.*`, `*_spec.*` — No files found
- `pom.xml`, `build.gradle`, `build.gradle.kts` — No build files found
- `*.java` — No Java source files found
- `junit*.xml`, `surefire-reports/` — No test output artifacts found
- `jest.config.*`, `pytest.ini`, `.mocharc.*` — No test configuration files found

**Infrastructure findings:**

- Current testing framework: **None** — no testing dependencies declared
- Test runner configuration: **None** — no Maven/Gradle configuration exists
- Coverage tools in use: **None**
- Mock/stub libraries detected: **None**
- Test data fixtures or factories: **None**
- CI/CD pipeline: **None** — no workflow files present

This is a greenfield project requiring complete test infrastructure creation from scratch, including the Maven build system (`pom.xml`), Java source directory structure, test directory structure, and all testing dependencies.

### 0.2.2 Web Search Research Conducted

Research was conducted to determine the optimal testing stack for a new Java 17 project:

- **JUnit 5 (Jupiter) testing framework:** JUnit 5.10.2 is the recommended stable version for Java 17 projects. JUnit 6.0.3 (released January 2026) is the latest overall release but is newer and less ecosystem-proven. JUnit 5.10.2 offers maximum compatibility with Maven Surefire and IDE tooling.
- **Maven Surefire Plugin:** Version 3.5.5 is the latest stable release, providing native JUnit Jupiter support without additional provider dependencies (since Surefire ≥ 2.22.0).
- **JaCoCo code coverage:** Version 0.8.14 is the latest stable release (October 2025), with official support for Java 17 and ASM 9.7 compatibility.
- **Mocking strategies:** For a simple calculator application with no external service dependencies, `java.time.Clock` injection or fixed `LocalDate` references are sufficient for controlling time-dependent tests — no external mocking library is required.
- **Test organization conventions for Java/Maven:** Standard Maven convention places test sources in `src/test/java/` mirroring the `src/main/java/` package structure, with test classes named `*Test.java` for Surefire auto-discovery.


## 0.3 Testing Scope Analysis


### 0.3.1 Test Target Identification

**Primary code to be tested:**

Since this is a greenfield project, all source files listed below will be created alongside their corresponding test files. The application architecture follows OOP principles as specified by the user.

- **Class:** `AgeCalculator` at `src/main/java/com/agecalculator/AgeCalculator.java` — requires unit tests for core calculation logic
  - Method `calculateAge(LocalDate dob)` — age computation from DOB to current date
  - Method `calculateAge(LocalDate dob, LocalDate referenceDate)` — testable overload with explicit reference date
- **Class:** `DateValidator` at `src/main/java/com/agecalculator/util/DateValidator.java` — requires unit tests for input validation
  - Method `parse(String input)` — parse DD/MM/YYYY string to LocalDate
  - Method `validate(LocalDate dob)` — ensure DOB is not in the future
- **Class:** `AgeResult` at `src/main/java/com/agecalculator/model/AgeResult.java` — requires unit tests for result model
  - Constructor validation, getters, `toString()` formatting
- **Class:** `AgeCalculatorApp` at `src/main/java/com/agecalculator/AgeCalculatorApp.java` — main entry point
  - Out of unit test scope (console I/O); tested indirectly through component tests

**Existing test file mapping:**

| Source File | Existing Test File | Test Categories Present |
|-------------|-------------------|------------------------|
| `src/main/java/com/agecalculator/AgeCalculator.java` | None — to be created | None |
| `src/main/java/com/agecalculator/util/DateValidator.java` | None — to be created | None |
| `src/main/java/com/agecalculator/model/AgeResult.java` | None — to be created | None |
| `src/main/java/com/agecalculator/AgeCalculatorApp.java` | None — out of scope | N/A |

**Dependencies requiring mocking:**

- **External services to mock:** None — the application has no external API or service dependencies
- **Database interactions to stub:** None — no database is used
- **File system operations to virtualize:** None
- **Time dependency:** The `java.time.LocalDate.now()` call must be made testable by accepting a reference `LocalDate` parameter, enabling deterministic assertions without mocking the system clock

### 0.3.2 Version Compatibility Research

Based on the Java 17.0.18 runtime installed in the environment, the recommended testing stack is:

| Category | Library | Version | Rationale |
|----------|---------|---------|-----------|
| Testing framework | JUnit Jupiter (`junit-jupiter`) | 5.10.2 | Stable JUnit 5 release with full Java 17 support; aggregator artifact includes API, engine, and params |
| Test runner | Maven Surefire Plugin | 3.5.5 | Latest stable with native JUnit Platform support |
| Coverage tool | JaCoCo Maven Plugin | 0.8.14 | Latest stable release, official Java 17 support |
| Assertion library | JUnit Jupiter Assertions (built-in) | 5.10.2 | Bundled with JUnit Jupiter — `assertThrows`, `assertEquals`, `assertAll` |
| Parameterized tests | JUnit Jupiter Params (built-in) | 5.10.2 | Bundled with `junit-jupiter` aggregator for `@ParameterizedTest`, `@CsvSource`, `@MethodSource` |
| Mocking library | None required | N/A | No external dependencies to mock; time testability achieved via method parameter injection |

**Version conflict analysis:** No conflicts detected. All selected versions are mutually compatible with Java 17 and Maven 3.8.7.


## 0.4 Test Implementation Design


### 0.4.1 Test Strategy Selection

**Test types to implement:**

- **Unit tests:** Focus on isolated testing of `AgeCalculator.calculateAge()`, `DateValidator.parse()`, `DateValidator.validate()`, and `AgeResult` model behavior
- **Parameterized tests:** Leverage JUnit 5 `@ParameterizedTest` with `@CsvSource` and `@MethodSource` for data-driven validation across multiple DOB inputs
- **Edge case tests:** Address leap year boundaries (Feb 29), century boundaries, DOB equal to today, and month-end transitions
- **Error handling tests:** Verify `DateTimeParseException` for malformed input, `IllegalArgumentException` for future dates, and `DateTimeException` for impossible calendar dates

### 0.4.2 Test Case Blueprint

```
Component: AgeCalculator
Test Categories:
- Happy path: Standard DOB (15/08/1998), DOB at year boundary (01/01/2000), DOB earlier same month
- Edge cases: DOB = today (age 0,0,0), DOB = yesterday (age 0,0,1), leap year DOB (29/02/2000), DOB on Dec 31
- Error cases: Future DOB throws IllegalArgumentException, null DOB throws NullPointerException
- Boundary: DOB exactly 1 year ago, DOB on month boundary (31/01 to 28/02 transition)
```

```
Component: DateValidator
Test Categories:
- Happy path: Valid DD/MM/YYYY strings (15/08/1998, 01/01/2000, 29/02/2000)
- Edge cases: Leading zeros (01/01/0001), max day values per month (28,29,30,31)
- Error cases: Invalid format (YYYY-MM-DD, MM/DD/YYYY), impossible dates (31/02/2020, 29/02/2001), empty string, null, alphabetic input
- Future date: Valid-format date in the future triggers validation failure
```

```
Component: AgeResult
Test Categories:
- Happy path: Construct with valid years/months/days, verify getters
- Edge cases: Zero values (0 years, 0 months, 0 days), large values (100+ years)
- toString: Output matches "Your age is X years, Y months, and Z days."
- Equality: Two AgeResult objects with same values are logically equivalent
```

### 0.4.3 Existing Test Extension Strategy

Not applicable — this is a greenfield project with no existing tests to extend, refactor, or fix. All test files will be created new.

### 0.4.4 Test Data and Fixtures Design

**Required test data structures:**

- Fixed reference dates using `LocalDate.of()` for deterministic assertions (e.g., `LocalDate.of(2025, 3, 30)`)
- CSV-based test data for `@CsvSource` parameterized tests mapping DOB strings to expected age components
- Method-source providers returning `Stream<Arguments>` for complex test scenarios with `LocalDate` objects

**Fixture organization strategy:**

- No shared fixture class needed — test data is lightweight and inline
- Each test class provides its own data through `@CsvSource` annotations and `static` provider methods
- Reference dates are defined as `private static final` constants within each test class

**Mock object specifications:**

- No mock objects required — the application has no external dependencies
- Time-dependent behavior is made testable by accepting `LocalDate referenceDate` as a method parameter rather than calling `LocalDate.now()` directly

**Test state management approach:**

- Each test method is fully isolated — no shared mutable state
- `@BeforeEach` used only for constructing fresh `AgeCalculator` and `DateValidator` instances
- No database, file system, or network state to manage


## 0.5 Test File Transformation Mapping


### 0.5.1 File-by-File Test Plan

| Target Test File | Transformation | Source File/Reference | Purpose/Changes |
|-----------------|----------------|----------------------|-----------------|
| `src/test/java/com/agecalculator/AgeCalculatorTest.java` | CREATE | `src/main/java/com/agecalculator/AgeCalculator.java` | Comprehensive unit tests for age calculation: normal DOB, leap year DOB, boundary dates, future date rejection, null handling |
| `src/test/java/com/agecalculator/util/DateValidatorTest.java` | CREATE | `src/main/java/com/agecalculator/util/DateValidator.java` | Unit tests for date parsing and validation: valid formats, invalid dates (31/02), wrong formats, empty/null input, future date detection |
| `src/test/java/com/agecalculator/model/AgeResultTest.java` | CREATE | `src/main/java/com/agecalculator/model/AgeResult.java` | Unit tests for result model: constructor, getters, toString formatting, edge values (zero age, large age) |
| `pom.xml` | CREATE | N/A | Maven build configuration with JUnit 5, Surefire, JaCoCo dependencies and plugin configuration |
| `src/main/java/com/agecalculator/AgeCalculator.java` | CREATE | N/A | Core calculation class using `LocalDate`, `Period` — source under test |
| `src/main/java/com/agecalculator/util/DateValidator.java` | CREATE | N/A | Date parsing/validation utility using `DateTimeFormatter` — source under test |
| `src/main/java/com/agecalculator/model/AgeResult.java` | CREATE | N/A | OOP result model encapsulating years, months, days — source under test |
| `src/main/java/com/agecalculator/AgeCalculatorApp.java` | CREATE | N/A | Main entry point with console I/O — not directly unit-tested |

### 0.5.2 New Test Files Detail

**`src/test/java/com/agecalculator/AgeCalculatorTest.java`** — Core age calculation unit tests

- Test categories: happy path, leap year edge cases, boundary conditions, error handling
- Mock dependencies: None — uses `LocalDate` reference date injection
- Assertions focus:
  - `assertEquals` on years, months, days components from `Period`/`AgeResult`
  - `assertThrows(IllegalArgumentException.class, ...)` for future DOB
  - `assertAll` for grouped assertions on age components
  - `@ParameterizedTest` with `@CsvSource` for data-driven normal DOB tests
- Key test methods:
  - `testCalculateAge_normalDob_returnsCorrectAge()`
  - `testCalculateAge_leapYearDob_returnsCorrectAge()`
  - `testCalculateAge_dobIsToday_returnsZeroAge()`
  - `testCalculateAge_dobIsYesterday_returnsOneDayAge()`
  - `testCalculateAge_futureDob_throwsException()`
  - `testCalculateAge_nullDob_throwsException()`
  - `testCalculateAge_centuryBoundary_returnsCorrectAge()`
  - `testCalculateAge_monthEndTransition_returnsCorrectAge()`
  - `testCalculateAge_multipleNormalDobs_parameterized()`

**`src/test/java/com/agecalculator/util/DateValidatorTest.java`** — Input validation unit tests

- Test categories: valid parsing, invalid date rejection, format validation, future date detection
- Mock dependencies: None
- Assertions focus:
  - `assertEquals` on parsed `LocalDate` for valid inputs
  - `assertThrows(DateTimeParseException.class, ...)` for format errors
  - `assertThrows(DateTimeException.class, ...)` for impossible dates
  - `assertThrows(IllegalArgumentException.class, ...)` for future/null input
- Key test methods:
  - `testParse_validDate_returnsLocalDate()`
  - `testParse_leapYearDate_returnsLocalDate()`
  - `testParse_invalidDay31Feb_throwsException()`
  - `testParse_wrongFormat_throwsDateTimeParseException()`
  - `testParse_emptyString_throwsException()`
  - `testParse_nullInput_throwsException()`
  - `testParse_alphabeticInput_throwsException()`
  - `testValidate_futureDate_throwsException()`
  - `testValidate_todayDate_passes()`

**`src/test/java/com/agecalculator/model/AgeResultTest.java`** — Result model unit tests

- Test categories: construction, getters, toString formatting, edge values
- Mock dependencies: None
- Assertions focus:
  - `assertEquals` on getter return values
  - `assertEquals` on `toString()` output format
  - Constructor behavior with zero and large values
- Key test methods:
  - `testAgeResult_constructorAndGetters()`
  - `testAgeResult_zeroAge_toString()`
  - `testAgeResult_normalAge_toString()`
  - `testAgeResult_largeAge_toString()`
  - `testAgeResult_toStringFormat_matchesExpected()`

### 0.5.3 Test Configuration Updates

- **`pom.xml`:** Create with JUnit Jupiter 5.10.2 test dependency, Maven Surefire Plugin 3.5.5, JaCoCo Plugin 0.8.14, Java 17 compiler settings, and `maven-compiler-plugin` configuration
- **Coverage config:** JaCoCo configured with `prepare-agent` and `report` goals, line coverage minimum rule of 80%
- **Test runner config:** Surefire configured to auto-discover `*Test.java` classes in `src/test/java/`

### 0.5.4 Cross-File Test Dependencies

- **Shared fixtures:** None — each test class is self-contained with inline test data
- **Mock objects:** None required
- **Test utilities:** No shared test helper classes needed for this project scope
- **Import dependencies across test files:**
  - `AgeCalculatorTest.java` imports `AgeCalculator`, `AgeResult`, and `LocalDate`
  - `DateValidatorTest.java` imports `DateValidator` and `LocalDate`
  - `AgeResultTest.java` imports `AgeResult` only
  - All test files import `org.junit.jupiter.api.*` and `org.junit.jupiter.params.*` as needed


## 0.6 Dependency Inventory


### 0.6.1 Testing Dependencies

| Registry | Package Name | Version | Purpose |
|----------|-------------|---------|---------|
| Maven Central | `org.junit.jupiter:junit-jupiter` | 5.10.2 | JUnit 5 aggregator — includes junit-jupiter-api, junit-jupiter-engine, and junit-jupiter-params for writing and running tests |
| Maven Central | `org.apache.maven.plugins:maven-surefire-plugin` | 3.5.5 | Maven test execution plugin with native JUnit Platform provider |
| Maven Central | `org.jacoco:jacoco-maven-plugin` | 0.8.14 | Code coverage instrumentation, measurement, and reporting |
| Maven Central | `org.apache.maven.plugins:maven-compiler-plugin` | 3.11.0 | Java 17 source/target compilation configuration |

**Runtime Dependencies (source code, not test-specific):**

| Registry | Package Name | Version | Purpose |
|----------|-------------|---------|---------|
| JDK 17 stdlib | `java.time.LocalDate` | Built-in | Date representation for DOB and reference date |
| JDK 17 stdlib | `java.time.Period` | Built-in | Age calculation (years, months, days between dates) |
| JDK 17 stdlib | `java.time.format.DateTimeFormatter` | Built-in | Parsing DD/MM/YYYY input format |

No external runtime libraries beyond the Java 17 standard library are required. The application relies entirely on `java.time.*` as specified by the user.

### 0.6.2 Import Updates

Not applicable for this greenfield project — there are no existing files requiring import updates. All imports will be established fresh in every new source and test file.

**Import conventions to follow in new test files:**

- `AgeCalculatorTest.java`:
  - `import com.agecalculator.AgeCalculator;`
  - `import com.agecalculator.model.AgeResult;`
  - `import org.junit.jupiter.api.*;`
  - `import org.junit.jupiter.params.ParameterizedTest;`
  - `import org.junit.jupiter.params.provider.CsvSource;`
- `DateValidatorTest.java`:
  - `import com.agecalculator.util.DateValidator;`
  - `import org.junit.jupiter.api.*;`
  - `import org.junit.jupiter.params.ParameterizedTest;`
  - `import org.junit.jupiter.params.provider.ValueSource;`
- `AgeResultTest.java`:
  - `import com.agecalculator.model.AgeResult;`
  - `import org.junit.jupiter.api.*;`


## 0.7 Coverage and Quality Targets


### 0.7.1 Coverage Metrics

- **Current coverage:** 0% — no tests or source code exist yet
- **Target coverage:** ≥ 80% line coverage based on Java/JUnit industry standards for new projects
- **Coverage gaps to address (post-implementation targets):**

| Component | Target Coverage | Focus Areas |
|-----------|----------------|-------------|
| `AgeCalculator` | ≥ 90% | All branches in `calculateAge()` — normal path, leap year path, future date guard, null guard |
| `DateValidator` | ≥ 90% | All parsing branches — valid format, invalid format, impossible date, null/empty input, future date check |
| `AgeResult` | ≥ 85% | Constructor, all getters, `toString()` output formatting |
| `AgeCalculatorApp` (main) | Excluded | Console I/O entry point — not unit-testable without I/O redirection |

- **Per-file coverage targets:** Enforced via JaCoCo `check` goal with `BUNDLE`-level rule requiring minimum 80% line coverage

### 0.7.2 Test Quality Criteria

- **Assertion density:** Every test method must contain at least one meaningful assertion; parameterized tests must assert on all output components (years, months, days)
- **Test isolation:** Each test method runs independently with no shared mutable state; `@BeforeEach` creates fresh instances only
- **Performance constraints:** All unit tests must complete in under 5 seconds total — no network, database, or file system access
- **Maintainability standards:**
  - Test method names follow `test<Method>_<scenario>_<expectedResult>()` convention
  - Parameterized tests use `@CsvSource` or `@MethodSource` for data-driven coverage
  - Test classes mirror the source package structure (`com.agecalculator` → `com.agecalculator`)
  - `@DisplayName` annotations provide human-readable descriptions for test reports
- **Test naming convention:**
  - Test files: `<ClassName>Test.java` (e.g., `AgeCalculatorTest.java`)
  - Test methods: `test<Method>_<scenario>_<expectedBehavior>` (e.g., `testCalculateAge_normalDob_returnsCorrectAge`)


## 0.8 Scope Boundaries


### 0.8.1 Exhaustively In Scope

**New test files:**
- `src/test/java/com/agecalculator/AgeCalculatorTest.java` — all unit tests for age calculation logic
- `src/test/java/com/agecalculator/util/DateValidatorTest.java` — all unit tests for date parsing and validation
- `src/test/java/com/agecalculator/model/AgeResultTest.java` — all unit tests for the age result model

**New source files (required to enable testing):**
- `src/main/java/com/agecalculator/AgeCalculator.java` — core calculator class
- `src/main/java/com/agecalculator/util/DateValidator.java` — date validation utility
- `src/main/java/com/agecalculator/model/AgeResult.java` — OOP result model
- `src/main/java/com/agecalculator/AgeCalculatorApp.java` — main application entry point

**Build and test configuration:**
- `pom.xml` — Maven project descriptor with all dependencies and plugins

**Documentation updates:**
- `README.md` — Update with project description, build instructions, and test execution commands

### 0.8.2 Explicitly Out of Scope

- **GUI implementation** — The user listed Java Swing/JavaFX as optional enhancements; these are not part of the testing scope
- **Next birthday countdown** — Listed as optional enhancement; excluded from test scope
- **Total age in months/days display** — Listed as optional enhancement; excluded unless core logic supports it trivially
- **Integration tests** — No external services or databases exist; all testing is unit-level
- **End-to-end console I/O testing** — The `AgeCalculatorApp` main method relies on `Scanner`/`System.in` which is not in scope for unit tests
- **Performance optimization** — Not applicable for a simple calculator application
- **Deployment configuration** — No CI/CD pipeline or Docker configuration required
- **Any files or patterns listed in `.blitzyignore`** — No `.blitzyignore` files exist in this repository


## 0.9 Execution Parameters


### 0.9.1 Testing-Specific Instructions

- **Test execution command:**
  ```
  mvn clean test
  ```
- **Coverage measurement command:**
  ```
  mvn clean verify jacoco:report
  ```
  Coverage reports are generated at `target/site/jacoco/index.html`
- **Single test execution pattern:**
  ```
  mvn -Dtest=AgeCalculatorTest test
  mvn -Dtest=AgeCalculatorTest#testCalculateAge_normalDob_returnsCorrectAge test
  ```
- **Debug mode execution:**
  ```
  mvn -Dmaven.surefire.debug test
  ```
  This starts the JVM in debug mode on port 5005, waiting for a debugger to attach
- **Compile-only verification:**
  ```
  mvn clean compile test-compile
  ```

### 0.9.2 Test Patterns and Conventions

- **File discovery pattern:** Maven Surefire auto-discovers test classes matching `*Test.java`, `*Tests.java`, `Test*.java` in `src/test/java/`
- **Package structure mirrors source:** `src/test/java/com/agecalculator/` mirrors `src/main/java/com/agecalculator/`
- **No excluded test categories:** All tests run on every execution
- **Environment setup requirements:** Java 17 and Maven 3.8+ must be installed; no environment variables or external services required
- **Parallel execution:** Not configured — sequential execution is sufficient for this project size


## 0.10 Special Instructions for Testing


### 0.10.1 Testing-Specific Requirements

The following directives govern test implementation for this project:

- **OOP design mandate:** The user explicitly requires Object-Oriented Programming principles. The application must be structured into distinct classes (`AgeCalculator`, `DateValidator`, `AgeResult`) rather than a single monolithic class, and tests must target each class independently.
- **java.time API mandate:** Tests must exercise `java.time.LocalDate`, `java.time.Period`, and `java.time.format.DateTimeFormatter` specifically — alternative date libraries (e.g., Joda-Time) are not acceptable.
- **Exception handling mandate:** The user requires `try-catch` exception handling. Tests must verify that exceptions are thrown for invalid inputs (`DateTimeParseException`, `IllegalArgumentException`) and that error messages are meaningful.
- **Clean and readable standards:** Test code must follow consistent naming conventions, use descriptive `@DisplayName` annotations, and maintain clear separation between arrange/act/assert phases.
- **Deterministic test design:** Age calculation tests must use a fixed reference date (not `LocalDate.now()`) to ensure tests produce the same results regardless of when they are executed. The `AgeCalculator` class should accept an optional reference date parameter for testability.
- **All five user-specified test categories must be covered:**
  - Normal DOB (e.g., 15/08/1998)
  - Leap year DOB (29/02/2000)
  - Invalid date (31/02/2020)
  - Future date
  - Wrong format input
- **Leap year correctness:** Tests must explicitly verify that the application handles leap years correctly, including February 29 on leap years and the non-existence of February 29 on non-leap years (e.g., 29/02/2001 should be rejected).
- **Output format verification:** The exact output format `Your age is X years, Y months, and Z days.` must be validated in tests, matching the user's sample output specification.
- **No external mocking libraries:** The project's simplicity does not warrant Mockito or other mocking frameworks. Time-dependent behavior is controlled through method parameter injection.


