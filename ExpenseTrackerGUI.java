import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;


class Expense {
    private String description;
    private double amount;

    public Expense(String description, double amount) {
        this.description = description;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description + ": Rs" + String.format("%.2f", amount);
    }
}

class ExpenseTracker {
    private List<Expense> expenses;
    private double budgetLimit;

    public ExpenseTracker() {
        this.expenses = new ArrayList<>();
        this.budgetLimit = 10000.0; 
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses);
    }

    public double getTotalExpenses() {
        return expenses.stream().mapToDouble(Expense::getAmount).sum();
    }

    public void setBudget(double budget) {
        this.budgetLimit = budget;
    }

    public double getBudget() {
        return budgetLimit;
    }

    public boolean isOverBudget() {
        return getTotalExpenses() > budgetLimit;
    }

    public void editExpense(String oldDescription, String newDescription, double newAmount) {
        for (Expense expense : expenses) {
            if (expense.getDescription().equals(oldDescription)) {
                expense.setDescription(newDescription);
                expense.setAmount(newAmount);
                break;
            }
        }
    }

    public void deleteExpense(String description) {
        expenses.removeIf(expense -> expense.getDescription().equals(description));
    }
}

public class ExpenseTrackerGUI {
    private ExpenseTracker tracker;
    private JFrame frame;
    private JTextArea textArea;
    private JComboBox<Expense> expenseComboBox;
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel signupPanel;
    private JTextField newUsernameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ExpenseTrackerGUI() {
        tracker = new ExpenseTracker();
        createWindow();
        showOperations();
    }

