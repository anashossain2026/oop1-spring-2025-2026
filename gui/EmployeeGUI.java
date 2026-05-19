package employeemanagementsystem.gui;

import employeemanagementsystem.entity.Employee;
import employeemanagementsystem.fileio.EmployeeFileIO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

/**
 * EmployeeGUI - Main application window for the Employee Management System.
 *
 * This class extends JFrame and builds the full user interface.
 *
 * Layout overview (different from the Student Management System demo):
 * WEST   - inputPanel (labels, text fields, and action buttons stacked vertically on the left side)
 * CENTER - scrollPane with the employee records table
 * SOUTH  - searchPanel (search bar) at the bottom
 *
 * The GUI communicates with EmployeeFileIO for all data persistence.
 */
public class EmployeeGUI extends JFrame {

    // --- Input text fields (one per employee attribute) ---
    private JTextField idField;         // Field where the user types the employee ID
    private JTextField nameField;       // Field where the user types the employee name
    private JTextField salaryField;     // Field where the user types the employee salary
    private JTextField departmentField; // Field where the user types the employee department
    private JTextField searchField;     // Field where the user types a search keyword

    // --- Table components ---
    private JTable table;                  // The visual table widget displayed in the window
    private DefaultTableModel tableModel;  // The data model that backs the JTable

    // =========================================================================
    // CONSTRUCTOR — builds and displays the entire GUI
    // =========================================================================

    /**
     * Constructs the EmployeeGUI window, wires up all components and event
     * listeners, ensures the data file exists, and loads all existing records
     * into the table.
     */
    public EmployeeGUI() {
        // Set the text shown in the window's title bar
        setTitle("Employee Management System");

        // Set the initial window size in pixels (width x height)
        setSize(900, 560);

        // Close the application completely when the user clicks the X button
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Use BorderLayout as the root layout with 10-pixel gaps between regions
        setLayout(new BorderLayout(10, 10));

        // -----------------------------------------------------------------
        // WEST PANEL - Input fields on the LEFT side (different from teacher's NORTH layout)
        // Using BoxLayout to stack components vertically
        // -----------------------------------------------------------------
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        inputPanel.setPreferredSize(new Dimension(220, 0)); // Fixed width on the left

        // Each row: label on top, field below
        inputPanel.add(new JLabel("Employee ID (8 digits):"));
        idField = new JTextField();
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        inputPanel.add(idField);
        inputPanel.add(Box.createVerticalStrut(8)); // Spacing

        inputPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        inputPanel.add(nameField);
        inputPanel.add(Box.createVerticalStrut(8));

        inputPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        salaryField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        inputPanel.add(salaryField);
        inputPanel.add(Box.createVerticalStrut(8));

        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        departmentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        inputPanel.add(departmentField);
        inputPanel.add(Box.createVerticalStrut(12));

        // Action buttons inside the input panel (stacked vertically on the left)
        JButton addBtn    = new JButton("Add Employee");
        JButton updateBtn = new JButton("Update Employee");
        JButton deleteBtn = new JButton("Delete Employee");
        JButton viewAllBtn = new JButton("View All");
        JButton clearBtn  = new JButton("Clear Fields");

        // Make buttons same width as the panel
        Dimension btnSize = new Dimension(Integer.MAX_VALUE, 30);
        addBtn.setMaximumSize(btnSize);
        updateBtn.setMaximumSize(btnSize);
        deleteBtn.setMaximumSize(btnSize);
        viewAllBtn.setMaximumSize(btnSize);
        clearBtn.setMaximumSize(btnSize);

        // Add all buttons to the input panel in top-to-bottom order
        inputPanel.add(addBtn);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(updateBtn);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(deleteBtn);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(viewAllBtn);
        inputPanel.add(Box.createVerticalStrut(5));
        inputPanel.add(clearBtn);

        // -----------------------------------------------------------------
        // TABLE - Center area showing all employee records
        // -----------------------------------------------------------------
        // Column headers shown at the top of the table
        String[] columns = { "ID", "Name", "Salary", "Department" };

        // Create a custom DefaultTableModel that prevents the user from editing cells directly
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            // isCellEditable returns false for all cells → read-only table
            public boolean isCellEditable(int row, int column) {
                return false; // Editing must go through the input fields + Update button
            }
        };

