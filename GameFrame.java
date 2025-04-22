    import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameFrame extends JFrame{
    private int width, height;
    private String title;
    private JFrame f;
    private JPanel cp;
    private GameCanvas gameCanvas;
    private Entity[] entities;
    private GameClient dataHandler;

    public GameFrame(int width, int height, String title){
        this.width = width;
        this.height = height;
        this.title = title;
        cp = (JPanel) this.getContentPane();
        gameCanvas = new GameCanvas(width, height);
        dataHandler = gameCanvas.getDataHandler();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                dataHandler.clickInput(e.getX(), e.getY());
            }
        });
    }

    public void setUpGUI() {        
        setTitle(title);
        cp.add(gameCanvas, BorderLayout.CENTER);    
        cp.setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void addKeyBindings(){
        ActionMap am = cp.getActionMap(); 
        InputMap im = cp.getInputMap();

        AbstractAction keyInputW = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("W", true);
            }
        };

        AbstractAction keyInputS = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("S", true);
            }
        };

        AbstractAction keyInputA = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("A", true);
            }
        };

        AbstractAction keyInputD = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("D", true);
            }
        };

        AbstractAction stopInputW = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("W", false);
            }
        };

        AbstractAction stopInputS = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("S", false);
            }
        };

        AbstractAction stopInputA = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("A", false);
            }
        };

        AbstractAction stopInputD = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent ae){
                dataHandler.keyInput("D", false);
            }
        };

        am.put("keyInputW", keyInputW);
        am.put("keyInputS", keyInputS);
        am.put("keyInputA", keyInputA);
        am.put("keyInputD", keyInputD);
        am.put("stopInputW", stopInputW);
        am.put("stopInputS", stopInputS);
        am.put("stopInputA", stopInputA);
        am.put("stopInputD", stopInputD);

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "keyInputW");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "keyInputA");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "keyInputS");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "keyInputD");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "stopInputW");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "stopInputA");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "stopInputS");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "stopInputD");
    }

    public GameCanvas getCanvas(){
        return gameCanvas;
    }
}
