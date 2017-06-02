package utils;

import java.util.Scanner;


public class TerminalListener{

	public void start(){
		Scanner keyboard = new Scanner(System.in);
        while (true) {
            String input = keyboard.nextLine();
            if(input != null) {
                if ("x".equals(input)) {
                	keyboard.close();
                    System.out.println("Exiting the programm");
                    uInputJNI.getSingletonInstance().destroyUInputDevice();
                    System.exit(0);

                } 
            }
        }
	}
}
