import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameServer {
    private static final int GAMELOOPINTERVAL = 16;
    private static final int TICKSPERSECOND = 60;
    private ServerSocket ss;
    private ArrayList<Socket> sockets;
    private ServerMaster gameStateManager;
    private ArrayList<ConnectedPlayer> connectedPlayers;
    private ScheduledExecutorService gameLoopScheduler;
    private ScheduledExecutorService sendAssetsScheduler;
    private int clientNum;
    private int port;

    public GameServer() {
        clientNum = 1;
        gameStateManager = ServerMaster.getInstance();
        sockets = new ArrayList<>();
        connectedPlayers = new ArrayList<>();
        gameLoopScheduler = Executors.newSingleThreadScheduledExecutor();
        sendAssetsScheduler = Executors.newSingleThreadScheduledExecutor();
        port = 5000;
        while (true) { 
            try { ss = new ServerSocket(port); } 
            catch (IOException ex) { System.out.println("IOException from GameServer constructor"); }
    
            if (ss != null) {
                System.out.println("Routed to port " + port);
                break;
            } 

            port++;
        }
        
        System.out.println("GAMESERVER HAS BEEN CREATED.");
    }

    public void startGameLoop(){
        final Runnable gameLoop = new Runnable(){
            @Override
            public void run(){
                try {
                    gameStateManager.update();    
                    // System.out.println("called update on gsm");
                } catch (Exception e) {
                    System.err.println("Exception in game loop update():" + e);
                }
                

                if (!connectedPlayers.isEmpty()){
                    for (ConnectedPlayer cp : connectedPlayers) {
                        String data = gameStateManager.getAssetsData(cp.cid);
                        cp.promptAssetsThread(data);
                    }
                }
            }
        };
        gameLoopScheduler.scheduleAtFixedRate(gameLoop, 0, Math.round(1000/TICKSPERSECOND), TimeUnit.MILLISECONDS);
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
    
    public int getPort() {
        return port;
    }

    private class ConnectedPlayer {
        private Socket clientSocket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        //For halting operations within a thread
        private BlockingQueue<String> sendQueue;
        private int cid;
    
        public ConnectedPlayer(Socket sck, int n){
            clientSocket = sck;
            cid = n;
            sendQueue = new LinkedBlockingDeque<>();
            gameStateManager.addEntity((new Player(cid, gameStateManager.getCurrentRoom().getCenterX(), gameStateManager.getCurrentRoom().getCenterY())));
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
                    gameStateManager.updateEntities();
                    sendEntitiesData();   
                }
            };
            sendAssetsScheduler.scheduleAtFixedRate(sendAssetsData, 0, GAMELOOPINTERVAL, TimeUnit.MILLISECONDS);
        }

        public void promptAssetsThread(String data){
            sendQueue.offer(data);
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
                        String str;
                        try {
                            int byteLength = dataIn.readInt();
                            byte[] buffer = new byte[byteLength];
                            dataIn.readFully(buffer);
                            str = new String(buffer, "UTF-8");
                        } catch (IOException ex){
                            System.out.println("IOEception from getInputsData()");
                            break;
                        }

                        String[] inputStrParts = str.split("\\|");
                        // System.out.println(str);
                        // System.out.println("Input string: " + str);

                        for (String part : inputStrParts) {
                            System.out.println(part);    
                            if (part.isEmpty()) {}
                            else if (part.startsWith(NetworkProtocol.CLICK)){
                                System.out.println(part);
                                String[] coors = part.split(NetworkProtocol.SUB_DELIMITER);
                                int x = Integer.parseInt(coors[0].substring(NetworkProtocol.CLICK.length()));
                                int y = Integer.parseInt(coors[1]);
                                System.out.println("CLick at " + x + "," + y);
                                gameStateManager.loadClickInput(x, y, cid); 
                            } else {
                                // System.out.println(part);
                                char parsedChar = part.toCharArray()[0];
                                // System.out.println("Parsed char: " + parsedChar); 
                                gameStateManager.loadKeyInput(parsedChar, cid);
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
        cs.startGameLoop();
        cs.closeSocketsOnShutdown();
        cs.waitForConnections();
    }
}
