package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class MessageBroadcaster implements Runnable {
    private LinkedBlockingDeque<String> messages;
    private List<BufferedWriter> outputStreams;
    private Thread thread;
    
    public MessageBroadcaster() {
        messages = new LinkedBlockingDeque<String>();
        outputStreams = new ArrayList<BufferedWriter>();
        thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        try {
            sendMessages();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendMessages() throws InterruptedException, IOException {
        while (true) {
            String message = messages.take(); // blocks if there are no messages
            if (!outputStreams.isEmpty()) {
                for (BufferedWriter b : outputStreams) {
                    b.write(message);
                    b.flush();
                }
            }
        }
    }
    
    public void addMessage(String message) {
        messages.add(message);
    }
    
    public void addOutputStream(BufferedWriter output) {
        outputStreams.add(output);
    }
    
    public void removeOutputStream(BufferedWriter output) {
        outputStreams.remove(output);
    }
    
}
