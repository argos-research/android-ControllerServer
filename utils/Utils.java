package utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Utils{

	private long packageCounter = 0;
	private long initMilis,lastMilis,currMilis; //used for measuring the data flow
	private double bytesReceived = 0;
	private String clientAddress = "";

	private String cutJSON = "";
	
	private String extraInfo = "";
	
	private uInputJNI mUInputJNI;
	
	private JSONObject mGyroData = null;

	private JSONObject mAccData = null;

	public Utils(String extraInfo, uInputJNI uInputJNI){
		resetAllValues();
		this.extraInfo = extraInfo;
		this.mUInputJNI = uInputJNI;

	}

	public void setClientAddress(String someAddress){
		this.clientAddress = someAddress;
	}

	public void extractData(byte[] buffer,int bytesRead){
		String lineJSON = "";
		String received = new String(buffer, 0, bytesRead);
		
		for(int i = 0; i < received.length() ; i++){
			if(!cutJSON.equals("")){ //if we are coming from a "cut" stated, then just continue the JSON
				lineJSON = cutJSON; //continue
				lineJSON += received.charAt(i);
				//System.err.println("REsuming from cut JSON "+ cutJSON);
				cutJSON = ""; //reset it
			}
			else if(received.charAt(i) == '{'){ //beginning of JSON
				if(lineJSON.equals("")){ //only if its empty String
					lineJSON += received.charAt(i);
				}
			}else if(received.charAt(i) == '}'){ // the end of it
				//extract it according to
				//http://stackoverflow.com/questions/20070382/java-string-to-json-conversion
				try {
					lineJSON += received.charAt(i);
					//System.out.println("JSON added: "+ lineJSON);
					handleInput(toJSON(lineJSON));
					lineJSON = "";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}else{ //the normal case. here just record the string
				lineJSON += received.charAt(i);
			}
		}

		//this is the case that we run out of the buffer size and something is being cut but not indicated with the code above
		if(!lineJSON.equals(""))
			cutJSON = lineJSON;		//save it for the next time	
	}
	
	public JSONObject toJSON(String input) throws JSONException{
		bytesReceived += input.getBytes().length;	//for the speed measurment
		return new JSONObject(input);
	}

	
	//"Loop":1 marks the beginning and the "Loops count":5 marks the number of the sent messages
	//for now just measuring
//	public void handleInput(JSONObject ob) throws JSONException{
//		int loopNumber = ob.getInt("Loop");
//		//String loopNumber = (String) ob.get("Loop");
//		int loops = ob.getInt("Loops count");
//		
//		currMilis = ob.getLong("Created time");
//		
//		updateWriter(toCorretStringFormat(loopNumber, loops));
//		
//		if(loopNumber == 1){ //reset the List
//			initMilis = ob.getLong("Created time");
//		}
//		
//		if(loopNumber == loops){ //last one
//			lastMilis = ob.getLong("Created time");
//			// i = 1;
//			//System.err.println("\nIt took " + getTimeSpent() + " for creating, sedning and recieving of "+ loops +" JSON files.\n");
//			updateWriter(toCorretStringFormat(loopNumber, loops)); //print the time spent
//			//reset the longs
//			resetAllValues();
//			
//		}
//	}

	public void handleInput(String JSONString) throws JSONException{
		this.handleInput(this.toJSON(JSONString));
	}
	
	//new version
	public void handleInput(JSONObject ob) throws JSONException{
		//int loopNumber = ob.getInt("Loop");
		//String loopNumber = (String) ob.get("Loop");
		//int loops = ob.getInt("Loops count");
		
		packageCounter ++;
		
		currMilis = ob.getLong("Created time");
		
		if(ob.has("Gyro data")){
			mGyroData = ob.getJSONObject("Gyro data");
			System.out.println("Gyro data "+mGyroData.toString());
			//trigger the one key that is send
			if(mGyroData.getInt("forward") > 0){
				mUInputJNI.trigger_single_key_click(mGyroData.getInt("forward"));
			}else if(mGyroData.getInt("backward") > 0){
				mUInputJNI.trigger_single_key_click(mGyroData.getInt("backward"));
			}else if(mGyroData.getInt("left") > 0){
				mUInputJNI.trigger_single_key_click(mGyroData.getInt("left"));
			}else if(mGyroData.getInt("right") > 0){
				mUInputJNI.trigger_single_key_click(mGyroData.getInt("right"));
			}
			
		}
		//TODO consider something cleaner
		//int multiplier = 5;
		int multiplier = 64;
		//int multiplier = 4095;
		
		//accelerometer case
		if(ob.has("Accelerometer data")){
			mAccData = ob.getJSONObject("Accelerometer data");

			if(mAccData.getInt("forward") > 0){
				mUInputJNI.trigger_axis_Y_event(-mAccData.getInt("forward") * multiplier);
				//dont send negative values for the test
				//mUInputJNI.trigger_axis_Y_event(mAccData.getInt("forward") * multiplier);
			}
			if(mAccData.getInt("backward") > 0){
				mUInputJNI.trigger_axis_Y_event(mAccData.getInt("backward") * multiplier);
			}
			if(mAccData.getInt("left") > 0){
				mUInputJNI.trigger_axis_X_event(-mAccData.getInt("left") * multiplier);
			}
			if(mAccData.getInt("right") > 0){
				mUInputJNI.trigger_axis_X_event(mAccData.getInt("right") * multiplier);
			}
		}

		
		updateWriter(toCorretStringFormat());
		
		if(currMilis == 0){ //at the beginning
			initMilis = ob.getLong("Created time");
		}
		
//		if(loopNumber == loops){ //last one
//			lastMilis = ob.getLong("Created time");
//			// i = 1;
//			//System.err.println("\nIt took " + getTimeSpent() + " for creating, sedning and recieving of "+ loops +" JSON files.\n");
//			updateWriter(toCorretStringFormat(loopNumber, loops)); //print the time spent
//			//reset the longs
//			resetAllValues();
//			
//		}
		
	}
	
	
	
	
	
	public JSONArray getAccelerometerValues(String JSON) throws JSONException{
		return getAccelerometerValues(new JSONObject(JSON));
	}
	
	public JSONArray getAccelerometerValues(JSONObject ob) throws JSONException{
		//"Accelerometer data"
		return ob.getJSONArray("Accelerometer data");
	}
	
	
	public JSONArray getGyroValues(String JSON) throws JSONException{
		return getGyroValues(new JSONObject(JSON));
	}
	
	public JSONArray getGyroValues(JSONObject ob) throws JSONException{
		//"Accelerometer data"
		return ob.getJSONArray("Gyro data");
	}

	
	
	
	
	public void resetAllValues(){
		lastMilis = 0;
		initMilis = 0;
		currMilis = 0;
		bytesReceived = 0;
	}

    public String getTimeSpent() {
		if(lastMilis - initMilis > 1000)
			return ((double)(lastMilis - initMilis))/1000 + " seconds";
		else
			return (lastMilis - initMilis) + " miliseconds";
	}
    
    public String getDownloadSpeed(long currMilis,long initMilis){
    	if(currMilis - initMilis == 0){
    		return String.format("%.2f KB/s", bytesReceived/1000);
    	}else if(currMilis - initMilis < 1000){ // not big enough
    		return String.format("%.2f KB/s", bytesReceived/1000);
    	}else{
    		long diff = (currMilis - initMilis)/1000;
    		double speed = (bytesReceived/1000) / diff;
        	return String.format("%.2f KB/s", speed);
    	}
    }
    
    
    public void updateWriter(String msg){
		// try{
			//Runtime.getRuntime().exec("clear"); //instead of flushing... NOT WORKING
    		//working --> http://stackoverflow.com/questions/10241217/how-to-clear-console-in-java
			// System.out.print("\033[H\033[2J"); //not working in eclipse. Flushes the screen
			// System.out.flush();
			// System.out.println(msg);

			// systemWriter.write(msg);
			// systemWriter.flush();	
		// }catch(IOException ex){
		// 	ex.printStackTrace();
		// }
	}
    
    public String toCorretStringFormat(){
    	return String.format("%s\nFrom client %s:\nReceived %d packets.\nAverage download speed is %s.%s\n", extraInfo,clientAddress,packageCounter,this.getDownloadSpeed(currMilis,initMilis), this.getTotalTimeSpent());
    }
    
    public String getTotalTimeSpent(){
    	return lastMilis > 0 ? "\nIt took " + getTimeSpent() + " for receiving "+bytesReceived/1000000+" MB.\n" : "";
    }

    //found on http://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java
	public static InetAddress getLocalHostLANAddress() throws UnknownHostException {
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