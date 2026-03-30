# Age Calculator

A Java application that computes a user's exact age in years, months, and days from a Date of Birth (DOB) entered in `DD/MM/YYYY` format. Built using Java 17 and the `java.time` API (`LocalDate`, `Period`, `DateTimeFormatter`).

## Features

- Calculate exact age in years, months, and days
- Input validation for DD/MM/YYYY format
- Proper handling of leap year dates (e.g., February 29)
- Rejection of future dates with meaningful error messages
- Rejection of invalid/impossible calendar dates (e.g., 31/02/2020)
- Object-Oriented design with separate classes for calculation, validation, and result modeling

## Project Structure

```text
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── com
    │           └── agecalculator
    │               ├── AgeCalculator.java
    │               ├── AgeCalculatorApp.java
    │               ├── model
    │               │   └── AgeResult.java
    │               └── util
    │                   └── DateValidator.java
    └── test
        └── java
            └── com
                └── agecalculator
                    ├── AgeCalculatorTest.java
                    ├── model
                    │   └── AgeResultTest.java
                    └── util
                        └── DateValidatorTest.java
```

## Prerequisites

- Java 17 or higher
- Apache Maven 3.8+

## Build and Run

```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.agecalculator.AgeCalculatorApp"
```

## Test Execution

```bash
# Run all tests
mvn clean test

# Run a specific test class
mvn -Dtest=AgeCalculatorTest test

# Run a specific test method
mvn -Dtest=AgeCalculatorTest#testCalculateAge_normalDob_returnsCorrectAge test

# Run tests with coverage report
mvn clean verify jacoco:report
```

Coverage reports generated at `target/site/jacoco/index.html`.

## Usage Example

```text
Enter your Date of Birth (DD/MM/YYYY): 15/08/1998
Your age is 27 years, 6 months, and 15 days.
```

## Testing

- **Framework:** JUnit 5 (Jupiter) 5.10.2
- **Test categories:** Normal DOB, Leap Year DOB, Invalid Date, Future Date, Wrong Format
- **Coverage target:** ≥ 80% line coverage (enforced by JaCoCo)
