package httpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;

import servers.ServerSettings;

/**
* This class is used for obtaining information from Speed Dreams 2. 
* There is a running HTTP API server provided by Alexander Weidinger. 
* An example of the provided JSON is:
* {"speed":"-0.534997","name":"Player","rpm":"104.719757","gear":"-1","gearNb":"7","pos":"6","ncars":"6"}.
* There is also an integration for testing if the HTTP SP2 server is running
* because currently it is initialized once you have started the race and 
* the race, cars etc. are loading. While loading and before pressing "Enter"
* to start the game, the HTTP server is alive but will send back only 
* an empty JSON: "{}".
*
* @date 10.06.2017
* @author Konstantin Vankov
*/
public class HttpRequest{
	
	/**
	* Callback interface when there is a error provided from the SP2 HTTP API.
	*/
	public interface IErrorHandler {
		/**
		* Called on HTTP GET error response.
		* 
		* @param error an instance of the obtained error.
		*/
		void onError(Exception error);
	}
	
	/**
	* Callback interface for providing the successful GET response
	* from the SP2 HTTP API.
	*/
	public interface IJsonHandler extends IErrorHandler{
		/**
		* Called on HTTP GET successful response.
		*
		* @param JSON the {@link JSONObject} instance of the obtained GET response.
		*/
		void onComplete(JSONObject JSON);
	}
	
	
	/**
	* An {@link HttpRequest} instance of a HTTP GET call.
	* The GET tries to retrieve the JSON from the SP2 HTTP API.
	*
	* @param responseHandler a callback reference which holds the given HTTP API
	*		 server response which might be successful or not.
	* @return an instance of this class which points to a GET call.
	*/
	public static HttpRequest get(IJsonHandler responseHandler){
		HttpRequest req = new HttpRequest(Type.GET);
		req.jsonHandler	= responseHandler;
		return req;
	}
	
	private enum Type {
		GET,
		POST
	}
	
	private Type type						= null;
	
	// a reference of the response
	private IJsonHandler jsonHandler 		= null;
	
	// a reference of the occurred exception if there is such
	private Exception executionException 	= null;
	
	private HttpRequest(Type type){
		this.type = type;
	}
	
	
	/**
	* Method for GET/POST requester which should be run in a thread.
	* This method is called upon the <b>get</b> method instance of
	* the method from above.
	*/
	public void doInBackround(){
		//only if the server has initialized the HTTP API server
		if(this.pingHost(ServerSettings.SERVER_ADDRESS_IP,ServerSettings.SERVER_ADDRESS_PORT, ServerSettings.PING_TIMEOUT))	{	
			String res = this.doInBackground();
			if(res == null){
				this.handleError(executionException);
			}else{
				if(jsonHandler != null){
					try {
						JSONObject JSON = new JSONObject(res);
						jsonHandler.onComplete(JSON);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						handleError(e);
					}
				}
			}
		}
	}
	
	/**
	* This method runs the actual logic of the class. 
	* It is build similar to the AsyncTask android class.
	* Here is where actually the HTTP call is being made.
	* @return a String representation of the HTTP call which can be null if something went wrong.
	*/
	private String doInBackground(){
		String res = null;
		URL url;
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			url = new URL(this.getServerAddress());
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(this.type.toString());
			conn.setRequestProperty("Accept", "*/*"); //because of the funny encoding on the server side...
			conn.setConnectTimeout(ServerSettings.CONNECT_TIMEOUT);
            conn.setReadTimeout(ServerSettings.READ_TIMEOUT);
            
            InputStream errorStream = conn.getErrorStream();
            if(errorStream != null){
            	String errorString = convertInputStreamToString(errorStream);
            	System.out.println("HttpRequest Server replied with error: " + conn.getResponseCode() + " " + conn.getResponseMessage() + ": " + errorString);
            	executionException = new RuntimeException(errorString);
            	return null;
            }
            
            //read response if successful
            in = new BufferedInputStream(conn.getInputStream());
            //return convertInputStreamToString(in);
            res = convertInputStreamToString(in);
		} catch (IOException e) {
			e.printStackTrace();
            executionException = e;
            return null;
            
		} finally {
			if (conn != null) {
                conn.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		
		return res;
	}

	/**
	* A converter of {@link InputStream} to String.
	* @param inputStream some {@link InputStream} instance.
	* @return a string representation of a {@link InputStream}.
	* @throws {@link IOException} if something went wrong.
	*/
	private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }

        inputStream.close();
        return result.toString();
    }
	
	/**
	* Callbacks the <b>onError</b> callback reference on a given exception.
	* @param error some thrown error.
	*/
	private void handleError(Exception error) {
        IErrorHandler errorHandler = jsonHandler;
        if (errorHandler != null)
            errorHandler.onError(error);
    }
	
	/**
	* A getter for the server address in a proper form ("http://someIP:port").
	* @return The server address in a proper form ("http://someIP:port").
	*/
	private String getServerAddress(){
		return String.format("http://%s:%d",ServerSettings.SERVER_ADDRESS_IP, ServerSettings.SERVER_ADDRESS_PORT);
	}
	
	/**
	* Method for testing whether a HTTP request should be send to the server
	* or not. In case of server failure or if it is not running, an exception
	* will be thrown if a HTTP call is send to it.
	* @param host the HTTP server's ID/FQDN or address
	* @param port the HTTP server's port.
	* @param timeout the socket timeout.
	* @return true if the HTTP server's socket is initialized otherwise false.
	*/
	private boolean pingHost(String host, int port, int timeout) {
	   Socket socket = null;
	    try  {
	    	socket = new Socket();
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    } finally{
	    	if(socket != null){
	    		try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	}
}

