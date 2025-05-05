package server;

import server.database.Record;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;


public class ConnectionThread extends Thread {


    // -- the main server (port listener) will supply the socket
    //    the thread (this class) will provide the I/O streams


    private final Server server;

    volatile boolean go;


    private final String clientIP;


    volatile BufferedReader datain;
    volatile DataOutputStream dataout;



    public ConnectionThread ( String ip,
                              Socket socket,
                              Server server ) {

        this.server = server;
        this.clientIP = ip;

        go = true;

        // -- create the stream I/O objects on top of the socket
        try {
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run () {
        // -- server thread runs until the client terminates the connection

        try {


            while (go) {

                if ( this.isInterrupted() ) {

                    go = false;
                    dataout.close();
                    datain.close();
                    Thread.currentThread().interrupt();
                    break;

                } else {

                    try {
                        // -- always receives a String object with a newline (\n)
                        //    on the end due to how BufferedReader readLine() works.
                        //    The client adds it to the user's string but the BufferedReader
                        //    readLine() call strips it off
                        String pre = datain.readLine();
                        log( "FROM: " + clientIP );
                        log("RECEIVED: " + pre + "\n" );
                        String[] txt = pre.split(":", -1);

                        // -- if the disconnect string is received then
                        //    close the socket, remove this thread object from the
                        //    server's active client thread list, and terminate the thread

                        switch( txt[0] ) {

                            case( "disconnect" ):
                                disconnect();
                                break;

                            case( "login" ):
                                login(txt[1], txt[2]);
                                break;

                            case( "register" ):
                                register( txt[1], txt[2], txt[3], txt[4], txt[5] );
                                break;

                            case( "get" ):
                                sendUserInfo();
                                break;

                            case( "logout" ):
                                logout();
                                break;

                            case( "delete" ):
                                send( server.delete(txt[1]) );
                                break;

                            default:
                                unknownCommand();
                                break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        go = false;
                        interrupt();
                        Thread.currentThread().interrupt();
                    }

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        } /*catch (InterruptedException e) {
            log( "INTERRUPTED: "+clientIP );
            Thread.currentThread().interrupt();
        }*/

    }
    @Override
    public void interrupt() {

        try {

            datain.close();
            dataout.close();

        } catch (IOException e) {

           System.out.println( "COULDNT CLOSE STREAMS" );
           e.printStackTrace();

        }

        go = false;


    }



    private void disconnect() throws IOException {

        go = false;

        send( "disconnected" );

        datain.close();
        server.remove( clientIP );
        interrupt();

    }
    private void login( String username,
                        String password ) throws IOException {

        send( server.login( username, password, clientIP ) );

    }
    private void logout() throws IOException {

        send( "logged-out" );
        server.logout( clientIP );

    }
    private void register( String... info ) throws IOException {

        send( server.register( info[0],
                               info[1],
                               info[2],
                               info[3],
                               info[4] ) );

    }
    private void sendUserInfo() throws IOException {

        Record user = server.get( clientIP );
        send(user.toString());

    }
    private void unknownCommand() throws IOException {
        send( "unknown command" );
    }


    private void send( String _msg ) throws IOException {
        log( "SENDING: "+_msg );
        log( "TO: "+ clientIP +"\n" );
        dataout.writeBytes(_msg + "\n");
        dataout.flush();
    }
    private void send( boolean _msg ) throws IOException {
        send( _msg?"confirm":"deny" );
    }


    private void log( String message ) {

        System.out.println( message );

    }


    public String id() {

        return clientIP;

    }



}