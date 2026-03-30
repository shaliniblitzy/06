package com.agecalculator.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utility class for parsing and validating date-of-birth input strings
 * in DD/MM/YYYY format.
 *
 * <p>This class provides two static operations:</p>
 * <ol>
 *   <li>{@link #parse(String)} — Converts a DD/MM/YYYY formatted string into
 *       a {@link LocalDate} using strict validation that rejects impossible
 *       calendar dates (e.g., 31/02/2020, 29/02/2001).</li>
 *   <li>{@link #validate(LocalDate)} — Ensures a date of birth is not in
 *       the future relative to the current system date.</li>
 * </ol>
 *
 * <p>This class cannot be instantiated — all methods are static. It follows
 * the Single Responsibility Principle by handling only date parsing and
 * validation, leaving age calculation to {@code AgeCalculator} and output
 * formatting to {@code AgeResult}.</p>
 *
 * <p>The internal {@link DateTimeFormatter} uses {@link ResolverStyle#STRICT}
 * with the {@code "dd/MM/uuuu"} pattern to enforce strict calendar validation.
 * The {@code uuuu} (proleptic year) pattern is required because
 * {@code yyyy} is only valid under {@link ResolverStyle#SMART} mode.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *   // Parse and validate a date of birth
 *   LocalDate dob = DateValidator.parse("15/08/1998");
 *   DateValidator.validate(dob);
 *
 *   // Invalid date — throws DateTimeParseException
 *   DateValidator.parse("31/02/2020"); // February never has 31 days
 *
 *   // Wrong format — throws DateTimeParseException
 *   DateValidator.parse("1998-08-15"); // Expected DD/MM/YYYY
 *
 *   // Null input — throws IllegalArgumentException
 *   DateValidator.parse(null);
 *
 *   // Future date — throws IllegalArgumentException
 *   DateValidator.validate(LocalDate.now().plusDays(1));
 * }</pre>
 *
 * @see java.time.LocalDate
 * @see java.time.format.DateTimeFormatter
 * @see java.time.format.ResolverStyle
 */
public class DateValidator {

    /**
     * Strict date formatter for the DD/MM/YYYY pattern.
     *
     * <p>Uses {@link ResolverStyle#STRICT} to reject impossible calendar dates
     * such as 31/02/2020 (February never has 31 days) and 29/02/2001
     * (2001 is not a leap year). Uses the {@code "uuuu"} proleptic year
     * pattern instead of {@code "yyyy"} because strict mode requires it.</p>
     *
     * <p>Accepted examples: {@code "15/08/1998"}, {@code "29/02/2000"},
     * {@code "01/01/2000"}.</p>
     *
     * <p>Rejected examples: {@code "31/02/2020"}, {@code "29/02/2001"},
     * {@code "1998-08-15"}, {@code "abc"}, {@code ""}.</p>
     */
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Private constructor to prevent instantiation of this utility class.
     *
     * <p>All methods in this class are static. Attempting to create an instance
     * via reflection will succeed but is not supported.</p>
     */
    private DateValidator() {
    }

    /**
     * Parses a date string in DD/MM/YYYY format into a {@link LocalDate}.
     *
     * <p>Uses strict resolution to reject impossible calendar dates
     * (e.g., 31/02/2020, 29/02/2001). The method does not catch any
     * {@link DateTimeParseException} — it propagates naturally to the caller,
     * which is responsible for handling parse failures in its own
     * {@code try-catch} block.</p>
     *
     * <p>Both malformed format strings (e.g., {@code "1998-08-15"},
     * {@code "abc"}, {@code ""}) and impossible dates (e.g.,
     * {@code "31/02/2020"}, {@code "00/13/2000"}) result in a
     * {@link DateTimeParseException} being thrown by the underlying
     * {@link LocalDate#parse(CharSequence, DateTimeFormatter)} call under
     * {@link ResolverStyle#STRICT} mode.</p>
     *
     * @param input the date string in DD/MM/YYYY format; must not be {@code null}
     * @return the parsed {@link LocalDate} representing the date
     * @throws IllegalArgumentException if {@code input} is {@code null}
     * @throws DateTimeParseException   if the input cannot be parsed as a valid
     *                                  date in DD/MM/YYYY format, or if the date
     *                                  represents an impossible calendar date
     */
    public static LocalDate parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Input date string must not be null.");
        }
        return LocalDate.parse(input, FORMATTER);
    }

    /**
     * Validates that the given date of birth is not in the future.
     *
     * <p>A date of birth equal to today's date is considered valid — a person
     * born today has an age of zero years, zero months, and zero days. Only
     * dates strictly after the current system date are rejected.</p>
     *
     * <p>This method uses {@link LocalDate#now()} to determine the current
     * date and {@link LocalDate#isAfter(java.time.chrono.ChronoLocalDate)}
     * for the comparison, ensuring that today's date passes validation.</p>
     *
     * @param dob the date of birth to validate; must not be {@code null}
     * @throws NullPointerException     if {@code dob} is {@code null}
     * @throws IllegalArgumentException if {@code dob} is after the current date
     */
    public static void validate(LocalDate dob) {
        if (dob == null) {
            throw new NullPointerException("Date of birth must not be null.");
        }
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
    }
}
