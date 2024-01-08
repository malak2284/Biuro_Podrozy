package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

class Database {
    private String url;
    private TravelData travelData;

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    public void create() {
        try {
            String username = "root";
            String password = "";

            try (Connection con = DriverManager.getConnection(url, username, password)) {

                                Statement stmt= con.createStatement();
                String sql1 = "CREATE TABLE offers (id INT AUTO_INCREMENT," +
                        "country VARCHAR(255)," +
                        "departure_date DATE,"+
                        "return_date DATE,"+
                        "place VARCHAR(255),"+
                        "price INT,"+
                        "currency VARCHAR(255),"+
                        "PRIMARY KEY(id))";
                stmt.executeUpdate(sql1);

                List<String> offersList = travelData.getOffersDescriptionsList("pl_PL", "yyyy-MM-dd");

                for (String offer : offersList) {

                    String[] parts = offer.split("\\s+");

                    // Sprawdzamy, czy tablica ma więcej elementów niż oczekiwano
                    if (parts.length > 6) {
                        // Łączymy części oferty, aby uzyskać poprawną nazwę kraju
                        parts[0] = parts[0] + " " + parts[1];

                        // Przesuwamy pozostałe części oferty
                        for (int i = 1; i < parts.length - 1; i++) {
                            parts[i] = parts[i + 1];
                        }
                    }

                    String country = parts[0];
                    String departureDate = parts[1];
                    String returnDate = parts[2];
                    String place = parts[3];
                    float price = Float.parseFloat(parts[4].replace(",", ".").replace(".", ""));
                    String language = parts[5];


                    java.sql.Date departure_date = null;
                    java.sql.Date return_date = null;

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        departure_date = new java.sql.Date(dateFormat.parse(departureDate).getTime());
                        return_date = new java.sql.Date(dateFormat.parse(returnDate).getTime());

                    } catch (ParseException e) {
                        System.err.println("Błąd parsowania daty: " + e.getMessage());
                        e.printStackTrace();
                    }

                    String sql = "INSERT INTO offers (country, departure_date, return_date, place, price, currency) VALUES (?, ?, ?, ?, ?, ?)";

                    PreparedStatement pstmt = con.prepareStatement(sql);
                    pstmt.setString(1, country);
                    pstmt.setDate(2, departure_date);
                    pstmt.setDate(3, return_date);
                    pstmt.setString(4, place);
                    pstmt.setFloat(5, price);
                    pstmt.setString(6, language);

                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }







    public void showGui() {
        JFrame frame = new JFrame("Travel Offers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JComboBox<String> languageComboBox = new JComboBox<>(new String[]{"pl_PL", "en_GB"});
        JComboBox<String> dateFormatComboBox = new JComboBox<>(new String[]{"yyyy-MM-dd", "dd/MM/yyyy"});

        JButton showOffersButton = new JButton("Show Offers");
        showOffersButton.addActionListener(e -> {
            String selectedLocale = (String) languageComboBox.getSelectedItem();
            String selectedDateFormat = (String) dateFormatComboBox.getSelectedItem();

            List<String> offersList = travelData.getOffersDescriptionsList(selectedLocale, selectedDateFormat);

            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Country");
            model.addColumn("Departure Date");
            model.addColumn("Return Date");
            model.addColumn("Place");
            model.addColumn("Price");
            model.addColumn("Currency");

            for (String offer : offersList) {
                String[] parts = offer.split("\\s+");

                // Sprawdzamy, czy tablica ma więcej elementów niż oczekiwano
                if (parts.length > 6) {
                    // Łączymy części oferty, aby uzyskać poprawną nazwę kraju
                    parts[0] = parts[0] + " " + parts[1];

                    // Przesuwamy pozostałe części oferty
                    for (int i = 1; i < parts.length - 1; i++) {
                        parts[i] = parts[i + 1];
                    }
                }
                model.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]});
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(BorderLayout.CENTER, scrollPane);
            frame.setSize(800, 400);
            frame.setVisible(true);
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Language:"));
        controlPanel.add(languageComboBox);
        controlPanel.add(new JLabel("Date Format:"));
        controlPanel.add(dateFormatComboBox);
        controlPanel.add(showOffersButton);

        frame.getContentPane().add(BorderLayout.SOUTH, controlPanel);
        frame.setSize(800, 400);
        frame.setVisible(true);
    }
}