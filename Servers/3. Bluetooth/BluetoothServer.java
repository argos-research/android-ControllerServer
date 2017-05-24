

//taken from 2. on 04.05 18:51
import java.io.*;
import utils.Utils;
import utils.uInputJNI;

import java.net.*; 
import java.util.*;
import javax.microedition.io.*;
import javax.bluetooth.*;
import javax.bluetooth.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BluetoothServer{

	//94f39d29-7d6d-437d-973b-fba39e49d4ee
	
	//working instantly with me
	
	//for the problem [2] check http://stackoverflow.com/questions/30946821/bluecove-with-bluez-chucks-can-not-open-sdp-session-2-no-such-file-or-direct
	// in /etc/systemd/system/bluetooth.target.wants/bluetooth.service
	// always keep ExecStart=/usr/lib/bluetooth/bluetoothd -C !!! (with -C) at the end


	//when the server is not responding (client get exception socket closed) then use 
	//sudo service bluetooth restart
	
	private int i = 1; //for counting the output stream of the server
	
	private final long SEND_INTERVAL_MILIS = 2000;
	
	private String clientSocketAddress = "";
	
	private boolean sending;
	private String sendingState = "";
	
	private Utils mUtils;
	
	private uInputJNI mUInputJNI;
	

	public BluetoothServer(boolean isSending){
		sending = isSending;
		sendingState = isSending ? "The server is sending data to the client." : "The server is not sending data to the client.";
		
		mUInputJNI = uInputJNI.getSingletonInstance();
	   
	   mUtils = new Utils(sendingState, mUInputJNI);
	}

	public void startserver() {
    	UUID uuid = new UUID("94f39d297d6d437d973bfba39e49d4ee",false);
    	
           try {
			   while(true){
			   		System.out.println(sendingState);
				   System.out.println("\nWaiting for clients...");
			        String url = "btspp://localhost:" + uuid +
			  //  			new UUID( 0x1101 ).toString() + 
			        		";name=File Server";
			        StreamConnectionNotifier service = 
			        		(StreamConnectionNotifier) Connector.open( url );
			
			        StreamConnection con = 
			        		(StreamConnection) service.acceptAndOpen();
			        OutputStream dos = con.openOutputStream();
			        InputStream dis = con.openInputStream();
			    
			        InputStreamReader daf = new InputStreamReader(System.in);
			        BufferedReader sd = new BufferedReader(daf);                
			        RemoteDevice dev = RemoteDevice.getRemoteDevice(con);   

			        clientSocketAddress = dev.getBluetoothAddress();
			        System.out.println
		    		("Connection established with client "
		    					+ clientSocketAddress);
			        
			        long lastSentMilis = 0, currentTime = 0;
			        Thread sendThread;
		        	while (true) 
		        	{	
		        		try{
		        			
		        			//System.out.println("1");
		        			if(sending){
			        			currentTime = System.currentTimeMillis();
			        			if(lastSentMilis == 0 || (currentTime - lastSentMilis > SEND_INTERVAL_MILIS)){ //don't overload the output stream
			        				//according to http://stackoverflow.com/questions/14494352/can-you-write-to-a-sockets-input-and-output-stream-at-the-same-time
			                        // read should be in a separate thread
			        				// this optimizes twice the speed of receiving!!!!
			        				
			        				sendThread = new Thread(new Runnable() {
										
										@Override
										public void run() {
											//System.out.println("Sending");
						        			String greeting = (i++) + " JSR-82 RFCOMM server says hello\n";
						        			try {
												dos.write( greeting.getBytes() );
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}		//if the client is not reading it it will block the strean!
						 
						        			//dos.flush();
						        			//System.out.println("2");										
										}
									});
			        				sendThread.start();
			        				lastSentMilis = currentTime;
			        				
			        			}
			        		}

						
		        			
					    	byte buffer[] = new byte[1024];
					    	//System.out.println("3");
					    	int bytes_read = dis.read( buffer );     //holds here and wait for the client
					    	mUtils.extractData(buffer, bytes_read);
					    	
					    	mUInputJNI.trigger_axis_Y_event(5);
					    	
					    	//System.out.println("4");
					    	
//					    	String received = new String(buffer, 0, bytes_read);
//					    	int bytesToCut = 15;
//					    	String receivedLastBytes = received.substring(received.length() - bytesToCut, received.length() -2);
//					    	long milis = 0;
//					    	if(Long.getLong(receivedLastBytes) != null)
//					    		milis = Long.getLong(receivedLastBytes);
//					    	System.out.println
//					    		("Message:"+ received +" LAST "+bytesToCut+" BYTES "+ milis
//					    					+ " From:"
//					    					+ dev.getBluetoothAddress());
					    	dos.flush(); //it is not like on the client side. It doesn't change much the data flow
	
							//shut down the server on "Shut down" msg
//							if(received.equals("Shut down")){
//								//con.close();
//								System.out.println("Shutting down from client"); //not completly shuting
//								//break;
//							}
		        		}catch(Exception e){
				    		//Client has disconnected
		        			service.close(); //this really closes the conenction with the client
				    		//con.close();
							System.out.println("The client has disconected. Closing the connection"); //not completly shuting only in case of output stream overload (both are reading and nobady is sending). Now with the thread this is never an issue
							//System.err.println(e.toString());
							//e.printStackTrace();
							//handle better when something happend here
							
							mUtils.resetAllValues();
							
							break;	//go out and wait for the some new client
							//startserver();
				        }
		        	}	
			        
			   }
			   //con.close();
			} catch ( IOException e ) {
			    System.err.print(e.toString());
			}    
    }

    public static void main( String args[] ) {
    	System.out.print("\033[H\033[2J"); //not working in eclipse. Flushes the screen
	    System.out.flush();
    	int isSending = Integer.parseInt(args[0]);
       	try {

    	    LocalDevice local = LocalDevice.getLocalDevice();
	        System.out.println("Server Started:\n"
	              +local.getBluetoothAddress()
	    		+"\n"+local.getFriendlyName()); 
		
	        
			BluetoothServer ff = new BluetoothServer(isSending == 1);
	    	ff.startserver(); 
		    
	    }catch (Exception e) {
	    	System.err.print(e.toString());
	    }
    }
} 

