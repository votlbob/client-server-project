package server;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;




public class ConnectionThread extends Thread {
    private boolean go;
    private String name;
    private int id;

    // -- the main server (port listener) will supply the socket
    //    the thread (this class) will provide the I/O streams
    //    BufferedReader is used because it handles String objects
    //    whereas DataInputStream does not (primitive types only)
    private BufferedReader datain;
    private DataOutputStream dataout;

    // -- this is a reference to the "parent" Server object
    //    it will be set at time of construction
    private Server server;


    public ConnectionThread (int id, Socket socket, Server server) {
        this.server = server;
        this.id = id;
        this.name = Integer.toString(id);
        go = true;

        // -- create the stream I/O objects on top of the socket
        try {
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String toString () {

        return name;

    }

    public String getname () {

        return name;

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
                System.out.println(pre);
                String[] txt = pre.split(":", -1);
                System.out.println("SERVER receive: " + txt[0]);
                // -- if it is not the termination message, send it back adding the
                //    required (by readLine) "\n"

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
                        register( txt[1], txt[2], txt[3] );
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
    private void register( String username,
                           String password,
                           String email) throws IOException {

        send( server.register( username, password, email ) );

    }
    private void unknownCommand() throws IOException {
        send( "unknown command" );
    }

    private void send( String _msg ) throws IOException {
        System.out.println( "ID: "+id+"\nSENDING: "+_msg+"\n" );
        dataout.writeBytes(_msg + "\n");
        dataout.flush();
    }
    private void send( boolean _msg ) throws IOException {
        send( _msg?"c":"d" );
    }
    private void confirm() throws IOException {
        send( "c" );
    }
    private void deny() throws IOException {
        send( "d" );
    }

}