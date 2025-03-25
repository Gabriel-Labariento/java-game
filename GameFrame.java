import java.awt.*;
import javax.swing.*;

public class GameFrame extends JFrame{
    private int width, height;
    private String title;

    public GameFrame(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void setUpGUI() {
        Container contentPane = getContentPane();
        GameCanvas gameCanvas = new GameCanvas(width, height);
        setTitle(title);
        contentPane.add(gameCanvas, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }


}
