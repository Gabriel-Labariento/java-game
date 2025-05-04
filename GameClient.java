
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameClient {
    public static final int TRANSFERINTERVAL = 16;
    private final ClientMaster clientState;
    private Socket theSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner console;
    private int clientId;
    private final HashMap<Character, String> idToName;
    private final HashMap<String, Boolean> keyMap;
    private int clickedX;
    private int clickedY;

    public GameClient(ClientMaster clientState){
        this.clientState = clientState;
        Executors.newSingleThreadScheduledExecutor();

        idToName = new HashMap<>();
        idToName.put('P', "Player");
        idToName.put('B', "Rat");
        idToName.put('S', "PlayerSlash");

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
            

            System.out.println("ATTEMPTING TO CONNECT TO SERVER...");
            theSocket = new Socket(ipAddress, 5000);
            
            //Disable Nagle's buffering algorithm: basically reduces latency
            theSocket.setTcpNoDelay(true);
            System.out.println("CONNECTION SUCCESSFUL!");

            dataIn = new DataInputStream(theSocket.getInputStream());
            dataOut = new DataOutputStream(theSocket.getOutputStream());

            startAssetsThread();
            startInputsThread(sendInputsScheduler);

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
                        if (receivedMessage.startsWith(NetworkProtocol.MAP_DATA)) {
                            parseMapData(receivedMessage);
                        } else {
                            synchronized (clientState.getEntities()) { // Synchronize entities arraylist to remove flickering
                                clientState.getEntities().clear();
                                parseEntitiesData(receivedMessage);
                            }
                        }
                    } catch (IOException ex){
                        System.out.println("IOEception from receiveAssetsThread");
                    }
                }}};
        receiveAssetsThread.start();
    }
    
    /**
     * Parses a serialized string expected to be in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     * @param message a serialized string in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     */
    private void parseEntitiesData(String message){

        String[] messageParts = message.split("\\" + NetworkProtocol.DELIMITER); // Have to use \\ to escape. Turns out "|" is special for java
        this.clientId = Integer.parseInt(messageParts[0]);

        for (String part : messageParts) {
            if (part.startsWith(NetworkProtocol.USER_PLAYER)) {
                // System.out.println("Parsing player");
                String[] playerData = part.substring(NetworkProtocol.USER_PLAYER.length()).split(NetworkProtocol.SUB_DELIMITER);
                // System.out.println("User player data: " + part);
                int playerId = Integer.parseInt(playerData[0]);
                int playerX = Integer.parseInt(playerData[1]);
                int playerY = Integer.parseInt(playerData[2]);
                double playerHealth = Double.parseDouble(playerData[3]);
                int playerRoomId = Integer.parseInt(playerData[4]);
        
                // System.out.println(" user Player loaded");
                try {
                    Room currentRoom = clientState.getRoomById(playerRoomId);
                    Player player = new Player(playerId, playerX, playerY);
                    player.setCurrentRoom(currentRoom);
                    player.setHitPoints(playerHealth);
                    clientState.setUserPlayer(player);
                    clientState.setCurrentRoom(currentRoom);
                        
                } catch (Exception e) {
                    System.out.println("Exception in parseAssetData() when setting user player");
                } 
            } else if (part.startsWith(NetworkProtocol.PLAYER)) {
                String[] otherPlayerData = part.substring(NetworkProtocol.PLAYER.length()).split(NetworkProtocol.SUB_DELIMITER);
                // System.out.println("Other player data: " + part);

                // Don't load if not in the same room as the client
                int otherRoomId = Integer.parseInt(otherPlayerData[4]);
                if (otherRoomId != clientState.getCurrentRoom().getRoomId()) continue;

                int otherId = Integer.parseInt(otherPlayerData[0]);
                int x = Integer.parseInt(otherPlayerData[1]);
                int y = Integer.parseInt(otherPlayerData[2]);
                double hp = Double.parseDouble(otherPlayerData[3]);
                
                
                // Only load the player if it is not the user player and it is in the same room
                if ( (otherId != clientId) && (otherRoomId == clientState.getCurrentRoom().getRoomId()) ) {
                    Player other = new Player(otherId, x, y);
                    other.setCurrentRoom(clientState.getRoomById(otherRoomId));
                    other.setHitPoints(hp);
                    clientState.addEntity(other);
                } 
            } else if (part.startsWith(NetworkProtocol.ENTITY)) {

                // System.out.println("Whole entity string: " + part);
                String[] entityData = part.substring(NetworkProtocol.ENTITY.length()).split(NetworkProtocol.SUB_DELIMITER);
                
                if (entityData.length >= 6) {
                    int roomId = Integer.parseInt(entityData[4]);
                    if (!(roomId == clientState.getCurrentRoom().getRoomId())) continue;
                    
                    char identifier = entityData[0].toCharArray()[0];
                    int id = Integer.parseInt(entityData[1]);
                    int x = Integer.parseInt(entityData[2]);
                    int y = Integer.parseInt(entityData[3]);
                    int sprite = Integer.parseInt(entityData[5]);
                    loadEntity(identifier, id, x, y, roomId, sprite);
                }
                // for (String string : entityData) {
                //     // System.out.println("Entity string: " + string);
                // }
                // Don't load if not in the same room as the client.
                else {
                    int roomId = Integer.parseInt(entityData[4]);
                    if (!(roomId == clientState.getCurrentRoom().getRoomId())) continue;
                    
                    char identifier = entityData[0].toCharArray()[0];
                    int id = Integer.parseInt(entityData[1]);
                    int x = Integer.parseInt(entityData[2]);
                    int y = Integer.parseInt(entityData[3]);
                    loadEntity(identifier, id, x, y, roomId, 0); // TODO: TEMPORARY 0 SPRITE   
                }
                 
                
            }
        
        }
        
    }

    /**
     * Parses a part of the String received by the receiveAssetsThread responsible for the map data.
     * After parsing, it sets the currentRoom and allRooms fields of the clientState.
     * @param message the substring containing map data
     */
    private void parseMapData(String message){
        DungeonMapDeserializeResult result = new DungeonMap().deserialize(message);
        clientState.setCurrentRoom(result.getStartRoom());
        clientState.setAllRooms(result.getAllRooms());
    }


    public void loadEntity(char identifier, int id, int x, int y, int roomId, int sprite){
        String name = idToName.get(identifier);
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        if (name == null) {
            System.out.println("Warning: unknown identity identifier " + identifier);
        } else {
            switch (name) {
                case "Rat":
                    Rat r = new Rat(x, y);
                    r.setId(id);
                    r.setCurrentRoom(clientState.getRoomById(roomId));
                    r.setCurrSprite(sprite);
                    clientState.addEntity(r);
                    // System.out.println("Added rat to client entities");
                    break;
                case "PlayerSlash":
                    PlayerSlash ps = new PlayerSlash(clientId, x, y, 32, 32, true, roomId);
                    ps.setId(id);
                    ps.matchHitBoxBounds();
                    clientState.addEntity(ps);
                    // System.out.println("Added playerSlash to client entities");
                    break;
                default:
                    break;
            }
    }
    }

    public void startInputsThread(ScheduledExecutorService sendInputsScheduler){
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
        sendInputsScheduler.scheduleAtFixedRate(sendInputsData, 0, TRANSFERINTERVAL, TimeUnit.MILLISECONDS);
    }

    public String getInputsData(){
        StringBuilder str = new StringBuilder();

        if(keyMap.get("W")) str.append("W");
        if(keyMap.get("A")) str.append("A");
        if(keyMap.get("S")) str.append("S");
        if(keyMap.get("D")) str.append("D");

        if(clickedX != 0 && clickedY != 0){
            str.append(NetworkProtocol.DELIMITER);
            str.append(NetworkProtocol.CLICK);
            str.append(clickedX);
            str.append(",");
            str.append(clickedY);
            clickedX = 0;
            clickedY = 0;
        }
        return str.toString();
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