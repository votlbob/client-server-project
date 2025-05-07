package server;

import server.database.DBMScsv;
import server.database.Record;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.*;


public class Server {
    
    private ServerGUI GUI;

    volatile boolean status;

    private final int PORT;

    volatile DBMScsv database;
    //private final DBMScsv log;

    private ServerSocket serversocket;

    volatile Vector<ConnectionThread> clientconnections;


    private Thread MAIN;
    private int lastCode = 0;


    public Server( int port,
                   DBMScsv database,
                   ServerGUI GUI ) {

        status = false;

        this.database = database;
        this.GUI = GUI;
        //this.log = log;

        PORT = port;

        clientconnections = new Vector<ConnectionThread>();

    }


    public void peerconnection( Socket socket ) {

        String socketIP = socket.getInetAddress().getHostAddress();
        String selfID = "127.0.0.1";
        String serverIP = serversocket.getInetAddress().getHostAddress();
        try { serverIP = Inet4Address.getLocalHost().getHostAddress(); }
        catch( Exception e ) { e.printStackTrace(); }

        String connectionID = (socketIP.equals(selfID))?serverIP:socketIP;

        /*System.out.println( "SOCKETIP: "+socketIP );
        System.out.println( "SELFIP:   "+selfID );
        System.out.println( "SERVERIP: "+serverIP );*/

        // -- when a client arrives, create a thread for their communication
        ConnectionThread connection = new ConnectionThread( connectionID,
                                                            socket,
                                                            this);

        // -- add the thread to the active client threads list
        clientconnections.add(connection);

        // -- start the thread
        connection.start();

        System.out.println("SERVER: connection received for id " + connection.id() + "\n");

    }


    // -- called by a ServerThread when a client is terminated

    public void start() {
        if ( !status ) {

            System.out.println( "SERVER START METHOD" );

            status = true;

            try {

                serversocket = new ServerSocket(PORT);   //PORT = 8000


                MAIN = new Thread( () -> {
                    try {
                        while (status) {


                            if (Thread.currentThread().isInterrupted()) {

                                status = false;
                                throw new InterruptedException();

                            } else {
                                try {

                                    Socket socket = serversocket.accept();
                                    // -- connection accepted, create a peer-to-peer socket
                                    //    between the server (thread) and client
                                    peerconnection(socket);

                                } catch (SocketException e) {
                                    status = false;
                                    Thread.currentThread().interrupt();
                                    System.out.println("\n" + "SOCKET CLOSED" + "\n");
                                }

                            }

                        }
                        System.out.println("SERVER: offline");


                    } catch (IOException e) {
                        e.printStackTrace();
                        status = false;
                    } catch (InterruptedException e) {
                        System.out.println( "SUPER INTERRUPTED" );
                        Thread.currentThread().interrupt();
                    }

                });
                MAIN.start();


                System.out.println("SERVER: online\n");


            } catch( IOException e ) {
                status = false;
                System.out.println( "SERVER FAILED TO START" );
                e.printStackTrace();
            }


        }
    }
    public boolean stop() {

        if (status) {

            try {
                System.out.println("HERE");
                serversocket.close();
            } catch (IOException e) {
                System.out.println("BRUH");
            }

            try {

                status = false;

                System.out.println("REALLY HERE");

                serversocket.close();
                removeAll();
                MAIN.interrupt();
                Thread.currentThread().interrupt();


                System.out.print(status);
                if (status) {
                    status = false;
                }
                System.out.println(" -> " + status);

                return true;

            } catch (Exception e) {
                System.out.println("SERVER FAILED TO CLOSE");
                e.printStackTrace();
                return false;
            }
        }

        return false;

    }
    public void restart() {
        stop();
        start();
    }
    public void shutdown() {
        stop();
        System.exit(1);
    }

    public boolean login( String username,
                          String password,
                          String ip ) {

        Record user;

        try {

            user = database.select("USERNAME=" + username).get(0);
            database.update( "USERNAME="+username,
                             "IP="+ip );

        } catch( IndexOutOfBoundsException e ) {
            System.out.println( "user doesn't exist" );
            return false;
        }

        return password.equals( user.getValue("PASSWORD") );

    }

    public boolean logout( String ip ) {

        return database.update("IP=" + ip,
                "IP=") != null;


    }
    public String register( String username,
                            String first,
                            String last,
                            String email,
                            String password ) {

        if ( checkUsernameFormat( username ) ) {
            if ( checkPasswordFormat( password ) ) {
                if ( checkEmailFormat( email ) ) {
                    if ( isAvailable( "USERNAME="+username ) ) {
                        if ( isAvailable( "EMAIL="+email ) ) {

                            database.insert( "USERNAME="+username,
                                                    "FIRST="+first,
                                                    "LAST="+last,
                                                    "EMAIL="+email,
                                                    "PASSWORD="+password );
                            database.refresh();
                            GUI.updateUserCount();

                            return "confirm";

                        } return "email_in_use";
                    } return "username_in_use";
                } return "email_format_error";
            } return "password_format_error";
        } return "username_format_error";

    }


    public Record get( String IP ) {

        return database.select("IP="+IP).get(0);

    }
    public boolean remove( String id ) {

        for ( ConnectionThread connection : clientconnections ) {

            if ( connection.id().equals( id ) ) {

                connection.interrupt();
                clientconnections.remove( connection );

                System.out.println( "SERVER: connection closed for client id " + id + "\n" );
                return true;

            }

        }

        return false;

    }
    public void removeAll() {

        for ( ConnectionThread connection : clientconnections ) {
            connection.interrupt();
        }
        clientconnections = new Vector<ConnectionThread>();

    }
    public boolean delete( String IP ) {

        ArrayList<Record> result = database.delete("IP="+IP);
        GUI.updateUserCount();
        return result != null;

    }

    public void log( String message ) {

        System.out.println("SHOULD BE LOGGED: "+message+"\n\n");
        //log.log(message);

    }


    public boolean checkUsernameFormat( String username ) {

        String regex = "^(?=.*[a-zA-Z])[A-Za-z\\d_]{4,}$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher( username ).matches();

    }
    public boolean checkPasswordFormat( String password ) {

        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher( password ).matches();

    }
    public boolean checkEmailFormat( String email ) {

        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher( email ).matches();

    }
    public boolean isAvailable( String fieldspec ) {

        return !database.contains( fieldspec );

    }

    public int generateNewCode() {

        int code = (int)(Math.random() * 900000) + 100000;
        lastCode = code;
        return code;

    }
    public void sendCode( String email ) {

        final String username = "clientserverauthenticationcode@gmail.com";
        final String password = "fiei gmqz dovl xncj";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse( email )
            );
            message.setSubject("PASSWORD VERIFICATION CODE");
            message.setText( "Your code is,\n\n"+generateNewCode() );

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
    public boolean changePassword( String IP, String new_password ) {

        ArrayList<Record> updated = database.update( "IP="+IP, "PASSWORD="+new_password );
        logout(IP);
        return updated != null;

    }

    
    public int getPort() {

        return PORT;

    }
    public ArrayList<String[]> getRegisteredUsers() {

        return new ArrayList<>();

    }




    public Vector<ConnectionThread> getConnections() {
        return clientconnections;
    }

    public String getLastCode() {
        return ""+lastCode;
    }



}