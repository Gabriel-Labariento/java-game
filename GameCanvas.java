import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private static final int GAMELOOPINTERVAL = 16;
    private static final int FPS = 60;
    private int width, height;
    private CopyOnWriteArrayList<Entity> entities; 
    private DataHandler dataHandler;
    private Player userPlayer;
    private Room currentRoom;
    private ScheduledExecutorService renderLoopScheduler;
    private ScheduledExecutorService sendInputsScheduler;

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        renderLoopScheduler =  Executors.newSingleThreadScheduledExecutor();
        sendInputsScheduler =  Executors.newSingleThreadScheduledExecutor();
        dataHandler = new DataHandler();
        entities = new CopyOnWriteArrayList<>();
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g){

        //Dont draw canvas if userPlayer is null
        if (userPlayer == null) return; 

        Graphics2D g2d = (Graphics2D) g;

        //TEMPORARY OFFSET CONFIGURATIONS
        int screenX = (720/2 - 50/2);
        int screenY = (540/2 - 50/2);

        Rectangle2D.Double background = new Rectangle2D.Double(-userPlayer.getWorldX()+screenX, -userPlayer.getWorldY()+screenY, 1000, 1000);
        g2d.setColor(Color.GRAY);
        g2d.fill(background);

        // Draw enemies, projectiles, other players
        for (Entity entity : entities)
            entity.draw(g2d, entity.getWorldX()-userPlayer.getWorldX() + screenX, entity.getWorldY()-userPlayer.getWorldY() + screenY);

        //Draw current user's player
        userPlayer.draw(g2d, screenX, screenY); //CHANGE 50 BY ACTUAL ASSET SIZE

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
    }

    public DataHandler getDataHandler(){
        return dataHandler;
    }

    public void startRenderLoop(){
        //Since putting Thread.sleep in a loop as necessary for this Loop is bad, use ScheduledExecutorService instead
        final Runnable renderLoop = this::repaint;
        renderLoopScheduler.scheduleAtFixedRate(renderLoop, 0, ((long)1000/FPS), TimeUnit.MILLISECONDS);
    }

    public class DataHandler {
        private Socket theSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private Scanner console;
        private int clientId;
        private HashMap<Character, String> idToName;
        private HashMap<String, Boolean> keyMap ;
        private int clickedX;
        private int clickedY;

        public DataHandler(){

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
                startRenderLoop();
            } catch (IOException ex) {
                System.out.println("IOException from connectToServer() method");
            }
        }

        public void startAssetsThread(){
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
                        
                        //Dispose loaded assets
                        entities.clear();

                        //Parse entity data
                        Boolean isLoadingClientId = true;
                        Boolean isLoadingY = false;
                        Boolean isLoadingEntity = false;

                        Character identifier = null;
                        String strX = "";
                        String strY = "";
                        String cid = "";
                        int length = str.length();
                        for(int i = 0; i < length; i++){
                            char parsedChar = str.charAt(i);

                            //str is always in the form of "{clientiD}{identifier}{userPlayer indicator}{roomAsset indicator}{x},{y}"
                            if (Character.isLetter(parsedChar)){
                                //Create new entity from previous data as new identifier is reached
                                if (isLoadingEntity){					
                                    loadAsset(identifier, Integer.parseInt(strX), Integer.parseInt(strY));
                                    isLoadingY = false;
                                    strX = "";
                                    strY = "";
                                }

                                if (isLoadingClientId){
                                    clientId = Integer.parseInt(cid);
                                    isLoadingClientId = false;

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
                                        userPlayer = new Player(clientId, Integer.parseInt(strX), Integer.parseInt(strY));
                                        break;
                                    //Check if currentRoom identifier
                                    case '%':
                                        currentRoom = new Room(identifier);
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
            }}}}};
            receiveAssetsThread.start();
        }
        
        public void loadAsset(char identifier, int x, int y){
            String name = idToName.get(identifier);
            if(name.equals("Player"))
                entities.add(new Player(0, x, y));
            else if(name.equals("Rat"))
                System.out.println("different name accessed");
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
}
