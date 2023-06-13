package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

import server.OctothorpeServer;
import server.Player;

public class OctothorpeClientController {
    private Socket socket;
    private BufferedWriter output;
    private ServerOutputWatcher inputWatcher;
    
    protected String watcherMessage;
    protected OctothorpeClientView view;
    
    private List<Player> players;
    private Player currentPlayer;
    
    public boolean isRunning = true;
    
    private static Map<Character, String> commandMap;
    {
    	commandMap = new HashMap<Character, String>();
    	commandMap.put(OctothorpeClientView.moveNorth, "move north\r\n");
    	commandMap.put(OctothorpeClientView.moveEast, "move east\r\n");
    	commandMap.put(OctothorpeClientView.moveSouth, "move south\r\n");
    	commandMap.put(OctothorpeClientView.moveWest, "move west\r\n");
    	commandMap.put(OctothorpeClientView.showMap, "map\r\n");
    	commandMap.put(OctothorpeClientView.showPlayers, "players\r\n");
    	commandMap.put(OctothorpeClientView.showTreasure, "treasure\r\n");
    	commandMap.put(OctothorpeClientView.sendChat, "chat");// this one is special
    	commandMap.put(OctothorpeClientView.quit, "quit\r\n");
    	commandMap.put(OctothorpeClientView.help, "help\r\n");	
    }
    
    public OctothorpeClientController(String address, int port, OctothorpeClientView view) {
        try {
            socket = new Socket(address, port);
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inputWatcher = new ServerOutputWatcher(new BufferedReader (new InputStreamReader(socket.getInputStream())));
            this.view = view;
        }
        catch (IOException e) {
            System.err.println("Error connecting to server at" + address + ":" + port);
            e.printStackTrace();
        }
    }
    
    public void runClient() {
        handleLogin();
        sendCommands();
    }
    
    private void handleLogin() {
    	view.displayMessage("Enter a username: ");
    	String username = view.getUserMessage();
    	boolean success = false;
    	String response;
    	
    	writeToServer("login "+username+"\r\n");
    	
//    	while(!success) {
//    		
//    		writeToServer("login "+username+"\r\n");
//    		response = getMessageFromWatcher();
//    		view.displayMessage(response);
//    		if(!response.startsWith(Integer.toString(OctothorpeServer.serverError))) {
//    			success = true;
//    		}
//    		else {
//    			username = view.getUserMessage();
//    		}
//    	}
    }
    
//    private String getMessageFromWatcher() {
//    	inputWatcher.returnNextMessage = true;
//    	// wait for the watcher to set the message
//    	while(watcherMessage == null) {}
//    	inputWatcher.returnNextMessage = false;
//    	return watcherMessage;
//	}

	private void sendCommands() {
    	char command;
    	while(isRunning) {
        	command = view.getUserInput();
        	if(command != '\0') {
        		if(command == OctothorpeClientView.quit)
        			isRunning = false;
        		writeToServer(commandMap.get(command));
        		// getting messages from the server is hendled my the watcher
        	}
        }
    }
    
    public List<Player> getPlayers(){
        return players;
    }
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public void writeToServer(String command){
    	if(command == null || command.isEmpty())
    		return;
    	try {
			output.write(command);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
