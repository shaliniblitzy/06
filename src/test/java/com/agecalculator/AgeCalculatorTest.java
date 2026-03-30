package com.agecalculator;

import com.agecalculator.model.AgeResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 unit tests for {@link AgeCalculator}.
 *
 * <p>This test class validates the core age calculation logic across
 * the five user-specified test categories:</p>
 * <ol>
 *   <li><b>Normal DOB</b> — standard date of birth calculations</li>
 *   <li><b>Leap year DOB</b> — February 29 edge cases</li>
 *   <li><b>Boundary conditions</b> — DOB equal to today, yesterday, century
 *       boundaries, month-end transitions</li>
 *   <li><b>Future date rejection</b> — DOB after the reference date</li>
 *   <li><b>Null handling</b> — null DOB triggers NullPointerException</li>
 * </ol>
 *
 * <p>All tests use a fixed reference date ({@code 2025-03-30}) to ensure
 * deterministic, reproducible results. The two-argument overload
 * {@link AgeCalculator#calculateAge(LocalDate, LocalDate)} is called
 * exclusively — the single-argument convenience method that depends on
 * {@link LocalDate#now()} is never invoked directly.</p>
 *
 * <p>Expected values for every assertion were empirically verified
 * against Java 17's {@link java.time.Period#between(LocalDate, LocalDate)}
 * implementation.</p>
 */
@DisplayName("AgeCalculator Unit Tests")
class AgeCalculatorTest {

    /**
     * Fixed reference date used by all tests for deterministic assertions.
     * Chosen as 2025-03-30 to match the AAP specification.
     */
    private static final LocalDate REFERENCE_DATE = LocalDate.of(2025, 3, 30);

    /** The instance under test, recreated before every test method. */
    private AgeCalculator calculator;

    /**
     * Creates a fresh {@link AgeCalculator} instance before each test,
     * guaranteeing complete test isolation with no shared mutable state.
     */
    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator();
    }

    // ------------------------------------------------------------------ //
    //  3.1 — Normal DOB Tests                                            //
    // ------------------------------------------------------------------ //

    /**
     * Verifies correct age computation for a standard date of birth
     * (15 August 1998) against the fixed reference date (30 March 2025).
     *
     * <p>Empirically verified: {@code Period.between(1998-08-15, 2025-03-30)}
     * yields 26 years, 7 months, 15 days.</p>
     */
    @Test
    @DisplayName("Calculate age for normal DOB 15/08/1998")
    void testCalculateAge_normalDob_returnsCorrectAge() {
        // Arrange
        LocalDate dob = LocalDate.of(1998, 8, 15);

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(26, result.getYears(), "Years component should be 26"),
                () -> assertEquals(7, result.getMonths(), "Months component should be 7"),
                () -> assertEquals(15, result.getDays(), "Days component should be 15")
        );
    }

    // ------------------------------------------------------------------ //
    //  3.2 — Leap Year DOB Tests                                         //
    // ------------------------------------------------------------------ //

    /**
     * Verifies correct age computation for a leap-year date of birth
     * (29 February 2000) against the fixed reference date (30 March 2025).
     *
     * <p>2025 is <em>not</em> a leap year, so Java's {@code Period.between}
     * adjusts 29 Feb to the last day of February (28 Feb) in the target
     * year when computing full years, then adds the remaining months and
     * days.</p>
     *
     * <p>Empirically verified: {@code Period.between(2000-02-29, 2025-03-30)}
     * yields 25 years, 1 month, 1 day.</p>
     */
    @Test
    @DisplayName("Calculate age for leap year DOB 29/02/2000")
    void testCalculateAge_leapYearDob_returnsCorrectAge() {
        // Arrange
        LocalDate dob = LocalDate.of(2000, 2, 29);

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(25, result.getYears(), "Years component should be 25"),
                () -> assertEquals(1, result.getMonths(), "Months component should be 1"),
                () -> assertEquals(1, result.getDays(), "Days component should be 1")
        );
    }

    // ------------------------------------------------------------------ //
    //  3.3 — Boundary Condition Tests                                    //
    // ------------------------------------------------------------------ //

    /**
     * Verifies that when the date of birth equals the reference date the
     * resulting age is exactly zero years, zero months, and zero days.
     */
    @Test
    @DisplayName("DOB equal to reference date returns zero age")
    void testCalculateAge_dobIsToday_returnsZeroAge() {
        // Arrange
        LocalDate dob = REFERENCE_DATE;

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(0, result.getYears(), "Years should be 0"),
                () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                () -> assertEquals(0, result.getDays(), "Days should be 0")
        );
    }

    /**
     * Verifies that a date of birth exactly one day before the reference
     * date produces an age of 0 years, 0 months, and 1 day.
     */
    @Test
    @DisplayName("DOB one day before reference date returns 0 years, 0 months, 1 day")
    void testCalculateAge_dobIsYesterday_returnsOneDayAge() {
        // Arrange
        LocalDate dob = REFERENCE_DATE.minusDays(1);

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(0, result.getYears(), "Years should be 0"),
                () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                () -> assertEquals(1, result.getDays(), "Days should be 1")
        );
    }

    /**
     * Verifies correct age computation for a date of birth that spans
     * a century boundary (31 December 1999 → 30 March 2025).
     *
     * <p>Empirically verified: {@code Period.between(1999-12-31, 2025-03-30)}
     * yields 25 years, 2 months, 30 days.</p>
     */
    @Test
    @DisplayName("Calculate age spanning century boundary")
    void testCalculateAge_centuryBoundary_returnsCorrectAge() {
        // Arrange
        LocalDate dob = LocalDate.of(1999, 12, 31);

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(25, result.getYears(), "Years component should be 25"),
                () -> assertEquals(2, result.getMonths(), "Months component should be 2"),
                () -> assertEquals(30, result.getDays(), "Days component should be 30")
        );
    }

    /**
     * Verifies correct age computation when the date of birth falls on
     * the 31st of a month and the reference date falls in a shorter month
     * (January 31 → February 28), exercising month-end transition logic
     * in {@link java.time.Period#between(LocalDate, LocalDate)}.
     *
     * <p>Empirically verified: {@code Period.between(2025-01-31, 2025-02-28)}
     * yields 0 years, 0 months, 28 days.</p>
     */
    @Test
    @DisplayName("Calculate age with month-end transition (31 Jan to 28 Feb)")
    void testCalculateAge_monthEndTransition_returnsCorrectAge() {
        // Arrange
        LocalDate dob = LocalDate.of(2025, 1, 31);
        LocalDate referenceDate = LocalDate.of(2025, 2, 28);

        // Act
        AgeResult result = calculator.calculateAge(dob, referenceDate);

        // Assert
        assertAll(
                () -> assertEquals(0, result.getYears(), "Years should be 0"),
                () -> assertEquals(0, result.getMonths(), "Months should be 0"),
                () -> assertEquals(28, result.getDays(), "Days should be 28")
        );
    }

    // ------------------------------------------------------------------ //
    //  3.4 — Error Handling Tests                                        //
    // ------------------------------------------------------------------ //

    /**
     * Verifies that passing a future date of birth (one day after the
     * reference date) throws an {@link IllegalArgumentException} with
     * the exact message {@code "Date of birth cannot be in the future."}.
     */
    @Test
    @DisplayName("Future DOB throws IllegalArgumentException")
    void testCalculateAge_futureDob_throwsException() {
        // Arrange
        LocalDate futureDob = REFERENCE_DATE.plusDays(1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculateAge(futureDob, REFERENCE_DATE)
        );
        assertEquals("Date of birth cannot be in the future.", exception.getMessage());
    }

    /**
     * Verifies that passing a {@code null} date of birth throws a
     * {@link NullPointerException}, exercising the null-guard at the
     * start of {@link AgeCalculator#calculateAge(LocalDate, LocalDate)}.
     */
    @Test
    @DisplayName("Null DOB throws NullPointerException")
    void testCalculateAge_nullDob_throwsException() {
        // Act & Assert
        assertThrows(
                NullPointerException.class,
                () -> calculator.calculateAge(null, REFERENCE_DATE)
        );
    }

    // ------------------------------------------------------------------ //
    //  3.5 — Parameterized Tests                                         //
    // ------------------------------------------------------------------ //

    /**
     * Data-driven test that verifies age calculation for multiple distinct
     * dates of birth against the fixed reference date ({@code 2025-03-30}).
     *
     * <p>Each row supplies the DOB components (year, month, day) and the
     * expected age components (years, months, days). All expected values
     * have been empirically verified against Java 17's
     * {@link java.time.Period#between(LocalDate, LocalDate)}.</p>
     *
     * @param year           DOB year
     * @param month          DOB month
     * @param day            DOB day
     * @param expectedYears  expected years component
     * @param expectedMonths expected months component
     * @param expectedDays   expected days component
     */
    @ParameterizedTest(name = "DOB {0}-{1}-{2} → age {3}y {4}m {5}d")
    @DisplayName("Parameterized age calculation for multiple DOBs")
    @CsvSource({
            "2000, 1,  1,  25, 2, 29",   // Jan 1 2000 → Mar 30 2025
            "1990, 6, 15,  34, 9, 15",   // Jun 15 1990 → Mar 30 2025
            "2024, 3, 30,   1, 0,  0",   // Exactly 1 year before reference
            "2025, 3, 30,   0, 0,  0"    // Same day as reference (zero age)
    })
    void testCalculateAge_multipleNormalDobs_parameterized(
            int year, int month, int day,
            int expectedYears, int expectedMonths, int expectedDays) {
        // Arrange
        LocalDate dob = LocalDate.of(year, month, day);

        // Act
        AgeResult result = calculator.calculateAge(dob, REFERENCE_DATE);

        // Assert
        assertAll(
                () -> assertEquals(expectedYears, result.getYears(),
                        "Years mismatch for DOB " + dob),
                () -> assertEquals(expectedMonths, result.getMonths(),
                        "Months mismatch for DOB " + dob),
                () -> assertEquals(expectedDays, result.getDays(),
                        "Days mismatch for DOB " + dob)
        );
    }
}
