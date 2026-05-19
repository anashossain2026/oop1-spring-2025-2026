// Declares that this class belongs to the "fileio" package (folder)
package employeemanagementsystem.fileio;

// Import the Employee data model used throughout this class
import employeemanagementsystem.entity.Employee;

// Import all Java I/O classes needed for reading/writing files
import java.io.*;

/**
 * EmployeeFileIO - Handles all file-based persistence for employee records.
 *
 * The data is stored in a plain text file ("employees.txt") where each
 * line represents one employee in CSV format:
 * id,name,salary,department
 * e.g. 10000001,John Doe,55000,IT
 *
 * Update and Delete operations use a write-to-temp-then-rename strategy
 * because you cannot overwrite individual lines in a text file in-place.
 * The steps are:
 * 1. Read the original file line by line.
 * 2. Write every line (possibly modified or skipped) to a temporary file.
 * 3. Delete the original file.
 * 4. Rename the temporary file to the original name.
 *
 * All methods are static — there is no need to create an instance of this
 * class.
 */
public class EmployeeFileIO {

    // Path to the main data file (created in the project's working directory)
    private static final String FILE_NAME = "src/employeemanagementsystem/fileio/employees.txt";

    // Path to the temporary file used during update/delete operations
    private static final String TEMP_FILE = "src/employeemanagementsystem/fileio/temp.txt";

    // =========================================================================
    // FILE SETUP
    // =========================================================================

    /**
     * Creates the data file if it does not already exist.
     *
     * Called once at application startup so that subsequent read/write
     * operations always find an existing file.
     *
     * @throws IOException if the file cannot be created (e.g. no write permission).
     */
    public static void createFileIfNotExists() throws IOException {
        File file = new File(FILE_NAME); // Build a File object pointing to employees.txt

        // Only create if the file is not already there
        if (!file.exists()) 
            file.createNewFile(); // Creates an empty file on disk
    }

    // =========================================================================
    // QUERY HELPERS
    // =========================================================================

