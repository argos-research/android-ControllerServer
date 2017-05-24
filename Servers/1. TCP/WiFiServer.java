
//taken from 5. on 04.05 18:51


import java.net.*;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Enumeration;

import org.json.JSONException;
import java.util.Scanner;

public class WiFiServer extends Thread {
private ServerSocket serverSocket;
private boolean mClosingAfterEach;
private int mCounterReset;

private int i = 1; //for counting the output stream of the server

private final long SEND_INTERVAL_MILIS = 500;

private double bytesReceived = 0;
private String clientSocketAddress = "";

private String stateString = "";
private Utils mUtils;

private uInputJNI mUInputJNI;

	
	public WiFiServer(int port, boolean closingAfterEach, int counterReset) throws IOException {
	   serverSocket = new ServerSocket(port);
	   //serverSocket.setSoTimeout(30*1000);
	   mClosingAfterEach = closingAfterEach;
	   mCounterReset = counterReset;

	   stateString = mClosingAfterEach ? "Closing the socket after each call (port "+port+")." : "Keeping the channel open (port "+port+")." ;

	   mUInputJNI = uInputJNI.getSingletonInstance();
	   
	   mUtils = new Utils(stateString, mUInputJNI);
	}

	public void destroyUInputDevice(){
		mUInputJNI.destroyUInputDevice();
	}

	public void createUInputDevice(){
		mUInputJNI.setupUInputDevice();
	}

