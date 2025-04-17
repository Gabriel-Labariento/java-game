
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DataHandler {
    private ClientGameState clientState;
    private Socket theSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner console;
    private int clientId;
    private HashMap<Character, String> idToName;
    private HashMap<String, Boolean> keyMap;
    private int clickedX;
    private int clickedY;


    public DataHandler(ClientGameState clientState){
        this.clientState = clientState;

        idToName = new HashMap<>();
        idToName.put('P', "Player");
        idToName.put('B', "Rat");

        keyMap = new HashMap<>();
        keyMap.put("W", false);
        keyMap.put("A", false);
        keyMap.put("S", false);
        keyMap.put("D", false);

        console = new Scanner(System.in);
    }

    public void closeSocketsOnShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread (()-> {
            try {
                theSocket.close();
            } catch (IOException ex) {
                System.err.println("IOException from closeSocketOnShutdown() method");
            }
        }));
    }

    public void connectToServer(ScheduledExecutorService sendInputsScheduler){
        try {
            // TODO: REMOVE AUTOMATIC IP AND LOCALHOST
            System.out.println("Please input the server's IP address: ");    
            // String ipAddress = console.nextLine();
            String ipAddress = "localhost";

            System.out.println("Please input the port number: ");
            // int portNum = Integer.parseInt(console.nextLine());
            int portNum = 7000;

            System.out.println("ATTEMPTING TO CONNECT TO SERVER...");
            theSocket = new Socket(ipAddress, portNum);
            
            //Disable Nagle's buffering algorithm: basically reduces latency
            theSocket.setTcpNoDelay(true);
            System.out.println("CONNECTION SUCCESSFUL!");

            dataIn = new DataInputStream(theSocket.getInputStream());
            dataOut = new DataOutputStream(theSocket.getOutputStream());

            startAssetsThread();
            startInputsThread(sendInputsScheduler);
            // startRenderLoop();

        } catch (IOException ex) {
            System.out.println("IOException from connectToServer() method");
        }
    }

    private void startAssetsThread(){
        Thread receiveAssetsThread = new Thread(){
            @Override
            public void run(){
                while (true){
                    try {
                        int byteLength = dataIn.readInt();
                        byte[] buffer = new byte[byteLength];
                        dataIn.readFully(buffer);
                        String receivedMessage = new String(buffer, "UTF-8");

                        // If the received message starts with the protocol identifier for map data, parse the map data
                        if (receivedMessage.startsWith(NetworkProtocol.MAP_DATA + ":")) {
                            parseMapData(receivedMessage);
                        } else {
                            clientState.getEntities().clear();
                            parseAssetsData(receivedMessage);
                        }
                    } catch (IOException ex){
                        System.out.println("IOEception from receiveAssetsThread");
                    }
                }}};
        receiveAssetsThread.start();
    }
    
    private void parseEntityData(String str){
        Boolean isLoadingClientId = true;
        Boolean isLoadingY = false;
        Boolean isLoadingEntity = false;

        Character identifier = null;
        String strX = "";
        String strY = "";
        String cid = "";

        int length = str.length();
        for (int i = 0; i < length; i++){
            char parsedChar = str.charAt(i);

            //str is always in the form of "{clientiD}{identifier}{userPlayer indicator}{roomAsset indicator}{x},{y}"
            if (Character.isLetter(parsedChar)){
                if (isLoadingClientId){
                    clientId = Integer.parseInt( (String) cid);
                    isLoadingClientId = false;
                }    

                //Create new entity from previous data as new identifier is reached
                if (isLoadingEntity){					
                    loadAsset(identifier, Integer.parseInt(strX), Integer.parseInt(strY));
                    isLoadingY = false;
                    strX = "";
                    strY = "";
                }

                isLoadingEntity = true;
                identifier = parsedChar;
            }
            else if (isLoadingClientId)
                //Parse starting values as clientId
                cid += parsedChar;
            else {
                switch (parsedChar) {
                    //Check if userplayer identifier
                    case '$':
                        try {
                            clientState.setUserPlayer(new Player(clientId, Integer.parseInt(strX), Integer.parseInt(strY)));
                        } catch (Exception e) {
                            System.out.println("Exception at parseEntityData() of DataHandler");
                        }
                        break;
                    //Check if currentRoom identifier
                    case '%':
                        // clientState.setCurrentRoom(new Room(identifier)); TODO
                        break;
                    //Check if x and y delimiter
                    case ',':
                        isLoadingY = true;
                        break;
                    default:
                        //Load x and y values
                        if(!isLoadingY)
                            strX += parsedChar;
                        else
                            strY += parsedChar;
                        break;
                }
            }}
    }

    /**
     * Parses a serialized string expected to be in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     * @param message a serialized string in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     */
    private void parseAssetsData(String message){

        String[] messageParts = message.split("\\" + NetworkProtocol.DELIMITER); // Have to use \\ to escape. Turns out "|" is special for java
        this.clientId = Integer.parseInt(messageParts[0]);

        for (String part : messageParts) {
            if (part.startsWith(NetworkProtocol.USER_PLAYER + ":")) {
                // System.out.println("Parsing player");
                String[] playerCoordinates = part.substring(NetworkProtocol.USER_PLAYER.length() + 1).split(NetworkProtocol.SUB_DELIMITER);
                int playerX = Integer.parseInt(playerCoordinates[0]);
                int playerY = Integer.parseInt(playerCoordinates[1]);
        
                loadAsset('P', playerX, playerY);
                // System.out.println(" user Player loaded");

                try {
                    clientState.setUserPlayer(new Player(clientId, playerX, playerY));    
                } catch (Exception e) {
                    System.out.println("Exception in parseAssetData() when setting user player");
                } 
            } else if (part.startsWith(NetworkProtocol.PLAYER + ":")) {
                String[] otherPlayerData = part.substring(NetworkProtocol.PLAYER.length() + 1).split(NetworkProtocol.SUB_DELIMITER);
                int otherId = Integer.parseInt(otherPlayerData[0]);
                int x = Integer.parseInt(otherPlayerData[1]);
                int y = Integer.parseInt(otherPlayerData[2]);
                
                if (otherId != clientId) {
                    loadAsset('P', x, y);
                    clientState.addEntity(new Player(otherId, x, y));
                } 
            } else if (part.startsWith(NetworkProtocol.ENTITY + ":")) {
                // TODO: implementation for other entities
                // String[] entityCoordinates = part.substring(2).split(NetworkProtocol.SUB_DELIMITER);
                // int entityX = Integer.parseInt(entityCoordinates[0]);
                // int entityY = Integer.parseInt(entityCoordinates[1]);
                // loadAsset('B', entityX, entityY);
            }
            

        }
        
    }

    /**
     * TODO: JAVA-DOC
     * @param message
     */
    private void parseMapData(String message){
        DungeonMapDeserializeResult result = new DungeonMap().deserialize(message);
        clientState.setCurrentRoom(result.getStartRoom());
        clientState.setAllRooms(result.getAllRooms());
        
    }

    public void loadAsset(char identifier, int x, int y){
        String name = idToName.get(identifier);
        if(name.equals("Player"))
            clientState.addEntity((new Player(0, x, y)));
        else if(name.equals("Rat"))
            System.out.println("different name accessed");
    }

    public void startInputsThread(ScheduledExecutorService sendInputsScheduler){
        final int GAMELOOPINTERVAL = 16;

        final Runnable sendInputsData = new Runnable(){
            @Override
            public void run() {
                try {
                        String inputDataString = getInputsData();

                        // Send data if there are any actual inputs only
                        if (!inputDataString.isEmpty()) {
                            byte[] inputDataBytes = inputDataString.getBytes("UTF-8");
                            dataOut.writeInt(inputDataBytes.length);
                            dataOut.write(inputDataBytes);
                        }
                    } catch (IOException ex) {
                        System.out.println("IOException from startInputsThread");
                    }   
            }
        };
        sendInputsScheduler.scheduleAtFixedRate(sendInputsData, 0, GAMELOOPINTERVAL, TimeUnit.MILLISECONDS);
    }

    public String getInputsData(){
        String str = "";
        if(keyMap.get("W"))
            str += "W";
        if(keyMap.get("A"))
            str += "A";
        if(keyMap.get("S"))
            str += "S";
        if(keyMap.get("D"))
            str += "D";
        if(clickedX != 0 && clickedY != 0){
            str += clickedX + "," + clickedY;
            clickedX = 0;
            clickedY = 0;
        }
        return str;
    }

    public void keyInput(String input, Boolean isPressed){
        switch (input) {
            case "W":
                keyMap.replace("W", isPressed);
                break;
            case "A":
                keyMap.replace("A", isPressed);
                break;
            case "S":
                keyMap.replace("S", isPressed);
                break;
            case "D":
                keyMap.replace("D", isPressed);
                break;      
        }
    }

    public void clickInput(int x, int y){
        clickedX = x;
        clickedY = y;
    }

}