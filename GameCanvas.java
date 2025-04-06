import java.awt.*;
import java.util.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private int width, height;
    private Map gameMap;
    private ArrayList<MovableCharacter> players;


    public GameCanvas(int w, int h){
        this.width = w;
        this.height = h;
        gameMap = new Map();
        setPreferredSize(new Dimension(w, h));
        gameMap.generateRooms(7);
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

        gameMap.draw(g2d);

        for (MovableCharacter movableCharacter : players) {
            movableCharacter.draw(g2d);
        }
    }

    public ArrayList<MovableCharacter> getPlayers(){
        return players;
    }

}
