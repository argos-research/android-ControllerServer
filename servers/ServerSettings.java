package servers;

/**
 * A holder class for storing the main and most 
 * important server information.
 * @author Konstantin Vankov 
 */
public class ServerSettings {
	
	//the server location
	//public static final String SERVER_ADDRESS 		= "http://127.0.0.1:9080";
	public static final String SERVER_ADDRESS_IP 	= "127.0.0.1";
	public static final int SERVER_ADDRESS_PORT 	= 9080;
	
	// network timeouts in ms
    public static final int CONNECT_TIMEOUT 		= 500;
    public static final int READ_TIMEOUT 			= 500;
    public static final int PING_TIMEOUT 			= 500;
    
	//marks the duration between the sending packets 
	public static final long SEND_INTERVAL_MILIS = 500;
	
	//the current active technology will be marked with green
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	
}
