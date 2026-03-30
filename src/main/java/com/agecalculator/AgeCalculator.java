package com.agecalculator;

import com.agecalculator.model.AgeResult;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Core business logic class that computes a user's exact age in years,
 * months, and days using the {@link java.time} API.
 *
 * <p>This class follows the Single Responsibility Principle — it is solely
 * responsible for age calculation logic. Input parsing is handled by
 * {@code DateValidator}, result formatting is handled by {@link AgeResult},
 * and console I/O is handled by {@code AgeCalculatorApp}.</p>
 *
 * <p>Two overloaded {@code calculateAge} methods are provided:</p>
 * <ul>
 *   <li>{@link #calculateAge(LocalDate)} — convenience method that uses the
 *       current system date as the reference date (production use)</li>
 *   <li>{@link #calculateAge(LocalDate, LocalDate)} — testable method that
 *       accepts an explicit reference date for deterministic unit testing</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *   AgeCalculator calculator = new AgeCalculator();
 *   AgeResult result = calculator.calculateAge(
 *       LocalDate.of(1998, 8, 15),
 *       LocalDate.of(2025, 3, 30)
 *   );
 *   System.out.println(result);
 *   // Output: Your age is 26 years, 7 months, and 15 days.
 * }</pre>
 *
 * @see AgeResult
 * @see java.time.Period
 * @see java.time.LocalDate
 */
public class AgeCalculator {

    /**
     * Calculates age from the given date of birth relative to the current
     * system date.
     *
     * <p>This is a convenience method that delegates to
     * {@link #calculateAge(LocalDate, LocalDate)} with
     * {@link LocalDate#now()} as the reference date. It is intended for
     * production use where the current date is the natural reference point.</p>
     *
     * @param dob the date of birth; must not be {@code null} and must not
     *            be after the current system date
     * @return an {@link AgeResult} containing the computed years, months,
     *         and days
     * @throws NullPointerException     if {@code dob} is {@code null}
     * @throws IllegalArgumentException if {@code dob} is in the future
     *                                  relative to the current system date
     */
    public AgeResult calculateAge(LocalDate dob) {
        return calculateAge(dob, LocalDate.now());
    }

    /**
     * Calculates age from the given date of birth relative to an explicit
     * reference date.
     *
     * <p>This overload enables deterministic unit testing by accepting a
     * fixed reference date instead of relying on {@link LocalDate#now()}.
     * The age is computed using {@link Period#between(LocalDate, LocalDate)}
     * and the resulting years, months, and days are encapsulated in an
     * {@link AgeResult}.</p>
     *
     * <p>Validation is performed before calculation:</p>
     * <ol>
     *   <li>Both {@code dob} and {@code referenceDate} must be non-null</li>
     *   <li>{@code dob} must not be after {@code referenceDate} (i.e., the
     *       date of birth cannot be in the future relative to the reference)</li>
     * </ol>
     *
     * @param dob           the date of birth; must not be {@code null}
     * @param referenceDate the reference date to calculate age against;
     *                      must not be {@code null}
     * @return an {@link AgeResult} containing the computed years, months,
     *         and days
     * @throws NullPointerException     if {@code dob} is {@code null}
     * @throws NullPointerException     if {@code referenceDate} is {@code null}
     * @throws IllegalArgumentException if {@code dob} is after
     *                                  {@code referenceDate}
     */
    public AgeResult calculateAge(LocalDate dob, LocalDate referenceDate) {
        // Validate that the date of birth is not null
        Objects.requireNonNull(dob, "Date of birth must not be null.");

        // Validate that the reference date is not null
        Objects.requireNonNull(referenceDate, "Reference date must not be null.");

        // Validate that the date of birth is not in the future relative to the reference date
        if (dob.isAfter(referenceDate)) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }

        // Calculate the period between the date of birth and the reference date
        Period period = Period.between(dob, referenceDate);

        // Return an AgeResult encapsulating the computed age components
        return new AgeResult(period.getYears(), period.getMonths(), period.getDays());
    }
}
