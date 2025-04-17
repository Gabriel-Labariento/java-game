import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private static final int GAMELOOPINTERVAL = 16;
    private ServerSocket ss;
    private ArrayList<Socket> sockets;
    private GameStateManager gameStateManager;
    private ScheduledExecutorService sendAssetsScheduler;
    private int clientNum = 1;

    public GameServer() {
        gameStateManager = new GameStateManager();
        sockets = new ArrayList<>();
        sendAssetsScheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            ss = new ServerSocket(7000);
            System.out.println("Server started on port 7000");
        } catch (IOException ex) 
        {
            System.out.println("IOException from GameServer constructor");
        }
        System.out.println("GAMESERVER HAS BEEN CREATED.");
    }

    public void closeSocketsOnShutdown(){
        Runtime.getRuntime().addShutdownHook( new Thread(() -> {
            try { 
                for (Socket skt:sockets) {
                    skt.close();
                }
            } catch(IOException e) {
                System.out.println("IOException fromcloseSocketsOnShutdown() method.");
            }
        }));  
    }

    public void waitForConnections() {
        try {
            System.out.println("NOW ACCEPTING CONNECTIONS...");
            while (true){
                // Create a socket for the client to use
                Socket sock = ss.accept();
                // Disable Nagle's buffering algorithm: basically reduces latency
                sock.setTcpNoDelay(true);
                sockets.add(sock);
                
                ConnectedPlayer cp = new ConnectedPlayer(sock, clientNum);
                clientNum++;
                cp.startThreads();
            }        
            } catch (IOException ex) {
                System.out.println("IOException from waitForConnection() method.");
        }
    }

    private class ConnectedPlayer {
        private Socket clientSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int cid;
    
        public ConnectedPlayer(Socket sck, int n){
            clientSocket = sck;
            cid = n;
            gameStateManager.getEntities().add(new Player(cid, 300, 300));
            try {
                dataIn = new DataInputStream(clientSocket.getInputStream());
                dataOut = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("IOException from ConnectedPlayer constructor");
            }
        }

        public void startThreads(){
            startAssetsThread();
            startInputsThread();
        }

        /**
         * Calls the sendMapData() once and sendEntitiesData() continuously every 16 miliseconds. 
         */
        private void startAssetsThread(){
            System.out.println("NEW PLAYER HAS ENTERED");

            final Runnable sendAssetsData = new Runnable(){
                boolean mapDataSent = false;
                @Override
                public void run() {
                    if (!mapDataSent){
                        sendMapData();
                        mapDataSent = true;
                        System.out.println("Map Data Sent");
                    }
                    sendEntitiesData();   
                }
            };
            sendAssetsScheduler.scheduleAtFixedRate(sendAssetsData, 0, GAMELOOPINTERVAL, TimeUnit.MILLISECONDS);
        }

        /**
         * Sends the serialized map data by converting it to a byte array
         */
        private void sendMapData(){
            try {
                String mapDataString = gameStateManager.getMapData();
                byte[] mapDataBytes = mapDataString.getBytes("UTF-8");
                System.out.println("Sending Map Data...");
                dataOut.writeInt(mapDataString.length());
                dataOut.write(mapDataBytes);
            } catch (IOException ex) {
                System.out.println("IOException from sendMapData() method");
            }
        }

        /**
         * Sends the serialized entities data by converting it to a byte array
         */
        private void sendEntitiesData(){
            try {
                String assetsDataString = gameStateManager.getAssetsData(cid);
                // System.out.println(assetsDataString);
                gameStateManager.updateUserPlayerIndex(cid);
                byte[] assetsDataBytes = assetsDataString.getBytes("UTF-8");
                dataOut.writeInt(assetsDataBytes.length);
                dataOut.write(assetsDataBytes);    
            } catch (IOException e) {
                System.out.println("IOException from sendEntitiesData() method");
            }
            
        }

        private void startInputsThread(){
            Thread getInputsThread = new Thread(){
                
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
                            System.out.println("IOEception from getInputsData()");
                        }

                        int length = str.length();
                        boolean isLoadingY = false;
                        String x = "";
                        String y = "";

                        for(int i = 0; i < length; i++){
                            char parsedChar = str.charAt(i);
                            
                            if(!Character.isLetter(parsedChar)){
                                //Delimiter for x and y
                                if(parsedChar == ','){
                                    isLoadingY = true;
                                    continue;
                                }
                                    
                                //Check if loading char to either x or y strings
                                if (isLoadingY){
                                    y += parsedChar;
                                    //Check if last char in the parseable string
                                    if(i == length - 1){
                                        isLoadingY = false;
                                        System.out.println("Mouse click at " + x + "," + y); //Replace with action handler
                                    }
                                }
                                else
                                    x += parsedChar;
                            
                            }
                            else {
                                ((Player) gameStateManager.getPlayerFromClientId(cid)).update(parsedChar);
                            }
                        }
                    }
                }
            };
            getInputsThread.start();
        } 
    }
  
    // When GameServer is run, the main method instantiates a new 
    public static void main(String[] args) {
        GameServer cs = new GameServer();
        cs.closeSocketsOnShutdown();
        cs.waitForConnections();
    }
}
