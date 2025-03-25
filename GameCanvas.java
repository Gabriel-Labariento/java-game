import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private int width, height;
    private ArrayList<MovableCharacter> players; 

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        players = new ArrayList<>();
        players.add(new Player(0, 0, 5));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        for (MovableCharacter player : players) {
            player.draw(g2d);
        }
    }

    public ArrayList<MovableCharacter> getPlayers() {
        return players;
    }

}