        table = new JTable(tableModel); // Build the visual table backed by tableModel
        table.setRowHeight(22);         // Make each row 22 pixels tall for readability

        // Wrap the table in a scroll pane so a scrollbar appears when there are many records
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Employee Records")); // Labelled border

        // -----------------------------------------------------------------
        // SOUTH PANEL - Search bar at the bottom
        // -----------------------------------------------------------------
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search by ID or Name"));

        searchField = new JTextField();        // Keyword entry box
        JButton searchBtn = new JButton("Search"); // Triggers the search action

        searchPanel.add(searchField, BorderLayout.CENTER); // Text field fills available space
        searchPanel.add(searchBtn, BorderLayout.EAST);     // Button sits at the right edge

        // -----------------------------------------------------------------
        // ASSEMBLE — add the major panels to the JFrame
        // -----------------------------------------------------------------
        add(inputPanel, BorderLayout.WEST);   // Input fields on the LEFT
        add(scrollPane, BorderLayout.CENTER); // Table fills the remaining space
        add(searchPanel, BorderLayout.SOUTH); // Search bar at the BOTTOM

        // -----------------------------------------------------------------
        // EVENT LISTENERS — wire each button/interaction to its handler method
        // -----------------------------------------------------------------

        // "Add Employee" button → call addEmployee() when clicked
        addBtn.addActionListener(e -> addEmployee());

        // "Update Employee" button → call updateEmployee() when clicked
        updateBtn.addActionListener(e -> updateEmployee());

        // "Delete Employee" button → call deleteEmployee() when clicked
        deleteBtn.addActionListener(e -> deleteEmployee());

        // "View All" button → clear the search box and reload all records
        viewAllBtn.addActionListener(e -> {
            searchField.setText(""); // Clear any active search keyword
            viewAll();               // Reload all employee records into the table
        });

        // "Clear Fields" button → reset all text fields and deselect the table row
        clearBtn.addActionListener(e -> clearFields());

        // "Search" button → call searchEmployee() when clicked
        searchBtn.addActionListener(e -> searchEmployee());

        // Table row click → auto-fill the input fields with the selected employee's data
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow(); // -1 if nothing is selected

