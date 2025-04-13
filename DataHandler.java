
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
        idToName.put('A', "Player");
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
                    String str = "";
                    try {
                        int byteLength = dataIn.readInt();
                        byte[] buffer = new byte[byteLength];
                        dataIn.readFully(buffer);
                        str = new String(buffer, "UTF-8");
                    } catch (IOException ex){
                        System.out.println("IOEception from receiveAssetsThread");
                    }
                    
                    // Dispose loaded assets
                    clientState.getEntities().clear();

                    // Parse entity data
                    parseEntityData(str);
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
                        clientState.setCurrentRoom(new Room(identifier));
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