    /**
     * Checks whether an employee with the given ID already exists in the file.
     *
     * Used before adding a new employee to prevent duplicate IDs.
     *
     * @param id The 8-digit ID to search for.
     * @return true if a matching record is found; false otherwise.
     */
    public static boolean idExists(String id) {
        // try-with-resources: the BufferedReader is automatically closed when done
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            // Read the file one line at a time until there are no more lines
            while ((line = br.readLine()) != null) {
                Employee e = Employee.fromLine(line); // Parse the CSV line into an Employee

                // If parsing succeeded and the ID matches, the ID already exists
                if (e != null && e.getId().equals(id))
                    return true;
            }
        } catch (IOException ignored) {
            // If the file doesn't exist yet or can't be read, treat it as "no match"
        }
        return false; // No matching ID was found
    }

    /**
     * Counts the total number of valid employee records in the data file.
     *
     * Used by getAllEmployees() to pre-allocate the exact array size
     * (avoids using a List/ArrayList).
     *
     * @return Number of valid records (lines that parse successfully).
     */
    public static int countRecords() {
        int count = 0; // Start the counter at zero

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = br.readLine()) != null) {
                // Only count lines that successfully parse into an Employee
                if (Employee.fromLine(line) != null)
                    count++; // Increment for each valid record
            }
        } catch (IOException ignored) {
            // File unreadable — return 0
        }
        return count; // Return the final count
    }

    // =========================================================================
    // CRUD OPERATIONS
    // =========================================================================

    /**
     * Appends a new employee record to the end of the data file.
     *
     * The FileWriter is opened in append mode (second argument = true),
     * so existing records are never overwritten.
     *
     * @param e The Employee object to save.
     * @throws IOException if the file cannot be written.
     */
    public static void addEmployee(Employee e) throws IOException {
        // PrintWriter wraps BufferedWriter for convenient println() support.
        // FileWriter(FILE_NAME, true) opens the file in APPEND mode.
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(FILE_NAME, true)))) {
            pw.println(e.toLine()); // Write the CSV line followed by a newline character
        }
    }

    /**
     * Replaces an existing employee record that matches the given employee's ID.
     *
     * Strategy (write-to-temp-then-rename):
     * - Read every line from the original file.
     * - If the line's ID matches, write the updated data instead.
     * - Write all other lines unchanged.
     * - Replace the original file with the temporary file.
     *
     * @param e Employee object containing the updated values (ID must already exist).
     * @return true if the record was found and updated; false if the ID was not found.
     * @throws IOException if a file operation fails.
     */
    public static boolean updateEmployee(Employee e) throws IOException {
        File inputFile = new File(FILE_NAME); // Reference to the original data file
        File tempFile = new File(TEMP_FILE);  // Reference to the temporary output file
        boolean found = false; // Track whether the target ID was located

        // Open the original file for reading AND the temp file for writing simultaneously
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                Employee existing = Employee.fromLine(line); // Parse each existing record

                // Check if this line belongs to the employee we want to update
                if (existing != null && existing.getId().equals(e.getId())) {
                    bw.write(e.toLine()); // Write the NEW (updated) data instead
                    found = true; // Mark that we found and replaced the record
                } else {
                    bw.write(line); // Write the original line unchanged
                }
                bw.newLine(); // Always write a newline after each record
            }
        }

        if (found) {
            // Replace the original file with the updated temp file
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not finalize update."); // Rename failed
            }
        } else {
            // The ID was not found — discard the temp file, nothing changed
            tempFile.delete();
        }
        return found; // Inform the caller whether the update succeeded
    }

    /**
     * Removes the employee record with the specified ID from the data file.
     *
     * Strategy (write-to-temp-then-rename):
     * - Read every line from the original file.
     * - Skip (do not write) the line whose ID matches.
     * - Write all other lines to the temporary file.
     * - Replace the original file with the temporary file.
     *
     * @param id The 8-digit ID of the employee to delete.
     * @return true if the record was found and deleted; false if ID was not found.
     * @throws IOException if a file operation fails.
     */
    public static boolean deleteEmployee(String id) throws IOException {
        File inputFile = new File(FILE_NAME); // Original data file
        File tempFile = new File(TEMP_FILE);  // Temp file to hold surviving records
        boolean found = false; // Track whether the target record was found

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
                BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                Employee existing = Employee.fromLine(line); // Parse the current line

                // If this record's ID matches the one to delete, skip writing it
                if (existing != null && existing.getId().equals(id)) {
                    found = true;
                    continue; // "continue" jumps to the next loop iteration, skipping bw.write
                }

                // Write every other record to the temp file (they survive the delete)
                bw.write(line);
                bw.newLine(); // Preserve the newline that separates records
            }
        }

        if (found) {
            // Replace the original file with the temp file (which no longer has the deleted record)
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                throw new IOException("Could not finalize delete."); // Rename failed
            }
        } else {
            // Nothing was deleted — discard the unused temp file
            tempFile.delete();
        }
        return found; // Let the caller know whether deletion actually happened
    }

    // =========================================================================
    // BULK RETRIEVAL
    // =========================================================================

    /**
     * Reads all valid employee records from the file and returns them as a 2D
     * Object array that can be loaded directly into a JTable.
     *
     * Array dimensions: [numberOfEmployees][4]
     * Column 0 = ID, Column 1 = Name, Column 2 = Salary, Column 3 = Department
     *
     * Two-pass approach (no List/ArrayList used):
     * Pass 1 — countRecords() determines the exact number of rows needed.
     * Pass 2 — Read again to fill the pre-sized array.
     *
     * @return 2D Object array with one row per employee.
     */
    public static Object[][] getAllEmployees() {
        int total = countRecords(); // Pass 1: find out how many rows to allocate
        Object[][] rows = new Object[total][4]; // Allocate exactly the right size
        int idx = 0; // Index into the rows array

        // Pass 2: fill the array with actual employee data
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            // Read until end-of-file or until the array is full (safety guard)
            while ((line = br.readLine()) != null && idx < total) {
                Employee e = Employee.fromLine(line); // Parse the CSV line

                if (e != null) { // Skip any malformed lines
                    Object[] row = e.toRow(); // Get the 4-element row array from Employee
                    rows[idx][0] = row[0]; // ID column
                    rows[idx][1] = row[1]; // Name column
                    rows[idx][2] = row[2]; // Salary column
                    rows[idx][3] = row[3]; // Department column
                    idx++; // Move to the next row slot
                }
            }
        } catch (IOException ignored) {
            // Return whatever was collected so far (possibly an empty array)
        }
        return rows; // Return the fully populated 2D array
    }

    /**
     * Searches employee records by keyword matching against ID or Name
     * (case-insensitive partial match) and returns results as a 2D array.
     *
     * Example: keyword "joh" would match "John Doe" or ID "10000001joh" (if it existed).
     *
     * Two-pass approach (no List/ArrayList used):
     * Pass 1 — Count how many records match the keyword.
     * Pass 2 — Fill an array of exactly that size with the matching records.
     *
     * @param keyword Search term entered by the user (matched against ID and Name).
     * @return 2D Object array of matching employees, ready for JTable display.
     */
    public static Object[][] searchEmployees(String keyword) {
        // Convert keyword to lowercase once so every comparison is case-insensitive
        String kw = keyword.toLowerCase();

        // --- Pass 1: Count matching records ---
        int matchCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                Employee e = Employee.fromLine(line); // Parse each line into an Employee

                // Count the employee if either their ID or name contains the keyword
                if (e != null && (e.getId().toLowerCase().contains(kw)
                        || e.getName().toLowerCase().contains(kw))) {
                    matchCount++; // This employee is a match
                }
            }
        } catch (IOException ignored) {
            // If file is unreadable, matchCount stays 0 and we return an empty array
        }

        // --- Pass 2: Populate the results array ---
        Object[][] results = new Object[matchCount][4]; // Exactly sized for all matches
        int idx = 0; // Current position in the results array

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            // Read until end-of-file or until the array is full
            while ((line = br.readLine()) != null && idx < matchCount) {
                Employee e = Employee.fromLine(line); // Parse each line

                // Apply the same match condition as in Pass 1
                if (e != null && (e.getId().toLowerCase().contains(kw)
                        || e.getName().toLowerCase().contains(kw))) {
                    Object[] row = e.toRow(); // Convert matching Employee to a row
                    results[idx][0] = row[0]; // ID
                    results[idx][1] = row[1]; // Name
                    results[idx][2] = row[2]; // Salary
                    results[idx][3] = row[3]; // Department
                    idx++; // Advance to the next result slot
                }
            }
        } catch (IOException ignored) {
            // Return whatever was collected so far
        }
        return results; // Return all matching employee rows
    }
}