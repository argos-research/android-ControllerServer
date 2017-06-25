package utils;

import java.util.Scanner;


/**
 * Terminal listener for closing properly the whole application.
 * <b>IMPORTANT:</b> the program should only be closed with this
 * listener because of the uInputJNI.getSingletonInstance().destroyUInputDevice()
 * which will release the memory used for the created virtual joystick!
 * @author Konstantin Vankov 
 */
public class TerminalListener{

    /*Marks the exit character of the whole server application*/
    private final String EXIT_MARKER = "x";

    /**
     * Start listening the terminal for some exit character
     * which will terminate the application properly by destroying 
     * the created uInput virtual joystick! If this is not used
     * the created device won't be deleted and this can cause 
     * some serious problems.
     */
	public void start(){
		Scanner keyboard = new Scanner(System.in);
        while (true) {
            String input = keyboard.nextLine();
            if(input != null) {
                if (input.equals(EXIT_MARKER)) {
                	keyboard.close();
                    System.out.println("Exiting the program...");
                    uInputJNI.getSingletonInstance().destroyUInputDevice();
                    System.exit(0);

                } 
            }
        }
	}
}
