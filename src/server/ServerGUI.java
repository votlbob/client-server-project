package server;

import server.database.DBMScsv;
import server.database.Record;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
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

    private static final String filename = "C:\\Users\\fabou\\IdeaProjects\\Client Server Project\\src\\server\\database\\registry.csv";
    private static final File file = new File( filename );
    public final DBMScsv database = new DBMScsv();


    public ServerGUI() {

        __init__();

    }


    private void __init__() {

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        try {
            database.connect( filename );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        server = new Server( 8000, database );
        serverUp = false;

        setTitle("Server GUI");
        setSize(580, 384);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadPanel();

        getContentPane().add(panel, BorderLayout.CENTER);

    }
    private void loadPanel() {

        panel = new JPanel();

        serverStatusLabel = new JLabel("SERVER STATUS: " + (serverUp?"online":"offline") );
        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(serverStatusLabel, BorderLayout.WEST);

        portLabel = new JLabel("PORT: " + server.getPort() );
        portLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(portLabel, BorderLayout.EAST);
        //portField = new JTextField(String.valueOf( server.getPort() ));


        // === Table Panel (User Table) ===
        String[] columnNames = {"username", "email", "Access IP"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBounds( 0, 200, 580, 200 );
        tableScrollPane.setPreferredSize(new Dimension(580, 250));
        panel.add(tableScrollPane);
        updateDisplayTable();
        startFileMonitor();

        // === Bottom Panel (Buttons) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        startServerButton = new JButton("Start Server");
        stopServerButton = new JButton("Stop Server");
        restartServerButton = new JButton("Restart Server");

        startServerButton.addActionListener(e -> startServerButtonClicked());
        stopServerButton.addActionListener(e -> stopServerButtonClicked());
        restartServerButton.addActionListener(e ->  restartServerButtonClicked());

        buttonPanel.add(startServerButton);
        buttonPanel.add(stopServerButton);
        buttonPanel.add(restartServerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

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


    public void updateDisplayTable() {

        database.refresh();
        tableModel.setRowCount(0);

        for ( ConnectionThread c : server.getConnections() ) {

            String[] info = new String[]{ "", "", c.id() };

            if ( database.contains("IP="+c.id()) ) {

                Record user = database.select("IP="+info[2]).get(0);

                info[0] = user.getValue("USERNAME");
                info[1] = user.getValue("EMAIL");

            }
            tableModel.addRow( info );

        }

    }
    public void startFileMonitor() {

        new Thread(() -> {
            long lastModified = 0;
            while (true) {
                if (file.exists() && file.lastModified() != lastModified) {
                    lastModified = file.lastModified();
                    SwingUtilities.invokeLater( this::updateDisplayTable );
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

    }
    public void closeWindow() {

        stopServer();

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