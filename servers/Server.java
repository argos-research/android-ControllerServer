package servers;

import org.json.JSONException;

import utils.*;

public abstract class Server extends Thread{
	private Utils mUtils;

	private uInputJNI mUInputJNI;
	
	private int serverPort;
	
	/**
	 * @param port the server port.
	 * @param additionInformation some information which will be kept always at the beginning of the terminal printed information
	 */
	public Server(int port, String additionInformation){
		this.serverPort 			= port;
				
		this.mUInputJNI 			= uInputJNI.getSingletonInstance();
		
		//TODO remove this and use the singleton instance
		this.mUtils					= new Utils(additionInformation, mUInputJNI);
		
	}
	
	public void destroyUInputDevice(){
		this.mUInputJNI.destroyUInputDevice();
	}

	public void createUInputDevice(){
		this.mUInputJNI.setupUInputDevice();
	}
	
	
	public synchronized Utils getUtils(){
		return this.mUtils;
	}
	
	public synchronized uInputJNI getUInputJNI(){
		return this.mUInputJNI;
	}
	
	public synchronized int getServerPort(){
		return this.serverPort;
	}
	
	public void handleInput(String msg) throws JSONException{
		this.mUtils.handleInput(this.mUtils.toJSON(msg));
	}
	
	
	
	@Override
	public abstract void run();

	
	
}
