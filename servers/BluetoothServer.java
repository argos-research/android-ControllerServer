package servers;

//taken from 2. on 04.05 18:51
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import utils.Utils;
import utils.uInputValuesHolder;

import javax.microedition.io.*;

import org.json.JSONException;
import org.json.JSONObject;

import httpClient.HttpRequest;
import httpClient.HttpRequest.IJsonHandler;

import javax.bluetooth.*;


/**
  * The Bluetooth server class. It holds all of the Bluetooth logic and functions related to this server.
  *
  * <b>IMPORTANT!</b> If you face the problem [2] TODO (write down the whole [2] problem statement), you can 
  * have a look at http://stackoverflow.com/questions/30946821/bluecove-with-bluez-chucks-can-not-open-sdp-session-2-no-such-file-or-direct
  * or just follow the these steps:
  *	1. Open the file: /etc/systemd/system/bluetooth.target.wants/bluetooth.service
  * 2. Add a "- C" at the end of the line ExecStart=/usr/lib/bluetooth/bluetoothd (it has to look like this: ExecStart=/usr/lib/bluetooth/bluetoothd -C)
  * 3. After changing the line, you will have to reload and restart the bluetooth device with:
  * #reload
  * systemctl daemon-reload
  * #restart
  * sudo service bluetooth restart
  * 
  * 
  * 
  * If you are facing also some socket problems (i.e. the client can not connect to the server or client get exception socket closed),
  * make sure to restart the Bluetooth device with the command from above.
  * 
  * It is not always an issue, but it is highly recommended to pair the PC (running the server) and the
  * android client, which is running the client application.
  */
public class BluetoothServer extends Server{
	
	private volatile String clientSocketAddress = "";
	
	private volatile String serverInfo = "";
	
	//well know UUID and also very stable working one
	private UUID uuid = new UUID("94f39d297d6d437d973bfba39e49d4ee",false);

	private OutputStream dos = null;
	
	private BluetoothServer(int port, String additionalInformation){
		super(port,additionalInformation, Server.Type.Bluetooth);
		
		this.serverInfo = additionalInformation;
	}
	
	
	public BluetoothServer() throws BluetoothStateException{
		this(0,String.format("Running with MAC: %s.",LocalDevice.getLocalDevice().getBluetoothAddress()));
		
	}

	int i = 0;
	
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
					try {
						dos.write(JSON.toString().getBytes());
					} catch (IOException e) {
						updateUtilsServerInfos(String.format("Unable to send to the client failure: %s. \n%s", e.toString(),serverInfo));
					}
				}
			}
			
		}).doInBackround();
		
	}

	@Override
	public void run() {
		final String url = "btspp://localhost:" + uuid +
  //  					new UUID( 0x1101 ).toString() + 
        				";name=File Server";
		StreamConnectionNotifier service = null;
        try {

        	service								= (StreamConnectionNotifier) Connector.open( url );
        	StreamConnection con	 			= (StreamConnection) service.acceptAndOpen();
	        dos 								= con.openOutputStream();
	        InputStream dis 					= con.openInputStream();
	        
	        RemoteDevice dev 					= RemoteDevice.getRemoteDevice(con); 

	        clientSocketAddress	 				= dev.getBluetoothAddress();
	        
	        Utils.getSingletonInstance().setClientAddress(clientSocketAddress);
	        
	        //System.out.println("Connection established with  " + getSocket().getRemoteSocketAddress());
	        Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Bluetooth);
	        super.updateUtilsServerInfos(String.format("Connection established with %s. %s", clientSocketAddress,this.serverInfo));
	        Utils.getSingletonInstance().resetAllValues();
	        
	        //start the parallel sending thread
	        super.startSendingThread();
		      
	        super.createUInputDevice(); //initialize the device if it not currently active
	        
	        while(true){
	        	try{
	        		byte buffer[] = new byte[1024];
			    	dis.read( buffer );     //holds here and wait for the client
			    	String JSONinput = new String(buffer);
			    	//System.out.println(JSONinput);
			    	Utils.getSingletonInstance().handleInput(JSONinput);
			    	
			    	/*
			        Currently there is a bug in the SpeedDream 2 HTTP API which provides the data from the game. The bug is
			        that when you choose to play a game and the loading screen is ready, you need to press Enter to start
			        racing. Unfortunately, in this time the HTTP socket will be initialized but if you try to send a HTTP GET,
			        this will force the game to crash and it can even harm you PC. That is why you will need to press the
			        'start game' button in the main activity, which will send a simple "enter" key press which will trigger
			        the game to start and will also start the sending thread on the server side which will start sending
			        the provided JSON from the SP2 HTTP API.
			         */
//			    	try{
//				    	if(Utils.getSingletonInstance().getKeyEvent(JSONinput) == uInputValuesHolder.KEY_ENTER){
//				    		//start the parallel sending thread
//					        super.startSendingThread();
//				    	}
//			    	} catch (JSONException je){
//			    		//something went wrong with reading the value
//						super.updateUtilsServerInfos(String.format("The client is not sending proper JSON files... %s",this.serverInfo));
//			    	}
			    	

	        	} catch (Exception e) {
	        		Utils.getSingletonInstance().resetAllValues();
	        		
	        		Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
					super.updateUtilsServerInfos(String.format("The client has disconnected... %s",this.serverInfo));
					super.stopSendingThread();
					//con.close();
					service.close();
					
					// this.destroyUInputDevice();
					this.run(); //keep in in the loop 
				}
	        	
	        	
	        }
			 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(service != null)
					service.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
} 

