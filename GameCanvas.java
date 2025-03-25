import java.awt.*;
import javax.swing.*;

public class GameCanvas extends JComponent {
    private int width, height;

    public GameCanvas(int width, int height){
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);
    }

}
