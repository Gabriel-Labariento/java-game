import java.awt.*;
import java.awt.geom.*;
import java.util.concurrent.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private static final long REFRESHINTERVAL = 16;
    private int width, height;
    private GameClient gameClient;
    private ClientMaster clientMaster;
    private ScheduledExecutorService renderLoopScheduler;

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        renderLoopScheduler = Executors.newSingleThreadScheduledExecutor();

        clientMaster = new ClientMaster();
        gameClient = new GameClient(clientMaster);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g){

        //Dont draw canvas if userPlayer is null
        Player userPlayer = clientMaster.getUserPlayer();
        if (userPlayer == null) return; 

        Graphics2D g2d = (Graphics2D) g;

        //TEMPORARY OFFSET CONFIGURATIONS
        int userPlayerScreenX = (width/2 - userPlayer.getWidth()/2);
        int userPlayerScreenY = (height/2 - userPlayer.getHeight()/2);
        int xOffset = userPlayerScreenX - userPlayer.getWorldX();
        int yOffset = userPlayerScreenY - userPlayer.getWorldY();

        Rectangle2D.Double background = new Rectangle2D.Double(xOffset, yOffset, 1000, 1000);
        g2d.setColor(Color.GRAY);
        g2d.fill(background);

        // Draw enemies, projectiles, other players
        for (Entity entity : clientMaster.getEntities()){
            entity.draw(g2d, entity.getWorldX()+ xOffset, entity.getWorldY()+yOffset);
        }
            
        //Draw current user's player
        userPlayer.draw(g2d, userPlayerScreenX, userPlayerScreenY); //CHANGE 50 BY ACTUAL ASSET SIZE

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
    }

    public GameClient getGameClient(){
        return gameClient;
    }

    public void startRenderLoop(){
        //Since putting Thread.sleep in a loop as necessary for this Loop is bad, use ScheduledExecutorService instead
        final Runnable renderLoop = this::repaint;
        renderLoopScheduler.scheduleAtFixedRate(renderLoop, 0, REFRESHINTERVAL, TimeUnit.MILLISECONDS);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public ClientMaster getClientState() {
        return clientMaster;
    }

    public void setClientState(ClientMaster clientMaster) {
        this.clientMaster = clientMaster;
    }

    public ScheduledExecutorService getRenderLoopScheduler() {
        return renderLoopScheduler;
    }

    public void setRenderLoopScheduler(ScheduledExecutorService renderLoopScheduler) {
        this.renderLoopScheduler = renderLoopScheduler;
    }
}
