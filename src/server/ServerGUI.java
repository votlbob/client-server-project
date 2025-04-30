package server;

import database.Record;
import database.DBMScsv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ServerGUI extends JFrame {

    private JTable clientTable;
    private DefaultTableModel tableModel;
    private JTextField portField;
    private JLabel serverStatusLabel;
    private Server server;
    private DBMScsv database;

    public ServerGUI() {
        setTitle("User Server");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // === Top Panel (Status + Port) ===
        JPanel topPanel = new JPanel(new BorderLayout());

        serverStatusLabel = new JLabel("Server Status: Offline");
        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(serverStatusLabel, BorderLayout.WEST);

        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        portPanel.add(new JLabel("PORT:"));
        portField = new JTextField("8000", 6);
        portPanel.add(portField);
        topPanel.add(portPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // === Table Panel (User Table) ===
        String[] columnNames = {"username", "email", "Active?"};
        tableModel = new DefaultTableModel(columnNames, 0);
        clientTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(clientTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        add(tableScrollPane, BorderLayout.CENTER);

        // === Bottom Panel (Buttons) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton startButton = new JButton("Start Server");
        JButton stopButton = new JButton("Stop Server");
        JButton resetButton = new JButton("Reset Server");

        startButton.addActionListener(e -> startServerButtonClicked());
        stopButton.addActionListener(e -> stopServerButtonClicked());
        resetButton.addActionListener(e -> resetServerButtonClicked());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load database to initially populate table
        database = new DBMScsv();
        try {
            database.connect("whitelist.csv");
        } catch (FileNotFoundException e) {
            System.out.println("Could not load whitelist.csv");
        }

        refreshClientTable();
        setVisible(true);
    }

    private void startServerButtonClicked() {
        int port = Integer.parseInt(portField.getText().trim());
        server = new Server();
        serverStatusLabel.setText("Server Status: Online");
        refreshClientTable();
    }

    private void stopServerButtonClicked() {
        if (server != null) {
            server.shutdown();
            serverStatusLabel.setText("Server Status: Offline");
        }
    }

    private void resetServerButtonClicked() {
        stopServerButtonClicked();
        startServerButtonClicked();
    }

    public void refreshClientTable() {
        tableModel.setRowCount(0);
        if (database != null) {
            ArrayList<Record> users = database.getTable().getTable();
            for (Record r : users) {
                try {
                    String username = r.getValue("username");
                    String email = r.getValue("email");
                    String active = r.getValue("active");
                    tableModel.addRow(new Object[]{username, email, active});
                } catch (IllegalArgumentException e) {
                    System.out.println("Malformed record: " + r);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }

}