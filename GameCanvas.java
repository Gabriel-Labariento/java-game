import java.awt.*;
import java.awt.geom.*;
import java.util.concurrent.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private static final int GAMELOOPINTERVAL = 16;
    private static final int FPS = 60;
    private int width, height;
    private DataHandler dataHandler;
    private ClientGameState clientState;
    private ScheduledExecutorService renderLoopScheduler;
    private ScheduledExecutorService sendInputsScheduler;

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        renderLoopScheduler = Executors.newSingleThreadScheduledExecutor();
        sendInputsScheduler = Executors.newSingleThreadScheduledExecutor();
        clientState = new ClientGameState();
        dataHandler = new DataHandler(clientState);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g){

        //Dont draw canvas if userPlayer is null
        if (clientState.getUserPlayer() == null) return; 

        Graphics2D g2d = (Graphics2D) g;

        //TEMPORARY OFFSET CONFIGURATIONS
        int screenX = (720/2 - 50/2);
        int screenY = (540/2 - 50/2);

        Rectangle2D.Double background = new Rectangle2D.Double(-clientState.getUserPlayer().getWorldX()+screenX, -clientState.getUserPlayer().getWorldY()+screenY, 1000, 1000);
        g2d.setColor(Color.GRAY);
        g2d.fill(background);

        // Draw enemies, projectiles, other players
        for (Entity entity : clientState.getEntities())
            entity.draw(g2d, entity.getWorldX()-clientState.getUserPlayer().getWorldX() + screenX, entity.getWorldY()-clientState.getUserPlayer().getWorldY() + screenY);

        //Draw current user's player
        clientState.getUserPlayer().draw(g2d, screenX, screenY); //CHANGE 50 BY ACTUAL ASSET SIZE

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

    public ClientGameState getClientState() {
        return clientState;
    }

    public void setClientState(ClientGameState clientState) {
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
