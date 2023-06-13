package server;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/*
 * Author: Blueskreen
 */

public class OctothorpeServer implements Runnable{
	// status codes
	public static final int information = 100,
							 playerUpdate = 101,
							 treasureNotification = 102,
							 treasureTaken = 103,
							 mapData = 104,
							 success = 200,
							 badRequest = 400,
							 serverError = 500;
	// other variables
	public static final int defaultPort = 3333;
	private static final String defaultMap = "map.txt";
	private static ServerSocket serverSocket;
	List<Socket> sockets = new ArrayList<Socket>();
	Octothorpe game;
	List<Player> allPlayers = new ArrayList<Player>();
	Thread thread;
	
	MessageBroadcaster broadcaster;
	
	public OctothorpeServer(Octothorpe game) {
		this.game = game;
		broadcaster = new MessageBroadcaster();
	}
	
	public static void main (String[] args) throws UnknownHostException, IOException, InterruptedException {
		// test for arguments for the port and map
		String mapString;
		if(args.length < 1) {
			System.out.println("Starting server with default settings: port "+defaultPort+", map "+defaultMap);
			System.out.println("For information on command line aguments run: server help");
			serverSocket = new ServerSocket(defaultPort);
			mapString = loadMap(defaultMap);
		}
		else {
            if (args[0].equalsIgnoreCase("help") || !args[0].matches("\\d+")) {
				printHelp();
				return;
			}
			serverSocket = new ServerSocket(Integer.valueOf(args[0]));
			System.out.println("Starting on port " +serverSocket.getLocalPort());
			if(args.length == 2) {
				mapString = loadMap(args[1]);
				System.out.println("Map loaded from " + args[1]);
			}
			else {
				mapString = loadMap(defaultMap);
				System.out.println("Default map, "+defaultMap+" loaded");
			}
		}
		
		// Start the logic to wait for and serve connections
		OctothorpeServer server = new OctothorpeServer(new Octothorpe(mapString));
		server.runServer(server);
	}

	private static void printHelp() {
		StringBuilder helpMessage = new StringBuilder();
		helpMessage.append("# Octothorpe Server #\n");
		helpMessage.append("The arguments for this application are as follows:\n");
		helpMessage.append("\tserver [port number] [map file]\n");
		helpMessage.append("\tThe port number should be a TCP port\n");
		helpMessage.append("\tThe map file should be a path to a text file containing the map\n\n");
		System.out.println(helpMessage.toString());
	}
	
