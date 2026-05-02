class Position {
    private String title;
    private double salary;

    public Position(String title, double salary) {
        this.title = title;
        this.salary = salary;
    }

    public void displayPosition() {
        System.out.println("Position: " + title);
        System.out.println("Salary: " + salary);
    }
}

class Employee {
    private String name;
    private Position position;  

    public Employee(String name, Position position) {
        this.name = name;
        this.position = position;
    }

    public void displayEmployee() {
        System.out.println("Employee Name: " + name);
        position.displayPosition(); 
    }
}

class Company {
    private String companyName;
    private Employee[] employees;  

    public Company(String companyName, Employee[] employees) {
        this.companyName = companyName;
        this.employees = employees;
    }

    public void displayCompany() {
        System.out.println("Company: " + companyName);
        System.out.println("Employees:");

        for (Employee emp : employees) {
            emp.displayEmployee();
        }
    }
}

public class Association_Task {
    public static void main(String[] args) {
        
        Position p1 = new Position("Engineer", 30000);
        Position p2 = new Position("Manager", 40000);

        Employee e1 = new Employee("Akas", p1);
        Employee e2 = new Employee("Rakib", p2);

        company.displayCompany();
    }
}