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


public class BluetoothServer extends Server{

	//94f39d29-7d6d-437d-973b-fba39e49d4ee
	
	//working instantly with me
	
	//for the problem [2] check http://stackoverflow.com/questions/30946821/bluecove-with-bluez-chucks-can-not-open-sdp-session-2-no-such-file-or-direct
	// in /etc/systemd/system/bluetooth.target.wants/bluetooth.service
	// always keep ExecStart=/usr/lib/bluetooth/bluetoothd -C !!! (with -C) at the end


	//when the server is not responding (client get exception socket closed) then use 
	//sudo service bluetooth restart
	
	
	private volatile String clientSocketAddress = "";
	
	private volatile String serverInfo = "";
	
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
//		String greeting = (i++) + " JSR-82 RFCOMM server says hello\n";
//		try {
//			dos.write( greeting.getBytes() );
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//NEW
		HttpRequest.get(new IJsonHandler() {
			
			@Override
			public void onError(Exception error) {
		        updateUtilsServerInfos(String.format("There is a problem with the local communication with the SD2 HTTP API. HTTP GET failure: %s. %s", error.toString(),serverInfo));
			}
			
			@Override
			public void onComplete(JSONObject JSON) {
				try {
					dos.write(JSON.toString().getBytes());
				} catch (IOException e) {
					updateUtilsServerInfos(String.format("Unable to send to the client failure: %s. \n%s", e.toString(),serverInfo));
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
	        
	        super.createUInputDevice(); //initialize the device if it not currently active
	        
	        while(true){
	        	try{
	        		byte buffer[] = new byte[1024];
			    	dis.read( buffer );     //holds here and wait for the client
			    	String JSONinput = new String(buffer);
			    	//System.out.println(JSONinput);
			    	Utils.getSingletonInstance().handleInput(JSONinput);
			    	
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
			    	try{
				    	if(Utils.getSingletonInstance().getKeyEvent(JSONinput) == uInputValuesHolder.KEY_ENTER){
				    		//start the parallel sending thread
					        super.startSendingThread();
				    	}
			    	} catch (JSONException je){
			    		//something went wrong with reading the value
						super.updateUtilsServerInfos(String.format("The client is not sending proper JSON files... %s",this.serverInfo));
			    	}
			    	

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
	
	
	public static boolean pingHost(String host, int port, int timeout) {
		Socket socket = null;
	    try  {
	    	socket = new Socket();
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    } finally{
	    	if(socket != null){
	    		try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
} 

