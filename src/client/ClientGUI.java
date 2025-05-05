package client;

import java.awt.*;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;

public class ClientGUI extends JFrame {

    private JPanel panel;

    private String previousState = "login";
    private String curState = "login";



    private Client client;


    HashMap<String, Supplier<Boolean>> pages;


    private JLabel serverLabel,
                   usernameLabel,
                   passwordLabel,
                   confirmPasswordLabel,
                   emailLabel,
                   codeLabel;

    private JTextField serverField,
                       usernameField,
                       emailField,
                       codeField,
                       firstNameField,
                       lastNameField;
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
        EventQueue.invokeLater( () -> {
            try {
                ClientGUI window = new ClientGUI();
                window.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public ClientGUI() {

        __init__();

        connect( "127.0.0.1", "login");

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

        panel = new JPanel();

        curState = "";

        // On Close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        setBounds(100, 100, 580, 384);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        loadConnectPage();

    }


    private void load( String page ) {

        pages.get(page).get();

    }

    private boolean loadLoginPage() {

        resetPanel();

        String previousPreviousState = previousState;
        previousState = curState;
        curState = "login";
        setTitle(curState);


        backButton = new JButton("<-");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        backButton.addActionListener( e -> backButtonClicked() );
        backButton.setBounds(15, 15, 60, 20);
        panel.add(backButton);


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


        showPasswordToggle = new JButton((passwordShown?"hide":"show") + " password");
        showPasswordToggle.setOpaque(false);
        showPasswordToggle.setContentAreaFilled(false);
        showPasswordToggle.setBorderPainted(false);
        showPasswordToggle.setFont(new Font("Tahoma", Font.PLAIN, 10));
        showPasswordToggle.addActionListener( e -> togglePasswordVisibility() );
        showPasswordToggle.setBounds(200, 77, 120, 20);
        panel.add(showPasswordToggle);


        signupButton = new JButton("SIGN-UP");
        signupButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        signupButton.addActionListener( e -> signupButtonClicked() );
        signupButton.setBounds(115, 100, 100, 40);
        panel.add(signupButton);


        loginButton = new JButton("LOG-IN");
        loginButton.addActionListener( e -> loginButtonClicked() );
        loginButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        loginButton.setBounds(225, 100, 100, 40);
        panel.add(loginButton);


        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.addActionListener( e -> forgotPasswordButtonClicked() );
        forgotPasswordButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        forgotPasswordButton.setBounds(120, 150, 200, 40);
        panel.add(forgotPasswordButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadSignupPage() {

        resetPanel();

        String previousPreviousState = previousState;
        previousState = curState;
        curState = "signup";
        setTitle(curState);


        backButton = new JButton("<-");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        backButton.addActionListener( e -> backButtonClicked() );
        backButton.setBounds(15, 15, 60, 20);
        panel.add(backButton);


        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameLabel.setBounds(120, 37, 87, 26);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setEditable(true);
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        usernameField.setBounds(255, 41, 150, 19);
        panel.add(usernameField);
        usernameField.setColumns(10);


        JLabel firstNameLabel = new JLabel("First name:");
        firstNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        firstNameLabel.setBounds(120, 57, 87, 26);
        panel.add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setEditable(true);
        firstNameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        firstNameField.setBounds(255, 61, 150, 19);
        panel.add(firstNameField);
        firstNameField.setColumns(10);

        JLabel lastNameLabel = new JLabel("Last name:");
        lastNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lastNameLabel.setBounds(120, 77, 87, 26);
        panel.add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setEditable(true);
        lastNameField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lastNameField.setBounds(255, 81, 150, 19);
        panel.add(lastNameField);
        lastNameField.setColumns(10);


        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordLabel.setBounds(120, 97, 87, 26);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        passwordField.setBounds(255, 101, 150, 19);
        panel.add(passwordField);
        passwordField.setColumns(10);


        confirmPasswordLabel = new JLabel("Re-type password:");
        confirmPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        confirmPasswordLabel.setBounds(120, 117, 87, 26);
        panel.add(confirmPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setEditable(true);
        newPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        newPasswordField.setBounds(255, 121, 150, 19);
        panel.add(newPasswordField);
        newPasswordField.setColumns(10);


        emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailLabel.setBounds(120, 137, 87, 26);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setEditable(true);
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 12));
        emailField.setBounds(255, 141, 150, 19);
        panel.add(emailField);
        emailField.setColumns(10);


        showPasswordToggle = new JButton((passwordShown?"hide":"show") + " password");
        showPasswordToggle.setOpaque(false);
        showPasswordToggle.setContentAreaFilled(false);
        showPasswordToggle.setBorderPainted(false);
        showPasswordToggle.setFont(new Font("Tahoma", Font.PLAIN, 10));
        showPasswordToggle.addActionListener( e -> togglePasswordVisibility() );
        showPasswordToggle.setBounds(180, 165, 120, 20);
        panel.add(showPasswordToggle);


        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        registerButton.addActionListener( e -> registerButtonClicked() );
        registerButton.setBounds(325, 165, 80, 20);
        panel.add(registerButton);


        load();

        return curState.equals(previousState);

    }
    private boolean loadConnectPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "connect";
        setTitle(curState);

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

        connectButton = new JButton("Connect");
        connectButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        connectButton.addActionListener( e -> connectButtonClicked() );
        connectButton.setBounds(335, 21, 100, 20);
        panel.add(connectButton);

        load();

        return curState.equals(previousState);

    }
    private boolean loadAccountPage() {

        resetPanel();

        String previousPreviousState = curState;
        previousState = curState;
        curState = "account";
        setTitle(curState);

        String[] info = getInfo();

        JLabel welcome = new JLabel("WELCOME, "+info[2]+"!", SwingConstants.CENTER);
        welcome.setFont(new Font("Tahoma", Font.BOLD, 30));
        welcome.setBounds(0, 60, 580, 50);
        panel.add(welcome);


        JLabel username = new JLabel(info[0]);
        username.setFont(new Font("Tahoma", Font.PLAIN, 20));
        username.setBounds(70, 150, 580, 50);
        panel.add(username);

        JLabel email = new JLabel(info[3]);
        email.setFont(new Font("Tahoma", Font.PLAIN, 12));
        email.setBounds(70, 170, 580, 50);
        panel.add(email);

        JLabel name = new JLabel(info[1]+", "+info[2]);
        name.setFont(new Font("Tahoma", Font.PLAIN, 15));
        name.setBounds(70, 195, 580, 50);
        panel.add(name);


        logoutButton = new JButton("LOG-OUT");
        logoutButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        logoutButton.addActionListener( e -> logoutButtonClicked() );
        logoutButton.setBounds(300, 150, 180, 40);
        panel.add(logoutButton);

        changePasswordButton = new JButton("CHANGE PASSWORD");
        changePasswordButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        changePasswordButton.addActionListener( e -> loadResetPasswordPage() );
        changePasswordButton.setBounds(300, 200, 180, 40);
        panel.add(changePasswordButton);


        deleteAccountButton = new JButton("delete account");
        deleteAccountButton.setOpaque(false);
        deleteAccountButton.setContentAreaFilled(false);
        deleteAccountButton.setBorderPainted(false);
        deleteAccountButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        deleteAccountButton.addActionListener( e -> deleteAccountButtonClicked() );
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
        setTitle("");


        backButton = new JButton("<-");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        backButton.addActionListener( e -> backButtonClicked() );
        backButton.setBounds(15, 15, 60, 20);
        panel.add(backButton);


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
        sendCodeButton.addActionListener( e -> sendCodeButtonClicked() );
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
        verifyButton.addActionListener( e -> {
            if ( verify( Integer.parseInt(codeField.getText()) ) ) {

                loadResetPasswordPage();

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
        setTitle("");


        backButton = new JButton("<-");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        backButton.addActionListener( e -> backButtonClicked() );
        backButton.setBounds(15, 15, 60, 20);
        panel.add(backButton);


        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        passwordLabel.setBounds(120, 40, 280, 40);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setEditable(true);
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 15));
        passwordField.setBounds(120, 70, 280, 30);
        panel.add(passwordField);
        passwordField.setColumns(10);


        JLabel retypePasswordLabel = new JLabel("Re-type password:");
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
        showPasswordToggle.addActionListener( e -> togglePasswordVisibility() );
        showPasswordToggle.setBounds(200, 150, 120, 20);
        panel.add(showPasswordToggle);


        JButton changeButton = new JButton("change");
        changeButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        changeButton.addActionListener( e -> changePasswordButtonClicked() );
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
        setTitle(curState);



        load();

        return curState.equals(previousState);

    }


    private void connectButtonClicked() {

        String IP = serverField.getText();

        connect( IP, "login" );

    }
    private void loginButtonClicked() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        switch( login( username, password ) ) {

            case( "confirm" ):
                loadAccountPage();
                break;

            case( "deny" ):
                loadLoginPage();
                break;

            case( "connection_invalid" ):
                loadConnectPage();
                break;

        }

    }
    private void logoutButtonClicked() {

        logout();

    }
    private void signupButtonClicked() {

        loadSignupPage();

    }
    private void registerButtonClicked() {

        if ( passwordField.getPassword().length != 0
                            &&
             Arrays.equals( passwordField.getPassword(),
                            newPasswordField.getPassword() )) {

            String s = register( usernameField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    emailField.getText(),
                    new String(passwordField.getPassword()) );

            switch( s ) {

                case( "confirm" ):
                    loadLoginPage();
                    break;

                case( "deny" ):
                    loadSignupPage();
                    break;

                case( "connection_invalid" ):
                    loadConnectPage();
                    break;

                default:
                    System.out.println( s );
                    loadSignupPage();
                    break;

            }

        }

    }
    private void deleteAccountButtonClicked() {

        if ( JOptionPane.showConfirmDialog(this, "DELETE Account?") ==
                JOptionPane.YES_OPTION ) {

            client.delete();
            loadLoginPage();

        }

    }
    private void changePasswordButtonClicked() {

        if (Arrays.equals( passwordField.getPassword(),
                newPasswordField.getPassword() )) {

            if ( changePassword( new String(passwordField.getPassword()) ) ) {

                loadLoginPage();

            }

        }

    }
    private void forgotPasswordButtonClicked() {

        loadVerificationPage();

    }
    private void sendCodeButtonClicked() {

        String email = emailField.getText();

        if ( email.equals( client.information()[3] ) ) {

            client.send( "request-code:"+email );

        }

    }
    private void backButtonClicked() {

        switch( curState ) {

            case( "verify" ):
                load("login");
                break;

            case( "login" ):
                try { client.disconnect(); }
                catch( Exception ignored ) {}
                load( "connect" );
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
        showPasswordToggle.setText( (passwordShown?"hide":"show") + " password" );

    }


    private void closeWindow() {

        logout();
        try{ client.disconnect(); }
        catch( Exception ignored ) {}

    }


    private void resetPanel() {
        getContentPane().remove(panel);
        panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);
    }
    private void load() {
        revalidate();
        repaint();
    }


    private void connect( String IP,
                          String onSuccess ) {
        try {

            client = new Client( this, IP );
            System.out.println( "CONNECTED: "+ client.getIP() +"\n" );
            load(onSuccess);

        }
        catch (UnknownHostException e) {
            System.out.println("Host " + IP + " is unavailable.");
            loadConnectPage();
        }
        catch (IOException e) {
            System.out.println("Unable to create I/O streams.");
            loadConnectPage();
        }
    }
    private String login( String username,
                           String password ) {

        return client.login( username, password );

    }
    private String register( String... info ) {

        return client.register( info );

    }
    private void logout() {

        if ( client != null ) client.logout();
        loadLoginPage();

    }
    private boolean verify( int code ) {

        return client.checkVerificationCode( code );

    }
    private boolean changePassword( String password ) {

        return client.changePassword( password );

    }


    private String[] getInfo() {

        return client.information();

    }


}