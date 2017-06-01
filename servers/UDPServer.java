package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.json.JSONException;

import utils.TerminalListener;
import utils.*;

public class UDPServer extends Server{

  public UDPServer(int port) {
    super(port,"WiFi UDP server running on port "+port+".");
    
  }

  @Override
  public void run() {
    System.out.println("\nWaiting for client on port "+
             super.getServerPort() + "...");
    
    DatagramSocket serverSocket;
    try {
      serverSocket = new DatagramSocket(super.getServerPort());
      
      System.out.println("Connection established on port "+ super.getServerPort() + "!");
          
      byte[] receiveData = new byte[1024];
        
      long lastSentMilis = 0, currentTime = 0;
      Thread sendThread;
      while(true){
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          try {
            serverSocket.receive(receivePacket);
          } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
          
          InetAddress IPAddress = receivePacket.getAddress();
          final int receiverPort = receivePacket.getPort();
          String ip = IPAddress.toString();
          ip = ip.substring(1,ip.length());
          final String finalIP = ip;
          super.getUtils().setClientAddress(ip);
          
          String JSON  = new String( receivePacket.getData());
          
          try{
            super.getUtils().handleInput(JSON);
          }catch (JSONException e) {
            System.err.println("The client is not sending JSON files! Disconecting...");
            super.getUtils().resetAllValues();
            e.printStackTrace(); 
          }
          
          
          currentTime = System.currentTimeMillis();
          if(lastSentMilis == 0 || (currentTime - lastSentMilis > ServerSettings.SEND_INTERVAL_MILIS)){ //don't overload the output stream
            //according to http://stackoverflow.com/questions/14494352/can-you-write-to-a-sockets-input-and-output-stream-at-the-same-time
            // read should be in a separate thread
            // this optimizes twice the speed of receiving!!!!
                 
            sendThread = new Thread(new Runnable() {
               
              @Override
              public void run() {
                //System.out.println("Sending");
                try {
                  System.err.println("\n\nSending to "+ finalIP + ":"+ receiverPort+"...\n\n");
                  
                  byte[] sendData = new byte[1024];
                  String capitalizedSentence = "Some response from the server";
                  sendData = capitalizedSentence.getBytes();
                  DatagramPacket sendPacket =
                  new DatagramPacket(sendData, sendData.length, IPAddress, receiverPort);
                  serverSocket.send(sendPacket);
                  //System.out.println("Send " + greeting);
                } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }   //if the client is not reading it it will block the stream                  
              }
            });
            sendThread.start();
            lastSentMilis = currentTime;
          }
      }   
    } catch (SocketException e2) {
        System.out.println("Unable to init the UDP socket.");
        e2.printStackTrace();
    } 
  }
    
  
  public static void main(String [] args) {
       System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
       System.out.flush();
       
       int port = Integer.parseInt(args[0]);
       try {
        System.out.println("UDP server running on "+ Utils.getLocalHostLANAddress().getHostAddress() +":"+port+ "\n");
      } catch (UnknownHostException e1) {
        System.err.println("Could retrive the local IP address of this machine.");
        e1.printStackTrace();
      } 
       
        Thread t = new UDPServer(port);
        t.start();
        //http://stackoverflow.com/questions/27381021/detect-a-key-press-in-console
        
        
        Thread terminalListener = new TerminalListener();
        terminalListener.start();   
  }
  
}
