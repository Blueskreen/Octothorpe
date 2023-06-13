package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class OctothorpeClientView {
    public static final char moveNorth = 'w',
    						 moveEast = 'd',
    						 moveSouth = 's',
    						 moveWest = 'a',
    						 showMap = 'm',
    						 showPlayers = 'p',
    						 showTreasure = 't',
    						 sendChat = 'c',
    						 quit = 'q',
    						 help = 'h';
    						 
	
	private BufferedReader localInput;
	private Scanner inputScanner;
    
    public OctothorpeClientView() {
        localInput = new BufferedReader(new InputStreamReader(System.in));
        inputScanner = new Scanner(System.in);
        
    }
    
    public void displayMessage(String message) {
    	System.out.println(message);
    }
    
    public String getUserMessage() {
    	return inputScanner.nextLine();
    }
    
    public char getUserInput() {
    	char command;
    	return inputScanner.next().charAt(0);
//		try {
//			if(System.in.available() > 0) {
//				command = (char) System.in.read();
//				System.out.print("\b");
//				return command;
//			}
//		} 
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		return '\0';
    }
}
