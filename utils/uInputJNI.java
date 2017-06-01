package utils;

public class uInputJNI {
	
	
	/**
	 * Load the JNI library at the very beginning
	 */
	static {                                                                     
        try {                                                                  
           System.loadLibrary( "uInputJNI" );  
           System.out.println("uInputJNI library succesfully loaded.");
        }                                                                      
        catch( UnsatisfiedLinkError e ) {                                      
           System.err.println(                                                 
              "Could not load the native uInputJNI code library." ); 
           System.exit(1);                                                     
        }                                                                      
	}
	
	private boolean uInputDeviceInitilized = false;
	
	private uInputJNI(boolean state){
		this.uInputDeviceInitilized = state;
	}
	
	private static uInputJNI myIntance = null;
	
	public synchronized static uInputJNI getSingletonInstance(){
		if(myIntance == null){
			
			myIntance = new uInputJNI(false);
			//try to initialize the uInput device
			if(!myIntance.setup_uinput_device()){
				 System.err.println(                                                 
			              "Could not initilize the uInput device." ); 
			           System.exit(1); 
			}else{
				System.out.println("UInput device succesfully initialized.");
				myIntance.setUInputDeviceInitilized(true);
			}
				
		}else{
			System.out.println("UInput is initialized so will be reused.");
		}
		
		return myIntance;
	}
	
	public synchronized boolean isUInputDeviceInitilized(){
		return this.uInputDeviceInitilized;
	}
	
	public synchronized void setUInputDeviceInitilized(boolean newState){
		this.uInputDeviceInitilized = newState;
	}
	
	public synchronized boolean setupUInputDevice(){
		//only if it is not initialized
		if(!uInputDeviceInitilized){
			//try to set it up
			this.uInputDeviceInitilized = this.setup_uinput_device();
			return this.uInputDeviceInitilized;
		}else{
			//otherwise it is already initialized
			return uInputDeviceInitilized;
		}
	}
	
	public synchronized void destroyUInputDevice(){
		if(uInputDeviceInitilized){
			//only if it is initialized
			this.close_device();
			this.uInputDeviceInitilized = false;
					
		}
	}
	
	/**
	 * Initialization of the uInput device
	 * @return true if successful otherwise false
	 */
	public native boolean setup_uinput_device();
	
	/**
	 * Sending a key event to the uinput device
	 * @param key_code shows the corresponding hex value of the key representation. 
	 * 			For more information about those exact values, please take a look at linux/input.h
	 * 			or just follow this instance #include <linux/input.h> in the C code. 
	 * 			In {@link uInputValuesHolder} can be found a representation of these keys
	 * 			and their values.
	 */
	public native void trigger_single_key_click(int key_code);
	
	/**
	 * In my model I have 4 axis of movement - forward, backward, left, right.
	 * The idea is to have 10 'steps' for each of these axis in order to 
	 * map if the accelerometer is only 60% rotated in some direction or 
	 * if its only 10% (in this case the 'step' is 10%). To do so, I will
	 * have 10 possible values for each of these 4 axis. This means, that
	 * I will have 10^4 possibilities => I will need log2(10^4) bit to 
	 * send properly my accelerometer data to the server. log2(10^4) is 
	 * ~ 13.289 => I can map my accelerometer values to 14 bit. The first
	 * possibility for this would be to use the java primitive short type
	 * since it has 16 bits of length. => for each axis I will have available 
	 * 4 bit to map some vales. These 4 bits decrease my 'step' from 10% to
	 * 100/(2^4) = 6.25 % which make it more accurate because instead of 
	 * 10 'steps' now I will have 16.
	 * 
	 */
	
	/**
	 * Following the above comment, this method should trigger a 
	 * movement on the X axis. For now I will use the mouse axis
	 * @param step to move in [-16;16]
	 */
	public native void trigger_axis_X_event(int step);

	/**
	 * Following the above comment, this method should trigger a 
	 * movement on the Y axis. For now I will use the mouse axis
	 * @param step to move in [-16;16]
	 */
	public native void trigger_axis_Y_event(int step);
	
	//TODO shutdown the device
	public native void close_device();

}
