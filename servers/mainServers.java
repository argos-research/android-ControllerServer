package servers;

import java.io.IOException;

import utils.TerminalListener;

public class mainServers {

	private static Thread TCPServer;
	private static Thread UDPServer;
	private static Thread BluetoothServer;


	public static void main(String[] args) {
		System.out.print("\033[H\033[2J"); //not working in eclipse but works in terminal. Flushes the screen
		System.out.flush();
		
		if(args.length != 2){
			System.err.println("Missing ports! Start the program with additional [TCPPort] [UDPPort] !");
			System.exit(1);
		}
		
		int tcpPort = Integer.parseInt(args[0]);
		
		try {
			TCPServer = new TCPServer(tcpPort);
			TCPServer.start();
		}catch(IOException e) {
			String error = "Could start the main TCP server thread. Check if the port "+tcpPort+" is not in use.";
			e.printStackTrace();
			((TCPServer) TCPServer).updateUtilsServerInfos(error);
		}
		
		int udpPort = Integer.parseInt(args[1]);
	       
		UDPServer = new UDPServer(udpPort);
		UDPServer.start();
		
        
		//http://stackoverflow.com/questions/27381021/detect-a-key-press-in-console
		TerminalListener terminalListener = new TerminalListener();
		terminalListener.start();
	}

}
