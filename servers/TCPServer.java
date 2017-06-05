
//taken from 5. on 04.05 18:51

package servers;

import utils.*;

import java.net.*;
import java.io.*;

import org.json.JSONException;

public class TCPServer extends Server {
private ServerSocket serverSocket;

private volatile Socket socket;

private volatile String serverInfo = "";

//TODO remove
private volatile int i = 0;

private String clientSocketAddress = "";


	private TCPServer(int port, String additionalInformation){
		super(port,additionalInformation,Server.Type.TCP);
		
		this.serverInfo = additionalInformation;
	}
	  
	public TCPServer(int port) throws IOException {
		this(port,String.format("Running on %s:%d.",Utils.getSingletonInstance().getLocalAddress().getHostAddress(),port));
		
		serverSocket = new ServerSocket(port);
		//serverSocket.setSoTimeout(30*1000);
		
	 
	}
	
	private synchronized Socket getSocket(){
		return this.socket;
	}

	@Override
	public void sendLogic() {
		System.out.println("SEND TCP called");
		
		String greeting = (i++) + " TCP" + getSocket().getLocalSocketAddress() +"\n";
		
		DataOutputStream out;
		try {
			out = new DataOutputStream(getSocket().getOutputStream());
			
			out.writeUTF(greeting);
	        out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		//not closing after each FOR NOW ONLY THIS VERSION!
//      System.out.println("\nWaiting for client on port "+
//         serverSocket.getLocalPort() + "...");
      
      try {
    	  socket = serverSocket.accept();

           
          clientSocketAddress = getSocket().getRemoteSocketAddress().toString();
          clientSocketAddress = clientSocketAddress.substring(1,clientSocketAddress.length()); //remove the bullshit
          Utils.getSingletonInstance().setClientAddress(clientSocketAddress);
          
          //start the parallel sending thread
          super.startSendingThread();

          Utils.getSingletonInstance().setActiveConnectionType(Server.Type.TCP);
          super.updateUtilsServerInfos(String.format("%s Connection established with %s.",this.serverInfo, this.getSocket().getRemoteSocketAddress()));
          Utils.getSingletonInstance().resetAllValues();
          
          super.createUInputDevice(); //initialize the device if it not currently active
          
          long lastSentMilis = 0, currentTime = 0;
          Thread sendThread;
          while(socket.isConnected()) {
             // BEST VERSION!!!
            DataInputStream in = new DataInputStream(getSocket().getInputStream());
            
            //System.out.println(in.readUTF());
            try{
            	//super.handleInput(in.readUTF());
            	Utils.getSingletonInstance().handleInput(in.readUTF());
            	
            	//Asynchrony call is NOT POSSIBLE here as above => the bluetooth approach
            	//start it on thread here and the input on the client
//            	
            	
 //           	currentTime = System.currentTimeMillis();
//    			if(lastSentMilis == 0 || (currentTime - lastSentMilis > ServerSettings.SEND_INTERVAL_MILIS)){ //don't overload the output stream
//    				//according to http://stackoverflow.com/questions/14494352/can-you-write-to-a-sockets-input-and-output-stream-at-the-same-time
//                    // read should be in a separate thread
//    				// this optimizes twice the speed of receiving!!!!
//    				
//    				sendThread = new Thread(new Runnable() {
//						
//						@Override
//						public void run() {
//							//System.out.println("Sending");
//		        			String greeting = (i++) + " Thank you for connecting to " + getSocket().getLocalSocketAddress() +"\n";
//		        			try {
//		   
//		    		            DataOutputStream out = new DataOutputStream(getSocket().getOutputStream());
//		    		            //out.write(greeting.getBytes(),0,greeting.length());
//		    		            out.writeUTF(greeting);
//		    		            out.flush();
//		    		            //System.out.println("Send " + greeting);
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}		//if the client is not reading it it will block the stream									
//						}
//					});
//    				sendThread.start();
//    				lastSentMilis = currentTime;
//    				
//    			}
	            
	            //out.write(msg.getBytes(),0,msg.length()); //always with write and not writeUTF because it sends 
	                                                      //funny data to the client!
	
            	
	           
	            
            } catch (JSONException e) {
				//System.err.println("The client is not sending JSON files! Disconecting...");
				//e.printStackTrace();
				Utils.getSingletonInstance().resetAllValues();

		        Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
				super.updateUtilsServerInfos(String.format("The client it is not sending JSON files. Disconecting... %s",this.serverInfo));
				// this.destroyUInputDevice();
				super.stopSendingThread();
				this.run(); //keep in in the loop TODO consider just with another while
			}catch (IOException e) {
	            //e.printStackTrace();
				//System.err.println("The client has disconnected.");
				Utils.getSingletonInstance().resetAllValues();
				Utils.getSingletonInstance().setActiveConnectionType(Server.Type.Nothing);
				super.updateUtilsServerInfos(String.format("The client has disconected... %s",this.serverInfo));
				getSocket().close();
				super.stopSendingThread();
				// this.destroyUInputDevice();
				this.run(); //keep in in the loop TODO consider just with another while
			}
            
            // ------------- BEST !!!!!
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
    	  // System.err.println("Some unexpected exception... Closing the applicaiton");
    	  super.updateUtilsServerInfos(String.format("Some unexpected exception..."));
    	  Utils.getSingletonInstance().resetAllValues();
    	  super.stopSendingThread();
    	  ex.printStackTrace();
    	  //super.destroyUInputDevice();
    	  //System.exit(1);
      }
   }
	   
	
	
	

	
	
//	public static void main(String [] args) {
//	   System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
//	   System.out.flush();
//	   
//	   int port = Integer.parseInt(args[0]);
//	   
//	   try {
//	      Thread t = new TCPServer(port);
//	      t.start();
//	      //http://stackoverflow.com/questions/27381021/detect-a-key-press-in-console
//		    
//	      
//	      TerminalListener terminalListener = new TerminalListener();
//	      terminalListener.start();
//	      
//	   }catch(IOException e) {
//		   System.err.println("Could start the main TCP server thread. Check if the port "+port+" is not in use.");
//	      e.printStackTrace();
//	   }
//
//         
//	}
	
}