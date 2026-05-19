package employeemanagementsystem;

import employeemanagementsystem.gui.EmployeeGUI;

import javax.swing.SwingUtilities;


public class Start {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeGUI::new);
    }
}
