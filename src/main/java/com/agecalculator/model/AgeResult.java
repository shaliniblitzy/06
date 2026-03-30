package com.agecalculator.model;

/**
 * Immutable result model that encapsulates a computed age
 * in years, months, and days.
 *
 * <p>This class follows the Single Responsibility Principle — it is solely
 * responsible for holding the age computation result and providing a
 * human-readable formatted string representation.</p>
 *
 * <p>Instances of this class are immutable: once constructed, the age
 * components cannot be changed. Thread safety is guaranteed by immutability.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *   AgeResult result = new AgeResult(27, 6, 15);
 *   System.out.println(result);
 *   // Output: Your age is 27 years, 6 months, and 15 days.
 * }</pre>
 *
 * @see java.time.Period
 */
public class AgeResult {

    /** The number of complete years in the computed age. */
    private final int years;

    /** The number of remaining complete months in the computed age. */
    private final int months;

    /** The number of remaining days in the computed age. */
    private final int days;

    /**
     * Constructs an {@code AgeResult} with the specified age components.
     *
     * <p>No validation is performed on the parameters — the calling
     * {@code AgeCalculator} class is responsible for producing valid
     * {@link java.time.Period} values before constructing this object.</p>
     *
     * @param years  the number of complete years in the age
     * @param months the number of remaining complete months (0–11)
     * @param days   the number of remaining days (0–30)
     */
    public AgeResult(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    /**
     * Returns the number of complete years in this age.
     *
     * @return the number of complete years
     */
    public int getYears() {
        return years;
    }

    /**
     * Returns the number of remaining complete months in this age.
     *
     * @return the number of remaining complete months (typically 0–11)
     */
    public int getMonths() {
        return months;
    }

    /**
     * Returns the number of remaining days in this age.
     *
     * @return the number of remaining days (typically 0–30)
     */
    public int getDays() {
        return days;
    }

    /**
     * Returns a human-readable string representation of this age.
     *
     * <p>The format is exactly:
     * {@code "Your age is X years, Y months, and Z days."}
     * where X, Y, and Z are the years, months, and days components
     * respectively.</p>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code new AgeResult(27, 6, 15).toString()} returns
     *       {@code "Your age is 27 years, 6 months, and 15 days."}</li>
     *   <li>{@code new AgeResult(0, 0, 0).toString()} returns
     *       {@code "Your age is 0 years, 0 months, and 0 days."}</li>
     * </ul>
     *
     * @return formatted age string matching the specification
     */
    @Override
    public String toString() {
        return String.format("Your age is %d years, %d months, and %d days.", years, months, days);
    }
}
