package server;

import server.database.DBMScsv;
import server.database.Record;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;



public class Server {
    
    
    volatile boolean status;

    private int PORT;
    private int nextId = 0;

    private String filename = "C:\\Users\\fabou\\IdeaProjects\\Client Server Project\\src\\server\\database\\registry.csv";
    private DBMScsv database;

    private ServerSocket serversocket;

    private Vector<ConnectionThread> clientconnections;

    

    public Server( int port ) {

        status = false;

        try {
            database = new DBMScsv();
            database.connect( filename );
        } catch(Exception e) {
            e.printStackTrace();
            shutdown();
        }

        PORT = port;

        clientconnections = new Vector<ConnectionThread>();

    }


    public void peerconnection( Socket socket ) {
        // -- when a client arrives, create a thread for their communication
        ConnectionThread connection = new ConnectionThread( nextId,
                                                            socket,
                                                     this);

        // -- add the thread to the active client threads list
        clientconnections.add(connection);

        // -- start the thread
        connection.start();

        // -- place some text in the area to let the server operator know
        //    what is going on
        System.out.println("SERVER: connection received for id " + nextId + "\n");
        ++nextId;
    }


    // -- called by a ServerThread when a client is terminated
    public void removeID( int id ) {
        // -- find the object belonging to the client thread being terminated
        for (int i = 0; i < clientconnections.size(); ++i) {
            ConnectionThread cc = clientconnections.get(i);
            long x = cc.getId();
            if (x == id) {
                // -- remove it from the active threads list
                //    the thread will terminate itself
                clientconnections.remove(i);

                // -- place some text in the area to let the server operator know
                //    what is going on
                System.out.println("SERVER: connection closed for client id " + id + "\n");
                break;
            }
        }


    }


    public void start() {

        if ( !status ) {

            status = true;

            new Thread(() -> {

                try {

                    serversocket = new ServerSocket(PORT);   //PORT = 8000

                    while (status) {
                        // -- block until a client comes along
                        Socket socket = serversocket.accept();

                        // -- connection accepted, create a peer-to-peer socket
                        //    between the server (thread) and client
                        peerconnection(socket);
                    }
                    System.out.println("exited from here");

                } catch( IOException e ) {
                    e.printStackTrace();
                }

            }).start();

        }

    }
    public void stop() {

        for (ConnectionThread c : clientconnections) c.interrupt();
        clientconnections = new Vector<ConnectionThread>();

        System.out.print(status);
        if ( status ) { status = false; }
        System.out.println(" -> "+status);

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
                          String password ) {

        Record user;

        try {

            user = database.select("USERNAME=" + username).get(0);

        } catch( IndexOutOfBoundsException e ) {
            System.out.println( "user doesn't exist" );
            return false;
        }

        return password.equals( user.getValue("PASSWORD") );

    }
    public String register( String username,
                            String first,
                            String last,
                            String email,
                            String password ) {

        if ( checkUsernameFormat( username ) ) {
            if ( checkPasswordFormat( password ) ) {
                if ( isAvailable( "USERNAME="+username ) ) {
                    if ( isAvailable( "EMAIL="+email ) ) {

                        database.insert( "USERNAME="+username,
                                                "FIRST="+first,
                                                "LAST="+last,
                                                "EMAIL="+email,
                                                "PASSWORD="+password );
                        try {
                            database.disconnect();
                            database.connect( filename );
                        } catch( Exception e ) {
                            e.printStackTrace();
                            shutdown();
                        }

                        return "confirm";

                    } return "email_in_use";
                } return "username_in_use";
            } return "password_format_error";
        } return "username_format_error";

    }
    public Record get( String username ) {
        return database.select("USERNAME="+username).get(0);
    }
    public void remove( String username ) {

    }
    public void delete( String username ) {

    }


    public boolean checkUsernameFormat( String username ) {
        return true;
    }
    public boolean checkPasswordFormat( String password ) {
        return true;
    }
    public boolean isAvailable( String fieldspec ) {

        return !database.contains( fieldspec );

    }

    
    public int getPort() {

        return PORT;

    }
    public ArrayList<String[]> getRegisteredUsers() {

        return new ArrayList<>();

    }



    public static void main (String args[]) {

        new Server( 8000 );

    }



}