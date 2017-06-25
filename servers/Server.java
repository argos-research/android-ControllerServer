package servers;

import org.json.JSONException;

import utils.*;

/**
 * This class represent all of the methods that a server child class
 * should use in order communicate with the HTTP local server from 
 * Speed Dreams 2 and the Client (the android device).
 *
 * It runs the receive logic on the main Thread and there is an 
 * additional thread for the parallel sending of the data.
 */
public abstract class Server extends Thread{
	
//	static enum Type{
//		TCP("TCP"),
//		UDP("UDP"),
//		Bluetooth("Bluetooth");
//		
//		private String type;
//		
//		Type(String type){
//			this.type = type;
//		}
//	}
	
	public static enum Type{
		TCP,
		UDP,
		Bluetooth,
		Nothing
	}
	
	private int serverPort;
	
	
	private Type serverType;
	
	
	private Thread senderThread;
		
	private volatile boolean keepSending = false;

	/**
	 * @param port the server port.
	 * @param serverInfo some information which will be kept always at the beginning of the terminal printed information
	 * @param type the current server {@link Type}
	 */
	public Server(int port, String serverInfo, Type type){
		this.serverPort 	= port;
		this.serverType		= type;
		
		this.updateUtilsServerInfos(serverInfo);
	}
	
	/**
	 * This method is used to update the {@link utils} server
	 * strings containing the basic server Info such as current connection,
	 * IP, Type etc. which will always stay at the very top of the terminal.
	 * There are three types String server infos in the {@link utils} :
	 * <b>serverBTinfo</b>,  <b>serverTCPinfo</b>, <b>serverUDPinfo</b>.
	 *  
	 * @param serverInfo the new server info.
	 *  
	 * @see utils
	 */
	public synchronized void updateUtilsServerInfos(String serverInfo) {
		switch(this.serverType){
		case TCP:
			Utils.getSingletonInstance().setServerTCPinfo(this.getActiveTechnologyTag("   TCP   ")+serverInfo);
			break;
		case UDP:
			Utils.getSingletonInstance().setServerUDPinfo(this.getActiveTechnologyTag("   UDP   ")+serverInfo);
			break;
		case Bluetooth:
			Utils.getSingletonInstance().setServerBTinfo(this.getActiveTechnologyTag("Bluetooth")+serverInfo);
			break;
		default: 
			break;
		}
		
	}
	
	/**
	 * String getter with {@link ServerSettings} ANSI_GREEN color if it is the current 
	 * active connection or {@link ServerSettings} ANSI_YELLOW otherwise.
	 *
	 * @param technology the name of some the requested technology.
	 * @return colored representation of the requested technology. 
	 */
	private synchronized String getActiveTechnologyTag(String technology){
		if(Utils.getSingletonInstance().getActiveConnectionType().toString().contains(technology.trim()))
			return String.format("[%s%s%s]: ",ServerSettings.ANSI_GREEN,technology,ServerSettings.ANSI_RESET);
		else
			return String.format("[%s%s%s]: ",ServerSettings.ANSI_YELLOW,technology,ServerSettings.ANSI_RESET);
	}
	
	/**
	 * This method starts the separate thread in this class
	 * responsible for sending parallel information to the client.
	 * It will send data with a delay marked in {@link ServerSettings}
	 * <b>SEND_INTERVAL_MILIS</b>.
	 * 
	 */
	public void startSendingThread(){
		this.keepSending = true;
		senderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//because of the bug in the SP2 HTTP API make sure you will wait at least 1,5 seconds in order the server to be initialized. TODO remove this when the HTTP API is fixed!
				//should be FIXED TODO CHECK IT!
				// try {
				// 	Thread.sleep(1500);
				// } catch (InterruptedException e1) {
				// 	e1.printStackTrace();
				// }
				
				while(isSending()){
					sendLogic();
					try {
						Thread.sleep(ServerSettings.SEND_INTERVAL_MILIS);
					} catch (InterruptedException e) {
						//System.out.println("Successfully disconnected.");
						keepSending = false;
					}
				}
				
			}
		});
		
		senderThread.start();
	}
	
	public synchronized boolean isSending(){
		return this.keepSending;
	}
	
	
	public synchronized void stopSendingThread(){
		this.keepSending = false;
		if(senderThread != null)
			senderThread.interrupt();
	}


	public void destroyUInputDevice(){
		uInputJNI.getSingletonInstance().destroyUInputDevice();
	}

	public void createUInputDevice(){
		uInputJNI.getSingletonInstance().setupUInputDevice();
	}
	
	
	public synchronized int getServerPort(){
		return this.serverPort;
	}
	
	public synchronized Type getServerType(){
		return this.serverType;
	}
	
	
	/**
	 * Simplification of calling "Utils.getSingletonInstance().handleInput(msg)".
	 * 
	 * @param msg the received String message.
	 * @throws JSONException if the message can not be converted into a JSON object.
	 * @see Utils
	 */
	public void handleInput(String msg) throws JSONException{
		Utils.getSingletonInstance().handleInput(msg);
	}
	


	/*********************************** Abstract methods ***********************************/

	/**
	 * This method should be over written in every child class.
	 * It holds the logic behind the <i>received</i> event.
	 */
	@Override
	public abstract void run();


	/**
	 * This method should be over written in every child class.
	 * It holds the logic behind the <i>sent</i> event and
	 * it will be called from once every {@link ServerSettings}
	 * SEND_INTERVAL_MILIS after the client is connected to 
	 * some of the servers.
	 */
	public abstract void sendLogic();
	
}