            if (row >= 0) { // A valid row was selected
                // Populate each text field from the corresponding table column
                idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));         // Column 0 = ID
                nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));       // Column 1 = Name
                salaryField.setText(String.valueOf(tableModel.getValueAt(row, 2)));     // Column 2 = Salary
                departmentField.setText(String.valueOf(tableModel.getValueAt(row, 3))); // Column 3 = Department
            }
        });

        // -----------------------------------------------------------------
        // STARTUP — ensure the data file exists, then load existing records
        // -----------------------------------------------------------------
        try {
            EmployeeFileIO.createFileIfNotExists(); // Create employees.txt if it's the first run
        } catch (IOException ex) {
            showError("Error creating file: " + ex.getMessage()); // Alert user if creation fails
        }

        viewAll(); // Load all existing employee records into the table on launch

        setLocationRelativeTo(null); // Center the window on the screen
        setVisible(true);            // Make the window visible to the user
    }

    // =========================================================================
    // VALIDATION METHODS
    // =========================================================================

    /**
     * Validates that the given ID is exactly 8 numeric digits.
     *
     * Rules enforced:
     * - Must not be empty.
     * - Must match the regular expression \d{8} (exactly 8 digit characters).
     *
     * @param id The ID string entered by the user.
     * @return true if valid; false if invalid (also shows an error dialog).
     */
    private boolean isValidId(String id) {
        // Check for empty input first
        if (id.isEmpty()) {
            showError("Employee ID is required!");
            return false;
        }

        // \d{8} means "exactly 8 digit characters (0-9)" — no letters or symbols allowed
        if (!id.matches("\\d{8}")) {
            showError("Employee ID must be exactly 8 digits (numbers only).\n"
                    + "Minimum: 8 digits, Maximum: 8 digits.");
            return false;
        }
        return true; // ID passed all checks
    }

    /**
     * Validates all four input fields before an Add or Update operation.
     *
     * Rules enforced:
     * - Name, salary, and department must not be empty.
     * - ID must pass isValidId() (exactly 8 digits).
     * - No field may contain a comma (commas are the CSV delimiter in the data file).
     * - Salary must be parseable as a decimal number.
     *
     * @param id         ID field value.
     * @param name       Name field value.
     * @param salary     Salary field value.
     * @param department Department field value.
     * @return true if all fields are valid; false if any validation fails.
     */
    private boolean isValidAllFields(String id, String name, String salary, String department) {
        // Ensure none of the non-ID fields are blank
        if (name.isEmpty() || salary.isEmpty() || department.isEmpty()) {
            showError("All fields are required!");
            return false;
        }

        // Validate the ID using the dedicated ID validation method
        if (!isValidId(id))
            return false;

        // Commas would break the CSV format in the data file — disallow them everywhere
        if (name.contains(",") || salary.contains(",") || department.contains(",")) {
            showError("Commas are not allowed in any field!");
            return false;
        }

        // Salary must be a valid decimal number (e.g. "55000" or "55000.50")
        try {
            Double.parseDouble(salary); // Attempt to parse; exception means it's not a number
        } catch (NumberFormatException ex) {
            showError("Salary must be a valid number!");
            return false;
        }
        return true; // All fields passed validation
    }

    // =========================================================================
    // CRUD ACTION METHODS (called by button listeners)
    // =========================================================================

    /**
     * Reads the input fields and adds a new employee to the data file.
     *
     * Steps:
     * 1. Trim whitespace from all field values.
     * 2. Validate all fields.
     * 3. Check that the ID is not already in use.
     * 4. Save the new employee.
     * 5. Clear the input fields and refresh the table.
     */
    private void addEmployee() {
        // Read and trim each input field value (trim removes leading/trailing spaces)
        String id         = idField.getText().trim();
        String name       = nameField.getText().trim();
        String salary     = salaryField.getText().trim();
        String department = departmentField.getText().trim();

        // Stop immediately if any field fails validation
        if (!isValidAllFields(id, name, salary, department))
            return;

        // Prevent adding a second employee with the same ID
        if (EmployeeFileIO.idExists(id)) {
            showError("Duplicate ID! An employee with ID " + id + " already exists.");
            return;
        }

        try {
            // Create a new Employee object and save it to the data file
            EmployeeFileIO.addEmployee(new Employee(id, name, salary, department));
            showInfo("Employee added successfully!"); // Inform the user of success
            clearFields(); // Reset the input form for the next entry
            viewAll();     // Refresh the table to show the newly added record
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage()); // Show any file I/O error to the user
        }
    }

    /**
     * Reads the input fields and updates the matching employee record in the data file.
     *
     * Steps:
     * 1. Trim whitespace from all field values.
     * 2. Validate all fields.
     * 3. Attempt the update (returns false if the ID does not exist).
     * 4. Clear the input fields and refresh the table on success.
     */
    private void updateEmployee() {
        // Read and trim each input field value
        String id         = idField.getText().trim();
        String name       = nameField.getText().trim();
        String salary     = salaryField.getText().trim();
        String department = departmentField.getText().trim();

        // Stop if any validation fails
        if (!isValidAllFields(id, name, salary, department))
            return;

        try {
            // updateEmployee returns true if it found and replaced the record
            boolean updated = EmployeeFileIO.updateEmployee(
                    new Employee(id, name, salary, department));

            if (updated) {
                showInfo("Employee updated successfully!"); // Notify the user
                clearFields(); // Reset the form
                viewAll();     // Refresh the table to show the updated data
            } else {
                showError("Employee ID not found!"); // No record with that ID exists
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage()); // Show any file I/O error
        }
    }

    /**
     * Reads the ID field and deletes the matching employee record after user confirmation.
     *
     * Steps:
     * 1. Trim and validate the ID field.
     * 2. Ask the user to confirm the deletion (prevents accidental deletes).
     * 3. Attempt the delete (returns false if the ID does not exist).
     * 4. Clear the input fields and refresh the table on success.
     */
    private void deleteEmployee() {
        String id = idField.getText().trim(); // Only the ID is needed to identify the record

        // Validate the ID before proceeding
        if (!isValidId(id))
            return;

        // Show a Yes/No confirmation dialog — safety net against accidental deletions
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete employee ID: " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        // If the user chose anything other than "Yes", abort the delete
        if (confirm != JOptionPane.YES_OPTION)
            return;

        try {
            // deleteEmployee returns true if it found and removed the record
            boolean deleted = EmployeeFileIO.deleteEmployee(id);

            if (deleted) {
                showInfo("Employee deleted successfully!"); // Notify the user
                clearFields(); // Reset the form
                viewAll();     // Refresh the table (the deleted record is now gone)
            } else {
                showError("Employee ID not found!"); // No record with that ID exists
            }
        } catch (IOException ex) {
            showError("Error: " + ex.getMessage()); // Show any file I/O error
        }
    }

    /**
     * Reads the search field keyword and displays only the matching employee records.
     *
     * The search is case-insensitive and matches any ID or Name that contains
     * the keyword as a substring (partial match).
     */
    private void searchEmployee() {
        String keyword = searchField.getText().trim(); // The keyword entered by the user

        // Require at least something to search for
        if (keyword.isEmpty()) {
            showError("Enter an ID or Name to search!");
            return;
        }

        // Retrieve matching rows from the file (2D array, one row per match)
        Object[][] results = EmployeeFileIO.searchEmployees(keyword);

        tableModel.setRowCount(0); // Clear the current table contents before loading results

        // Add each matching employee row to the table
        for (int i = 0; i < results.length; i++) {
            tableModel.addRow(results[i]); // Each results[i] is a 4-element Object array
        }

        // Inform the user if no matches were found
        if (results.length == 0)
            showInfo("No matching employee found.");
    }

    /**
     * Loads all employee records from the data file and displays them in the table.
     *
     * Called at startup and after every Add, Update, or Delete operation to
     * keep the table in sync with the data file.
     */
    private void viewAll() {
        // Retrieve all records as a 2D array from the file
        Object[][] rows = EmployeeFileIO.getAllEmployees();

        tableModel.setRowCount(0); // Clear all existing rows from the table

        // Add each employee row to the table model (which automatically updates the JTable)
        for (int i = 0; i < rows.length; i++) {
            // rows[i][0] is the ID — skip rows where it is null (safety guard for empty slots)
            if (rows[i][0] != null)
                tableModel.addRow(rows[i]);
        }
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Clears all input text fields and removes any row selection in the table.
     *
     * Called after a successful Add/Update/Delete and when the Clear button is clicked.
     */
    private void clearFields() {
        idField.setText("");         // Erase the ID field
        nameField.setText("");       // Erase the Name field
        salaryField.setText("");     // Erase the Salary field
        departmentField.setText(""); // Erase the Department field
        searchField.setText("");     // Erase the Search field
        table.clearSelection();      // Deselect any highlighted row in the table
    }

    /**
     * Displays an informational pop-up dialog with the given message.
     *
     * Used to show success messages (e.g. "Employee added successfully!").
     *
     * @param msg The message to show in the dialog body.
     */
    private void showInfo(String msg) {
        // showMessageDialog(parent, message, title, messageType)
        // INFORMATION_MESSAGE shows a blue "i" icon
        JOptionPane.showMessageDialog(this, msg, "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an error pop-up dialog with the given message.
     *
     * Used to show validation errors and I/O failure messages.
     *
     * @param msg The error message to show in the dialog body.
     */
    private void showError(String msg) {
        // ERROR_MESSAGE shows a red "X" icon so errors are visually distinct from info
        JOptionPane.showMessageDialog(this, msg, "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}