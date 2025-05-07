package server;

import server.database.DBMScsv;
import server.database.Record;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerGUI extends JFrame {


    private Server server;

    private boolean serverUp;
    private JFrame registry;


    private JPanel panel;
    private JTable userTable,
                   loggerTable;
    private JButton startServerButton,
                    stopServerButton,
                    restartServerButton,
                    registryButton,
                    viewLogButton;
    private JLabel addressLabel,
                   portLabel,
                   serverStatusLabel,
                   registeredUsersLabel;
    private JTextField portField;
    private DefaultTableModel tableModel,
                              loggerTableModel;

    private static final String filename = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "server", "database", "registry.csv").toString();
    private static final String logfilename = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "server", "serverlog.csv").toString();


    private static final File file = new File( filename );
    private static final File logfile = new File( logfilename );

    volatile DBMScsv database = new DBMScsv();
    public final DBMScsv log = new DBMScsv();


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
            log.connect( logfilename );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        server = new Server( 8000, database, this );
        serverUp = false;

        setTitle("Server GUI");
        setSize(580, 384);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        loadPanel();

        getContentPane().add(panel, BorderLayout.CENTER);

    }
    private void loadPanel() {

        panel = new JPanel();

        serverStatusLabel = new JLabel("SERVER STATUS: " + (serverUp?"online":"offline") );
        serverStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        serverStatusLabel.setBounds( 15, 15, 60, 20 );
        panel.add(serverStatusLabel);


        // === Table Panel (User Table) ===
        String[] columnNames = {"username", "email", "Access IP"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBounds( 10, 200, 560, 200 );
        tableScrollPane.setPreferredSize(new Dimension(560, 250));
        panel.add(tableScrollPane);
        updateDisplayTable();
        startFileMonitor();

        // === Bottom Panel (Buttons) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        startServerButton = new JButton("Start Server");
        stopServerButton = new JButton("Stop Server");
        registryButton = new JButton("Registry");
        viewLogButton = new JButton("View Log");

        startServerButton.addActionListener(e -> startServerButtonClicked());
        stopServerButton.addActionListener(e -> stopServerButtonClicked());
        registryButton.addActionListener(e ->  registryButtonClicked());
        viewLogButton.addActionListener(e ->  viewLogButtonClicked());

        buttonPanel.add(startServerButton);
        buttonPanel.add(stopServerButton);
        buttonPanel.add(registryButton);
        //buttonPanel.add(viewLogButton);
        panel.add(buttonPanel);

        registeredUsersLabel = new JLabel("Registered Users: "+database.getTable().getTable().size());
        registeredUsersLabel.setBounds(10,450,50,20);
        panel.add(registeredUsersLabel);

        setVisible(true);

    }


    private void startServerButtonClicked() {

        startServer();

    }
    private void stopServerButtonClicked() {

        serverUp = false;
        serverStatusLabel.setText( "SERVER STATUS: offline" );

        stopServer();

    }
    private void restartServerButtonClicked() {

        restartServer();

    }
    private void registryButtonClicked() {

        registry = new JFrame();
        Thread REGISTRY;

        registry.setBounds(100, 100, 580, 384);
        registry.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel registryPanel = new JPanel();


        String[] columnNames = {"username", "last", "first", "email", "password", "ip"};
        DefaultTableModel registryTableModel = new DefaultTableModel(columnNames, 0);
        JTable registryUserTable = new JTable(registryTableModel);
        JScrollPane registryTableScrollPane = new JScrollPane(registryUserTable);
        registryTableScrollPane.setBounds( 10, 200, 560, 200 );
        registryTableScrollPane.setPreferredSize(new Dimension(560, 250));
        registryPanel.add(registryTableScrollPane);


        REGISTRY = new Thread(() -> {

            long lastModified = 0;

            while ( !Thread.currentThread().isInterrupted() ) {

                if ( file.exists() && lastModified+200 < file.lastModified() ) {

                    //System.out.println(file.lastModified());
                    lastModified = file.lastModified();

                    database.refresh();
                    registryTableModel.setRowCount(0);

                    for ( Record user : database.getTable().getTable() ) {

                        String[] info = new String[]{ user.getValue("USERNAME"),
                                user.getValue("LAST"),
                                user.getValue("FIRST"),
                                user.getValue("EMAIL"),
                                user.getValue("PASSWORD"),
                                user.getValue("IP") };

                        registryTableModel.addRow( info );

                    }

                    registry.setIgnoreRepaint( false );


                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        });
        REGISTRY.start();


        registry.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                REGISTRY.interrupt();
                Thread.currentThread().interrupt();
            }
        });

        registry.getContentPane().add( registryPanel );
        registry.setVisible( true );

    }
    private void viewLogButtonClicked() {

        JFrame logger = new JFrame();

        logger.setBounds(100, 100, 580, 384);
        logger.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


        JPanel loggerPanel = new JPanel();


        String[] columnNames = {"SERVER LOG"};
        DefaultTableModel loggerTableModel = new DefaultTableModel(columnNames, 0);
        JTable loggerTable = new JTable(loggerTableModel);
        JScrollPane loggerTableScrollPane = new JScrollPane(loggerTable);
        loggerTableScrollPane.setBounds( 0, 200, 580, 200 );
        loggerTableScrollPane.setPreferredSize(new Dimension(580, 250));
        loggerPanel.add(loggerTableScrollPane);


        new Thread(() -> {
            long lastModified = 0;
            while (true) {
                if (logfile.exists() && logfile.lastModified() != lastModified) {
                    lastModified = logfile.lastModified();
                    SwingUtilities.invokeLater( this::updateLogTable );
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();


        logger.getContentPane().add( loggerPanel );
        logger.setVisible( true );

    }
    /*private void databaseButtonClicked() {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DBMSApp window = new DBMSApp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }*/


    public ArrayList<String[]> refreshClientTable() {

        return server.getRegisteredUsers();

    }


    private void startServer() {

        serverUp=true;
        serverStatusLabel.setText( "SERVER STATUS: online" );
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
    public void updateLogTable() {

        log.refresh();
        loggerTableModel.setRowCount(0);

        for ( Record user : database.getTable().getTable() ) {


            String[] info = new String[]{ ""+user.getFields().size() };
            loggerTableModel.addRow( info );


        }

    }
    public void updateUserCount() {

        registeredUsersLabel.setText("Registered Users: "+database.getTable().getTable().size());

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

        if ( registry!=null ) registry.dispose();
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