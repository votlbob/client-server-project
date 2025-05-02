package server;

import server.database.Record;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;




public class ConnectionThread extends Thread {


    // -- the main server (port listener) will supply the socket
    //    the thread (this class) will provide the I/O streams


    private Server server;

    volatile boolean go;


    private int id;
    private String username = "1";


    private BufferedReader datain;
    private DataOutputStream dataout;



    public ConnectionThread (int id,
                             Socket socket,
                             Server server) {

        this.server = server;
        this.id = id;
        go = true;

        // -- create the stream I/O objects on top of the socket
        try {
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run () {
        // -- server thread runs until the client terminates the connection
        while (go) {
            try {
                // -- always receives a String object with a newline (\n)
                //    on the end due to how BufferedReader readLine() works.
                //    The client adds it to the user's string but the BufferedReader
                //    readLine() call strips it off
                String pre = datain.readLine();
                log("RECEIVED: " + pre);
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

                    default:
                        unknownCommand();
                        break;
                }

            }
            catch (IOException e) {
                e.printStackTrace();
                go = false;
            }

        }
    }
    @Override
    public void interrupt() {
        go = false;
        super.interrupt();
    }



    private void disconnect() throws IOException {

        send( "disconnected" );

        datain.close();
        server.removeID(id);
        go = false;

    }
    private void login( String username,
                        String password ) throws IOException {

        send( server.login( username, password) );

    }
    private void register( String... info ) throws IOException {

        send( server.register( info[0],
                               info[1],
                               info[2],
                               info[3],
                               info[4] ) );

    }
    private void sendUserInfo() throws IOException {

        Record user = server.get( username );
        send(user.toString());

    }
    private void unknownCommand() throws IOException {
        send( "unknown command" );
    }


    private void send( String _msg ) throws IOException {
        log( "ID: "+id);
        log( "SENDING: "+_msg+"\n" );
        dataout.writeBytes(_msg + "\n");
        dataout.flush();
    }
    private void send( boolean _msg ) throws IOException {
        send( _msg?"confirm":"deny" );
    }
    private void confirm() throws IOException {
        send( "c" );
    }
    private void deny() throws IOException {
        send( "d" );
    }

    private void log( String message ) {

        System.out.println( message );

    }


}