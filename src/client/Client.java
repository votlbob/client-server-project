package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    // -- port and host name of server
    private static final int PORT = 8000;

    /* --
     For Windows
     From ipconfig:

     Wireless LAN adapter Wireless Network Connection:

     Connection-specific DNS Suffix  . : clunet.edu
     Link-local IPv6 Address . . . . . : fe80::1083:3e22:f5a1:a3ec%11
     IPv4 Address. . . . . . . . . . . : 199.107.222.115 <=======This address works
     Subnet Mask . . . . . . . . . . . : 255.255.240.0
     Default Gateway . . . . . . . . . : 199.107.210.2

     For MacOS
     System Preferences -> Network -> Advanced -> TCP/IP -> IPv4 address 192.168.1.14

    -- */
    private static String HOST = "127.0.0.1";

    // -- socket variable for peer to peer communication
    private Socket socket;

    // -- stream variables for peer to peer communication
    //    to be opened on top of the socket
    private BufferedReader datain;
    private DataOutputStream dataout;


    public Client (String host)
    {
        Client.HOST = host;
        try {
            // -- construct the peer to peer socket
            socket = new Socket(HOST, PORT);
            // -- wrap the socket in stream I/O objects
            datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            dataout = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Host " + HOST + " at port " + PORT + " is unavailable.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Unable to create I/O streams.");
            System.exit(1);
        }

    }

    public String sendString (String _msg)
    {
        String rtnmsg = "";

        try {
            // -- the server only receives String objects that are
            //    terminated with a newline \n"
            // -- send the String making sure to flush the buffer
            dataout.writeBytes(_msg + "\n");
            dataout.flush();

            // -- receive the response from the server
            //    The do/while makes this a blocking read. Normally BufferedReader.readLine() is non-blocking.
            //    That is, if there is no String to read, it will read "". Doing it this way does not allow
            //    that to occur. We must get a response from the server. Time out could be implemented with
            //    a counter.
            rtnmsg = "";
            do {
                rtnmsg = datain.readLine();
            } while (rtnmsg.equals(""));

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return rtnmsg;

    }

    public void disconnect ()
    {
        String text = "disconnect";
        try {
            // -- the server only receives String objects that are
            //    terminated with a newline "\n"

            // -- send a special message to let the server know
            //    that this client is shutting down
            text += "\n";
            dataout.writeBytes(text);
            dataout.flush();

            // -- close the peer to peer socket
            socket.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }

    }


    public static void main(String[] args)
    {
        Scanner kb = new Scanner(System.in);
        System.out.print("Server IP Address: ");
        String serveripaddress = kb.next();
        kb.close();

        // -- instantiate a Client object
        //    the constructor will attempt to connect to the server
        Client client = new Client(serveripaddress);

        String commandString;
        String replyString;

        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            };
            commandString = "hello";
            System.out.println("CLIENT send:  " + commandString);
            // -- send message to server and receive reply.
            replyString = client.sendString(commandString);
            System.out.println("CLIENT receive: " + replyString);
        }

        client.disconnect();
    }

}
