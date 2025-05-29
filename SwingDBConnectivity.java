import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List; // To avoid ambiguity

public class SwingDBConnectivity extends JFrame {

    private JComboBox<String> tableSelector, columnSelector, collectionSelector;
    private JTextArea resultArea;
    private Connection connection;

    // Constructor starts here
    public SwingDBConnectivity() {
        setTitle("Swing Database Connectivity");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tableSelector = new JComboBox<>(new String[]{"Table1", "Table2"});
        columnSelector = new JComboBox<>(new String[]{"Column1", "Column2", "Column3", "Column4", "Column5"});
        collectionSelector = new JComboBox<>(new String[]{"ArrayList", "HashSet", "LinkedList"});
        JButton fetchButton = new JButton("Fetch Data");
        resultArea = new JTextArea();

        fetchButton.addActionListener(e -> fetchData());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Table: "));
        topPanel.add(tableSelector);
        topPanel.add(new JLabel("Select Column: "));
        topPanel.add(columnSelector);
        topPanel.add(new JLabel("Select Collection: "));
        topPanel.add(collectionSelector);
        topPanel.add(fetchButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/your_database?useSSL=false"; // Change 'your_database'
            String user = "root"; // Change if needed
            String password = "rayyan_202004"; // Change your MySQL password
            connection = DriverManager.getConnection(url, user, password);
            JOptionPane.showMessageDialog(this, "Database Connected Successfully.");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "MySQL Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + e.getMessage());
        }
    }

    private void fetchData() {
        String table = (String) tableSelector.getSelectedItem();
        String column = (String) columnSelector.getSelectedItem();
        String collectionType = (String) collectionSelector.getSelectedItem();

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + table);
            Collection<RowData> dataList = switch (collectionType) {
                case "ArrayList" -> new ArrayList<>();
                case "HashSet" -> new HashSet<>();
                default -> new LinkedList<>();
            };

            while (rs.next()) {
                dataList.add(new RowData(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getFloat(3),
                    rs.getString(4).charAt(0),
                    rs.getBoolean(5)
                ));
            }

            List<RowData> sortedList = new ArrayList<>(dataList);
            sortedList.sort(Comparator.comparing(row -> row.getColumnByName(column).toString()));

            StringBuilder sb = new StringBuilder();
            for (RowData row : sortedList) {
                sb.append(row).append("\n");
            }
            sb.append("\nTotal records: ").append(sortedList.size());

            resultArea.setText(sb.toString());
        } catch (SQLException e) {
            resultArea.setText("Error fetching data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingDBConnectivity().setVisible(true));
    }

    static class RowData {
        private int column1;
        private String column2;
        private float column3;
        private char column4;
        private boolean column5;

        public RowData(int column1, String column2, float column3, char column4, boolean column5) {
            this.column1 = column1;
            this.column2 = column2;
            this.column3 = column3;
            this.column4 = column4;
            this.column5 = column5;
        }

        public Object getColumnByName(String column) {
            return switch (column) {
                case "Column1" -> column1;
                case "Column2" -> column2;
                case "Column3" -> column3;
                case "Column4" -> column4;
                case "Column5" -> column5;
                default -> null;
            };
        }

       @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowData rowData = (RowData) o;
    return Objects.equals(column2, rowData.column2) &&  
           column4 == rowData.column4;

@Override
public int hashCode() {
    return Objects.hash(column2, column4);
}

        @Override
        public String toString() {
            return column1 + " | " + column2 + " | " + column3 + " | " + column4 + " | " + column5;
        }
    }
}
