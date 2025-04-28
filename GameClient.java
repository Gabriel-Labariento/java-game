
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
    

    public GameClient(ClientMaster clientMaster){
        this.clientMaster = clientMaster;
        sendInputsScheduler = Executors.newSingleThreadScheduledExecutor();

        idToName = new HashMap<>();
        idToName.put('A', "Player");
        idToName.put('B', "PlayerSlash");

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

    public void connectToServer(){
        try {
            System.out.println("Please input the server's IP address: ");    
            String ipAddress = console.nextLine();

            System.out.println("Please input the port number: ");
            int portNum = Integer.parseInt(console.nextLine());

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


    private void startAssetsThread(){
        Thread receiveAssetsThread = new Thread(){
            @Override
            public void run(){
                while (true){
                    String str = "";
                    try {
                        int byteLength = dataIn.readInt();
                        byte[] buffer = new byte[byteLength];
                        dataIn.readFully(buffer);
                        str = new String(buffer, "UTF-8");
                    } catch (IOException ex){
                        System.out.println("IOEception from receiveAssetsThread");
                    }
                
                    // Parse entity data
                    System.out.println(str);
                    parseEntityData(str);
                }}};
        receiveAssetsThread.start();
    }
    
    private void parseEntityData(String str){
        // Dispose loaded assets
        clientMaster.getEntities().clear();

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

            //str is always in the form of "{clientiD}{identifier}{x},{y}{userPlayer indicator}{roomAsset indicator}"
            if (Character.isLetter(parsedChar)){
                if (isLoadingClientId){
                    clientId = Integer.parseInt( (String) cid);
                    isLoadingClientId = false;
                }    

                //Create new entity from previous data as new identifier is reached
                if (isLoadingEntity && identifier != null){					
                    loadAsset(identifier, Integer.parseInt(strX), Integer.parseInt(strY));
                    isLoadingY = false;
                }
                
                identifier = parsedChar;
                strX = "";
                strY = "";
                isLoadingEntity = true;
                isLoadingY = false;
            }
            else if (isLoadingClientId)
                //Parse starting values as clientId
                cid += parsedChar;
            else {
                switch (parsedChar) {
                    //Check if userplayer identifier
                    case '$':
                        try {
                            clientMaster.setUserPlayer(new Player(clientId, Integer.parseInt(strX), Integer.parseInt(strY)));
                        } catch (Exception e) {
                            System.out.println("Exception at parseEntityData() of GameClient");
                        }
                        isLoadingEntity = false;
                        identifier = null;
                        strX = "";
                        strY = "";
                        break;

                    //Check if currentRoom identifier
                    case '%':
                        clientMaster.setCurrentRoom(new Room(identifier));
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

    public void loadAsset(char identifier, int x, int y){
        String name = idToName.get(identifier);
        if(name.equals("Player"))
            clientMaster.addEntity(new Player(0, x, y));
        else if(name.equals("PlayerSlash"))
            clientMaster.addEntity(new PlayerSlash(0, x, y, 80, 80, 0, false, 0));
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

        if(keyMap.get("W"))
            str.append("W");
        if(keyMap.get("A"))
            str.append("A");
        if(keyMap.get("S"))
            str.append("S");
        if(keyMap.get("D"))
            str.append("D");
        if(clickedX != 0 && clickedY != 0){
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