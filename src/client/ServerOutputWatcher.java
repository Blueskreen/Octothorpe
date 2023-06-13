package client;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerOutputWatcher implements Runnable{
	private Thread thread;
	private BufferedReader input;
	private boolean returnNextMessage = true;
	
	public ServerOutputWatcher (BufferedReader input) {
		this.input = input;
		thread = new Thread();
		thread.start();
	}

	@Override
	public void run() {
		String message;
		while(true) {
			message = getCommandResponses();
			if(!message.isBlank())
				System.out.println(message);
		}
	}
	
	private String getCommandResponses() {
        StringBuilder response = new StringBuilder();
        try{
        	// wait for there to be something on the stream
        	while(!input.ready()) {}
        	
        	while(input.ready()) {
        		response.append(input.readLine()+"\n");
        	}	
        }
        catch (IOException e) {
        	e.printStackTrace();
        }
        return response.toString();
    }
}