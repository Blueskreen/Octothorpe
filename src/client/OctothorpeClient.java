package client;

public class OctothorpeClient {
    // Constants
    public static final int defaultPort = 3333;
    public static final String defaultAddress = "127.0.0.1";
    
    public static void main(String[] args) throws Exception {
        OctothorpeClient client = new OctothorpeClient(args);
        client.startClient();
    }
    
    // Instance variables
    private int clientPort = 3333;
    private String clientAddress = "127.0.0.1";
    private OctothorpeClientController controller;
    private OctothorpeClientView view;
    
    public OctothorpeClient(String[] args) throws Exception {
        parseArgs(args);
        view = new OctothorpeClientView();
        controller = new OctothorpeClientController(clientAddress, clientPort, view);
    }
    
    private void startClient() {
        controller.runClient();
    }
    
    private void parseArgs(String[] args) throws Exception {
        // assume arguments are in the format: java client <address> <port>
        switch (args.length) {
        	case 0:
        		break;
        	case 1:
                clientAddress = getAddress(args[0]);
                break;
            case 2:
                clientAddress = getAddress(args[0]);
                clientPort = getPort(args[1]);
                break;
            default:
                throw new Exception("Expected at most two arguments, but got " + args.length + ".\nArgs were: " + args);
        }
        
    }
    
    private static String getAddress(String address) {
        if (!address.matches("\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b")) {
            System.err.println(
                    "The address: " + address + "is not a valid IPv4 address.\nStarting client on " + defaultAddress);
            return defaultAddress;
        }
        return address;
    }
    
    private static int getPort(String port) {
        if (!port.matches("\\d+")) {
            System.err.println("The port: " + port + "is not a valid port.\nStarting client on " + defaultPort);
            return defaultPort;
        }
        return Integer.parseInt(port);
    }
    
}
