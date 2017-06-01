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
	
	static enum Type{
		TCP,
		UDP,
		Bluetooth
	}
	
	private int serverPort;
	
	
	private Type serverType;
	
	
	private Thread senderThread;
		
	private volatile boolean keepSending = true;

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
	 * This method is used to update the {@link Utils} server
	 * strings containing the basic server Info such as current connection,
	 * IP, Type etc. which will always stay at the very top of the terminal.
	 * There are three types String server infos in the {@link Utils} :
	 * <b>serverBTinfo</b>,  <b>serverTCPinfo</b>, <b>serverUDPinfo</b>.
	 *  
	 * @param serverInfo the new server info.
	 *  
	 * @see Utils
	 */
	public void updateUtilsServerInfos(String serverInfo) {
		switch(this.serverType){
		case TCP:
			Utils.getSingletonInstance().setServerTCPinfo(serverInfo);
			break;
		case UDP:
			Utils.getSingletonInstance().setServerUDPinfo(serverInfo);
			break;
		case Bluetooth:
			Utils.getSingletonInstance().setServerBTinfo(serverInfo);
			break;
		default: 
			break;
		}
		
	}

	/**
	 * This method starts the separate thread in this class
	 * responsible for sending parallel information to the client.
	 * It will send data with a delay marked in {@link ServerSettings}
	 * <b>SEND_INTERVAL_MILIS</b>.
	 * 
	 * @param sendingRunnable
	 */
	public void startSendingThread(){
		senderThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(keepSending){
					sendLogic();
					try {
						Thread.sleep(ServerSettings.SEND_INTERVAL_MILIS);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		});
		
		senderThread.start();
	}
	
	public abstract void sendLogic();
	

	public synchronized void stopSendingThread(){
		this.keepSending = false;
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
	
	public void handleInput(String msg) throws JSONException{
		Utils.getSingletonInstance().handleInput(msg);
	}
	
	
	
	@Override
	public abstract void run();

	
	
}