	private static String loadMap(String fileName){
		try { return Files.readString(new File(fileName).toPath());}
		catch(IOException e) {
			System.err.println("Error loading map file: "+e.toString());
			System.err.println("Loading default map: "+defaultMap);
		}
		try {return Files.readString(new File(defaultMap).toPath());}
		catch(Exception e) {
			System.err.println("Error loading default map:");
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	private void runServer(OctothorpeServer server) throws InterruptedException, IOException {
		while(true) {
			sockets.add(serverSocket.accept());
			thread = new Thread(server);
			thread.start();
		}
	}

	@Override
	public void run() {
		try {serveContent();}
		catch(Exception e) {e.printStackTrace();}
	}
	
	public void serveContent() throws Exception{
		if(sockets.size() > 0) {
			Socket socket = sockets.remove(0);
			System.out.println("Connection from "+socket.getInetAddress()+" opened");
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			Player player = loginPlayer(input, output);
			if(player != null) {
				broadcaster.addOutputStream(output);
				sendBroadcast(playerUpdate, player.toString()+" connected");
				executePlayerCommands(input, output, player);
				game.getPlayers().remove(player);
				sendBroadcast(playerUpdate, player.toString()+" disconnected");
				broadcaster.removeOutputStream(output); // possible race condition
			}
			System.out.println("Connection from "+socket.getInetAddress()+" closing");
			socket.close();
		}
	}
	
	private void executePlayerCommands(BufferedReader input, BufferedWriter output, Player player) throws IOException {
		boolean isPlaying = true;
		String command = "";
		while(isPlaying) {
			checkForVisableTreasures(player, output);
			command = input.readLine();
			if(command == null)
				return;
			else if(command.equalsIgnoreCase("quit") || command.equalsIgnoreCase("exit")) {
				isPlaying = false;
				writeOut(output, success, "Goodby");
			}
			else if(command.toLowerCase().startsWith("move"))
				movePlayer(player, command, output);
			else if(command.equalsIgnoreCase("players"))
				showPlayers(output);
			else if(command.equalsIgnoreCase("map"))
				showMap(output);
			else if (command.equalsIgnoreCase("treasure"))
				showTreasure(output);
			else if(command.toLowerCase().startsWith("chat"))
				sendChat(player, command, output);
			else if(command.equalsIgnoreCase("help"))
				showHelp(output);
			else
				writeCommandError(output, command);
		}
	}

	private void sendChat(Player player, String command, BufferedWriter output) throws IOException {
		String[] commandParts = command.split(" ");
		if(commandParts.length < 2) {
			writeCommandError(output, command);
			return;
		}
		else {
			String message = "";
			for(int i = 1; i < commandParts.length; i++)
				message+=commandParts[i]+" ";
			sendBroadcast(information, player.getName() +" says: "+ message);
			writeOut(output, success, "Chat sent");
		}
	}

	private void checkForVisableTreasures(Player player, BufferedWriter output) throws IOException {
		List<Treasure> visableTreasures = game.getVisableTreasures(player);
		if(visableTreasures.size() < 1)
			return;
		else {
			for(Treasure t : visableTreasures)
				writeOut(output, treasureNotification, t.toStringWithLocation());
		}
	}

	private void movePlayer(Player player, String command, BufferedWriter output) throws IOException {
		String[] commandParts = command.split(" ");
		if(commandParts.length != 2) {
			writeCommandError(output, command);
			return;
		}
		Point newMove;
		if(commandParts[1].equalsIgnoreCase("north"))
			newMove = new Point(player.getLocation().x, player.getLocation().y -1);
		else if (commandParts[1].equalsIgnoreCase("east"))
			newMove = new Point(player.getLocation().x + 1, player.getLocation().y);
		else if (commandParts[1].equalsIgnoreCase("south"))
			newMove = new Point(player.getLocation().x, player.getLocation().y + 1);
		else if (commandParts[1].equalsIgnoreCase("west"))
			newMove = new Point(player.getLocation().x - 1, player.getLocation().y);
		else {
			writeCommandError(output, command);
			return;
		}
		
		if(game.isValidMove(newMove)) {
			player.setLocation(newMove);
			writeOut(output, success, player.getName() + ", ("+player.getLocation().x+", "+player.getLocation().y+")");
			sendBroadcast(playerUpdate, player.toString());
			takeTreasuer(player, output);
		}
		else
			writeOut(output, badRequest, "Invalid direction: "+commandParts[1]);
	}

	private void takeTreasuer(Player player, BufferedWriter output) {
		List<Treasure> plunder = game.claimableTreasure(player);
		for(Treasure t : plunder) {
			game.takeTreasure(t, player);
			sendBroadcast(treasureTaken, player.getName()+", "+t.getId()+", "+t.getValue());	
		}
	}

	public Player loginPlayer(BufferedReader input, BufferedWriter output) throws IOException {
		String login = input.readLine();
		if(login == null)
			return null;
		String[] loginParts = login.split(" ");
		boolean loggedIn = false;
		Player player = null;
		while(!loggedIn) {
			if(loginParts.length != 2 || !loginParts[0].equals("login") || !loginParts[1].matches("\\w+")) {
				writeOut(output, badRequest, "error");
				login = input.readLine();
				loginParts = login.split(" ");
			}
			else {
				player = new Player(game.spawnPlayer(), 0, loginParts[1]);
				// for persistence of score and location for returning players during a server's run time
				if(allPlayers.contains(player))
					player = allPlayers.get(allPlayers.indexOf(player)); // wildly inefficient, but it will have to do for now
				else
					allPlayers.add(player);
				
				if (game.hasMaxPlayers())
					writeOut(output, serverError, "Maximum player count exceeded, try again later");
				else if(game.playerExists(player))
					writeOut(output, badRequest, "Could not connect you to "+player.getName());
				else {
					loggedIn = true;
					game.getPlayers().add(player);
					showMap(output);
					showPlayers(output);
					writeOut(output, success, "Welcome to Octothorpe # The game, "+ player.getName());
				}
			}
		}
		return player;
	}
	
	private void showMap(BufferedWriter output) throws IOException {
		// Gets the lines of the map and prints them using a lambda function
		writeOut(output, mapData, "Map Size: ("+game.getMapWidth()+", "+game.getMapLength()+")");
		game.getMapText().lines().forEachOrdered(line -> {
			try{writeOut(output, mapData, line);}
			catch (Exception e) {e.printStackTrace();}
		});
		writeOut(output, success, "Game world is "+game.getMapWidth()+"x"+game.getMapLength()+" spaces");
	}

	private void showPlayers(BufferedWriter output) throws IOException {
		if(game.getPlayers().isEmpty())
			return;
		for(Player p : game.getPlayers())
			writeOut(output, playerUpdate, p.toString());
		writeOut(output, success, "There are "+game.getNumberOfPlayers()+" players");
	}
	
	private void showTreasure(BufferedWriter output) throws IOException {
		for(Treasure t : game.getTreasures())
			writeOut(output, information, t.toString());
		writeOut(output, success, "Treasure displayed");
	}
	
	private void showHelp(BufferedWriter output) throws IOException {
		writeOut(output, information, "The following are the available commands");
		writeOut(output, information, "\tmove <direction> - Valid directions are: north, east, south, west");
		writeOut(output, information, "\tquit - exits the game");
		writeOut(output, information, "\tmap - prints the map");
		writeOut(output, information, "\tplayers - lists the active players");
		writeOut(output, information, "\ttreasure - displays the unclaimed treasure available in the game");
		writeOut(output, information, "\tchat <message> - sends a message to all the active players");
		writeOut(output, information, "\thelp - displays this message");
		writeOut(output, success, "Help message displayed");
	}

	private void writeOut(BufferedWriter output, int status, String out) throws IOException {
		output.write(status + ":"+out+"\r\n");
		output.flush();
	}
	
	private void writeCommandError(BufferedWriter output, String command) throws IOException {
		writeOut(output, badRequest, "Error: \""+command+"\" is an invalid player command. Enter \"help\" for a list of commands.");
	}
	
	private void sendBroadcast(int status, String message) {
		broadcaster.addMessage(status + ":"+message+"\r\n");
	}
}