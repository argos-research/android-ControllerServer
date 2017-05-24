


import java.io.*;
import java.net.*;


import java.util.Enumeration;
import org.json.JSONException;

class UDPServer 
{
      private static Utils mUtils;
      private static uInputJNI mUInputJNI;
      private final static long SEND_INTERVAL_MILIS = 500;

      public static void main(String args[]) throws Exception
      {
         System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
         System.out.flush();

         int port = Integer.parseInt(args[0]);
         String extraInfo = "";

         try {
            extraInfo = "UDP Server running on " + getLocalHostLANAddress().getHostAddress() +":"+port+ ".\n";
         } catch (UnknownHostException e1) {
            System.err.println("Could retrive the local IP address of this machine.");
            e1.printStackTrace();
         }

         mUInputJNI = uInputJNI.getSingletonInstance();
     
         mUtils = new Utils(extraInfo, mUInputJNI);

         mUtils.updateWriter(extraInfo + "Waiting for connections...");
         DatagramSocket serverSocket = new DatagramSocket(port);

         System.out.println("Connection established on port "+ port + "!");
            byte[] receiveData = new byte[1024];
       

            long lastSentMilis = 0, currentTime = 0;
            Thread sendThread;

            while(true)
               {
                  DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                  serverSocket.receive(receivePacket);

                  InetAddress IPAddress = receivePacket.getAddress();
                  final int receiverPort = receivePacket.getPort();
                  String ip = IPAddress.toString();
                  ip = ip.substring(1,ip.length());
                  final String finalIP = ip;
                  mUtils.setClientAddress(ip);

                  String JSON  = new String( receivePacket.getData());

                  try{
                	  mUtils.handleInput(mUtils.toJSON(JSON));
                  }catch (JSONException e) {
                	  System.err.println("The client is not sending JSON files! Disconecting...");
                	  mUtils.resetAllValues();
                	  e.printStackTrace(); 
            	  }



                  currentTime = System.currentTimeMillis();
                  if(lastSentMilis == 0 || (currentTime - lastSentMilis > SEND_INTERVAL_MILIS)){ //don't overload the output stream
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
      }

      //found on http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
      private  static InetAddress getLocalHostLANAddress() throws UnknownHostException {
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
