package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    ClientGUI GUI;

    // -- port and host name of server

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
    private String HOST;
    private String SELF;

    // Self: 127.0.0.1
    //Reinhart: 192.168.56.1
    // Logan: 10.100.32.197


    // -- socket variable for peer to peer communication
    private Socket socket;

    // -- stream variables for peer to peer communication
    //    to be opened on top of the socket
    private BufferedReader datain;
    private DataOutputStream dataout;


    public Client (ClientGUI initGUI)
            throws UnknownHostException,
            IOException {

        GUI = initGUI;

        // -- construct the peer to peer socket
        socket = new Socket(HOST, 8000);
        // -- wrap the socket in stream I/O objects
        datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataout = new DataOutputStream(socket.getOutputStream());

    }
    public Client (ClientGUI initGUI,
                   String host)
            throws UnknownHostException,
            IOException {

        GUI = initGUI;
        HOST = host;
        SELF = Inet4Address.getLocalHost().getHostAddress();

        System.out.println( "CONNECTING: "+ SELF +"\n" );

        // -- construct the peer to peer socket
        socket = new Socket(HOST, 8000);
        // -- wrap the socket in stream I/O objects
        datain = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dataout = new DataOutputStream(socket.getOutputStream());

    }


    public String send( String _msg ) {

        System.out.println("SENDING: "+_msg);

        String rtnmsg = "connection_invalid";

        try {

            dataout.writeBytes(_msg + "\n");
            dataout.flush();

            rtnmsg = datain.readLine();

            while( rtnmsg.isEmpty() ) {
                rtnmsg = datain.readLine();
            }

        } catch (SocketException | NullPointerException e) {
            rtnmsg = "connection_invalid";
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("RETURNED: "+rtnmsg+"\n");
        return rtnmsg;

    }

    public String login( String username,
                          String password ) {

        return send( "login:"+username+":"+password );

    }
    public String logout() {

        return send( "logout" );

    }
    public String register( String... info ) {

        String messageInfo = "";

        for (String s : info) {
            messageInfo += ":"+s;
        }

        return send( "register"+messageInfo );

    }
    public boolean changePassword( String password ) {

        return true;

    }
    public String disconnect() {

        return send("disconnect");

    }
    public void delete() {

        send( "delete:"+SELF );

    }

    public boolean checkVerificationCode( int code ) {

        return( send( "check:"+code ).equals( "confirm" ) );

    }

    public String[] information() {

        return send( "get" ).split(":", -1);

    }

    public String getIP() {

        return SELF;

    }

}