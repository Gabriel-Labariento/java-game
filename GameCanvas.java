import java.awt.*;
import java.util.concurrent.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    public static final int TILESIZE = 16;
    private static final int REFRESHINTERVAL = 16;
    private final int width, height;
    private final GameClient dataHandler;
    private ClientMaster clientState;
    private ScheduledExecutorService renderLoopScheduler;
    private ScheduledExecutorService sendInputsScheduler;
    private final TileManager tileManager;

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        renderLoopScheduler = Executors.newSingleThreadScheduledExecutor();
        sendInputsScheduler = Executors.newSingleThreadScheduledExecutor();
        clientState = new ClientMaster();
        dataHandler = new GameClient(clientState);
        setPreferredSize(new Dimension(width, height));
        tileManager = new TileManager();
    }

    @Override
    protected void paintComponent(Graphics g){

        //Dont draw canvas if userPlayer is null
        if ( (clientState.getUserPlayer() == null) || (clientState.getCurrentRoom() == null) )return; 

        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        // Set the background/outside of the room
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        double scaleFactor = 1.1;
        g2d.scale(scaleFactor, scaleFactor );
        Player userPlayer = clientState.getUserPlayer();
        
        int screenX = (int) (720/ (2 * scaleFactor) - userPlayer.getWidth() / (2 * scaleFactor));
        int screenY = (int) (540/ (2 * scaleFactor) - userPlayer.getHeight() / (2 * scaleFactor));

        int cameraX = userPlayer.getWorldX() - screenX;
        int cameraY = userPlayer.getWorldY() - screenY;

        Room currentRoom = clientState.getCurrentRoom();
        tileManager.drawTiledObject(g2d, currentRoom, cameraX, cameraY);
        currentRoom.draw(g2d, cameraX, cameraY);
        
        // Draw room doors
        for (Door door : currentRoom.getDoorsArrayList()) {
            door.draw(g2d, cameraX, cameraY);
        }

        // Draw enemies, projectiles, other players
        synchronized (clientState.getEntities()) {
            for (Entity entity : clientState.getEntities())
            entity.draw(g2d, entity.getWorldX() - (cameraX), entity.getWorldY() - (cameraY));    
        }
        
        //Draw current user's player
        userPlayer.draw(g2d, screenX, screenY); //CHANGE 50 BY ACTUAL ASSET SIZE        
    }

    public GameClient getDataHandler(){
        return dataHandler;
    }

    public void startRenderLoop(){
        //Since putting Thread.sleep in a loop as necessary for this Loop is bad, use ScheduledExecutorService instead
        final Runnable renderLoop = this::repaint;
        renderLoopScheduler.scheduleAtFixedRate(renderLoop, 0, REFRESHINTERVAL, TimeUnit.MILLISECONDS);
    }

    public ClientMaster getClientState() {
        return clientState;
    }

    public void setClientState(ClientMaster clientState) {
        this.clientState = clientState;
    }

    public ScheduledExecutorService getRenderLoopScheduler() {
        return renderLoopScheduler;
    }

    public void setRenderLoopScheduler(ScheduledExecutorService renderLoopScheduler) {
        this.renderLoopScheduler = renderLoopScheduler;
    }

    public ScheduledExecutorService getSendInputsScheduler() {
        return sendInputsScheduler;
    }

    public void setSendInputsScheduler(ScheduledExecutorService sendInputsScheduler) {
        this.sendInputsScheduler = sendInputsScheduler;
    }

}
