package org.vosk.demo;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpClient {

    public static final String TAG = UdpClient.class.getSimpleName();
    public static final String SERVER_IP = "192.168.123.161"; //server IP address
    public static final int SERVER_PORT = 1233;
    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
//    private UdpClient.OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
//     */
//    public UdpClient(UdpClient.OnMessageReceived listener) {
//        mMessageListener = listener;
//    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddr, SERVER_PORT);
                    Log.d("UDP", "C: Sending: '" + new String(message) + "'");

                    // send the UDP packet
                    socket.send(packet);
                    socket.close();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Close the connection and release the members
     */
//    public void stopClient() {
//
//        mRun = false;
//
//        if (mBufferOut != null) {
//            mBufferOut.flush();
//            mBufferOut.close();
//        }
//
//        mMessageListener = null;
//        mBufferIn = null;
//        mBufferOut = null;
//        mServerMessage = null;
//    }
//
//    public void run() {
//
//        mRun = true;
//
//        try {
//            //here you must put your computer's IP address.
//            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
//
//            Log.d("TCP Client", "C: Connecting...");
//
//            //create a socket to make the connection with the server
//            DatagramSocket socket = new DatagramSocket();
//
//            try {
//
//                //sends the message to the server
//
//                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.)), true);
//
//                //receives the message which the server sends back
//                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//
//                //in this while the client listens for the messages sent by the server
//                while (mRun) {
//
//                    mServerMessage = mBufferIn.readLine();
//
//                    if (mServerMessage != null && mMessageListener != null) {
//                        //call the method messageReceived from MyActivity class
//                        mMessageListener.messageReceived(mServerMessage);
//                    }
//
//                }
//
//                Log.d("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");
//
//            } catch (Exception e) {
//                Log.e("TCP", "S: Error", e);
//            } finally {
//                //the socket must be closed. It is not possible to reconnect to this socket
//                // after it is closed, which means a new socket instance has to be created.
//                socket.close();
//            }
//
//        } catch (Exception e) {
//            Log.e("TCP", "C: Error", e);
//        }
//
//    }
//
//    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
//    //class at on AsyncTask doInBackground
//    public interface OnMessageReceived {
//        public void messageReceived(String message);
//    }
}
