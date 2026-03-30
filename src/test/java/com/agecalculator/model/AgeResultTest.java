package com.agecalculator.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive JUnit 5 unit tests for the {@link AgeResult} immutable data model.
 *
 * <p>This test class validates the constructor, getter methods, and
 * {@code toString()} output formatting of the {@code AgeResult} POJO.
 * Test scenarios cover normal values, zero edge case (DOB equals today),
 * large values (100+ years), and exact output format matching against
 * the user-specified pattern: {@code "Your age is X years, Y months, and Z days."}</p>
 *
 * <p>Each test method is fully self-contained with no shared mutable state.
 * All assertions are deterministic and independent of the system clock.</p>
 */
@DisplayName("AgeResult Model Unit Tests")
class AgeResultTest {

    /**
     * Verifies that the three-argument constructor correctly stores all
     * age components and that each getter returns the expected value.
     *
     * <p>Uses {@code assertAll} to group related assertions so that all
     * three getters are checked even if one fails, providing complete
     * diagnostic information in a single test run.</p>
     *
     * <p>Values 27, 6, 15 match the user's example output from the AAP:
     * {@code "Your age is 27 years, 6 months, and 15 days."}</p>
     */
    @Test
    @DisplayName("Constructor sets all fields and getters return correct values")
    void testAgeResult_constructorAndGetters() {
        // Arrange: construct with the user's example values
        AgeResult result = new AgeResult(27, 6, 15);

        // Assert: all three getters return the values passed to the constructor
        assertAll(
            () -> assertEquals(27, result.getYears(), "Years should be 27"),
            () -> assertEquals(6, result.getMonths(), "Months should be 6"),
            () -> assertEquals(15, result.getDays(), "Days should be 15")
        );
    }

    /**
     * Verifies that {@code toString()} returns the correct formatted string
     * when all age components are zero — the edge case where the DOB equals
     * the reference date (i.e., a newborn or same-day calculation).
     *
     * <p>Expected output: {@code "Your age is 0 years, 0 months, and 0 days."}</p>
     */
    @Test
    @DisplayName("toString returns correct format for zero age (0,0,0)")
    void testAgeResult_zeroAge_toString() {
        // Arrange: construct with all-zero components (DOB = today edge case)
        AgeResult result = new AgeResult(0, 0, 0);

        // Act
        String output = result.toString();

        // Assert: exact string match for zero-age boundary
        assertEquals("Your age is 0 years, 0 months, and 0 days.", output);
    }

    /**
     * Verifies that {@code toString()} returns the correct formatted string
     * for a typical, non-zero age. The values 27, 6, 15 exactly match the
     * user-provided example output from AAP §0.1.2.
     *
     * <p>Expected output: {@code "Your age is 27 years, 6 months, and 15 days."}</p>
     *
     * <p>Format verification points:</p>
     * <ul>
     *   <li>Starts with {@code "Your age is "}</li>
     *   <li>Years value followed by {@code " years, "}</li>
     *   <li>Months value followed by {@code " months, and "}</li>
     *   <li>Days value followed by {@code " days."}</li>
     *   <li>Uses Oxford comma style: {@code "X years, Y months, and Z days."}</li>
     * </ul>
     */
    @Test
    @DisplayName("toString returns correct format for normal age (27, 6, 15)")
    void testAgeResult_normalAge_toString() {
        // Arrange: construct with the user's example values
        AgeResult result = new AgeResult(27, 6, 15);

        // Act
        String output = result.toString();

        // Assert: exact match against the user-specified example output
        assertEquals("Your age is 27 years, 6 months, and 15 days.", output);
    }

    /**
     * Verifies that {@code toString()} handles large age values (100+ years)
     * without truncation, overflow, or formatting errors.
     *
     * <p>Expected output: {@code "Your age is 105 years, 11 months, and 30 days."}</p>
     */
    @Test
    @DisplayName("toString returns correct format for large age (100+ years)")
    void testAgeResult_largeAge_toString() {
        // Arrange: construct with a centenarian-plus age
        AgeResult result = new AgeResult(105, 11, 30);

        // Act
        String output = result.toString();

        // Assert: large values render correctly with no truncation
        assertEquals("Your age is 105 years, 11 months, and 30 days.", output);
    }

    /**
     * Provides additional format verification using singular values (1, 1, 1)
     * to confirm that the output format does NOT change for singular quantities.
     * The user-specified format consistently uses plural form: {@code "X years,
     * Y months, and Z days."} — even when values are 1.
     *
     * <p>Also verifies structural properties of the output string:</p>
     * <ul>
     *   <li>Starts with {@code "Your age is "}</li>
     *   <li>Ends with {@code " days."}</li>
     * </ul>
     */
    @Test
    @DisplayName("toString format matches exact expected pattern")
    void testAgeResult_toStringFormat_matchesExpected() {
        // Arrange: construct with singular values to verify no singular/plural logic
        AgeResult result = new AgeResult(1, 1, 1);

        // Act
        String output = result.toString();

        // Assert: exact string match — plural form is used even for singular values
        assertEquals("Your age is 1 years, 1 months, and 1 days.", output);

        // Assert: structural prefix and suffix verification
        assertTrue(output.startsWith("Your age is "), "Output should start with 'Your age is '");
        assertTrue(output.endsWith(" days."), "Output should end with ' days.'");
    }
}
