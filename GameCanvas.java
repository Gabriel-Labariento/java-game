import java.awt.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private int width, height;
    private Game game;


    public GameCanvas(int w, int h){
        this.width = w;
        this.height = h;
        setPreferredSize(new Dimension(w, h));
        game = new Game();
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        game.getGameMap().draw(g2d);

        for (Entity player : game.getPlayers()) {
            player.draw(g2d);
        }
    }

    public Game getGame(){
        return game;
    }
}
