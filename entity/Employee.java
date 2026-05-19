package employeemanagementsystem.entity;

/**
 * Employee - Data model for a single employee record.
 *
 * Each employee has:
 * id     - exactly 8-digit numeric string (unique)
 * name   - employee full name
 * salary - employee salary stored as String
 * department - department the employee belongs to
 *
 * Helper methods convert Employee to/from CSV format used in employees.txt
 */
public class Employee {

    private String id;
    private String name;
    private String salary;
    private String department;

    // Constructor
    public Employee(String id, String name, String salary, String department) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.department = department;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getSalary() { return salary; }
    public String getDepartment() { return department; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSalary(String salary) { this.salary = salary; }
    public void setDepartment(String department) { this.department = department; }

    /**
     * Converts this Employee to a CSV line for writing to the text file.
     * Example: "12345678,John Doe,55000,IT"
     */
    public String toLine() {
        return id + "," + name + "," + salary + "," + department;
    }

    /**
     * Parses a CSV line from the data file and returns an Employee.
     * Returns null if the line is null or malformed.
     */
    public static Employee fromLine(String line) {
        if (line == null)
            return null;

        String[] data = line.split(",", -1);

        if (data.length != 4)
            return null;

        return new Employee(data[0], data[1], data[2], data[3]);
    }

    /**
     * Converts this Employee into an Object array row for JTable display.
     * Columns: [ID, Name, Salary, Department]
     */
    public Object[] toRow() {
        return new Object[] { id, name, salary, department };
    }
}
