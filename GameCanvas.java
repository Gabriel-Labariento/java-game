import java.awt.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private int width, height;
    private Map gameMap;


    public GameCanvas(int w, int h){
        this.width = w;
        this.height = h;
        gameMap = new Map();
        setPreferredSize(new Dimension(w, h));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        gameMap.generateRooms(7);
        gameMap.draw(g2d);
    }

}
