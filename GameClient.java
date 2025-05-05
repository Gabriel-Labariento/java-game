
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameClient {
    public static final int TRANSFERINTERVAL = 16;
    private ClientMaster clientMaster;
    private Socket theSocket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;
    private Scanner console;
    private int clientId;
    private HashMap<Character, String> idToName;
    private HashMap<String, Boolean> keyMap;
    private int clickedX;
    private int clickedY;
    private ScheduledExecutorService sendInputsScheduler;
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

                        // If the received message starts with the protocol identifier for map data, parse the map data
                        if (receivedMessage.startsWith(NetworkProtocol.MAP_DATA)) {
                            parseMapData(receivedMessage);
                        } else {
                            synchronized (clientMaster.getEntities()) { // Synchronize entities arraylist to remove flickering
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
    
    /**
     * Parses a serialized string expected to be in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     * @param message a serialized string in the form ClientId|P:playerX,playerY|E:entity1X,entity1Y,entity2x,entity2Y...|
     */
    private void parseEntitiesData(String message){

        System.out.println(message);
        String[] messageParts = message.split("\\" + NetworkProtocol.DELIMITER); // Have to use \\ to escape. Turns out "|" is special for java
        this.clientId = Integer.parseInt(messageParts[0]);

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
                System.out.println("Whole entity string: " + part);
                String[] entityData = part.substring(NetworkProtocol.ENTITY.length()).split(NetworkProtocol.SUB_DELIMITER);
                
                // for (String string : entityData) {
                //     // System.out.println("Entity string: " + string);
                // }
                // Don't load if not in the same room as the client.
                int roomId = Integer.parseInt(entityData[4]);
                if (!(roomId == clientMaster.getCurrentRoom().getRoomId())) continue;
                
                char identifier = entityData[0].toCharArray()[0];
                int id = Integer.parseInt(entityData[1]);
                int x = Integer.parseInt(entityData[2]);
                int y = Integer.parseInt(entityData[3]);
                loadEntity(identifier, id, x, y, roomId);    
            }
        
        }
        
    }

    /**
     * Parses a part of the String received by the receiveAssetsThread responsible for the map data.
     * After parsing, it sets the currentRoom and allRooms fields of the clientMaster.
     * @param message the substring containing map data
     */
    private void parseMapData(String message){
        DungeonMapDeserializeResult result = new DungeonMap().deserialize(message);
        clientMaster.setCurrentRoom(result.getStartRoom());
        clientMaster.setAllRooms(result.getAllRooms());
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

    public void loadEntity(char identifier, int id, int x, int y, int roomId){
        String name = idToName.get(identifier);
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        // if (name == null) System.out.println("Warning: unknown identity identifier " + identifier);
        switch (name) {
            case "Rat":
                Rat r = new Rat(x, y);
                r.setId(id);
                r.setCurrentRoom(clientMaster.getRoomById(roomId));
                clientMaster.addEntity(r);
                break;
            case "PlayerSlash":
                PlayerSlash ps = new PlayerSlash(clientId, null, x, y, 40, 40, 0, false, roomId);
                ps.setId(id);
                ps.matchHitBoxBounds();
                clientMaster.addEntity(ps);
                // System.out.println("Added playerSlash to client entities");
                break;
            default:
                break;
        }
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