package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.json.JSONException;

import utils.*;

public class UDPServer extends Server{

  private InetAddress IPAddress;
  
  private volatile DatagramSocket serverSocket;
  
  private int receiverPort;
  
  private volatile String serverInfo = "";
  
  
  private UDPServer(int port, String additionalInformation){
    super(port,additionalInformation,Server.Type.UDP);
    
    this.serverInfo = additionalInformation;
  }
  
  public UDPServer(int port) {
    this(port,String.format("Running on %s:%d.",Utils.getSingletonInstance().getLocalAddress().getHostAddress(),port));
    
  }
  
    @Override
    public void sendLogic() {
      System.out.println("SEND UDP called");
      
      int i = 0;
      byte[] sendData = new byte[1024];
      String capitalizedSentence = "UDP "+ (i++);
      sendData = capitalizedSentence.getBytes();
      DatagramPacket sendPacket =
      new DatagramPacket(sendData, sendData.length, this.IPAddress, receiverPort);
      
      try {
        this.serverSocket.send(sendPacket);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      super.stopSendingThread();
    }
      
    }

    @Override
    public void run() {
  //    System.out.println("\nWaiting for client on port "+
  //             super.getServerPort() + "...");
      
      try {
        this.serverSocket = new DatagramSocket(super.getServerPort());
        
  
        
        
        //System.out.println("Connection established on port "+ super.getServerPort() + "!");
        
        super.updateUtilsServerInfos(String.format("%s Connection established with %d.",this.serverInfo, super.getServerPort()));
        
        while(true){
        
          byte[] receiveData = new byte[1024];
             
          DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
          
          try {
            serverSocket.receive(receivePacket);         
          } catch (IOException e1) {
            e1.printStackTrace();  
          }
              
          this.IPAddress = receivePacket.getAddress();
          this.receiverPort = receivePacket.getPort();
             
          String ip = IPAddress.toString();
          ip = ip.substring(1,ip.length());
              
          Utils.getSingletonInstance().setClientAddress(ip);
              
          String JSON  = new String( receivePacket.getData());
          
          
          
          if(!super.isSending())
            super.startSendingThread();
          
        //the additional need of closing manually the connection because of the UDP way of work
          if(JSON.contains("close")){
            System.out.println("CLOSING");
//          serverSocket.disconnect();
          Utils.getSingletonInstance().resetAllValues();
        super.updateUtilsServerInfos(String.format("The client has disconected... %s",this.serverInfo));
        super.stopSendingThread();
          }else{
          try{
                Utils.getSingletonInstance().handleInput(JSON);
              }catch (JSONException e) {
                System.err.println("The client is not sending JSON files! Disconecting...");
                super.updateUtilsServerInfos("The client is not sending JSON files! Disconecting...");
                Utils.getSingletonInstance().resetAllValues();
                super.stopSendingThread();
                e.printStackTrace(); 
              }    
          }
          
         }
        
        
        
      } catch (SocketException e2) {
        System.out.println("Unable to init the UDP socket.");
        super.stopSendingThread();
        e2.printStackTrace();
        
        //this below is not possible https://stackoverflow.com/questions/38157060/udp-server-client-java
        //I need additional information on top of UDP to handle events if the client is 'connected'
      }
      
          
    }
    
  
//    public static void main(String [] args) {
//         System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
//         System.out.flush();
//         
//         int port = Integer.parseInt(args[0]);
//         
//         
//          Thread t = new UDPServer(port);
//          t.start();
//          //http://stackoverflow.com/questions/27381021/detect-a-key-press-in-console
//          
//          
//          TerminalListener terminalListener = new TerminalListener();
//          terminalListener.start();
//           
//    }

}
