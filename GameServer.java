import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int TICKSPERSECOND = 60;
    private ServerSocket ss;
    private ArrayList<Socket> sockets;
    private int clientNum;
    private ServerMaster serverMaster;
    private ArrayList<ConnectedPlayer> connectedPlayers;
    private ScheduledExecutorService gameLoopScheduler;
    private int port;

    public GameServer() {
        clientNum = 1;
        serverMaster = new ServerMaster();
        sockets = new ArrayList<>();
        connectedPlayers = new ArrayList<>();
        gameLoopScheduler = Executors.newSingleThreadScheduledExecutor();

        
        port = 5000;
        while(true) {
            try {
                ss = new ServerSocket(port);
            } catch (IOException ex) 
            {
                System.out.println("IOException from GameServer constructor ");
            }

            if (ss != null){
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
                serverMaster.update();
                
                //Send assets data to individual players
                if(!connectedPlayers.isEmpty()){
                    for (ConnectedPlayer cp : connectedPlayers){
                        String data = serverMaster.getAssetsData(cp.cid);
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
                connectedPlayers.add(cp);
            }        
            } catch (IOException ex) {
                System.out.println("IOException from waitForConnection() method.");
        }
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
            sendQueue = new LinkedBlockingQueue<>();

            serverMaster.getEntities().add(new Player(cid, 0, 0));
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
        private void startAssetsThread(){
            System.out.println("NEW PLAYER HAS ENTERED");
            Thread sendAssetsThread = new Thread(){
                @Override
                public void run(){
                    while (true) { 
                        try {
                        //Only start the rest of the thread if data is sent\
                        String assetsDataString = sendQueue.take();
                        byte[] assetsDataBytes = assetsDataString.getBytes("UTF-8");
                        dataOut.writeInt(assetsDataBytes.length);
                        dataOut.write(assetsDataBytes);
                        } catch (IOException ex) {
                            System.out.println("IOException from ConnectedPlayer's startAssetsThread method");
                            break;
                        } catch (InterruptedException ex) {
                            System.out.println("InterrupedException from ConnectedPlayer's startAssetsThread method");
                        }   
                    }
                }    
            };
            sendAssetsThread.start();
        }

        public void promptAssetsThread(String data){
            sendQueue.offer(data);
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
                            break;
                        }

                        int length = str.length();
                        boolean isLoadingY = false;
                        String x = "";
                        String y = "";
                        System.out.println(str);

                        for(int i = 0; i < length; i++){
                            char parsedChar = str.charAt(i);
                            
                            if(!Character.isLetter(parsedChar)){
                                //Delimiter for x and y
                                if(parsedChar == ','){
                                    isLoadingY = true;
                                    continue;   
                                }
                                    
                                //Check if loading char to either x or y strings
                                if (isLoadingY)
                                    y += parsedChar;                        
                                else
                                    x += parsedChar;
                            }
                            else{
                                serverMaster.loadKeyInput(parsedChar, cid);
                            }
                                
                        }

                        if(!x.isEmpty() && !y.isEmpty()){
                            serverMaster.loadClickInput(Integer.parseInt(x), Integer.parseInt(y), cid); 
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
