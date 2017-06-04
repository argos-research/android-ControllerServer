package servers;

//taken from 2. on 04.05 18:51
import java.io.*;
import utils.Utils;

import java.util.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
import javax.bluetooth.UUID;


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
		String greeting = (i++) + " JSR-82 RFCOMM server says hello\n";
		try {
			dos.write( greeting.getBytes() );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		//
		
	}

	@Override
	public void run() {
		final String url = "btspp://localhost:" + uuid +
  //  					new UUID( 0x1101 ).toString() + 
        				";name=File Server";
        StreamConnectionNotifier service = null;
        
        InputStreamReader daf = new InputStreamReader(System.in);
        BufferedReader sd = new BufferedReader(daf);   
        
		try {
			service =  (StreamConnectionNotifier) Connector.open( url );
			StreamConnection con = 
	        		(StreamConnection) service.acceptAndOpen();
	        dos = con.openOutputStream();
	        InputStream dis = con.openInputStream();
	        

	        RemoteDevice dev = RemoteDevice.getRemoteDevice(con); 

	        clientSocketAddress = dev.getBluetoothAddress();
	        
	        Utils.getSingletonInstance().setClientAddress(clientSocketAddress);
	        //start the parallel sending thread
	        super.startSendingThread();

	        //System.out.println("Connection established with  " + getSocket().getRemoteSocketAddress());
	        super.updateUtilsServerInfos(String.format("Connection established with %s. %s", clientSocketAddress,this.serverInfo));
	        Utils.getSingletonInstance().resetAllValues();
	        
	        
	        super.createUInputDevice(); //initialize the device if it not currently active
	        
	        while(true){
	        	try{
	        		byte buffer[] = new byte[1024];
			    	int bytes_read = dis.read( buffer );     //holds here and wait for the client
			    	String JSONinput = new String(buffer);
			    	//System.out.println(JSONinput);
			    	Utils.getSingletonInstance().handleInput(JSONinput);

	        	} catch (Exception e) {
	        		Utils.getSingletonInstance().resetAllValues();
					super.updateUtilsServerInfos(String.format("The client has disconected... %s",this.serverInfo));
					super.stopSendingThread();
					//con.close();
					service.close();
					
					// this.destroyUInputDevice();
					this.run(); //keep in in the loop TODO consider just with another while
					//break;
				}
	        	
	        	
	        }
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(service != null)
					service.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       
	}

//    public static void main( String args[] ) {
//    	System.out.print("\033[H\033[2J"); //not working in eclipse. Flushes the screen
//	    System.out.flush();
//    	int isSending = Integer.parseInt(args[0]);
//       	try {
//
//    	    LocalDevice local = LocalDevice.getLocalDevice();
//	        System.out.println("Server Started:\n"
//	              +local.getBluetoothAddress()
//	    		+"\n"+local.getFriendlyName()); 
//		
//	        
//			BluetoothServer ff = new BluetoothServer(isSending == 1);
//	    	ff.startserver(); 
//		    
//	    }catch (Exception e) {
//	    	System.err.print(e.toString());
//	    }
//    }
} 