	public void run() {
	   if(mClosingAfterEach){
	      System.out.println(stateString);
	      int i = 1;
	      try {
	         while(true) {
                 System.out.println("\nWaiting for client on port "
                		 	+serverSocket.getLocalPort() + "...");
	             
	        	
	
	             Socket server = serverSocket.accept();
	           
	          	 clientSocketAddress = server.getRemoteSocketAddress().toString();
	          	 clientSocketAddress = clientSocketAddress.substring(1,clientSocketAddress.length()); //removes the bullshit
	          	 mUtils.setClientAddress(clientSocketAddress);
	          	 
	             System.out.println("Connection established with " + server.getRemoteSocketAddress());
	          	
	             // BEST VERSION!!!
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            mUtils.resetAllValues();
	            
	            //System.out.println(in.readUTF());
	            try {
					mUtils.handleInput(mUtils.toJSON(in.readUTF()));
					
					
					//this doesn't change much the output because the socket is being always closed
					// => Asynchrony call is not a problem from here! 
					String msg = "Thanks for connecting\r";
						
		            DataOutputStream out = new DataOutputStream(server.getOutputStream());
		            out.writeUTF((i++) + " Thank you for connecting to " + server.getLocalSocketAddress()
		                + "\nGoodbye!");
		            out.flush();
		            //out.write(msg.getBytes(),0,msg.length()); //always with write and not writeUTF because it sends 
		                                                      //funny data to the client!
		            server.close(); //opt try not closing here with not closing on the client side as well
			
		            if(i > mCounterReset+1)
			               i = 1;
		            
				}catch (JSONException e) {
					System.err.println("The client is not sending JSON files! Disconnecting...");
					mUtils.resetAllValues();
					destroyUInputDevice();
					//e.printStackTrace();
				}catch (IOException e) {
		            //e.printStackTrace();
					System.err.println("The client has disconnected.");
					mUtils.resetAllValues();
					destroyUInputDevice();
				}
	
	            // ------------- BEST !!!!!
	          }
	      }catch(SocketTimeoutException s) {
	            System.out.println("Socket timed out!"); 
	            mUtils.resetAllValues();
	            destroyUInputDevice();
	      }catch (IOException e) {
	            //e.printStackTrace();
				System.err.println("Failed to accept the server socket.");
				mUtils.resetAllValues();
				destroyUInputDevice();
			}
	
	   }else{ //not closing after each FOR NOW ONLY THIS VERSION!
	      System.out.println(stateString);
          System.out.println("\nWaiting for client on port "+
             serverSocket.getLocalPort() + "...");
	      
	      try {
	          Socket server = serverSocket.accept();

	           
	          clientSocketAddress = server.getRemoteSocketAddress().toString();
	          clientSocketAddress = clientSocketAddress.substring(1,clientSocketAddress.length()); //remove the bullshit
	          mUtils.setClientAddress(clientSocketAddress);

	          System.out.println("Connection established with  " + server.getRemoteSocketAddress());
	          mUtils.resetAllValues();
	          this.createUInputDevice(); //initialize the device if it not currenlty active
	          
	          long lastSentMilis = 0, currentTime = 0;
	          Thread sendThread;
	          while(true) {
	             // BEST VERSION!!!
	            DataInputStream in = new DataInputStream(server.getInputStream());
	            
	            //System.out.println(in.readUTF());
	            try{
	            	mUtils.handleInput(mUtils.toJSON(in.readUTF()));
	            	
	            	//Asynchrony call is NOT POSSIBLE here as above => the bluetooth approach
	            	//start it on thread here and the input on the client
	            	currentTime = System.currentTimeMillis();
        			if(lastSentMilis == 0 || (currentTime - lastSentMilis > SEND_INTERVAL_MILIS)){ //don't overload the output stream
        				//according to http://stackoverflow.com/questions/14494352/can-you-write-to-a-sockets-input-and-output-stream-at-the-same-time
                        // read should be in a separate thread
        				// this optimizes twice the speed of receiving!!!!
        				
        				sendThread = new Thread(new Runnable() {
							
							@Override
							public void run() {
								//System.out.println("Sending");
			        			String greeting = (i++) + " Thank you for connecting to " + server.getLocalSocketAddress() +"\n";
			        			try {
			   
			    		            DataOutputStream out = new DataOutputStream(server.getOutputStream());
			    		            //out.write(greeting.getBytes(),0,greeting.length());
			    		            out.writeUTF(greeting);
			    		            out.flush();
			    		            //System.out.println("Send " + greeting);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}		//if the client is not reading it it will block the stream									
							}
						});
        				sendThread.start();
        				lastSentMilis = currentTime;
        				
        			}
		            
		            //out.write(msg.getBytes(),0,msg.length()); //always with write and not writeUTF because it sends 
		                                                      //funny data to the client!
		
		            //server.close(); //opt try not closing here with not closing on the client side as well
		            if(i > mCounterReset+1)
		               i = 1;
		            
	            } catch (JSONException e) {
					System.err.println("The client is not sending JSON files! Disconecting...");
					//e.printStackTrace();
					mUtils.resetAllValues();
					this.destroyUInputDevice();
				}catch (IOException e) {
		            //e.printStackTrace();
					System.err.println("The client has disconnected.");
					mUtils.resetAllValues();
					server.close();
					this.destroyUInputDevice();
					this.run(); //keep in in the loop TODO consider just with another while
				}
	            
	            // ------------- BEST !!!!!
	          }
	      }catch(SocketTimeoutException s) {
	            System.out.println("Socket timed out!");
	            mUtils.resetAllValues();
	            this.destroyUInputDevice();
	      }catch (IOException e) {
	         	//e.printStackTrace();
				System.err.println("Failed to accept the server socket.");
				mUtils.resetAllValues();
				this.destroyUInputDevice();
	      } 
	   }
	   
	}
	
	

	
	
	public static void main(String [] args) {
	   System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
	   System.out.flush();
	   
	   int port = Integer.parseInt(args[0]);
	   int keepSocketOpen = Integer.parseInt(args[1]); //1 for true and 0 for false
	   int counterResetInterval = Integer.parseInt(args[2]); //when the counter for the server's input stream to be resset
	   try {
			System.err.println("TCP server running on "+ getLocalHostLANAddress().getHostAddress() +":"+port+ "\n");
		} catch (UnknownHostException e1) {
			System.err.println("Could retrive the local IP address of this machine.");
			e1.printStackTrace();
		}	
	   
	   try {
	      Thread t = new WiFiServer(port, keepSocketOpen == 1,counterResetInterval);
	      t.start();
	      //http://stackoverflow.com/questions/27381021/detect-a-key-press-in-console
		    Scanner keyboard = new Scanner(System.in);
	        while (true) {
	            String input = keyboard.nextLine();
	            if(input != null) {
	                if ("x".equals(input)) {
	                	keyboard.close();
	                    System.out.println("Exiting the programm");
	                    ((WiFiServer) t).destroyUInputDevice();
	                    System.exit(0);

	                } 
	            }
	        }
	   }catch(IOException e) {
		   System.err.println("Could start the main server thread. Check if the port "+port+" is not in use.");
	      e.printStackTrace();
	   }

	   
        

         
	}
	
	
	//found on http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
	    try {
	        InetAddress candidateAddress = null;
	        // Iterate all NICs (network interface cards)...
	        for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
	            NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
	            // Iterate all IP addresses assigned to each card...
	            for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
	                InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
	                if (!inetAddr.isLoopbackAddress()) {

	                    if (inetAddr.isSiteLocalAddress()) {
	                        // Found non-loopback site-local address. Return it immediately...
	                        return inetAddr;
	                    }
	                    else if (candidateAddress == null) {
	                        // Found non-loopback address, but not necessarily site-local.
	                        // Store it as a candidate to be returned if site-local address is not subsequently found...
	                        candidateAddress = inetAddr;
	                        // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
	                        // only the first. For subsequent iterations, candidate will be non-null.
	                    }
	                }
	            }
	        }
	        if (candidateAddress != null) {
	            // We did not find a site-local address, but we found some other non-loopback address.
	            // Server might have a non-site-local address assigned to its NIC (or it might be running
	            // IPv6 which deprecates the "site-local" concept).
	            // Return this non-loopback candidate address...
	            return candidateAddress;
	        }
	        // At this point, we did not find a non-loopback address.
	        // Fall back to returning whatever InetAddress.getLocalHost() returns...
	        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
	        if (jdkSuppliedAddress == null) {
	            throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
	        }
	        return jdkSuppliedAddress;
	    }
	    catch (Exception e) {
	        UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
	        unknownHostException.initCause(e);
	        throw unknownHostException;
	    }
	}
}