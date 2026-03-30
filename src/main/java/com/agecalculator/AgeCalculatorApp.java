package com.agecalculator;

import com.agecalculator.model.AgeResult;
import com.agecalculator.util.DateValidator;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Main console entry point for the Age Calculator application.
 *
 * <p>This class orchestrates the complete user interaction workflow:</p>
 * <ol>
 *   <li>Prompts the user to enter their Date of Birth in DD/MM/YYYY format</li>
 *   <li>Delegates input parsing to {@link DateValidator#parse(String)}</li>
 *   <li>Delegates future-date validation to {@link DateValidator#validate(LocalDate)}</li>
 *   <li>Delegates age computation to {@link AgeCalculator#calculateAge(LocalDate)}</li>
 *   <li>Displays the formatted result via {@link AgeResult#toString()}</li>
 * </ol>
 *
 * <p>Exception handling is implemented using {@code try-catch} blocks to
 * provide user-friendly error messages instead of raw stack traces:</p>
 * <ul>
 *   <li>{@link DateTimeParseException} — for malformed or invalid date strings</li>
 *   <li>{@link IllegalArgumentException} — for future dates or null input</li>
 * </ul>
 *
 * <p>This class follows the Single Responsibility Principle by only handling
 * console I/O orchestration — all business logic is delegated to specialized
 * classes ({@link AgeCalculator}, {@link DateValidator}, {@link AgeResult}).</p>
 *
 * <p>Example interaction:</p>
 * <pre>
 * Enter your Date of Birth (DD/MM/YYYY): 15/08/1998
 * Your age is 27 years, 6 months, and 15 days.
 * </pre>
 *
 * @see AgeCalculator
 * @see DateValidator
 * @see AgeResult
 */
public class AgeCalculatorApp {

    /**
     * Application entry point that reads a date of birth from the console,
     * calculates the user's age, and prints the result.
     *
     * <p>The method uses a try-with-resources statement to manage the
     * {@link Scanner} resource, ensuring it is closed when the method
     * completes. Inside the resource block, a nested {@code try-catch}
     * handles parsing and validation errors:</p>
     * <ul>
     *   <li>{@link DateTimeParseException} is caught when the input string
     *       cannot be parsed as a valid DD/MM/YYYY date (wrong format,
     *       impossible calendar date, alphabetic input, empty string)</li>
     *   <li>{@link IllegalArgumentException} is caught when the parsed date
     *       is in the future or the input is null</li>
     * </ul>
     *
     * @param args command-line arguments (not used by this application)
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Prompt the user for their date of birth
            System.out.print("Enter your Date of Birth (DD/MM/YYYY): ");

            // Read the user's input line
            String input = scanner.nextLine();

            try {
                // Parse the input string into a LocalDate using strict DD/MM/YYYY format
                LocalDate dob = DateValidator.parse(input);

                // Validate that the date of birth is not in the future
                DateValidator.validate(dob);

                // Create the age calculator and compute the age
                AgeCalculator calculator = new AgeCalculator();
                AgeResult result = calculator.calculateAge(dob);

                // Display the formatted age result
                System.out.println(result.toString());

            } catch (DateTimeParseException e) {
                // Handle malformed or invalid date input (wrong format, impossible dates)
                System.out.println("Invalid date format. Please enter date in DD/MM/YYYY format.");
            } catch (IllegalArgumentException e) {
                // Handle future dates and null input with the exception's own message
                System.out.println(e.getMessage());
            }
        }
    }
}
