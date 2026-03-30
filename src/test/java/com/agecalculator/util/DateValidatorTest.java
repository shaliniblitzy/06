package com.agecalculator.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 unit tests for {@link DateValidator}.
 *
 * <p>This test class validates two public static methods:</p>
 * <ul>
 *   <li>{@link DateValidator#parse(String)} — Parses DD/MM/YYYY formatted strings
 *       into {@link LocalDate} instances with strict calendar validation.</li>
 *   <li>{@link DateValidator#validate(LocalDate)} — Ensures a date of birth is not
 *       in the future relative to the current system date.</li>
 * </ul>
 *
 * <p>Test categories cover all five user-specified scenarios:</p>
 * <ol>
 *   <li>Normal DOB parsing (valid DD/MM/YYYY dates)</li>
 *   <li>Leap year DOB handling (Feb 29 on leap and non-leap years)</li>
 *   <li>Invalid date rejection (impossible calendar dates)</li>
 *   <li>Future date rejection (DOB after current date)</li>
 *   <li>Wrong format input rejection (malformed strings, alphabetic, empty, null)</li>
 * </ol>
 *
 * <p>All tests are fully isolated with no shared mutable state. Since
 * {@link DateValidator} exposes only static methods, no instance setup
 * or {@code @BeforeEach} is required.</p>
 */
@DisplayName("DateValidator Unit Tests")
class DateValidatorTest {

    // =========================================================================
    // parse() — Happy Path Tests
    // =========================================================================

    /**
     * Verifies that a standard valid date string in DD/MM/YYYY format
     * is correctly parsed into the corresponding {@link LocalDate}.
     * This is the primary happy-path test matching the user example (15/08/1998).
     */
    @Test
    @DisplayName("Parse valid date 15/08/1998 returns correct LocalDate")
    void testParse_validDate_returnsLocalDate() {
        // Act
        LocalDate result = DateValidator.parse("15/08/1998");

        // Assert
        assertEquals(LocalDate.of(1998, 8, 15), result);
    }

    /**
     * Verifies that February 29 on a leap year (2000) is correctly parsed.
     * Year 2000 is a leap year (divisible by 400), so Feb 29 is valid.
     */
    @Test
    @DisplayName("Parse leap year date 29/02/2000 returns correct LocalDate")
    void testParse_leapYearDate_returnsLocalDate() {
        // Act
        LocalDate result = DateValidator.parse("29/02/2000");

        // Assert
        assertEquals(LocalDate.of(2000, 2, 29), result);
    }

    /**
     * Verifies that the first day of the year (01/01) with leading zeros
     * is correctly parsed. Tests boundary at year start.
     */
    @Test
    @DisplayName("Parse first day of year 01/01/2000 returns correct LocalDate")
    void testParse_firstDayOfYear_returnsLocalDate() {
        // Act
        LocalDate result = DateValidator.parse("01/01/2000");

        // Assert
        assertEquals(LocalDate.of(2000, 1, 1), result);
    }

    /**
     * Verifies that the last day of the year (31/12) is correctly parsed.
     * Tests boundary at year end with maximum day and month values.
     */
    @Test
    @DisplayName("Parse last day of year 31/12/2000 returns correct LocalDate")
    void testParse_lastDayOfYear_returnsLocalDate() {
        // Act
        LocalDate result = DateValidator.parse("31/12/2000");

        // Assert
        assertEquals(LocalDate.of(2000, 12, 31), result);
    }

    // =========================================================================
    // parse() — Invalid Date Tests (Impossible Calendar Dates)
    // =========================================================================

    /**
     * Verifies that 31/02/2020 (February 31) is rejected as an impossible
     * calendar date. February never has 31 days in any year. The STRICT
     * resolver in {@link DateValidator} should throw {@link DateTimeParseException}.
     */
    @Test
    @DisplayName("Parse impossible date 31/02/2020 throws DateTimeParseException")
    void testParse_invalidDay31Feb_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse("31/02/2020"));
    }

    /**
     * Verifies that 29/02/2001 (February 29 on a non-leap year) is rejected.
     * 2001 is NOT a leap year (not divisible by 4), so February 29 does not
     * exist. The STRICT resolver rejects this impossible date.
     */
    @Test
    @DisplayName("Parse 29/02/2001 (non-leap year) throws DateTimeParseException")
    void testParse_invalidDay29FebNonLeapYear_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse("29/02/2001"));
    }

    /**
     * Verifies that day 32 (32/01/2020) is rejected as impossible.
     * No month in any year has 32 days. The STRICT resolver rejects this.
     */
    @Test
    @DisplayName("Parse impossible day 32/01/2020 throws DateTimeParseException")
    void testParse_invalidDay32_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse("32/01/2020"));
    }

    /**
     * Verifies that month 13 (00/13/2000) is rejected as impossible.
     * There are only 12 months in the Gregorian calendar. The STRICT resolver
     * rejects this impossible date.
     */
    @Test
    @DisplayName("Parse impossible month 00/13/2000 throws DateTimeParseException")
    void testParse_invalidMonth13_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse("00/13/2000"));
    }

    // =========================================================================
    // parse() — Wrong Format Tests (Malformed Input Strings)
    // =========================================================================

    /**
     * Parameterized test verifying that various wrong date format strings
     * are rejected with {@link DateTimeParseException}. Covers:
     * <ul>
     *   <li>ISO format: {@code 1998-08-15}</li>
     *   <li>Dash-separated: {@code 15-08-1998}</li>
     *   <li>US format (month/day swapped): {@code 08/15/1998}</li>
     *   <li>Reversed: {@code 1998/08/15}</li>
     *   <li>Dot-separated European format: {@code 15.08.1998}</li>
     * </ul>
     *
     * @param input the malformed date string to parse
     */
    @ParameterizedTest
    @ValueSource(strings = {"1998-08-15", "15-08-1998", "08/15/1998", "1998/08/15", "15.08.1998"})
    @DisplayName("Parse wrong date format throws DateTimeParseException")
    void testParse_wrongFormat_throwsDateTimeParseException(String input) {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse(input));
    }

    /**
     * Verifies that purely alphabetic input ({@code "abc"}) is rejected
     * with {@link DateTimeParseException}. Non-numeric strings cannot be
     * parsed as dates in any format.
     */
    @Test
    @DisplayName("Parse alphabetic input throws DateTimeParseException")
    void testParse_alphabeticInput_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse("abc"));
    }

    /**
     * Verifies that an empty string is rejected with {@link DateTimeParseException}.
     * An empty string has no date content to parse.
     */
    @Test
    @DisplayName("Parse empty string throws DateTimeParseException")
    void testParse_emptyString_throwsException() {
        // Act & Assert
        assertThrows(DateTimeParseException.class,
                () -> DateValidator.parse(""));
    }

    // =========================================================================
    // parse() — Null Input Test
    // =========================================================================

    /**
     * Verifies that null input is rejected with {@link IllegalArgumentException}
     * (NOT {@link DateTimeParseException}). The {@link DateValidator#parse(String)}
     * method has an explicit null guard that throws {@link IllegalArgumentException}
     * with the message {@code "Input date string must not be null."}.
     */
    @Test
    @DisplayName("Parse null input throws IllegalArgumentException")
    void testParse_nullInput_throwsException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateValidator.parse(null)
        );
        assertEquals("Input date string must not be null.", exception.getMessage());
    }

    // =========================================================================
    // validate() — Future Date Test
    // =========================================================================

    /**
     * Verifies that a future date of birth (tomorrow) is rejected with
     * {@link IllegalArgumentException}. Uses {@code LocalDate.now().plusDays(1)}
     * to guarantee the date is always in the future regardless of test run date.
     * The exception message must exactly match
     * {@code "Date of birth cannot be in the future."}.
     */
    @Test
    @DisplayName("Validate future date throws IllegalArgumentException")
    void testValidate_futureDate_throwsException() {
        // Arrange
        LocalDate futureDob = LocalDate.now().plusDays(1);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DateValidator.validate(futureDob)
        );
        assertEquals("Date of birth cannot be in the future.", exception.getMessage());
    }

    // =========================================================================
    // validate() — Boundary and Happy Path Tests
    // =========================================================================

    /**
     * Verifies that today's date is accepted as a valid date of birth.
     * The boundary condition (DOB = today) should pass validation — a person
     * born today has an age of zero years, zero months, and zero days.
     * {@link DateValidator#validate(LocalDate)} uses {@code isAfter()} which
     * returns {@code false} for today's date.
     */
    @Test
    @DisplayName("Validate today's date does not throw")
    void testValidate_todayDate_passes() {
        // Arrange
        LocalDate today = LocalDate.now();

        // Act & Assert
        assertDoesNotThrow(() -> DateValidator.validate(today));
    }

    /**
     * Verifies that a past date (15/08/1998) is accepted as a valid date
     * of birth. Past dates should always pass validation without throwing
     * any exception.
     */
    @Test
    @DisplayName("Validate past date does not throw")
    void testValidate_pastDate_passes() {
        // Arrange
        LocalDate pastDate = LocalDate.of(1998, 8, 15);

        // Act & Assert
        assertDoesNotThrow(() -> DateValidator.validate(pastDate));
    }

    // =========================================================================
    // validate() — Null DOB Test
    // =========================================================================

    /**
     * Verifies that a null date of birth is rejected with
     * {@link NullPointerException}. The {@link DateValidator#validate(LocalDate)}
     * method has an explicit null check that throws {@link NullPointerException}
     * with the message {@code "Date of birth must not be null."}.
     */
    @Test
    @DisplayName("Validate null DOB throws NullPointerException")
    void testValidate_nullDob_throwsException() {
        // Act & Assert
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> DateValidator.validate(null)
        );
        assertEquals("Date of birth must not be null.", exception.getMessage());
    }
}
