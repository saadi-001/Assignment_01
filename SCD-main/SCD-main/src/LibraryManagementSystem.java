import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LibraryManagementSystem extends JFrame implements ActionListener {
    private final JTextField txtStudentName = new JTextField(20);
    private final JTextField txtRollNumber = new JTextField(20);
    private final JTextField txtBookTitle = new JTextField(20);
    private final JTextField txtIssueDate = new JTextField(20);
    private final JTextField txtReturnDate = new JTextField(20);
    private final JTextArea txtRemarks = new JTextArea(4, 20);
    private final JComboBox<String> comboCategory = new JComboBox<>(new String[]{
            "Select Category",
            "Programming",
            "Artificial Intelligence",
            "Databases",
            "Networking"
    });
    private final JRadioButton rbNewEdition = new JRadioButton("New Edition");
    private final JRadioButton rbOldEdition = new JRadioButton("Old Edition");
    private final ButtonGroup editionGroup = new ButtonGroup();
    private final JButton btnIssue = new JButton("Issue Book");
    private final JButton btnReset = new JButton("Reset");
    private final JButton btnExit = new JButton("Exit");

    public LibraryManagementSystem() {
        setTitle("Library Book Issue System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel rootPanel = new JPanel(new BorderLayout(10, 10));
        rootPanel.add(buildFormPanel(), BorderLayout.CENTER);
        rootPanel.add(buildButtonPanel(), BorderLayout.SOUTH);

        setContentPane(rootPanel);

        configureRadioButtons();
        configureButtons();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        addRow(formPanel, gbc, 0, new JLabel("Student Name:"), txtStudentName);
        addRow(formPanel, gbc, 1, new JLabel("Roll Number:"), txtRollNumber);
        addRow(formPanel, gbc, 2, new JLabel("Book Title:"), txtBookTitle);
        addRow(formPanel, gbc, 3, new JLabel("Book Category:"), comboCategory);
        addRow(formPanel, gbc, 4, new JLabel("Issue Date (dd/MM/yyyy):"), txtIssueDate);
        addRow(formPanel, gbc, 5, new JLabel("Return Date (dd/MM/yyyy):"), txtReturnDate);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Book Type:"), gbc);

        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typePanel.add(rbNewEdition);
        typePanel.add(rbOldEdition);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1;
        formPanel.add(typePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Remarks:"), gbc);

        txtRemarks.setLineWrap(true);
        txtRemarks.setWrapStyleWord(true);
        JScrollPane remarksScrollPane = new JScrollPane(txtRemarks);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(remarksScrollPane, gbc);

        return formPanel;
    }

    private JPanel buildButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(btnIssue);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnExit);
        return buttonPanel;
    }

    private void configureButtons() {
        btnIssue.addActionListener(this);
        btnReset.addActionListener(this);
        btnExit.addActionListener(this);
    }

    private void configureRadioButtons() {
        editionGroup.add(rbNewEdition);
        editionGroup.add(rbOldEdition);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();

        if (source == btnIssue) {
            issueBook();
        } else if (source == btnReset) {
            resetForm();
        } else if (source == btnExit) {
            dispose();
            System.exit(0);
        }
    }

    private void issueBook() {
        try {
            validateRequiredFields();

            String studentName = txtStudentName.getText().trim();
            int rollNumber = validateRollNumber();
            String bookTitle = txtBookTitle.getText().trim();
            String category = validateCategory();
            String bookType = validateBookType();
            Date issueDate = parseDate(txtIssueDate.getText().trim(), "Issue Date");
            Date returnDate = parseDate(txtReturnDate.getText().trim(), "Return Date");

            if (returnDate.before(issueDate)) {
                throw new InvalidDateException("Return date cannot be earlier than the issue date.");
            }

            String remarks = txtRemarks.getText().trim();
            String summary = buildSummary(studentName, rollNumber, bookTitle, category, bookType, issueDate, returnDate, remarks);

            JOptionPane.showMessageDialog(
                    this,
                    summary,
                    "Book Issued Successfully",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (EmptyFieldException ex) {
            showError(ex.getMessage());
        } catch (InvalidRollNumberException ex) {
            showError(ex.getMessage());
        } catch (NullSelectionException ex) {
            showError(ex.getMessage());
        } catch (InvalidDateException ex) {
            showError(ex.getMessage());
        } catch (NumberFormatException ex) {
            showError("Invalid numeric format for roll number.");
        } catch (Exception ex) {
            showError("Unexpected error: " + ex.getMessage());
        }
    }

    private void validateRequiredFields() throws EmptyFieldException {
        if (txtStudentName.getText().trim().isEmpty()
                || txtRollNumber.getText().trim().isEmpty()
                || txtBookTitle.getText().trim().isEmpty()
                || txtIssueDate.getText().trim().isEmpty()
                || txtReturnDate.getText().trim().isEmpty()) {
            throw new EmptyFieldException("Please fill all required fields.");
        }
    }

    private int validateRollNumber() throws InvalidRollNumberException {
        String rollText = txtRollNumber.getText().trim();

        if (!rollText.matches("\\d+")) {
            throw new InvalidRollNumberException("Roll number must contain digits only.");
        }

        int rollNumber = Integer.parseInt(rollText);
        if (rollNumber <= 0) {
            throw new InvalidRollNumberException("Roll number must be greater than zero.");
        }

        return rollNumber;
    }

    private String validateCategory() throws NullSelectionException {
        if (comboCategory.getSelectedIndex() == 0) {
            throw new NullSelectionException("Please select a book category.");
        }

        return comboCategory.getSelectedItem().toString();
    }

    private String validateBookType() throws NullSelectionException {
        if (rbNewEdition.isSelected()) {
            return "New Edition";
        }

        if (rbOldEdition.isSelected()) {
            return "Old Edition";
        }

        throw new NullSelectionException("Please select a book type.");
    }

    private Date parseDate(String value, String fieldName) throws InvalidDateException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try {
            return dateFormat.parse(value);
        } catch (ParseException ex) {
            throw new InvalidDateException(fieldName + " must be in dd/MM/yyyy format.");
        }
    }

    private String buildSummary(String studentName,
                                int rollNumber,
                                String bookTitle,
                                String category,
                                String bookType,
                                Date issueDate,
                                Date returnDate,
                                String remarks) {
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        StringBuilder summary = new StringBuilder();

        summary.append("Book issued for:").append(System.lineSeparator());
        summary.append("Student Name: ").append(studentName).append(System.lineSeparator());
        summary.append("Roll Number: ").append(rollNumber).append(System.lineSeparator());
        summary.append("Book Title: ").append(bookTitle).append(System.lineSeparator());
        summary.append("Category: ").append(category).append(System.lineSeparator());
        summary.append("Book Type: ").append(bookType).append(System.lineSeparator());
        summary.append("Issue Date: ").append(outputFormat.format(issueDate)).append(System.lineSeparator());
        summary.append("Return Date: ").append(outputFormat.format(returnDate));

        if (!remarks.isEmpty()) {
            summary.append(System.lineSeparator()).append("Remarks: ").append(remarks);
        }

        return summary.toString();
    }

    private void resetForm() {
        txtStudentName.setText("");
        txtRollNumber.setText("");
        txtBookTitle.setText("");
        txtIssueDate.setText("");
        txtReturnDate.setText("");
        txtRemarks.setText("");
        comboCategory.setSelectedIndex(0);
        editionGroup.clearSelection();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Input Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}