    private void createWindow() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());

        JButton startButton = new JButton("Start ExpenseTracker");
        startButton.addActionListener(e -> showOperations());
        frame.add(startButton);

        frame.setVisible(true);
    }

    private void showOperations() {
        frame.getContentPane().removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Provides some spacing between components
    
        GridBagLayout gbl = new GridBagLayout();
        frame.setLayout(gbl);
        frame.add(new JLabel("                        WELCOME TO EXPENSE TRACKER"),gbc);
        frame.add(createButton("Add Expense", this::addExpense), gbc);
        frame.add(createButton("View Total Expense", e -> viewTotalExpense()), gbc);
        frame.add(createButton("Edit Expense", this::editExpense), gbc); // Separate Edit button
        frame.add(createButton("Delete Expense", this::deleteExpense), gbc); // Separate Delete button
        frame.add(createButton("Set Budget Limit", this::setBudgetLimit), gbc);
        frame.add(createButton("Exit", e -> System.exit(0)), gbc);
    
        textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        frame.add(scrollPane, gbc);
    
        expenseComboBox = new JComboBox<>();
        updateExpenseComboBox();
        gbc.weighty = 0;
        frame.add(expenseComboBox, gbc);
    
        frame.pack(); // Instead of setting the size, pack will cause the window to be sized to fit the preferred size and layouts of its subcomponents
        frame.setLocationRelativeTo(null); // To center the frame on screen
        frame.setVisible(true);
    }
    

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50)); // Set a preferred size for the buttons
        button.addActionListener(listener);
        return button;
    }
    

    private void addExpense(ActionEvent event) {
        String description = JOptionPane.showInputDialog(frame, "Enter Expense Description:");
        String amountStr = JOptionPane.showInputDialog(frame, "Enter Amount:");
        double amount = Double.parseDouble(amountStr);

        if (tracker.getTotalExpenses() + amount > tracker.getBudget()) {
            JOptionPane.showMessageDialog(frame, "Budget Limit Exceeded!", "Warning", JOptionPane.WARNING_MESSAGE);
            return; 
        }

        tracker.addExpense(new Expense(description, amount));
        updateExpenseComboBox();
        viewExpenses();
    }

    private void viewExpenses() {
        StringBuilder expensesText = new StringBuilder();
        for (Expense expense : tracker.getExpenses()) {
            expensesText.append(expense).append("\n");
        }
        textArea.setText(expensesText.toString());
        checkBudget();
    }

    private void viewTotalExpense() {
        textArea.setText("Total Expenses: $" + String.format("%.2f", tracker.getTotalExpenses()));
        checkBudget();
    }


    private void editExpense(ActionEvent event) {
        Expense selectedExpense = (Expense) expenseComboBox.getSelectedItem();
        if (selectedExpense != null) {
            String newDescription = JOptionPane.showInputDialog(frame, "Enter New Description:", selectedExpense.getDescription());
            String newAmountStr = JOptionPane.showInputDialog(frame, "Enter New Amount:", selectedExpense.getAmount());
            double newAmount = Double.parseDouble(newAmountStr);
            tracker.editExpense(selectedExpense.getDescription(), newDescription, newAmount);
            updateExpenseComboBox();
            viewExpenses();
        }
    }
    
    // Method to handle delete button action
    private void deleteExpense(ActionEvent event) {
        Expense selectedExpense = (Expense) expenseComboBox.getSelectedItem();
        if (selectedExpense != null) {
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tracker.deleteExpense(selectedExpense.getDescription());
                updateExpenseComboBox();
                viewExpenses();
            }
        }
    }

    private void setBudgetLimit(ActionEvent event) {
        String budgetStr = JOptionPane.showInputDialog(frame, "Set New Budget Limit:", tracker.getBudget());
        double budget = Double.parseDouble(budgetStr);
        tracker.setBudget(budget);
        viewExpenses();
        checkBudget();
    }

    private void updateExpenseComboBox() {
        expenseComboBox.removeAllItems();
        for (Expense expense : tracker.getExpenses()) {
            expenseComboBox.addItem(expense);
        }
    }

    private void checkBudget() {
        if (tracker.isOverBudget()) {
            JOptionPane.showMessageDialog(frame, "Budget Limit Exceeded!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void createSignupWindow() {
        signupPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        newUsernameField = new JTextField(15);
        newPasswordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);

        signupPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
         // Adding some padding
        signupPanel.add(new JLabel("                   SIGNIN PORTAL"), gbc);
        gbc.gridwidth = 1;
        signupPanel.add(new JLabel("Username:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        signupPanel.add(newUsernameField, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        signupPanel.add(new JLabel("Password:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        signupPanel.add(newPasswordField, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        signupPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        signupPanel.add(confirmPasswordField, gbc);
        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(this::signupAction);
        signupPanel.add(signupButton, gbc);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> switchToLogin());
        signupPanel.add(backButton, gbc);

        frame.setContentPane(signupPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // This method switches the view back to the login page
    private void switchToLogin() {
        frame.setContentPane(loginPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // This method is used to handle the signup action
    private void signupAction(ActionEvent event) {
        String newUsername = newUsernameField.getText();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Check if password fields match
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // In a real application, you would also check if the username is already taken and hash the password.
        // For demonstration, we will simply print out the credentials.
        System.out.println("New user created: Username - " + newUsername + ", Password - " + newPassword);
        JOptionPane.showMessageDialog(frame, "User created successfully!", "Signup Successful", JOptionPane.INFORMATION_MESSAGE);

        // After signup, switch back to the login window
        switchToLogin();
    }

    private void createLoginWindow() {
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginPanel.setBorder(new EmptyBorder(50,50,50,50)); // Adding some padding

        loginPanel.add(new JLabel("      EXPENSE TRACKER"),gbc);
        loginPanel.add(new JLabel("           LOGIN PORTAL"), gbc);
        loginPanel.add(new JLabel(""),gbc);
        gbc.gridwidth = 1;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginPanel.add(usernameField, gbc);
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        loginPanel.add(passwordField, gbc);
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this::loginAction);
        loginPanel.add(loginButton, gbc);
        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(e -> createSignupWindow());
        loginPanel.add(signupButton, gbc);

        frame.setContentPane(loginPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void loginAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (validateCredentials(username, password)) {
            // If credentials are valid, switch to the main operations view.
            showOperations();
        } else {
            // Show an error message if the credentials are invalid.
            JOptionPane.showMessageDialog(frame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    private boolean validateCredentials(String username, String password) {
        // In a real application, this method should check the credentials against a user database.
        // For this example, we will just simulate a successful login.
        // This is insecure and for demonstration purposes only.
    
        // Example of hardcoded check (insecure!)
        // return username.equals("user") && password.equals("pass");
    
        // For now, we assume any credentials are valid:
        return true;
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseTrackerGUI expenseTrackerGUI = new ExpenseTrackerGUI();
            expenseTrackerGUI.createLoginWindow(); // Start with the login window.
        });
    }
}