package servers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.json.JSONException;
import org.json.JSONObject;

import httpClient.HttpRequest;
import httpClient.HttpRequest.IJsonHandler;
import utils.*;

/**
 * The UDP server class used for UDP communication
 * between the client and the server. 
 * TODO REMOVE THESE "THE"s!
 * @author Konstantin Vankov 
 */
public class UDPServer extends Server{

  private InetAddress IPAddress;
  
  private volatile DatagramSocket serverSocket;
  
  private int receiverPort;
  
  private volatile String serverInfo = "";
  
  private volatile boolean onceEvent = true;
  
  
  private UDPServer(int port, String additionalInformation){
    super(port,additionalInformation,Server.Type.UDP);
    
    this.serverInfo = additionalInformation;
  }
  
  public UDPServer(int port) {
    this(port,String.format("Running on %s:%d.",Utils.getSingletonInstance().getLocalAddress().getHostAddress(),port));
    
  }
  
    @Override
    public void sendLogic() {
      
      HttpRequest.get(new IJsonHandler() {
    
        @Override
        public void onError(Exception error) {
          updateUtilsServerInfos(String.format("There is a problem with the local communication with the SD2 HTTP API. HTTP GET failure: %s. %s", error.toString(),serverInfo));
        }
        
        @Override
        public void onComplete(JSONObject JSON) {
          //when the server has initialized the socket but the game has not started, it will send just an empty JSON: {} so here I will ignore it and not send it to the client
          if(JSON.length() > 3){
            DatagramPacket sendPacket =
                    new DatagramPacket(JSON.toString().getBytes(), JSON.toString().length(), IPAddress, receiverPort);
            try {
                  serverSocket.send(sendPacket);
              } catch (IOException e) {
                updateUtilsServerInfos(String.format("Unable to send to the client failure: %s. \n%s", e.toString(),serverInfo));
                stopSendingThread();
              }
          }
        }
      }).doInBackround();
    }

    @Override
    public void run() {
      
      try {
        this.serverSocket = new DatagramSocket(super.getServerPort());
        
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
          
          if(onceEvent){
            Utils.getSingletonInstance().setActiveConnectionType(Server.Type.UDP);

            //this below is not possible https://stackoverflow.com/questions/38157060/udp-server-client-java
            //I need additional information on top of UDP to handle events if the client is 'connected'
            super.updateUtilsServerInfos(String.format("Connection established with %d.%s ", super.getServerPort(),this.serverInfo));
            
            //start the parallel sending thread
            super.startSendingThread();
          
            onceEvent = false;
          }
          
          //start sending only if the enter button is pressed.
        /*
          Currently there is a bug in the SpeedDream 2 HTTP API which provides the data from the game. The bug is
          that when you choose to play a game and the loading screen is ready, you need to press Enter to start
          racing. Unfortunately, in this time the HTTP socket will be initialized but if you try to send a HTTP GET,
          this will force the game to crash and it can even harm you PC. That is why you will need to press the
          'start game' button in the main activity, which will send a simple "enter" key press which will trigger
          the game to start and will also start the sending thread on the server side which will start sending
          the provided JSON from the SP2 HTTP API.
           */
//        try{
//          if(Utils.getSingletonInstance().getKeyEvent(JSON) == uInputValuesHolder.KEY_ENTER){
//            //start the parallel sending thread
//              super.startSendingThread();
//          }
//        } catch (JSONException je){
//          //something went wrong with reading the value
//        super.updateUtilsServerInfos(String.format("The client is not sending proper JSON files... %s",this.serverInfo));
//        }
          
          
        //the additional need of closing manually the connection because of the UDP way of work
          if(JSON.contains("close")){
            System.out.println("CLOSING");
            //          serverSocket.disconnect();
            Utils.getSingletonInstance().resetAllValues();
            Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
            super.updateUtilsServerInfos(String.format("The client has disconnected... %s",this.serverInfo));
            super.stopSendingThread();
            onceEvent = true;
          }else{
            try{
                Utils.getSingletonInstance().handleInput(JSON);
              }catch (JSONException e) {
                Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
                super.updateUtilsServerInfos("The client is not sending JSON files so disconnecting... "+this.serverInfo);
                Utils.getSingletonInstance().resetAllValues();
                super.stopSendingThread();
                onceEvent = true;
                e.printStackTrace(); 
              }    
            }
          
         }
      } catch (SocketException e2) {
        super.updateUtilsServerInfos("Unable to initialize the UDP socket... The server is NOT running.");
        super.stopSendingThread();
        onceEvent = true;
        e2.printStackTrace();
        
      } 
    }
    

}
