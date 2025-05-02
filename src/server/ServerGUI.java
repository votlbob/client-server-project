package server;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ServerGUI extends JFrame {


    private Server server;
    private boolean serverUp;


    private JPanel panel;
    private JTable userTable;
    private JButton startServerButton,
                    stopServerButton,
                     restartServerButton;
    private JLabel addressLabel,
                   portLabel,
                   serverStatusLabel;
    private JTextField portField;
    private DefaultTableModel tableModel;


    public ServerGUI() {

        __init__();

    }


    private void __init__() {

        server = new Server( 8000 );

        setTitle("Server GUI");
        setSize(580, 384);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadPanel();

        getContentPane().add(panel, BorderLayout.CENTER);

    }
    private void loadPanel() {

        panel = new JPanel();

        serverStatusLabel = new JLabel("SERVER STATUS: Offline");
        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(serverStatusLabel, BorderLayout.CENTER);

        portLabel = new JLabel("PORT: ");
        portField = new JTextField(String.valueOf( server.getPort() ));


        // === Table Panel (User Table) ===
        String[] columnNames = {"username", "email", "Active?"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 300));
        add(tableScrollPane, BorderLayout.CENTER);

        // === Bottom Panel (Buttons) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        JButton startButton = new JButton("Start Server");
        JButton stopButton = new JButton("Stop Server");
        JButton resetButton = new JButton("Reset Server");

        startButton.addActionListener(e -> startServerButtonClicked());
        stopButton.addActionListener(e -> stopServerButtonClicked());
        resetButton.addActionListener(e ->  restartServerButtonClicked());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);

    }


    private void startServerButtonClicked() {

        startServer();

    }
    private void stopServerButtonClicked() {

        stopServer();

    }
    private void restartServerButtonClicked() {

        restartServer();

    }


    public ArrayList<String[]> refreshClientTable() {

        return server.getRegisteredUsers();

    }


    private void startServer() {

        server.start();

    }
    private void stopServer() {

        server.stop();

    }
    private void restartServer() {

        server.restart();

    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGUI window = new ServerGUI();
                    window.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

}