package servers;

import utils.*;

import java.net.*;
import java.io.*;

import org.json.JSONException;
import org.json.JSONObject;

import httpClient.HttpRequest;
import httpClient.HttpRequest.IJsonHandler;


/**
 * The TCP server class used for TCP communication
 * between the client and the server. 
 * TODO REMOVE THESE "THE"s!
 * @author Konstantin Vankov 
 */
public class TCPServer extends Server {
private ServerSocket serverSocket;

private volatile Socket socket;

/*Stores the important server information - IP address and port number of the running TCP server*/
private volatile String serverInfo = "";

/*Stores the address of the connected client socket - IP and port number.*/
private String clientSocketAddress = "";


	private TCPServer(int port, String additionalInformation){
		super(port,additionalInformation,Server.Type.TCP);
		
		this.serverInfo = additionalInformation;
	}
	  
	public TCPServer(int port) throws IOException {
		this(port,String.format("Running on %s:%d.",Utils.getSingletonInstance().getLocalAddress().getHostAddress(),port));
		
		serverSocket = new ServerSocket(port);

		//you can use timeout if you want to but it is not suggested
		//serverSocket.setSoTimeout(30*1000);
		
	 
	}
	
	private synchronized Socket getSocket(){
		return this.socket;
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
					DataOutputStream out;
					try {
						out = new DataOutputStream(getSocket().getOutputStream());
						
						out.writeUTF(JSON.toString());
				        out.flush();
					} catch (IOException e) {
						updateUtilsServerInfos(String.format("Unable to send to the client failure: %s. \n%s", e.toString(),serverInfo));
					}
				}
			}
			
		}).doInBackround();
		
	}
	
	@Override
	public void run() {
      
      try {
    	  socket = serverSocket.accept();

           
          clientSocketAddress = getSocket().getRemoteSocketAddress().toString();
          clientSocketAddress = clientSocketAddress.substring(1,clientSocketAddress.length()); //removes the "\" from the beginning
          Utils.getSingletonInstance().setClientAddress(clientSocketAddress);
          
          Utils.getSingletonInstance().setActiveConnectionType(Server.Type.TCP);
          super.updateUtilsServerInfos(String.format("%s Connection established with %s.",this.serverInfo, this.getSocket().getRemoteSocketAddress()));
          Utils.getSingletonInstance().resetAllValues();
          
          //start the parallel sending thread
	      super.startSendingThread();
          
          super.createUInputDevice(); //initialize the device if it not currently active
          
          while(socket.isConnected()) {
            DataInputStream in = new DataInputStream(getSocket().getInputStream());
            
            //System.out.println(in.readUTF());
            try{
            	//super.handleInput(in.readUTF());
            	String JSONString = in.readUTF();
            	Utils.getSingletonInstance().handleInput(JSONString);
            	
            	//Asynchrony call is NOT POSSIBLE here as above => the Bluetooth approach
            	//start it on thread here and the input on the client
           

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
//		    	try{
//			    	if(Utils.getSingletonInstance().getKeyEvent(JSONString) == uInputValuesHolder.KEY_ENTER){
//			    		//start the parallel sending thread
//				        super.startSendingThread();
//			    	}
//		    	} catch (JSONException je){
//		    		//something went wrong with reading the value
//					super.updateUtilsServerInfos(String.format("The client is not sending proper JSON files... %s",this.serverInfo));
//		    	}
            	
	           
	            
            } catch (JSONException e) {
				//System.err.println("The client is not sending JSON files! Disconnecting...");
				//e.printStackTrace();
				Utils.getSingletonInstance().resetAllValues();

		        Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
				super.updateUtilsServerInfos(String.format("The client it is not sending JSON files. Disconnecting... %s",this.serverInfo));
				// this.destroyUInputDevice();
				super.stopSendingThread();
				this.run(); //keep in in the loop TODO consider just with another while
			}catch (IOException e) {
	            //e.printStackTrace();
				//System.err.println("The client has disconnected.");
				Utils.getSingletonInstance().resetAllValues();
				Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
				super.updateUtilsServerInfos(String.format("The client has disconnected... %s",this.serverInfo));
				getSocket().close();
				super.stopSendingThread();
				// this.destroyUInputDevice();
				this.run(); //keep in in the loop TODO consider just with another while
			}
            
          }
      }catch(SocketTimeoutException s) {
            //System.out.println("Socket timed out!");
    	    super.updateUtilsServerInfos(String.format("TCP socket timed out..."));
            Utils.getSingletonInstance().resetAllValues();
            super.stopSendingThread();
            // this.destroyUInputDevice();
      }catch (IOException e) {
         	e.printStackTrace();
			//System.err.println("Failed to accept the server socket.");
			Utils.getSingletonInstance().resetAllValues();
			super.stopSendingThread();
			super.updateUtilsServerInfos(String.format("Failed to accept the server socket..."));
			// this.destroyUInputDevice();
      } catch (Exception ex){
    	  // System.err.println("Some unexpected exception... Closing the application");
    	  super.updateUtilsServerInfos(String.format("Some unexpected exception..."));
    	  Utils.getSingletonInstance().resetAllValues();
    	  super.stopSendingThread();
    	  ex.printStackTrace();
    	  //super.destroyUInputDevice();
      }
   }
	   
	
	
}