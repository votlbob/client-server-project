package client;

import database.*;
import database.Record;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientGUI {

    private JFrame frame;
    private JPanel panel;

    private String previousState = "login";
    private String curState = "login";



    private Client client;


    HashMap<String, Supplier<Boolean>> pages;


    private JLabel serverLabel,
            portLabel,
            usernameLabel,
            passwordLabel,
            emailLabel,
            codeLabel;

    private JTextField serverField,
            portField,
            usernameField,
            emailField,
            codeField;
    private JPasswordField passwordField,
            newPasswordField;

    private JButton backButton,
            connectButton,
            signupButton,
            loginButton,
            logoutButton,
            registerButton,
            showPasswordToggle,
            forgotPasswordButton,
            changePasswordButton,
            deleteAccountButton,
            sendCodeButton,
            verifyButton,
            homeButton;
    boolean passwordShown = false;


    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ClientGUI window = new ClientGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public ClientGUI() {

        __init__();

        connect( "127.0.0.1", "8000" );
        loadAccountPage();

    }

    private void __init__() {

        pages = new HashMap<>();
        pages.put( "login", this::loadLoginPage );
        pages.put( "signup", this::loadSignupPage );
        pages.put( "connect", this::loadConnectPage );
        pages.put( "account", this::loadAccountPage );
        pages.put( "verify", this::loadVerificationPage );
        pages.put( "reset", this::loadResetPasswordPage );
        pages.put( "serverdown", this::loadServerDownPage );

        frame = new JFrame();
        panel = new JPanel();

        curState = "";

        // On Close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        frame.setBounds(100, 100, 580, 384);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
    }


    private void load( String page ) {

        pages.get(page).get();

    }

    private boolean loadLoginPage() {

        resetPanel();

        String previousPreviousState = previousState;
        previousState = curState;
        curState = "login";
        frame.setTitle(curState);


        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameLabel.setBounds(120, 37, 87, 26);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setEditable(true);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(205, 41, 120, 19);
        panel.add(usernameField);
        usernameField.setColumns(10);


        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordLabel.setBounds(120, 57, 87, 26);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(205, 61, 120, 19);
        panel.add(passwordField);
        passwordField.setColumns(10);


        showPasswordToggle = new JButton("show password");
        showPasswordToggle.setOpaque(false);
        showPasswordToggle.setContentAreaFilled(false);
        showPasswordToggle.setBorderPainted(false);
        showPasswordToggle.setFont(new Font("Tahoma", Font.PLAIN, 10));
        showPasswordToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });
        showPasswordToggle.setBounds(200, 77, 120, 20);
        panel.add(showPasswordToggle);


        signupButton = new JButton("SIGN-UP");
        signupButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                signupButtonClicked();
            }
        });
        signupButton.setBounds(115, 100, 100, 40);
        panel.add(signupButton);


        loginButton = new JButton("LOG-IN");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButtonClicked();
            }
        });
        loginButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        loginButton.setBounds(225, 100, 100, 40);
        panel.add(loginButton);


        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                forgotPasswordButtonClicked();
            }
        });
        forgotPasswordButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        forgotPasswordButton.setBounds(120, 150, 200, 40);
        panel.add(forgotPasswordButton);


        load();

        if (curState==previousState) return true;
        return false;

    }
    private boolean loadSignupPage() {

        resetPanel();

        String previousPreviousState = previousState;
        previousState = curState;
        curState = "signup";
        frame.setTitle(curState);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameLabel.setBounds(120, 37, 87, 26);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setEditable(true);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(205, 41, 150, 19);
        panel.add(usernameField);
        usernameField.setColumns(10);


        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordLabel.setBounds(120, 57, 87, 26);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(205, 61, 150, 19);
        panel.add(passwordField);
        passwordField.setColumns(10);


        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailLabel.setBounds(120, 77, 87, 26);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setEditable(true);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailField.setBounds(205, 81, 150, 19);
        panel.add(emailField);
        emailField.setColumns(10);


        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerButtonClicked();
            }
        });
        registerButton.setBounds(275, 105, 80, 20);
        panel.add(registerButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadConnectPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "connect";
        frame.setTitle(curState);

        serverLabel = new JLabel("Server IP:");
        serverLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        serverLabel.setBounds(120, 17, 87, 26);
        panel.add(serverLabel);

        serverField = new JTextField();
        serverField.setEditable(true);
        serverField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        serverField.setBounds(205, 21, 120, 20);
        panel.add(serverField);
        serverField.setColumns(10);

        portLabel = new JLabel("PORT:");
        portLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        portLabel.setBounds(120, 37, 87, 26);
        panel.add(portLabel);

        portField = new JTextField();
        portField.setEditable(true);
        portField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        portField.setBounds(205, 41, 120, 20);
        panel.add(portField);
        portField.setColumns(10);

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectButtonClicked();
            }
        });
        connectButton.setBounds(335, 41, 100, 20);
        panel.add(connectButton);

        load();

        return curState.equals(previousState);

    }
    private boolean loadAccountPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "account";
        frame.setTitle(curState);


        JLabel welcome = new JLabel("WELCOME, "+getFirstName()+"!", SwingConstants.CENTER);
        welcome.setFont(new Font("Tahoma", Font.BOLD, 30));
        welcome.setBounds(0, 60, 580, 50);
        panel.add(welcome);


        JLabel username = new JLabel(getUsername());
        username.setFont(new Font("Tahoma", Font.PLAIN, 20));
        username.setBounds(70, 150, 580, 50);
        panel.add(username);

        JLabel email = new JLabel(getEmail());
        email.setFont(new Font("Tahoma", Font.PLAIN, 12));
        email.setBounds(70, 170, 580, 50);
        panel.add(email);

        JLabel name = new JLabel(getLastName()+", "+getFirstName());
        name.setFont(new Font("Tahoma", Font.PLAIN, 15));
        name.setBounds(70, 195, 580, 50);
        panel.add(name);


        logoutButton = new JButton("LOG-OUT");
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logoutButtonClicked();
            }
        });
        logoutButton.setBounds(300, 150, 180, 40);
        panel.add(logoutButton);

        changePasswordButton = new JButton("CHANGE PASSWORD");
        changePasswordButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        changePasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changePasswordButtonClicked();
            }
        });
        changePasswordButton.setBounds(300, 200, 180, 40);
        panel.add(changePasswordButton);


        deleteAccountButton = new JButton("delete account");
        deleteAccountButton.setOpaque(false);
        deleteAccountButton.setContentAreaFilled(false);
        deleteAccountButton.setBorderPainted(false);
        deleteAccountButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        deleteAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAccountButtonClicked();
            }
        });
        deleteAccountButton.setBounds(220, 290, 120, 40);
        panel.add(deleteAccountButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadVerificationPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "verify";
        frame.setTitle("");


        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        emailLabel.setBounds(120, 40, 100, 40);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setEditable(true);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        emailField.setBounds(120, 70, 230, 30);
        panel.add(emailField);
        emailField.setColumns(10);

        sendCodeButton = new JButton("send code");
        sendCodeButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        sendCodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendCodeButtonClicked();
            }
        });
        sendCodeButton.setBounds(360, 70, 100, 30);
        panel.add(sendCodeButton);


        codeLabel = new JLabel("Code:");
        codeLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        codeLabel.setBounds(120, 90, 100, 40);
        panel.add(codeLabel);

        codeField = new JTextField();
        codeField.setEditable(true);
        codeField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        codeField.setBounds(120, 120, 230, 30);
        panel.add(codeField);
        codeField.setColumns(10);

        verifyButton = new JButton("verify");
        verifyButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        verifyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ( verify( Integer.parseInt(codeField.getText()) ) ) {

                    loadResetPasswordPage();

                }
            }
        });
        verifyButton.setBounds(360, 120, 100, 30);
        panel.add(verifyButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadResetPasswordPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "reset";
        frame.setTitle("");


        JLabel newPasswordLabel = new JLabel("Password:");
        newPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        newPasswordLabel.setBounds(120, 40, 280, 40);
        panel.add(newPasswordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        passwordField.setBounds(120, 70, 280, 30);
        panel.add(passwordField);
        passwordField.setColumns(10);


        JLabel retypePasswordLabel = new JLabel("Re-type Password:");
        retypePasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        retypePasswordLabel.setBounds(120, 90, 280, 40);
        panel.add(retypePasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setEditable(true);
        newPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        newPasswordField.setBounds(120, 120, 280, 30);
        panel.add(newPasswordField);
        newPasswordField.setColumns(10);


        showPasswordToggle = new JButton("show passwords");
        showPasswordToggle.setOpaque(false);
        showPasswordToggle.setContentAreaFilled(false);
        showPasswordToggle.setBorderPainted(false);
        showPasswordToggle.setFont(new Font("Tahoma", Font.PLAIN, 10));
        showPasswordToggle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });
        showPasswordToggle.setBounds(200, 150, 120, 20);
        panel.add(showPasswordToggle);


        JButton changeButton = new JButton("continue");
        changeButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        changeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (Arrays.equals( passwordField.getPassword(),
                        newPasswordField.getPassword() )) {

                    if ( changePassword( new String(passwordField.getPassword()) ) ) {

                        loadLoginPage();

                    }

                }

            }
        });
        changeButton.setBounds(200, 180, 120, 20);
        panel.add(changeButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadServerDownPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "serverdown";
        frame.setTitle(curState);



        load();

        return curState.equals(previousState);

    }
    private void loadChooseUsernamePage() {

        resetPanel();

        String previousPreviousState = previousState;
        previousState = curState;
        curState = "signup";
        frame.setTitle(curState);


        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameLabel.setBounds(120, 37, 87, 26);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setEditable(true);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(205, 41, 150, 19);
        panel.add(usernameField);
        usernameField.setColumns(10);


        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordLabel.setBounds(120, 57, 87, 26);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(205, 61, 150, 19);
        panel.add(passwordField);
        passwordField.setColumns(10);


        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailLabel.setBounds(120, 77, 87, 26);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setEditable(true);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailField.setBounds(205, 81, 150, 19);
        panel.add(emailField);
        emailField.setColumns(10);


        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerButtonClicked();
            }
        });
        registerButton.setBounds(275, 105, 80, 20);
        panel.add(registerButton);


        load();

    }


    private void connectButtonClicked() {

        String IP = serverField.getText();
        String PORT = portField.getText();
        System.out.println(PORT);

        connect( IP, PORT );

    }
    private void loginButtonClicked() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if ( login( username, password ) ) {
            loadAccountPage();
        }

    }
    private void logoutButtonClicked() {

        logout();

    }
    private void signupButtonClicked() {

        loadSignupPage();

    }
    private void registerButtonClicked() {

        if ( register( usernameField.getText(),
                new String(passwordField.getPassword()),
                emailField.getText()) );
        loadAccountPage();

    }
    private void deleteAccountButtonClicked() {

        if ( JOptionPane.showConfirmDialog(frame, "DELETE Account?") ==
                JOptionPane.YES_OPTION ) {

            client.delete();
            loadLoginPage();

        }

    }
    private void changePasswordButtonClicked() {

        loadResetPasswordPage();

    }
    private void forgotPasswordButtonClicked() {

        loadVerificationPage();

    }
    private void sendCodeButtonClicked() {

    }
    private void backButtonClicked() {

        switch( previousState ) {

            case( "login" ):
                break;
            default:
                load(previousState);
                break;

        }

    }
    private void togglePasswordVisibility() {
        if ( !passwordShown ) {

            for (Component c : panel.getComponents()) {
                try {
                    ((JPasswordField) c).setEchoChar((char) 0);
                } catch (Exception ignored) {
                }
            }

        } else {

            for (Component c : panel.getComponents()) {
                try {
                    ((JPasswordField) c).setEchoChar('●');
                } catch (Exception ignored) {
                }
            }

        }

        passwordShown = !passwordShown;
    }


    private void closeWindow() {

        logout();
        if (client!=null) client.disconnect();

    }


    private void resetPanel() {
        frame.getContentPane().remove(panel);
        panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
    }
    private void load() {
        frame.revalidate();
        frame.repaint();
    }


    private void connect( String IP,
                          String PORT ) {
        try {

            client = new Client( this, IP, PORT );
            loadLoginPage();

        }
        catch (UnknownHostException e) {
            System.out.println("Host " + IP + " at port " + PORT + " is unavailable.");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to create I/O streams.");
        }
    }
    private boolean login( String username,
                           String password ) {

        return client.login( username, password );

    }
    private boolean register( String username,
                              String password,
                              String email) {

        return client.register( username, password, email );

    }
    private void logout() {

        loadLoginPage();

    }
    private boolean verify( int code ) {
        return client.checkVerificationCode( code );
    }
    private boolean changePassword( String password ) {

        return client.changePassword( password );

    }


    private String getUsername() {

        return client.information()[0];

    }
    private String getEmail() {

        return client.information()[1];

    }
    private String getFirstName() {

        return client.information()[2];

    }
    private String getLastName() {

        return client.information()[3];

    }


}