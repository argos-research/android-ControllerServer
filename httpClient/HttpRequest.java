package httpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;

import servers.ServerSettings;

public class HttpRequest{
	
	public interface IErrorHandler {
		void onError(Exception error);
	}
	
	public interface IJsonHandler extends IErrorHandler{
		void onComplete(JSONObject JSON);
	}
	
	
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
	
	private IJsonHandler jsonHandler 		= null;
	
	private Exception executionException 	= null;
	
	private HttpRequest(Type type){
		this.type = type;
	}
	
	
	public void doInBackround(){
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
	
	private String doInBackground(){
		String res = null;
		URL url;
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			url = new URL(ServerSettings.SERVER_ADDRESS);
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
	
	private void handleError(Exception error) {
        IErrorHandler errorHandler = jsonHandler;
        if (errorHandler != null)
            errorHandler.onError(error);
    }
	
	
}
