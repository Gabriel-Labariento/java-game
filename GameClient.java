
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameClient {
    public static final int TRANSFERINTERVAL = 16;
    private final ClientMaster clientMaster;
    private Socket theSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner console;
    private int clientId;
    private final HashMap<Character, String> idToName;
    private final HashMap<String, Boolean> keyMap;
    private int clickedX;
    private int clickedY;
    private final ScheduledExecutorService sendInputsScheduler;
    private boolean isOnMenu;
    private GameServer gs;

    public GameClient(ClientMaster clientMaster){
        this.clientMaster = clientMaster;
        sendInputsScheduler = Executors.newSingleThreadScheduledExecutor();

        idToName = new HashMap<>();
        idToName.put('A', "HeavyCat");
        idToName.put('B', "FastCat");
        idToName.put('C', "GunCat");
        idToName.put('D', "PlayerSmash");
        idToName.put('E', "PlayerSlash");
        idToName.put('F', "PlayerBullet");
        idToName.put('G', "Rat");
        idToName.put('H', "RatKing");
        idToName.put('I', "Snakelet");
        idToName.put('J', "Snake");
        
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

    public void connectToServer(String ipAddress, int portNum){
        try {
            System.out.println("ATTEMPTING TO CONNECT TO SERVER...");
            theSocket = new Socket(ipAddress, portNum);
            
            //Disable Nagle's buffering algorithm: basically reduces latency
            theSocket.setTcpNoDelay(true);
            System.out.println("CONNECTION SUCCESSFUL!");

            dataIn = new DataInputStream(theSocket.getInputStream());
            dataOut = new DataOutputStream(theSocket.getOutputStream());

            startAssetsThread();
            startInputsThread();
            // startRenderLoop();

        } catch (IOException ex) {
            System.out.println("IOException from connectToServer() method");
        }
    }

    public void hostServer(){
        gs = new GameServer();
        gs.startGameLoop();
        gs.closeSocketsOnShutdown();
        gs.waitForConnections();      
    }
      
    public String getServerIP(){
        //Get the local machine's IPv4 address
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) e.nextElement();

                //Check if interface is down or is only visible to the host
                if (!networkInterface.isUp() || networkInterface.isLoopback())
                    continue;

                Enumeration<InetAddress> a = networkInterface.getInetAddresses();
                while (a.hasMoreElements()) {
                    InetAddress inetAddress = a.nextElement();

                    // Check if address is an Ipv4 address and is only visible to host
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("SocketException at GameClient's getServerIP() method");
        } 
        return null;  
    }

    public int getServerPort(){
        return gs.getPort();
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
                        // System.out.println("ReceivedMessage: " + receivedMessage);
                        // If the received message starts with the protocol identifier for map data, parse the map data
                        if (receivedMessage.startsWith(NetworkProtocol.MAP_DATA)) {
                            parseMapData(receivedMessage);
                        } else if (receivedMessage.startsWith(NetworkProtocol.BOSS_KILLED)) {
                            parseBossKilledData(receivedMessage);
                        } else if (receivedMessage.startsWith(NetworkProtocol.LEVEL_CHANGE)) {
                            clientMaster.getEntities().clear();
                            String mapData = receivedMessage.substring(NetworkProtocol.LEVEL_CHANGE.length());  // Receives a string containing map and player data
                            parseMapData(mapData);
                        } else {
                            synchronized (clientMaster.getEntities()) {                                 // Synchronize entities arraylist to remove flickering
                                clientMaster.getEntities().clear();
                                parseEntitiesData(receivedMessage);
                            }
                        }
                    } catch (IOException ex){
                        System.out.println("IOEception from receiveAssetsThread");
                    }
                }}};
        receiveAssetsThread.start();
    }
    
    // private String preparePlayerDataForParsing(String playerData){
    //     String[] dataParts = playerData.split(NetworkProtocol.SUB_DELIMITER);
    //     int cid = Integer.parseInt(dataParts[1]);
    //     if (cid == clientId)
    // }


    /**
     * Parses a serialized string expected to be in the form ClientId|P:playerX,playerY,playerHealth,playerRoomId|E:entity1X,entity1Y,entity2x,entity2Y...|
     * @param message a serialized string in the form ClientId|P:playerX,playerY,playerHealth,playerRoomId|E:entity1X,entity1Y,entity2x,entity2Y...|
     */
    private void parseEntitiesData(String message){

        // System.out.println(message);
        String[] messageParts = message.split("\\" + NetworkProtocol.DELIMITER); // Have to use \\ to escape. Turns out "|" is special for java
        this.clientId = Integer.parseInt(messageParts[0]);
        clientMaster.setXPBarPercent(Integer.parseInt(messageParts[1]));
        clientMaster.setUserLvl(Integer.parseInt(messageParts[2]));

        for (String part : messageParts) {
            if (part.startsWith(NetworkProtocol.USER_PLAYER)) {
                // System.out.println("Parsing player");
                String[] playerData = part.substring(NetworkProtocol.USER_PLAYER.length()).split(NetworkProtocol.SUB_DELIMITER);
                // System.out.println("User player data: " + part);
                char identifier = playerData[0].toCharArray()[0];
                int playerId = Integer.parseInt(playerData[1]);
                int playerX = Integer.parseInt(playerData[2]);
                int playerY = Integer.parseInt(playerData[3]);
                int playerHealth = Integer.parseInt(playerData[4]);
                int playerRoomId = Integer.parseInt(playerData[5]);
        
                // System.out.println(" user Player loaded");
                try {
                    Room currentRoom = clientMaster.getRoomById(playerRoomId);
                    Player player = getPlayer(identifier, playerId, playerX, playerY);
                    player.setCurrentRoom(currentRoom);
                    player.setHitPoints(playerHealth);
                    clientMaster.setUserPlayer(player);
                    clientMaster.setCurrentRoom(currentRoom);
                        
                } catch (Exception e) {
                    System.out.println("Exception in parseAssetData() when setting user player");
                } 
            } else if (part.startsWith(NetworkProtocol.PLAYER)) {
                String[] otherPlayerData = part.substring(NetworkProtocol.PLAYER.length()).split(NetworkProtocol.SUB_DELIMITER);
                // System.out.println("Other player data: " + part);

                // Don't load if not in the same room as the client
                int otherRoomId = Integer.parseInt(otherPlayerData[5]);
                if (otherRoomId != clientMaster.getCurrentRoom().getRoomId()) continue;

                char identifier = otherPlayerData[0].toCharArray()[0];
                int otherId = Integer.parseInt(otherPlayerData[1]);
                int x = Integer.parseInt(otherPlayerData[2]);
                int y = Integer.parseInt(otherPlayerData[3]);
                int hp = Integer.parseInt(otherPlayerData[4]);
                
                
                // Only load the player if it is not the user player and it is in the same room
                if ( (otherId != clientId) && (otherRoomId == clientMaster.getCurrentRoom().getRoomId()) ) {
                    Player other = getPlayer(identifier, otherId, x, y);
                    other.setCurrentRoom(clientMaster.getRoomById(otherRoomId));
                    other.setHitPoints(hp);
                    clientMaster.addEntity(other);
                } 
            } else if (part.startsWith(NetworkProtocol.ENTITY)) {
                // System.out.println("Whole entity string: " + part);
                String[] entityData = part.substring(NetworkProtocol.ENTITY.length()).split(NetworkProtocol.SUB_DELIMITER);
                
                // for (String string : entityData) {
                //     // System.out.println("Entity string: " + string);
                // }

                if (entityData.length >= 6) {
                    int roomId = Integer.parseInt(entityData[4]);
                    if (!(roomId == clientMaster.getCurrentRoom().getRoomId())) continue;
                    
                    char identifier = entityData[0].toCharArray()[0];
                    int id = Integer.parseInt(entityData[1]);
                    int x = Integer.parseInt(entityData[2]);
                    int y = Integer.parseInt(entityData[3]);
                    int sprite = Integer.parseInt(entityData[5]);
                    loadEntity(identifier, id, x, y, roomId, sprite);
                } else { // SPRITELESS OBJECTS
                    // Don't load if not in the same room as the client.
                    int roomId = Integer.parseInt(entityData[4]);
                    if (!(roomId == clientMaster.getCurrentRoom().getRoomId())) continue;
                    
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
     * After parsing, it sets the currentRoom and allRooms fields of the clientMaster.
     * @param message the substring containing map data
     */
    private void parseMapData(String message){
        // System.out.println("Inside parseMapData: " + message);
        DungeonMapDeserializeResult result = new DungeonMap().deserialize(message);
        clientMaster.setCurrentRoom(result.getStartRoom());
        clientMaster.setAllRooms(result.getAllRooms());
    }

    /**
     * Parses a string in the format BK:roomId, doorId,x,y,direction to
     * create a new door in the end room after defeating a boss
     * @param message the substring containing the end room and new door data
     */
    private void parseBossKilledData(String message) {
        String[] dataParts = message.substring(NetworkProtocol.BOSS_KILLED.length() + NetworkProtocol.DOOR.length()).split(NetworkProtocol.SUB_DELIMITER);
        int doorId = Integer.parseInt(dataParts[0]);
        int x = Integer.parseInt(dataParts[1]);
        int y = Integer.parseInt(dataParts[2]);
        // System.out.println("Door Y: " + doorY);
        String direction = dataParts[3];
        int roomAID = Integer.parseInt(dataParts[4]);
        int roomBID = Integer.parseInt(dataParts[5]);
        Door d = new Door(x, y, direction, clientMaster.getRoomById(roomAID), clientMaster.getRoomById(roomBID));
        d.setId(doorId);
        clientMaster.getRoomById(roomAID).addDoorToArrayList(d);
    }

    public Player getPlayer(char identifier, int id, int x, int y){
        String name = idToName.get(identifier);
        switch (name){
            case "HeavyCat":
                return new HeavyCat(id, x, y);
            case "FastCat":
                return new FastCat(id, x, y);
            case "GunCat":
                return new GunCat(id, x, y);
            default:
                return null;
        }
    }

    public void loadEntity(char identifier, int id, int x, int y, int roomId, int sprite){
        String name = idToName.get(identifier);
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        // if (name == null) System.out.println("Warning: unknown identity identifier " + identifier);
        Entity e = null;
        switch (name) {
            case "Rat":
                e = new Rat(x, y);
                break;
            case "PlayerSlash":
                e =  new PlayerSlash(clientId, null, x, y, 40, 40, 0, false, roomId);
                break;
            case "RatKing":
                e = new RatKing(x, y);
                break;
            case "Snakelet":
                e = new Snakelet(x, y);
                break;
            case "Snake":
                e = new Snake(x, y);
                break;
            default:
                break;
        }
        if (e == null) return;

        e.setId(id);
        e.setCurrentRoom(clientMaster.getRoomById(roomId));
        e.setCurrSprite(sprite);
        e.matchHitBoxBounds();
        clientMaster.addEntity(e);
    }

    public void startInputsThread(){
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