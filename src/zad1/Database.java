package zad1;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
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

//                Statement stmt= con.createStatement();
//                String sql1 = "CREATE TABLE offers (id INT AUTO_INCREMENT," +
//                        "country VARCHAR(255)," +
//                        "departure_date DATE,"+
//                        "return_date DATE,"+
//                        "place VARCHAR(255),"+
//                        "price INT,"+
//                        "currency VARCHAR(255),"+
//                        "PRIMARY KEY(id))";
//                stmt.executeUpdate(sql1);

                List<String> offersList = travelData.getOffersDescriptionsList("pl_PL", "yyyy-MM-dd");

                for (String offer : offersList) {
                    System.out.println(offer);
                    String[] parts = offer.split(" ");
                    String sql = "INSERT INTO offers (country, departure_date, return_date, place, price, currency) VALUES (?, ?, ?, ?, ?, ?)";

                    System.out.println(parts[3]);
                    java.sql.Date departure_date = java.sql.Date.valueOf(parts[2]);
                    java.sql.Date return_date = java.sql.Date.valueOf(parts[3]);

                    int price = Integer.parseInt(parts[5]);

                    PreparedStatement pstmt = con.prepareStatement(sql);
                        pstmt.setString(1, parts[1]);
                        pstmt.setDate(2, departure_date);
                        pstmt.setDate(3, return_date);
                        pstmt.setString(4, parts[4]);
                        pstmt.setInt(5, price);
                        pstmt.setString(6, parts[6]);

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
                String[] parts = offer.split(" ");
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